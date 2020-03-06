package com.example.smarttalk.activity;

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
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.smarttalk.R;
import com.example.smarttalk.Utils;
import com.example.smarttalk.adapter.MessageAdapter;
import com.example.smarttalk.constants.AppConstant;
import com.example.smarttalk.databasehelper.DatabaseHelper;
import com.example.smarttalk.retrofit.BaseApplication;
import com.example.smarttalk.retrofit.Data;
import com.example.smarttalk.retrofit.FCMAPI;
import com.example.smarttalk.retrofit.MessageEntity;
import com.example.smarttalk.constants.AppConstant.SharedPreferenceConstant;
import com.example.smarttalk.modelclass.Message;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static com.example.smarttalk.constants.AppConstant.SharedPreferenceConstant.*;

public class MessageActivity extends AppCompatActivity {
    private static final String TAG = "MessageActivity";


    @BindView(R.id.toolbar)
    Toolbar mtoolbar;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.Emessage)
    EditText text_send;
    @BindView(R.id.text)
    TextView textView;
    @BindView(R.id.Image)
    ImageView imageView;

    String ReceiverUserID, SenderID, Mobileno, Name, MessageID, timeStamp,profileImage;
    MessageAdapter messageAdapter;
    Context mcontext;
    List<Message> message1;
    SharedPreferences preferences;

    DatabaseHelper databaseHelper;
    public static final String THIS_BROADCAST = "this is my broadcast";
    public static final String UPDATE_MESSAGE_STATUS_BRODCAST = "update message status broadcast";
    public static final String MESSAGEID_STATUS_UPDATE = "messageID status update";
