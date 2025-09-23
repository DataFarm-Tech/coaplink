package com.example.coapdemo;

import jakarta.persistence.*;

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

    public Batt() {}

    public Batt(String nodeId, int level, int health) {
        this.nodeId = nodeId;
        this.level = level;
        this.health = health;
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
        if (level < 0 || level > 100) {
            throw new IllegalArgumentException("Level must be between 0 and 100");
        }
        this.level = level;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        if (health < 0 || health > 100) {
            throw new IllegalArgumentException("Health must be between 0 and 100");
        }
        this.health = health;
    }
}
