package com.telegram_bot.Services;

import com.telegram_bot.Entities.Subscriber;
import com.telegram_bot.Repositories.SubscriberRepository;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class SubscriberService {

    @Autowired
    SubscriberRepository subscriberRepository;

    @Autowired
    WeatherService weatherService;

    @Autowired
    CoordinatesService coordService;

    public Subscriber findByTelegramId(long telegramId) {
        return subscriberRepository.findByTelegramId(telegramId);
    }

    public String setCity(long telegramId, String cityAndCountryCode) {
        try {
            System.out.println(cityAndCountryCode);
            String result;
            String city;
            String code;
            String[] cityAndCountry = cityAndCountryCode.split(",");
            if (cityAndCountry.length == 2) {
                city = cityAndCountry[0].trim();
                code = cityAndCountry[1].trim();
            } else {
                code = "";
                city = cityAndCountryCode;
            }
            String timeZoneId = coordService.getTimeZone(city, code);
            if (timeZoneId.equals("I have not found this city")) {
                return "city was not found";
            }
            if (code.length() == 0) {
                String[] zoneAndCode = timeZoneId.split(", ");
                timeZoneId = zoneAndCode[0].trim();
                code = zoneAndCode[1].trim();
            }
            code = code.toLowerCase();
            Subscriber subscriber = findByTelegramId(telegramId);
            subscriber.setCity(city);
            subscriber.setCountryCode(code);
            subscriber.setTimeZoneId(timeZoneId);
            subscriber.setPreviousCommand("cityIsSetted");
            subscriberRepository.save(subscriber);
            result = city+","+code;
            return result;
        } catch (Exception ex) {
            return "exception";
        }
    }

    public String setPeriodOfNotice(long telegramId, String periodOfNotice) {
        try {
            int period = Integer.parseInt(periodOfNotice);
            if (period >= 0 && period <= 96) {
                Subscriber subscriber = findByTelegramId(telegramId);
                subscriber.setPeriodOfNotice(period);
                subscriber.setPreviousCommand("periodOfNoticeIsSetted");
                subscriberRepository.save(subscriber);
                if (period == 1) {
                    return "\u23F0 You will be notified " + period + " hour before changes in weather";
                } else {
                    return "\u23F0 You will be notified " + period + " hours before changes in weather";
                }

            } else throw new Exception();
        } catch (Exception e) {
            return "\u2757 Period of notice should be a positive integer less than 96, I provide forecast only for next 4 days";
        }
    }

    public void saveANewSubscriber(long telegramId, String name) {
        Subscriber subscriber = new Subscriber();
        subscriber.setTelegramId(telegramId);
        subscriber.setName(name);
        subscriberRepository.save(subscriber);
    }

    public void setPreviousCommand(long telegramId, String command) {
        Subscriber subscriber = subscriberRepository.findByTelegramId(telegramId);
        subscriber.setPreviousCommand(command);
        subscriberRepository.save(subscriber);
    }

    public String getPreviousCommand(long telegramId) {
        Subscriber subscriber = subscriberRepository.findByTelegramId(telegramId);
        if (subscriber.getPreviousCommand() != null) {
            return subscriber.getPreviousCommand();
        } else {
            subscriber.setPreviousCommand("/start");
        }
        return "/start";
    }

    public String unsubscribe(long telegramId) {
        Subscriber subscriber = subscriberRepository.findByTelegramId(telegramId);
        subscriber.setPeriodOfNotice(0);
        subscriber.setPreviousCommand("Unsubscribed");
        subscriberRepository.save(subscriber);
        String result = subscriber.getName() + ", you have been unsubscribed from notifications";
        return result;
    }

    public String getInfo(long telegramId) {
        Subscriber subscriber = subscriberRepository.findByTelegramId(telegramId);
        String result = "\uD83D\uDCCE Information:\n\n";
        String city = subscriber.getCity();
        String code = subscriber.getCountryCode();
        if (city == null || code == null) {
            result += "\uD83C\uDF0D City: is not selected\n";
        } else {
            result += "\uD83C\uDF0D City: " + city + ", " + code + "\n";
        }
        int period = subscriber.getPeriodOfNotice();
        if (period == 0) {
            result += "\u23F0 Period of notice: is not defined";
        } else if (period == 1) {
            result += "\u23F0 Period of notice: " + period + " hour\n";
        } else {
            result += "\u23F0 Period of notice: " + period + " hours\n";
        }
        return result;
    }

    public String getCurrentWeather(long telegramId) {
        Subscriber subscriber = subscriberRepository.findByTelegramId(telegramId);
        String city = subscriber.getCity();
        String code = subscriber.getCountryCode();
        if (city == null || code == null) {
            return "\u26a0 Please, select a city";
        }
        JSONObject weather = weatherService.getCurrentWeather(city, code);
        String result = "Weather in " + city + "," + code + " is " + weather.getString("main") + ".\n";
        result += "Temperature: " + weather.getDouble("temp") + " \u2103.\n";
        result += "Humidity: " + weather.getInt("humidity") + " %.\n";
        result += "Wind speed: " + weather.getDouble("wind speed") + " m/s.\n";
        result += "Pressure: " + weather.getDouble("pressure") + " hPA.";
        return result;
    }

    public String getNotification(long telegramId) {
        Subscriber subscriber = findByTelegramId(telegramId);
        String code = subscriber.getCountryCode();
        String city = subscriber.getCity();
        int period = subscriber.getPeriodOfNotice();
        String timeZone = subscriber.getTimeZoneId();
        JSONObject json = weatherService.notifyIfChange(city, code, period, timeZone);
        String result;
        if (period == 1) {
            result = "Weather in " + city + " in " + period + " hour:\n";
        } else result = "Weather in " + city + " in " + period + " hours:\n";
        if (json.has("no changes")) {
            return "no changes";
        } else {
            for (int i = 0; i < json.getJSONArray("array").length(); i++) {
                result += json.getJSONArray("array").getString(i) + "\n";
            }
            return result;
        }
    }

    public String setCityFromCoordinates(long telegramId, double lat, double lon){
        JSONObject json = coordService.getCityFromCoordinates(lat, lon);
        String city =json.getString("city");
        String code = json.getString("code").toLowerCase();
        String state = json.getString("state");
        String timeZoneId = coordService.getTimeZone(state, code);
        System.out.println(state);
        System.out.println(city);
        if (timeZoneId.equals("I have not found this city")) {
            timeZoneId=coordService.getTimeZone(city, code);
            if (timeZoneId.equals("I have not found this city")){
                return "I have not found this city";
            }
            else {
                return city+","+code;
            }
        }
        return state+","+code;
    }
}
