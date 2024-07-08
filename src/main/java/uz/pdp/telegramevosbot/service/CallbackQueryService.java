package uz.pdp.telegramevosbot.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import uz.pdp.telegramevosbot.entity.*;
import uz.pdp.telegramevosbot.enums.ProductStatusEnum;
import uz.pdp.telegramevosbot.enums.StateEnum;
import uz.pdp.telegramevosbot.finals.BotBtnText;
import uz.pdp.telegramevosbot.repository.HistoryProductBasketRepository;
import uz.pdp.telegramevosbot.repository.ProductBasketRepository;
import uz.pdp.telegramevosbot.repository.ProductRepository;
import uz.pdp.telegramevosbot.repository.TgUserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CallbackQueryService {
    private final MySender mySender;
    private final ProductBasketRepository productBasketRepository;
    private final ProductRepository productRepository;
    private final BotButtonService botButtonService;
    private final TgUserRepository tgUserRepository;
    private final MessageService messageService;
    private final HistoryProductBasketRepository historyProductBasketRepository;

    public void process(CallbackQuery callbackQuery) {
        Message message = callbackQuery.getMessage();
        TgUser tgUser = findTgUser(message);
        String data = callbackQuery.getData();
        if (data.startsWith("product") && tgUser.getStateEnum().equals(StateEnum.PRODUCT)) {
            product(data, message);
        } else if (data.equals("-") || data.equals("+")) {
            plusMinusProduct(data, message);
        } else if (data.equals(BotBtnText.BASKET)) {
            toBasket(message, tgUser);
        } else if (data.startsWith("basket")) {
            deleteBasket(data, message, tgUser);
        } else if (data.equals(BotBtnText.CONFIRM)) {
            confirmBasket(message, tgUser);
        }
    }

    private void confirmBasket(Message message, TgUser tgUser) {
        List<ProductBasket> allByTgUserId = productBasketRepository.findAllByTgUserId(tgUser.getId());
        List<HistoryProductBasket> list = allByTgUserId.stream()
                .map(productBasket ->
                        new HistoryProductBasket(productBasket.getTgUser().getId(),
                                productBasket.getProduct().getName(),
                                productBasket.getProduct().getPrice(),
                                productBasket.getProduct().getAttachment().getId(),
                                productBasket.getProduct().getCategory().getId(),
                                productBasket.getQuantity()))
                .toList();
        historyProductBasketRepository.saveAll(list);
        productBasketRepository.deleteAll(allByTgUserId);

        DeleteMessage deleteMessage = new DeleteMessage();
        deleteMessage.setChatId(message.getChatId());
        deleteMessage.setMessageId(message.getMessageId());
        mySender.exe(deleteMessage);

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(message.getChatId());
        sendMessage.setText("Заказ успешно принят.");
        sendMessage.setReplyMarkup(botButtonService.start(tgUser));
        mySender.exe(sendMessage);
    }

    private void deleteBasket(String data, Message message, TgUser tgUser) {
        int id = Integer.parseInt(data.split(":")[1]);
        Optional<ProductBasket> byId = productBasketRepository.findById(id);
        if (byId.isEmpty()) {
            return;
        }
        ProductBasket basket = byId.get();
        productBasketRepository.delete(basket);
        List<ProductBasket> allByTgUserId = productBasketRepository.findAllByTgUserId(tgUser.getId());
        if (allByTgUserId.isEmpty()) {
            DeleteMessage deleteMessage = new DeleteMessage();
            deleteMessage.setChatId(tgUser.getUserId());
            deleteMessage.setMessageId(message.getMessageId());
            mySender.exe(deleteMessage);
            message.setText("/start");
            messageService.process(message);
            return;
        }

        StringBuilder sb = messageService.getUserProductBasket(allByTgUserId);
        EditMessageText edit = new EditMessageText();
        edit.setText(sb.toString());
        edit.setMessageId(message.getMessageId());
        edit.setChatId(message.getChatId());
        mySender.exe(edit);

        EditMessageReplyMarkup editMessageReplyMarkup = new EditMessageReplyMarkup();
        editMessageReplyMarkup.setMessageId(message.getMessageId());
        editMessageReplyMarkup.setChatId(message.getChatId());
        editMessageReplyMarkup.setReplyMarkup(botButtonService.basket(tgUser));
        mySender.exe(editMessageReplyMarkup);
    }


    private void toBasket(Message message, TgUser tgUser) {

        DeleteMessage deleteMessage = new DeleteMessage();
        deleteMessage.setChatId(tgUser.getUserId());
        deleteMessage.setMessageId(message.getMessageId());
        mySender.exe(deleteMessage);

        ProductBasket productBasket = new ProductBasket();
        InlineKeyboardMarkup replyMarkup = message.getReplyMarkup();
        List<List<InlineKeyboardButton>> keyboard = replyMarkup.getKeyboard();
        List<InlineKeyboardButton> buttonList = keyboard.get(0);
        InlineKeyboardButton inlineKeyboardButton = buttonList.get(1);
        String callbackData = inlineKeyboardButton.getCallbackData();
        String[] split = callbackData.split(";");
        String quantity = split[0];
        String productId = split[1].split(":")[1];
        Product product = productRepository.findById(Integer.parseInt(productId)).orElseThrow();
        productBasket.setProduct(product);
        productBasket.setQuantity(Integer.parseInt(quantity));
        productBasket.setProductStatusEnum(ProductStatusEnum.PROCESS);
        productBasket.setTgUser(findTgUser(message));
        productBasketRepository.save(productBasket);

        Category category = product.getCategory();
        Integer id = null;
        String text = BotBtnText.MENU;
        if (category.getParentCategory() != null) {
            Category parentCategory = category.getParentCategory();
            if (parentCategory.getParentCategory()!=null){
                id = parentCategory.getParentCategory().getId();
            }
            text = parentCategory.getName();
        }
            messageService.saveUserWithState(StateEnum.CATEGORY, id, tgUser);
        message.setText(text);
        messageService.category(message, tgUser);
    }

    private void plusMinusProduct(String data, Message message) {
        InlineKeyboardMarkup replyMarkup = message.getReplyMarkup();
        List<List<InlineKeyboardButton>> keyboard = replyMarkup.getKeyboard();
        List<InlineKeyboardButton> buttonList = keyboard.get(0);
        InlineKeyboardButton inlineKeyboardButton = buttonList.get(1);
        String text = inlineKeyboardButton.getCallbackData();
        String quantity = text.split(";")[0];
        String productId = text.split(";")[1];
        int i = Integer.parseInt(quantity);
        if (data.equals("-") && i == 1) return;
        if (data.equals("-")) {
            i--;
        } else if (data.equals("+")) {
            i++;
        }

        inlineKeyboardButton.setText("" + i);
        inlineKeyboardButton.setCallbackData(i + ";" + productId);

        EditMessageReplyMarkup edit = new EditMessageReplyMarkup();
        edit.setChatId(message.getChatId());
        edit.setMessageId(message.getMessageId());
        edit.setReplyMarkup(replyMarkup);
        mySender.exe(edit);

    }

    private void product(String data, Message message) {
        EditMessageReplyMarkup edit = new EditMessageReplyMarkup();
        edit.setChatId(message.getChatId());
        edit.setMessageId(message.getMessageId());
        List<List<String>> strings = new ArrayList<>(List.of(List.of("-", "1;" + data, "+"), List.of(BotBtnText.BASKET)));
        edit.setReplyMarkup(botButtonService.withStringsList(strings));
        mySender.exe(edit);
    }

    private TgUser findTgUser(Message message) {
        Optional<TgUser> byUserId = tgUserRepository.findByUserId(message.getChatId());
        return byUserId.orElseGet(() -> new TgUser(message.getChatId(), StateEnum.START, null));
    }
}
