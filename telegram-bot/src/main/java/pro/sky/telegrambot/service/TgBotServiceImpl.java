package pro.sky.telegrambot.service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.helper.Parser;
import pro.sky.telegrambot.model.NotificationTask;
import pro.sky.telegrambot.repository.NotificationTaskRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
public class TgBotServiceImpl implements TgBotService{

    private TelegramBot telegramBot;
    private NotificationTaskRepository notificationTaskRepository;

    public TgBotServiceImpl(TelegramBot telegramBot, NotificationTaskRepository notificationTaskRepository){
        this.telegramBot=telegramBot;
        this.notificationTaskRepository=notificationTaskRepository;

    }

    public void addTask(Message message){
        NotificationTask task;
        long chatId = message.chat().id();
        SendMessage result;
        //parse
        try {
            task = Parser.tryParserNotificationTask(message.text());
            task.setChatId(chatId);
        }catch (Exception e){
            result = new SendMessage(chatId, "wrong notification");

            telegramBot.execute(result);
            return;
        }
        //send to db
        notificationTaskRepository.save(task);
        //send response
        result = new SendMessage(chatId,String.format("all right!, %s %s", task.getDateTime(),task.getMessage()));

        telegramBot.execute(result);

    }

    @Scheduled(cron = "0 0/1 * * * *")
    public void NotificationsNow(){
        var time = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        var result = notificationTaskRepository.findByDateTime(time);
        for(var elementInResult : result) {
            var response = new SendMessage(elementInResult.getChatId(), elementInResult.getMessage());
            telegramBot.execute(response);
        }
    }
}
