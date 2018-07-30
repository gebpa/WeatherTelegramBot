package com.telegram_bot.Entities;

import javax.persistence.*;

@Entity
@Table
public class Subscriber {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private long telegramId;
    private String name;
    private String city;
    private String countryCode;
    private String timeZoneId;
    private int periodOfNotice;
    private String previousCommand;


    public Subscriber() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getTelegramId() {
        return telegramId;
    }

    public void setTelegramId(long telegramId) {
        this.telegramId = telegramId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getTimeZoneId() {
        return timeZoneId;
    }

    public void setTimeZoneId(String timeZoneId) {
        this.timeZoneId = timeZoneId;
    }

    public int getPeriodOfNotice() {
        return periodOfNotice;
    }

    public void setPeriodOfNotice(int periodOfNotice) {
        this.periodOfNotice = periodOfNotice;
    }

    public String getPreviousCommand() {
        return previousCommand;
    }

    public void setPreviousCommand(String previousCommand) {
        this.previousCommand = previousCommand;
    }
}
