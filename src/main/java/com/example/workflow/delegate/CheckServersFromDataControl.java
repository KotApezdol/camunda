package com.example.workflow.delegate;

import com.example.workflow.config.HibernateUtil;
import com.example.workflow.data.clients.DataCash;
import com.example.workflow.data.clients.DataServs;
import com.example.workflow.data.clients.IpIdCash;
import com.example.workflow.data.clients.IpIdServ;
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
        List<Integer> shops = clSession.createQuery("select d.shopIp  from DataServs d where d.clientId = :id", Integer.class).setParameter("id",clientId).getResultList();
        clSession.getTransaction().commit();

        for(Integer shopNumber : shops){
            clSession.beginTransaction();
            IpIdServ idServ = new IpIdServ(shopNumber,Integer.parseInt(clientId));
            DataServs servs = clSession.get(DataServs.class,idServ);
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
                for (Integer cashNumber : servs.getCashes()) {
                    try {

                        IpIdCash idCash = new IpIdCash(shopNumber,cashNumber,Integer.parseInt(clientId));
                        DataCash cash = clSession.get(DataCash.class, idCash);
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
