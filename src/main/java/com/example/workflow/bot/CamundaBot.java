package com.example.workflow.bot;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;


@Component
public class CamundaBot extends TelegramLongPollingBot {


    long idChat;
    @Value("${bot.chatId}")
    String chatId;

    public long getIdChat(){
        return idChat;
    }
    @Value("${bot.name}")
    String botName;
    @Value("${bot.token}")
    String botToken;

    @Override
    public String getBotUsername() {
        return botName;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public void onUpdateReceived(Update update) {

    String messageText = update.getMessage().getText();
    long chatId = update.getMessage().getChatId();
    switch (messageText){
        case "/hi":
            startCommandReceived(chatId, update.getMessage().getChat().getFirstName());
            break;
        case "/reg":
            idChat = update.getMessage().getChatId();
            sendMessage(update.getMessage().getChatId(),update.getMessage().getChatId().toString());
            break;
        case "/getid":

            sendMessage(update.getMessage().getChatId(),update.getMessage().getChatId().toString());
            break;
    }

    }

    private void startCommandReceived(long chatId, String name){
        String answer = "Hi, " + name + ", nice to meet you!";
        sendMessage(chatId, answer);
    }

    private void sendMessage(long chatId, String textToSend) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void sendToTelegram(String chatId, String textToSend) {
        SendMessage message = new SendMessage();
        message.enableMarkdown(true);
        message.setChatId(chatId);
        message.setText(textToSend);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void sendCamunda(String filed, String all, String textToSend, String textToFail) {
        String combo = "Всего найдено расхождений на "+all+" ТК \nНе прошли проверку "+filed+" ТК \nЗагружено на "+textToSend+" ТК \nОшибка загрузки на "+textToFail+" ТК";
        SendMessage message = new SendMessage();
        message.enableMarkdown(true);
        message.setChatId(String.valueOf(chatId));
        message.setText(combo);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

}
