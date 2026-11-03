# Décisions et limites de la PoC

## Deux solutions de communication étudiées

L'interrogation HTTP périodique consiste à demander régulièrement au serveur si de nouveaux messages existent. Cette méthode est facile à concevoir et à tester, mais elle multiplie les requêtes et peut retarder l'affichage. WebSocket maintient une connexion bidirectionnelle permettant de diffuser les messages immédiatement. Nous retenons WebSocket, car cette approche démontre un choix d'architecture utile dans une application de tchat.

## Périmètre du prototype

Une conversation est créée pour deux acteurs fictifs. La base PostgreSQL conserve les messages. Le backend est structuré en contrôleur, service métier et couche d'accès aux données. Angular représente la partie cliente. Les autres domaines de location (véhicules, offres, réservations, paiement) restent hors de cette PoC.

## Décisions en attente pour une application complète

La définition des rôles métiers, l'authentification de production, l'attribution des conseillers, la politique de conservation des messages, la déconnexion et la montée en charge nécessitent une étude complémentaire. Aucun résultat de performance ou de sécurité en production n'est revendiqué dans ce prototype.

## Sources techniques consultées

Documentation officielle Spring WebSocket / STOMP : https://docs.spring.io/spring-framework/reference/web/websocket/stomp/enable.html

Spring Boot 4.1.2 : https://spring.io/blog/2026/10/23/spring-boot-4-1-2-available-now/

Compatibilité Angular : https://angular.dev/reference/versions
