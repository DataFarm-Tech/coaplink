// package com.example.coapdemo;

// import org.hibernate.Session;
// import org.hibernate.SessionFactory;

// public class CaptureRepository {

//     private final SessionFactory sessionFactory;

//     public CaptureRepository(SessionFactory sessionFactory) {
//         this.sessionFactory = sessionFactory;
//     }

//     public Capture saveCapture(Capture capture) {
//         try (Session session = sessionFactory.openSession()) {
//             session.beginTransaction();
//             session.persist(capture);
//             session.getTransaction().commit();

//         } catch (Exception e) {
//             e.printStackTrace();
//         }
//     }
// }


package com.example.coapdemo;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

public class CaptureRepository {
    private final SessionFactory sessionFactory;
    
    public CaptureRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
    
    public Capture saveCapture(Capture capture) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            
            // persist() will populate the auto-generated ID
            session.persist(capture);
            
            transaction.commit();
            
            // Return the capture entity with the populated ID
            return capture;
            
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            throw new RuntimeException("Failed to save capture", e);
        }
    }
    
    // // Keep the old method for backward compatibility if needed
    // public void saveCapture(Capture capture) {
    //     save(capture);
    // }
}