package com.telegram_bot.Services;

import com.telegram_bot.Entities.Subscriber;
import com.telegram_bot.Repositories.SubscriberRepository;
import com.telegram_bot.WeatherBot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class NotificationService {

    @Autowired
    SubscriberRepository subscriberRepository;

    @Autowired
    WeatherBot weatherBot;

    //repeat every hour
    @Scheduled(fixedDelay = 3600000)
    public void sendForecast(){
        List<Subscriber> list = subscriberRepository.findAll();
        for(Subscriber subscriber : list){
            if (subscriber.getCountryCode()!=null && subscriber.getCity()!=null && subscriber.getPeriodOfNotice()!=0){
                    weatherBot.sendNotification(subscriber.getTelegramId());
                try {
                    Thread.sleep(1000);
                } catch(InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

}
