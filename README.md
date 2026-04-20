# ShopFlow - Plateforme E-commerce

Une plateforme e-commerce B2C moderne permettant la gestion d'une marketplace avec trois rôles distincts : **ADMIN**, **SELLER**  et **CUSTOMER**.

## Fonctionnalités

### Administrateur
- Gestion globale de la plateforme
- Gestion des catégories de produits
- Modération des avis et commentaires
- Tableau de bord analytique complet

### Vendeur (Seller)
- Gestion de la boutique personnelle
- Gestion des produits et variantes (tailles, couleurs, etc.)
- Traitement des commandes
- Tableau de bord vendeur

### Client (Customer)
- Parcours du catalogue produits
- Gestion du panier d'achat
- Processus de commande et paiement
- Historique des commandes
- Dépôt d'avis et notations

## Tech Stack

### Backend
- **Java 21** - Langage de programmation
- **Spring Boot 3.2.5** - Framework Java
- **Spring Security** - Sécurité et authentification JWT
- **Spring Data JPA** - Accès aux données
- **PostgreSQL** - Base de données relationnelle
- **MapStruct** - Mapping DTO/Entity
- **Lombok** - Réduction du código boilerplate

### Frontend
- **Angular 17+** - Framework JavaScript
- **TypeScript** - Langage typé
- **Tailwind CSS** - Framework CSS utilitaire
- **RxJS** - Programmation réactive

## Prérequis

Avant de lancer le projet, assurez-vous d'avoir installé :

- **Java Development Kit (JDK) 21** ou supérieur
- **Node.js** (version 18+) et **npm**
- **PostgreSQL** (version 14+) installé et-configuré
- **Maven** (optionnel, wrapper inclus)

## Installation et Configuration

### 1. Configuration de la base de données

Créez une base de données PostgreSQL :

```sql
CREATE DATABASE shopflow;
```

Les credentials par défaut sont configurés dans `backend/src/main/resources/application.properties` :
- Utilisateur : `postgres`
- Mot de passe : `admin`
- Port : `5432`

### 2. Backend

```bash
# Aller dans le répertoire backend
cd backend

# Compiler le projet (PowerShell)
./mvnw clean install

# Lancer l'application (PowerShell)
./mvnw spring-boot:run
```

L'API REST démarre sur : **http://localhost:8080**

### 3. Frontend

```bash
# Aller dans le répertoire frontend
cd frontend

# Installer les dépendances
npm install

# Lancer le serveur de développement
npm start
```

L'application frontend démarrer sur : **http://localhost:4200**

## Documentation API

Une fois le backend lancé, accédez à la documentation Swagger UI :

**http://localhost:8080/swagger-ui**

Cette interface interactive vous permet de :
- Explorer toutes les endpoints API
- Tester les fonctionnalités directement
- Visualiser les modèles de données

## Tests

Pour exécuter les tests unitaires du backend :

```bash
cd backend
./mvnw test
```


## Comptes de Test

Après démarrage, vous pouvez créer des comptes via l'API ou l'interface d'inscription. Voici les rôles disponibles :
|Role    |   Email                     |  Description 
|-------------------------------------------------------------------------------
|ADMIN   | admin@shopflow.com          |   Full system access
|-------------------------------------------------------------------------------
|SELLER  | mohamed.benali@seller.com   |   TechZone Tunisia (15 products)
|SELLER  | fatma.trabelsi@seller.com   |   Fashion House Tunis (5 products)
|SELLER  | ahmed.bouazizi@seller.com   |   HomeStyle Déco (5 products)
|-------------------------------------------------------------------------------
|CUSTOMER| salma.hammami@customer.com  |   Has orders, cart, reviews
|CUSTOMER| karim.jlassi@customer.com   |   Has orders, cart
|CUSTOMER| leila.sassi@customer.com    |   Has orders, pending order 
|CUSTOMER| youssef.khelifi@customer.com|   Has cart, one order
|-------------------------------------------------------------------------------

## URLs Utiles

| Service     | URL                              |
|-------------|----------------------------------|
| Frontend    | http://localhost:4200            |
| API Backend | http://localhost:8080            |
| Swagger UI  | http://localhost:8080/swagger-ui |
