package com.example.smarttalk.retrofit;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.smarttalk.activity.MessageActivity;
import com.example.smarttalk.R;
import com.example.smarttalk.databasehelper.DatabaseHelper;
import com.example.smarttalk.modelclass.Message;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseMessagingServ";

    public String MessaageBody,senderName,sernderID,senderImageURL,senderMobilenumber;
    String firstThirtyEightChars = "";
    String messgae="";
    Bitmap bitmap=null;
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived( remoteMessage );
        int notificationID = new Random().nextInt(3000);
        Log.d(TAG, "onMessageReceived: "+"remoteMessage.getFrom()");
        try {
            //Receiver  receive the message.
            JSONObject jsonObject = new JSONObject( remoteMessage.getData() );
            MessaageBody=jsonObject.getString( "Body" ) ;
            senderName=jsonObject.getString( "SenderName" );
            sernderID=jsonObject.getString( "SenderID" );
            senderImageURL=jsonObject.getString( "SenderImage" );
            senderMobilenumber=jsonObject.getString( "SenderMobileNumber" ) ;

            DatabaseHelper md = new DatabaseHelper( this );
            Message message=new Message();
//receiver side
            message.setSenderID( jsonObject.getString( "SenderID" ) );
            message.setConversionID(jsonObject.getString( "SenderID" ) );
            message.setMessageID( jsonObject.getString( "MessageID" ) );
            message.setBody( jsonObject.getString( "Body" ) );
            message.setTimeStamp(jsonObject.getString( "TimeStamp" ) );
            md.insert( message );

        } catch (Exception e) {

        }

        //notification channelid and its regis in contactFragment PendingIntent.FLAG_ONE_SHOT PendingIntent.FLAG_UPDATE_CURRENT
        Intent intent = new Intent(getApplicationContext(), MessageActivity.class);
        intent.putExtra("ReceiverUserID", sernderID);
        intent.putExtra("imageView",senderImageURL);
        intent.putExtra("name",senderName);
        intent.putExtra("number",senderMobilenumber);

        Log.d(TAG, "onMessageReceived84: "+senderImageURL+" 2.-->"+senderName+"3.-->"+sernderID+"4.---->"+senderMobilenumber+"5.-->"+MessaageBody);
        PendingIntent pendingIntent=PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
         bitmap = getBitmapfromUrl(senderImageURL);

       // String firebase="https://firebasestorage.googleapis.com/v0/b/smarttalk-44aa3.appspot.com/o/conversion_images%2FLXEB0148WSGJVJ7T?alt=media&token=dfa6debf-0091-4455-8d82-55e28c080dbf";
        if(MessaageBody.length()>38) {
            firstThirtyEightChars = MessaageBody.substring(0, 38);
        }
        if(firstThirtyEightChars.equals("https://firebasestorage.googleapis.com")){
            messgae="Send You Photo......";
        }else {
            messgae=MessaageBody;
        }
        //text
        NotificationCompat.BigTextStyle textStyle=new NotificationCompat.BigTextStyle();
        textStyle.setBigContentTitle(senderName);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);


        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this,"mynotification")
                 .setSmallIcon( R.mipmap.smarttalk)
                .setLargeIcon(bitmap)
                .setContentTitle(senderName)
                .setContentText(messgae)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setStyle(textStyle)
                .setSound(defaultSoundUri)
                .setContentIntent( pendingIntent )
                .setDefaults( Notification.DEFAULT_SOUND)
                .setAutoCancel(true);
            NotificationManager notificationManager =
                ( NotificationManager ) getSystemService( Context.NOTIFICATION_SERVICE);

        notificationManager.notify(notificationID, notificationBuilder.build());

    }


    public Bitmap getBitmapfromUrl(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(input);
            return bitmap;

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;

        }
    }
}


