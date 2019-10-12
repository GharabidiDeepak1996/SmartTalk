package com.example.smarttalk.database.DatabaseHelper;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.smarttalk.MessageActivity;
import com.example.smarttalk.database.model.Message;

import static com.example.smarttalk.MessageActivity.THIS_BROADCAST;

public class MessageDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "Message.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TAG = "MessageDatabaseHelper";

    Context mcontext;
    public MessageDatabaseHelper(@Nullable Context context) {
        super( context, DATABASE_NAME, null, DATABASE_VERSION );
    }
    public class Messages {
        //https://www.androidhive.info/2011/11/android-sqlite-database-tutorial/
        public static final String TABLE_NAME = "MESSAGE";
        public static final String SR_NO_ID= "SR.NO ID";
        public static final String SenderID = "SENDERID";
        public static final String  conversionID= "CONVERSIONID";
        public static final String MessageID="MESSAGEID";
        public static final String Body="BODY";
        public static final String TimeStamp="TIMESTAMP";

    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String query = "CREATE TABLE " + Messages.TABLE_NAME + "( "
                + Messages.SenderID + " TEXT, "
                + Messages.conversionID + " TEXT, "
                + Messages.MessageID + " TEXT, "
                +Messages.Body + " TEXT, "
                +Messages.TimeStamp + " TEXT"
                +")";
        sqLiteDatabase.execSQL( query );
    }
    public void insert(Message message) {

        SQLiteDatabase db = this.getWritableDatabase();    //write into the table
        ContentValues contentValues = new ContentValues();  //insert the values in rows
        //sr.no id` will be inserted automatically.
        Log.d( TAG, "insert: "+message.getSenderID() );
        contentValues.put( Messages.SenderID, message.getSenderID() );
        contentValues.put( Messages.conversionID, message.getConversionID() );
        contentValues.put( Messages.MessageID, message.getMessageID() );
        contentValues.put( Messages.Body, message.getBody() );
        contentValues.put( Messages.TimeStamp, message.getTimeStamp() );

        long row = db.insert( Messages.TABLE_NAME, null, contentValues ); //row count number of rows inserted
        Log.d( TAG, "insert row: " +row);

        Intent intent=new Intent( THIS_BROADCAST );
        intent.putExtra("MessageID",message.getMessageID());
        Log.d( TAG, "MessageDataBase messageID: "+message.getMessageID() );
        mcontext.sendBroadcast( intent );

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
