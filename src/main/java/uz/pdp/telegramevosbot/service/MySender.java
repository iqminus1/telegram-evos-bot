package uz.pdp.telegramevosbot.service;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class MySender extends DefaultAbsSender {
    protected MySender() {
        super(new DefaultBotOptions(), "6027918055:AAHfPXcPeBad31_qqqrndyqc5fjpTcqAuG0");
    }

    public void exe(SendMessage sendMessage) {
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }

    }

    public Message exe(SendPhoto sendPhoto) {
        try {
            return execute(sendPhoto);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    public void exe(EditMessageText edit) {
        try {
            execute(edit);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    public void exe(EditMessageReplyMarkup edit) {
        try {
            execute(edit);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    public void exe(DeleteMessage deleteMessage) {
        try {
            execute(deleteMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
}
