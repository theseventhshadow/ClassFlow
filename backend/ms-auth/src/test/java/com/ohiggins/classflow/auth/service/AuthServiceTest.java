package com.ohiggins.classflow.auth.service;

import com.ohiggins.classflow.auth.dto.*;
import com.ohiggins.classflow.auth.entity.Role;
import com.ohiggins.classflow.auth.entity.User;
import com.ohiggins.classflow.auth.repository.UserRepository;
import com.ohiggins.classflow.auth.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService Tests")
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserService userService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider tokenProvider;

    @InjectMocks
    private AuthService authService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setFirstName("Juan");
        user.setLastName("Pérez");
        user.setIdNumber("12.345.678-9");
        user.setEmail("juan@example.com");
        user.setPassword("encoded-password");
        user.setRole(Role.STUDENT);
        user.setCourse("1A");
        user.setActive(true);
    }

    @Test
    @DisplayName("Should login successfully")
    void testLogin() {
        LoginRequestDTO request = new LoginRequestDTO();
        request.setEmail("juan@example.com");
        request.setPassword("secret");

        UserDetails principal = user;
        Authentication authentication = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());

        when(authenticationManager.authenticate(any(Authentication.class))).thenReturn(authentication);
        when(tokenProvider.generateToken(authentication)).thenReturn("jwt-token");
        when(userRepository.findByEmail("juan@example.com")).thenReturn(Optional.of(user));

        LoginResponseDTO result = authService.login(request);

        assertThat(result)
                .isNotNull()
                .extracting(LoginResponseDTO::getToken, LoginResponseDTO::getEmail, LoginResponseDTO::getRole, LoginResponseDTO::getFullName)
                .containsExactly("jwt-token", "juan@example.com", "STUDENT", "Juan Pérez");

        verify(authenticationManager, times(1)).authenticate(any(Authentication.class));
        verify(tokenProvider, times(1)).generateToken(authentication);
        verify(userRepository, times(1)).findByEmail("juan@example.com");
    }

    @Test
    @DisplayName("Should register user successfully")
    void testRegister() {
        RegisterRequestDTO request = new RegisterRequestDTO();
        request.setFirstName("Juan");
        request.setLastName("Pérez");
        request.setIdNumber("12.345.678-9");
        request.setEmail("juan@example.com");
        request.setPassword("secret123");
        request.setRole("STUDENT");
        request.setCourse("1A");
        request.setGuardianId(55L);

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setFirstName("Juan");
        savedUser.setLastName("Pérez");
        savedUser.setIdNumber("12.345.678-9");
        savedUser.setEmail("juan@example.com");
        savedUser.setPassword("encoded-secret");
        savedUser.setRole(Role.STUDENT);
        savedUser.setCourse("1A");
        savedUser.setGuardianId(55L);
        savedUser.setActive(true);

        when(userRepository.existsByEmail("juan@example.com")).thenReturn(false);
        when(userRepository.existsByIdNumber("12.345.678-9")).thenReturn(false);
        when(passwordEncoder.encode("secret123")).thenReturn("encoded-secret");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(userService.convertToDTO(savedUser)).thenReturn(UserResponseDTO.builder()
                .id(1L)
                .firstName("Juan")
                .lastName("Pérez")
                .fullName("Juan Pérez")
                .idNumber("12.345.678-9")
                .email("juan@example.com")
                .role("STUDENT")
                .course("1A")
                .active(true)
                .build());

        UserResponseDTO result = authService.register(request);

        assertThat(result)
                .isNotNull()
                .extracting(UserResponseDTO::getEmail, UserResponseDTO::getRole)
                .containsExactly("juan@example.com", "STUDENT");

        verify(userRepository, times(1)).existsByEmail("juan@example.com");
        verify(userRepository, times(1)).existsByIdNumber("12.345.678-9");
        verify(userRepository, times(1)).save(any(User.class));
        verify(userService, times(1)).convertToDTO(savedUser);
    }

    @Test
    @DisplayName("Should throw RuntimeException when email already exists")
    void testRegisterDuplicateEmail() {
        RegisterRequestDTO request = new RegisterRequestDTO();
        request.setEmail("juan@example.com");
        request.setIdNumber("12.345.678-9");

        when(userRepository.existsByEmail("juan@example.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Email already registered");

        verify(userRepository, times(1)).existsByEmail("juan@example.com");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should validate token successfully")
    void testValidateToken() {
        when(tokenProvider.validateToken("jwt-token")).thenReturn(true);
        when(tokenProvider.getEmailFromToken("jwt-token")).thenReturn("juan@example.com");
        when(userService.findByEmail("juan@example.com")).thenReturn(UserResponseDTO.builder()
                .id(1L)
                .firstName("Juan")
                .lastName("Pérez")
                .fullName("Juan Pérez")
                .email("juan@example.com")
                .role("STUDENT")
                .build());

        UserResponseDTO result = authService.validateToken("jwt-token");

        assertThat(result)
                .isNotNull()
                .extracting(UserResponseDTO::getEmail)
                .isEqualTo("juan@example.com");

        verify(tokenProvider, times(1)).validateToken("jwt-token");
        verify(tokenProvider, times(1)).getEmailFromToken("jwt-token");
        verify(userService, times(1)).findByEmail("juan@example.com");
    }

    @Test
    @DisplayName("Should throw RuntimeException when token is invalid")
    void testValidateTokenInvalid() {
        when(tokenProvider.validateToken("bad-token")).thenReturn(false);

        assertThatThrownBy(() -> authService.validateToken("bad-token"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Invalid token");

        verify(tokenProvider, times(1)).validateToken("bad-token");
        verify(tokenProvider, never()).getEmailFromToken(any());
    }

    @Test
    @DisplayName("Should change password successfully")
    void testChangePassword() {
        ChangePasswordRequestDTO request = new ChangePasswordRequestDTO();
        request.setCurrentPassword("old-pass");
        request.setNewPassword("new-pass");

        User updated = new User();
        updated.setId(1L);
        updated.setFirstName("Juan");
        updated.setLastName("Pérez");
        updated.setIdNumber("12.345.678-9");
        updated.setEmail("juan@example.com");
        updated.setPassword("encoded-new-pass");
        updated.setRole(Role.STUDENT);
        updated.setCourse("1A");
        updated.setActive(true);

        when(userRepository.findByEmail("juan@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("old-pass", "encoded-password")).thenReturn(true);
        when(passwordEncoder.encode("new-pass")).thenReturn("encoded-new-pass");
        when(userRepository.save(any(User.class))).thenReturn(updated);
        when(userService.convertToDTO(updated)).thenReturn(UserResponseDTO.builder()
                .id(1L)
                .firstName("Juan")
                .lastName("Pérez")
                .fullName("Juan Pérez")
                .email("juan@example.com")
                .role("STUDENT")
                .course("1A")
                .active(true)
                .build());

        UserResponseDTO result = authService.changePassword("juan@example.com", request);

        assertThat(result)
                .isNotNull()
                .extracting(UserResponseDTO::getEmail)
                .isEqualTo("juan@example.com");

        verify(userRepository, times(1)).findByEmail("juan@example.com");
        verify(passwordEncoder, times(1)).matches("old-pass", "encoded-password");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw RuntimeException when current password is incorrect")
    void testChangePasswordWrongCurrentPassword() {
        ChangePasswordRequestDTO request = new ChangePasswordRequestDTO();
        request.setCurrentPassword("wrong");
        request.setNewPassword("new-pass");

        when(userRepository.findByEmail("juan@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "encoded-password")).thenReturn(false);

        assertThatThrownBy(() -> authService.changePassword("juan@example.com", request))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Current password is incorrect");

        verify(userRepository, times(1)).findByEmail("juan@example.com");
        verify(userRepository, never()).save(any(User.class));
    }
}