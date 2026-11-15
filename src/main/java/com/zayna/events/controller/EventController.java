package com.zayna.events.controller;

import com.zayna.events.dto.EventDTO;
import com.zayna.events.service.IEventService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Contrôleur REST pour le module Events de ZAYNA
 */
@RestController
@RequestMapping("/api/events")
@CrossOrigin(origins = "*")
public class EventController {

    private final IEventService eventService;

    public EventController(IEventService eventService) {
        this.eventService = eventService;
    }

    /**
     * Obtenir tous les événements ou filtrer par catégorie
     * Exemples :
     * GET /api/events
     * GET /api/events?category=Workshops
     */
    @GetMapping
    public ResponseEntity<List<EventDTO>> getAllEvents(
            @RequestParam(required = false) String category) {
        List<EventDTO> events = eventService.getAllEvents(category);
        return ResponseEntity.ok(events);
    }

    /**
     * Obtenir les détails d'un événement par son identifiant
     * GET /api/events/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<EventDTO> getEventById(@PathVariable Long id) {
        EventDTO event = eventService.getEventById(id);
        return ResponseEntity.ok(event);
    }

    /**
     * Créer un nouvel événement
     * POST /api/events
     */
    @PostMapping
    public ResponseEntity<EventDTO> createEvent(@RequestBody EventDTO eventDTO) {
        EventDTO created = eventService.createEvent(eventDTO);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    /**
     * Mettre à jour un événement existant
     * PUT /api/events/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<EventDTO> updateEvent(
            @PathVariable Long id,
            @RequestBody EventDTO eventDTO) {
        EventDTO updated = eventService.updateEvent(id, eventDTO);
        return ResponseEntity.ok(updated);
    }

    /**
     * Supprimer un événement
     * DELETE /api/events/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {
        eventService.deleteEvent(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * S'inscrire à un événement (décrémente une place et confirme l'inscription)
     * POST /api/events/{id}/register
     */
    @PostMapping("/{id}/register")
    public ResponseEntity<EventDTO> registerForEvent(@PathVariable Long id) {
        EventDTO registered = eventService.registerForEvent(id);
        return ResponseEntity.ok(registered);
    }

    /**
     * Servir une image d'événement stockée en local
     * GET /api/events/images/{fileName}
     */
    @GetMapping("/images/{fileName:.+}")
    public ResponseEntity<byte[]> getEventImage(@PathVariable String fileName) {
        byte[] imageBytes = eventService.getEventImage(fileName);
        if (imageBytes.length == 0) {
            return ResponseEntity.notFound().build();
        }

        HttpHeaders headers = new HttpHeaders();
        if (fileName.toLowerCase().endsWith(".png")) {
            headers.setContentType(MediaType.IMAGE_PNG);
        } else {
            headers.setContentType(MediaType.IMAGE_JPEG);
        }

        return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
    }

    /**
     * Uploader une image d'événement depuis le smartphone ou web
     * POST /api/events/upload-image
     */
    @PostMapping(value = "/upload-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, String>> uploadEventImage(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Fichier vide"));
        }
        try {
            String originalFilename = file.getOriginalFilename();
            String extension = ".jpg";
            if (originalFilename != null && originalFilename.lastIndexOf(".") != -1) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String newFileName = UUID.randomUUID() + extension;
            Path uploadPath = Paths.get("uploads", "events").toAbsolutePath().normalize();
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            Path destination = uploadPath.resolve(newFileName);
            Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
            String imageUrl = "/api/events/images/" + newFileName;
            return ResponseEntity.ok(Map.of("imageUrl", imageUrl));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur upload: " + e.getMessage()));
        }
    }

}