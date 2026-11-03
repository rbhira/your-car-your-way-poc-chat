package fr.yourcaryourway.poc.web;

import fr.yourcaryourway.poc.chat.ChatService;
import fr.yourcaryourway.poc.security.DemoTokens;
import java.security.Principal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

/** Filtre sur CONNECT, SUBSCRIBE et SEND. Les simples topics ne sont pas publics. */
@Component
public class ChatSocketGuard implements ChannelInterceptor {
    private static final Pattern SEND = Pattern.compile("^/app/conversations/([0-9]+)/send$");
    private static final Pattern SUBSCRIBE = Pattern.compile("^/topic/conversations/([0-9]+)$");
    private final DemoTokens tokens;
    private final ChatService chat;

    public ChatSocketGuard(DemoTokens tokens, ChatService chat) {
        this.tokens = tokens;
        this.chat = chat;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor access = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (access == null || access.getCommand() == null) return message;
        StompCommand command = access.getCommand();
        if (command == StompCommand.CONNECT) {
            String bearer = access.getFirstNativeHeader("Authorization");
            String requestedRole = access.getFirstNativeHeader("X-Demo-Role");
            String actor = tokens.actorForRequestedRole(bearer, requestedRole);
            access.setUser((Principal) () -> actor);
        } else if (command == StompCommand.SEND || command == StompCommand.SUBSCRIBE) {
            Principal principal = access.getUser();
            if (principal == null) throw new SecurityException("Connexion sans identité.");
            Matcher matcher = (command == StompCommand.SEND ? SEND : SUBSCRIBE)
                    .matcher(access.getDestination() == null ? "" : access.getDestination());
            if (!matcher.matches()) throw new SecurityException("Destination non autorisée.");
            chat.requireMember(Long.parseLong(matcher.group(1)), principal.getName());
        }
        return message;
    }
}
