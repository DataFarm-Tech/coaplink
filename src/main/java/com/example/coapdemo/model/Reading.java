package com.example.coapdemo;

import jakarta.persistence.*;

@Entity
@Table(name = "reading")
public class Reading {
    
    @Id
    @Column(name = "capture_id")
    private Long captureId;
    
    @Id  
    @Column(name = "node_id", nullable = false, length = 6)
    private String nodeId;
    
    @Column(name = "read_type", nullable = false)
    private Double dataType;
    
    @Column(name = "read_val", nullable = false)
    private Double dataValue;
    
    // Default constructor
    public Reading() {}
    
    // Constructor with parameters
    public Reading(Long captureId, String nodeId, Double dataType, Double dataValue) {
        this.captureId = captureId;
        this.nodeId = nodeId;
        this.dataType = dataType;
        this.dataValue = dataValue;
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
    
    public Double getDataType() {
        return dataType;
    }
    
    public void setDataType(Double dataType) {
        this.dataType = dataType;
    }
    
    public Double getDataValue() {
        return dataValue;
    }
    
    public void setDataValue(Double dataValue) {
        this.dataValue = dataValue;
    }
}