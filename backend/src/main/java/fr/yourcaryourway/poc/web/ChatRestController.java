package fr.yourcaryourway.poc.web;

import fr.yourcaryourway.poc.chat.ChatMessage;
import fr.yourcaryourway.poc.chat.ChatService;
import fr.yourcaryourway.poc.security.DemoTokens;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/conversations")
public class ChatRestController {
    private final ChatService chat;
    private final DemoTokens tokens;

    public ChatRestController(ChatService chat, DemoTokens tokens) {
        this.chat = chat;
        this.tokens = tokens;
    }

    @GetMapping("/{roomId}/messages")
    public List<ChatMessage> history(@PathVariable long roomId,
                                      @RequestHeader(value="Authorization", required=false) String bearer,
                                      @RequestHeader(value="X-Demo-Role", required=false) String requestedRole) {
        return chat.history(roomId, tokens.actorForRequestedRole(bearer, requestedRole));
    }

    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<String> forbidden(SecurityException ex) {
        return ResponseEntity.status(403).body(ex.getMessage());
    }
}
