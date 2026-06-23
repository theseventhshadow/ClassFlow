package com.ohiggins.classflow.message.repository;

import com.ohiggins.classflow.message.entity.Message;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@TestPropertySource(locations = "classpath:application-test.properties")
class MessageRepositoryTest {

	@Autowired
	private MessageRepository messageRepository;

	@Test
	void saveAndFindByReceiverId() {
		Message message = new Message(null, 10L, 20L, "Subject", "Body", false, LocalDateTime.now());
		messageRepository.save(message);

		assertThat(messageRepository.findByReceiverId(20L)).hasSize(1);
	}

	@Test
	void findBySenderIdReturnsMessages() {
		messageRepository.save(new Message(null, 10L, 20L, "Subject", "Body", false, LocalDateTime.now()));
		messageRepository.save(new Message(null, 11L, 21L, "Subject 2", "Body 2", true, LocalDateTime.now()));

		assertThat(messageRepository.findBySenderId(10L)).hasSize(1);
	}

	@Test
	void findUnreadByReceiverIdReturnsOnlyUnreadMessages() {
		messageRepository.save(new Message(null, 10L, 20L, "Subject", "Body", false, LocalDateTime.now()));
		messageRepository.save(new Message(null, 10L, 20L, "Subject 2", "Body 2", true, LocalDateTime.now()));

		assertThat(messageRepository.findByReceiverIdAndReadFalse(20L)).hasSize(1);
	}
}