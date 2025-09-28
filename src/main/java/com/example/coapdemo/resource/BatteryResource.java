package com.example.coapdemo;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.eclipse.californium.core.coap.CoAP;
import com.upokecenter.cbor.CBORObject;
import java.time.LocalDateTime;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import com.upokecenter.cbor.CBORType;

public class BatteryResource extends CoapResource {
    private static final int THREAD_POOL_SIZE = 10;
    private static final ExecutorService executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
    private final BatteryRepository batteryRepository;
    private final CaptureRepository captureRepository;
    
    public BatteryResource(BatteryRepository batteryRepository, CaptureRepository captureRepository) { // Fixed constructor name
        super("battery");
        getAttributes().setTitle("Battery Resource");
        this.batteryRepository = batteryRepository;
        this.captureRepository = captureRepository;
    }
    
    private boolean processBattery(CBORObject batteryElement, String nodeId, int index) {
        Double battLvl;
        Double battHlth;

        battLvl = batteryElement.get("level") != null ? batteryElement.get("level").AsDouble() : null;
        battHlth = batteryElement.get("health") != null ? batteryElement.get("health").AsDouble() : null;
        
        if (battLvl == null) {
            System.err.println("Invalid battery level for battery " + index + ": null");
            return false;
        }
        
        if (battLvl < 0 || battLvl > 100) {
            System.err.println("Invalid battery level for battery " + index + ": " + battLvl);
            return false;
        }
        
        if (battHlth == null) {
            System.err.println("Invalid battery health for battery " + index + ": null");
            return false;
        }
        
        if (battHlth < 0 || battHlth > 100) {
            System.err.println("Invalid battery health for battery " + index + ": " + battHlth);
            return false;
        }
        
        try {
            
            LocalDateTime timestamp = LocalDateTime.ofInstant(Instant.now(), ZoneOffset.UTC);
                    
            // Create and save capture to get the auto-generated captureId
            Capture capture = new Capture(timestamp); // You may need to calculate response time
            Capture savedCapture = captureRepository.saveCapture(capture); // This should return the saved entity with ID
            Long captureId = savedCapture.getCaptureId();
            
            // Fixed constructor call to match your Battery entity
            Battery battery = new Battery(captureId, nodeId, battLvl, battHlth);
            batteryRepository.saveBattery(battery);
            
            System.out.println("Saved battery " + index + " for node " + nodeId + 
                             " - Level: " + battLvl + "%, Health: " + battHlth + "%");
            return true;
        } catch (Exception e) {
            System.err.println("Error saving battery " + index + ": " + e.getMessage());
            return false;
        }
    }
    
    @Override
    public void handlePOST(CoapExchange exchange) {
        byte[] payload = exchange.getRequestPayload();
        
        try {
            CBORObject received = CBORObject.DecodeFromBytes(payload);
            
            // Extract nodeId from the correct field name
            String nodeId = received.get("nodeId") != null ? received.get("nodeId").AsString() : null;
            System.out.println("Extracted nodeId: '" + nodeId + "'");
            if (nodeId == null || nodeId.isEmpty()) {
                exchange.respond(CoAP.ResponseCode.BAD_REQUEST, "nodeId cannot be null or empty");
                return;
            }
            
            // Extract batteries array
            CBORObject batteriesArray = received.get("batteries");
            if (batteriesArray == null || !batteriesArray.getType().equals(CBORType.Array)) {
                exchange.respond(CoAP.ResponseCode.BAD_REQUEST, "batteries array is required");
                return;
            }
            
            // Respond immediately so client is not blocked
            exchange.respond(CoAP.ResponseCode.CHANGED);
            
            executor.submit(() -> { // Save asynchronously
                try {
                    Integer batteryArraySize = batteriesArray.size();
                    
                    if (batteryArraySize == 0) {
                        System.err.println("Empty batteries array received from node: " + nodeId);
                        return;
                    }
                    
                    for (int i = 0; i < batteryArraySize; i++) {
                        processBattery(batteriesArray.get(i), nodeId, i);
                    }

                } catch (Exception e) {
                    System.err.println("Error processing batteries for node " + nodeId + ": " + e.getMessage());
                    e.printStackTrace();
                }
            });
            
        } catch (Exception e) {
            e.printStackTrace();
            exchange.respond(CoAP.ResponseCode.BAD_REQUEST, "Invalid CBOR payload: " + e.getMessage());
        }
    }
    
    public void shutdown() {
        executor.shutdown();
    }
}