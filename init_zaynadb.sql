-- Script de création de table pour MySQL / phpMyAdmin
CREATE DATABASE IF NOT EXISTS `zaynadb` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `zaynadb`;

CREATE TABLE IF NOT EXISTS `events` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `title` VARCHAR(255) NOT NULL,
    `date` VARCHAR(100) NOT NULL,
    `time` VARCHAR(100) NOT NULL,
    `location` VARCHAR(255) NOT NULL,
    `description` TEXT,
    `badge_text` VARCHAR(100),
    `badge_type` VARCHAR(50) DEFAULT 'seats',
    `seats_left` INT DEFAULT 0,
    `is_registered` BOOLEAN DEFAULT FALSE,
    `image` VARCHAR(500),
    `category` VARCHAR(50) NOT NULL
);
