package uz.pdp.telegramevosbot.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import uz.pdp.telegramevosbot.entity.Product;
import uz.pdp.telegramevosbot.entity.ProductBasket;
import uz.pdp.telegramevosbot.entity.TgUser;
import uz.pdp.telegramevosbot.finals.BotBtnText;
import uz.pdp.telegramevosbot.repository.CategoryRepository;
import uz.pdp.telegramevosbot.repository.ProductBasketRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BotButtonServiceImpl implements BotButtonService {
    private final CategoryRepository categoryRepository;
    private final ProductBasketRepository productBasketRepository;

    public ReplyKeyboardMarkup start(TgUser tgUser) {


        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setResizeKeyboard(true);

        List<KeyboardRow> rowList = new ArrayList<>();

        List<ProductBasket> allByTgUserId = productBasketRepository.findAllByTgUserId(tgUser.getId());

        if (!allByTgUserId.isEmpty()) {
            KeyboardRow row1 = new KeyboardRow();
            row1.add(new KeyboardButton(BotBtnText.BASKET));
            rowList.add(row1);
        }

        KeyboardRow row1 = new KeyboardRow();
        row1.add(new KeyboardButton(BotBtnText.MENU));

        KeyboardRow row2 = new KeyboardRow();
        row2.add(new KeyboardButton(BotBtnText.MY_ORDERS));

        KeyboardRow row3 = new KeyboardRow();
        row3.add(new KeyboardButton(BotBtnText.REVIEW));
        row3.add(new KeyboardButton(BotBtnText.SETTINGS));


        rowList.add(row1);
        rowList.add(row2);
        rowList.add(row3);

        replyKeyboardMarkup.setKeyboard(rowList);

        return replyKeyboardMarkup;

    }

    @Override
    public ReplyKeyboardMarkup withStringList(List<String> strings) {
        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
        markup.setResizeKeyboard(true);

        List<KeyboardRow> rows = new ArrayList<>();

        for (String string : strings) {
            KeyboardRow keyboardButtons = new KeyboardRow();
            keyboardButtons.add(string);
            rows.add(keyboardButtons);
        }

        KeyboardRow row4 = new KeyboardRow();
        row4.add(new KeyboardButton(BotBtnText.BACK));
        rows.add(row4);

        markup.setKeyboard(rows);
        return markup;
    }

    @Override
    public ReplyKeyboardMarkup stringListAndSizeRow(List<String> strings, int size) {
        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
        markup.setResizeKeyboard(true);

        List<KeyboardRow> rows = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();
        int i = 0;
        for (String string : strings) {
            KeyboardButton button = new KeyboardButton(string);
            row.add(button);
            i++;
            if (i == size) {
                i = 0;
                rows.add(row);
                row = new KeyboardRow();
            }
        }
        if (strings.size() % size != 0) rows.add(row);
        markup.setKeyboard(rows);
        return markup;
    }

    @Override
    public ReplyKeyboardMarkup stringListAndSizeRowWithBack(List<String> strings, int size) {
        ReplyKeyboardMarkup markup = stringListAndSizeRow(strings, size);
        List<KeyboardRow> keyboard = markup.getKeyboard();
        KeyboardRow row = new KeyboardRow();
        row.add(new KeyboardButton(BotBtnText.BACK));
        keyboard.add(row);
        markup.setKeyboard(keyboard);
        return markup;
    }

    @Override
    public InlineKeyboardMarkup basket(TgUser tgUser) {
        List<ProductBasket> allByTgUserId = productBasketRepository.findAllByTgUserId(tgUser.getId());

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();

        List<InlineKeyboardButton> buttons = new ArrayList<>();

        InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
        inlineKeyboardButton.setCallbackData(BotBtnText.CONFIRM);
        inlineKeyboardButton.setText(BotBtnText.CONFIRM);
        buttons.add(inlineKeyboardButton);

        List<List<InlineKeyboardButton>> markupButtons = new ArrayList<>();
        markupButtons.add(buttons);


        for (ProductBasket productBasket : allByTgUserId) {
            StringBuilder sb = new StringBuilder();
            sb.append(productBasket.getProduct().getCategory().getName()).append(" ").append(productBasket.getProduct().getName()).append(" - ").append(productBasket.getProduct().getPrice()).append(" x ").append(productBasket.getQuantity()).append("\n");
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText("❌" + sb);
            button.setCallbackData("basket:" + productBasket.getId());
            markupButtons.add(List.of(button));
        }

        markup.setKeyboard(markupButtons);
        return markup;
    }

    @Override
    public InlineKeyboardMarkup withStringsList(List<List<String>> strings) {
        InlineKeyboardMarkup inline = new InlineKeyboardMarkup();

        List<List<InlineKeyboardButton>> inlineButtons = new ArrayList<>();
        for (List<String> string : strings) {
            List<InlineKeyboardButton> buttons = new ArrayList<>();
            for (String str : string) {
                InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
                inlineKeyboardButton.setText(str.split(";")[0]);
                inlineKeyboardButton.setCallbackData(str);
                buttons.add(inlineKeyboardButton);
            }
            inlineButtons.add(buttons);
        }

        inline.setKeyboard(inlineButtons);
        return inline;
    }

    @Override
    public InlineKeyboardMarkup product(List<Product> products) {
        InlineKeyboardMarkup inline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();

        for (Product product : products) {
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setCallbackData("product:" + product.getId());
            button.setText(product.getName() + "  " + product.getPrice());
            buttons.add(List.of(button));
        }
        inline.setKeyboard(buttons);

        return inline;
    }
}
