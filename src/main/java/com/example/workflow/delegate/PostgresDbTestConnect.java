package com.example.workflow.delegate;

import com.example.workflow.config.ProcessVariableConstants;
import com.example.workflow.connectors.PostgresConnect;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@Component
public class PostgresDbTestConnect implements JavaDelegate {
    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        String centrumIp = (String) delegateExecution.getVariable(ProcessVariableConstants.CENTRUM_IP);
        String passDb = (String) delegateExecution.getVariable(ProcessVariableConstants.PASS_DB);
        String userDb = (String) delegateExecution.getVariable(ProcessVariableConstants.USER_DB);
        String url = "jdbc:postgresql://"+centrumIp+":5432/set";
        boolean isDbConnect = false;

        PostgresConnect test = new PostgresConnect();
        if(test.testConnect(url, userDb, passDb)) isDbConnect = true;

        delegateExecution.setVariable((ProcessVariableConstants.IS_DB_CONNECT), isDbConnect);

    }
}
