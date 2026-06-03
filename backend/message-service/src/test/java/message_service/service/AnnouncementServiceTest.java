package message_service.service;

import message_service.dto.AnnouncementDTO;
import message_service.dto.AnnouncementRequestDTO;
import message_service.entity.Announcement;
import message_service.repository.AnnouncementRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("null")
class AnnouncementServiceTest {

	@Mock
	private AnnouncementRepository announcementRepository;

	@InjectMocks
	private AnnouncementService announcementService;

	private Announcement announcement;
	private AnnouncementRequestDTO request;

	@BeforeEach
	void setUp() {
		announcement = new Announcement(1L, "Title", "Content", 30L, 40L, LocalDateTime.of(2026, 5, 15, 11, 0), true);
		request = new AnnouncementRequestDTO();
		request.setTitle("Title");
		request.setContent("Content");
		request.setCourseId(30L);
		request.setSenderId(40L);
	}

	@Test
	void findAllMapsAnnouncements() {
		when(announcementRepository.findAll()).thenReturn(List.of(announcement));

		List<AnnouncementDTO> result = announcementService.findAll();

		assertThat(result).hasSize(1);
		assertThat(result.get(0).getTitle()).isEqualTo("Title");
	}

	@Test
	void findActiveMapsAnnouncements() {
		when(announcementRepository.findByActiveTrue()).thenReturn(List.of(announcement));

		List<AnnouncementDTO> result = announcementService.findActive();

		assertThat(result).extracting(AnnouncementDTO::getActive).containsExactly(true);
	}

	@Test
	void findByCourseIdMapsAnnouncements() {
		when(announcementRepository.findByCourseIdOrCourseIdIsNull(30L)).thenReturn(List.of(announcement));

		List<AnnouncementDTO> result = announcementService.findByCourseId(30L);

		assertThat(result).extracting(AnnouncementDTO::getCourseId).containsExactly(30L);
	}

	@Test
	void createPersistsAnnouncement() {
		when(announcementRepository.save(any(Announcement.class))).thenAnswer(invocation -> invocation.getArgument(0));

		AnnouncementDTO result = announcementService.create(request);

		ArgumentCaptor<Announcement> captor = ArgumentCaptor.forClass(Announcement.class);
		verify(announcementRepository).save(captor.capture());
		Announcement saved = captor.getValue();

		assertThat(saved.getTitle()).isEqualTo("Title");
		assertThat(saved.getContent()).isEqualTo("Content");
		assertThat(saved.getCourseId()).isEqualTo(30L);
		assertThat(saved.getSenderId()).isEqualTo(40L);
		assertThat(saved.getPublishedAt()).isNotNull();
		assertThat(saved.getActive()).isTrue();
		assertThat(result.getTitle()).isEqualTo("Title");
	}

	@Test
	void deleteMarksAnnouncementInactive() {
		Announcement activeAnnouncement = new Announcement(1L, "Title", "Content", 30L, 40L, LocalDateTime.of(2026, 5, 15, 11, 0), true);
		when(announcementRepository.findById(1L)).thenReturn(Optional.of(activeAnnouncement));
		when(announcementRepository.save(any(Announcement.class))).thenAnswer(invocation -> invocation.getArgument(0));

		announcementService.delete(1L);

		assertThat(activeAnnouncement.getActive()).isFalse();
		verify(announcementRepository).save(activeAnnouncement);
	}

	@Test
	void deleteThrowsWhenMissing() {
		when(announcementRepository.findById(1L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> announcementService.delete(1L))
				.isInstanceOf(RuntimeException.class)
				.hasMessage("Announcement not found");

		verify(announcementRepository, never()).save(any());
	}
}