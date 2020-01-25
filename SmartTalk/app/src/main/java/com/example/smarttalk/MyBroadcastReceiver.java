package com.example.smarttalk;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.smarttalk.R;
import com.example.smarttalk.constants.AppConstant;
import com.example.smarttalk.databasehelper.DatabaseHelper;
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
import static com.example.smarttalk.constants.AppConstant.SharedPreferenceConstant.LOOGED_IN_USER_ID;
import static com.example.smarttalk.constants.AppConstant.SharedPreferenceConstant.MESSAGE_SUCCESSFULL_SENDED;

public class MyBroadcastReceiver extends BroadcastReceiver {
    String actionUriSMSSend = "com.scheduler.action.SMS_SEND";
    private static final String TAG = "MyBroadcastReceiver";
    String MessageID,timeStamp,name;
DatabaseHelper databaseHelper;
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(actionUriSMSSend)) {
            SharedPreferences preferences = context.getSharedPreferences(AppConstant.SharedPreferenceConstant.SHARED_PREF_NAME, MODE_PRIVATE);
            String SenderID = preferences.getString(LOOGED_IN_USER_ID, "");
             MessageID = Utils.generateUniqueMessageId();
            //Timestamp.
            SimpleDateFormat sdf = new SimpleDateFormat("h:mm a");
            timeStamp = sdf.format(new Date());
               name="deeak";

            Bundle bundle = intent.getExtras();
            String message1 = bundle.getString("message");
             String receiverID = bundle.getString("receiver");
            Log.d(TAG, "onReceive12: "+receiverID);

          sendMessage(SenderID,receiverID,message1);
        }
    }

        private void sendMessage(String SenderId, final String ReceiverId, String message1) {
            Retrofit retrofit = BaseApplication.getRetrofitInstance();
            FCMAPI api = retrofit.create(FCMAPI.class);
            //modelcLass
            Data data = new Data();
            data.SenderID = SenderId;
            data.ReceiverID = ReceiverId;
            data.Body = message1;
            data.MessageID = MessageID;
            data.TimeStamp = timeStamp;
            data.Name = name;

            MessageEntity messageEntity = new MessageEntity();
            messageEntity.data = data;
            messageEntity.to = "/topics/" + ReceiverId;

            api.sendMessage(messageEntity).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                    Log.d(TAG, "onResponse: " + response.code());
                    if (response.code() == 200) {

                        //Log.d( TAG, "onResponse: MessageEntity send successfully" );
                        // Log.d(TAG, "onResponse: ");
                      databaseHelper.updateMessagestatus(MESSAGE_SUCCESSFULL_SENDED, MessageID);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                    // databaseHelper.Pendingmessagesupdate();

                }
            });
        }
    }


