package com.example.coapdemo;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "capture")
public class Capture {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "capture_id")
    private Long captureId;
    
    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;
    
    @Column(name = "resp_time", nullable = true)
    private Double responseTime;
    
    // Default constructor
    public Capture() {}
    
    // Constructor with parameters
    public Capture(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
    
    // Getters and setters
    public Long getCaptureId() {
        return captureId;
    }
    
    public void setCaptureId(Long captureId) {
        this.captureId = captureId;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
    
    public Double getResponseTime() {
        return responseTime;
    }
    
    public void setResponseTime(Double responseTime) {
        this.responseTime = responseTime;
    }
}