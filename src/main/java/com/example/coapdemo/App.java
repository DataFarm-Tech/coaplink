package com.example.coapdemo;
import org.eclipse.californium.core.CoapServer;
import org.hibernate.SessionFactory;

public class App {
    public static void main(String[] args) {
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();

        BatteryRepository batteryRepository = new BatteryRepository(sessionFactory);
        CaptureRepository captureRepository = new CaptureRepository(sessionFactory);
        ReadingRepository readingRepository = new ReadingRepository(sessionFactory);
        
        BatteryService batteryService = new BatteryService(batteryRepository, captureRepository);
        ReadingService readingService = new ReadingService(readingRepository, captureRepository);
        
        CoapServer server = new CoapServer();
        server.add(new BatteryResource(batteryService));
        server.add(new ReadingResource(readingService));
        server.start();
        System.out.println("CoAP server is running...");
    }
}