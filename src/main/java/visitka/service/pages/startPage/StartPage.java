package visitka.service.pages.startPage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import utils.messages.MessageBuilder;
import utils.messages.keyboard.InlineKeyboardBuilder;
import utils.pages.interfaces.Page;
import utils.tuples.Pair;
import visitka.service.pages.subscribePage.SubsDataBase;
import visitka.utils.Emoji;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Component
public class StartPage implements Page {

    public static final String NAME = "/start";

    private final String text = "Привет, это пример телеграм-бота, который я могу сделать. " + Emoji.BANANA.emoji();

    private final String fakeText = "Ты попал не туда " + Emoji.COLD_SWEAT.emoji();

    private final Logger logger = LogManager.getLogger();


    @Override
    public List<Pair<PartialBotApiMethod<Message>, Boolean>> execute(Update update) throws TelegramApiException {
        logger.info("{} вызвал команду /start", update.getMessage().getChat().getUserName());
        List<SendPhoto> messages = new ArrayList<>();
        MessageBuilder messageBuilder = new MessageBuilder();
        InlineKeyboardBuilder keyboardBuilder = new InlineKeyboardBuilder();

        keyboardBuilder = keyboardBuilder
                .addButton(Emoji.SMIRK.emoji() + " Скинь меня кому-нибудь", "/share").nextRow()
                .addButton(Emoji.MAN_DANCING.emoji() + " Во че еще могу", "/invest").nextRow()
                .addButton(Emoji.LINKED_PAPERCLIPS.emoji() +
                        (SubsDataBase.contains(update.getMessage().getChatId())?" Отписаться":" Подписаться"),
                        "/sub " + (SubsDataBase.contains(update.getMessage().getChatId())?"0":"1")).nextRow()
                .addButton(Emoji.GAME_DIE.emoji() + " Кубики", "/cubes").nextRow();

        InputStream inputStream = StartPage.class.getResourceAsStream("/pics/banana-rob.jpg");
        InputFile picture;

        try {
            assert inputStream != null;
            picture = new InputFile(new ByteArrayInputStream(inputStream.readAllBytes()), "file");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        messages.add(messageBuilder.createPhotoMessage
                (keyboardBuilder.build(), update.getMessage().getChatId(), text, picture));

        return messages.stream().map(e -> new Pair<PartialBotApiMethod<Message>, Boolean>(e, true)).toList();
    }

    @Override
    public List<Pair<PartialBotApiMethod<Message>, Boolean>> executeCallback(Update update) throws TelegramApiException {
        logger.info("{} вызвал через кнопку команду /start", update.getCallbackQuery().getMessage().getChat().getUserName());
        List<SendPhoto> messages = new ArrayList<>();
        MessageBuilder messageBuilder = new MessageBuilder();
        InlineKeyboardBuilder keyboardBuilder = new InlineKeyboardBuilder();


        keyboardBuilder = keyboardBuilder
                .addButton(Emoji.SMIRK.emoji() + " Скинь меня кому-нибудь", "/share").nextRow()
                .addButton(Emoji.MAN_DANCING.emoji() + " Во че еще могу", "/invest").nextRow()
                .addButton(Emoji.LINKED_PAPERCLIPS.emoji() +
                        (SubsDataBase.contains(update.getCallbackQuery().getMessage().getChatId())?" Отписаться":" Подписаться"),
                        "/sub " + (SubsDataBase.contains(update.getCallbackQuery().getMessage().getChatId())?"0":"1")).nextRow()
                .addButton(Emoji.GAME_DIE.emoji() + " Кубики", "/cubes").nextRow();

        InputStream inputStream = StartPage.class.getResourceAsStream("/pics/banana-rob.jpg");
        InputFile picture;

        try {
            assert inputStream != null;
            picture = new InputFile(new ByteArrayInputStream(inputStream.readAllBytes()), "file");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        messages.add(messageBuilder.createPhotoMessage
                (keyboardBuilder.build(), update.getCallbackQuery().getMessage().getChatId(), text, picture));

        return messages.stream().map(e -> new Pair<PartialBotApiMethod<Message>, Boolean>(e, true)).toList();
    }

    @Override
    public List<Pair<PartialBotApiMethod<Message>, Boolean>> executeCallbackWithArgs(Update update, String... args) {
        logger.info("{} вызвал через кнопку команду /start с агрументами {}", update.getCallbackQuery().getMessage().getChat().getUserName(), args);

        MessageBuilder messageBuilder = new MessageBuilder();
        InlineKeyboardBuilder keyboardBuilder = new InlineKeyboardBuilder();

        keyboardBuilder = keyboardBuilder.addButton("Сюда", "/start").nextRow();

        InputStream inputStream = StartPage.class.getResourceAsStream("/pics/sadbanana.jpg");
        InputFile picture;


        try {
            assert inputStream != null;
            picture = new InputFile(new ByteArrayInputStream(inputStream.readAllBytes()), "file");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        SendPhoto sendPhoto = messageBuilder.createPhotoMessage(keyboardBuilder.build(), update.getCallbackQuery().getMessage().getChatId(),
                fakeText, picture);

        return Stream.of(sendPhoto).map(e -> new Pair<PartialBotApiMethod<Message>, Boolean>(sendPhoto, true)).toList();
    }
}
