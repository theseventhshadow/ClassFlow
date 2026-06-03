package auth_service.controller;

import auth_service.dto.*;
import auth_service.service.AuthService;
import auth_service.service.UserService;
import auth_service.security.JwtTokenProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("AuthController Tests")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    private LoginResponseDTO loginResponseDTO;
    private UserResponseDTO userResponseDTO;

    @BeforeEach
    void setUp() {
        loginResponseDTO = new LoginResponseDTO("jwt-token", "juan@example.com", "STUDENT", "Juan Pérez");

        userResponseDTO = UserResponseDTO.builder()
                .id(1L)
                .firstName("Juan")
                .lastName("Pérez")
                .fullName("Juan Pérez")
                .idNumber("12.345.678-9")
                .email("juan@example.com")
                .role("STUDENT")
                .course("1A")
                .active(true)
                .build();
    }

    @Test
    @DisplayName("Should login successfully")
    void testLogin() throws Exception {
        LoginRequestDTO request = new LoginRequestDTO();
        request.setEmail("juan@example.com");
        request.setPassword("secret123");

        when(authService.login(any(LoginRequestDTO.class))).thenReturn(loginResponseDTO);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", equalTo("jwt-token")))
                .andExpect(jsonPath("$.email", equalTo("juan@example.com")))
                .andExpect(jsonPath("$.role", equalTo("STUDENT")));

        verify(authService, times(1)).login(any(LoginRequestDTO.class));
    }

    @Test
    @DisplayName("Should register user successfully")
    void testRegister() throws Exception {
        RegisterRequestDTO request = new RegisterRequestDTO();
        request.setFirstName("Juan");
        request.setLastName("Pérez");
        request.setIdNumber("12.345.678-9");
        request.setEmail("juan@example.com");
        request.setPassword("secret123");
        request.setRole("STUDENT");
        request.setCourse("1A");
        request.setGuardianId(55L);

        when(authService.register(any(RegisterRequestDTO.class))).thenReturn(userResponseDTO);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", equalTo(1)))
                .andExpect(jsonPath("$.email", equalTo("juan@example.com")));

        verify(authService, times(1)).register(any(RegisterRequestDTO.class));
    }

    @Test
    @DisplayName("Should validate token successfully")
    void testValidate() throws Exception {
        when(authService.validateToken("jwt-token")).thenReturn(userResponseDTO);

        mockMvc.perform(get("/api/auth/validate")
                        .header("Authorization", "Bearer jwt-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email", equalTo("juan@example.com")));

        verify(authService, times(1)).validateToken("jwt-token");
    }

    @Test
    @DisplayName("Should get user by id")
    void testGetUserById() throws Exception {
        when(userService.findById(1L)).thenReturn(userResponseDTO);

        mockMvc.perform(get("/api/auth/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName", equalTo("Juan Pérez")));

        verify(userService, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should update user successfully")
    void testUpdateUser() throws Exception {
        UpdateUserRequestDTO request = new UpdateUserRequestDTO();
        request.setFirstName("Juan");
        request.setLastName("Gómez");
        request.setEmail("juan.gomez@example.com");
        request.setCourse("2A");

        UserResponseDTO updated = UserResponseDTO.builder()
                .id(1L)
                .firstName("Juan")
                .lastName("Gómez")
                .fullName("Juan Gómez")
                .idNumber("12.345.678-9")
                .email("juan.gomez@example.com")
                .role("STUDENT")
                .course("2A")
                .active(true)
                .build();

        when(userService.updateFromRequest(eq(1L), any(UpdateUserRequestDTO.class))).thenReturn(updated);

        mockMvc.perform(put("/api/auth/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email", equalTo("juan.gomez@example.com")));

        verify(userService, times(1)).updateFromRequest(eq(1L), any(UpdateUserRequestDTO.class));
    }

    @Test
    @DisplayName("Should delete user successfully")
    void testDeleteUser() throws Exception {
        doNothing().when(userService).delete(1L);

        mockMvc.perform(delete("/api/auth/users/1"))
                .andExpect(status().isNoContent());

        verify(userService, times(1)).delete(1L);
    }

    @Test
    @DisplayName("Should change password successfully")
    void testChangePassword() throws Exception {
        ChangePasswordRequestDTO request = new ChangePasswordRequestDTO();
        request.setCurrentPassword("old-pass");
        request.setNewPassword("new-pass");

        when(authService.validateToken("jwt-token")).thenReturn(userResponseDTO);
        when(authService.changePassword(eq("juan@example.com"), any(ChangePasswordRequestDTO.class))).thenReturn(userResponseDTO);

        mockMvc.perform(post("/api/auth/change-password")
                        .header("Authorization", "Bearer jwt-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email", equalTo("juan@example.com")));

        verify(authService, times(1)).validateToken("jwt-token");
        verify(authService, times(1)).changePassword(eq("juan@example.com"), any(ChangePasswordRequestDTO.class));
    }

    @Test
    @DisplayName("Should get current user")
    void testGetCurrentUser() throws Exception {
        when(authService.validateToken("jwt-token")).thenReturn(userResponseDTO);

        mockMvc.perform(get("/api/auth/me")
                        .header("Authorization", "Bearer jwt-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email", equalTo("juan@example.com")));

        verify(authService, times(1)).validateToken("jwt-token");
    }
}