package com.esprit.zayna.events.service;

import com.esprit.zayna.events.dto.EventDTO;

import java.util.List;

/**
 * Interface de service pour la gestion des événements
 */
public interface IEventService {

    List<EventDTO> getAllEvents(String category);

    EventDTO getEventById(Long id);

    EventDTO createEvent(EventDTO eventDTO);

    EventDTO updateEvent(Long id, EventDTO eventDTO);

    void deleteEvent(Long id);

    EventDTO registerForEvent(Long id);

    byte[] getEventImage(String fileName);
}
