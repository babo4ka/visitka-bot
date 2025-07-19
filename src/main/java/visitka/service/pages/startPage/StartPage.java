package visitka.service.pages.startPage;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import utils.messages.MessageBuilder;
import utils.pages.interfaces.Page;

import java.util.ArrayList;
import java.util.List;

@Component
public class StartPage implements Page {

    public static final String NAME = "/start";

    @Override
    public List<PartialBotApiMethod<Message>> execute(Update update) throws TelegramApiException {
        String text = "Hello";
        List<SendMessage> messages = new ArrayList<>();
        MessageBuilder messageBuilder = new MessageBuilder();

        messages.add(messageBuilder.createTextMessage
                (null, update.getMessage().getChatId(), text));

        return messages.stream().map(e -> (PartialBotApiMethod<Message>) e).toList();
    }
}
