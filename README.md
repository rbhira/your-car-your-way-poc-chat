# Your Car Your Way : PoC du tchat

## 1. Objectif

Cette preuve de concept est le **livrable de l'étape 4 du Projet 7, Mission Option B**. Elle vérifie un seul usage : un client et un conseiller peuvent échanger des messages dans une conversation. Le projet de développement complet de l'application de location reste séparé.

L'application utilise Angular, Spring Boot, Java 21, WebSocket avec le protocole STOMP et PostgreSQL. Elle reprend donc les technologies et les principes annoncés dans notre proposition d'architecture.

## 2. Fonctionnalités prévues

Un client et un conseiller peuvent ouvrir la conversation n° 1. Le serveur conserve chaque message dans PostgreSQL avant de le diffuser aux personnes connectées. L'historique des 100 derniers messages apparaît après la connexion. La longueur des messages est limitée à 1 000 caractères. Un contrôle d'accès refuse les jetons inconnus, les combinaisons de profil et de jeton incorrectes, et les conversations auxquelles l'acteur n'appartient pas.

La PoC ne propose ni réservation, ni paiement, ni intelligence artificielle. Les deux acteurs sont fictifs. Les jetons simples sont uniquement destinés à une démonstration locale. **Cette version ne doit pas être exposée sur Internet.**

### Navigation et connexion dans la version 0.2

La connexion fonctionne en cliquant sur **Se connecter** ou en appuyant sur **Entrée** depuis le champ du jeton. Après une actualisation de la page (F5), le client et le conseiller doivent se reconnecter : il s'agit d'un choix de cette démonstration locale, où les jetons ne sont pas sauvegardés dans le navigateur. Les messages, eux, sont conservés en base et sont relus après reconnexion. L'authentification persistante n'est pas dans le périmètre de cette PoC.

**Mise à jour depuis la version 0.1 :** si le dossier de la PoC est déjà installé et que les trois services tournent, remplacer uniquement `frontend/src/app/app.component.html` par la version corrigée. Ensuite, dans CMD ouvert à la racine du projet, lancer `docker compose up -d --build web`. Cette commande reconstruit l'interface sans supprimer le volume PostgreSQL. Ne pas remplacer `.env` et ne pas exécuter `docker compose down -v`.

### Correctif 0.3 : profil et jeton doivent correspondre

Le profil Client doit être sélectionné avec le jeton POC_CLIENT_TOKEN, et le profil Conseiller avec POC_AGENT_TOKEN. Le backend vérifie désormais cette association lors de la connexion WebSocket/STOMP et lors de la lecture de l'historique par API REST. Le jeton du conseiller ne doit plus fonctionner avec le profil Client, ni l'inverse. Le mot de passe DB_PASSWORD ne donne aucun accès au tchat.

Pour corriger une installation existante, utiliser l'archive de correctif ciblé. Depuis CMD ouvert à la racine du projet, extraire l'archive dans ce dossier (en conservant les sous-dossiers backend et frontend), puis exécuter : `docker compose up -d --build api web`. La base PostgreSQL, son volume et le fichier `.env` ne doivent pas être supprimés. **Ne pas utiliser `docker compose down -v`.**

Tests manuels de régression : Client + jeton client = accepté ; Conseiller + jeton conseiller = accepté ; Client + jeton conseiller = refusé ; Conseiller + jeton client = refusé. Vérifier que les deux connexions correctes peuvent toujours échanger des messages, que l'historique réapparaît après reconnexion et que la touche Entrée continue de fonctionner.

## 3. Démarrer avec Docker Desktop

Prérequis : Docker Desktop avec Docker Compose et une connexion Internet au premier lancement pour télécharger les images et dépendances.

1. Décompresser l'archive dans un dossier indépendant du projet personnel.
2. Copier `.env.example` vers `.env`. Sous Windows CMD : `copy .env.example .env`.
3. Ouvrir `.env`, conserver deux jetons différents et choisir un mot de passe PostgreSQL local.
4. À la racine de ce dossier, lancer `docker compose up --build`.
5. Ouvrir `http://localhost:4200` dans un navigateur, puis dans un second navigateur ou une fenêtre privée.
6. Sur le premier, sélectionner **Client**, puis saisir la valeur `POC_CLIENT_TOKEN` du fichier `.env`.
7. Sur le second, sélectionner **Conseiller**, puis saisir la valeur `POC_AGENT_TOKEN`.
8. Envoyer un message depuis le premier navigateur. Vérifier sa réception sur le second et répondre.
9. Rafraîchir les deux pages, se reconnecter et vérifier que les messages sont toujours présents.

Si un port est déjà utilisé (4200, 8080 ou 5433), modifier sa correspondance dans `docker-compose.yml` et adapter la configuration de l'interface si nécessaire.

Arrêter avec `Ctrl+C`, puis `docker compose down`. Ne pas utiliser `docker compose down -v` si tu souhaites conserver les messages enregistrés.

## 4. Travailler avec IntelliJ et un terminal Angular

