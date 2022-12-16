package com.example.workflow.data.clients;

import java.io.Serializable;

public class IdAlarm implements Serializable {
    private int shopNumber;
    private int cashNumber;
    private String client;
    private String cmTypeOfData;

    public IdAlarm(){}

    public IdAlarm(int shopNumber, int cashNumber, String client, String cmTypeOfData){
        this.shopNumber = shopNumber;
        this.cashNumber = cashNumber;
        this.client = client;
        this.cmTypeOfData = cmTypeOfData;
    }
}
