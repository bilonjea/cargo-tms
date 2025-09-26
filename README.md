# Cargo TMS – Cargo Transport Management System

## 📦 Contexte
Application **full-stack** (Angular 20 + Spring Boot 3.5.6) pour gérer les **mouvements de marchandises** (entrées/sorties) dans un entrepôt sous douane.

Cas d’usage simulé : *RapidCargo CDG* →
- Déclarer des mouvements **IN / OUT**
- Lister les **50 derniers mouvements**
- Vérifier la **disponibilité** par référence marchandise
- Envoyer chaque déclaration par **email** avec **pièce jointe XML**

---

## 🏗️ Structure du projet

```text
cargo-tms/
│
├─ contracts-api/                     
│   └─ src/main/resources/openapi.yaml       # Spécification OpenAPI (Swagger), génération DTO
│
├─ tms/
│   ├─ tms-service/                          # Backend Spring Boot (REST API, JPA, Mail, règles métier)
│   └─ tms-db/                               # Migrations Liquibase (module séparé ou inclus)
│
├─ tms-sb-admin/                             # Serveur Spring Boot Admin (monitoring)
│
├─ frontend/                                 # Application Angular 20 (UI : liste + formulaire)
│
├─ infra/                                    # docker-compose (Postgres, Mailhog, SBA, etc.)
│
└─ postman/                                  # Collections Postman pour tester les endpoints
    └─ cargo.postman_collection.json

```

### Détails

