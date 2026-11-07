-- Script d'initialisation manuel pour phpMyAdmin / XAMPP si besoin
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

INSERT INTO `events` (`title`, `date`, `time`, `location`, `description`, `badge_text`, `badge_type`, `seats_left`, `is_registered`, `image`, `category`)
VALUES
('Street Style Workshop', '24 May 2025', '2:00 PM - 5:00 PM', 'ZAYNA Studio, Mumbai', 'Learn how to elevate your everyday looks with street style tips from industry experts.', '12 seats left', 'seats', 12, 0, '/api/events/images/street_style.jpg', 'WORKSHOPS'),
('Local Designers Fashion Show', '31 May 2025', '6:30 PM - 9:00 PM', 'The Courtyard, Bengaluru', 'Celebrate creativity at an exclusive runway showcase featuring emerging local talent.', '80+ going', 'going', 80, 0, '/api/events/images/designers_show.jpg', 'SHOWS'),
('Color Styling Meetup', '7 Jun 2025', '11:00 AM - 1:00 PM', 'Creative Space, Delhi', 'Discover the power of color and find shades that truly suit you.', '15 seats left', 'seats', 15, 0, '/api/events/images/color_styling.jpg', 'STYLING'),
('Eco-Chic Upcycling Masterclass', '14 Jun 2025', '3:00 PM - 6:00 PM', 'Atelier Green Lab, Mumbai', 'Transform pre-loved pieces into statement items using modern upcycling techniques.', '10 seats left', 'seats', 10, 0, '/api/events/images/upcycling.jpg', 'WORKSHOPS');
