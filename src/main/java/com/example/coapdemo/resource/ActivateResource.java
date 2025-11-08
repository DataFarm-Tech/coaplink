package com.example.coapdemo;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.eclipse.californium.core.coap.CoAP;
import com.upokecenter.cbor.CBORObject;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ActivateResource extends CoapResource {

    private static final int THREAD_POOL_SIZE = 10;
    private static final ExecutorService executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

    private final ActivateService activateService;

    public ActivateResource(ActivateService activateService) {
        super("activate");
        this.activateService = activateService;
        getAttributes().setTitle("Activate Resource");
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

            // Fixed typo: .AsString → .AsString()
            String gpsCoor = received.get("gps") != null ? received.get("gps").AsString() : null;
            if (gpsCoor == null || gpsCoor.isEmpty()) {
                exchange.respond(CoAP.ResponseCode.BAD_REQUEST, "gps cannot be null or empty");
                return;
            }

            exchange.respond(CoAP.ResponseCode.CHANGED);

            executor.submit(() -> {
                activateService.processActivate(nodeId, gpsCoor);
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
