package com.example.smarttalk.group.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.smarttalk.R;
import com.example.smarttalk.Utils;
import com.example.smarttalk.constants.AppConstant;
import com.example.smarttalk.databasehelper.DatabaseHelper;
import com.example.smarttalk.group.adapter.GroupMessageActivityAdapter;
import com.example.smarttalk.group.pojo.GroupMessages;
import com.example.smarttalk.group.retrofit.FCMAPI;
import com.example.smarttalk.group.retrofit.GroupData;
import com.example.smarttalk.group.retrofit.GroupEntity;
import com.example.smarttalk.modelclass.User;
import com.example.smarttalk.retrofit.BaseApplication;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static com.example.smarttalk.constants.AppConstant.SharedPreferenceConstant.LOGGED_IN_USER_NAME;
import static com.example.smarttalk.constants.AppConstant.SharedPreferenceConstant.LOOGED_IN_USER_ID;
import static com.example.smarttalk.constants.NetworkConstants.NOTIFY_ADAPTER_LIST;

public class GroupMessageActivity extends AppCompatActivity {
    private static final String TAG = "GroupMessageActivity";
    //https://mkyong.com/java/java-how-to-search-a-string-in-a-list/

    @BindView(R.id.group_message_toolbar)
    Toolbar mtoolbar;
    @BindView(R.id.group_recyclerview)
    RecyclerView mrecyclerView;
    @BindView(R.id.inputType)
    EditText minputTextView;
    @BindView(R.id.Image)
    CircleImageView groupImageView;
    @BindView(R.id.profile_name)
    TextView tgroupName;
    @BindView(R.id.membersName)
    TextView tMembers;
    List<String> registrationID, members;
    String groupID, groupName, groupImage, MessageID, timeStamp, senderID, senderName;
    List<GroupMessages> groupMessagesList;
    GroupMessageActivityAdapter activityAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_message);
        ButterKnife.bind(this);
        toolBar();

        SharedPreferences sharedPreferences = getSharedPreferences(AppConstant.SharedPreferenceConstant.SHARED_PREF_NAME, MODE_PRIVATE);
        senderID = sharedPreferences.getString(LOOGED_IN_USER_ID, "");
        senderName = sharedPreferences.getString(LOGGED_IN_USER_NAME, "");
        IntentFilter intentFilter = new IntentFilter(NOTIFY_ADAPTER_LIST);
        registerReceiver(registerReceiver, intentFilter);

        Intent intent = getIntent();
        registrationID = (List<String>) intent.getSerializableExtra("registrationID");
        members = (List<String>) intent.getSerializableExtra("members");
        groupName = intent.getStringExtra("groupName");
        groupImage = intent.getStringExtra("groupImage");
        groupID = intent.getStringExtra("groupID");
        groupMessagesList = new ArrayList<>();
        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        groupMessagesList = databaseHelper.fetchGroupMessages(groupID);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mrecyclerView.setLayoutManager(linearLayoutManager);

        activityAdapter = new GroupMessageActivityAdapter(this, groupMessagesList);
        mrecyclerView.setAdapter(activityAdapter);

        //smooth scrolling from bottom when new message insert
        activityAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                linearLayoutManager.smoothScrollToPosition(mrecyclerView, null, activityAdapter.getItemCount() - 1);

            }
        });

        Glide.with(this)
                .load(groupImage)
                .placeholder(R.mipmap.avatar)
                .into(groupImageView);
        tgroupName.setText(groupName);

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 1; i++) {
            builder.append(members);
        }
        tMembers.setText(builder);


    }

    public void toolBar() {
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp);
        mtoolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }

    public void sendMessageBton(View view) {
        //messageID automatically generated when button click
        MessageID = Utils.generateUniqueMessageId();
        //TimeStamp generate when button click
        SimpleDateFormat sdf = new SimpleDateFormat("h:mm a");
        timeStamp = sdf.format(new Date());
        String msg = minputTextView.getText().toString();

        mrecyclerView.smoothScrollToPosition(minputTextView.getBottom());

        if (!msg.equals("")) {
            //retrofit
            sendMessage(msg, MessageID, timeStamp);
            //to insert Into Database
            sendToDatabase(groupID, senderName, senderID, MessageID, msg, timeStamp);
        } else {
            Toast.makeText(GroupMessageActivity.this, "You can't send empty message", Toast.LENGTH_SHORT).show();
        }
        minputTextView.setText("");
    }

    public void sendMessage(String messageBody, String messageID, String timeStamp) {
        Retrofit retrofit = BaseApplication.getRetrofitInstance();
        FCMAPI api = retrofit.create(FCMAPI.class);

        GroupData groupData = new GroupData();
        groupData.body = messageBody;
        groupData.messageID = messageID;
        groupData.timeStamp = timeStamp;
        groupData.groupName = groupName;
        groupData.groupImage = groupImage;
        groupData.groupID = groupID;
        groupData.senderID = senderID;
        groupData.senderName = senderName;

        Log.d(TAG, "sendMessage: " + senderName);

        GroupEntity groupEntity = new GroupEntity();
        groupEntity.data = groupData;
        groupEntity.registration_ids = registrationID;

        api.sendMessage(groupEntity).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code() == 200) {
                    Log.d(TAG, "onResponse2356: " + "success");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(TAG, "onResponse2356: " + "fail");
            }
        });
    }

    public void sendToDatabase(String groupid, String senderName, String senderid, String messageID, String messageBody, String timeStamp) {
        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        GroupMessages groupMessages = new GroupMessages();
        groupMessages.setGroupID(groupid);
        groupMessages.setSenderID(senderid);
        groupMessages.setSenderName(senderName);
        groupMessages.setMessageID(messageID);
        groupMessages.setMessageBody(messageBody);
        groupMessages.setTimeStamp(timeStamp);
        databaseHelper.groupMessageInsert(groupMessages);

    }

    BroadcastReceiver registerReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            DatabaseHelper databaseHelper = new DatabaseHelper(context);
            groupMessagesList = databaseHelper.fetchGroupMessages(groupID);
            activityAdapter.notifyToList(groupMessagesList);
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(registerReceiver);
    }
}
