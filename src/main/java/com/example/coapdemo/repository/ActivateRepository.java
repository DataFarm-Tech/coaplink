package com.example.coapdemo;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

public class ActivateRepository {

    private final SessionFactory sessionFactory;

    public ActivateRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    /**
     * Save node to DB.
     */
    public void saveActivate(ActiveNode node) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.persist(node);
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Get node by nodeId.
     */
    public ActiveNode getNodeByNodeId(String nodeId) {
        try (Session session = sessionFactory.openSession()) {
            return session.get(ActiveNode.class, nodeId);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
