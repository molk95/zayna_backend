package com.esprit.zayna;

import com.esprit.zayna.events.entity.Event;
import com.esprit.zayna.events.enumeration.EventCategory;
import com.esprit.zayna.events.repository.EventRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@SpringBootApplication
public class ZaynaBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZaynaBackendApplication.class, args);
    }

    @Bean
    public CommandLineRunner initDatabase(EventRepository repository) {
        return args -> {
            // Créer le dossier local d'uploads/events s'il n'existe pas
            Path uploadDir = Paths.get("uploads", "events");
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }

            if (repository.count() == 0) {
                System.out.println(">> Insertion des événements de démonstration ZAYNA dans MySQL...");

                // Téléchargement des images d'origine en local dans uploads/events/
                _downloadImageLocally(
                        "https://images.unsplash.com/photo-1515886657613-9f3515b0c78f?auto=format&fit=crop&w=800&q=80",
                        "street_style.jpg");

                _downloadImageLocally(
                        "https://images.unsplash.com/photo-1509631179647-0177331693ae?auto=format&fit=crop&w=800&q=80",
                        "designers_show.jpg");

                _downloadImageLocally(
                        "https://images.unsplash.com/photo-1490481651871-ab68de25d43d?auto=format&fit=crop&w=800&q=80",
                        "color_styling.jpg");

                _downloadImageLocally(
                        "https://images.unsplash.com/photo-1558769132-cb1aea458c5e?auto=format&fit=crop&w=800&q=80",
                        "upcycling.jpg");

                // 1. Street Style Workshop
                Event event1 = new Event(
                        null,
                        "Street Style Workshop",
                        "24 May 2025",
                        "2:00 PM - 5:00 PM",
                        "ZAYNA Studio, Mumbai",
                        "Learn how to elevate your everyday looks with street style tips from industry experts.",
                        "12 seats left",
                        "seats",
                        12,
                        false,
                        "/api/events/images/street_style.jpg",
                        EventCategory.WORKSHOPS
                );

                // 2. Local Designers Fashion Show
                Event event2 = new Event(
                        null,
                        "Local Designers Fashion Show",
                        "31 May 2025",
                        "6:30 PM - 9:00 PM",
                        "The Courtyard, Bengaluru",
                        "Celebrate creativity at an exclusive runway showcase featuring emerging local talent.",
                        "80+ going",
                        "going",
                        80,
                        false,
                        "/api/events/images/designers_show.jpg",
                        EventCategory.SHOWS
                );

                // 3. Color Styling Meetup
                Event event3 = new Event(
                        null,
                        "Color Styling Meetup",
                        "7 Jun 2025",
                        "11:00 AM - 1:00 PM",
                        "Creative Space, Delhi",
                        "Discover the power of color and find shades that truly suit you.",
                        "15 seats left",
                        "seats",
                        15,
                        false,
                        "/api/events/images/color_styling.jpg",
                        EventCategory.STYLING
                );

                // 4. Eco-Chic Upcycling Masterclass
                Event event4 = new Event(
                        null,
                        "Eco-Chic Upcycling Masterclass",
                        "14 Jun 2025",
                        "3:00 PM - 6:00 PM",
                        "Atelier Green Lab, Mumbai",
                        "Transform pre-loved pieces into statement items using modern upcycling techniques.",
                        "10 seats left",
                        "seats",
                        10,
                        false,
                        "/api/events/images/upcycling.jpg",
                        EventCategory.WORKSHOPS
                );

                repository.save(event1);
                repository.save(event2);
                repository.save(event3);
                repository.save(event4);

                System.out.println(">> Données initialisées avec succès dans la base de données !");
            }
        };
    }

    private void _downloadImageLocally(String imageUrl, String targetFileName) {
        try {
            Path targetPath = Paths.get("uploads", "events", targetFileName);
            if (!Files.exists(targetPath)) {
                URL url = URI.create(imageUrl).toURL();
                try (InputStream in = url.openStream()) {
                    Files.copy(in, targetPath, StandardCopyOption.REPLACE_EXISTING);
                }
            }
        } catch (Exception e) {
            System.err.println("Note: Image sauvegardée localement si connexion disponible: " + targetFileName);
        }
    }
}
