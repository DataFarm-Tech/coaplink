package com.example.coapdemo;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.eclipse.californium.core.coap.CoAP;
import com.upokecenter.cbor.CBORObject;
import com.upokecenter.cbor.CBORType;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
            
            // Fast validation - fail fast
            CBORObject nodeIdObj = received.get("node_id");
            if (nodeIdObj == null) {
                exchange.respond(CoAP.ResponseCode.BAD_REQUEST, "nodeId is required");
                return;
            }
            
            CBORObject readingsArray = received.get("readings");
            if (readingsArray == null || readingsArray.getType() != CBORType.Array) {
                exchange.respond(CoAP.ResponseCode.BAD_REQUEST, "readings array is required");
                return;
            }
            
            int readingsArraySize = readingsArray.size();
            if (readingsArraySize == 0) {
                exchange.respond(CoAP.ResponseCode.BAD_REQUEST, "readings array cannot be empty");
                return;
            }

            System.out.println(readingsArraySize);
            
            // Extract string only after validation passes
            String nodeId = nodeIdObj.AsString();
            if (nodeId.isEmpty()) {
                exchange.respond(CoAP.ResponseCode.BAD_REQUEST, "nodeId cannot be empty");
                return;
            }
            
            // Respond immediately
            exchange.respond(CoAP.ResponseCode.CHANGED);
            
            // Process asynchronously
            executor.submit(() -> {
                try {
                    for (int i = 0; i < readingsArraySize; i++) {
                        readingService.processReading(readingsArray.get(i), nodeId);
                    }
                } catch (Exception e) {
                    System.err.println("Error processing readings for node " + nodeId + ": " + e.getMessage());
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