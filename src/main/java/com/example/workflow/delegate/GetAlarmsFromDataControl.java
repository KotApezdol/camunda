package com.example.workflow.delegate;

import com.example.workflow.config.HibernateUtil;
import com.example.workflow.data.clients.*;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.hibernate.Session;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class GetAlarmsFromDataControl implements JavaDelegate {
    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        Session clSession = HibernateUtil.getClientsSessionFactory().openSession();
        Session pgSession = HibernateUtil.getPostgresSessionFactory().openSession();
        clSession.beginTransaction();
        pgSession.beginTransaction();
        List<DataValues> dataValues = clSession.createQuery("select d from DataValues d", DataValues.class).getResultList();
        List<Integer> clients = clSession.createQuery("select distinct d.clientId from DataServs d", Integer.class).getResultList();
        clSession.getTransaction().commit();
        for (Integer clientId : clients) {
            clSession.beginTransaction();
            Clients client = clSession.get(Clients.class,clientId);
            List<DataCash> cashes = clSession.createQuery("select d from DataCash d where d.clientId = :id", DataCash.class)
                    .setParameter("id", clientId)
                    .getResultList();

            for (DataCash cash : cashes) {
                for (DataValues value : dataValues) {
                    if( value.getCmTypeOfData() != 90 ) {
                        try {
                            Integer count = pgSession.createQuery("select count(d.cmExtraInfo) from DataControl d where d.clientId = :id and d.shopNumber = :shopNumber and d.cashNumber = :cashNumber and d.cmTypeOfData = :value and d.code is not null ", Integer.class)
                                    .setParameter("id", clientId)
                                    .setParameter("shopNumber", cash.getShopNumber())
                                    .setParameter("cashNumber", cash.getCashNumber())
                                    .setParameter("value", value.getCmTypeOfData())
                                    .getSingleResultOrNull();
                            String string = pgSession.createQuery("select d.cmExtraInfo from DataControl d where d.clientId = :id and d.shopNumber = :shopNumber and d.cashNumber = :cashNumber and d.cmTypeOfData = :value and d.cmTypeOfData > 0 and d.code is null ", String.class)
                                    .setParameter("id", clientId)
                                    .setParameter("shopNumber", cash.getShopNumber())
                                    .setParameter("cashNumber", cash.getCashNumber())
                                    .setParameter("value", value.getCmTypeOfData())
                                    .getSingleResultOrNull();
                            if (count != null) {
                                if (count > 0) {
                                    DataAlarm cashCountAlarm = new DataAlarm(LocalDate.now(), client.getName(), cash.getShopNumber(), cash.getCashNumber(), value.getValue(), count);
                                    clSession.merge(cashCountAlarm);
                                }
                            }
                            if (string != null) {
                                if (!string.equals("0")) {
                                    DataAlarm cashStringAlarm = new DataAlarm(LocalDate.now(), client.getName(), cash.getShopNumber(), cash.getCashNumber(), value.getValue(), Integer.parseInt(string));
                                    clSession.merge(cashStringAlarm);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }if(value.getCmTypeOfData() == 90) {
                        if(cash.isChecked() && cash.getTransport() != 0){
                            if(cash.getTransport() != 2) {
                                try {
                                    TransportDataValues transportDataValues = clSession.get(TransportDataValues.class, cash.getTransport());
                                    String error = value.getValue() + " (" + transportDataValues.getValue() + ")";
                                    DataAlarm cashAlarm = new DataAlarm(LocalDate.now(), client.getName(), cash.getShopNumber(), cash.getCashNumber(), error, 1);
                                    clSession.merge(cashAlarm);
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }
            }
            clSession.getTransaction().commit();
        }
        pgSession.close();
        clSession.close();
    }
}
