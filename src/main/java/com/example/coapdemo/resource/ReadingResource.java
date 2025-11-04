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

public class ReadingResource extends CoapResource {
    
    private static final int THREAD_POOL_SIZE = 10;
    private static final ExecutorService executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
    private final ReadingService readingService;
    
    public ReadingResource(ReadingService readingService) {
        super("reading");
        this.readingService = readingService;
        getAttributes().setTitle("Reading Resource");
    }

    @Override
    public void handlePOST(CoapExchange exchange) {
        byte[] payload = exchange.getRequestPayload();
        
        try {
            CBORObject received = CBORObject.DecodeFromBytes(payload);

            String nodeId = received.get("node_id") != null ? received.get("node_id").AsString() : null;
                
            if (nodeId == null || nodeId.isEmpty()) {
                exchange.respond(CoAP.ResponseCode.BAD_REQUEST, "nodeId cannot be null or empty");
                return;
            }
            
            CBORObject readingsArray = received.get("readings");
        
            if (readingsArray == null || !readingsArray.getType().equals(CBORType.Array)) {
                exchange.respond(CoAP.ResponseCode.BAD_REQUEST, "readings array is required");
                return;
            }

            exchange.respond(CoAP.ResponseCode.CHANGED);
            
            executor.submit(() -> { // Save asynchronously
                
                try {
                    //validation
                    Integer readingsArraySize = readingsArray.size();
                    
                    if (readingsArraySize == 0) {
                        System.err.println("Empty readings array received from node: " + nodeId);
                        return;
                    }
                    
                    for (int i = 0; i < readingsArraySize; i++) {
                        readingService.processReading(readingsArray.get(i), nodeId);
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