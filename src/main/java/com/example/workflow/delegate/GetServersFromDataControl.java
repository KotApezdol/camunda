package com.example.workflow.delegate;

import com.example.workflow.config.HibernateUtil;
import com.example.workflow.data.clients.DataCash;
import com.example.workflow.data.clients.DataServs;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;



@Component
public class GetServersFromDataControl implements JavaDelegate {
    @Value("${typeOfData.all}")
    List<Integer> allDataInt = new ArrayList<>();
    @Value("${typeOfData.prod}")
    List<Integer> productDataInt = new ArrayList<>();
    @Value("${typeOfData.mrc}")
    List<Integer> mrcDataInt = new ArrayList<>();
    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {

        Session pgSession = HibernateUtil.getPostgresSessionFactory().openSession();
        Session clSession = HibernateUtil.getClientsSessionFactory().openSession();
        pgSession.beginTransaction();
        clSession.beginTransaction();
        clSession.createNativeQuery("truncate table data_servs", DataServs.class).executeUpdate();
        clSession.createNativeQuery("truncate table data_cash", DataCash.class).executeUpdate();
        clSession.getTransaction().commit();

        List<Integer> clients = pgSession.createQuery("select distinct d.clientId from DataControl d where d.clientId != null",Integer.class)
                .getResultList();

        for(Integer clientId : clients) {

            List<Long> shops = pgSession.createQuery("select distinct d.shopNumber from DataControl d where d.clientId = :id and d.shopNumber != null", Long.class)
                    .setParameter("id", clientId)
                    .getResultList();


            for (Long shopNumber : shops) {
                clSession.beginTransaction();
                boolean checked = false;
                int id = clientId;
                int transport = -1;
                String ip = pgSession.createQuery("select distinct d.shopIp from DataControl d where d.clientId = :id AND d.shopNumber = :shopNumber", String.class)
                        .setParameter("id", clientId)
                        .setParameter("shopNumber", shopNumber)
                        .setMaxResults(1)
                        .getSingleResult();
                List<Long> cashesLong = pgSession.createQuery("select distinct d.cashNumber from DataControl d where d.clientId = :id AND d.shopNumber = :shopNumber", Long.class)
                        .setParameter("id", clientId)
                        .setParameter("shopNumber", shopNumber)
                        .getResultList();
                List<Integer> cashes =new ArrayList<>();
                for (Long get : cashesLong){
                    cashes.add(get.intValue());
                }

                DataServs servs = new DataServs(ip, id, shopNumber.intValue(), cashes, checked);
                clSession.merge(servs);
                clSession.getTransaction().commit();
            }

            List<DataServs> servs = clSession.createQuery("select d from DataServs d where d.clientId = :id", DataServs.class)
                    .setParameter("id", clientId)
                    .getResultList();

            for (DataServs server : servs) {
                clSession.beginTransaction();
                boolean checked = false;
                boolean product_uploaded = false;
                boolean mrc_uploaded = false;
                int id = clientId;
                int transport = -9;

                for (Integer cashNumber : server.getCashes()) {
                    String ip = pgSession.createQuery("select d.cashIp from DataControl d where d.shopNumber = :shopNumber and d.cashNumber = :cashNumber and d.clientId = :id", String.class)
                            .setParameter("shopNumber",server.getShopNumber())
                            .setParameter("cashNumber", cashNumber)
                            .setParameter("id", clientId)
                            .setMaxResults(1)
                            .getSingleResult();
                    List<String> items = pgSession.createQuery("select distinct d.code from DataControl d where d.clientId = :id AND d.cmTypeOfData IN (:type) AND d.cashIp = :ip AND d.code != NULL", String.class)
                            .setParameter("id", clientId)
                            .setParameter("type", productDataInt)
                            .setParameter("ip", ip)
                            .getResultList();

                    List<String> mrc = pgSession.createQuery("select distinct d.code from DataControl d where d.clientId = :id AND d.cmTypeOfData IN (:type) AND d.cashIp = :ip AND d.productMarking != NULL", String.class)
                            .setParameter("id", clientId)
                            .setParameter("type", mrcDataInt)
                            .setParameter("ip", ip)
                            .getResultList();

                    String cmExtraInfo = pgSession.createQuery("select distinct d.cmExtraInfo from DataControl d where d.clientId = :id AND d.cmTypeOfData IN (:type) AND d.cashIp = :ip", String.class)
                            .setParameter("id", clientId)
                            .setParameter("type", allDataInt)
                            .setParameter("ip", ip)
                            .setMaxResults(1)
                            .getSingleResultOrNull();

                    int itemsCount = items.size();
                    int mrcCount = mrc.size();

                    DataCash dataCash = new DataCash(ip, id, server.getShopNumber(), cashNumber, items, mrc, checked, product_uploaded, mrc_uploaded, itemsCount, mrcCount, cmExtraInfo, transport);
                    clSession.merge(dataCash);
                }
                clSession.getTransaction().commit();
            }
        }

        pgSession.close();
        clSession.close();
    }
}
