package com.telegram_bot;

import com.telegram_bot.Configurations.BotConfiguration;
import com.telegram_bot.Configurations.TextValues;
import com.telegram_bot.Services.SubscriberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

@Component
public class WeatherBot extends TelegramLongPollingBot {

    @Autowired
    BotConfiguration config;

    @Autowired
    SubscriberService subscriberService;

    @Autowired
    TextValues textValues;

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            long telegramId = update.getMessage().getChatId();
            String userName;
            if (update.getMessage().getFrom().getFirstName() != null) {
                userName = update.getMessage().getFrom().getFirstName();
            } else if (update.getMessage().getFrom().getUserName() != null) {
                userName = update.getMessage().getFrom().getUserName();
            } else userName = "Anonymous";
            if (subscriberService.findByTelegramId(telegramId) == null) {
                subscriberService.saveANewSubscriber(telegramId, userName);
            }
            String previousCommand = subscriberService.getPreviousCommand(telegramId);
            if (update.getMessage().hasText()) {
                String messageFromUser = update.getMessage().getText();
                messageFromUser = emojiDeleter(messageFromUser);
                if (messageFromUser.equals("/start") || messageFromUser.equals("Cancel")) {
                    String[] buttons = {textValues.getSetCity(), textValues.getSetPeriod(), textValues.getInfo(), textValues.getUnsubscribe(), textValues.getSetFromGeo(), textValues.getCurrentWeather()};
                    sendKeyboard(telegramId, textValues.getListOfCommands(), buttons, 4);
                    subscriberService.setPreviousCommand(telegramId, messageFromUser);
                } else if (previousCommand.equals("Set city")) {
                    String result = subscriberService.setCity(telegramId, messageFromUser);
                    if (result.equals("city was not found")) {
                        String[] buttons = {textValues.getCancel()};
                        sendKeyboard(telegramId, textValues.getCityNotFound() + "\n" + textValues.getSpellingMistake(), buttons, -1);
                    } else if (result.equals("exception")) {
                        String[] buttons = {textValues.getCancel()};
                        sendKeyboard(telegramId, textValues.getSomethingWentWrong(), buttons, -1);
                    } else {
                        String[] buttons = {textValues.getSetCity(), textValues.getSetPeriod(), textValues.getInfo(), textValues.getUnsubscribe(), textValues.getSetFromGeo(), textValues.getCurrentWeather()};
                        sendKeyboard(telegramId, textValues.getSelectedCityIs().replaceAll("city,code",result) + "\n\n" + textValues.getListOfCommands(), buttons, 4);
                    }
                } else if (previousCommand.equals("Set period of notice")) {
                    String result = subscriberService.setPeriodOfNotice(telegramId, messageFromUser);
                    if (result.equals(textValues.getPeriodException())) {
                        String[] buttons = {textValues.getCancel()};
                        sendKeyboard(telegramId, result, buttons, -1);
                    } else {
                        String[] buttons = {textValues.getSetCity(), textValues.getSetPeriod(), textValues.getInfo(), textValues.getUnsubscribe(), textValues.getSetFromGeo(), textValues.getCurrentWeather()};
                        sendKeyboard(telegramId, result + "\n\n" + textValues.getListOfCommands(), buttons, 4);
                    }
                } else if (previousCommand.contains("possible")) {
                    if (messageFromUser.equals("Yes")) {
                        String cityAndCode = previousCommand.replaceAll("possible ", "");
                        String result = subscriberService.setCity(telegramId, cityAndCode);
                        if (result.equals("city was not found")) {
                            String[] buttons = {textValues.getCancel()};
                            sendKeyboard(telegramId, textValues.getCityNotFound(), buttons, -1);
                        } else if (result.equals("exception")) {
                            String[] buttons = {textValues.getCancel()};
                            sendKeyboard(telegramId, textValues.getSomethingWentWrong(), buttons, -1);
                        } else {
                            String[] buttons = {textValues.getSetCity(), textValues.getSetPeriod(), textValues.getInfo(), textValues.getUnsubscribe(), textValues.getSetFromGeo(), textValues.getCurrentWeather()};
                            sendKeyboard(telegramId, textValues.getSelectedCityIs().replaceAll("city,code",result) + "\n" + textValues.getListOfCommands(), buttons, 4);
                        }
                    } else if (messageFromUser.equals("No")) {
                        String[] buttons = {textValues.getSetCity(), textValues.getSetPeriod(), textValues.getInfo(), textValues.getUnsubscribe(), textValues.getSetFromGeo(), textValues.getCurrentWeather()};
                        sendKeyboard(telegramId, textValues.getListOfCommands(), buttons, 4);
                        subscriberService.setPreviousCommand(telegramId, messageFromUser);
                    } else {
                        String[] buttons = {textValues.getYes(), textValues.getNo(), textValues.getCancel()};
                        sendKeyboard(telegramId, textValues.getNoCommand(), buttons, -1);
                    }
                } else if (messageFromUser.equals("Set city")) {
                    String[] buttons = {textValues.getCancel()};
                    sendKeyboard(telegramId, userName + textValues.getInvitationToEnterCity(), buttons, -1);
                    subscriberService.setPreviousCommand(telegramId, messageFromUser);
                } else if (messageFromUser.equals("Set period of notice")) {
                    String[] buttons = {textValues.getCancel()};
                    sendKeyboard(telegramId, userName + textValues.getInvitationToEnterPeriod(), buttons, -1);
                    subscriberService.setPreviousCommand(telegramId, messageFromUser);
                } else if (messageFromUser.equals("Unsubscribe from notifications")) {
                    String result = subscriberService.unsubscribe(telegramId);
                    String[] buttons = {textValues.getSetCity(), textValues.getSetPeriod(), textValues.getInfo(), textValues.getSetFromGeo(), textValues.getCurrentWeather()};
                    sendKeyboard(telegramId, result + "\n\n" + textValues.getListOfCommands(), buttons, 3);
                } else if (messageFromUser.equals("Info about notifications")) {
                    String result = subscriberService.getInfo(telegramId);
                    String[] buttons = {textValues.getSetCity(), textValues.getSetPeriod(), textValues.getUnsubscribe(), textValues.getSetFromGeo(), textValues.getCurrentWeather()};
                    sendKeyboard(telegramId, result + "\n\n" + textValues.getListOfCommands(), buttons, 3);
                } else if (messageFromUser.equals("Current weather")) {
                    String result = subscriberService.getCurrentWeather(telegramId);
                    String[] buttons = {textValues.getSetCity(), textValues.getSetPeriod(), textValues.getInfo(), textValues.getUnsubscribe(), textValues.getSetFromGeo(), textValues.getCurrentWeather()};
                    sendKeyboard(telegramId, result + "\n\n" + textValues.getListOfCommands(), buttons, 4);
                } else {
                    String[] buttons = {textValues.getSetCity(), textValues.getSetPeriod(), textValues.getInfo(), textValues.getUnsubscribe(), textValues.getSetFromGeo(), textValues.getCurrentWeather()};
                    sendKeyboard(telegramId, textValues.getNoCommand() + "\n" + textValues.getListOfCommands(), buttons, 4);
                }
            } else if (update.getMessage().hasLocation() && !previousCommand.contains("Set ")) {
                double lon = update.getMessage().getLocation().getLongitude();
                double lat = update.getMessage().getLocation().getLatitude();
                String cityAndCode = subscriberService.setCityFromCoordinates(telegramId, lat, lon);
                subscriberService.setPreviousCommand(telegramId, "possible " + cityAndCode);
                String[] buttons = {textValues.getYes(), textValues.getNo()};
                sendKeyboard(telegramId, userName + ", is your city " + cityAndCode + "?", buttons, -1);
            }

        }
    }


    public void sendNotification(long telegramId) {
        String message = subscriberService.getNotification(telegramId);
        if (!message.equals("no changes")) {
            SendMessage sendMessage = new SendMessage()
                    .setText(message)
                    .setChatId(telegramId);
            try {
                execute(sendMessage);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendKeyboard(long telegramId, String message, String[] buttons, int isGeo) {
        SendMessage sendMessage = new SendMessage()
                .setText(message)
                .setChatId(telegramId);

        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup()
                .setSelective(true)
                .setResizeKeyboard(true)
                .setOneTimeKeyboard(true);
        sendMessage.setReplyMarkup(replyKeyboardMarkup);

        List<KeyboardRow> keyboard = new ArrayList<>();

        for (int i = 0; i < buttons.length; i++) {
            KeyboardRow keyboardRow = new KeyboardRow();
            KeyboardButton keyboardButton = new KeyboardButton(buttons[i]);
            if (i == isGeo) {
                keyboardButton.setRequestLocation(true);
            }
            keyboardRow.add(keyboardButton);
            keyboard.add(keyboardRow);
        }

        replyKeyboardMarkup.setKeyboard(keyboard);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

    }

    //this mettod deletes all emojis from message and trims it
    public String emojiDeleter(String str) {
        String characterFilter = "[^\\p{L}\\p{M}\\p{N}\\p{P}\\p{Z}\\p{Cf}\\p{Cs}\\s]";
        String emotionless = str.replaceAll(characterFilter, "");
        return emotionless.trim();
    }

    @Override
    public String getBotToken() {
        return config.getBotToken();
    }

    @Override
    public String getBotUsername() {
        return config.getBotUserName();
    }
}