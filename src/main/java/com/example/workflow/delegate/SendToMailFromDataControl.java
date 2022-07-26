package com.example.workflow.delegate;

import com.example.workflow.config.HibernateUtil;
import com.example.workflow.data.clients.Clients;
import com.example.workflow.data.clients.DataAlarm;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SendToMailFromDataControl implements JavaDelegate {

    @Autowired
    private JavaMailSender javaMailSender;
    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        String clientId = (String) delegateExecution.getVariable("clientId");
        String getMails = (String) delegateExecution.getVariable("mails");
        String[] mails = getMails.split(",");
        Session clSession = HibernateUtil.getClientsSessionFactory().openSession();
        clSession.beginTransaction();
        Clients clients = clSession.get(Clients.class,clientId);
        List<DataAlarm> alarms= clSession.createQuery("select d from DataAlarm d", DataAlarm.class)
                .setParameter("id", clientId)
                .getResultList();
        StringBuilder alarm = new StringBuilder();
        for (DataAlarm cash : alarms){
            alarm.append("\nТК: "+cash.getShopNumber()+" Касса: "+cash.getCashNumber());

        }
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(mails);
        msg.setSubject("Алармы по контролю данных клиент: "+clients.getName());
        msg.setText("Проблемные кассы:"+alarm);
        javaMailSender.send(msg);

        clSession.close();
    }
}
