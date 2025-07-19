package visitka.service.pages;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import utils.pages.PageManager;
import visitka.service.pages.startPage.StartPage;

import java.util.List;

@Component
public class PagesManager extends PageManager {

    @Autowired
    ApplicationContext context;


    @Override
    @EventListener(ContextRefreshedEvent.class)
    protected void setupPages() {
        addPage(StartPage.NAME, context.getBean(StartPage.class));
    }

    @Override
    public List<PartialBotApiMethod<Message>> execute(Update update, String pageName) throws TelegramApiException {
        return pages.get(pageName).execute(update);
    }

    @Override
    public List<PartialBotApiMethod<Message>> executeWithArgs(Update update, String pageName, String... args) throws TelegramApiException {
        return null;
    }

    @Override
    public List<PartialBotApiMethod<Message>> executeCallback(Update update, String pageName) throws TelegramApiException {
        return null;
    }

    @Override
    public List<PartialBotApiMethod<Message>> executeCallbackWithArgs(Update update, String pageName, String... args) throws TelegramApiException {
        return null;
    }
}
