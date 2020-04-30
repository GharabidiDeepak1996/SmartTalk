package com.example.smarttalk.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;

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
    String UserID,number;
    FirebaseDatabase database;
    private FirebaseAuth mFirebaseAuth;
    public static final int RC_SIGN_IN = 1;
    private FirebaseAuth.AuthStateListener mAuthStateListner;

    List<AuthUI.IdpConfig> providers = Arrays.asList(
            new AuthUI.IdpConfig.PhoneBuilder().build() );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );

        mFirebaseAuth = FirebaseAuth.getInstance();
        mAuthStateListner = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    number = user.getPhoneNumber();
                    database();
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
       database=FirebaseDatabase.getInstance();
        database.setPersistenceEnabled( true );
    }

    public void database() {
        final String user = number;
        final byte[] encoded = Base64.encode(user.getBytes(), Base64.DEFAULT );
         FirebaseDatabase.getInstance();
        DatabaseReference myRef=database.getReference("User");
         UserID=myRef.push().getKey();
        myRef.addListenerForSingleValueEvent( new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child( new String( encoded ).trim() ).exists()) {
                    //existing user
                    Intent intent = new Intent( AuthenticationActivity.this, HomeActivity.class );
                    startActivity( intent );
                    //Save Contact Number into Shared Preference
                    //https://www.journaldev.com/9412/android-shared-preferences-example-tutorial
                    //https://stackoverflow.com/questions/23024831/android-shared-preferences-example
                    //https://github.com/orhanobut/hawk
                    SharedPreferences sharedPreferences=getSharedPreferences( SHARED_PREF_NAME ,MODE_PRIVATE);
                    SharedPreferences.Editor editor=sharedPreferences.edit();
                    editor.putString( LOGGED_IN_USER_CONTACT_NUMBER, user);
                    editor.apply();


                } else {
                      //new user
                    Intent intent = new Intent( AuthenticationActivity.this, RegistrationActivity.class );
                    intent.putExtra( "Base64_ID",new String( encoded ).trim() );
                  //  intent.putExtra( "UserID",UserID );
                    intent.putExtra( "MobileNumber",user);
                    startActivity( intent );
                }
            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
            }
        } );
        finish();
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
