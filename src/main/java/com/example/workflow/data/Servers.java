package com.example.workflow.data;

import java.io.Serializable;
import java.util.ArrayList;

public class Servers implements Serializable {

    String shopNumber;
    String serverIP;
    ArrayList<String> cashIP;
    public String getServerIP() {
        return serverIP;
    }
    public void setServerIP(String serverIP) {
        this.serverIP = serverIP;
    }
    public ArrayList<String> getCashIP() {
        return cashIP;
    }
    public void setCashIP(ArrayList<String> cashIP) {
        this.cashIP = cashIP;
    }
    public String getShopNumber() {
        return shopNumber;
    }
    public void setShopNumber(String shopNumber) {
        this.shopNumber = shopNumber;
    }
}
