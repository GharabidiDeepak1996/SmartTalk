package com.example.smarttalk.Schedule;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.smarttalk.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MessageSchedule extends AppCompatActivity {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.floating_button1)
    FloatingActionButton fab;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_scheduler);
        ButterKnife.bind( this );
        fab.setOnClickListener(view -> {
            // Click action
            Intent intent = new Intent(MessageSchedule.this, CreateSchedule.class);
            startActivity(intent);
        });
        // set a LinearLayoutManager with default orientation Is vertical
        //reference:- https://abhiandroid.com/materialdesign/recyclerview
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager); // set LayoutManager to RecyclerView
        toolbarsetup();
    }

    public void toolbarsetup() {
        toolbar.setTitle("Message Scheduler");
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp); // your drawable
        setSupportActionBar(toolbar);
    }

}
