package com.example.smarttalk;

import android.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.smarttalk.appupdate.ForceUpdateChecker;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.example.smarttalk.constants.AppConstant.SharedPreferenceConstant.SHARED_PREF_NAME;



public class GetStartedActivity extends AppCompatActivity implements ForceUpdateChecker.OnUpdateNeededListener{

@BindView( R.id.getstarted ) Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_get_started );
        ButterKnife.bind(this);//important to add this under super.onCreate and setContentView

        ForceUpdateChecker.with( this )
                .onUpdateNeeded( this )
                .check();


        button.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPreferences =getSharedPreferences(SHARED_PREF_NAME,MODE_PRIVATE);
                SharedPreferences.Editor editor=sharedPreferences.edit();                                        //save the data
                editor.putBoolean(LauncherActivity.IS_ALREADY_LOGIN, true);
                editor.apply();

                Intent intent=new Intent( GetStartedActivity.this, AuthenticationActivity.class );
                startActivity( intent );
            }
        } );
    }

    @Override
    public void onUpdateNeeded(String updateUrl) {
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle("New version available")
                .setMessage("Please, update app to new version to continue reposting.")
                .setPositiveButton("Update", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText( GetStartedActivity.this,""+updateUrl,Toast.LENGTH_LONG ).show();

                                                            }
                        }).setNegativeButton("No, thanks", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).create();
        alertDialog.show();
    }

}
