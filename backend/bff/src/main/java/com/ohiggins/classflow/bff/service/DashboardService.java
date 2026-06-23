package com.ohiggins.classflow.bff.service;

import com.ohiggins.classflow.bff.dto.DashboardResponse;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    private static final Logger log = LoggerFactory.getLogger(DashboardService.class);

    private final WebClient authWebClient;
    private final WebClient academicWebClient;
    private final WebClient assistanceWebClient;
    private final WebClient messageWebClient;
    private final WebClient notificationWebClient;

    public DashboardService(
            @Qualifier("authWebClient") WebClient authWebClient,
            @Qualifier("academicWebClient") WebClient academicWebClient,
            @Qualifier("assistanceWebClient") WebClient assistanceWebClient,
            @Qualifier("messageWebClient") WebClient messageWebClient,
            @Qualifier("notificationWebClient") WebClient notificationWebClient) {
        this.authWebClient = authWebClient;
        this.academicWebClient = academicWebClient;
        this.assistanceWebClient = assistanceWebClient;
        this.messageWebClient = messageWebClient;
        this.notificationWebClient = notificationWebClient;
    }

    @SuppressWarnings("unchecked")
    public Mono<DashboardResponse> getDashboard(Long userId) {
        Mono<JsonNode> userMono = fetchObject(authWebClient, "/api/auth/users/{userId}", userId);
        Mono<List<JsonNode>> coursesMono = fetchList(academicWebClient, "/api/courses");
        Mono<List<JsonNode>> subjectsMono = fetchList(academicWebClient, "/api/subjects");
        Mono<List<JsonNode>> evaluationsMono = fetchList(academicWebClient, "/api/evaluations");

        return userMono.switchIfEmpty(Mono.error(new RuntimeException("User not found: " + userId)))
                .flatMap(user -> {
                    String role = user.path("role").asText("UNKNOWN");
                    boolean isStudent = "STUDENT".equals(role);

                    Mono<List<JsonNode>> gradesMono = isStudent
                        ? fetchList(academicWebClient, "/api/grades/student/{userId}", userId)
                        : fetchList(academicWebClient, "/api/grades");

                    Mono<List<JsonNode>> attendancesMono = isStudent
                        ? fetchList(assistanceWebClient, "/api/attendance/student/{userId}", userId)
                        : fetchList(assistanceWebClient, "/api/attendance");

                    Mono<List<JsonNode>> annotationsMono = isStudent
                        ? fetchList(assistanceWebClient, "/api/annotations/student/{userId}", userId)
                        : fetchList(assistanceWebClient, "/api/annotations");

                    Mono<List<JsonNode>> messagesMono = fetchList(messageWebClient, "/api/messages/receiver/{userId}", userId);
                    Mono<List<JsonNode>> unreadMessagesMono = fetchList(messageWebClient, "/api/messages/receiver/{userId}/unread", userId);
                    Mono<List<JsonNode>> announcementsMono = fetchList(messageWebClient, "/api/announcements/active");
                    Mono<List<JsonNode>> notificationsMono = fetchList(notificationWebClient, "/api/notifications/user/{userId}", userId);
                    Mono<List<JsonNode>> pendingNotificationsMono = fetchList(notificationWebClient, "/api/notifications/user/{userId}/pending", userId);

                    return Mono.zip(objects -> new DashboardResponse(
                                    user,
                                    role,
                                    (List<JsonNode>) objects[0],
                                    (List<JsonNode>) objects[1],
                                    (List<JsonNode>) objects[2],
                                    (List<JsonNode>) objects[3],
                                    (List<JsonNode>) objects[4],
                                    (List<JsonNode>) objects[5],
                                    (List<JsonNode>) objects[6],
                                    (List<JsonNode>) objects[7],
                                    (List<JsonNode>) objects[8],
                                    (List<JsonNode>) objects[9],
                                    (List<JsonNode>) objects[10]
                            ),
                            coursesMono,
                            subjectsMono,
                            evaluationsMono,
                            gradesMono,
                            attendancesMono,
                            annotationsMono,
                            messagesMono,
                            unreadMessagesMono,
                            announcementsMono,
                            notificationsMono,
                            pendingNotificationsMono
                    );
                })
                .flatMap(response -> {
                    if ("GUARDIAN".equals(response.role())) {
                        return filterByGuardian(response, userId);
                    }
                    return Mono.just(response);
                })
                .flatMap(this::enrichWithStudentNames);
    }

    /**
     * Filters the dashboard response to only include data belonging to the guardian's students.
     * Fetches the student list from the auth service using the guardian's ID.
     */
    private Mono<DashboardResponse> filterByGuardian(DashboardResponse response, Long guardianId) {
        return fetchGuardianStudentIds(guardianId).map(studentIds -> {
            if (studentIds.isEmpty()) {
                return response;
            }
            Set<Long> validIds = new HashSet<>(studentIds);
            return new DashboardResponse(
                    response.user(),
                    response.role(),
                    response.courses(),
                    response.subjects(),
                    response.evaluations(),
                    filterListByStudentId(response.grades(), validIds),
                    filterListByStudentId(response.attendances(), validIds),
                    filterListByStudentId(response.annotations(), validIds),
                    response.messages(),
                    response.unreadMessages(),
                    response.announcements(),
                    response.notifications(),
                    response.pendingNotifications()
            );
        });
    }

    private Mono<List<Long>> fetchGuardianStudentIds(Long guardianId) {
        return fetchList(authWebClient, "/api/auth/users/guardian/{guardianId}", guardianId)
                .map(students -> students.stream()
                        .map(s -> s.get("id").asLong())
                        .toList())
                .defaultIfEmpty(Collections.emptyList());
    }

    private List<JsonNode> filterListByStudentId(List<JsonNode> items, Set<Long> validIds) {
        return items.stream()
                .filter(item -> item.has("studentId") && validIds.contains(item.get("studentId").asLong()))
                .collect(Collectors.toList());
    }

    /**
     * Enriches grades, attendances, and annotations with student names fetched from the auth service.
     * Falls back gracefully if the auth service is unavailable.
     */
    private Mono<DashboardResponse> enrichWithStudentNames(DashboardResponse response) {
        Set<Long> studentIds = collectStudentIds(response);
        if (studentIds.isEmpty()) {
            return Mono.just(response);
        }

        List<Long> idsList = new ArrayList<>(studentIds);

        List<Mono<JsonNode>> fetches = idsList.stream()
                .map(id -> fetchObject(authWebClient, "/api/auth/users/{id}", id)
                        .defaultIfEmpty(JsonNodeFactory.instance.objectNode()))
                .collect(Collectors.toList());

        return Mono.zip(fetches, results -> {
                    Map<Long, String> nameMap = new HashMap<>();
                    for (int i = 0; i < results.length; i++) {
                        JsonNode node = (JsonNode) results[i];
                        if (node != null && node.has("id")) {
                            Long userId = idsList.get(i);
                            String name = extractUserName(node);
                            if (name != null && !name.isBlank()) {
                                nameMap.put(userId, name);
                            }
                        }
                    }
                    return nameMap;
                })
                .map(nameMap -> buildEnrichedResponse(response, nameMap));
    }

    private Set<Long> collectStudentIds(DashboardResponse response) {
        Set<Long> ids = new HashSet<>();
        for (JsonNode grade : response.grades()) {
            if (grade.has("studentId")) {
                ids.add(grade.get("studentId").asLong());
            }
        }
        for (JsonNode attendance : response.attendances()) {
            if (attendance.has("studentId")) {
                ids.add(attendance.get("studentId").asLong());
            }
        }
        for (JsonNode annotation : response.annotations()) {
            if (annotation.has("studentId")) {
                ids.add(annotation.get("studentId").asLong());
            }
        }
        return ids;
    }

    private String extractUserName(JsonNode userNode) {
        if (userNode.has("nombre") && !userNode.get("nombre").isNull()
                && !userNode.get("nombre").asText().isBlank()) {
            return userNode.get("nombre").asText();
        }
        if (userNode.has("fullName") && !userNode.get("fullName").isNull()
                && !userNode.get("fullName").asText().isBlank()) {
            return userNode.get("fullName").asText();
        }
        if (userNode.has("firstName")) {
            String first = userNode.get("firstName").asText("");
            String last = userNode.has("lastName") ? userNode.get("lastName").asText("") : "";
            String name = (first + " " + last).trim();
            if (!name.isBlank()) {
                return name;
            }
        }
        return null;
    }

    private DashboardResponse buildEnrichedResponse(DashboardResponse response, Map<Long, String> nameMap) {
        List<JsonNode> enrichedGrades = enrichList(response.grades(), "studentId", nameMap);
        List<JsonNode> enrichedAttendances = enrichList(response.attendances(), "studentId", nameMap);
        List<JsonNode> enrichedAnnotations = enrichList(response.annotations(), "studentId", nameMap);

        if (enrichedGrades == response.grades()
                && enrichedAttendances == response.attendances()
                && enrichedAnnotations == response.annotations()) {
            return response;
        }

        return new DashboardResponse(
                response.user(),
                response.role(),
                response.courses(),
                response.subjects(),
                response.evaluations(),
                enrichedGrades,
                enrichedAttendances,
                enrichedAnnotations,
                response.messages(),
                response.unreadMessages(),
                response.announcements(),
                response.notifications(),
                response.pendingNotifications()
        );
    }

    private List<JsonNode> enrichList(List<JsonNode> items, String idField, Map<Long, String> nameMap) {
        boolean changed = false;
        List<JsonNode> result = new ArrayList<>(items.size());
        for (JsonNode item : items) {
            if (item.has(idField) && item instanceof ObjectNode objectNode) {
                Long id = item.get(idField).asLong();
                String name = nameMap.get(id);
                if (name != null && !item.has("studentName")) {
                    ObjectNode enriched = objectNode.put("studentName", name);
                    result.add(enriched);
                    changed = true;
                    continue;
                }
            }
            result.add(item);
        }
        return changed ? result : items;
    }

    private Mono<JsonNode> fetchObject(WebClient client, String uri, Object... uriVariables) {
        return client.get()
                .uri(uri, uriVariables)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .onErrorResume(WebClientResponseException.class, e -> {
                    if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                        return Mono.empty();
                    }
                    log.warn("Error fetching from {}: {} {}", uri, e.getStatusCode(), e.getMessage());
                    return Mono.empty();
                })
                .onErrorResume(e -> {
                    log.warn("Unexpected error fetching from {}: {}", uri, e.getMessage());
                    return Mono.empty();
                });
    }

    private Mono<List<JsonNode>> fetchList(WebClient client, String uri, Object... uriVariables) {
        return client.get()
                .uri(uri, uriVariables)
                .retrieve()
                .bodyToFlux(JsonNode.class)
                .collectList()
                .onErrorResume(WebClientResponseException.class, e -> {
                    if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                        return Mono.just(Collections.emptyList());
                    }
                    log.warn("Error fetching list from {}: {} {}", uri, e.getStatusCode(), e.getMessage());
                    return Mono.just(Collections.emptyList());
                })
                .onErrorResume(e -> {
                    log.warn("Unexpected error fetching list from {}: {}", uri, e.getMessage());
                    return Mono.just(Collections.emptyList());
                });
    }
}
