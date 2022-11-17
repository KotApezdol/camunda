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
        String clientId = (String) delegateExecution.getVariable("clientId");

        Session pgSession = HibernateUtil.getPostgresSessionFactory().openSession();
        Session clSession = HibernateUtil.getClientsSessionFactory().openSession();
        pgSession.beginTransaction();
        clSession.beginTransaction();
        clSession.createNativeQuery("truncate table data_servs", DataServs.class).executeUpdate();
        clSession.createNativeQuery("truncate table data_cash", DataCash.class).executeUpdate();
        clSession.getTransaction().commit();
        clSession.beginTransaction();

        List<String> shops = pgSession.createQuery("select distinct d.shopIp from DataControl d where d.clientId = :id AND d.cmTypeOfData IN (:type)", String.class)
                .setParameter("id",clientId)
                .setParameter("type",allDataInt)
                .getResultList();


        for (String ip : shops){
            boolean checked = false;
            int id = Integer.parseInt(clientId);
            Long number = pgSession.createQuery("select distinct d.shopNumber from DataControl d where d.clientId = :id AND d.cmTypeOfData IN (:type) AND d.shopIp = :ip", Long.class)
                    .setParameter("id",clientId)
                    .setParameter("type",allDataInt)
                    .setParameter("ip",ip)
                    .getSingleResult();
            List<String> cashes = pgSession.createQuery("select distinct d.cashIp from DataControl d where d.clientId = :id AND d.cmTypeOfData IN (:type) AND d.shopIp = :ip", String.class)
                    .setParameter("id",clientId)
                    .setParameter("type",allDataInt)
                    .setParameter("ip",ip)
                    .getResultList();

            DataServs servs = new DataServs(ip,id,number.intValue(),cashes,checked);
            clSession.merge(servs);
        }

        List<String> cashes = pgSession.createQuery("select distinct d.cashIp from DataControl d where d.clientId = :id AND d.cmTypeOfData IN (:type)", String.class)
                .setParameter("id",clientId)
                .setParameter("type",allDataInt)
                .getResultList();

        for(String ip : cashes){
            boolean checked = false;
            boolean product_uploaded = false;
            boolean mrc_uploaded = false;
            int id = Integer.parseInt(clientId);

            Long shopNumber = pgSession.createQuery("select distinct d.shopNumber from DataControl d where d.clientId = :id AND d.cmTypeOfData IN (:type) AND d.cashIp = :ip", Long.class)
                    .setParameter("id",clientId)
                    .setParameter("type",allDataInt)
                    .setParameter("ip",ip)
                    .getSingleResult();

            Long cashNumber = pgSession.createQuery("select distinct d.cashNumber from DataControl d where d.clientId = :id AND d.cmTypeOfData IN (:type) AND d.cashIp = :ip", Long.class)
                    .setParameter("id",clientId)
                    .setParameter("type",allDataInt)
                    .setParameter("ip",ip)
                    .getSingleResult();

            List<String> items = pgSession.createQuery("select distinct d.code from DataControl d where d.clientId = :id AND d.cmTypeOfData IN (:type) AND d.cashIp = :ip", String.class)
                    .setParameter("id",clientId)
                    .setParameter("type",productDataInt)
                    .setParameter("ip",ip)
                    .getResultList();

            List<String> mrc = pgSession.createQuery("select distinct d.code from DataControl d where d.clientId = :id AND d.cmTypeOfData IN (:type) AND d.cashIp = :ip AND d.productMarking != NULL", String.class)
                    .setParameter("id",clientId)
                    .setParameter("type",mrcDataInt)
                    .setParameter("ip",ip)
                    .getResultList();

            int itemsCount = items.size();
            int mrcCount = mrc.size();

            DataCash dataCash = new DataCash(ip,id,shopNumber.intValue(),cashNumber.intValue(),items,mrc,checked,product_uploaded,mrc_uploaded,itemsCount,mrcCount);
            clSession.merge(dataCash);
        }

        clSession.getTransaction().commit();
        pgSession.close();
        clSession.close();

    }
}
