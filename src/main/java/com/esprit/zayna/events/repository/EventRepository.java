package com.esprit.zayna.events.repository;

import com.esprit.zayna.events.entity.Event;
import com.esprit.zayna.events.enumeration.EventCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Interface Repository pour l'accès aux données des événements
 */
@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findByCategory(EventCategory category);

    List<Event> findByTitleContainingIgnoreCase(String keyword);
}
