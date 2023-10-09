package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.service.TgBotService;


import javax.annotation.PostConstruct;
import java.util.List;

@Service
public class TelegramBotUpdatesListener implements UpdatesListener {

    private Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);

    @Autowired
    private TelegramBot telegramBot;
    @Autowired
    private TgBotService tgBotService;

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

    @Override
    public int process(List<Update> updates) {

        updates.forEach(update -> {
            String res = update.message().text();
            logger.info("Processing update: {}", update);
            // Process your updates here
           if(res.equals("/start")){
                telegramBot.execute(new SendMessage(update.message().chat().id(), "Ku-Ku"));
           } else if (update.message().text().startsWith("/add")) {
               tgBotService.addTask(update.message());
           }
        });
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

}
