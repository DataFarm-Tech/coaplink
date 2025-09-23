package com.example.coapdemo;

import org.eclipse.californium.core.CoapServer;

public class App {
    public static void main(String[] args) {
        // Initialize Hibernate SessionFactory early
        HibernateUtil.getSessionFactory();

        // Start CoAP server
        CoapServer server = new CoapServer();
        server.add(new CoapInt()); // Your CBOR resource
        server.start();

        System.out.println("CoAP server is running...");
    }
}

