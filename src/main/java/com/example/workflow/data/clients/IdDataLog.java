package com.example.workflow.data.clients;

import java.io.Serializable;

public class IdDataLog implements Serializable {
    private int shopNumber;
    private int cashNumber;
    private String client;

    public IdDataLog(){}

    public IdDataLog(int shopNumber, int cashNumber, String client){
        this.shopNumber = shopNumber;
        this.cashNumber = cashNumber;
        this.client = client;
    }
}
