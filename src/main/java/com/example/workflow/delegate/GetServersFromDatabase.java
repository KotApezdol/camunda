package com.example.workflow.delegate;

import com.example.workflow.config.ProcessVariableConstants;
import com.example.workflow.connectors.PostgresConnect;
import com.example.workflow.data.Servers;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Objects;


@Component
public class GetServersFromDatabase implements JavaDelegate {
    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        String url = "jdbc:postgresql://172.29.21.238:5432/postgres";
        String allShop = "*";
        String shopNumber = (String) delegateExecution.getVariable(ProcessVariableConstants.SHOP_NUMBER);
        PostgresConnect getServ = new PostgresConnect();
        List<Servers> serversFromDb;

        if(shopNumber == null){
            shopNumber = allShop;
        }

        if(Objects.equals(shopNumber, "*")){
            serversFromDb = getServ.selectServ(url,"postgres","postgres");
        }else{
            serversFromDb = getServ.selectServ(shopNumber,url, "postgres", "postgres");
        }

        int countAllServers = serversFromDb.size();
        int countAllCashes = 0;
        for (Servers serv : serversFromDb){
            int cashes = serv.getCashIP().size();
            countAllCashes += cashes;
        }
        delegateExecution.setVariable("countAllCashes", countAllCashes);
        delegateExecution.setVariable("allShops",allShop);
        delegateExecution.setVariable("countAllServers", countAllServers);
        delegateExecution.setVariable(ProcessVariableConstants.SERVERS_FROM_DB,serversFromDb);

    }
}
