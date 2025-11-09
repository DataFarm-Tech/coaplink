package com.example.coapdemo;
import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.eclipse.californium.core.coap.CoAP;
import com.upokecenter.cbor.CBORObject;
import com.upokecenter.cbor.CBORType;
import java.time.LocalDateTime;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

public class ReadingService {
    private final ReadingRepository readingRepository;
    private final CaptureRepository captureRepository;

    public ReadingService(ReadingRepository readingRepository, CaptureRepository captureRepository) {
        this.readingRepository = readingRepository;
        this.captureRepository = captureRepository;
    }

    public void processReading(CBORObject readingElement, String nodeId) {
        // This method is now just for compatibility - actual processing happens in processReadings
        System.out.println("Single reading processed (deprecated)");
    }

    public void processReadings(CBORObject readingsArray, String nodeId) {
        
        try {
            // Collect all temperature and pH values
            List<Double> temperatures = new ArrayList<>();
            List<Double> phValues = new ArrayList<>();
            
            int arraySize = readingsArray.size();
            for (int i = 0; i < arraySize; i++) {
                CBORObject reading = readingsArray.get(i);
                
                Double temp = reading.get("temperature").AsDouble();

                if (temp != null && (temp < 0 || temp > 100)) {
                    temperatures.add(temp);
                }

                Double ph = reading.get("ph").AsDouble();

                if (ph != null && (ph < 0 || ph > 13)) {
                    phValues.add(ph); //TODO: double cbeck this
                }
            }
            
            // Calculate medians
            Double medianTemp = calculateMedian(temperatures);
            Double medianPh = calculateMedian(phValues);
            
            LocalDateTime timestamp = LocalDateTime.ofInstant(Instant.now(), ZoneOffset.UTC);
            
            // Save median temperature with its own capture
            if (medianTemp != null) {
                Capture tempCapture = new Capture(timestamp);
                Capture savedTempCapture = captureRepository.saveCapture(tempCapture);
                Long tempCaptureId = savedTempCapture.getCaptureId();
                
                Reading tempReading = new Reading(tempCaptureId, nodeId, "temperature", medianTemp, timestamp);
                readingRepository.saveReading(tempReading);
                System.out.println("Saved median temperature for node " + nodeId + ": " + medianTemp);
            }
            
            // Save median pH with its own capture
            if (medianPh != null) {
                Capture phCapture = new Capture(timestamp);
                Capture savedPhCapture = captureRepository.saveCapture(phCapture);
                Long phCaptureId = savedPhCapture.getCaptureId();
                
                Reading phReading = new Reading(phCaptureId, nodeId, "ph", medianPh, timestamp);
                readingRepository.saveReading(phReading);
                System.out.println("Saved median pH for node " + nodeId + ": " + medianPh);
            }
            
        } catch (Exception e) {
            System.err.println("Error processing readings: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private Double calculateMedian(List<Double> values) {
        if (values == null || values.isEmpty()) {
            return null;
        }
        
        // Sort the values
        Collections.sort(values);
        
        int size = values.size();
        if (size % 2 == 0) {
            // Even number - average the two middle values
            return (values.get(size / 2 - 1) + values.get(size / 2)) / 2.0;
        } else {
            // Odd number - return the middle value
            return values.get(size / 2);
        }
    }
}