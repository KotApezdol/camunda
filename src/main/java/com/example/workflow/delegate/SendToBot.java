package com.example.workflow.delegate;

import com.example.workflow.bot.CamundaBot;
import com.example.workflow.config.ProcessVariableConstants;
import com.example.workflow.data.Servers;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;


import java.util.List;

@Component
public class SendToBot implements JavaDelegate {
    CamundaBot bot;
    public SendToBot (CamundaBot bot){
        this.bot = bot;
    }
    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {

        int countAllServers = (int) delegateExecution.getVariable("countAllServers");
        int countUploaded = (int) delegateExecution.getVariable("countUploaded");
        int countFiled = (int) delegateExecution.getVariable("countFiled");
        int countFiledServers = (int) delegateExecution.getVariable("filedServers");
        bot.sendCamunda(String.valueOf(countFiledServers), String.valueOf(countAllServers), String.valueOf(countUploaded),String.valueOf(countFiled));

    }
}
