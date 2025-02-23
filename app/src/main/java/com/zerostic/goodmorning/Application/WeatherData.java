package com.zerostic.goodmorning.Application;

import com.google.gson.annotations.SerializedName;

public class WeatherData {
    @SerializedName("main")
    Main main;

    public Main getMain() {
        return main;
    }

    public void setMain(Main main) {
        this.main = main;
    }
}
