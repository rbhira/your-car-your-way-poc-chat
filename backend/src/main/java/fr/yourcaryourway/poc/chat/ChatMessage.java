package fr.yourcaryourway.poc.chat;

import java.time.Instant;

public record ChatMessage(long id, long conversationId, String sender,
                          String content, Instant createdAt) { }
