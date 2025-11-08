package com.example.coapdemo;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class ActivateService {

    private final ActivateRepository activateRepository;
    private final CaptureRepository captureRepository;

    public ActivateService(ActivateRepository activateRepository, CaptureRepository captureRepository) {
        this.activateRepository = activateRepository;
        this.captureRepository = captureRepository;
    }

    /**
     * Process an activation request.
     * Only saves node if it does not already exist.
     */
    public boolean processActivate(String nodeId, String gpsCoor) {
        try {
            
            // Check if node already exists
            ActiveNode existingNode = activateRepository.getNodeByNodeId(nodeId);
            if (existingNode != null) {
                System.out.println("Node already exists: " + nodeId);
                return false;
            }
            
            // Create a new capture
            LocalDateTime timestamp = LocalDateTime.ofInstant(Instant.now(), ZoneOffset.UTC);
            Capture capture = new Capture(timestamp);
            Capture savedCapture = captureRepository.saveCapture(capture);
            Long captureId = savedCapture.getCaptureId();
            
            // Node does not exist, save it
            ActiveNode nodeToActivate = new ActiveNode(captureId, nodeId, gpsCoor);
            activateRepository.saveActivate(nodeToActivate);
            System.out.println("Node added: " + nodeId);

            return true;

        } catch (Exception e) {
            System.err.println("Error saving node: " + e.getMessage());
            return false;
        }
    }
}
