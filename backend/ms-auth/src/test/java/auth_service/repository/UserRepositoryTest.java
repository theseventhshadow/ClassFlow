package auth_service.repository;

import auth_service.entity.Role;
import auth_service.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@TestPropertySource(locations = "classpath:application-test.properties")
@DisplayName("UserRepository Tests")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setFirstName("Juan");
        user.setLastName("Pérez");
        user.setIdNumber("123456789");
        user.setEmail("juan@example.com");
        user.setPassword("encoded-password");
        user.setRole(Role.STUDENT);
        user.setCourse("1A");
        user.setGuardianId(55L);
        user.setActive(true);
    }

    @Test
    @DisplayName("Should save and retrieve user")
    void testSaveAndRetrieveUser() {
        User saved = userRepository.save(user);

        assertThat(saved)
            .isNotNull()
            .extracting(User::getEmail, User::getRole)
            .containsExactly("juan@example.com", Role.STUDENT);
    }

    @Test
    @DisplayName("Should find user by email")
    void testFindByEmail() {
        userRepository.save(user);

        Optional<User> result = userRepository.findByEmail("juan@example.com");

        assertThat(result)
                .isPresent()
                .get()
                .extracting(User::getFirstName)
                .isEqualTo("Juan");
    }

    @Test
    @DisplayName("Should find user by id number")
    void testFindByIdNumber() {
        userRepository.save(user);

        Optional<User> result = userRepository.findByIdNumber("123456789");

        assertThat(result)
                .isPresent()
                .get()
                .extracting(User::getLastName)
                .isEqualTo("Pérez");
    }

    @Test
    @DisplayName("Should check user exists by email and id number")
    void testExistsChecks() {
        userRepository.save(user);

        assertThat(userRepository.existsByEmail("juan@example.com")).isTrue();
        assertThat(userRepository.existsByIdNumber("123456789")).isTrue();
    }

    @Test
    @DisplayName("Should find users by role")
    void testFindByRole() {
        userRepository.save(user);

        assertThat(userRepository.findByRole(Role.STUDENT))
                .hasSize(1)
                .extracting(User::getEmail)
                .containsExactly("juan@example.com");
    }

    @Test
    @DisplayName("Should find users by course")
    void testFindByCourse() {
        userRepository.save(user);

        assertThat(userRepository.findByCourse("1A"))
                .hasSize(1)
                .extracting(User::getIdNumber)
            .containsExactly("123456789");
    }

    @Test
    @DisplayName("Should find users by guardian id")
    void testFindByGuardianId() {
        userRepository.save(user);

        assertThat(userRepository.findByGuardianId(55L))
                .hasSize(1)
                .extracting(User::getEmail)
                .containsExactly("juan@example.com");
    }
}