package com.example.coapdemo;

import org.eclipse.californium.core.CoapServer;
import org.hibernate.SessionFactory;

public class App {
    public static void main(String[] args) {
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        BattRepository battRepository = new BattRepository(sessionFactory);

        CoapServer server = new CoapServer();
        server.add(new BattResource(battRepository));
        server.start();

        System.out.println("CoAP server is running...");
    }
}
