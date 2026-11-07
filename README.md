# ZAYNA Backend

Backend Spring Boot 3 & MySQL pour la gestion dynamique des événements de mode de l'application mobile ZAYNA.

---

## Architecture modulaire du projet

Chaque membre de l'équipe dispose de son propre package sous `com.zayna`:

```
backend/
├── src/main/java/com/ZAYNA/zayna/
│   ├── ZaynaBackendApplication.java         # Point d'entrée principal (scanne tous les modules)
│   └── events/                              # Dossier dédié au module Events
│       ├── controller/EventController.java  # Endpoints REST (CRUD + Inscription)
│       ├── dto/EventDTO.java                # Objets de transfert de données
│       ├── entity/Event.java                # Entité JPA (Table MySQL "events")
│       ├── enumeration/EventCategory.java   # Enum (WORKSHOPS, SHOWS, STYLING, UPCOMING)
│       ├── repository/EventRepository.java  # Interface Spring Data JPA
│       └── service/                         # IEventService.java & EventServiceImpl.java
│   └── (autres modules de vos camarades: user, marketplace, looks...)
├── src/main/resources/
│   └── application.properties               # Configuration MySQL (XAMPP & Docker)
├── uploads/events/                          # Stockage local des images des événements
├── Dockerfile                               # Image Docker multi-stage
├── docker-compose.yml                       # Orchestration Backend + MySQL 8
├── init_zaynadb.sql                         # Script SQL pour phpMyAdmin
└── pom.xml                                  # Dépendances Maven (Web, Data JPA, MySQL)
```

---

## Démarrage sous Docker

1. Ouvrez un terminal dans le dossier `backend` :
   ```bash
   cd backend
   ```
2. Lancez les conteneurs :
   ```bash
   docker compose up -d --build
   ```

---

## Démarrage en local avec XAMPP (Sans Docker)

1. Démarrez le service **MySQL** dans le **XAMPP Control Panel**.
2. Lancez Spring Boot depuis votre IDE (IntelliJ, Eclipse, VS Code) en exécutant `ZaynaBackendApplication.java`, ou en ligne de commande :
   ```bash
   cd backend
   mvn spring-boot:run
   ```
*(La base `zaynadb` est créée automatiquement).*
