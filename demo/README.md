🟧. Introduction

Bonjour, je suis Jean-Noël Bilong, senior développeur full-stack Java/Angular,
avec un fort volet DevOps et Agile. Aujourd’hui, je vous présente ma démo technique du projet Cargo-TMS,
réalisée à partir du cahier des charges Acteos pour le test full-stack.

L’objectif de ce projet est de gérer les mouvements de marchandises entrées et sorties 
avec :

- la validation des règles métiers et techniques,

- l’envoi automatique d’un fichier XML par e-mail,

- la visualisation des 50 derniers mouvements dans une UI Angular,

- le tout incluant une gestion des stocks simplifiée.

J’ai choisi pour se faire une approche API-first, pour garantir une cohérence parfaite
entre le backend Spring Boot et le frontend Angular.

Voici la structure du projet sur GitHub.

Le repos principal s’appelle cargo-tms.

Il contient plusieurs modules :

- contracts-api : le contrat OpenAPI qui décrit toute l’API REST,
exposé par le backend et consommé par le frontend ;

- tms-service : le backend Spring Boot 3.5.6, développé en Java 21 ;

- tms-db : la gestion de la base de données avec Liquibase ;

- tms-sb-admin : un module Spring Boot Admin pour la supervision ;

- frontend : l’application Angular 20 ;

- le module infra : le docker-compose qui orchestre PostgreSQL, MailHog et Spring Boot Admin.

- et enfin la colletion postmant pour tester l'api

Pour la Stack technique
MapStruct, Lombok, Liquibase, JUnit 5 et Mailjet/MailHog pour la messagerie,
ainsi que Faker pour générer des jeux de données propres et reproductibles,
complètent la stack technique du projet.

Déroulé de la démo est séquencéee par

Démarrage : initialisation des composants applicatifs via docker-compose
(BDD, MailHog, SB-Admin, backend, frontend).

Backend : démonstration via postman (contrat, endpoints, flux de création).

Frontend : démonstration de l’UI et du parcours “mouvements & stocks”.

🟧 2. Démarrage (oral + commandes)

1) Lancer l’infra (détaché)

“Je lance l’infra applicative en détaché.”
docker compose -f infra/docker-compose.yml up -d

“Je vérifie que tout tourne.”
docker ps

2) Vérifier les composants web

“Je contrôle visuellement :
– MailHog (boîte mail de test),
– Spring Boot Admin (supervision),
– la vue PostgreSQL (pgAdmin ou client SQL).”

3) Backend en session dédiée

“Je crée une session dédiée pour le backend.”
screen -S backend_session

“Je lance le backend.”
make run-backend

“Je détache la session.”
(Ctrl + A, puis D)

4) Contrôles rapides backend

“Je vérifie la BDD (migrations Liquibase OK).”
(connexion rapide, liste des schémas/tables)

“J’ouvre Swagger/OpenAPI pour confirmer les endpoints exposés.”
(page Swagger du backend)

5) Frontend

“Je démarre le front.”
cd frontend && pnpm start

“Je vais sur l’UI et je vérifie les écrans :
– Historique (50 derniers mouvements),
– Entrées,
– Sorties.”

6) Feu vert démo

“Tout est vert (containers, Swagger, BDD, UI). 
On peut enchaîner sur la démo backend dans Postman puis la démo frontend.”

🟧 3. Démonstration backend

Dans Postman, on voit l’ensemble des endpoints.

1) Visualiser l’historique (à vide)

On commence simplement : j’affiche les 50 derniers mouvements → liste vide, normal.

2) Charger des données fictives

Je charge les données de test (Faker).

Je réaffiche les 50 derniers mouvements pour vérifier que l’historique se remplit bien.

On jette aussi un œil à la BDD pour confirmer.

3) Création de mouvements — focus erreurs (IN non créé)

Gestion des erreurs de validation :

Code AWB : je saisis un AWB non valide → rejet attendu.

Quantités incohérentes : je simule des incohérences
(quantity > totalQuantity et weight > totalWeight) → refus côté backend.

Entrepôt inexistant : je tente un IN sur un entrepôt inexistant → rejet (règle métier).

Champs requis (IN) : démonstration d’un manque de champ obligatoire (ex. fromWarehouse pour un IN) → rejet avec message clair.

4) Mouvement OUT — règles métier & validations techniques

Code AWB : même logique, je renseigne un AWB.

Pas de sortie sans stock : je tente un OUT sans stock disponible → rejet (règle métier).

Validations techniques :

pour un OUT : présence obligatoire des documents de douane ;

pour un IN : absence de certaines données spécifiques (le backend refuse si elles sont indûment fournies).

5) Cas passants (flux nominal)

Création d’un IN ✅

Contrôle de stock pour la référence 07712345678 → incrémentation attendue.

Création d’un OUT ✅

Contrôle de stock pour la même référence → décrémentation attendue.

E-mail : vérification du message dans MailHog avec la pièce jointe XML → OK.

6) Cas passants en charge / cohérence de stock

Deux IN successifs → incrémentation ×2.

Contrôle de stock (réf. 07712345678) → stock au niveau attendu (“full” pour la démo).

Trois OUT de suite → on provoque un OUT au-delà du disponible → erreur stock (protection OK).

Contrôle final → stock à zéro (ou au niveau attendu), cohérence confirmée.


🟩 5. Frontend




🟩 6. Conclusion
Pour conclure, Cargo-TMS répond aux objectifs du cahier des charges Acteos :
la gestion complète des entrées/sorties, les règles de validation métier,
la génération d’un XML et son envoi automatique par mail,
et une interface Angular claire et opérationnelle.

L’architecture est modulaire et extensible :
on peut facilement ajouter du reporting, une authentification,
des intégrations avec d’autres services logistiques (par exemple une API des entrepôts connus),
une gestion des stocks plus avancée,
et une pagination frontend/backend côté Angular, si le volume l’exige.