package com.example.coapdemo;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

public class BattRepository {

    private final SessionFactory sessionFactory;

    public BattRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void saveBatt(Batt batt) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.persist(batt);
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("WRONG");
            // handle exception or rethrow as needed
        }
    }
}
