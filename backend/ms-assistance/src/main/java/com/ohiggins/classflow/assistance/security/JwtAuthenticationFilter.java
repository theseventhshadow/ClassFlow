package com.ohiggins.classflow.assistance.security;

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
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Delega la validacion del JWT a ms-auth (unico servicio que conoce JWT_SECRET), en vez
 * de duplicar la logica de firma/expiracion en cada microservicio. El cliente HTTP vive
 * dentro del filtro (no como bean separado) a proposito: @WebMvcTest incluye siempre los
 * beans Filter encontrados en el paquete escaneado, pero no un @Component plano aparte,
 * así que separarlos rompe los tests de controller con "No qualifying bean" al no poder
 * resolver esa dependencia dentro del slice.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final RestClient restClient;

    public JwtAuthenticationFilter(@Value("${auth.service.url}") String authServiceUrl) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(3000);
        factory.setReadTimeout(3000);

        this.restClient = RestClient.builder()
                .baseUrl(authServiceUrl)
                .requestFactory(factory)
                .build();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String authorization = request.getHeader("Authorization");

        if (StringUtils.hasText(authorization)) {
            validate(authorization).ifPresent(user -> {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        user, null, List.of(new SimpleGrantedAuthority("ROLE_" + user.role())));
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            });
        }

        filterChain.doFilter(request, response);
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
