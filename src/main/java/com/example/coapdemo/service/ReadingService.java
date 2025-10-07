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

public class ReadingService {
    private final ReadingRepository readingRepository;
    private final CaptureRepository captureRepository;
    
    public ReadingService(ReadingRepository readingRepository, CaptureRepository captureRepository) {
        this.readingRepository = readingRepository;
        this.captureRepository = captureRepository;
    }
    
    public void processReading(CBORObject readingElement, String nodeId, int index) {
        Double temperature = readingElement.get("temperature") != null ? readingElement.get("temperature").AsDouble() : null;
        Double pH = readingElement.get("ph") != null ? readingElement.get("ph").AsDouble() : null;
        
        try {
            LocalDateTime timestamp = LocalDateTime.ofInstant(Instant.now(), ZoneOffset.UTC);
            Capture capture = new Capture(timestamp);
            Capture savedCapture = captureRepository.saveCapture(capture);
            Long captureId = savedCapture.getCaptureId();

            Reading new_reading = new Reading(captureId, nodeId, "temperature", temperature, timestamp);


            readingRepository.saveReading(new_reading);
            return;
        } catch (Exception e) {
            System.err.println("Error saving battery " + index + ": " + e.getMessage());
            return;
        }
    }
}