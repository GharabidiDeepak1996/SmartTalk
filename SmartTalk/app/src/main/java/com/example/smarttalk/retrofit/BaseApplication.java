package com.example.smarttalk.retrofit;

import android.app.Application;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class BaseApplication extends Application {

    private static Retrofit retrofit;
    private static final String BASE_URL = "https://fcm.googleapis.com";
//https://fcm.googleapis.com/fcm/send
//https://fcm.googleapis.com/fcm/send/AAAAogPTcbA:APA91bEw8_6yyZMqiEtB-S4V7-zD7s76BAoTbH1_dTGl_jzEcz86lnjIQVdGA8KueTEcp8I2nfzaSpOBf7qRBR0eJtv16GcQgTG-9JrZkqRTeAMMDhN3CdlS8gGGonhxhcWBtdXFa-8X
    @Override
    public void onCreate() {
        super.onCreate();
    }

    public static Retrofit getRetrofitInstance() {
        if (retrofit == null) {


            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
