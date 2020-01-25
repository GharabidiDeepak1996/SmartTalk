package com.example.smarttalk.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.smarttalk.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class SmsSchedulerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms_scheduler);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.floating_button1);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Click action
                Intent intent = new Intent(SmsSchedulerActivity.this, SchedulingMessageActivity.class);
                startActivity(intent);
            }
        });
    }
}
