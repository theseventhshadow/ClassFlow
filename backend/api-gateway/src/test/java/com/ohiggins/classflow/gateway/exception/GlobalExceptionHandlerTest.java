package com.ohiggins.classflow.gateway.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.ServerWebExchange;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("GlobalExceptionHandler Tests")
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    private ServerWebExchange createExchange(String path) {
        return MockServerWebExchange.from(
                MockServerHttpRequest.get(path).build()
        );
    }

    @Test
    @DisplayName("Should handle RuntimeException and return 400")
    void testHandleRuntimeException() {
        ServerWebExchange exchange = createExchange("/api/test");
        RuntimeException exception = new RuntimeException("Error de prueba");

        ResponseEntity<ErrorResponse> response = handler.handleRuntimeException(exception, exchange);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(400);
        assertThat(response.getBody().getError()).isEqualTo("Bad Request");
        assertThat(response.getBody().getMessage()).isEqualTo("Error de prueba");
        assertThat(response.getBody().getPath()).isEqualTo("/api/test");
        assertThat(response.getBody().getTimestamp()).isNotNull();
    }

    @Test
    @DisplayName("Should handle generic Exception and return 500")
    void testHandleGenericException() {
        ServerWebExchange exchange = createExchange("/api/error");
        Exception exception = new Exception("Error interno");

        ResponseEntity<ErrorResponse> response = handler.handleGenericException(exception, exchange);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(500);
        assertThat(response.getBody().getError()).isEqualTo("Internal Server Error");
        assertThat(response.getBody().getMessage()).contains("Error interno");
        assertThat(response.getBody().getPath()).isEqualTo("/api/error");
        assertThat(response.getBody().getTimestamp()).isNotNull();
    }

    @Test
    @DisplayName("Should include path in error response")
    void testErrorResponseIncludesPath() {
        ServerWebExchange exchange = createExchange("/api/auth/login");
        RuntimeException exception = new RuntimeException("Auth failed");

        ResponseEntity<ErrorResponse> response = handler.handleRuntimeException(exception, exchange);

        assertThat(response.getBody().getPath()).isEqualTo("/api/auth/login");
    }
}
