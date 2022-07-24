package com.example.workflow.delegate;

import com.example.workflow.config.ProcessVariableConstants;
import com.example.workflow.connectors.PostgresConnect;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

public class SelectCashCount implements JavaDelegate {
    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {

        String centrumIp = (String) delegateExecution.getVariable(ProcessVariableConstants.CENTRUM_IP);
        String passDb = (String) delegateExecution.getVariable(ProcessVariableConstants.PASS_DB);
        String userDb = (String) delegateExecution.getVariable(ProcessVariableConstants.USER_DB);
        String url = "jdbc:postgresql://"+centrumIp+":5432/set";

        String query = "SELECT COUNT(*) FROM cash_cash WHERE status = 'ACTIVE'";
        PostgresConnect respons = new PostgresConnect();
        String result = String.valueOf(respons.selectResult(query, url, userDb, passDb));



        delegateExecution.setVariable(ProcessVariableConstants.CASH_COUNT,result);

    }
}
