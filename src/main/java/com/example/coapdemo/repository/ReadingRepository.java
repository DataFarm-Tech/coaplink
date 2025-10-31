package com.example.coapdemo;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

/**
 * @brief The following class refers to the battery interface
 * to the DB.
 */
public class ReadingRepository {

    private final SessionFactory sessionFactory;

    public ReadingRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
    
    /**
     * @brief The following method saves a battery
     * entry in the DB.
     */
    public void saveReading(Reading reading) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.persist(reading);
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
