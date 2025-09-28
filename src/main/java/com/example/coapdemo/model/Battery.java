package com.example.coapdemo;

import jakarta.persistence.*;

@Entity
@Table(name = "battery")
public class Battery {
    
    @Id
    @Column(name = "capture_id")
    private Long captureId;
    
    @Id  
    @Column(name = "node_id", nullable = false, length = 6)
    private String nodeId;
    
    @Column(name = "bat_lvl", nullable = false)
    private Double batteryLevel;
    
    @Column(name = "bat_hlth", nullable = false)
    private Double batteryHealth;
    
    // Foreign key relationship to Capture entity
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "capture_id", referencedColumnName = "capture_id", insertable = false, updatable = false)
    private Capture capture;
    
    // Default constructor
    public Battery() {}
    
    // Constructor with parameters
    public Battery(Long captureId, String nodeId, Double batteryLevel, Double batteryHealth) {
        this.captureId = captureId;
        this.nodeId = nodeId;
        this.batteryLevel = batteryLevel;
        this.batteryHealth = batteryHealth;
    }
    
    // Getters and setters
    public Long getCaptureId() {
        return captureId;
    }
    
    public void setCaptureId(Long captureId) {
        this.captureId = captureId;
    }
    
    public String getNodeId() {
        return nodeId;
    }
    
    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }
    
    public Double getBatteryLevel() {
        return batteryLevel;
    }
    
    public void setBatteryLevel(Double batteryLevel) {
        this.batteryLevel = batteryLevel;
    }
    
    public Double getBatteryHealth() {
        return batteryHealth;
    }
    
    public void setBatteryHealth(Double batteryHealth) {
        this.batteryHealth = batteryHealth;
    }
    
    public Capture getCapture() {
        return capture;
    }
    
    public void setCapture(Capture capture) {
        this.capture = capture;
    }
}