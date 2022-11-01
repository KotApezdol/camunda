package com.example.workflow.delegate;

import com.example.workflow.config.HibernateUtil;
import com.example.workflow.data.postgres.DataControl;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.hibernate.Session;
import org.springframework.stereotype.Component;

import static com.example.workflow.config.ProcessVariableConstants.COUNT_ALL_CASHES;
import static com.example.workflow.config.ProcessVariableConstants.COUNT_ALL_SERV;

@Component
public class CountServersFromDataControl implements JavaDelegate {

    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {

        Integer clientId = (Integer) delegateExecution.getVariable("clientId");
        Integer typeOfData = (Integer) delegateExecution.getVariable("typeOfData");

        Session session = HibernateUtil.getPostgresSessionFactory().openSession();
        session.beginTransaction();

        String countServ = String.valueOf(session.createQuery("select count (distinct d.shopNumber) from DataControl d where d.clientId = :id AND d.cmTypeOfData IN (:type) and d.productMarking != NULL", DataControl.class)
                .setParameter("id",clientId)
                .setParameter("type",typeOfData)
                .getSingleResult());
        String  countCashes = String.valueOf(session.createQuery("select count (distinct d.cashIp) from DataControl d where d.clientId = :id AND d.cmTypeOfData IN (:type) and d.productMarking != NULL", DataControl.class)
                .setParameter("id",clientId)
                .setParameter("type",typeOfData)
                .getSingleResult());
        session.close();
        delegateExecution.setVariable(COUNT_ALL_SERV,countServ);
        delegateExecution.setVariable(COUNT_ALL_CASHES,countCashes);

    }
}
