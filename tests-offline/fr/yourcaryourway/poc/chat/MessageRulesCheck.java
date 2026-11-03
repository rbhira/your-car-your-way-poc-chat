package fr.yourcaryourway.poc.chat;

public class MessageRulesCheck {
    private static void expectReject(String text) {
        try { MessageRules.normalize(text); throw new AssertionError("Message accepté à tort"); }
        catch (IllegalArgumentException expected) { /* succès */ }
    }
    public static void main(String[] args) {
        if (!"Bonjour".equals(MessageRules.normalize("  Bonjour  "))) throw new AssertionError();
        expectReject(null);
        expectReject(" \t ");
        expectReject("x".repeat(1001));
        if (MessageRules.normalize("x".repeat(1000)).length() != 1000) throw new AssertionError();
        System.out.println("OK : 5 vérifications de la règle de message.");
    }
}
