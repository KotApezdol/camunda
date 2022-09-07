package com.example.workflow.bot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

public class MessageSander {
    CamundaBot bot;

    String chartId;

    public MessageSander (CamundaBot bot){
        this.bot = bot;
    }
}
