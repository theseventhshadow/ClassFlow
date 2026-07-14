package com.ohiggins.classflow.notification.security;

/**
 * Datos minimos del usuario autenticado. Para llamadas internas de servicio a servicio
 * (ej. ms-auth enviando el correo de recuperacion de contrasena), id/email quedan en null
 * y el role es "SERVICE".
 */
public record AuthenticatedUser(Long id, String email, String role) {

    public static final String SERVICE_ROLE = "SERVICE";

    public static AuthenticatedUser internalService() {
        return new AuthenticatedUser(null, null, SERVICE_ROLE);
    }
}
