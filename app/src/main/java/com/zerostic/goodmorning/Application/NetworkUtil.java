package com.zerostic.goodmorning.Application;

import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class NetworkUtil {
    private static final String TAG = "NetworkUtils";

    private final static String WEATHER_BASE_URL = "http://dataservice.accuweather.com/forecasts/v1/daily/5day/202396";
    private final static String API_KEY = "4dWsoNQd0xd9oFAdZQGXX9eK4rXbzXen";
    private final static String METRIC_VALUE = "true";

    private final static String PARAM_API_KEY = "apikey";

    private final static String PARAM_METRIC = "metric";

    public static URL buildUrlForWeather(){
        Uri builtUri = Uri.parse(WEATHER_BASE_URL).buildUpon()
                .appendQueryParameter(PARAM_API_KEY, API_KEY)
                .appendQueryParameter(PARAM_METRIC, METRIC_VALUE)
                .build();
        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.i(TAG, "buildUrlForWeather: url: "+url);
        return url;
    }

    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in  = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if(hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }
}
