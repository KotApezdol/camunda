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
public class CheckConnectionCash implements JavaDelegate {
    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        List<Servers> checked = new ArrayList<>();
        List<Servers> trueServers = (List<Servers>) delegateExecution.getVariable(ProcessVariableConstants.TRUE_SERVERS);
        String url = "jdbc:postgresql://172.29.21.238:5432/clients";
        PostgresConnect checkCash = new PostgresConnect();
        for (Servers serv : trueServers) {
            Servers checkedServ = new Servers();
            ArrayList<String> checkedCashes = new ArrayList<>();
            String cashes = String.join(",",serv.getCashIP());

                try {
                    checkedCashes = checkCash.CheckCashConnect(cashes, url, "postgres", "postgres");
                }catch (Exception e){
                    e.printStackTrace();
                }
            checkedServ.setShopNumber(serv.getShopNumber());
            checkedServ.setServerIP(serv.getServerIP());
            checkedServ.setCashIP(checkedCashes);
            checked.add(checkedServ);
        }

        int countTrueCashes = 0;
        for (Servers serv : checked){
            int cashes = serv.getCashIP().size();
            countTrueCashes += cashes;
        }

        delegateExecution.setVariable("countTrueCashes", countTrueCashes);
        delegateExecution.setVariable(ProcessVariableConstants.CHECKED, checked);

    }
}
