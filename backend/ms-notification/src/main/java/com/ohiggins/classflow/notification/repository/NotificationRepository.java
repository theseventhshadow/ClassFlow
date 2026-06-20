package com.ohiggins.classflow.notification.repository;

import com.ohiggins.classflow.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserId(Long userId);
    List<Notification> findByUserIdAndSentFalse(Long userId);
    List<Notification> findBySentFalse();
}