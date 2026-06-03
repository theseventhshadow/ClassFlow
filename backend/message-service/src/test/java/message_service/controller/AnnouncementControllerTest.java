package message_service.controller;

import message_service.dto.AnnouncementDTO;
import message_service.service.AnnouncementService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AnnouncementController.class)
@TestPropertySource(locations = "classpath:application-test.properties")
class AnnouncementControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private AnnouncementService announcementService;

	@Test
	void getAllReturnsAnnouncements() throws Exception {
		when(announcementService.findAll()).thenReturn(java.util.List.of(sampleAnnouncement()));

		mockMvc.perform(get("/api/announcements"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].title").value("Title"));
	}

	@Test
	void createReturnsCreatedAnnouncement() throws Exception {
		when(announcementService.create(any())).thenReturn(sampleAnnouncement());

		mockMvc.perform(post("/api/announcements")
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.content("""
					{
					  "title": "Title",
					  "content": "Content",
					  "courseId": 30,
					  "senderId": 40
					}
					"""))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.courseId").value(30));
	}

	@Test
	void deleteReturnsNoContent() throws Exception {
		doNothing().when(announcementService).delete(1L);

		mockMvc.perform(delete("/api/announcements/1"))
				.andExpect(status().isNoContent());
	}

	private AnnouncementDTO sampleAnnouncement() {
		return AnnouncementDTO.builder()
				.id(1L)
				.title("Title")
				.content("Content")
				.courseId(30L)
				.senderId(40L)
				.publishedAt(LocalDateTime.of(2026, 5, 15, 11, 0))
				.active(true)
				.build();
	}
}