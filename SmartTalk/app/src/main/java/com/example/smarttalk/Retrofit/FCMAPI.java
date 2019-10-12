package com.example.smarttalk.Retrofit;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface FCMAPI {

    @Headers({
            "Content-Type: application/json",
            "Authorization: key=AAAAogPTcbA:APA91bEw8_6yyZMqiEtB-S4V7-zD7s76BAoTbH1_dTGl_jzEcz86lnjIQVdGA8KueTEcp8I2nfzaSpOBf7qRBR0eJtv16GcQgTG-9JrZkqRTeAMMDhN3CdlS8gGGonhxhcWBtdXFa-8X"
    })
    @POST("/fcm/send")
    Call<ResponseBody> sendMessage(@Body MessageEntity messageEntity);
}
