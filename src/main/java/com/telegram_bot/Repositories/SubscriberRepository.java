package com.telegram_bot.Repositories;

import com.telegram_bot.Entities.Subscriber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubscriberRepository extends JpaRepository<Subscriber, Long> {

    Subscriber findByTelegramId(long telegramId);
}
