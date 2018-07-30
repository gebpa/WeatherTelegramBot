package com.telegram_bot.Configurations;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@EnableConfigurationProperties
@ConfigurationProperties(prefix="configs")
public class BotConfiguration {
    private String botToken;
    private String botUserName;
    private String apiForecast;
    private String apiWeather;
    private String apiZoneId;
    private String apiGeo;

    public String getBotToken() {
        return botToken;
    }

    public void setBotToken(String botToken) {
        this.botToken = botToken;
    }

    public String getBotUserName() {
        return botUserName;
    }

    public void setBotUserName(String botUserName) {
        this.botUserName = botUserName;
    }

    public String getApiForecast() {
        return apiForecast;
    }

    public void setApiForecast(String apiForecast) {
        this.apiForecast = apiForecast;
    }

    public String getApiWeather() {
        return apiWeather;
    }

    public void setApiWeather(String apiWeather) {
        this.apiWeather = apiWeather;
    }

    public String getApiZoneId() {
        return apiZoneId;
    }

    public void setApiZoneId(String apiZoneId) {
        this.apiZoneId = apiZoneId;
    }

    public String getApiGeo() {
        return apiGeo;
    }

    public void setApiGeo(String apiGeo) {
        this.apiGeo = apiGeo;
    }
}
