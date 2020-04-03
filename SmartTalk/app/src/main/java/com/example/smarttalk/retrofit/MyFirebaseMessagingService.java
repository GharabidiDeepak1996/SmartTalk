package com.example.smarttalk.retrofit;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.smarttalk.activity.MessageActivity;
import com.example.smarttalk.R;
import com.example.smarttalk.databasehelper.DatabaseHelper;
import com.example.smarttalk.modelclass.Message;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONObject;

import java.util.Random;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseMessagingServ";


    public String MessaageBody,Title,sernderID;
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived( remoteMessage );
        int notificationID = new Random().nextInt(3000);
        Log.d( TAG, "onMessageReceived: "+remoteMessage.getData() );
        try {
            //Receiver  receive the message.
            JSONObject jsonObject = new JSONObject( remoteMessage.getData() );
            MessaageBody=jsonObject.getString( "Body" ) ;
            Title=jsonObject.getString( "Name" );
            sernderID=jsonObject.getString( "SenderID" );
            DatabaseHelper md = new DatabaseHelper( this );
            Message message=new Message();

            message.setSenderID( jsonObject.getString( "SenderID" ) );
            message.setConversionID(jsonObject.getString( "SenderID" ) );
            message.setMessageID( jsonObject.getString( "MessageID" ) );
            message.setBody( jsonObject.getString( "Body" ) );
            Log.d( TAG, "onMessageReceived: "+ jsonObject.getString( "Body" ) );
            message.setTimeStamp(jsonObject.getString( "TimeStamp" ) );
            md.insert( message );

        } catch (Exception e) {

        }
        //notification channelid set on contactfragment.KzkxODkyODgxNDkxMg
        Log.d(TAG, "onMessageReceived56: "+sernderID);
        Intent intent = new Intent(getApplicationContext(), MessageActivity.class);
        Bundle bundle=new Bundle();
        bundle.putString("hisUID",sernderID);
        intent.putExtras(bundle);
       intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent=PendingIntent.getActivity(
                this,
                0,
                intent,
              PendingIntent.FLAG_ONE_SHOT );
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this,"mynotification")
                .setContentTitle(Title)
                .setContentText(MessaageBody)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setStyle(new NotificationCompat.BigTextStyle())
                .setSound( RingtoneManager.getDefaultUri( RingtoneManager.TYPE_NOTIFICATION))
                .setSmallIcon( R.mipmap.smarttalk)
                .setContentIntent( pendingIntent )
                .setDefaults( Notification.DEFAULT_SOUND)
                .setAutoCancel(true);



        NotificationManager notificationManager =
                ( NotificationManager ) getSystemService( Context.NOTIFICATION_SERVICE);

        notificationManager.notify(notificationID, notificationBuilder.build());

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


