package com.example.workflow.delegate;

import com.example.workflow.config.HibernateUtil;
import com.example.workflow.data.postgres.DataControl;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.hibernate.Session;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CheckCashFromDataControl implements JavaDelegate {
    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        Session pgSession = HibernateUtil.getPostgresSessionFactory().openSession();
        Session clSession = HibernateUtil.getClientsSessionFactory().openSession();
        pgSession.beginTransaction();
        clSession.beginTransaction();
        Integer clientId = (Integer) delegateExecution.getVariable("clientId");
        Integer typeOfData = (Integer) delegateExecution.getVariable("typeOfData");

        List<String> shopsIp = (List<String>) delegateExecution.getVariable("checkedServ");

        String[] cashesIp = (String[]) pgSession.createQuery("select distinct d.cashIp from DataControl d where d.clientId = :id AND d.cmTypeOfData IN (:type)", DataControl.class)
                .setParameter("id",clientId)
                .setParameter("type",typeOfData)
                .list().toArray();


        pgSession.close();
        clSession.close();
    }
}
