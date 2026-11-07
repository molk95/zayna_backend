package com.zayna;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@SpringBootApplication
public class ZaynaBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZaynaBackendApplication.class, args);
    }

    /**
     * Initialisation du répertoire de stockage des images
     */
    @Bean
    public CommandLineRunner initDatabase() {
        return args -> {
            Path uploadDir = Paths.get("uploads", "events");
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }
        };
    }
}
