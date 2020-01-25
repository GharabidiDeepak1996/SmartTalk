package com.example.smarttalk.activity;


import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.smarttalk.R;
import com.example.smarttalk.modelclass.User;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import butterknife.BindViews;
import butterknife.ButterKnife;

import static com.example.smarttalk.constants.AppConstant.SharedPreferenceConstant.LOGGED_IN_USER_CONTACT_NUMBER;
import static com.example.smarttalk.constants.AppConstant.SharedPreferenceConstant.SHARED_PREF_NAME;


public class RegistrationActivity extends AppCompatActivity {
    private static final String TAG = "RegistrationActivity";

    User modelclass;
    String mobilenumber;
    DatabaseReference myRef;
    FirebaseDatabase database;
    @BindViews({R.id.FirstName,R.id.LastName} ) List<EditText> ListEditText;

String UserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_registration );
        ButterKnife.bind( this );

        mobilenumber=getIntent().getStringExtra( "MobileNumber" );
        UserID=getIntent().getStringExtra( "UserID" );
        modelclass = new User();
    }

    private void getValues() {
        modelclass.setUserId( UserID );
        String firstname=ListEditText.get( 0 ).getText().toString();
        // Create firstname a char array of given String
        char cfname[] = firstname.toCharArray();
        for (int i = 0; i < firstname.length(); i++) {
            // If first character of a word is found
            if (i == 0 && cfname[i] != ' ' ||
                    cfname[i] != ' ' && cfname[i - 1] == ' ') {

                // If it is in lower-case
                if (cfname[i] >= 'a' && cfname[i] <= 'z') {

                    // Convert into Upper-case
                    cfname[i] = (char)(cfname[i] - 'a' + 'A');
                }
            }
            // If apart from first character
            // Any one is in Upper-case
            else if (cfname[i] >= 'A' && cfname[i] <= 'Z')

                // Convert into Lower-Case
                cfname[i] = (char)(cfname[i] + 'a' - 'A');
        }
        // Convert the char array to equivalent String
        String Firstname = new String(cfname);
        modelclass.setFirstname( Firstname );

        String lastname=ListEditText.get( 1 ).getText().toString();
        // Create Lastname a char array of given String
        char clname[] = lastname.toCharArray();
        for (int i = 0; i < lastname.length(); i++) {
            // If first character of a word is found
            if (i == 0 && clname[i] != ' ' ||
                    clname[i] != ' ' && clname[i - 1] == ' ') {

                // If it is in lower-case
                if (clname[i] >= 'a' && clname[i] <= 'z') {

                    // Convert into Upper-case
                    clname[i] = (char)(clname[i] - 'a' + 'A');
                }
            }
            // If apart from first character
            // Any one is in Upper-case
            else if (clname[i] >= 'A' && clname[i] <= 'Z')

                // Convert into Lower-Case
                clname[i] = (char)(clname[i] + 'a' - 'A');
        }
        // Convert the char array to equivalent String
        String Lastname = new String(clname);
        modelclass.setLastname( Lastname);
        modelclass.setMobilenumber( mobilenumber );

    }


    public void submit(View view) {
        getValues();
        //  DatabaseReference myRef=FirebaseDatabase.getInstance().getReference(); this onw way
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference( "User" );  //table name is message*/

        //base64 code will be child
        String Base64_ID = getIntent().getStringExtra( "Base64_ID" );
        myRef.child( Base64_ID ).setValue( modelclass );
        Toast.makeText( RegistrationActivity.this, "Data is inserted Succesfull", Toast.LENGTH_LONG ).show();
        //Save Contact Number into Shared Preference
        //https://stackoverflow.com/questions/3624280/how-to-use-sharedpreferences-in-android-to-store-fetch-and-edit-values
        SharedPreferences sharedPreferences = getSharedPreferences( SHARED_PREF_NAME, MODE_PRIVATE );
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString( LOGGED_IN_USER_CONTACT_NUMBER, mobilenumber );
        editor.apply();

        Intent intent = new Intent( RegistrationActivity.this, HomeActivity.class );
        startActivity( intent );
        finish();
    }
        }
