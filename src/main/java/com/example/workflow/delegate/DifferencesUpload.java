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
public class DifferencesUpload implements JavaDelegate {
    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        List<String > uploaded = new ArrayList<>();
        List<Servers> servers = (List<Servers>) delegateExecution.getVariable(ProcessVariableConstants.CHECKED);
        PostgresConnect connect = new PostgresConnect();
        String urlP = "jdbc:postgresql://172.29.21.238:5432/postgres";
        String urlC = "jdbc:postgresql://172.29.21.238:5432/clients";
        for(Servers serv : servers){
            String cashes = "";
            for (String cash : serv.getCashIP()){
                cashes += "'"+cash+"'";
            }
            List<String > mrc = connect.getListMrc(serv.getShopNumber(), cashes,urlP,"postgres", "postgres");
            for (String gMrc : mrc){
                String upload = connect.sendMrc(serv.getServerIP(),serv.getShopNumber(),gMrc,urlC,"postgres", "postgres");
                if (Objects.equals(upload, "0")){
                    uploaded.add(serv.getShopNumber());
                }
            }
        }
        int countUploaded = uploaded.size();
        delegateExecution.setVariable(ProcessVariableConstants.UPLOADED_SHOPS,countUploaded);

    }
}