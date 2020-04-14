package com.example.smarttalk.group.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.smarttalk.R;

public class SelectNewParticipants extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_new_participants);

       /* private void setupToolbar() {
            //https://medium.com/android-grid/how-to-implement-back-up-button-on-toolbar-android-studio-c272bbc0f1b0
            setSupportActionBar(mtoolbar);
            Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp);
        }*/
    }
}
