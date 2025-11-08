package com.example.coapdemo;

import org.eclipse.californium.core.CoapServer;
import org.hibernate.SessionFactory;

public class App {

    public static void main(String[] args) {
        // Initialize Hibernate
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();

        // Initialize repositories
        BatteryRepository batteryRepository = new BatteryRepository(sessionFactory);
        CaptureRepository captureRepository = new CaptureRepository(sessionFactory);
        ReadingRepository readingRepository = new ReadingRepository(sessionFactory);
        ActivateRepository activateRepository = new ActivateRepository(sessionFactory); // fixed

        // Initialize services
        BatteryService batteryService = new BatteryService(batteryRepository, captureRepository);
        ReadingService readingService = new ReadingService(readingRepository, captureRepository);
        ActivateService activateService = new ActivateService(activateRepository, captureRepository); // fixed

        // Initialize CoAP server
        CoapServer server = new CoapServer();

        // Add resources
        server.add(new BatteryResource(batteryService));
        server.add(new ReadingResource(readingService));
        server.add(new ActivateResource(activateService)); // added

        // Start server
        server.start();
        System.out.println("CoAP server is running...");
    }
}
