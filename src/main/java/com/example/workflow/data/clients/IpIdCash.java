package com.example.workflow.data.clients;

import java.io.Serializable;

public class IpIdCash implements Serializable {

    private int shopNumber;
    private int cashNumber;
    private int clientId;

    public IpIdCash(){}

    public IpIdCash(int shopNumber, int cashNumber, int clientId){
        this.shopNumber = shopNumber;
        this.cashNumber = cashNumber;
        this.clientId = clientId;
    }
}
