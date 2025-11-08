package com.example.coapdemo;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

public class ActivateRepository {

    private final SessionFactory sessionFactory;

    public ActivateRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    // Save node
    public void saveActivate(ActiveNode nodeToActivate) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.persist(nodeToActivate);
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Get node by nodeId only
    public ActiveNode getNodeByNodeId(String nodeId) {
        try (Session session = sessionFactory.openSession()) {
            Query<ActiveNode> query = session.createQuery(
                "FROM ActiveNode WHERE nodeId = :nodeId", ActiveNode.class);
            query.setParameter("nodeId", nodeId);
            return query.uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
