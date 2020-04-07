package com.example.smarttalk.schedule.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import com.example.smarttalk.R;
import com.example.smarttalk.databasehelper.DatabaseHelper;
import com.example.smarttalk.modelclass.ScheduleMessage;
import com.example.smarttalk.schedule.adapter.MessageScheduleAdapter;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MessageSchedule extends AppCompatActivity {
    private static final String TAG = "MessageSchedule";
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    MessageScheduleAdapter mAdapter;
    List<ScheduleMessage> mList;
    DatabaseHelper databaseHelper;
    public static final String THIS_BROADCAST_FOR_NOTIFY_THE_ADAPTER="notify the adapter";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_scheduler);
        ButterKnife.bind( this );

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        toolbarsetup();
        enableSwipeToDeleteAndUndo();

        databaseHelper = new DatabaseHelper(this);
        mList=databaseHelper.listOfScheduleMessage();

        mAdapter=new MessageScheduleAdapter(this,mList);
        recyclerView.setAdapter(mAdapter);

        IntentFilter intentFilter=new IntentFilter(THIS_BROADCAST_FOR_NOTIFY_THE_ADAPTER);
        registerReceiver(notifyAdapter,intentFilter);
    }

    public void toolbarsetup() {
        toolbar.setTitle("Message Scheduler");
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp); // your drawable
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    private void enableSwipeToDeleteAndUndo() {
        SwipeToDeleteCallback swipeToDeleteCallback = new SwipeToDeleteCallback(this) {

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                final int position = viewHolder.getAdapterPosition();
                final ScheduleMessage item = mAdapter.getData().get(position); //receive all data for restore
                final String messages = mAdapter.getData().get(position).MessageID;

                //remove
                mAdapter.removeItem(position);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        databaseHelper.deleteScheduledMessage((messages));
                        Log.d(TAG, "run56: "+(position));
                        //Do something after 3000ms ---> 3sec
                    }
                }, 3000);

                View parentLayout = findViewById(android.R.id.content);
                Snackbar snackbar = Snackbar.make(parentLayout, "Item was removed from the list.", Snackbar.LENGTH_LONG);
                snackbar.setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                      //restore
                        mAdapter.restoreItem(item, position);
                        recyclerView.scrollToPosition(position);
                    }
                });

                snackbar.setActionTextColor(Color.YELLOW);
                snackbar.show();
            }
        };
        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchhelper.attachToRecyclerView(recyclerView);
    }
    BroadcastReceiver notifyAdapter=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            DatabaseHelper databaseHelper = new DatabaseHelper(context);
           List<ScheduleMessage> mList=databaseHelper.listOfScheduleMessage();
             mAdapter.updateList(mList);

        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(notifyAdapter);
    }
}
