package notification_service.repository;

import notification_service.entity.Notification;
import notification_service.enums.NotificationType;
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
@DisplayName("NotificationRepository Tests")
class NotificationRepositoryTest {

    @Autowired
    private NotificationRepository notificationRepository;

    private Notification notification;

    @BeforeEach
    void setUp() {
        notification = new Notification();
        notification.setUserId(101L);
        notification.setType(NotificationType.ALERT);
        notification.setSubject("Subject");
        notification.setContent("Content");
        notification.setSent(false);
    }

    @Test
    @DisplayName("Should save and retrieve notification")
    void testSaveAndRetrieve() {
        Notification saved = notificationRepository.save(notification);

        assertThat(saved)
                .isNotNull()
                .extracting(Notification::getUserId, Notification::getSent)
                .containsExactly(101L, false);
    }

    @Test
    @DisplayName("Should find notifications by user id")
    void testFindByUserId() {
        notificationRepository.save(notification);

        assertThat(notificationRepository.findByUserId(101L))
                .hasSize(1)
                .extracting(Notification::getSubject)
                .containsExactly("Subject");
    }

    @Test
    @DisplayName("Should find pending notifications by user id")
    void testFindByUserIdAndSentFalse() {
        notificationRepository.save(notification);

        assertThat(notificationRepository.findByUserIdAndSentFalse(101L))
                .hasSize(1);
    }

    @Test
    @DisplayName("Should find all pending notifications")
    void testFindBySentFalse() {
        notificationRepository.save(notification);

        assertThat(notificationRepository.findBySentFalse())
                .hasSize(1);
    }

    @Test
    @DisplayName("Should mark notification as sent")
    void testUpdateSentState() {
        Notification saved = notificationRepository.save(notification);
        saved.setSent(true);
        Notification updated = notificationRepository.save(saved);

        assertThat(updated.getSent()).isTrue();
        Optional<Notification> result = notificationRepository.findById(updated.getId());
        assertThat(result).isPresent();
    }
}