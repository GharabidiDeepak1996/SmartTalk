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
import android.widget.Toast;

import androidx.annotation.LongDef;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.smarttalk.activity.MessageActivity;
import com.example.smarttalk.R;
import com.example.smarttalk.databasehelper.DatabaseHelper;
import com.example.smarttalk.group.pojo.GroupMessages;
import com.example.smarttalk.modelclass.Message;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
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
    String groupName,groupImage,groupID,groupMessage,senderNameG,senderIDG,messageID,timeStamp;
    public int USER_NOTIFICATION_REQUEST;
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d(TAG, "onMessageReceived: "+remoteMessage.getData());

        int notificationID = new Random().nextInt(3000);
        //group Activity
        JSONObject jsonObject2 = new JSONObject(remoteMessage.getData());
        try {
            groupID=jsonObject2.getString("groupID");
            groupName=jsonObject2.getString("groupName");
            groupImage=jsonObject2.getString("groupImage");
            groupMessage=jsonObject2.getString("body");
            senderIDG=jsonObject2.getString("senderID");
            senderNameG=jsonObject2.getString("senderName");
            messageID=jsonObject2.getString("messageID");
            timeStamp=jsonObject2.getString("timeStamp");
            if (groupID.equals(jsonObject2.getString("groupID"))) {
                USER_NOTIFICATION_REQUEST=100;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //single Activity
        JSONObject jsonObject1 = new JSONObject(remoteMessage.getData());
        try {

            MessaageBody = jsonObject1.getString("Body");
            senderName = jsonObject1.getString("SenderName");
            sernderID = jsonObject1.getString("SenderID");
            senderImageURL = jsonObject1.getString("SenderImage");
            senderMobilenumber = jsonObject1.getString("SenderMobileNumber");
            Log.d(TAG, "onMessageReceived45: "+sernderID);
        } catch (Exception e) {
            Log.d(TAG, "onMessageReceived: " + e);
        }
        try {
            //Receiver  receive the message.
            JSONObject jsonObject = new JSONObject(remoteMessage.getData());

            DatabaseHelper md = new DatabaseHelper(this);
            Message message = new Message();
            message.setSenderID(jsonObject.getString("SenderID"));
            message.setConversionID(jsonObject.getString("SenderID"));
            message.setMessageID(jsonObject.getString("MessageID"));
            message.setBody(jsonObject.getString("Body"));
            message.setTimeStamp(jsonObject.getString("TimeStamp"));
            md.insert(message);

        } catch (Exception e) {

            Log.d(TAG, "onMessageReceived: " + e);
        }

        //notification channelid and its regis in contactFragment PendingIntent.FLAG_ONE_SHOT PendingIntent.FLAG_UPDATE_CURRENT
        Intent intent = new Intent(getApplicationContext(), MessageActivity.class);
        intent.putExtra("ReceiverUserID", sernderID);
        intent.putExtra("imageView", senderImageURL);
        intent.putExtra("name", senderName);
        intent.putExtra("number", senderMobilenumber);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        bitmap = getBitmapfromUrl(senderImageURL);
        bitmap=getBitmapfromGroupUrl(groupImage);

        // String firebase="https://firebasestorage.googleapis.com/v0/b/smarttalk-44aa3.appspot.com/o/conversion_images%2FLXEB0148WSGJVJ7T?alt=media&token=dfa6debf-0091-4455-8d82-55e28c080dbf";
        if (MessaageBody != null) {
            if (MessaageBody.length() > 38) {
                firstThirtyEightChars = MessaageBody.substring(0, 38);
            } else {
                firstThirtyEightChars = MessaageBody;
            }
        } else {
            Log.d(TAG, "onMessageReceived: ");
        }


        if (firstThirtyEightChars.equals("https://firebasestorage.googleapis.com")) {
            messgae = "Send You Photo......";
        } else {
            messgae = MessaageBody;
        }
        //text
        NotificationCompat.BigTextStyle textStyle = new NotificationCompat.BigTextStyle();
        textStyle.setBigContentTitle(senderName);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        try {
            if(sernderID.equals(jsonObject1.getString("SenderID"))){
                USER_NOTIFICATION_REQUEST=101;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        switch ( USER_NOTIFICATION_REQUEST){
            case 101:
                Log.d(TAG, "onMessageReceived12:"+"single");
                NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, "mynotification")
                        .setSmallIcon(R.mipmap.smarttalk)
                        .setLargeIcon(bitmap)
                        .setContentTitle(senderName)
                        .setContentText(messgae)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setStyle(textStyle)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent)
                        .setDefaults(Notification.DEFAULT_SOUND)
                        .setAutoCancel(true);
                NotificationManager notificationManager =
                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                notificationManager.notify(notificationID, notificationBuilder.build());
                break;
            case 100:
                Log.d(TAG, "onMessageReceived12:"+"group"+groupID+"1-->"+senderIDG+"2.----"+senderNameG+"3.--->"+messageID+"4.-->"+groupMessage+"5-->"+timeStamp);

                DatabaseHelper databaseHelper = new DatabaseHelper(this);
                GroupMessages groupMessages = new GroupMessages();
                groupMessages.setGroupID(groupID);
                groupMessages.setSenderID(senderIDG);
                groupMessages.setSenderName(senderNameG);
                groupMessages.setMessageID(messageID);
                groupMessages.setMessageBody(groupMessage);
                groupMessages.setTimeStamp(timeStamp);
               databaseHelper.groupMessageInsert(groupMessages);
                NotificationCompat.Builder notificationBuilder2 = new NotificationCompat.Builder(this, "mynotification")
                        .setSmallIcon(R.mipmap.smarttalk)
                        .setLargeIcon(bitmap)
                        .setContentTitle(groupName)
                        .setContentText(groupMessage)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setStyle(textStyle)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent)
                        .setDefaults(Notification.DEFAULT_SOUND)
                        .setAutoCancel(true);
                NotificationManager notificationManager2 =
                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                notificationManager2.notify(notificationID, notificationBuilder2.build());

            break;
            default:
                Toast.makeText(this,"Something went worng",Toast.LENGTH_LONG).show();
        }

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
    public Bitmap getBitmapfromGroupUrl(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
             bitmap = BitmapFactory.decodeStream(input);
            return bitmap;

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;

        }

    }

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
    }
}
