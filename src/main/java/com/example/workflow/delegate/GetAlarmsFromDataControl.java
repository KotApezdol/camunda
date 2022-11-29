package com.example.workflow.delegate;

import com.example.workflow.config.HibernateUtil;
import com.example.workflow.data.clients.DataAlarm;
import com.example.workflow.data.clients.DataCash;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.hibernate.Session;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GetAlarmsFromDataControl implements JavaDelegate {
    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        String clientId = (String) delegateExecution.getVariable("clientId");
        Session clSession = HibernateUtil.getClientsSessionFactory().openSession();
        clSession.beginTransaction();

        List<String> cashes = clSession.createQuery("select d.cashIp from DataCash d where d.clientId = :id", String.class)
                .setParameter("id",clientId).getResultList();
        clSession.getTransaction().commit();

        for(String ip : cashes){
            clSession.beginTransaction();
            DataCash cash = clSession.get(DataCash.class,ip);
            Long exist = clSession.createQuery("select count (d.cashNumber) from DataAlarm d where d.cashIp = :ip and d.clientId = :id", Long.class)
                    .setParameter("ip", ip)
                    .setParameter("id", clientId)
                    .getSingleResult();
            DataAlarm cashAlarm;
            if(exist > 0){
                cashAlarm = clSession.get(DataAlarm.class,ip);
                if(!cash.isChecked()){
                    int checked = cashAlarm.getCheckError();
                    cashAlarm.setCheckError(checked + 1);
                }else {
                    cashAlarm.setCheckError(0);
                }
                if(!cash.isProduct_uploaded() && cash.getItemsCount() > 0){
                    int productUploaded = cashAlarm.getItemsError();
                    cashAlarm.setItemsError(productUploaded + 1);
                }else {
                    cashAlarm.setItemsError(0);
                }
                if(!cash.isMrc_uploaded() && cash.getMrccount() > 0){
                    int mrcUploaded = cashAlarm.getMrcError();
                    cashAlarm.setMrcError(mrcUploaded + 1);
                }else {
                    cashAlarm.setMrcError(0);
                }
                clSession.merge(cashAlarm);

            }else {
                int itemsErr;
                int mrcErr;
                int checkErr;
                if(!cash.isProduct_uploaded() && cash.getItemsCount() > 0){
                    itemsErr = 1;
                }else {
                    itemsErr = 0;
                }
                if(!cash.isMrc_uploaded() && cash.getMrccount() > 0){
                    mrcErr = 1;
                }else {
                    mrcErr = 0;
                }
                if(!cash.isChecked()){
                    checkErr = 1;
                }else {
                    checkErr = 0;
                }
                cashAlarm = new DataAlarm(cash.getCashIp(), cash.getClientId(), cash.getShopNumber(), cash.getCashNumber(), itemsErr, mrcErr, checkErr);
                clSession.merge(cashAlarm);
            }
            clSession.getTransaction().commit();
        }

        clSession.close();
    }
}
