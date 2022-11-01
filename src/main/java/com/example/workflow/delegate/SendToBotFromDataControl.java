package com.example.workflow.delegate;

import com.example.workflow.bot.CamundaBot;
import com.example.workflow.config.HibernateUtil;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SendToBotFromDataControl implements JavaDelegate {
    CamundaBot bot;
    @Value("${bot.chatId}")
    String chatId;

    public SendToBotFromDataControl (CamundaBot bot){
        this.bot = bot;
    }
    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        String clientId = (String) delegateExecution.getVariable("clientId");
        Session clSession = HibernateUtil.getClientsSessionFactory().openSession();
        clSession.beginTransaction();

        Long countAllSev = clSession.createQuery("select distinct count(d.shopNumber) from DataServs d where d.clientId = :id", Long.class)
                .setParameter("id", clientId)
                .getSingleResult();
        Long countAllCash = clSession.createQuery("select distinct count(d.cashNumber) from DataCash d where d.clientId = :id", Long.class)
                .setParameter("id", clientId)
                .getSingleResult();
        Long countCheckedServ = clSession.createQuery("select distinct count(d.shopNumber) from DataServs d where d.clientId = :id and d.checked = false", Long.class)
                .setParameter("id", clientId)
                .getSingleResult();
        Long countCheckedCash = clSession.createQuery("select distinct count(d.cashNumber) from DataCash d where d.clientId = :id and d.checked = false", Long.class)
                .setParameter("id", clientId)
                .getSingleResult();
        Long uploadedCash = clSession.createQuery("select distinct count(d.cashNumber) from DataCash d where d.clientId = :id and d.product_uploaded = true", Long.class)
                .setParameter("id", clientId)
                .getSingleResult();

        String message = "Всего серверов ("+countAllSev+")\nВсего касс ("+countAllCash+")\nНе прошло проверку серверов ("+countCheckedServ+")\nНе прошло проверку касс ("+countCheckedCash+")\nРасхождения по товарам загружены на "+uploadedCash+" касс";
        bot.sendToTelegram(chatId,message);
        clSession.close();

    }
}
