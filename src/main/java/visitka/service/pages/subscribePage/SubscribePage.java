package visitka.service.pages.subscribePage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import utils.messages.MessageBuilder;
import utils.messages.keyboard.InlineKeyboardBuilder;
import utils.pages.interfaces.Page;
import visitka.utils.Emoji;

import java.util.List;
import java.util.stream.Stream;

@Component
public class SubscribePage implements Page {

    public static final String NAME = "/sub";

    Logger logger = LogManager.getLogger();


    final String text = "Вы можете подписаться на рассылку сообщений\n" +
            "Мне было лень делать базу данных, поэтому подписчики будут храниться до тех пор пока бот не перезагрузится " + Emoji.SWEAT.emoji();

    final String negativeText = "Отписался " + Emoji.OPEN_MOUTH.emoji();

    @Override
    public List<PartialBotApiMethod<Message>> executeCallbackWithArgs(Update update, String... args) {
        logger.info("{} вызвал через кнопку команду /sub с аргуметами {}",
                update.getCallbackQuery().getMessage().getChat().getUserName(), args);

        MessageBuilder messageBuilder = new MessageBuilder();
        InlineKeyboardBuilder keyboardBuilder = new InlineKeyboardBuilder();
        keyboardBuilder = keyboardBuilder.addButton(Emoji.UNAMUSED.emoji() + " На главную", "/start").nextRow();

        if(args[0].equals("1"))
            SubsDataBase.add(update.getCallbackQuery().getMessage().getChatId());
        else
            SubsDataBase.remove(update.getCallbackQuery().getMessage().getChatId());

        SendMessage message = messageBuilder.createTextMessage(keyboardBuilder.build(),
                update.getCallbackQuery().getMessage().getChatId(),
                args[0].equals("1")?text:negativeText);


        return Stream.of(message).map(m -> (PartialBotApiMethod<Message>) message).toList();
    }
}
