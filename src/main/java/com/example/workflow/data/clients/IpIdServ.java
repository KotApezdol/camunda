package com.example.workflow.data.clients;

import java.io.Serializable;

public class IpIdServ implements Serializable {
    private int shopNumber;
    private int clientId;

    public IpIdServ(){}

    public IpIdServ(int shopNumber,int clientId){
        this.shopNumber = shopNumber;
        this.clientId = clientId;
    }

}
