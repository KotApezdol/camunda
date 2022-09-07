package com.example.workflow.delegate;

import com.example.workflow.config.ProcessVariableConstants;
import com.example.workflow.connectors.PostgresConnect;
import com.example.workflow.data.Servers;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class CheckConnectionServers implements JavaDelegate {
    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        List<Servers> trueServers = new ArrayList<>();
        List<Servers> filedServers = new ArrayList<>();
        List<Servers> allServers = (List<Servers>) delegateExecution.getVariable(ProcessVariableConstants.SERVERS_FROM_DB);
        String url = "jdbc:postgresql://172.29.21.238:5432/clients";
        PostgresConnect checkServ = new PostgresConnect();
        for (Servers serv : allServers) {
            try {
                String check = checkServ.CheckServConnect(serv.getServerIP(), url, "postgres", "postgres");
                if (Objects.equals(check, "0")) {
                    trueServers.add(serv);
                }
                else {
                    filedServers.add(serv);
                }
                }catch (Exception e){
                    e.printStackTrace();
                }
        }
        int countTrueServers = trueServers.size();
        int countFiledServers = filedServers.size();

        delegateExecution.setVariable("countTrueServers", countTrueServers);
        delegateExecution.setVariable("filedServers",countFiledServers);
        delegateExecution.setVariable(ProcessVariableConstants.TRUE_SERVERS, trueServers);


    }
}
