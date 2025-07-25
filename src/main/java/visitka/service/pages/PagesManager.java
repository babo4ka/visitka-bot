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
import utils.tuples.Pair;
import visitka.service.pages.cubesPage.CubesPage;
import visitka.service.pages.investPage.InvestPage;
import visitka.service.pages.startPage.StartPage;
import visitka.service.pages.subscribePage.SubscribePage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class PagesManager extends PageManager {

    @Autowired
    ApplicationContext context;


    @Override
    @EventListener(ContextRefreshedEvent.class)
    protected void setupPages() {
        addPage(StartPage.NAME, context.getBean(StartPage.class));
        addPage(CubesPage.NAME, context.getBean(CubesPage.class));
        addPage(InvestPage.NAME, context.getBean(InvestPage.class));
        addPage(SubscribePage.NAME, context.getBean(SubscribePage.class));
    }

    private final Map<Long, String> lastCalledPageByUser = new HashMap<>();

    @Override
    public List<Pair<PartialBotApiMethod<Message>, Boolean>> execute(Update update, String pageName) throws TelegramApiException {
        long chatId = update.getMessage().getChatId();
        pageName = getPageName(pageName, chatId);

        return pages.get(pageName).execute(update);
    }

    @Override
    public List<Pair<PartialBotApiMethod<Message>, Boolean>> executeWithArgs(Update update, String pageName, String... args) throws TelegramApiException {
        long chatId = update.getMessage().getChatId();
        pageName = getPageName(pageName, chatId);

        return pages.get(pageName).executeWithArgs(update, args);
    }

    @Override
    public List<Pair<PartialBotApiMethod<Message>, Boolean>> executeCallback(Update update, String pageName) throws TelegramApiException {
        long chatId = update.getCallbackQuery().getMessage().getChatId();
        pageName = getPageName(pageName, chatId);

        return pages.get(pageName).executeCallback(update);
    }

    @Override
    public List<Pair<PartialBotApiMethod<Message>, Boolean>> executeCallbackWithArgs(Update update, String pageName, String... args) throws TelegramApiException {
        long chatId = update.getCallbackQuery().getMessage().getChatId();
        pageName = getPageName(pageName, chatId);

        return pages.get(pageName).executeCallbackWithArgs(update, args);
    }


    private String getPageName(String pageName, long chatId){
        if(!pageName.isEmpty()){
            if(!lastCalledPageByUser.containsKey(chatId)) lastCalledPageByUser.put(chatId, pageName);
            else lastCalledPageByUser.replace(chatId, pageName);
        }
        else pageName = lastCalledPageByUser.get(chatId);

        return pageName;
    }
}
