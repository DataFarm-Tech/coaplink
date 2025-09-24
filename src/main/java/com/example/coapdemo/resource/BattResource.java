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

public class BattResource extends CoapResource {

    private static final int THREAD_POOL_SIZE = 10;
    private static final ExecutorService executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

    private final BattRepository battRepository;

    public BattResource(BattRepository battRepository) {
        super("battery");
        getAttributes().setTitle("Battery Resource");
        this.battRepository = battRepository;
    }

    @Override
    public void handlePOST(CoapExchange exchange) {
        byte[] payload = exchange.getRequestPayload();

        try {
            CBORObject received = CBORObject.DecodeFromBytes(payload);

            String nodeId = received.get("nodeId") != null ? received.get("nodeId").AsString() : null;
            Integer battLvl = received.get("level") != null ? received.get("level").AsInt32() : null;
            Integer battHealth = received.get("health") != null ? received.get("health").AsInt32() : null;

            if (nodeId == null || nodeId.isEmpty()) {
                exchange.respond(CoAP.ResponseCode.BAD_REQUEST, "nodeId cannot be null or empty");
                return;
            }

            if (battLvl == null || battLvl < 0 || battLvl > 100) {
                exchange.respond(CoAP.ResponseCode.BAD_REQUEST, "Level must be between 0 and 100");
                return;
            }

            if (battHealth == null || battHealth < 0 || battHealth > 100) {
                exchange.respond(CoAP.ResponseCode.BAD_REQUEST, "Health must be between 0 and 100");
                return;
            }

            // Respond immediately so client is not blocked
            exchange.respond(CoAP.ResponseCode.CHANGED);

            executor.submit(() -> { // Save asynchronously
                /**
                 * Data normalisation etc...
                 */
                LocalDateTime timestamp = LocalDateTime.ofInstant(Instant.now(), ZoneOffset.UTC);
                Batt batt = new Batt(nodeId, battLvl, battHealth, timestamp);
                battRepository.saveBatt(batt);
            });

        } catch (Exception e) {
            e.printStackTrace();
            exchange.respond(CoAP.ResponseCode.BAD_REQUEST, "Invalid CBOR payload");
        }
    }

    public void shutdown() {
        executor.shutdown();
    }
}
