package com.example.workflow.config;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;

public class HibernateUtil {

    private static final SessionFactory postgresSessionFactory = initPostgresSessionFactory();
    private static final SessionFactory clientsSessionFactory = initClientsSessionFactory();
    private static SessionFactory initPostgresSessionFactory() {
        try {
            return new  Configuration()
                    .configure("hibernate.postgres.cfg.xml")
                    .buildSessionFactory();
        }
        catch (Throwable ex) {
            System.err.println("Initial SessionFactory creation failed." + ex);
            throw new ExceptionInInitializerError(ex);
        }

    }

    private static SessionFactory initClientsSessionFactory() {
        try {
            return new  Configuration()
                    .configure("hibernate.clients.cfg.xml")
                    .buildSessionFactory();
        }
        catch (Throwable ex) {
            System.err.println("Initial SessionFactory creation failed." + ex);
            throw new ExceptionInInitializerError(ex);
        }

    }
    public static SessionFactory getPostgresSessionFactory() {

        if (postgresSessionFactory == null){
            initPostgresSessionFactory();
        }

        return postgresSessionFactory;
    }

    public static SessionFactory getClientsSessionFactory() {

        if (clientsSessionFactory == null){
            initClientsSessionFactory();
        }

        return clientsSessionFactory;
    }

    public static void postgresClose() {
        getPostgresSessionFactory().close();
    }

    public static void clientsClose() {
        getClientsSessionFactory().close();
    }


}
