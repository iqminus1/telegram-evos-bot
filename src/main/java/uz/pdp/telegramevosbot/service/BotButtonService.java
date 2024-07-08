package uz.pdp.telegramevosbot.service;

import jakarta.validation.constraints.Positive;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import uz.pdp.telegramevosbot.entity.Product;
import uz.pdp.telegramevosbot.entity.TgUser;

import java.util.List;

public interface BotButtonService {
    ReplyKeyboardMarkup start(TgUser tgUser);

    ReplyKeyboardMarkup withStringList(List<String> strings);

    ReplyKeyboardMarkup stringListAndSizeRow(List<String> strings, int size);
    ReplyKeyboardMarkup stringListAndSizeRowWithBack(List<String> strings, int size);

    InlineKeyboardMarkup basket(TgUser tgUser);

    InlineKeyboardMarkup withStringsList(List<List<String>> strings);

    InlineKeyboardMarkup product(List<Product> products);
}
