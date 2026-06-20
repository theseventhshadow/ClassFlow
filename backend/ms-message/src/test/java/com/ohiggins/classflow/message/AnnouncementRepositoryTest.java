package com.ohiggins.classflow.message.repository;

import com.ohiggins.classflow.message.entity.Announcement;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@TestPropertySource(locations = "classpath:application-test.properties")
class AnnouncementRepositoryTest {

	@Autowired
	private AnnouncementRepository announcementRepository;

	@Test
	void findByActiveTrueReturnsOnlyActiveAnnouncements() {
		announcementRepository.save(new Announcement(null, "Title 1", "Content 1", 30L, 40L, LocalDateTime.now(), true));
		announcementRepository.save(new Announcement(null, "Title 2", "Content 2", 30L, 41L, LocalDateTime.now(), false));

		assertThat(announcementRepository.findByActiveTrue()).hasSize(1);
	}

	@Test
	void findByCourseIdOrCourseIdIsNullReturnsCourseAndGlobalAnnouncements() {
		announcementRepository.save(new Announcement(null, "Title 1", "Content 1", 30L, 40L, LocalDateTime.now(), true));
		announcementRepository.save(new Announcement(null, "Title 2", "Content 2", null, 41L, LocalDateTime.now(), true));
		announcementRepository.save(new Announcement(null, "Title 3", "Content 3", 31L, 42L, LocalDateTime.now(), true));

		assertThat(announcementRepository.findByCourseIdOrCourseIdIsNull(30L)).hasSize(2);
	}
}