package com.zerostic.goodmorning.Application;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherAPI {
    @GET("weather")
    Call<WeatherData> getWeather(@Query("q") String cityname,
                                 @Query("appid") String apikey);

}
