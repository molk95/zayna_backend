package com.esprit.zayna.events.entity;

import com.esprit.zayna.events.enumeration.EventCategory;
import jakarta.persistence.*;

/**
 * Entité représentant un événement de mode dans la base de données MySQL
 */
@Entity
@Table(name = "events")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String date;

    @Column(nullable = false)
    private String time;

    @Column(nullable = false)
    private String location;

    @Column(length = 1500)
    private String description;

    private String badgeText;

    private String badgeType; // "seats" ou "going"

    private Integer seatsLeft;

    private Boolean isRegistered = false;

    @Column(length = 500)
    private String image;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventCategory category;

    // Constructeur sans argument (requis par JPA)
    public Event() {
    }

    // Constructeur complet
    public Event(Long id, String title, String date, String time, String location,
                 String description, String badgeText, String badgeType,
                 Integer seatsLeft, Boolean isRegistered, String image, EventCategory category) {
        this.id = id;
        this.title = title;
        this.date = date;
        this.time = time;
        this.location = location;
        this.description = description;
        this.badgeText = badgeText;
        this.badgeType = badgeType;
        this.seatsLeft = seatsLeft;
        this.isRegistered = isRegistered != null ? isRegistered : false;
        this.image = image;
        this.category = category;
    }

    // --- Getters et Setters ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBadgeText() {
        return badgeText;
    }

    public void setBadgeText(String badgeText) {
        this.badgeText = badgeText;
    }

    public String getBadgeType() {
        return badgeType;
    }

    public void setBadgeType(String badgeType) {
        this.badgeType = badgeType;
    }

    public Integer getSeatsLeft() {
        return seatsLeft;
    }

    public void setSeatsLeft(Integer seatsLeft) {
        this.seatsLeft = seatsLeft;
    }

    public Boolean getIsRegistered() {
        return isRegistered;
    }

    public void setIsRegistered(Boolean registered) {
        isRegistered = registered;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public EventCategory getCategory() {
        return category;
    }

    public void setCategory(EventCategory category) {
        this.category = category;
    }
}
