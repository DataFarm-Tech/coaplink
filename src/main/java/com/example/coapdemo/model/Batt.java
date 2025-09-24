package com.example.coapdemo;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "battery")
public class Batt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nodeId;

    @Column(nullable = false)
    private int level;   // battery level percentage (0-100)

    @Column(nullable = false)
    private int health;  // battery health percentage (0-100)

    @Column(nullable = true)
    private LocalDateTime timestamp;

    public Batt() {}

    public Batt(String nodeId, int level, int health, LocalDateTime timestamp) {
        this.nodeId = nodeId;
        this.level = level;
        this.health = health;
        this.timestamp = timestamp; // Initialize with current time
    }

    // Getters and setters

    public Long getId() {
        return id;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
