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
public class CheckServersFromDataControl implements JavaDelegate {
    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        String clientId = (String) delegateExecution.getVariable("clientId");
        Session clSession = HibernateUtil.getClientsSessionFactory().openSession();
        clSession.beginTransaction();
        List<String> shopIp = clSession.createQuery("select d.shopIp  from DataServs d where d.clientId = :id", String.class).setParameter("id",clientId).getResultList();
        clSession.getTransaction().commit();

        for(String ip : shopIp){
            clSession.beginTransaction();
            DataServs servs = clSession.get(DataServs.class,ip);
            int chek;
            try {
                chek = clSession.createQuery("select get_retail_db_status(:clientId,:ip)", Integer.class)
                        .setParameter("clientId", servs.getClientId())
                        .setParameter("ip", servs.getShopIp())
                        .getSingleResult();

            }
            catch (Exception e){
                System.out.println(e.getMessage());
                chek = 1;
            }
            if(chek==0) {
                servs.setChecked(true);
                clSession.merge(servs);
                for (String cashIp : servs.getCashes()) {
                    try {


                        DataCash cash = clSession.get(DataCash.class, cashIp);
                        int chekCash;
                        try {
                            Query<Integer> query = clSession.createNativeQuery("select status from get_cash_db_status_starter(:clientId,:ip)", Integer.class)
                                    .setParameter("clientId", cash.getClientId())
                                    .setParameter("ip", cash.getCashIp());
                            chekCash = query.getSingleResult();
                        } catch (Exception e) {
                            System.out.println(e.getMessage());
                            chekCash = 1;
                        }
                        if (chekCash == 0) {
                            cash.setChecked(true);
                            clSession.merge(cash);
                        }

                    }catch (Exception e){
                        System.out.println(e.getMessage());
                    }

                }
            }
            clSession.getTransaction().commit();
        }

        clSession.close();
    }
}
