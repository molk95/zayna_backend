package com.esprit.zayna.events.controller;

import com.esprit.zayna.events.dto.EventDTO;
import com.esprit.zayna.events.service.IEventService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
}
