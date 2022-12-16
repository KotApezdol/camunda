package com.example.workflow.delegate;

import com.example.workflow.bot.CamundaBot;
import com.example.workflow.config.HibernateUtil;
import com.example.workflow.data.clients.Clients;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

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
        Session clSession = HibernateUtil.getClientsSessionFactory().openSession();
        clSession.beginTransaction();

        List<Integer> clients = clSession.createQuery("select distinct d.clientId from DataServs d",Integer.class)
                .getResultList();
        StringBuilder message = new StringBuilder();

        for(Integer clientId : clients) {

            Clients client = clSession.get(Clients.class, clientId);

            Long countAllSev = clSession.createQuery("select count(distinct d.shopNumber) from DataServs d where d.clientId = :id", Long.class)
                    .setParameter("id", clientId)
                    .getSingleResult();
            Long countSevDiff = clSession.createQuery("select count(distinct d.shopNumber) from DataCash d where d.clientId = :id and d.itemsCount > 0", Long.class)
                    .setParameter("id", clientId)
                    .getSingleResult();
            Long countAllCash = clSession.createQuery("select count(d.cashNumber) from DataCash d where d.clientId = :id and d.itemsCount > 0", Long.class)
                    .setParameter("id", clientId)
                    .getSingleResult();
            Long countCheckedServ = clSession.createQuery("select count(distinct d.shopNumber) from DataServs d where d.clientId = :id and d.checked = false", Long.class)
                    .setParameter("id", clientId)
                    .getSingleResult();
            Long countCheckedCash = clSession.createQuery("select count(d.cashNumber) from DataCash d where d.clientId = :id and d.checked = false and d.itemsCount > 0", Long.class)
                    .setParameter("id", clientId)
                    .getSingleResult();
            Long uploadedCash = clSession.createQuery("select count(d.cashNumber) from DataCash d where d.clientId = :id and d.product_uploaded = true", Long.class)
                    .setParameter("id", clientId)
                    .getSingleResult();
            Long sumItems = clSession.createQuery("select sum(d.itemsCount) from DataCash d where d.clientId = :id and d.product_uploaded = true and d.itemsCount > 0", Long.class)
                    .setParameter("id", clientId)
                    .getSingleResult();
            Long sumMrc = clSession.createQuery("select sum(d.mrccount) from DataCash d where d.clientId = :id and d.mrc_uploaded = true and d.itemsCount > 0", Long.class)
                    .setParameter("id", clientId)
                    .getSingleResult();
            if(sumItems == null)sumItems = 0L;
            if(sumMrc == null)sumMrc = 0L;
            long allDiff = sumItems + sumMrc;

            String messageClient = "Клиент: " + client.getName() + "\nВсего серверов (" + countAllSev + ")" +
                    "\nВсего серверов с расхождениями (" + countSevDiff + ")" +
                    "\nВсего касс с расхождениями (" + countAllCash + ")" +
                    "\nНе прошло проверку серверов (" + countCheckedServ + ")" +
                    "\nНе прошло проверку касс (" + countCheckedCash + ")" +
                    "\nРасхождения по товарам загружены на " + uploadedCash + " касс" +
                    "\nВсего расхождений загружено (" + allDiff + ")" +
                    "\nРахождений по товарам (" + sumItems + ")" +
                    "\nРасхождений по МРЦ (" + sumMrc + ")\n\n";

            message.append(messageClient);
        }
        bot.sendToTelegram(chatId, String.valueOf(message));
        clSession.close();

    }
}
