package com.ohiggins.classflow.gateway.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ohiggins.classflow.gateway.exception.ErrorResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Valida el JWT de cada request delegando en ms-auth (GET /api/auth/validate), unico
 * servicio que conoce JWT_SECRET. Todo vive en este bean para no depender de otro
 * @Component: @WebFluxTest incluye automaticamente la configuracion de seguridad
 * encontrada en el paquete escaneado, y una dependencia externa faltante en ese slice
 * rompe el contexto con "No qualifying bean" (aprendido al hacer lo mismo en los
 * microservicios MVC).
 */
@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    private static final String[] PUBLIC_AUTH_POST_ENDPOINTS = {
        "/api/auth/login",
        "/api/auth/forgot-password",
        "/api/auth/reset-password"
    };

    private static final String[] PUBLIC_ENDPOINTS = {
        "/actuator/**",
        "/swagger-ui/**",
        "/swagger-ui.html",
        "/v3/api-docs/**",
        "/webjars/**"
    };

    private final WebClient authWebClient;
    private final ObjectMapper objectMapper;

    public SecurityConfig(@Value("${auth.service.url}") String authServiceUrl, ObjectMapper objectMapper) {
        this.authWebClient = WebClient.builder().baseUrl(authServiceUrl).build();
        this.objectMapper = objectMapper;
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        AuthenticationWebFilter authenticationWebFilter = new AuthenticationWebFilter(authenticationManager());
        authenticationWebFilter.setServerAuthenticationConverter(bearerConverter());
        authenticationWebFilter.setSecurityContextRepository(NoOpServerSecurityContextRepository.getInstance());

        http
            .csrf(ServerHttpSecurity.CsrfSpec::disable)
            .authorizeExchange(exchanges -> exchanges
                .pathMatchers(HttpMethod.POST, PUBLIC_AUTH_POST_ENDPOINTS).permitAll()
                .pathMatchers(PUBLIC_ENDPOINTS).permitAll()
                .anyExchange().authenticated())
            .addFilterAt(authenticationWebFilter, SecurityWebFiltersOrder.AUTHENTICATION)
            .exceptionHandling(handling -> handling
                .authenticationEntryPoint((exchange, ex) -> writeError(exchange, HttpStatus.UNAUTHORIZED, "Token inválido, ausente o expirado."))
                .accessDeniedHandler((exchange, ex) -> writeError(exchange, HttpStatus.FORBIDDEN, "No tienes permiso para realizar esta acción.")));

        return http.build();
    }

    private ServerAuthenticationConverter bearerConverter() {
        return exchange -> Mono.justOrEmpty(exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION))
                .filter(header -> !header.isBlank())
                .map(header -> new UsernamePasswordAuthenticationToken(header, header));
    }

    /**
     * Llama a GET /api/auth/validate con el header Authorization tal cual llego. Cualquier
     * fallo (401 de ms-auth, timeout, conexion rechazada) se trata igual: credenciales invalidas.
     */
    private ReactiveAuthenticationManager authenticationManager() {
        return authentication -> {
            String authorizationHeader = (String) authentication.getCredentials();
            return authWebClient.get()
                    .uri("/api/auth/validate")
                    .header(HttpHeaders.AUTHORIZATION, authorizationHeader)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {
                    })
                    .timeout(Duration.ofSeconds(3))
                    .flatMap(body -> {
                        Object role = body.get("role");
                        if (role == null) {
                            return Mono.<Authentication>error(new BadCredentialsException("Token inválido"));
                        }
                        Authentication authToken = new UsernamePasswordAuthenticationToken(
                                body.get("email"), null, List.of(new SimpleGrantedAuthority("ROLE_" + role)));
                        return Mono.just(authToken);
                    })
                    .onErrorMap(ex -> !(ex instanceof BadCredentialsException), ex -> new BadCredentialsException("Token inválido", ex));
        };
    }

    private Mono<Void> writeError(ServerWebExchange exchange, HttpStatus status, String message) {
        var response = exchange.getResponse();
        response.setStatusCode(status);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        ErrorResponse error = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(message)
                .path(exchange.getRequest().getURI().getPath())
                .build();

        byte[] bytes;
        try {
            bytes = objectMapper.writeValueAsBytes(error);
        } catch (Exception e) {
            bytes = new byte[0];
        }
        DataBuffer buffer = response.bufferFactory().wrap(bytes);
        return response.writeWith(Mono.just(buffer));
    }
}
