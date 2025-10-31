package com.example.coapdemo;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reading")
public class Reading {
    
    @Id
    @Column(name = "capture_id")
    private Long captureId;
    
    @Id  
    @Column(name = "node_id", nullable = false, length = 6)
    private String nodeId;
    
    @Column(name = "reading_type", nullable = false)
    private String dataType;
    
    @Column(name = "reading_val", nullable = false)
    private Double dataValue;

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;
    
    // Default constructor
    public Reading() {}
    
    // Constructor with parameters
    public Reading(Long captureId, String nodeId, String dataType, Double dataValue, LocalDateTime timestamp) {
        this.captureId = captureId;
        this.nodeId = nodeId;
        this.dataType = dataType;
        this.dataValue = dataValue;
        this.timestamp = timestamp;
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
    
    public String getDataType() {
        return dataType;
    }
    
    public void setDataType(String dataType) {
        this.dataType = dataType;
    }
    
    public Double getDataValue() {
        return dataValue;
    }
    
    public void setDataValue(Double dataValue) {
        this.dataValue = dataValue;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}