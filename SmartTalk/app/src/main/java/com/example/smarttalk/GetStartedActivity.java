package com.example.smarttalk;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import static com.example.smarttalk.constants.AppConstant.SharedPreferenceConstant.SHARED_PREF_NAME;



public class GetStartedActivity extends AppCompatActivity {
Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_get_started );

        button=findViewById( R.id.getstarted );
        button.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SharedPreferences sharedPreferences =getSharedPreferences(SHARED_PREF_NAME,MODE_PRIVATE);
                SharedPreferences.Editor editor=sharedPreferences.edit();                                        //save the data
                editor.putBoolean(LauncherActivity.IS_ALREADY_LOGIN, true);
                editor.apply();

                Intent intent=new Intent( GetStartedActivity.this, AuthenticationActivity.class );
                startActivity( intent );
                finish();
            }
        } );

    }

}
