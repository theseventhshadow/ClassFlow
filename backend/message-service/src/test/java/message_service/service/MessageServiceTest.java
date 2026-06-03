package message_service.service;

import message_service.dto.MessageDTO;
import message_service.dto.MessageRequestDTO;
import message_service.entity.Message;
import message_service.repository.MessageRepository;
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
class MessageServiceTest {

	@Mock
	private MessageRepository messageRepository;

	@InjectMocks
	private MessageService messageService;

	private Message message;
	private MessageRequestDTO request;

	@BeforeEach
	void setUp() {
		message = new Message(1L, 10L, 20L, "Subject", "Body", false, LocalDateTime.of(2026, 5, 15, 10, 0));
		request = new MessageRequestDTO();
		request.setSenderId(10L);
		request.setReceiverId(20L);
		request.setSubject("Subject");
		request.setBody("Body");
	}

	@Test
	void findAllMapsMessages() {
		when(messageRepository.findAll()).thenReturn(List.of(message));

		List<MessageDTO> result = messageService.findAll();

		assertThat(result).hasSize(1);
		assertThat(result.get(0).getId()).isEqualTo(1L);
		assertThat(result.get(0).getSenderId()).isEqualTo(10L);
		assertThat(result.get(0).getRead()).isFalse();
	}

	@Test
	void findByReceiverIdMapsMessages() {
		when(messageRepository.findByReceiverId(20L)).thenReturn(List.of(message));

		List<MessageDTO> result = messageService.findByReceiverId(20L);

		assertThat(result).extracting(MessageDTO::getReceiverId).containsExactly(20L);
	}

	@Test
	void findBySenderIdMapsMessages() {
		when(messageRepository.findBySenderId(10L)).thenReturn(List.of(message));

		List<MessageDTO> result = messageService.findBySenderId(10L);

		assertThat(result).extracting(MessageDTO::getSenderId).containsExactly(10L);
	}

	@Test
	void findUnreadByReceiverIdMapsMessages() {
		when(messageRepository.findByReceiverIdAndReadFalse(20L)).thenReturn(List.of(message));

		List<MessageDTO> result = messageService.findUnreadByReceiverId(20L);

		assertThat(result).extracting(MessageDTO::getRead).containsExactly(false);
	}

	@Test
	void sendPersistsMessage() {
		when(messageRepository.save(any(Message.class))).thenAnswer(invocation -> invocation.getArgument(0));

		MessageDTO result = messageService.send(request);

		ArgumentCaptor<Message> captor = ArgumentCaptor.forClass(Message.class);
		verify(messageRepository).save(captor.capture());
		Message saved = captor.getValue();

		assertThat(saved.getSenderId()).isEqualTo(10L);
		assertThat(saved.getReceiverId()).isEqualTo(20L);
		assertThat(saved.getSubject()).isEqualTo("Subject");
		assertThat(saved.getBody()).isEqualTo("Body");
		assertThat(saved.getRead()).isFalse();
		assertThat(saved.getSentAt()).isNotNull();
		assertThat(result.getSubject()).isEqualTo("Subject");
	}

	@Test
	void markAsReadUpdatesMessage() {
		Message unread = new Message(1L, 10L, 20L, "Subject", "Body", false, LocalDateTime.of(2026, 5, 15, 10, 0));
		when(messageRepository.findById(1L)).thenReturn(Optional.of(unread));
		when(messageRepository.save(any(Message.class))).thenAnswer(invocation -> invocation.getArgument(0));

		MessageDTO result = messageService.markAsRead(1L);

		assertThat(result.getRead()).isTrue();
		verify(messageRepository).save(unread);
	}

	@Test
	void markAsReadThrowsWhenMissing() {
		when(messageRepository.findById(1L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> messageService.markAsRead(1L))
				.isInstanceOf(RuntimeException.class)
				.hasMessage("Message not found");

		verify(messageRepository, never()).save(any());
	}

	@Test
	void deleteDelegatesToRepository() {
		messageService.delete(1L);

		verify(messageRepository).deleteById(1L);
	}
}