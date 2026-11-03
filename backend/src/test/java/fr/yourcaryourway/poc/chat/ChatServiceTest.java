package fr.yourcaryourway.poc.chat;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import java.time.Instant;
import org.junit.jupiter.api.Test;

class ChatServiceTest {
    @Test void shouldRejectBlankMessage() {
        ChatRepository repository = mock(ChatRepository.class);
        when(repository.isParticipant(1, "client")).thenReturn(true);
        ChatService service = new ChatService(repository);
        assertThrows(IllegalArgumentException.class, () -> service.send(1, "client", "  "));
        verify(repository, never()).save(anyLong(), anyString(), anyString());
    }

    @Test void shouldRejectNonMember() {
        ChatRepository repository = mock(ChatRepository.class);
        ChatService service = new ChatService(repository);
        assertThrows(SecurityException.class, () -> service.send(2, "client", "Bonjour"));
        verify(repository, never()).save(anyLong(), anyString(), anyString());
    }

    @Test void shouldStoreTrimmedMessage() {
        ChatRepository repository = mock(ChatRepository.class);
        when(repository.isParticipant(1, "client")).thenReturn(true);
        when(repository.save(1, "client", "Bonjour"))
            .thenReturn(new ChatMessage(3, 1, "client", "Bonjour", Instant.EPOCH));
        ChatMessage message = new ChatService(repository).send(1, "client", " Bonjour ");
        assertEquals("Bonjour", message.content());
        verify(repository).save(1, "client", "Bonjour");
    }
}
