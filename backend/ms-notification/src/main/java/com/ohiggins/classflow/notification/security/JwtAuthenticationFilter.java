package com.ohiggins.classflow.notification.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Ademas del JWT normal de usuario (delegado a ms-auth via /api/auth/validate), acepta el
 * header X-Internal-Api-Key para la unica llamada de servicio a servicio que existe hoy
 * (ms-auth enviando el correo de recuperacion de contrasena, donde no hay un usuario
 * logueado del que reenviar un token).
 *
 * El cliente HTTP vive dentro del filtro (no como bean separado) a proposito: @WebMvcTest
 * incluye siempre los beans Filter encontrados en el paquete escaneado, pero no un
 * @Component plano aparte, así que separarlos rompe los tests de controller con
 * "No qualifying bean" al no poder resolver esa dependencia dentro del slice.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String INTERNAL_API_KEY_HEADER = "X-Internal-Api-Key";

    private final RestClient restClient;
    private final String internalApiKey;

    public JwtAuthenticationFilter(@Value("${auth.service.url}") String authServiceUrl,
                                    @Value("${internal.api.key}") String internalApiKey) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(3000);
        factory.setReadTimeout(3000);

        this.restClient = RestClient.builder()
                .baseUrl(authServiceUrl)
                .requestFactory(factory)
                .build();
        this.internalApiKey = internalApiKey;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String internalKey = request.getHeader(INTERNAL_API_KEY_HEADER);

        if (StringUtils.hasText(internalKey) && matchesInternalKey(internalKey)) {
            authenticate(request, AuthenticatedUser.internalService());
        } else {
            String authorization = request.getHeader("Authorization");
            if (StringUtils.hasText(authorization)) {
                validate(authorization).ifPresent(user -> authenticate(request, user));
            }
        }

        filterChain.doFilter(request, response);
    }

    private boolean matchesInternalKey(String candidate) {
        return MessageDigest.isEqual(
                candidate.getBytes(StandardCharsets.UTF_8),
                internalApiKey.getBytes(StandardCharsets.UTF_8));
    }

    private void authenticate(HttpServletRequest request, AuthenticatedUser user) {
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                user, null, List.of(new SimpleGrantedAuthority("ROLE_" + user.role())));
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authToken);
    }

    /**
     * Llama a GET /api/auth/validate con el header Authorization tal cual llego.
     * Cualquier fallo (401 de ms-auth, timeout, conexion rechazada) se trata igual: no autenticado.
     */
    private Optional<AuthenticatedUser> validate(String authorizationHeader) {
        try {
            Map<String, Object> body = restClient.get()
                    .uri("/api/auth/validate")
                    .header(HttpHeaders.AUTHORIZATION, authorizationHeader)
                    .retrieve()
                    .body(new ParameterizedTypeReference<Map<String, Object>>() {
                    });

            if (body == null || body.get("id") == null || body.get("role") == null) {
                return Optional.empty();
            }

            Long id = ((Number) body.get("id")).longValue();
            String role = String.valueOf(body.get("role"));
            String email = String.valueOf(body.get("email"));
            return Optional.of(new AuthenticatedUser(id, email, role));
        } catch (RestClientException | ClassCastException e) {
            return Optional.empty();
        }
    }
}
