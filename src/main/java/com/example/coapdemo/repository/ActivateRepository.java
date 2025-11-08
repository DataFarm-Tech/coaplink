package com.example.coapdemo;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

/**
 * @brief The following class refers to the battery interface
 * to the DB.
 */
public class ActivateRepository {

    private final SessionFactory sessionFactory;

    public ActivateRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
    
    /**
     * @brief Saves an ActiveNode entry in the DB.
     */
    public void saveActivate(ActiveNode nodeToActivate) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.persist(nodeToActivate);
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
