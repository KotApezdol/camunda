package com.example.workflow.connectors;

import java.sql.*;


public class PostgresConnect {

    public void connect(String url, String user, String password) {
       // Connection conn = null;
        try {
            Connection conn = DriverManager.getConnection(url, user, password);
            System.out.println("Connected to the PostgreSQL server successfully.");

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }

    public boolean testConnect(String url, String user, String password) {
        boolean connect = false;
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url, user, password);
            System.out.println("Connected to the PostgreSQL server successfully.");

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        if(conn != null){
            connect = true;
        }
        return connect;
    }
    public ResultSet selectResult(String query, String url, String user, String password) throws SQLException {
        Connection c = DriverManager.getConnection(url, user, password);
        Statement s = c.createStatement();
        return s.executeQuery(query);
    }
}
