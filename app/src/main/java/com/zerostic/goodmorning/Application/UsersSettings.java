package com.zerostic.goodmorning.Application;

public class UsersSettings {
    private String theme;
    private String type_of_sleeper;
    private String voice_settings;
    private String weather_settings;
    private String weather;
    private String update_prompt;


    public UsersSettings() {
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public String getType_of_sleeper() {
        return type_of_sleeper;
    }

    public void setType_of_sleeper(String type_of_sleeper) {
        this.type_of_sleeper = type_of_sleeper;
    }

    public String getVoice_settings() {
        return voice_settings;
    }

    public void setVoice_settings(String voice_settings) {
        this.voice_settings = voice_settings;
    }

    public String getWeather_settings() {
        return weather_settings;
    }

    public void setWeather_settings(String weather_settings) {
        this.weather_settings = weather_settings;
    }

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    public UsersSettings(String update_prompt) {
        this.update_prompt = update_prompt;
    }

    public String getUpdate_prompt() {
        return update_prompt;
    }

    public void setUpdate_prompt(String update_prompt) {
        this.update_prompt = update_prompt;
    }
}
