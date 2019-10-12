package com.example.smarttalk;

import androidx.appcompat.app.AppCompatActivity;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.smarttalk.Adapter.MessageAdapter;
import com.example.smarttalk.ModelClass.User;
import com.example.smarttalk.Retrofit.BaseApplication;
import com.example.smarttalk.Retrofit.Data;
import com.example.smarttalk.Retrofit.FCMAPI;
import com.example.smarttalk.Retrofit.MessageEntity;
import com.example.smarttalk.constants.AppConstant.SharedPreferenceConstant;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static com.example.smarttalk.constants.AppConstant.SharedPreferenceConstant.*;

public class MessageActivity extends AppCompatActivity {
    private static final String TAG = "MessageActivity";
    Toolbar toolbar;
    ImageButton btn_send;
    EditText text_send;

    Context mcontext;

    String ReceiverUserID;
    String SenderID;
    String Mobileno;
    String Name;


    private ArrayList<Data> mchat;


    TextView textView;
    ImageView imageView;
    RecyclerView recyclerView;
    String timeStamp;
    public static final String THIS_BROADCAST = "this is my broadcast";


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_message );

        btn_send = findViewById( R.id.sender );
        text_send = findViewById( R.id.Emessage );
        textView = findViewById( R.id.text );
        toolbar = findViewById( R.id.toolbar );
        imageView = findViewById( R.id.Image );
        recyclerView = findViewById( R.id.recycler_view );
        recyclerView.setHasFixedSize( true );
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager( getApplicationContext() );
        linearLayoutManager.setStackFromEnd( true );
        recyclerView.setLayoutManager( linearLayoutManager );
        //receiving from myrecycleradapter.
        Intent intent = getIntent();
        ReceiverUserID = intent.getStringExtra( "ReceiverUserID" );
        Mobileno = intent.getStringExtra( "number" );
        Name = intent.getStringExtra( "name" );
        textView.setText( Name );
        if (ReceiverUserID.contains( "==" )) {
            ReceiverUserID = ReceiverUserID.replace( "==", "" );
            Log.d( TAG, "onCreate: ReplaceUserID " + ReceiverUserID );
        }

        IntentFilter intentFilter = new IntentFilter(THIS_BROADCAST);
        registerReceiver(broadcastReceiver, intentFilter);
    }

    public void BsendMessage(View view) {
        //Timestamp.
        SimpleDateFormat sdf = new SimpleDateFormat( "h:mm a" );
        timeStamp = sdf.format( new Date() );

        String msg = text_send.getText().toString();
        if (!msg.equals( "" )) {
            //retrieve data from contactfragment
            //https://www.journaldev.com/9412/android-shared-preferences-example-tutorial
            // or hawk
            SharedPreferences preferences = getSharedPreferences( SharedPreferenceConstant.SHARED_PREF_NAME, MODE_PRIVATE );
            SenderID = preferences.getString( LOOGED_IN_USER_ID, "" );
            sendMessage( SenderID, ReceiverUserID, msg );
        } else {
            Toast.makeText( MessageActivity.this, "You can't send empty message", Toast.LENGTH_SHORT ).show();
        }
        text_send.setText( "" );
    }

    //This for sender side.
    private void sendMessage(String SenderId, final String ReceiverId, String messageBody) {
        Retrofit retrofit = BaseApplication.getRetrofitInstance();
        FCMAPI api = retrofit.create( FCMAPI.class );
        mchat = new ArrayList<>();
        Data data = new Data();
        data.SenderID = SenderId;
        data.ReceiverID = ReceiverId;
        data.Body = messageBody;
        data.MessageID = Utils.generateRandomString( 16 );
        data.TimeStamp = timeStamp;
        mchat.add( data );


        //MessageAdapter
        MessageAdapter messageAdapter = new MessageAdapter( MessageActivity.this, mchat);
        recyclerView.setAdapter( messageAdapter );

        MessageEntity messageEntity = new MessageEntity();
        messageEntity.data = data;
        messageEntity.to = "/topics/" + ReceiverUserID;

        api.sendMessage( messageEntity ).enqueue( new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d( TAG, "onResponse: " + response.code() );
                if (response.code() == 200) {
                    Log.d( TAG, "onResponse: MessageEntity send successfully" );
                } else {

                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d( TAG, "onFailure: " + t.getMessage() );
            }
        } );
    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String MessageID = intent.getStringExtra( "MessageID" );
            Log.d( TAG, "MessageActivity messageID: "+MessageID);

        }
    };

    public void onDestroy() {
        unregisterReceiver( broadcastReceiver );
        super.onDestroy();

    }
}
