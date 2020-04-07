package com.example.smarttalk.schedule.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.smarttalk.Utils;
import com.example.smarttalk.constants.AppConstant;
import com.example.smarttalk.databasehelper.DatabaseHelper;
import com.example.smarttalk.modelclass.Message;
import com.example.smarttalk.retrofit.BaseApplication;
import com.example.smarttalk.retrofit.Data;
import com.example.smarttalk.retrofit.FCMAPI;
import com.example.smarttalk.retrofit.MessageEntity;

import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static android.content.Context.MODE_PRIVATE;
import static com.example.smarttalk.constants.AppConstant.SharedPreferenceConstant.LOGGED_IN_USER_NAME;
import static com.example.smarttalk.constants.AppConstant.SharedPreferenceConstant.LOOGED_IN_USER_ID;
import static com.example.smarttalk.constants.AppConstant.SharedPreferenceConstant.MESSAGE_SUCCESSFULL_SENDED;

public class MyBroadcastReceiver extends BroadcastReceiver {
    String actionUriSMSSend = "com.scheduler.action.SMS_SEND";
    private static final String TAG = "MyBroadcastReceiver";
    Context mcontext;
    String messageID;

    @Override
    public void onReceive(Context context, Intent intent) {
        mcontext = context;
        if (intent.getAction().equals(actionUriSMSSend)) {


            Bundle bundle = intent.getExtras();
            String senderID = bundle.getString("senderID");
            String receiverID = bundle.getString("receiverID");
            String messageBody = bundle.getString("messageBody");
            String senderName = bundle.getString("senderName");
            String timeStamp = bundle.getString("timeStamp");
            messageID = bundle.getString("messageID");


            sendMessage(senderID, receiverID, messageBody, timeStamp, senderName);
        }
    }

    private void sendMessage(String SenderId, final String ReceiverId, String messageBody, String timeStamp, String senderName) {
        Retrofit retrofit = BaseApplication.getRetrofitInstance();
        FCMAPI api = retrofit.create(FCMAPI.class);

        Data data = new Data();
        data.SenderID = SenderId;
        data.ReceiverID = ReceiverId;
        data.Body = messageBody;
        data.MessageID = messageID;
        data.TimeStamp = timeStamp;
        data.Name = senderName;

        MessageEntity messageEntity = new MessageEntity();
        messageEntity.data = data;
        messageEntity.to = "/topics/" + ReceiverId;

        api.sendMessage(messageEntity).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.code() == 200) {
                    DatabaseHelper databaseHelper = new DatabaseHelper(mcontext);

                    Message message = new Message();
                    message.setSenderID(SenderId);
                    message.setConversionID(ReceiverId);
                    message.setMessageID(messageID);
                    message.setBody(messageBody);
                    message.setTimeStamp(timeStamp);
                    message.setDeliveryStatus(MESSAGE_SUCCESSFULL_SENDED);
                    databaseHelper.insert(message);

                    databaseHelper.deleteScheduledMessage(messageID);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {

            }
        });
    }
}


