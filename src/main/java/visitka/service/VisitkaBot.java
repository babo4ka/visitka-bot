package visitka.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendDice;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import utils.messages.ManyUsersMessagesDump;
import utils.messages.MessageBuilder;
import utils.tuples.Pair;
import visitka.config.BotConfig;
import visitka.service.pages.PagesManager;
import visitka.service.pages.subscribePage.SubsDataBase;
import visitka.utils.Emoji;

import java.util.Arrays;
import java.util.List;

@Component
@EnableScheduling
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

    ManyUsersMessagesDump messagesDump = new ManyUsersMessagesDump();


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
        deletePreviousMessages(update.getMessage().getChatId());
        String[] data = update.getMessage().getText().split(" ");
        String page = "";
        String[] args;

        if(data[0].startsWith("/")){
            page = data[0];
            args = Arrays.copyOfRange(data, 1, data.length);
        }else args = data;

        List<Pair<PartialBotApiMethod<Message>, Boolean>> messages;

        if(args.length == 0){
            messages = pageManager.execute(update, page);
        }else{
            messages = pageManager.executeWithArgs(update, page, args);
        }

        if(messages == null) return;


        for(var message: messages){
            Message msg = null;
            if(message.getFirst() instanceof SendMessage) msg = execute((SendMessage) message.getFirst());
            else if(message.getFirst() instanceof SendPhoto) msg = execute((SendPhoto) message.getFirst());

            if(message.getSecond())
                messagesDump.addMessage(msg, update.getMessage().getChatId());
        }
    }


    private void processCallback(Update update) throws TelegramApiException {
        deletePreviousMessages(update.getCallbackQuery().getMessage().getChatId());
        String[] data = update.getCallbackQuery().getData().split(" ");
        String page = "";
        String[] args;

        if(data[0].startsWith("/")){
            page = data[0];
            args = Arrays.copyOfRange(data, 1, data.length);
        }else args = data;


        List<Pair<PartialBotApiMethod<Message>, Boolean>> messages;

        if(args.length == 0){
            messages = pageManager.executeCallback(update, page);
        }else{
            messages = pageManager.executeCallbackWithArgs(update, page, args);
        }

        if(messages == null) return;


        for(var message: messages){
            Message msg = null;
            if(message.getFirst() instanceof SendMessage) msg = execute((SendMessage) message.getFirst());
            else if(message.getFirst() instanceof SendPhoto) msg = execute((SendPhoto) message.getFirst());
            else if (message.getFirst() instanceof SendDice) msg = execute((SendDice) message.getFirst());

            if(message.getSecond())
                messagesDump.addMessage(msg, update.getCallbackQuery().getMessage().getChatId());
        }
    }


    private void deletePreviousMessages(long chatId) {
        List<DeleteMessage> messages = messagesDump.getMessagesById(chatId);
        if (messages != null) {
            messages.forEach(msg -> {
                try {
                    execute(msg);
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
            });

            messagesDump.clearDump(chatId);
        }
    }

    @Scheduled(fixedRate = 15000)
    private void sendMessagesToSubs(){
        MessageBuilder messageBuilder = new MessageBuilder();

        SubsDataBase.getSubs().forEach(id -> {
            SendMessage message = messageBuilder.createTextMessage(null, id, "Ты подписан, держи " + Emoji.PLEADING_FACE.emoji() + "\n"
            + "Будет каждые 15 секунд отправляться!");

            try {
                execute(message);
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
        });

    }
}
