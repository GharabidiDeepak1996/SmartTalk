package com.example.smarttalk.retrofit;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface FCMAPI {
    //https://codingwithmitch.com/blog/android-firebase-cloud-messages-cloud-function/
    //https://android.jlelse.eu/firebase-push-notification-fe1e03119b77

    @Headers({
            "Content-Type: application/json",

    })
    @POST("/fcm/send")
    Call<ResponseBody> sendMessage(@Body MessageEntity messageEntity);
}
