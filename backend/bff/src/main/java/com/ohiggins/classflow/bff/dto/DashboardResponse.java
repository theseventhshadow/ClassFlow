package com.ohiggins.classflow.bff.dto;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.List;

/**
 * Representa el dashboard consolidado que consume el frontend.
 */
public record DashboardResponse(
        JsonNode user,
        String role,
        List<JsonNode> courses,
        List<JsonNode> subjects,
        List<JsonNode> evaluations,
        List<JsonNode> grades,
        List<JsonNode> attendances,
        List<JsonNode> annotations,
        List<JsonNode> messages,
        List<JsonNode> unreadMessages,
        List<JsonNode> announcements,
        List<JsonNode> notifications,
        List<JsonNode> pendingNotifications
) {
}
