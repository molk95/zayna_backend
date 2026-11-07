package com.zayna.events.service;

import com.zayna.events.dto.EventDTO;
import com.zayna.events.entity.Event;
import com.zayna.events.enumeration.EventCategory;
import com.zayna.events.repository.EventRepository;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implémentation du service métier des événements ZAYNA
 */
@Service
public class EventServiceImpl implements IEventService {

    private final EventRepository eventRepository;

    public EventServiceImpl(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Override
    public List<EventDTO> getAllEvents(String category) {
        List<Event> events;
        if (category == null || category.trim().isEmpty() || category.equalsIgnoreCase("All") || category.equalsIgnoreCase("Upcoming")) {
            events = eventRepository.findAll();
        } else {
            try {
                EventCategory cat = EventCategory.valueOf(category.toUpperCase());
                events = eventRepository.findByCategory(cat);
            } catch (IllegalArgumentException e) {
                events = eventRepository.findAll();
            }
        }
        return events.stream().map(EventDTO::new).collect(Collectors.toList());
    }

    @Override
    public EventDTO getEventById(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Événement introuvable avec l'identifiant : " + id));
        return new EventDTO(event);
    }

    @Override
    public EventDTO createEvent(EventDTO eventDTO) {
        Event event = eventDTO.toEntity();
        Event saved = eventRepository.save(event);
        return new EventDTO(saved);
    }

    @Override
    public EventDTO updateEvent(Long id, EventDTO eventDTO) {
        Event existing = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Événement introuvable avec l'identifiant : " + id));

        existing.setTitle(eventDTO.getTitle());
        existing.setDate(eventDTO.getDate());
        existing.setTime(eventDTO.getTime());
        existing.setLocation(eventDTO.getLocation());
        existing.setDescription(eventDTO.getDescription());
        existing.setBadgeText(eventDTO.getBadgeText());
        existing.setBadgeType(eventDTO.getBadgeType());
        existing.setSeatsLeft(eventDTO.getSeatsLeft());
        existing.setImage(eventDTO.getImage());

        try {
            existing.setCategory(EventCategory.valueOf(eventDTO.getCategory().toUpperCase()));
        } catch (Exception ignored) {
        }

        Event updated = eventRepository.save(existing);
        return new EventDTO(updated);
    }

    @Override
    public void deleteEvent(Long id) {
        if (!eventRepository.existsById(id)) {
            throw new RuntimeException("Événement introuvable pour suppression avec l'id : " + id);
        }
        eventRepository.deleteById(id);
    }

    @Override
    public EventDTO registerForEvent(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Événement introuvable avec l'identifiant : " + id));

        if (event.getSeatsLeft() != null && event.getSeatsLeft() > 0) {
            event.setSeatsLeft(event.getSeatsLeft() - 1);
            event.setIsRegistered(true);
        } else {
            throw new RuntimeException("Désolé, plus aucune place disponible pour cet événement.");
        }

        Event updated = eventRepository.save(event);
        return new EventDTO(updated);
    }

    @Override
    public byte[] getEventImage(String fileName) {
        try {
            // 1. Chercher dans le dossier uploads/events/ sur le disque
            Path filePath = Paths.get("uploads", "events", fileName);
            File file = filePath.toFile();
            if (file.exists()) {
                return Files.readAllBytes(filePath);
            }

            // 2. Chercher dans les ressources du classpath
            ClassPathResource resource = new ClassPathResource("static/images/" + fileName);
            if (resource.exists()) {
                try (InputStream is = resource.getInputStream()) {
                    return is.readAllBytes();
                }
            }
        } catch (Exception e) {
            System.err.println("Erreur de lecture image : " + e.getMessage());
        }
        return new byte[0];
    }
}
