package com.example.lenovo.fourfirenotice.model;

import com.example.lenovo.fourfirenotice.model.gson.Weather;

/**
 * Created by lenovo on 2017/12/11.
 */

public interface InterModel
{
    boolean handleProvinceResponse(String response);
    boolean handleCityResponse(String response,int provinceID);
    boolean handleCountyResponse(String response,int cityId);
    Weather handleWeatherResponse(String response);
    void sendOkHttpRequest(String address,okhttp3.Callback callback);
}
