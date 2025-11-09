package com.example.coapdemo;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;


public class ActivateService {

    private final ActivateRepository activateRepository;
    private final CaptureRepository captureRepository;
    private static final byte[] SECRET_KEY = new byte[]{
        0x12, (byte)0xA4, 0x56, (byte)0xB7, (byte)0x8C, (byte)0x91, (byte)0xDE, (byte)0xF3,
        0x45, 0x67, (byte)0x89, (byte)0xAB, (byte)0xCD, (byte)0xEF, 0x01, 0x23,
        0x34, 0x56, 0x78, (byte)0x9A, (byte)0xBC, (byte)0xDE, (byte)0xF0, 0x12,
        0x34, 0x56, 0x78, (byte)0x9A, (byte)0xBC, (byte)0xDE, (byte)0xF0, 0x12
    };

    public ActivateService(ActivateRepository activateRepository, CaptureRepository captureRepository) {
        this.activateRepository = activateRepository;
        this.captureRepository = captureRepository;
    }

    public boolean processActivate(String nodeId, String gpsCoor, byte[] key) {
        try {
            // Check if node already activated
            ActiveNode existingNode = activateRepository.getNodeByNodeId(nodeId);
            if (existingNode != null) {
                return false;
            }

            // Verify HMAC key directly
            if (!verifyKey(key)) {
                System.err.println("Invalid key for node: " + nodeId);
                return false;
            }

            LocalDateTime timestamp = LocalDateTime.ofInstant(Instant.now(), ZoneOffset.UTC);
            Capture capture = new Capture(timestamp);
            Capture savedCapture = captureRepository.saveCapture(capture);
            Long captureId = savedCapture.getCaptureId();

            ActiveNode nodeToActivate = new ActiveNode(captureId, nodeId, gpsCoor);
            activateRepository.saveActivate(nodeToActivate);
            System.out.println("Node added: " + nodeId);
            return true;

        } catch (Exception e) {
            System.err.println("Error saving node: " + e.getMessage());
            return false;
        }
    }

    private boolean verifyKey(byte[] receivedKey) {
        try {
            Mac hmacSha256 = Mac.getInstance("HmacSHA256");
            SecretKeySpec keySpec = new SecretKeySpec(SECRET_KEY, "HmacSHA256");
            hmacSha256.init(keySpec);

            // Compute HMAC of empty message (same as ESP32)
            byte[] expectedHmac = hmacSha256.doFinal();

            return Arrays.equals(expectedHmac, receivedKey);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
