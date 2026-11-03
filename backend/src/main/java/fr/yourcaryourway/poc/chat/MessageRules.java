package fr.yourcaryourway.poc.chat;

/** Règles métier réutilisables sans dépendance à Spring. */
public final class MessageRules {
    public static final int MAX_LENGTH = 1000;

    private MessageRules() { }

    public static String normalize(String raw) {
        if (raw == null || raw.isBlank()) {
            throw new IllegalArgumentException("Le message ne peut pas être vide.");
        }
        String clean = raw.strip();
        if (clean.codePointCount(0, clean.length()) > MAX_LENGTH) {
            throw new IllegalArgumentException("Le message dépasse 1 000 caractères.");
        }
        return clean;
    }
}
