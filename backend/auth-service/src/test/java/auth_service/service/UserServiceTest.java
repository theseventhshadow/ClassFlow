package auth_service.service;

import auth_service.dto.UpdateUserRequestDTO;
import auth_service.dto.UserResponseDTO;
import auth_service.entity.Role;
import auth_service.entity.User;
import auth_service.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService Tests")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User user;
    private UserResponseDTO userDTO;

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

        userDTO = UserResponseDTO.builder()
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
    @DisplayName("Should find user by id")
    void testFindById() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserResponseDTO result = userService.findById(1L);

        assertThat(result)
                .isNotNull()
                .extracting(UserResponseDTO::getEmail, UserResponseDTO::getFullName)
                .containsExactly("juan@example.com", "Juan Pérez");

        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should throw RuntimeException when user not found by id")
    void testFindByIdNotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.findById(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("User not found with ID: 999");

        verify(userRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Should find user by email")
    void testFindByEmail() {
        when(userRepository.findByEmail("juan@example.com")).thenReturn(Optional.of(user));

        UserResponseDTO result = userService.findByEmail("juan@example.com");

        assertThat(result)
                .isNotNull()
                .extracting(UserResponseDTO::getIdNumber)
                .isEqualTo("12.345.678-9");

        verify(userRepository, times(1)).findByEmail("juan@example.com");
    }

    @Test
    @DisplayName("Should find user by id number")
    void testFindByIdNumber() {
        when(userRepository.findByIdNumber("12.345.678-9")).thenReturn(Optional.of(user));

        UserResponseDTO result = userService.findByIdNumber("12.345.678-9");

        assertThat(result)
                .isNotNull()
                .extracting(UserResponseDTO::getFirstName)
                .isEqualTo("Juan");

        verify(userRepository, times(1)).findByIdNumber("12.345.678-9");
    }

    @Test
    @DisplayName("Should update user successfully")
    void testUpdate() {
        User updated = new User();
        updated.setId(1L);
        updated.setFirstName("Juan");
        updated.setLastName("Gómez");
        updated.setIdNumber("12.345.678-9");
        updated.setEmail("juan.gomez@example.com");
        updated.setPassword("encoded-password");
        updated.setRole(Role.STUDENT);
        updated.setCourse("2A");
        updated.setActive(true);

        UserResponseDTO updateDTO = UserResponseDTO.builder()
                .id(1L)
                .firstName("Juan")
                .lastName("Gómez")
                .email("juan.gomez@example.com")
                .course("2A")
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(updated);

        UserResponseDTO result = userService.update(1L, updateDTO);

        assertThat(result)
                .isNotNull()
                .extracting(UserResponseDTO::getEmail, UserResponseDTO::getCourse)
                .containsExactly("juan.gomez@example.com", "2A");

        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Should update user from request successfully")
    void testUpdateFromRequest() {
        User updated = new User();
        updated.setId(1L);
        updated.setFirstName("Juan");
        updated.setLastName("Gómez");
        updated.setIdNumber("12.345.678-9");
        updated.setEmail("juan.gomez@example.com");
        updated.setPassword("encoded-password");
        updated.setRole(Role.STUDENT);
        updated.setCourse("2A");
        updated.setActive(true);

        UpdateUserRequestDTO request = new UpdateUserRequestDTO();
        request.setFirstName("Juan");
        request.setLastName("Gómez");
        request.setEmail("juan.gomez@example.com");
        request.setCourse("2A");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(updated);

        UserResponseDTO result = userService.updateFromRequest(1L, request);

        assertThat(result)
                .isNotNull()
                .extracting(UserResponseDTO::getFullName)
                .isEqualTo("Juan Gómez");

        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Should logically delete user")
    void testDelete() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        userService.delete(1L);

        assertThat(user.getActive()).isFalse();
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw RuntimeException when user not found during update")
    void testUpdateNotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.update(999L, userDTO))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("User not found");

        verify(userRepository, times(1)).findById(999L);
        verify(userRepository, never()).save(any(User.class));
    }
}