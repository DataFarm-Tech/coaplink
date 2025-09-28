package com.example.coapdemo;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

public class BatteryRepository {

    private final SessionFactory sessionFactory;

    public BatteryRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void saveBattery(Battery battery) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.persist(battery);
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("WRONG");
            // handle exception or rethrow as needed
        }
    }
}
