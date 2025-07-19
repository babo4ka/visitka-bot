package visitka.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import visitka.config.BotConfig;
import visitka.service.pages.PagesManager;

import java.util.List;

@Component
public class VisitkaBot extends TelegramLongPollingBot {

    final BotConfig config;

    @Override
    public String getBotUsername() {
        return config.getName();
    }
    @Override
    public String getBotToken() {
        return config.getToken();
    }

    public VisitkaBot(BotConfig config){
        this.config = config;
    }

    @Autowired
    PagesManager pageManager;



    @Override
    public void onUpdateReceived(Update update) {

        try{
            if(update.hasMessage()){
                processMessage(update);
            }else if(update.hasCallbackQuery()){
                processCallback(update);
            }
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private void processMessage(Update update) throws TelegramApiException {
        List<PartialBotApiMethod<Message>> messages = pageManager.execute(update, update.getMessage().getText());

        for(var message: messages){
            if(message instanceof SendMessage) execute((SendMessage) message);
            else if(message instanceof SendPhoto) execute((SendPhoto) message);
        }
    }

    private void processCallback(Update update){

    }
}
