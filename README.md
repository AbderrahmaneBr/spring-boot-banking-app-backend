## Documentation

### Structure des dossiers

Le projet suit une structure modulaire et claire :

```
spring-boot-banking-app-backend/
│
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── banking/
│   │   │           ├── controller/      # Contrôleurs REST
│   │   │           ├── service/         # Logique métier
│   │   │           ├── dto/             # Objets de transfert de données (DTO)
│   │   │           ├── mapper/          # Convertisseurs entités <-> DTO
│   │   │           ├── model/           # Entités JPA (modèle)
│   │   │           └── repository/      # Accès aux données (JPA Repository)
│   │   └── resources/
│   │       └── application.properties   # Configuration
│   └── test/
│       └── java/                        # Tests unitaires et d’intégration
└── pom.xml                              # Dépendances Maven
```

### Architecture du modèle et justification

L’architecture repose sur la séparation des responsabilités (inspirée du Domain Driven Design).  
Les entités principales (`Compte`, `Client`, `Transaction`, etc.) sont dans `model`.  
Ce choix permet :

- Une meilleure lisibilité et organisation du code.
- Une testabilité accrue (chaque couche est testable indépendamment).
- Une évolutivité facilitée (ajout de fonctionnalités sans impacter l’ensemble).

### Différences entre les stratégies de mapping JPA

- **Single Table** :  
  Toutes les classes héritées sont stockées dans une seule table avec une colonne discriminante.  
  *Avantages* : rapide, simple.  
  *Inconvénients* : beaucoup de colonnes nulles, difficile à maintenir si la hiérarchie grandit.

- **Table per Class** :  
  Chaque classe concrète a sa propre table avec toutes ses propriétés.  
  *Avantages* : pas de colonnes inutilisées.  
  *Inconvénients* : duplication de colonnes, requêtes polymorphes complexes.

- **Joined Table** :  
  Une table par classe, reliées par des clés étrangères.  
  *Avantages* : normalisation, pas de redondance.  
  *Inconvénients* : requêtes plus lentes (jointures), complexité accrue.

_Dans une application bancaire, la stratégie « Joined Table » est souvent privilégiée pour sa flexibilité et sa normalisation._

### Description des couches

- **Controllers** (`controller/`) :  
  Exposent les endpoints REST, reçoivent les requêtes, valident les entrées, appellent les services.

- **Services** (`service/`) :  
  Contiennent la logique métier, orchestrent les opérations, appliquent les règles de gestion.

- **DTO** (`dto/`) :  
  Objets servant à transférer les données entre les couches, exposent uniquement les informations nécessaires à l’API.

- **Mappers** (`mapper/`) :  
  Convertissent les entités JPA en DTO et inversement (via MapStruct ou méthodes manuelles).

### Pourquoi utiliser des DTO ?

- **Sécurité** : on ne révèle pas la structure interne de la base de données.
- **Contrôle** : on expose uniquement les champs nécessaires à l’API.
- **Évolution** : on peut faire évoluer l’API sans impacter la structure interne.
- **Validation** : les DTO peuvent intégrer des règles de validation spécifiques.

**En résumé** :  
L’utilisation de DTO améliore la sécurité, la maintenabilité et la clarté du code, contrairement à l’exposition directe des entités.
