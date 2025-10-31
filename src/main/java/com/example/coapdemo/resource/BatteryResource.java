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
    private final BatteryService batteryService;
    
    public BatteryResource(BatteryService batteryService) {
        super("battery");
        this.batteryService = batteryService;
        getAttributes().setTitle("Battery Resource");
    }

    @Override
    public void handlePOST(CoapExchange exchange) {
        System.out.println("new msg");
	    byte[] payload = exchange.getRequestPayload();
        
        try {
            CBORObject received = CBORObject.DecodeFromBytes(payload);

            String nodeId = received.get("node_id") != null ? received.get("node_id").AsString() : null;
            if (nodeId == null || nodeId.isEmpty()) {
                exchange.respond(CoAP.ResponseCode.BAD_REQUEST, "nodeId cannot be null or empty");
                return;
            }

            CBORObject batteriesArray = received.get("batteries");
            
            if (batteriesArray == null || !batteriesArray.getType().equals(CBORType.Array)) {
                exchange.respond(CoAP.ResponseCode.BAD_REQUEST, "batteries array is required");
                return;
            }

            exchange.respond(CoAP.ResponseCode.CHANGED);
            
            executor.submit(() -> { // Save asynchronously
                try {
                    Integer batteryArraySize = batteriesArray.size();
                    
                    if (batteryArraySize == 0) {
                        System.err.println("Empty batteries array received from node: " + nodeId);
                        return;
                    }
                    
                    for (int i = 0; i < batteryArraySize; i++) {
                        batteryService.processBattery(batteriesArray.get(i), nodeId, i);
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
