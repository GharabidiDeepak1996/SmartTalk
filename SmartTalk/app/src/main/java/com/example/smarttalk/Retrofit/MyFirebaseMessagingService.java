package com.example.smarttalk.Retrofit;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.smarttalk.database.DatabaseHelper.DatabaseHelper;
import com.example.smarttalk.database.model.Message;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONObject;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseMessagingServ";

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived( remoteMessage );

        Log.d( TAG, "onMessageReceived: "+remoteMessage.getData() );
        try {
            //Receiver  receive the message.
            JSONObject jsonObject = new JSONObject( remoteMessage.getData() );
            DatabaseHelper md = new DatabaseHelper( this );
            Message message=new Message();
            message.setSenderID( jsonObject.getString( "SenderID" ) );
            message.setConversionID(jsonObject.getString( "ReceiverID" ) );
            message.setMessageID( jsonObject.getString( "MessageID" ) );
            message.setBody( jsonObject.getString( "Body" ) );
            Log.d( TAG, "onMessageReceived: "+ jsonObject.getString( "Body" ) );
            message.setTimeStamp(jsonObject.getString( "TimeStamp" ) );
            md.insert( message );

        } catch (Exception e) {

        }


    }
    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed token: " + token);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        //nothing.

    }
}


