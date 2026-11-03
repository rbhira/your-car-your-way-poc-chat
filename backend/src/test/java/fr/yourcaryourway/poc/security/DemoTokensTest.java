package fr.yourcaryourway.poc.security;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class DemoTokensTest {
    private final DemoTokens tokens = new DemoTokens("jeton-client-uniquement", "jeton-conseiller-uniquement");

    @Test void clientCorrectlyAuthenticated() {
        assertEquals("client", tokens.actorForRequestedRole("Bearer jeton-client-uniquement", "client"));
    }
    @Test void agentCorrectlyAuthenticated() {
        assertEquals("conseiller", tokens.actorForRequestedRole("Bearer jeton-conseiller-uniquement", "conseiller"));
    }
    @Test void clientTokenCannotClaimAgentRole() {
        assertThrows(SecurityException.class, () ->
                tokens.actorForRequestedRole("Bearer jeton-client-uniquement", "conseiller"));
    }
    @Test void agentTokenCannotClaimClientRole() {
        assertThrows(SecurityException.class, () ->
                tokens.actorForRequestedRole("Bearer jeton-conseiller-uniquement", "client"));
    }
    @Test void databasePasswordIsNotAChatToken() {
        assertThrows(SecurityException.class, () ->
                tokens.actorForRequestedRole("Bearer mot-de-passe-bdd", "client"));
    }
    @Test void roleHeaderIsMandatory() {
        assertThrows(SecurityException.class, () ->
                tokens.actorForRequestedRole("Bearer jeton-client-uniquement", null));
    }
}