public static  final String Messa="messaage";
    //AutoUpdate Internet Status.
    private NetworkChangeReceiver receiver;
    private boolean isConnected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        ButterKnife.bind(this);

        //conversion id Broadcast Receiver
        IntentFilter intentFilter = new IntentFilter(THIS_BROADCAST);
        registerReceiver(broadcastReceiver, intentFilter);
        //message status update
        IntentFilter intentFilter1 = new IntentFilter(UPDATE_MESSAGE_STATUS_BRODCAST);
        registerReceiver(UpdateBroadcastReceiver, intentFilter1);
        //messageid status update
        IntentFilter intentFilter2 = new IntentFilter(MESSAGEID_STATUS_UPDATE);
        receiver = new NetworkChangeReceiver();
        registerReceiver(receiver, intentFilter2);

        //INTERNET AUTO DETETCTED
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        receiver = new NetworkChangeReceiver();
        registerReceiver(receiver, filter);

        //messageRecyclerview
        recyclerView.setHasFixedSize(true);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(false);
        recyclerView.setLayoutManager(linearLayoutManager);

        //receiving from myrecycleradapter.
        Intent intent = getIntent();
        ReceiverUserID = intent.getStringExtra("ReceiverUserID");
        Log.d(TAG, "ReceiverUserID: "+ReceiverUserID);
        Mobileno = intent.getStringExtra("number");
        Name = intent.getStringExtra("name");
        profileImage=intent.getStringExtra("imageView");

        textView.setText(Name);

        Glide.with(this)
                .load(profileImage)
                .placeholder(R.mipmap.avatar)
                .into(imageView);

        setupToolbar();
        try {
            if (ReceiverUserID.contains("==")) {
                ReceiverUserID = ReceiverUserID.replace("==", "");
            }
        } catch (Exception e) {
            Log.d(TAG, "onCreate: " + e);
        }
         preferences = getSharedPreferences(SharedPreferenceConstant.SHARED_PREF_NAME, MODE_PRIVATE);
        SenderID = preferences.getString(LOOGED_IN_USER_ID, "");

        try {
            databaseHelper = new DatabaseHelper(this);
            message1 = new ArrayList<>();
            message1 = databaseHelper.getConversionID(ReceiverUserID);
            messageAdapter = new MessageAdapter(MessageActivity.this, message1);
            recyclerView.setAdapter(messageAdapter);
        }catch (Exception ignored){
            Log.d(TAG, "onCreate: "+ignored);
        }
    }

    private void setupToolbar() {
        //https://medium.com/android-grid/how-to-implement-back-up-button-on-toolbar-android-studio-c272bbc0f1b0
        setSupportActionBar(mtoolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp);
    }

    public void sendMessage(View view) {
        MessageID = Utils.generateUniqueMessageId();
        //scrollView
        recyclerView.smoothScrollToPosition(text_send.getBottom());
        //Timestamp.
        SimpleDateFormat sdf = new SimpleDateFormat("h:mm a");
        timeStamp = sdf.format(new Date());

        String msg = text_send.getText().toString();
        if (!msg.equals("")) {
            //retrieve data from contactfragment
            //https://www.journaldev.com/9412/android-shared-preferences-example-tutorial
            // or hawk

            sendMessage(SenderID, ReceiverUserID, msg); //send to retrofit method
            senderMessage(SenderID, ReceiverUserID, MessageID, msg, timeStamp, MESSAGE_PENDING); //send to database
            Log.d(TAG, "recent_message_id_onSend: "+MessageID);

        } else {
            Toast.makeText(MessageActivity.this, "You can't send empty message", Toast.LENGTH_SHORT).show();
        }
        //"" this indicate the black
        text_send.setText("");
        Log.d(TAG, "size: "+message1.size());
    }


    //This for sender side.
    private void sendMessage(String SenderId, final String ReceiverId, String messageBody) {
        Retrofit retrofit = BaseApplication.getRetrofitInstance();
        FCMAPI api = retrofit.create(FCMAPI.class);
//modelcLass
        Data data = new Data();
        data.SenderID = SenderId;
        data.ReceiverID = ReceiverId;
        data.Body = messageBody;
        data.MessageID = MessageID;
        data.TimeStamp = timeStamp;
        data.Name = Name;

        MessageEntity messageEntity = new MessageEntity();
        messageEntity.data = data;
        messageEntity.to = "/topics/" + ReceiverUserID;
        api.sendMessage(messageEntity).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                Log.d(TAG, "onResponse: " + response.code());
                if (response.code() == 200) {
                    Log.d(TAG, "sendMessage_receiver: "+ReceiverUserID+" to "+ReceiverId);

                    //Log.d( TAG, "onResponse: MessageEntity send successfully" );
                   // Log.d(TAG, "onResponse: ");
                    databaseHelper.updateMessagestatus(MESSAGE_SUCCESSFULL_SENDED, MessageID);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                databaseHelper.Pendingmessagesupdate();

            }
        });
    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            mcontext = context;
            Bundle bundle = intent.getExtras();
            assert bundle != null;
            String MessageID = bundle.getString("MessageID");

            //Get message data from database using messageId
            DatabaseHelper handler = new DatabaseHelper(context);
            Log.d(TAG, "context158: " + context);
            Message message = handler.getMessageById(MessageID);
            messageAdapter.addMessageToAdapter(message);
        }
    };
    private BroadcastReceiver UpdateBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if ((bundle != null)) {

                String messageId = bundle.getString("Messageid");
                DatabaseHelper updatedatabasehander = new DatabaseHelper(context);
                Message message = updatedatabasehander.getMessageById(messageId);
                messageAdapter.updateMessageToAdapter(message);


            }
        }
    };

    public void senderMessage(String SenderID, String ReceiverUserID, String MessageID, String msg, String timeStamp, String Pending) {
        Message message = new Message();
        message.setSenderID(SenderID);
        message.setConversionID(ReceiverUserID);
        message.setMessageID(MessageID);
        message.setBody(msg);
        message.setTimeStamp(timeStamp);
        message.setDeliveryStatus(Pending);
        databaseHelper.insert(message);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {


        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }


    //AutoDetect Internet
    public class NetworkChangeReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(final Context context, final Intent intent) {
            String MessageIDStatus = null;
            Bundle bundle = intent.getExtras();
            if ((bundle != null)) {
                MessageIDStatus = bundle.getString("Messagestatus");

            }
           isNetworkAvailable(context, MessageIDStatus);
        }

        public void isNetworkAvailable(Context context, String messageID) {

            ConnectivityManager connectivity = (ConnectivityManager)
                    context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivity != null) {
                NetworkInfo[] info = connectivity.getAllNetworkInfo();

                if (info != null) {
                    for (int i = 0; i < info.length; i++) {
                        if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                            if (!isConnected) {

                                if(preferences.contains(PENDING_MESSAGE_SENDTO_DATABASE)){
                                    String result = preferences.getString(AppConstant.SharedPreferenceConstant.PENDING_MESSAGE_SENDTO_DATABASE, null);
                                    Gson gson = new Gson();
                                    Message[] favoriteItems = gson.fromJson(result, Message[].class);
                                    message1 = Arrays.asList(favoriteItems);
                                    Log.d(TAG, "onCreate+Messageid: "+message1);
                                    for(Message data:message1) {
                                        Log.d(TAG, "isNetworkAvailableb: "+data.getMessageID());
                                        databaseHelper.updateMessagestatus(MESSAGE_SUCCESSFULL_SENDED,data.getMessageID() );
                                    }
                                }
                               databaseHelper.updateMessagestatus(MESSAGE_SUCCESSFULL_SENDED, messageID);

                            }
                            return;
                        }
                    }
                }
            }

            isConnected = false;
        }
    }
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
        unregisterReceiver(UpdateBroadcastReceiver);
        unregisterReceiver(receiver);
    }

}
