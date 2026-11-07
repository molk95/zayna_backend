package com.esprit.zayna.events.dto;

import com.esprit.zayna.events.entity.Event;
import com.esprit.zayna.events.enumeration.EventCategory;

/**
 * Data Transfer Object pour échanger les données d'un événement avec Flutter
 */
public class EventDTO {

    private Long id;
    private String title;
    private String date;
    private String time;
    private String location;
    private String description;
    private String badgeText;
    private String badgeType;
    private Integer seatsLeft;
    private Boolean isRegistered;
    private String image;
    private String category;

    public EventDTO() {
    }

    public EventDTO(Event event) {
        if (event != null) {
            this.id = event.getId();
            this.title = event.getTitle();
            this.date = event.getDate();
            this.time = event.getTime();
            this.location = event.getLocation();
            this.description = event.getDescription();
            this.badgeText = event.getBadgeText();
            this.badgeType = event.getBadgeType();
            this.seatsLeft = event.getSeatsLeft();
            this.isRegistered = event.getIsRegistered();
            this.image = event.getImage();
            this.category = event.getCategory() != null ? event.getCategory().name() : "WORKSHOPS";
        }
    }

    public Event toEntity() {
        Event event = new Event();
        event.setId(this.id);
        event.setTitle(this.title);
        event.setDate(this.date);
        event.setTime(this.time);
        event.setLocation(this.location);
        event.setDescription(this.description);
        event.setBadgeText(this.badgeText);
        event.setBadgeType(this.badgeType != null ? this.badgeType : "seats");
        event.setSeatsLeft(this.seatsLeft != null ? this.seatsLeft : 0);
        event.setIsRegistered(this.isRegistered != null ? this.isRegistered : false);
        event.setImage(this.image);
        try {
            event.setCategory(EventCategory.valueOf(this.category.toUpperCase()));
        } catch (Exception e) {
            event.setCategory(EventCategory.WORKSHOPS);
        }
        return event;
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
