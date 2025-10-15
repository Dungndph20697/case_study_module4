package com.codegym.case_study_module4.model;

import jakarta.persistence.*;

@Entity
@Table(name = "images")
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String url;

    @ManyToOne
    @JoinColumn(name = "room_id")
    private Room room;

    public Image() {}

    public Image(String url) {
        this.url = url;
    }

    // getters & setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public Room getRoom() { return room; }
    public void setRoom(Room room) { this.room = room; }
}
