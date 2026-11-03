package fr.yourcaryourway.poc.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocketMessageBroker
public class ChatWebsocketConfiguration implements WebSocketMessageBrokerConfigurer {
    private final ChatSocketGuard guard;
    private final String origin;

    public ChatWebsocketConfiguration(ChatSocketGuard guard,
                                      @Value("${app.allowed-origin}") String origin) {
        this.guard = guard;
        this.origin = origin;
    }

    @Override public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws").setAllowedOrigins(origin);
    }

    @Override public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.setApplicationDestinationPrefixes("/app");
        registry.enableSimpleBroker("/topic");
    }

    @Override public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(guard);
    }
}
