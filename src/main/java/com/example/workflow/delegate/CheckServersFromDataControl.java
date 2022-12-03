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
        String getId = (String) delegateExecution.getVariable("clientId");
        int clientId = Integer.parseInt(getId);
        Session clSession = HibernateUtil.getClientsSessionFactory().openSession();
        clSession.beginTransaction();
        List<String> ipShops = clSession.createQuery("select distinct d.shopIp  from DataServs d where d.clientId = :id", String.class).setParameter("id",clientId).getResultList();
        clSession.getTransaction().commit();

        for(String ip : ipShops){
            clSession.beginTransaction();
            List<Integer> shops = clSession.createQuery("select d.shopNumber  from DataServs d where d.clientId = :id and d.shopIp = :ip", Integer.class)
                    .setParameter("id",clientId)
                    .setParameter("ip",ip)
                    .getResultList();
            int chek = 1;
            try {
                chek = clSession.createQuery("select get_retail_db_status(:clientId,:ip)", Integer.class)
                        .setParameter("clientId", clientId)
                        .setParameter("ip", ip)
                        .getSingleResult();

            }
            catch (Exception e){
                e.printStackTrace();
            }
            if(chek==0){
                for(Integer shopNumber : shops){
                    IpIdServ idServ = new IpIdServ(shopNumber,clientId);
                    DataServs servs = clSession.get(DataServs.class,idServ);
                    servs.setChecked(true);
                    clSession.merge(servs);
                }
            }
            clSession.getTransaction().commit();
        }
        clSession.beginTransaction();
        List<Integer> chekedShops = clSession.createQuery("select d.shopNumber  from DataServs d where d.clientId = :id and d.checked = true", Integer.class)
                .setParameter("id",clientId)
                .getResultList();
        clSession.getTransaction().commit();
        for(Integer shopNumber : chekedShops) {
            clSession.beginTransaction();
            IpIdServ idServ = new IpIdServ(shopNumber,clientId);
            DataServs servs = clSession.get(DataServs.class,idServ);
            for (Integer cashNumber : servs.getCashes()) {
                try {
                    IpIdCash idCash = new IpIdCash(shopNumber, cashNumber, clientId);
                    DataCash cash = clSession.get(DataCash.class, idCash);
                    int chekCash = 1;
                    if(cash.getItemsCount()>0 || cash.getMrccount()>0){
                        try {
                            Query<Integer> query = clSession.createNativeQuery("select status from get_cash_db_status_starter(:clientId,:ip)", Integer.class)
                                    .setParameter("clientId", cash.getClientId())
                                    .setParameter("ip", cash.getCashIp());
                            chekCash = query.getSingleResult();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    if (chekCash == 0) {
                        cash.setChecked(true);
                        clSession.merge(cash);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            clSession.getTransaction().commit();
        }
        clSession.close();
    }
}
