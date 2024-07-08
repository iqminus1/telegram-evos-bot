package uz.pdp.telegramevosbot.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import uz.pdp.telegramevosbot.entity.*;
import uz.pdp.telegramevosbot.enums.StateEnum;
import uz.pdp.telegramevosbot.finals.BotBtnText;
import uz.pdp.telegramevosbot.repository.CategoryRepository;
import uz.pdp.telegramevosbot.repository.HistoryProductBasketRepository;
import uz.pdp.telegramevosbot.repository.ProductBasketRepository;
import uz.pdp.telegramevosbot.repository.TgUserRepository;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class MessageService {
    private final MySender mySender;
    private final BotButtonService botButtonService;
    private final CategoryRepository categoryRepository;
    private final TgUserRepository tgUserRepository;
    private final ProductBasketRepository productBasketRepository;
    private final HistoryProductBasketRepository historyProductBasketRepository;

    public void process(Message message) {
        if (message.hasText()) {
            TgUser tgUser = findTgUser(message);

            String text = message.getText();
            if (text.equals(BotBtnText.START)) {
                startUser(message, tgUser);
            } else if (tgUser.getStateEnum().equals(StateEnum.CATEGORY)
                    || (tgUser.getStateEnum().equals(StateEnum.START) && text.equals(BotBtnText.MENU))) {
                if (text.equals(BotBtnText.BACK)) {
                    backCategory(message, tgUser);
                    return;
                }
                category(message, tgUser);
            } else if (text.equals(BotBtnText.BACK) && tgUser.getStateEnum().equals(StateEnum.PRODUCT)) {
                backProduct(message, tgUser);
            } else if (text.equals(BotBtnText.BASKET)) {
                seeBasket(message, tgUser);
            } else if (text.equals(BotBtnText.MY_ORDERS) && tgUser.getStateEnum().equals(StateEnum.START)) {
                seeOrder(message, tgUser);
            }
        }
    }

    private void seeOrder(Message message, TgUser tgUser) {
        List<HistoryProductBasket> byTgUserId = historyProductBasketRepository.findByTgUserId(tgUser.getId());
        if (byTgUserId.isEmpty()) {
            sendMessage(message, "Вы совсем ничего не заказали.", null);
            return;
        }
        StringBuilder sb = new StringBuilder();

        double sum = byTgUserId.stream()
                .peek(history -> {
                    Optional<Category> byId = categoryRepository.findById(history.getCategoryId());
                    if (byId.isPresent()) {
                        Category category = byId.get();
                        if (category.getName().equals(history.getProductName())) {
                            sb.append(history.getProductName()).append(" ").append(history.getProductPrice()).append(" x ").append(history.getQuantity()).append("\n");
                        } else
                            sb.append(category.getName()).append(" ").append(history.getProductName()).append(" ").append(history.getProductPrice()).append(" x ").append(history.getQuantity()).append("\n");
                        return;
                    }
                    sb.append(history.getProductName()).append(" ").append(history.getProductPrice()).append(" x ").append(history.getQuantity()).append("\n");
                }).mapToDouble(history -> history.getQuantity() * history.getProductPrice()).sum();

        sb.append("\n\n\n").append("============ ИТОГО ============")
                .append("\n").append(sum).append(" сум");
        sendMessage(message, sb.toString(), null);

    }

    public StringBuilder getUserProductBasket(List<ProductBasket> allByTgUserId) {
        StringBuilder sb = new StringBuilder();
        double sum = allByTgUserId.stream().peek(productBasket -> {
                            if (productBasket.getProduct().getName().equals(productBasket.getProduct().getCategory().getName()))
                                sb.append(productBasket.getProduct().getName()).append(" - ").append(productBasket.getProduct().getPrice()).append(" x ").append(productBasket.getQuantity()).append("\n");
                            else
                                sb.append(productBasket.getProduct().getCategory().getName()).append(" ").append(productBasket.getProduct().getName()).append(" - ").append(productBasket.getProduct().getPrice()).append(" x ").append(productBasket.getQuantity()).append("\n");

                        }
                ).mapToDouble(productBasket -> productBasket.getProduct().getPrice() * productBasket.getQuantity())
                .sum();
        sb.append("\n\n\n").append("============ ИТОГО ============")
                .append("\n").append(sum).append(" сум");
        return sb;
    }

    private void seeBasket(Message message, TgUser tgUser) {
        List<ProductBasket> allByTgUserId = productBasketRepository.findAllByTgUserId(tgUser.getId());
        if (allByTgUserId.isEmpty()) {
            sendMessage(message, "Вы еще не выбрали не одного продукта", null);
            return;
        }
        StringBuilder sb = getUserProductBasket(allByTgUserId);
        sendMessage(message, botButtonService.basket(tgUser), sb.toString());

    }

    public void backProduct(Message message, TgUser tgUser) {
        Optional<Category> byId = categoryRepository.findById(tgUser.getCategoryId());
        if (byId.isEmpty()) return;
        Category category = byId.get();
        Integer id = null;
        String name = BotBtnText.MENU;
        if (category.getParentCategory() != null) {
            Category parentCategory = category.getParentCategory();
            if (parentCategory.getParentCategory() != null) {
                id = parentCategory.getParentCategory().getId();
            }
            name = category.getParentCategory().getName();
            tgUser.setCategoryId(parentCategory.getId());
        }
        tgUser.setCategoryId(id);
        message.setText(name);
        DeleteMessage deleteMessage = new DeleteMessage();
        deleteMessage.setMessageId(tgUser.getProductWithQueryId());
        deleteMessage.setChatId(tgUser.getUserId());
        mySender.exe(deleteMessage);
        saveUserWithState(StateEnum.CATEGORY, tgUser);
        category(message, tgUser);
    }

    private void backCategory(Message message, TgUser tgUser) {
        if (tgUser.getCategoryId() == null) {
            message.setText("/start");
            process(message);
            return;
        }
        Optional<Category> byId = categoryRepository.findById(tgUser.getCategoryId());
        if (byId.isEmpty()) return;
        Category category = byId.get();

        if (category.getParentCategory() != null) {
            Integer id = null;
            Category parentCategory = category.getParentCategory();
            if (parentCategory.getParentCategory() != null) {
                id = parentCategory.getParentCategory().getId();
            }
            tgUser.setCategoryId(id);
            message.setText(category.getParentCategory().getName());
            tgUserRepository.save(tgUser);
            process(message);
            return;
        }
        tgUser.setCategoryId(null);
        message.setText(BotBtnText.MENU);
        process(message);
    }

    private void product(Message message, TgUser tgUser) {
        Optional<Category> byNameAndParentCategoryId = categoryRepository.findByNameAndParentCategoryId(message.getText(), tgUser.getCategoryId());

        Category category = byNameAndParentCategoryId.orElseThrow();
        List<Product> products = category.getProducts();

        if (products.size() == 1) {
            sendMessage(message, "Выберите одно из следующих", botButtonService.withStringList(List.of(BotBtnText.BASKET)));

            List<List<String>> strings = new ArrayList<>(List.of(List.of("-", "1;product:" + products.get(0).getId(), "+"), List.of(BotBtnText.BASKET)));
            InlineKeyboardMarkup inline = botButtonService.withStringsList(strings);
            Message message1 = sendPhoto(message, tgUser, inline, products);
            tgUser.setProductWithQueryId(message1.getMessageId());

            return;

        }

        sendMessage(message, "Выберите одно из следующих", botButtonService.withStringList(List.of(BotBtnText.BASKET)));

        InlineKeyboardMarkup inline = botButtonService.product(products);
        sendPhoto(message, tgUser, inline, products);
    }

    private Message sendPhoto(Message message, TgUser tgUser, InlineKeyboardMarkup inline, List<Product> products) {
        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setChatId(message.getChatId());
        sendPhoto.setReplyMarkup(inline);
        sendPhoto.setPhoto(new InputFile().setMedia(new File(products.get(0).getAttachment().getPath())));
        Message exe = mySender.exe(sendPhoto);
        tgUser.setProductWithQueryId(exe.getMessageId());
        saveUserWithState(StateEnum.PRODUCT, tgUser);
        return exe;
    }

    private TgUser findTgUser(Message message) {
        Optional<TgUser> byUserId = tgUserRepository.findByUserId(message.getChatId());
        return byUserId.orElseGet(() -> new TgUser(message.getChatId(), StateEnum.START, null));
    }

    public void category(Message message, TgUser tgUser) {
        if (message.getText().equals(BotBtnText.MENU)) {
            List<Category> byParentCategoryIsNull = categoryRepository.findByParentCategoryIsNull();
            if (byParentCategoryIsNull.isEmpty()) {
                sendMessage(message, "Категорию еще не добавили", null);
                message.setText("/start");
                start(message, tgUser);
                return;
            }
            List<String> list = byParentCategoryIsNull.stream()
                    .map(Category::getName).toList();
            sendMessage(message, "Выберите категорию", botButtonService.stringListAndSizeRowWithBack(list, 2));
            saveUserWithState(StateEnum.CATEGORY, null, tgUser);
            return;
        }
        Optional<Category> byNameAndParentCategoryId = categoryRepository.findByNameAndParentCategoryId(message.getText(), tgUser.getCategoryId());

        if (byNameAndParentCategoryId.isEmpty()) {
            message.setText(BotBtnText.MENU);
            process(message);
            return;
        }

        Category category = byNameAndParentCategoryId.get();

        if (category.getCategories().isEmpty()) {
            product(message, tgUser);
            saveUserWithState(StateEnum.PRODUCT, category.getId(), tgUser);
            return;
        }

        List<Category> categories = category.getCategories();

        List<String> collect = categories.stream()
                .map(Category::getName)
                .collect(Collectors.toList());

        ReplyKeyboardMarkup buttons = botButtonService.stringListAndSizeRowWithBack(collect, 2);

        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setReplyMarkup(buttons);
        sendPhoto.setChatId(message.getChatId());
        InputFile inputFile = new InputFile();
        inputFile.setMedia(new File(category.getAttachment().getPath()));
        sendPhoto.setPhoto(inputFile);
        mySender.exe(sendPhoto);
        saveUserWithState(StateEnum.CATEGORY, category.getId(), tgUser);
    }


    private void start(Message message, TgUser tgUser) {
        sendMessage(message, "Выберите одно из следующих", botButtonService.start(tgUser));
        saveUserWithState(StateEnum.START, tgUser);
    }

    private void sendMessage(Message message, String str, ReplyKeyboardMarkup markup) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(message.getChatId());
        sendMessage.setText(str);
        sendMessage.setReplyMarkup(markup);
        mySender.exe(sendMessage);
    }

    private void sendMessage(Message message, InlineKeyboardMarkup markup, String str) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(message.getChatId());
        sendMessage.setText(str);
        sendMessage.setReplyMarkup(markup);
        mySender.exe(sendMessage);
    }

    public void saveUserWithState(StateEnum stateEnum, Integer categoryId, TgUser tgUser) {
        tgUser.setStateEnum(stateEnum);
        tgUser.setCategoryId(categoryId);
        tgUserRepository.save(tgUser);
    }

    private void saveUserWithState(StateEnum stateEnum, TgUser tgUser) {
        tgUser.setStateEnum(stateEnum);
        tgUserRepository.save(tgUser);
    }

    private void startUser(Message message, TgUser tgUser) {
        start(message, tgUser);
        tgUser.setCategoryId(null);
        tgUser.setProductWithQueryId(null);
        tgUserRepository.save(tgUser);
    }
}
