package com.example.smarttalk.database.DatabaseHelper;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.smarttalk.database.model.Contact;
import com.example.smarttalk.database.model.Message;

import static com.example.smarttalk.MessageActivity.THIS_BROADCAST;

public class ContactDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "Contact.db";
    private static final int DATABASE_VERSION =3;

    private static final String TAG = "ContactDatabaseHelper";

    public ContactDatabaseHelper(@Nullable Context context) {
        super( context, DATABASE_NAME, null, DATABASE_VERSION );
    }



    public class Contacts {
        //https://www.androidhive.info/2011/11/android-sqlite-database-tutorial/
         private static final String TABLE_NAME = "CONTACT";
        private  static final String FIRST_NAME= "FIRSTNAME";
        private  static final String LAST_NAME = "LASTNAME";
        private  static final String  MOBILE_NUMBER= "MOBILENUMBER";
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String query = "CREATE TABLE " + Contacts.TABLE_NAME + "( "
                + Contacts.FIRST_NAME + " TEXT,"
                + Contacts.LAST_NAME + " TEXT, "
                + Contacts.MOBILE_NUMBER + " TEXT "
                +")";
        sqLiteDatabase.execSQL( query );
    }

    public void insert(Contact contact) {
        SQLiteDatabase db = this.getWritableDatabase();    //write into the table
        ContentValues contentValues = new ContentValues();  //insert the values in rows

        contentValues.put( Contacts.FIRST_NAME,contact.getFirstName());
        contentValues.put( Contacts.LAST_NAME,contact.getLastName());
        contentValues.put( Contacts.MOBILE_NUMBER,contact.getMobileNmuber());

        long row = db.insert( Contacts.TABLE_NAME, null, contentValues ); //row count number of rows inserted
        Log.d( TAG, "insert row: " +row);
    }
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
