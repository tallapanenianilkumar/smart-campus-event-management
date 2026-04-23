package com.example.campus.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "event_table")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String title;
    private String description;
    @Column(length = 1000)
    private String imageUrl;
    private String date;
    private String time;
    private String day;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    
    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }
    
    public String getDay() { return day; }
    public void setDay(String day) { this.day = day; }
}
