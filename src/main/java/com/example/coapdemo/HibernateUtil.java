package com.example.coapdemo;

import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.Session;

import java.time.LocalDateTime;

public class HibernateUtil {
    private static final SessionFactory sessionFactory = buildSessionFactory();

    private static SessionFactory buildSessionFactory() {
        try {
            Configuration config = new Configuration();

            // JDBC and DB configuration
            config.setProperty("hibernate.connection.driver_class", "com.mysql.cj.jdbc.Driver");
            config.setProperty("hibernate.connection.url", "jdbc:mysql://45.79.239.100:3306/df_dev");
            config.setProperty("hibernate.connection.username", "root");
            config.setProperty("hibernate.connection.password", "RYy3GziqsVPPeP7abMzHZfSDj7DDKAX4vVMMfo");
            config.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");

            // HikariCP settings (optional)
            config.setProperty("hibernate.hikari.minimumIdle", "2");
            config.setProperty("hibernate.hikari.maximumPoolSize", "20");
            config.setProperty("hibernate.hikari.idleTimeout", "300000");
            config.setProperty("hibernate.hikari.connectionTimeout", "2000");
            config.setProperty("hibernate.hikari.poolName", "MyHikariCP");

            // Hibernate behavior
            config.setProperty("hibernate.hbm2ddl.auto", "none");
            config.setProperty("hibernate.show_sql", "false");
            config.setProperty("hibernate.format_sql", "false");

            // Performance tuning
            config.setProperty("hibernate.jdbc.batch_size", "20");
            config.setProperty("hibernate.order_inserts", "true");
            config.setProperty("hibernate.order_updates", "true");

            // Cache settings (optional)
            config.setProperty("hibernate.cache.use_second_level_cache", "true");
            config.setProperty("hibernate.cache.region.factory_class", "org.hibernate.cache.ehcache.EhCacheRegionFactory");
            config.setProperty("hibernate.cache.use_query_cache", "false");

            // Register entity
            config.addAnnotatedClass(Battery.class);
            config.addAnnotatedClass(Capture.class);
            config.addAnnotatedClass(Reading.class);

            return config.buildSessionFactory(
                new StandardServiceRegistryBuilder().applySettings(config.getProperties()).build()
            );

        } catch (Exception ex) {
            throw new ExceptionInInitializerError("Initial SessionFactory creation failed: " + ex);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}
