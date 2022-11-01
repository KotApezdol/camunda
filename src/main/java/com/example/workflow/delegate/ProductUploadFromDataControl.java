package com.example.workflow.delegate;

import com.example.workflow.config.HibernateUtil;
import com.example.workflow.data.clients.DataCash;
import com.example.workflow.data.clients.DataServs;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProductUploadFromDataControl implements JavaDelegate {
    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        String clientId = (String) delegateExecution.getVariable("clientId");
        Session clSession = HibernateUtil.getClientsSessionFactory().openSession();
        clSession.beginTransaction();
        List<String> shopIp = clSession.createQuery("select d.shopIp  from DataServs d where d.clientId = :id and d.checked = true", String.class).setParameter("id",clientId).getResultList();

        for(String ip : shopIp){
            DataServs serv = clSession.get(DataServs.class,ip);
            if (serv.isChecked()){
                for(String cashIp : serv.getCashes()){
                    DataCash cash = clSession.get(DataCash.class,cashIp);
                    if(cash.isChecked() && cash.getItemsCount() > 0){
                        int uploaded;
                        String get = String.join(",",cash.getItem());
                        String items = "'"+get+"'";
                        Long id = Long.valueOf(clientId);
                        Long shopNumber = Long.valueOf(serv.getShopNumber());
                        Long cashNumber = Long.valueOf(cash.getCashNumber());
                        try {
                            Query<Integer> query = clSession.createNativeQuery("SELECT * FROM send_product_to_server(:id, :servIp, :shopNumber, :cashNumber, :productList)", Integer.class)
                                    .setParameter("id", id)
                                    .setParameter("servIp", serv.getShopIp())
                                    .setParameter("shopNumber", shopNumber)
                                    .setParameter("cashNumber", cashNumber)
                                    .setParameter("productList", items);
                            uploaded = query.getSingleResult();
                        }catch (Exception e){
                            System.out.println(e.getMessage());
                            uploaded = 1;
                        }
                        if(uploaded == 0){
                            cash.setProduct_uploaded(true);
                            clSession.merge(cash);
                        }
                    }
                }
            }


        }

        clSession.getTransaction().commit();
        clSession.close();

    }
}
