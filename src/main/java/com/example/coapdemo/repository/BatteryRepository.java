package com.example.coapdemo;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

/**
 * @brief The following class refers to the battery interface
 * to the DB.
 */
public class BatteryRepository {

    private final SessionFactory sessionFactory;

    public BatteryRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
    
    /**
     * @brief The following method saves a battery
     * entry in the DB.
     */
    public void saveBattery(Battery battery) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.persist(battery);
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
