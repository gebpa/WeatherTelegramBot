package com.telegram_bot.Configurations;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource("classpath:messages.properties")
@ConfigurationProperties
public class TextValues {
    private String setCity;
    private String setPeriod;
    private String unsubscribe;
    private String info;
    private String setFromGeo;
    private String listOfCommands;
    private String cancel;
    private String periodException;
    private String yes;
    private String no;
    private String invitationToEnterCity;
    private String invitationToEnterPeriod;
    private String noCommand;
    private String currentWeather;
    private String cityNotFound;
    private String spellingMistake;
    private String selectedCityIs;
    private String somethingWentWrong;

    public String getSetCity() {
        return setCity;
    }

    public void setSetCity(String setCity) {
        this.setCity = setCity;
    }

    public String getSetPeriod() {
        return setPeriod;
    }

    public void setSetPeriod(String setPeriod) {
        this.setPeriod = setPeriod;
    }

    public String getUnsubscribe() {
        return unsubscribe;
    }

    public void setUnsubscribe(String unsubscribe) {
        this.unsubscribe = unsubscribe;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getSetFromGeo() {
        return setFromGeo;
    }

    public void setSetFromGeo(String setFromGeo) {
        this.setFromGeo = setFromGeo;
    }

    public String getListOfCommands() {
        return listOfCommands;
    }

    public void setListOfCommands(String listOfCommands) {
        this.listOfCommands = listOfCommands;
    }

    public String getCancel() {
        return cancel;
    }

    public void setCancel(String cancel) {
        this.cancel = cancel;
    }

    public String getPeriodException() {
        return periodException;
    }

    public void setPeriodException(String periodException) {
        this.periodException = periodException;
    }

    public String getYes() {
        return yes;
    }

    public void setYes(String yes) {
        this.yes = yes;
    }

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public String getInvitationToEnterCity() {
        return invitationToEnterCity;
    }

    public void setInvitationToEnterCity(String invitationToEnterCity) {
        this.invitationToEnterCity = invitationToEnterCity;
    }

    public String getInvitationToEnterPeriod() {
        return invitationToEnterPeriod;
    }

    public void setInvitationToEnterPeriod(String invitationToEnterPeriod) {
        this.invitationToEnterPeriod = invitationToEnterPeriod;
    }

    public String getNoCommand() {
        return noCommand;
    }

    public void setNoCommand(String noCommand) {
        this.noCommand = noCommand;
    }

    public String getCurrentWeather() {
        return currentWeather;
    }

    public void setCurrentWeather(String currentWeather) {
        this.currentWeather = currentWeather;
    }

    public String getCityNotFound() {
        return cityNotFound;
    }

    public void setCityNotFound(String cityNotFound) {
        this.cityNotFound = cityNotFound;
    }

    public String getSpellingMistake() {
        return spellingMistake;
    }

    public void setSpellingMistake(String spellingMistake) {
        this.spellingMistake = spellingMistake;
    }

    public String getSelectedCityIs() {
        return selectedCityIs;
    }

    public void setSelectedCityIs(String selectedCityIs) {
        this.selectedCityIs = selectedCityIs;
    }

    public String getSomethingWentWrong() {
        return somethingWentWrong;
    }

    public void setSomethingWentWrong(String somethingWentWrong) {
        this.somethingWentWrong = somethingWentWrong;
    }
}
