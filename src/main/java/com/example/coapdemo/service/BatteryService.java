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

public class BatteryService {
    private final BatteryRepository batteryRepository;
    private final CaptureRepository captureRepository;
    
    public BatteryService(BatteryRepository batteryRepository, CaptureRepository captureRepository) {
        this.batteryRepository = batteryRepository;
        this.captureRepository = captureRepository;
    }
    
    public boolean processBattery(CBORObject batteryElement, String nodeId, int index) {
        Double battLvl;
        Double battHlth;
        
        battLvl = batteryElement.get("bat_lvl") != null ? batteryElement.get("bat_lvl").AsDouble() : null;
        battHlth = batteryElement.get("bat_hlth") != null ? batteryElement.get("bat_hlth").AsDouble() : null;
        
        if (battLvl == null) {
            System.err.println("Invalid battery level for battery " + index + ": null");
            return false;
        }
        if (battLvl < 0 || battLvl > 100) {
            System.err.println("Invalid battery level for battery " + index + ": " + battLvl);
            return false;
        }
        if (battHlth == null) {
            System.err.println("Invalid battery health for battery " + index + ": null");
            return false;
        }
        if (battHlth < 0 || battHlth > 100) {
            System.err.println("Invalid battery health for battery " + index + ": " + battHlth);
            return false;
        }
        
        try {
            LocalDateTime timestamp = LocalDateTime.ofInstant(Instant.now(), ZoneOffset.UTC);
            Capture capture = new Capture(timestamp);
            Capture savedCapture = captureRepository.saveCapture(capture);
            Long captureId = savedCapture.getCaptureId();
            Battery battery = new Battery(captureId, nodeId, battLvl, battHlth);
            batteryRepository.saveBattery(battery);
            return true;
        } catch (Exception e) {
            System.err.println("Error saving battery " + index + ": " + e.getMessage());
            return false;
        }
    }
}