package com.example.coapdemo;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.eclipse.californium.core.coap.CoAP;
import com.upokecenter.cbor.CBORObject;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.commons.validator.routines.DoubleValidator;


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
            if (nodeId == null || nodeId.isEmpty() || (nodeID.length != 6)) {
                exchange.respond(CoAP.ResponseCode.BAD_REQUEST, "nodeId cannot be null or empty");
                return;
            }

            String gpsCoor = received.get("gps") != null ? received.get("gps").AsString() : null;
            if (gpsCoor == null || gpsCoor.isEmpty()) {
                exchange.respond(CoAP.ResponseCode.BAD_REQUEST, "gps cannot be null or empty");
                return;
            }

            if (!isValidGps(gpsCoor)) {
                exchange.respond(CoAP.ResponseCode.BAD_REQUEST, 
                    "Invalid GPS coordinates. Expected \"lat,lon\" with valid ranges.");
                return;
            }

            // CBOR "key" is a byte string on ESP32
            byte[] key = received.get("key") != null ? received.get("key").GetByteString() : null;
            if (key == null || key.length == 0) {
                exchange.respond(CoAP.ResponseCode.BAD_REQUEST, "invalid key");
                return;
            }

            // Respond immediately
            exchange.respond(CoAP.ResponseCode.CHANGED);

            // Process asynchronously
            executor.submit(() -> {
                boolean success = activateService.processActivate(nodeId, gpsCoor, key);
                if (!success) {
                    System.out.println("Node " + nodeId + " already exists, skipping activation.");
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


private static final DoubleValidator doubleValidator = DoubleValidator.getInstance();

private boolean isValidGps(String gps) {
    String[] parts = gps.split(",");
    if (parts.length != 2) return false;

    Double lat = doubleValidator.validate(parts[0].trim());
    Double lon = doubleValidator.validate(parts[1].trim());

    if (lat == null || lon == null) {
        return false;
    }

    return lat >= -90 && lat <= 90 && lon >= -180 && lon <= 180;
}
