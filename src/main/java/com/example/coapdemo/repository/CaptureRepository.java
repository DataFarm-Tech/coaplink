package com.example.coapdemo;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

/**
 * @brief The following class refers to the capture interface
 * to the DB.
 */
public class CaptureRepository {
    private final SessionFactory sessionFactory;
    
    public CaptureRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
    
    /**
     * @brief The following method saves a capture
     * to the DB.
     */
    public Capture saveCapture(Capture capture) {
        Transaction transaction = null;
        
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.persist(capture); // persist() will populate the auto-generated ID
            transaction.commit();
            return capture;
            
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            throw new RuntimeException("Failed to save capture", e);
        }
    }
}