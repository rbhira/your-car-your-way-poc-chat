package fr.yourcaryourway.poc.chat;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ChatRepository {
    private final JdbcTemplate jdbc;

    public ChatRepository(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    public boolean isParticipant(long conversationId, String actor) {
        Integer count = jdbc.queryForObject(
            "SELECT COUNT(*) FROM conversation_member WHERE conversation_id = ? AND actor_code = ?",
            Integer.class, conversationId, actor);
        return count != null && count > 0;
    }

    public ChatMessage save(long conversationId, String sender, String text) {
        return jdbc.queryForObject("""
            INSERT INTO chat_message (conversation_id, sender, content)
            VALUES (?, ?, ?) RETURNING id, conversation_id, sender, content, created_at
            """, ChatRepository::readMessage, conversationId, sender, text);
    }

    public List<ChatMessage> history(long conversationId) {
        return jdbc.query("""
            SELECT id, conversation_id, sender, content, created_at
            FROM (
              SELECT id, conversation_id, sender, content, created_at
              FROM chat_message WHERE conversation_id = ?
              ORDER BY id DESC LIMIT 100
            ) recent
            ORDER BY id ASC
            """, ChatRepository::readMessage, conversationId);
    }

    private static ChatMessage readMessage(ResultSet rs, int row) throws SQLException {
        return new ChatMessage(rs.getLong("id"), rs.getLong("conversation_id"),
            rs.getString("sender"), rs.getString("content"),
            rs.getTimestamp("created_at").toInstant());
    }
}
