package fr.yourcaryourway.poc.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/** Jetons de démonstration fournis par variables d'environnement. Pas une authentification de production. */
@Component
public class DemoTokens {
    private final String clientToken;
    private final String agentToken;

    public DemoTokens(@Value("${poc.client-token}") String clientToken,
                      @Value("${poc.agent-token}") String agentToken) {
        if (clientToken.isBlank() || agentToken.isBlank() || clientToken.equals(agentToken)) {
            throw new IllegalArgumentException("Deux jetons de démonstration distincts sont requis.");
        }
        this.clientToken = clientToken;
        this.agentToken = agentToken;
    }

    public String actorFromBearer(String bearer) {
        if (bearer == null || !bearer.startsWith("Bearer ")) {
            throw new SecurityException("Jeton absent.");
        }
        byte[] actual = bearer.substring(7).getBytes(StandardCharsets.UTF_8);
        if (constantTimeMatches(actual, clientToken)) return "client";
        if (constantTimeMatches(actual, agentToken)) return "conseiller";
        throw new SecurityException("Jeton invalide.");
    }

    /**
     * Refuse une identité différente de celle annoncée par le formulaire.
     * Le rôle réel est déterminé par le jeton côté serveur, jamais par Angular.
     */
    public String actorForRequestedRole(String bearer, String requestedRole) {
        String actualRole = actorFromBearer(bearer);
        if (!actualRole.equals(requestedRole)) {
            throw new SecurityException("Le jeton ne correspond pas au profil sélectionné.");
        }
        return actualRole;
    }

    private boolean constantTimeMatches(byte[] provided, String expected) {
        return MessageDigest.isEqual(provided, expected.getBytes(StandardCharsets.UTF_8));
    }
}
