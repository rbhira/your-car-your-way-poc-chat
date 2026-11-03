package fr.yourcaryourway.poc.chat;

import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ChatService {
    private final ChatRepository repository;

    public ChatService(ChatRepository repository) { this.repository = repository; }

    public ChatMessage send(long roomId, String actor, String rawContent) {
        requireMember(roomId, actor);
        return repository.save(roomId, actor, MessageRules.normalize(rawContent));
    }

    public List<ChatMessage> history(long roomId, String actor) {
        requireMember(roomId, actor);
        return repository.history(roomId);
    }

    public void requireMember(long roomId, String actor) {
        if (roomId <= 0 || actor == null || !repository.isParticipant(roomId, actor)) {
            throw new SecurityException("Accès à la conversation refusé.");
        }
    }
}
