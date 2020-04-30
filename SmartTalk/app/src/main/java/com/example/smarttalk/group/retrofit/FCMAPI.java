package com.example.smarttalk.group.retrofit;

import com.example.smarttalk.retrofit.MessageEntity;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface FCMAPI {
    //https://developer.mozilla.org/en-US/docs/Web/HTTP/Status/401

    @Headers({
            "Content-Type: application/json",
            "Authorization: key=AAAAogPTcbA:APA91bHR5MWI8T_1_vQ3aY9LBjK7ZTYvO-zgOw1OyHWMdijhHo7xClVe8y4_UgMXGz5zbynWOqf1DSL7N4EZPTpOSBKtSFVnCHOFaqrDLb9jX1Lm97-6FugZC4UwDFX_USOb4PJpTXvM"
    })

    @POST("/fcm/send")
    Call<ResponseBody> sendMessage(@Body GroupEntity groupEntity);
}
