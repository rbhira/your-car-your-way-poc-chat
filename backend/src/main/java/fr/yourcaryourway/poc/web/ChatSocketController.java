package fr.yourcaryourway.poc.web;

import fr.yourcaryourway.poc.chat.*;
import java.security.Principal;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class ChatSocketController {
    private final ChatService chat;
    private final SimpMessagingTemplate broker;

    public ChatSocketController(ChatService chat, SimpMessagingTemplate broker) {
        this.chat = chat;
        this.broker = broker;
    }

    @MessageMapping("/conversations/{roomId}/send")
    public void send(@DestinationVariable long roomId, @Payload IncomingMessage input,
                     Principal principal) {
        if (principal == null || input == null) throw new SecurityException("Identité ou message absent.");
        ChatMessage saved = chat.send(roomId, principal.getName(), input.content());
        broker.convertAndSend("/topic/conversations/" + roomId, saved);
    }
}
