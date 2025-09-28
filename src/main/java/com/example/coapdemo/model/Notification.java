package com.example.coapdemo;

import jakarta.persistence.*;

@Entity
@Table(name = "notification")
public class Notification {
    
    @Id
    @Column(name = "capture_id")
    private Long captureId;
    
    @Id  
    @Column(name = "node_id", nullable = false, length = 6)
    private String nodeId;
    
    @Column(name = "notif_code", nullable = false)
    private Integer notificationCode;
    
    // Default constructor
    public Notification() {}
    
    // Constructor with parameters
    public Notification(Long captureId, String nodeId, Integer notificationCode) {
        this.captureId = captureId;
        this.nodeId = nodeId;
        this.notificationCode = notificationCode;
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
    
    public Integer getNotificationCode() {
        return notificationCode;
    }
    
    public void setNotificationCode(Integer notificationCode) {
        this.notificationCode = notificationCode;
    }
}