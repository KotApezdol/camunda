package com.example.workflow.delegate;

import com.example.workflow.config.HibernateUtil;
import com.example.workflow.data.clients.Clients;
import com.example.workflow.data.clients.DataCash;
import com.example.workflow.data.clients.DataLog;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.hibernate.Session;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class DataCashToDataLog implements JavaDelegate {
    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        Session clSession = HibernateUtil.getClientsSessionFactory().openSession();
        clSession.beginTransaction();

        List<Integer> clientId = clSession.createQuery("select c.id from Clients c", Integer.class)
                        .getResultList();
        clSession.getTransaction().commit();
        for (Integer id : clientId){
            clSession.beginTransaction();
            List<DataCash> cashes = clSession.createQuery("select d from DataCash d where d.clientId = :id", DataCash.class)
                    .setParameter("id", id)
                    .getResultList();
            Clients client = clSession.get(Clients.class,id);
            for (DataCash cash : cashes){
                if(cash.getItemsCount() > 0 || cash.getMrccount() > 0) {
                    DataLog dataLog = new DataLog(LocalDate.now(), client.getName(), cash.getShopNumber(), cash.getCashNumber(), cash.getItemsCount(), cash.getMrccount());
                    clSession.merge(dataLog);
                }
            }
            clSession.getTransaction().commit();
        }

        clSession.close();
    }
}
