package com.example.workflow.connectors;

import com.example.workflow.data.Servers;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


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
    public String selectResult(String query, String url, String user, String password) throws SQLException {
        String result = "Скрипт не сработал";
        Connection c = DriverManager.getConnection(url, user, password);
        Statement s = c.createStatement();
        ResultSet r = s.executeQuery(query);
        while (r.next()){
            result = r.getString(1);
        }
        return result;
    }

    public List<Servers> selectServ(String url, String user, String password) throws SQLException {
        List<Servers> servers = new ArrayList<>();
        Connection c = DriverManager.getConnection(url, user, password);
        Statement s = c.createStatement();
        String servQuery = "select DISTINCT shop_number, shop_ip from data_control_mrc_alco_check_62 WHERE cm_type_of_data in (1,2,3,4) and product_marking is not null ORDER BY shop_number ASC";
        ResultSet servIp = s.executeQuery(servQuery);
            while (servIp.next()) {
                Servers server = new Servers();
                ArrayList<String> cashes = new ArrayList<>();
                server.setServerIP(servIp.getString(2));
                String cashQuery = "select DISTINCT cash_ip from data_control_mrc_alco_check_62 WHERE shop_ip = '" + servIp.getString(2) + "' and cm_type_of_data in (1,2,3,4) and product_marking is not null ORDER BY cash_ip ASC";
                Statement s2 = c.createStatement();
                ResultSet cashIp = s2.executeQuery(cashQuery);
                while (cashIp.next()) {
                    cashes.add(cashIp.getString(1));
                }
                server.setCashIP(cashes);
                servers.add(server);
            }
        return servers;
    }
    public List<Servers> selectServ(String shop, String url, String user, String password) throws SQLException {
        List<Servers> servers = new ArrayList<>();
        Connection c = DriverManager.getConnection(url, user, password);
        Statement s = c.createStatement();
        String servQuery = "select DISTINCT shop_number, shop_ip from data_control_mrc_alco_check_62 WHERE shop_number in ("+shop+") and cm_type_of_data in (1,2,3,4) and product_marking is not null ORDER BY shop_number ASC";
        ResultSet servIp = s.executeQuery(servQuery);
        while (servIp.next()) {
            Servers server = new Servers();
            ArrayList<String> cashes = new ArrayList<>();
            server.setServerIP(servIp.getString(2));
            String cashQuery = "select DISTINCT cash_ip from data_control_mrc_alco_check_62 WHERE shop_ip = '" + servIp.getString(2) + "' and cm_type_of_data in (1,2,3,4) and product_marking is not null ORDER BY cash_ip ASC";
            Statement s2 = c.createStatement();
            ResultSet cashIp = s2.executeQuery(cashQuery);
            while (cashIp.next()) {
                cashes.add(cashIp.getString(1));
            }
            server.setCashIP(cashes);
            servers.add(server);
        }
        return servers;
    }

    public String CheckServConnect(String ip, String url, String user, String password) throws SQLException {
        String result = "Скрипт не сработал";
        String query = "SELECT get_retail_db_status(62,'"+ip+"');";
        Connection c = DriverManager.getConnection(url, user, password);
        Statement s = c.createStatement();
        ResultSet r = s.executeQuery(query);
        while (r.next()){
            result = r.getString(1);
        }
        return result;
    }

    public String CheckCashConnect(String ip, String url, String user, String password) throws SQLException {
        String result = "Скрипт не сработал";
        String query = "SELECT get_cash_db_status(62,'"+ip+"');";
        Connection c = DriverManager.getConnection(url, user, password);
        Statement s = c.createStatement();
        ResultSet r = s.executeQuery(query);
        while (r.next()){
            result = r.getString(1);
        }
        return result;
    }

}
