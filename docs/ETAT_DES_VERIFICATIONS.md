# État de vérification de la PoC du tchat

## Objectif de la démonstration

La PoC du Projet 7, Mission Option B, doit démontrer une fonctionnalité de messagerie entre un client et un conseiller. Elle utilise Angular, Spring Boot, WebSocket/STOMP et PostgreSQL. Le développement de l'application complète de location de voitures reste distinct de ce livrable.

## Résultats observés sur le poste Windows le 2 novembre 2026

| Contrôle | Résultat observé | Source de la preuve |
|---|---|---|
| Affichage de l'interface sur localhost:4200 | Réussi | Capture de l'interface dans Chrome |
| Connexion avec jeton client | Réussi | Essai manuel rapporté |
| Connexion avec jeton conseiller | Réussi | Essai manuel rapporté |
| Envoi et réception de message en temps réel | Réussi | Essai manuel rapporté |
| Historique après actualisation et reconnexion | Réussi | Essai manuel rapporté |
| Connexion avec la touche Entrée | Réussi après correction | Essai manuel rapporté |
| Client sélectionné avec jeton conseiller | Refus attendu observé | Essai manuel rapporté |
| Conseiller sélectionné avec jeton client | Refus attendu observé | Essai manuel rapporté |
| Stockage PostgreSQL | Message du client retrouvé dans `chat_message` | Résultat rapporté de la commande `psql` |

Exemple de message contrôlé : « Bonjour, je souhaite obtenir des renseignements sur une location. » Cette observation montre que le message du client est conservé en base. Elle ne signifie pas que tous les comportements du tchat ont été testés.

## Limites et vérifications complémentaires

Le code comporte des tests automatisés de règles métier et de correspondance des rôles, mais cette note ne prétend pas que l'ensemble de la suite Maven ou Angular a été exécuté sur le poste Windows. Les vérifications détaillées de l'API REST sans jeton, des conversations non autorisées, des messages vides ou trop longs, de la réponse du conseiller et de la résistance en charge ne sont pas confirmées par des comptes rendus d'exécution.

Les mots de passe et jetons choisis pour la démonstration sont enregistrés dans `.env`. Ce fichier est exclu par `.gitignore` et ne doit jamais être publié. La PoC n'a pas été auditée pour une mise en production publique.

## Commande reproductible pour retrouver les messages

Depuis CMD dans le dossier contenant `docker-compose.yml` :

```bat
docker compose exec -T db psql -U ycyw_poc -d ycyw_poc -c "SELECT id, sender, content, created_at FROM chat_message ORDER BY id DESC LIMIT 10;"
```

Cette commande ne modifie pas les données. Pour la soutenance, conserver de préférence une capture anonymisée du résultat et une capture des deux sessions de tchat.
