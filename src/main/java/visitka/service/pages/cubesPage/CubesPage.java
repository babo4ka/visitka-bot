package visitka.service.pages.cubesPage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendDice;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import utils.messages.MessageBuilder;
import utils.messages.keyboard.InlineKeyboardBuilder;
import utils.pages.interfaces.Page;
import utils.tuples.Pair;
import visitka.utils.Emoji;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Component
public class CubesPage implements Page {

    public static final String NAME = "/cubes";

    private final Logger logger = LogManager.getLogger();

    private final String firstText = "Не эти кубики " + Emoji.FACE_WITH_SYMBOLS_ON_MOUTH.emoji();

    @Override
    public List<Pair<PartialBotApiMethod<Message>, Boolean>> executeCallback(Update update) throws TelegramApiException {
        logger.info("{} вызвал через кнопку команду /cubes", update.getCallbackQuery().getMessage().getChat().getUserName());

        List<SendPhoto> messages = new ArrayList<>();
        MessageBuilder messageBuilder = new MessageBuilder();
        InlineKeyboardBuilder keyboardBuilder = new InlineKeyboardBuilder();

        keyboardBuilder = keyboardBuilder.addButton("Дальше " + Emoji.CLOWN_FACE.emoji(), "/cubes 2").nextRow();

        InputStream inputStream = CubesPage.class.getResourceAsStream("/pics/mncrft.jpg");
        InputFile picture;

        try {
            assert inputStream != null;
            picture = new InputFile(new ByteArrayInputStream(inputStream.readAllBytes()), "file");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        messages.add(messageBuilder.createPhotoMessage
                (keyboardBuilder.build(), update.getCallbackQuery().getMessage().getChatId(), firstText, picture));

        return messages.stream().map(e -> new Pair<PartialBotApiMethod<Message>, Boolean>(e, true)).toList();
    }

    @Override
    public List<Pair<PartialBotApiMethod<Message>, Boolean>> executeCallbackWithArgs(Update update, String... args) {
        logger.info("{} вызвал через кнопку команду /cubes с аргументами {}",
                update.getCallbackQuery().getMessage().getChat().getUserName(), args);

        SendDice dice = new SendDice();
        dice.setChatId(update.getCallbackQuery().getMessage().getChatId());

        List<SendMessage> messages = new ArrayList<>();
        MessageBuilder messageBuilder = new MessageBuilder();
        InlineKeyboardBuilder keyboardBuilder = new InlineKeyboardBuilder();

        keyboardBuilder = keyboardBuilder.addButton(Emoji.UNAMUSED.emoji() +"На главную", "/start fake").nextRow()
                        .addButton(Emoji.SOB.emoji() + "Еще", "/cubes 2").nextRow();

        messages.add(messageBuilder.createTextMessage(keyboardBuilder.build(), update.getCallbackQuery().getMessage().getChatId(),
                "хеех"));

        return Stream.concat(Stream.of(new Pair<PartialBotApiMethod<Message>, Boolean>(dice, false)),
                messages.stream().map(e -> new Pair<PartialBotApiMethod<Message>, Boolean>(e, true))).toList();
    }
}