- **contracts-api/**  
  - Spécification `openapi.yaml`, source de vérité  
  - DTO générés côté backend (Java) et frontend (TypeScript)

- **tms-service/**  
  - Backend Spring Boot 3.5.6 (Java 21)  
  - Contient la logique métier et les API REST

- **tms-db/**  
  - Changelogs Liquibase

- **tms-sb-admin/**  
  - Serveur Spring Boot Admin (optionnel)

- **frontend/**  
  - Application Angular 20  
  - Fonctionnalités : liste des 50 derniers mouvements + formulaire IN/OUT

- **infra/**  
  - Orchestration Docker Compose  
  - Services : PostgreSQL, Mailhog, SBA

## ⚙️ Stack technique

### Backend
- **Langage** : Java 21  
- **Framework** : Spring Boot 3.5.6  
- **Starters** :
  - Web
  - Data JPA
  - Validation
  - Mail
  - Actuator
- **BDD** :
  - PostgreSQL (prod)
  - H2 (dev/test)
- **Migrations** : Liquibase  
- **Mapping** : MapStruct + Lombok  
- **XML** : Jackson XML (génération de la pièce jointe)  
- **Mail** :
  - Dev → Mailhog (SMTP fake + UI : [http://localhost:8025](http://localhost:8025))
  - Prod → SMTP réel (ex. Mailjet)
- **Tests** :
  - JUnit 5 (Jupiter)
  - Mockito
  - Spring Boot Test
- **Seed de données** : Faker (données aléatoires en dev)

### Frontend
- **Framework** : Angular 20  
- **Écrans** :
  - Liste des 50 derniers mouvements
  - Formulaire IN/OUT (Reactive Forms + validations)
- **Design** : Bootstrap minimal  
- **Client API** : DTO générés depuis OpenAPI

### Infra
- **Docker Compose** :
  - Postgres
  - Mailhog
  - Spring Boot Admin (optionnel)
  - (optionnel) containers backend/frontend

## 🔑 Fonctionnalités

- **Déclaration d’un mouvement IN/OUT** via API REST
- **Sauvegarde en BDD** avec règles métier :
  - `quantity ≤ totalQuantity`
  - `weight ≤ totalWeight`
  - Sortie interdite si pas d’entrée préalable ou si stock insuffisant
  - Si `refType = AWB` → `refCode = 11 chiffres`
- **Génération d’un XML conforme** (IN/OUT) → envoi par email en pièce jointe
- **Consultation** des 50 derniers mouvements
- **Endpoint `availability`** → expose la disponibilité pour pré-validation côté frontend
- **Endpoints dev-only** → charger / vider des données (fixtures)
- **Monitoring** via Spring Boot Admin (si déployé)


## 📜 API FIRST (OpenAPI)

- **Contrat** : `contracts-api/src/main/resources/openapi.yaml`
- **Erreurs** : format **RFC 7807 Problem Details** (`application/problem+json`)
- **DTO générés automatiquement** :
  - Java côté backend dans le projet contracts-api
  - TypeScript côté frontend
  ```
  pnpm openapi-generator-cli generate \
  -i ../contracts-api/src/main/resources/openapi.yaml \
  -g typescript-angular \
  -o src/app/api \
  --additional-properties=ngVersion=20,providedInRoot=true,stringEnums=true,withSeparateModelsAndApi=true,modelSuffix=Dto,serviceSuffix=Api
  ```

## 🚀 Lancer le projet

### Pré-requis
- Java **21**
- Maven **3.9+**
- Node **20+** (Angular 20)
- pnpm (gestionnaire de dépendances recommandé)
- Angular CLI installé pour générer des composants  
  Exemple : `pnpm ng generate component monComponent`
- Docker & Docker Compose (via WSL Ubuntu)

###  🛠️ Builds

Deux options sont disponibles pour compiler tous les projets :

- **Script Bash** : 
```
chmod +x build-all.sh
./build-all.sh
 ```
- **Makefile** : `make` (ou `make <cible>` pour un build ciblé)
 Exemple : make run-backend
 Exemple : make run-frontend


### Étapes

```
# 1. Démarrer l’infra (Postgres + Mailhog)
docker compose -f infra/docker-compose.yml up -d

# 2. Lancer le backend (profil dev : H2 + Mailhog)
cd tms-service
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev

# 3. Lancer le frontend Angular
cd frontend
npm install
npm start

# 4. Stopper sans perdre la DB
docker compose -f infra/docker-compose.yml down

# Reset complet (DB neuve) :
docker compose -f infra/docker-compose.yml down -v && docker compose -f infra/docker-compose.yml up -d
```

### 🔗 URLs

- **Backend Service** : [http://localhost:8080/tms-service_v1](http://localhost:8080/tms-service_v1)  
- **API** : [http://localhost:8080/tms-service_v1/swagger-ui/index.html](http://localhost:8080/tms-service_v1/swagger-ui/index.html)
- **UI** : [http://localhost:4200](http://localhost:4200) 
- **Postgres** : [http://localhost:5432)](http://localhost:5432))
- **Mailhog UI** : [http://localhost:8025](http://localhost:8025)  
- **Spring Boot Admin** : [http://localhost:9000/tms-sb-admin](http://localhost:9000/tms-sb-admin) *(si activé)*  

## 🧪 Tests

### Commande

```
mvn clean test
```
### Types de tests

- **Unitaires** : règles métier (OUT sans IN, OUT > disponible, etc.)
- **Intégration** : persistance JPA + validations Bean
- **Service Mail** : envoi simulé via Mailhog  

## 📊 Observabilité

- **Spring Boot Actuator** : `/actuator`
- **Spring Boot Admin** : module `tms-sb-admin` *(optionnel)*  

## 📌 Choix techniques & évolutivité

### Stock
- Calcul à la volée (**Σ IN – Σ OUT**)
- Variante : table `inventory` avec verrouillage optimiste (préparée)
- Futur : projection **Kafka** documentée (non implémentée)

### Email
- `MailService` abstrait
- Mailhog en **dev**
- Mailjet en **prod**

### OpenAPI-first
- Un **contrat unique** partagé entre backend et frontend

### XML
- Génération avec **Jackson XML**
- Alternative possible : **JAXB**

### Admin
- **Spring Boot Admin** indépendant  

## 👤 Auteur

- **Nom** : Jean Noël Bilong Mboumba 






