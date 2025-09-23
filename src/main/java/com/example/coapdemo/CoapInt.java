package com.example.coapdemo;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.eclipse.californium.core.coap.CoAP;
import com.upokecenter.cbor.CBORObject;
import java.time.LocalDateTime;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;


public class CoapInt extends CoapResource {

    public CoapInt() {
        super("cbor");
        getAttributes().setTitle("CBOR Resource");
    }

    @Override
    public void handleGET(CoapExchange exchange) {
        exchange.respond(CoAP.ResponseCode.CONTENT);
    }

    @Override
    public void handlePOST(CoapExchange exchange) {
        byte[] payload = exchange.getRequestPayload();

        try {
            CBORObject received = CBORObject.DecodeFromBytes(payload);
            String message = received.get("message").AsString();

            LocalDateTime timestamp = LocalDateTime.ofInstant(Instant.now(), ZoneOffset.UTC);

            HibernateUtil.saveMessage(message, timestamp);

            exchange.respond(CoAP.ResponseCode.CHANGED);
        } catch (Exception e) {
            e.printStackTrace();
            exchange.respond(CoAP.ResponseCode.BAD_REQUEST, "Invalid CBOR payload");
        }
    }
}
