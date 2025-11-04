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
import java.util.HashMap;
import java.util.Map;

public class ReadingService {
    private final ReadingRepository readingRepository;
    private final CaptureRepository captureRepository;
    
    public ReadingService(ReadingRepository readingRepository, CaptureRepository captureRepository) {
        this.readingRepository = readingRepository;
        this.captureRepository = captureRepository;
    }

    public void processReading(CBORObject readingElement, String nodeId) {
        Map<String, Double> readings = new HashMap<>();
        readings.put("temperature", readingElement.get("temperature") != null ? readingElement.get("temperature").AsDouble() : null);
        readings.put("ph", readingElement.get("ph") != null ? readingElement.get("ph").AsDouble() : null);

        try {
            for (Map.Entry<String, Double> entry : readings.entrySet()) {
                if (entry.getValue() != null) {
                    LocalDateTime timestamp = LocalDateTime.ofInstant(Instant.now(), ZoneOffset.UTC);
                    Capture capture = new Capture(timestamp);
                    Capture savedCapture = captureRepository.saveCapture(capture);
                    Long captureId = savedCapture.getCaptureId();
                    
                    Reading reading = new Reading(captureId, nodeId, entry.getKey(), entry.getValue(), timestamp);
                    readingRepository.saveReading(reading);
                }
            }
        } catch (Exception e) {
            System.err.println("Error saving reading: " + e.getMessage());
        }
    }
}