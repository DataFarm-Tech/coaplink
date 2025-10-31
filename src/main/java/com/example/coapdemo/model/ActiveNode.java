package com.example.coapdemo;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "active_node")
public class ActiveNode {
    
    @Id
    @Column(name = "capture_id")
    private Long captureId;
    
    @Id  
    @Column(name = "node_id", nullable = false, length = 6)
    private String nodeId;
    
    @Column(name = "gps", nullable = false, length = 30)
    private String gps;
    
    // Default constructor
    public ActiveNode() {}
    
    // Constructor with parameters
    public ActiveNode(Long captureId, String nodeId, String gps) {
        this.captureId = captureId;
        this.nodeId = nodeId;
        this.gps = gps;
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
    
    public String getGps() {
        return gps;
    }
    
    public void setGps(String gps) {
        this.gps = gps;
    }
}