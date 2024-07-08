package uz.pdp.telegramevosbot.service;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Component

public class MyBot extends TelegramLongPollingBot {
    public MyBot(MessageService messageService, CallbackQueryService callbackQueryService) {
        super("6027918055:AAHfPXcPeBad31_qqqrndyqc5fjpTcqAuG0");
        this.messageService = messageService;
        this.callbackQueryService = callbackQueryService;
        try {
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            telegramBotsApi.registerBot(this);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private final MessageService messageService;
    private final CallbackQueryService callbackQueryService;

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            messageService.process(update.getMessage());
        }else if (update.hasCallbackQuery()){
            callbackQueryService.process(update.getCallbackQuery());
        }
    }

    @Override
    public String getBotUsername() {
        return "upload_your_work_bot";
    }
}
