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
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

public class ReadingService {
    private final ReadingRepository readingRepository;
    private final CaptureRepository captureRepository;

    public ReadingService(ReadingRepository readingRepository, CaptureRepository captureRepository) {
        this.readingRepository = readingRepository;
        this.captureRepository = captureRepository;
    }

    public void processReadings(CBORObject readingsArray, String nodeId) {
    try {
        List<Double> temperatures = new ArrayList<>();
        List<Double> phValues = new ArrayList<>();

        // Collect valid readings
        int arraySize = readingsArray.size();
        for (int i = 0; i < arraySize; i++) {
            CBORObject reading = readingsArray.get(i);

            Double temp = reading.get("temperature").AsDouble();
            if (temp != null && temp >= 0 && temp <= 100) {
                temperatures.add(temp);
            }

            Double ph = reading.get("ph").AsDouble();
            if (ph != null && ph >= 0 && ph <= 13) {
                phValues.add(ph);
            }
        }

        // Z-score filtering and median calculation for temperature
        DescriptiveStatistics tempStats = new DescriptiveStatistics();
        temperatures.forEach(tempStats::addValue);
        double meanTemp = tempStats.getMean();
        double stdDevTemp = tempStats.getStandardDeviation();

        List<Double> filteredTemps = new ArrayList<>();
        for (Double t : temperatures) {
            if (stdDevTemp == 0 || Math.abs((t - meanTemp) / stdDevTemp) <= 2.0) { // threshold = 2.0
                filteredTemps.add(t);
            }
        }

        double medianTemp = 0;
        if (!filteredTemps.isEmpty()) {
            DescriptiveStatistics filteredTempStats = new DescriptiveStatistics();
            filteredTemps.forEach(filteredTempStats::addValue);
            medianTemp = filteredTempStats.getPercentile(50);
        }

        // Z-score filtering and median calculation for pH
        DescriptiveStatistics phStats = new DescriptiveStatistics();
        phValues.forEach(phStats::addValue);
        double meanPh = phStats.getMean();
        double stdDevPh = phStats.getStandardDeviation();

        List<Double> filteredPh = new ArrayList<>();
        for (Double p : phValues) {
            if (stdDevPh == 0 || Math.abs((p - meanPh) / stdDevPh) <= 2.0) {
                filteredPh.add(p);
            }
        }

        double medianPh = 0;
        if (!filteredPh.isEmpty()) {
            DescriptiveStatistics filteredPhStats = new DescriptiveStatistics();
            filteredPh.forEach(filteredPhStats::addValue);
            medianPh = filteredPhStats.getPercentile(50);
        }

        // Save median temperature
        LocalDateTime timestamp = LocalDateTime.ofInstant(Instant.now(), ZoneOffset.UTC);

        if (!filteredTemps.isEmpty()) {
            Capture tempCapture = new Capture(timestamp);
            Capture savedTempCapture = captureRepository.saveCapture(tempCapture);
            Long tempCaptureId = savedTempCapture.getCaptureId();

            Reading tempReading = new Reading(tempCaptureId, nodeId, "temperature", medianTemp, timestamp);
            readingRepository.saveReading(tempReading);
        }

        // Save median pH
        if (!filteredPh.isEmpty()) {
            Capture phCapture = new Capture(timestamp);
            Capture savedPhCapture = captureRepository.saveCapture(phCapture);
            Long phCaptureId = savedPhCapture.getCaptureId();

            Reading phReading = new Reading(phCaptureId, nodeId, "ph", medianPh, timestamp);
            readingRepository.saveReading(phReading);
        }

    } catch (Exception e) {
        e.printStackTrace();
    }
}
}
