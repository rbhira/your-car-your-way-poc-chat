-- Un seul salon et deux acteurs fictifs. Cette PoC n'effectue aucune migration de données réelles.
CREATE TABLE IF NOT EXISTS conversation (
  id BIGINT PRIMARY KEY,
  subject VARCHAR(150) NOT NULL,
  created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS conversation_member (
  conversation_id BIGINT NOT NULL REFERENCES conversation(id),
  actor_code VARCHAR(30) NOT NULL,
  PRIMARY KEY (conversation_id, actor_code)
);

CREATE TABLE IF NOT EXISTS chat_message (
  id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  conversation_id BIGINT NOT NULL REFERENCES conversation(id),
  sender VARCHAR(30) NOT NULL,
  content VARCHAR(1000) NOT NULL CHECK (char_length(trim(content)) BETWEEN 1 AND 1000),
  created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (conversation_id, sender)
    REFERENCES conversation_member(conversation_id, actor_code)
);

CREATE INDEX IF NOT EXISTS idx_chat_message_conversation_id
  ON chat_message(conversation_id, id);

INSERT INTO conversation(id, subject) VALUES (1, 'Question sur une location')
ON CONFLICT(id) DO NOTHING;
INSERT INTO conversation_member(conversation_id, actor_code) VALUES (1, 'client')
ON CONFLICT(conversation_id, actor_code) DO NOTHING;
INSERT INTO conversation_member(conversation_id, actor_code) VALUES (1, 'conseiller')
ON CONFLICT(conversation_id, actor_code) DO NOTHING;
