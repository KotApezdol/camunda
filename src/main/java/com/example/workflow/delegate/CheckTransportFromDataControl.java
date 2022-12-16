package com.example.workflow.delegate;

import com.example.workflow.config.HibernateUtil;
import com.example.workflow.data.clients.DataCash;
import com.example.workflow.data.clients.DataServs;
import com.example.workflow.data.clients.IpIdCash;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CheckTransportFromDataControl implements JavaDelegate {
    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {

        String getId = (String) delegateExecution.getVariable("clientId");
        int clientId = Integer.parseInt(getId);
        Session clSession = HibernateUtil.getClientsSessionFactory().openSession();
        clSession.beginTransaction();
        List<DataServs> servs = clSession.createQuery("select d from DataServs d where d.clientId = :id", DataServs.class)
                .setParameter("id", clientId)
                .getResultList();
        clSession.getTransaction().commit();
        for (DataServs server : servs){
            clSession.beginTransaction();
            for (Integer cashNumber : server.getCashes()) {
                IpIdCash idCash = new IpIdCash(server.getShopNumber(),cashNumber,clientId);
                DataCash cash = clSession.get(DataCash.class,idCash);
                if(cash.isChecked()) {
                    if(cash.getItemsCount()>0 || cash.getMrccount() >0) {
                        try {
                            Query<Integer> transportCheck = clSession.createNativeQuery("select * from get_cash_file_transfer_status(:id,:ip,:shop,:cash)", Integer.class)
                                    .setParameter("id", clientId)
                                    .setParameter("ip", server.getShopIp())
                                    .setParameter("shop", server.getShopNumber())
                                    .setParameter("cash", cashNumber);

                            cash.setTransport(transportCheck.getSingleResult());
                            clSession.merge(cash);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }
            }
            clSession.getTransaction().commit();
        }
        clSession.close();
    }
}
