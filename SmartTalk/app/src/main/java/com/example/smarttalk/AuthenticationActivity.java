package com.example.smarttalk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.List;

import static com.example.smarttalk.constants.AppConstant.SharedPreferenceConstant.LOGGED_IN_USER_CONTACT_NUMBER;
import static com.example.smarttalk.constants.AppConstant.SharedPreferenceConstant.SHARED_PREF_NAME;

public class AuthenticationActivity extends AppCompatActivity {
    //https://firebase.google.com/docs/auth/android/manage-users
    private static final String TAG = "AuthenticationActivity";
    public static final String PREFERENCE_NAME = "mydata";
    String UserID;


    String number;
        private FirebaseAuth mFirebaseAuth;
    public static final int RC_SIGN_IN = 1;
    private FirebaseAuth.AuthStateListener mAuthStateListner;

    List<AuthUI.IdpConfig> providers = Arrays.asList(
            new AuthUI.IdpConfig.PhoneBuilder().build() );


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        /*  setContentView( R.layout.home_activity );*/


        mFirebaseAuth = FirebaseAuth.getInstance();
        mAuthStateListner = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                Log.d( TAG, "onAuthStateChanged: " + firebaseAuth );

                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    number = user.getPhoneNumber();
                    database();
                    Log.d( TAG, "PhoneNUMBER: " + number );
                    Toast.makeText( AuthenticationActivity.this, "User Signed In", Toast.LENGTH_SHORT ).show();
                } else {
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setIsSmartLockEnabled( false )
                                    .setAvailableProviders( providers )
                                    .build(),
                            RC_SIGN_IN

                    );
                }
            }
        };
    }

    public void database() {
        final String user = number;
        Log.d( TAG, "database: " + user );
        //Base64
        final byte[] encoded = Base64.encode(user.getBytes(), Base64.DEFAULT );
       /* FirebaseDatabase database=FirebaseDatabase.getInstance();
        DatabaseReference myRef=database.getReference("User");*/
        DatabaseReference myRef=FirebaseDatabase.getInstance().getReference("User");
        //unique ID will get.
         UserID=myRef.push().getKey();
        myRef.addListenerForSingleValueEvent( new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d( TAG, "onDataChange: " + dataSnapshot );

                if (dataSnapshot.child( new String( encoded ).trim() ).exists()) {
                    //existing user
                  //  Toast.makeText( getApplicationContext(), "Username already exists. Please try other username.", Toast.LENGTH_SHORT ).show();
                    Intent intent = new Intent( AuthenticationActivity.this, HomeActivity.class );
                    startActivity( intent );
                    //Save Contact Number into Shared Preference
                    SharedPreferences sharedPreferences=getSharedPreferences( SHARED_PREF_NAME ,MODE_PRIVATE);
                    SharedPreferences.Editor editor=sharedPreferences.edit();
                    editor.putString( LOGGED_IN_USER_CONTACT_NUMBER, user);
                    editor.apply();


                } else {
                      //new user
                    Intent intent = new Intent( AuthenticationActivity.this, RegistrationActivity.class );
                    intent.putExtra( "Base64_ID",new String( encoded ).trim() );
                    intent.putExtra( "UserID",UserID );
                    intent.putExtra( "MobileNumber",user);
                    startActivity( intent );

                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w( TAG, "Failed to read value.", error.toException() );
            }
        } );
        Log.d( TAG, "database: " + user );
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFirebaseAuth.addAuthStateListener( mAuthStateListner );
    }

    @Override
    protected void onPause() {
        super.onPause();
        mFirebaseAuth.removeAuthStateListener( mAuthStateListner );

    }
}
