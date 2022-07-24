package com.example.workflow.connectors;

import com.example.workflow.config.ProcessVariableConstants;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class PostgresConnect {

/*    private final String url = "jdbc:postgresql://"+CENTRUM_IP+":5432/set";
    private final String user = USER_DB;
    private final String password = PASS_DB;*/

    public Connection connect(String url, String user, String password) {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url, user, password);
            System.out.println("Connected to the PostgreSQL server successfully.");

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return conn;
    }
}
