package com.example.smarttalk.database.DatabaseHelper;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.smarttalk.database.model.Contact;
import com.example.smarttalk.database.model.Message;

import java.util.ArrayList;
import java.util.List;

import static com.example.smarttalk.MessageActivity.THIS_BROADCAST;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME="smarttalk.db";
    public static final int DATABASE_VERSION=1;
    private static final String TAG = "DatabaseHelper";
    Context context;
    public DatabaseHelper(@Nullable Context context) {
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
        this.context=context;
    }
    public static class Contacts{
        public static final String TABLE_NAME="contact";
        public static final String USER_ID="user_id";
        public static final String FIRST_NAME="user_name";
        public static final String LAST_NAME="user_sur_name";
        public static final String MOBILE_NUMBER="user_mobile";
    }
    public class Messages {
        //https://www.androidhive.info/2011/11/android-sqlite-database-tutorial/
        public static final String TABLE_NAME = "MESSAGE";
        public static final String SR_NO_ID = "SR.NO ID";
        public static final String SenderID = "SENDERID";
        public static final String conversionID = "CONVERSIONID";
        public static final String MessageID = "MESSAGEID";
        public static final String Body = "BODY";
        public static final String TimeStamp = "TIMESTAMP";

    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String Contactsquery="create table "+Contacts.TABLE_NAME+" ( "+
                Contacts.USER_ID+" text , "+
                Contacts.FIRST_NAME+" text , "+
                Contacts.LAST_NAME +" text , "+
                Contacts.MOBILE_NUMBER+" text UNIQUE "+
                ");";
        sqLiteDatabase.execSQL(Contactsquery);

        String Messagesquery = "CREATE TABLE " + Messages.TABLE_NAME + "( "
                + Messages.SenderID + " TEXT, "
                + Messages.conversionID + " TEXT, "
                + Messages.MessageID + " TEXT, "
                + Messages.Body + " TEXT, "
                + Messages.TimeStamp + " TEXT"
                + ")";
        sqLiteDatabase.execSQL( Messagesquery );
    }
    //Message
    public void insert(Message message) {

        SQLiteDatabase db = this.getWritableDatabase();    //write into the table
        ContentValues contentValues = new ContentValues();  //insert the values in rows


        contentValues.put( Messages.SenderID, message.getSenderID() );
        contentValues.put( Messages.conversionID, message.getConversionID() );
        contentValues.put( Messages.MessageID, message.getMessageID() );
        contentValues.put( Messages.Body, message.getBody() );
        contentValues.put( Messages.TimeStamp, message.getTimeStamp() );

        long row = db.insert( Messages.TABLE_NAME, null, contentValues ); //row count number of rows inserted
        Log.d( TAG, "insert row: " + row );

        Intent intent = new Intent( THIS_BROADCAST );
        intent.putExtra( "MessageID", message.getMessageID() );
        context.sendBroadcast( intent );
    }

    //contact
    public void insert(Contact contact)
    {
        SQLiteDatabase database=this.getWritableDatabase();
        ContentValues contentValues= new ContentValues();
        contentValues.put( Contacts.USER_ID,contact.getUserID() );
        contentValues.put(Contacts.FIRST_NAME,contact.getFirstName());
        contentValues.put(Contacts.LAST_NAME,contact.getLastName());
        contentValues.put(Contacts.MOBILE_NUMBER,contact.getMobileNmuber());
        long row=database.insert( Contacts.TABLE_NAME,null,contentValues );

        Log.d(TAG,"Inside insertStudent() -> Row : "+row);
    }


    public List<Contact> display(){
        List<Contact> ContactList = new ArrayList<>(  );
        String query = "SELECT * FROM " + Contacts.TABLE_NAME;

        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery( query, null );
        StringBuffer stringBuffer = new StringBuffer();
        // Student student1=null; optional

        while(cursor.moveToNext()){
            Contact contact = new Contact();
            String user_id=cursor.getString( cursor.getColumnIndex(Contacts.USER_ID ));
            String first_name=cursor.getString( cursor.getColumnIndex(Contacts.FIRST_NAME ));
            String last_name=cursor.getString( cursor.getColumnIndex(Contacts.LAST_NAME ) );
            String mobile_number= cursor.getString( cursor.getColumnIndex(Contacts.MOBILE_NUMBER));

            Log.d( TAG, "display:First Name: "+ first_name +",Lastname: "+last_name+",Mobilenumber :"+mobile_number );
contact.setUserID( user_id );
            contact.setFirstName( first_name );
            contact.setLastName( last_name );
            contact.setMobileNmuber( mobile_number );

            stringBuffer.append( contact);
            ContactList.add( contact );

        }
        cursor.close();

        return ContactList;
    }
    //messaage ID
    public Message getMessageById(String messageId) {

        SQLiteDatabase database = this.getReadableDatabase();
        String query = "SELECT * FROM " + Messages.TABLE_NAME+" WHERE "+ Messages.MessageID +" = '"+messageId+"' ;";
        Cursor cursor = database.rawQuery(query, null);
        Log.d(TAG, "Cursor Count : " + cursor.getCount());
        Message message = new Message();

        while (cursor.moveToNext()) {
            String senderId = cursor.getString(cursor.getColumnIndex(Messages.SenderID));
            String conversionId = cursor.getString(cursor.getColumnIndex(Messages.conversionID));
            String messageID = cursor.getString(cursor.getColumnIndex(Messages.MessageID));
            String body = cursor.getString(cursor.getColumnIndex(Messages.Body));
            String timeStamp =cursor.getString(cursor.getColumnIndex(Messages.TimeStamp));

            message.setSenderID( senderId );
            message.setConversionID( conversionId );
            message.setMessageID( messageID );
            message.setBody( body );
            message.setTimeStamp( timeStamp );
        }
        cursor.close();
        return message;
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
