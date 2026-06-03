package assistance_service.repository;

import assistance_service.entity.Attendance;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@TestPropertySource(locations = "classpath:application-test.properties")
@DisplayName("AttendanceRepository Tests")
class AttendanceRepositoryTest {

    @Autowired
    private AttendanceRepository attendanceRepository;

    private Attendance attendance;

    @BeforeEach
    void setUp() {
        attendance = new Attendance();
        attendance.setStudentId(101L);
        attendance.setCourseId(201L);
        attendance.setDate(LocalDate.of(2026, 5, 15));
        attendance.setPresent(true);
        attendance.setJustification("Present");
    }

    @Test
    @DisplayName("Should save and retrieve attendance")
    void testSaveAndFindAttendance() {
        Attendance saved = attendanceRepository.save(attendance);

        assertThat(saved)
                .isNotNull()
                .extracting(Attendance::getStudentId, Attendance::getCourseId, Attendance::getPresent)
                .containsExactly(101L, 201L, true);
    }

    @Test
    @DisplayName("Should find attendance by student id")
    void testFindByStudentId() {
        attendanceRepository.save(attendance);

        assertThat(attendanceRepository.findByStudentId(101L))
                .hasSize(1)
                .extracting(Attendance::getCourseId)
                .containsExactly(201L);
    }

    @Test
    @DisplayName("Should find attendance by course and date")
    void testFindByCourseIdAndDate() {
        attendanceRepository.save(attendance);

        assertThat(attendanceRepository.findByCourseIdAndDate(201L, LocalDate.of(2026, 5, 15)))
                .hasSize(1)
                .extracting(Attendance::getStudentId)
                .containsExactly(101L);
    }

    @Test
    @DisplayName("Should find attendances by student id and date range")
    void testFindByStudentIdAndDateBetween() {
        Attendance attendance2 = new Attendance();
        attendance2.setStudentId(101L);
        attendance2.setCourseId(202L);
        attendance2.setDate(LocalDate.of(2026, 5, 16));
        attendance2.setPresent(false);

        attendanceRepository.save(attendance);
        attendanceRepository.save(attendance2);

        assertThat(attendanceRepository.findByStudentIdAndDateBetween(
                101L,
                LocalDate.of(2026, 5, 14),
                LocalDate.of(2026, 5, 16)))
                .hasSize(2);
    }

    @Test
    @DisplayName("Should delete attendance")
    void testDeleteAttendance() {
        Attendance saved = attendanceRepository.save(attendance);
        Long id = saved.getId();

        attendanceRepository.deleteById(id);

        Optional<Attendance> result = attendanceRepository.findById(id);

        assertThat(result).isEmpty();
    }
}