package com.ohiggins.classflow.auth.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Configura la seguridad HTTP y los componentes basicos de autenticacion.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    /** Endpoints POST de autenticacion accesibles sin token, porque el usuario aun no tiene uno en ese punto. */
    private static final String[] PUBLIC_AUTH_POST_ENDPOINTS = {
        "/api/auth/login",
        "/api/auth/forgot-password",
        "/api/auth/reset-password"
    };

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * Define la cadena de filtros y reglas de acceso.
     *
     * @param http configuracion HTTP de Spring Security.
     * @return cadena de filtros de seguridad.
     * @throws Exception si la configuracion falla.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.POST, PUBLIC_AUTH_POST_ENDPOINTS).permitAll()
                .requestMatchers(HttpMethod.POST, "/api/auth/register").hasRole("ADMINISTRATOR")
                .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**").permitAll()
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Expone el AuthenticationManager de Spring Security.
     *
     * @param config configuracion de autenticacion.
     * @return manager de autenticacion.
     * @throws Exception si no puede resolverse.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Configura el encoder de contrasenas usado por el servicio.
     *
     * @return encoder BCrypt.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}