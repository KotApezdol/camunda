package com.example.workflow.delegate;

import com.example.workflow.bot.CamundaBot;
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
        List<String > filed = new ArrayList<>();
        List<Servers> servers = (List<Servers>) delegateExecution.getVariable(ProcessVariableConstants.CHECKED);
        PostgresConnect connect = new PostgresConnect();
        String urlP = "jdbc:postgresql://172.29.21.238:5432/postgres";
        String urlC = "jdbc:postgresql://172.29.21.238:5432/clients";
        for(Servers serv : servers){
            String get = String.join("','",serv.getCashIP());
            String cashes = "'"+get+"'";
            String mrc = connect.getStringMrc(serv.getShopNumber(), cashes,urlP,"postgres", "postgres");
            try{
                    String upload = connect.sendMrc(serv.getServerIP(), serv.getShopNumber(),mrc,urlC,"postgres", "postgres");
                    if (Objects.equals(upload, "0")){
                    uploaded.add(serv.getShopNumber());
                }else {
                        filed.add(serv.getShopNumber());
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
        }
        int countUploaded = uploaded.size();
        int countFiled = filed.size();
        delegateExecution.setVariable(ProcessVariableConstants.NOT_UPLOADED, filed);
        delegateExecution.setVariable("countFiled",countFiled);
        delegateExecution.setVariable(ProcessVariableConstants.UPLOADED_SHOPS,uploaded);
        delegateExecution.setVariable("countUploaded",countUploaded);

    }
}
