package com.example.smarttalk.databasehelper;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.smarttalk.modelclass.Chat;
import com.example.smarttalk.modelclass.Message;
import com.example.smarttalk.modelclass.User;

import java.util.ArrayList;
import java.util.List;

import static com.example.smarttalk.MessageActivity.THIS_BROADCAST;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "smarttalk.db";
    public static final int DATABASE_VERSION = 2;
    private static final String TAG = "DatabaseHelper";
    Context context;

    public DatabaseHelper(@Nullable Context context) {
        super( context, DATABASE_NAME, null, DATABASE_VERSION );
        this.context = context;
    }

    public class Contacts {
        public static final String TABLE_NAME = "contact";
        public static final String ID = "_id";
        public static final String USER_ID = "user_id";
        public static final String FIRST_NAME = "user_name";
        public static final String LAST_NAME = "user_sur_name";
        public static final String MOBILE_NUMBER = "user_mobile";
    }

    public class Messages {
        //https://www.androidhive.info/2011/11/android-sqlite-database-tutorial/
        public static final String TABLE_NAME = "MESSAGE";
        public static final String SR_NO_ID = "ID";
        public static final String SenderID = "SENDERID";
        public static final String conversionID = "CONVERSIONID";
        public static final String MessageID = "MESSAGEID";
        public static final String Body = "BODY";
        public static final String TimeStamp = "TIMESTAMP";
        public static  final String newcol="jhdsgf";
    }

    public class Chats {
        public static final String TABLE_NAME = "Chat";
        public static final String CHAT_ID = "chatid";
        public static final String conversionID = "CONVERSIONID";
        public static final String MessageID = "MESSAGEID";
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        //Table Contacts
        String Contactsquery = "create table " + Contacts.TABLE_NAME + " ( " +
                Contacts.ID + " INTEGER PRIMARY KEY AUTOINCREMENT , " +
                Contacts.USER_ID + " text UNIQUE, " +
                Contacts.FIRST_NAME + " text , " +
                Contacts.LAST_NAME + " text , " +
                Contacts.MOBILE_NUMBER + " text UNIQUE " +
                ");";
        sqLiteDatabase.execSQL( Contactsquery );

        //Table Messages
        String Messagesquery = "CREATE TABLE " + Messages.TABLE_NAME + "( "
                + Messages.SR_NO_ID + " INTEGER PRIMARY KEY AUTOINCREMENT , "
                + Messages.SenderID + " TEXT, "
                + Messages.conversionID + " TEXT, "
                + Messages.MessageID + " TEXT, "
                + Messages.Body + " TEXT, "
                + Messages.TimeStamp + " TEXT"
                + Messages.newcol+ "TEXT"
                + ")";
        sqLiteDatabase.execSQL( Messagesquery );

        //Table Chatalist
        String ChatQuery = "CREATE TABLE " + Chats.TABLE_NAME + " ( " +
                Chats.CHAT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT , " +
                Chats.conversionID + " text UNIQUE, " +
                Chats.MessageID + " text, " +
                "FOREIGN KEY(" + Chats.MessageID + ") REFERENCES " + Messages.TABLE_NAME + "(" + Messages.MessageID + "));";
        sqLiteDatabase.execSQL( ChatQuery );
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

        //  long row = db.insert( Messages.TABLE_NAME, null, contentValues ); //row count number of rows inserted
        long row = db.insertWithOnConflict( Messages.TABLE_NAME, null, contentValues, SQLiteDatabase.CONFLICT_IGNORE );

        Log.d( TAG, "insert row: " + row );
        Intent intent = new Intent( THIS_BROADCAST );
        intent.putExtra( "MessageID", message.getMessageID() );
        intent.putExtra( "ConversionID", message.getConversionID() );
        context.sendBroadcast( intent );
        //Make chatlist model data from message data
        Chat chat = new Chat();
        chat.unseencount = 0;
        chat.message = message;
        insertChats( chat );

    }


    //contact
    public void insert(User contact) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put( Contacts.USER_ID, contact.getUserId() );
        contentValues.put( Contacts.FIRST_NAME, contact.getFirstname() );
        contentValues.put( Contacts.LAST_NAME, contact.getLastname() );
        contentValues.put( Contacts.MOBILE_NUMBER, contact.getMobilenumber() );
        // long row = database.insertWithOnConflict( Contacts.TABLE_NAME, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE );
        long row = database.insertWithOnConflict( Contacts.TABLE_NAME, null, contentValues, SQLiteDatabase.CONFLICT_IGNORE );

        Log.d( TAG, "Inside insertStudent() -> Row : " + row );
    }

    public List<User> display() {
        List<User> ContactList = new ArrayList<>();
        String query = "SELECT * FROM " + Contacts.TABLE_NAME;

        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery( query, null );
        StringBuffer stringBuffer = new StringBuffer();
        Log.d( TAG, "display:string buffer " + stringBuffer );
        // Student student1=null; optional

        while (cursor.moveToNext()) {
            User contact = new User();
            String user_id = cursor.getString( cursor.getColumnIndex( Contacts.USER_ID ) );
            String first_name = cursor.getString( cursor.getColumnIndex( Contacts.FIRST_NAME ) );
            String last_name = cursor.getString( cursor.getColumnIndex( Contacts.LAST_NAME ) );
            String mobile_number = cursor.getString( cursor.getColumnIndex( Contacts.MOBILE_NUMBER ) );

            Log.d( TAG, "display:First Name: " + first_name + ",Lastname: " + last_name + ",Mobilenumber :" + mobile_number );
            contact.setUserId( user_id );
            contact.setFirstname( first_name );
            contact.setLastname( last_name );
            contact.setMobilenumber( mobile_number );

            stringBuffer.append( contact );
            ContactList.add( contact );

        }
        cursor.close();

        return ContactList;
    }

    //messaage ID
    public Message getMessageById(String messageId) {

        SQLiteDatabase database = this.getReadableDatabase();
        String query = "SELECT * FROM " + Messages.TABLE_NAME + " WHERE " + Messages.MessageID + " = '" + messageId + "' ;";
        Cursor cursor = database.rawQuery( query, null );
        Log.d( TAG, "Cursor Count : " + cursor.getCount() );
        Message message = new Message();

        while (cursor.moveToNext()) {
            String senderId = cursor.getString( cursor.getColumnIndex( Messages.SenderID ) );
            String conversionId = cursor.getString( cursor.getColumnIndex( Messages.conversionID ) );
            String messageID = cursor.getString( cursor.getColumnIndex( Messages.MessageID ) );
            String body = cursor.getString( cursor.getColumnIndex( Messages.Body ) );
            String timeStamp = cursor.getString( cursor.getColumnIndex( Messages.TimeStamp ) );

            message.setSenderID( senderId );
            message.setConversionID( conversionId );
            message.setMessageID( messageID );
            message.setBody( body );
            message.setTimeStamp( timeStamp );
        }
        cursor.close();
        return message;
    }

    public List<Message> getConversionID(String conversionID) {
        //Log.d( TAG, "getConversionID: " + conversionID );
        List<Message> MessageList = new ArrayList<>();

        SQLiteDatabase data = this.getReadableDatabase();
        String query = "SELECT * FROM " + Messages.TABLE_NAME + " WHERE " + Messages.conversionID + " = '" + conversionID + "';";
        Cursor cursor = data.rawQuery( query, null );
        Log.d( TAG, "Cursor Count : " + cursor.getCount() );


        while (cursor.moveToNext()) {
            Message message = new Message();

            String senderId = cursor.getString( cursor.getColumnIndex( Messages.SenderID ) );
            String conversionId = cursor.getString( cursor.getColumnIndex( Messages.conversionID ) );
            String messageID = cursor.getString( cursor.getColumnIndex( Messages.MessageID ) );
            String body = cursor.getString( cursor.getColumnIndex( Messages.Body ) );
            String timeStamp = cursor.getString( cursor.getColumnIndex( Messages.TimeStamp ) );

            message.setSenderID( senderId );
            message.setConversionID( conversionId );
            message.setMessageID( messageID );
            message.setBody( body );
            message.setTimeStamp( timeStamp );

            MessageList.add( message );

        }
        cursor.close();
        return MessageList;
    }

    public void insertChats(Chat chat) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put( Chats.conversionID, chat.message.getConversionID() );
        contentValues.put( Chats.MessageID, chat.message.getMessageID() );
        long row = database.insertWithOnConflict( Chats.TABLE_NAME, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE );
        // long row = database.insertWithOnConflict( Chats.TABLE_NAME, null, contentValues, SQLiteDatabase.CONFLICT_IGNORE );
        Log.d( TAG, "insertChats: " + row );
    }

    public List<Chat> chatList() {

        SQLiteDatabase database = this.getReadableDatabase();
        String query = "SELECT * FROM " + Chats.TABLE_NAME +
                " JOIN " + Contacts.TABLE_NAME +
                " ON " + Contacts.USER_ID + " = " + Chats.TABLE_NAME + "." + Chats.conversionID +
                " JOIN " + Messages.TABLE_NAME +
                " ON " + Messages.TABLE_NAME + "." + Messages.MessageID + " = " + Chats.TABLE_NAME + "." + Chats.MessageID +
                " ORDER BY " +Messages.TimeStamp+ " DESC"
                + " ;";
        Log.d( TAG, " chatList() ==> Query : " + query );

        Cursor cursor = database.rawQuery( query, null );
        Log.d( TAG, "chatList() ==>> cursor_count: " + cursor.getCount() );
        List<Chat> chatList = new ArrayList<>();

        while (cursor.moveToNext()) {
            Chat chat = new Chat();
            String chatId = cursor.getString( cursor.getColumnIndex( Chats.CHAT_ID ) );
            String conversionId = cursor.getString( cursor.getColumnIndex( Chats.conversionID ) );
            String messageID = cursor.getString( cursor.getColumnIndex( Chats.MessageID ) );

            chat.setChatID( Integer.parseInt( chatId ) );
            Message message = new Message();
            message.setConversionID( conversionId );
            message.setMessageID( messageID );
            message.setBody( cursor.getString( cursor.getColumnIndex( Messages.Body ) ) );
            message.setTimeStamp( cursor.getString( cursor.getColumnIndex( Messages.TimeStamp ) ) );
            chat.message = message;

            User mUser = new User();
            mUser.setFirstname( cursor.getString( cursor.getColumnIndex( Contacts.FIRST_NAME ) ) );
            mUser.setLastname( cursor.getString( cursor.getColumnIndex( Contacts.LAST_NAME ) ) );
            mUser.setUserId( cursor.getString( cursor.getColumnIndex( Contacts.USER_ID ) ) );
            chat.user = mUser;
            chatList.add( chat );
        }
        cursor.close();
        return chatList;

    }

    public Chat getChatByConversationId(String conversionID) {
        Log.d( TAG, "getChatByConversationId: " + conversionID );

        SQLiteDatabase database = this.getReadableDatabase();
        String query = "SELECT * FROM " + Chats.TABLE_NAME +
                " JOIN " + Contacts.TABLE_NAME +
                " ON " + Contacts.USER_ID + " = " + Chats.TABLE_NAME + "." + Chats.conversionID +
                " JOIN " + Messages.TABLE_NAME +
                " ON " + Messages.TABLE_NAME + "." + Messages.MessageID + " = " + Chats.TABLE_NAME + "." + Chats.MessageID + " WHERE " + Chats.TABLE_NAME + "." + Chats.conversionID + " ='" + conversionID + "';";
        Log.d( TAG, " Query : " + query );

        Cursor cursor = database.rawQuery( query, null );
        Log.d( TAG, "getChatByConversationId() ==> cursor_count: " + cursor.getCount() );
        cursor.moveToFirst();
        Chat chat = new Chat();
        String chatId = cursor.getString( cursor.getColumnIndex( Chats.CHAT_ID ) );
        String conversionId = cursor.getString( cursor.getColumnIndex( Chats.conversionID ) );
        String messageID = cursor.getString( cursor.getColumnIndex( Chats.MessageID ) );

        chat.setChatID( Integer.parseInt( chatId ) );
        Message message = new Message();
        message.setConversionID( conversionId );
        message.setMessageID( messageID );
        message.setBody( cursor.getString( cursor.getColumnIndex( Messages.Body ) ) );
        message.setTimeStamp( cursor.getString( cursor.getColumnIndex( Messages.TimeStamp ) ) );
        chat.message = message;

        User mUser = new User();
        mUser.setFirstname( cursor.getString( cursor.getColumnIndex( Contacts.FIRST_NAME ) ) );
        mUser.setLastname( cursor.getString( cursor.getColumnIndex( Contacts.LAST_NAME ) ) );
        mUser.setUserId( cursor.getString( cursor.getColumnIndex( Contacts.USER_ID ) ) );
        chat.user = mUser;
        cursor.close();
        return chat;

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // If you need to add a column
        if (newVersion > oldVersion) {
            sqLiteDatabase.execSQL("ALTER TABLE "+Messages.TABLE_NAME+" ADD COLUMN "+Messages.newcol+ " String");

    }
}
}