Pour développer sans reconstruire les images à chaque modification, démarrer uniquement la base : `docker compose up -d db`. Dans IntelliJ IDEA, ouvrir `backend/pom.xml` en tant que projet Maven. Définir les variables `SPRING_DATASOURCE_PASSWORD`, `POC_CLIENT_TOKEN`, `POC_AGENT_TOKEN`, puis lancer `ChatPocApplication`. Les valeurs sont dans `.env`. Le port PostgreSQL local est 5433.

Dans un terminal placé dans `frontend`, exécuter `npm install`, puis `npm start`. Ouvrir ensuite `http://localhost:4200`.

## 5. Critères de démonstration

| Identifiant | Manipulation | Résultat attendu |
|---|---|---|
| T01 | Deux sessions avec jetons différents | Les deux sessions se connectent au même salon. |
| T02 | Message « Bonjour » envoyé par le client | Le conseiller voit le message sans recharger. |
| T03 | Réponse du conseiller | Le client voit la réponse sans recharger. |
| T04 | Rafraîchissement et reconnexion | Les messages récents réapparaissent. |
| T05 | Mauvais jeton | L'authentification STOMP est refusée. |
| T06 | Requête REST sans jeton | Le serveur renvoie HTTP 403. |
| T07 | Tentative sur la conversation 2 | Le serveur refuse l'accès. |
| T08 | Message vide ou de plus de 1 000 caractères | Le message n'est pas sauvegardé. |
| T09 | Profil Client avec jeton Conseiller ou l’inverse | Le serveur refuse la connexion. |

Les résultats de la démonstration locale du 2 novembre 2026 sont consignés ci-dessous et dans docs/ETAT_DES_VERIFICATIONS.md. Les essais non exécutés restent à effectuer avant toute conclusion générale sur la sécurité.

### Contrôles réellement exécutés le 2 novembre 2026

Le porteur du projet a testé l’interface dans Chrome, en utilisant Docker Desktop sur son poste Windows. Les connexions client et conseiller ont réussi. La connexion par la touche Entrée a fonctionné après correction. Les messages ont été échangés en temps réel. Après actualisation, reconnexion et réouverture de la conversation, l’historique a réapparu. Les deux combinaisons inversées de profil et de jeton ont été refusées avec un message explicite. Enfin, une commande `psql` exécutée via `docker compose exec` a retrouvé dans la table `chat_message` le message envoyé par le client.

**Périmètre de validation :** T01 et T02 sont observés ; T04 et T09 sont observés. La réponse émise depuis le conseiller (T03), la requête REST sans jeton (T06), l'accès à une conversation non autorisée (T07) et les limites de taille ou messages vides (T08) ne disposent pas ici d’une preuve d’exécution consignée. Pour T05, le refus des jetons inversés est observé, mais ce constat ne remplace pas tous les tests possibles avec des jetons inconnus.

L’existence de ces résultats ne prouve ni une résistance aux attaques ni une performance adaptée à une mise en production. Les jetons sont destinés à la démonstration locale uniquement. Ne pas mettre cette PoC telle quelle sur un serveur public.

## 6. Architecture et données

```text
Client Angular (4200)
   | GET /api/conversations/1/messages (Authorization: Bearer + X-Demo-Role)
   | WebSocket /ws + STOMP CONNECT Authorization + X-Demo-Role
   v
Spring Boot (8080)
   | ChatSocketGuard -> ChatService -> ChatRepository
   v
PostgreSQL (5432 dans Docker, 5433 sur le PC)
   | conversation
   | conversation_member
   | chat_message
```

Les tables sont créées par `backend/src/main/resources/schema.sql`. L'intégrité de `chat_message` garantit que l'auteur appartient à la conversation. L'index `(conversation_id, id)` aide à récupérer l'historique. La PoC contient une seule conversation, identifiée par le nombre 1.

La diffusion utilise le broker simple de Spring. Ce broker ne constitue pas une solution de messagerie distribuée en production. Une application réelle demanderait notamment une authentification complète, une stratégie de gestion des secrets, des tests de charge, une surveillance, une gestion des erreurs plus aboutie et une solution d'exploitation adaptée.

## 7. Tests disponibles

Les tests JUnit se trouvent dans `backend/src/test/java`. Ils s'exécutent avec `mvn test` et nécessitent Maven ainsi que les dépendances du projet. Le script `scripts/test-regles-sans-dependances.sh` vérifie quelques règles de messages avec Java 21 et n'utilise pas de bibliothèque externe.

## 8. Limites de ce livrable

Ce dépôt est une PoC et non un produit final. Les jetons de démonstration permettent d'identifier deux rôles fictifs, mais ne remplacent pas une véritable connexion utilisateur. Une seule conversation est créée et le système ne gère pas les pièces jointes, les notifications, la disponibilité des conseillers ou la conservation légale de données personnelles.

Le code est prévu pour être déposé dans un dépôt GitHub **distinct** de celui de notre future application complète. Il n'est pas encore publié sur GitHub par la seule création de cette archive.
