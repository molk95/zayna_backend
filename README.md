# ZAYNA Backend

Backend Spring Boot 3 & MySQL pour la gestion dynamique des événements de mode de l'application mobile ZAYNA.

---

## Architecture modulaire du projet

Chaque membre de l'équipe dispose de son propre package sous `com.zayna`:

```
backend/
├── src/main/java/com/ZAYNA/zayna/
│   ├── ZaynaBackendApplication.java         
│   └── events/                              
│       ├── controller/EventController.java  
│       ├── dto/EventDTO.java                
│       ├── entity/Event.java                
│       ├── enumeration/EventCategory.java   
│       ├── repository/EventRepository.java  
│       └── service/                         
│   └── (autres moduleS: user, marketplace, looks...)
├── src/main/resources/
│   └── application.properties           
├── uploads/events/                         
├── Dockerfile                               
├── docker-compose.yml                      
├── init_zaynadb.sql                        
└── pom.xml                                  
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
