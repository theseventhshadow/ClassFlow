package com.ohiggins.classflow.auth.security;

import com.ohiggins.classflow.auth.entity.Role;
import com.ohiggins.classflow.auth.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("JwtTokenProvider Tests")
class JwtTokenProviderTest {

    private JwtTokenProvider tokenProvider;

    @BeforeEach
    void setUp() {
        tokenProvider = new JwtTokenProvider();
        ReflectionTestUtils.setField(tokenProvider, "jwtSecret", "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970");
        ReflectionTestUtils.setField(tokenProvider, "jwtExpiration", 86400000);
        tokenProvider.init();
    }

    @Test
    @DisplayName("Should generate and validate token")
    void testGenerateAndValidateToken() {
        User user = new User();
        user.setEmail("juan@example.com");
        user.setPassword("password");
        user.setFirstName("Juan");
        user.setLastName("Pérez");
        user.setRole(Role.STUDENT);
        user.setActive(true);

        UserDetails principal = user;
        Authentication authentication = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());

        String token = tokenProvider.generateToken(authentication);

        assertThat(token).isNotBlank();
        assertThat(tokenProvider.validateToken(token)).isTrue();
        assertThat(tokenProvider.getEmailFromToken(token)).isEqualTo("juan@example.com");
    }

    @Test
    @DisplayName("Should reject invalid token")
    void testInvalidToken() {
        assertThat(tokenProvider.validateToken("bad-token")).isFalse();
    }
}