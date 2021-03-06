package com.example.smarttalk.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.example.smarttalk.R;
import com.example.smarttalk.activity.AuthenticationActivity;
import com.example.smarttalk.activity.GetStartedActivity;

import static com.example.smarttalk.constants.AppConstant.SharedPreferenceConstant.SHARED_PREF_NAME;

public class LauncherActivity extends AppCompatActivity {
    public static final String IS_ALREADY_LOGIN = "is_already_login";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.launcher_activity );
        SharedPreferences sharedPreferences=getSharedPreferences(SHARED_PREF_NAME,MODE_PRIVATE);
        boolean isLogin = sharedPreferences.getBoolean(IS_ALREADY_LOGIN, false);

        if(isLogin){
            Intent intent=new Intent( this, AuthenticationActivity.class );
            startActivity( intent );

        }
        else{
            Intent intent=new Intent( this, GetStartedActivity.class );
            startActivity( intent );

        }
    }
}
