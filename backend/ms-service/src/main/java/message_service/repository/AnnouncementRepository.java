package message_service.repository;

import message_service.entity.Announcement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {
    List<Announcement> findByCourseIdOrCourseIdIsNull(Long courseId);
    List<Announcement> findByActiveTrue();
}
