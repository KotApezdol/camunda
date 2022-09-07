package com.example.workflow.delegate;

import com.example.workflow.bot.CamundaBot;
import com.example.workflow.config.ProcessVariableConstants;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@Component
public class SendToTelegram implements JavaDelegate {
    CamundaBot bot;
    public SendToTelegram (CamundaBot bot){
        this.bot = bot;
    }
    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {

        String textToTelegram = (String) delegateExecution.getVariable(ProcessVariableConstants.TEXT_TO_TELEGRAM);
        String  idChat = (String) delegateExecution.getVariable(ProcessVariableConstants.ID_CHAT);
        bot.sendToTelegram(idChat,textToTelegram);
    }
}
