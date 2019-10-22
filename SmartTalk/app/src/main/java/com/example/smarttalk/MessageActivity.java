package com.example.smarttalk;

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
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smarttalk.Adapter.MessageAdapter;
import com.example.smarttalk.ModelClass.User;
import com.example.smarttalk.Retrofit.BaseApplication;
import com.example.smarttalk.Retrofit.Data;
import com.example.smarttalk.Retrofit.FCMAPI;
import com.example.smarttalk.Retrofit.MessageEntity;
import com.example.smarttalk.constants.AppConstant.SharedPreferenceConstant;
import com.example.smarttalk.database.DatabaseHelper.DatabaseHelper;
import com.example.smarttalk.database.model.Message;

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
    Toolbar mtoolbar;
    ImageButton btn_send;
    EditText text_send;
ScrollView scrollView;
    Context mcontext;

    String ReceiverUserID;
    String SenderID;
    String Mobileno;
    String Name;
    MessageAdapter messageAdapter;
  //  private ArrayList<Data> mchat;

    String MessageID;
    TextView textView;
    ImageView imageView;
    RecyclerView recyclerView;
    String timeStamp;
    public static final String THIS_BROADCAST = "this is my broadcast";


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_message );

        MessageID =Utils.generateRandomString( 16 );

//Broadcast Receiver
        IntentFilter intentFilter = new IntentFilter( THIS_BROADCAST );
        registerReceiver( broadcastReceiver, intentFilter );

        btn_send = findViewById( R.id.sender );
        text_send = findViewById( R.id.Emessage );
        textView = findViewById( R.id.text );
        imageView = findViewById( R.id.Image );
scrollView=findViewById( R.id.scrollView );

        //toolbar
        mtoolbar = findViewById( R.id.toolbar );
        //messageRecyclerview
        recyclerView = findViewById( R.id.recycler_view );
        recyclerView.setHasFixedSize( true );

        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager( getApplicationContext() );
        linearLayoutManager.setStackFromEnd( true );
        linearLayoutManager.setReverseLayout( false );
        recyclerView.setLayoutManager( linearLayoutManager );

        //receiving from myrecycleradapter.
        Intent intent = getIntent();
        ReceiverUserID = intent.getStringExtra( "ReceiverUserID" );
        Mobileno = intent.getStringExtra( "number" );
        Name = intent.getStringExtra( "name" );
        textView.setText( Name );

      setupToolbar();

        if (ReceiverUserID.contains( "==" )) {
            ReceiverUserID = ReceiverUserID.replace( "==", "" );
            Log.d( TAG, "onCreate: ReplaceUserID " + ReceiverUserID );
        }

        //MessageAdapter
        messageAdapter = new MessageAdapter( MessageActivity.this, new ArrayList<Message>(  ) );
        recyclerView.setAdapter( messageAdapter );


    }

    private void setupToolbar() {
        //https://medium.com/android-grid/how-to-implement-back-up-button-on-toolbar-android-studio-c272bbc0f1b0
        setSupportActionBar( mtoolbar );
        getSupportActionBar().setDisplayHomeAsUpEnabled( true );
        getSupportActionBar().setHomeAsUpIndicator( R.drawable.ic_arrow_back_black_24dp);
    }

//onClick Listnar
    public void sendMessage(View view) {
        //scrollView
        recyclerView.smoothScrollToPosition( text_send.getBottom() );
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
            sendMessage( SenderID, ReceiverUserID, msg ); //receiverside
            senderMessage(SenderID,ReceiverUserID,MessageID,msg,timeStamp); //sender side

        } else {
            Toast.makeText( MessageActivity.this, "You can't send empty message", Toast.LENGTH_SHORT ).show();
        }
        //"" this indicate the black
        text_send.setText( "" );
    }

    //This for sender side.
    private void sendMessage(String SenderId, final String ReceiverId, String messageBody) {
        Retrofit retrofit = BaseApplication.getRetrofitInstance();
        FCMAPI api = retrofit.create( FCMAPI.class );

        Data data = new Data();
        data.SenderID = SenderId;
        data.ReceiverID = ReceiverId;
        data.Body = messageBody;
        data.MessageID = MessageID;
        data.TimeStamp = timeStamp;

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

        //Insert message into table
    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d( TAG, "onReceive: " + context );
            Bundle bundle = intent.getExtras();
            String MessageID = bundle.getString( "MessageID" );
            Log.d( TAG, "MessageID: " + MessageID );

            //Get message data from database using messageId
            DatabaseHelper handler = new DatabaseHelper(context);
            Message message = handler.getMessageById(MessageID);

            messageAdapter.addMessageToAdapter(  message);
        }
    };

    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver( broadcastReceiver );
    }
   public void senderMessage(String SenderID,String ReceiverUserID,String MessageID,String msg,String timeStamp){
        Message message=new Message();
        message.setSenderID( SenderID );
        message.setConversionID( ReceiverUserID );
        message.setMessageID(  MessageID);
        message.setBody(  msg);
        message.setTimeStamp( timeStamp );

        DatabaseHelper databaseHelper=new DatabaseHelper( this );
        databaseHelper.insert( message );

   }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {


        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }

        return super.onOptionsItemSelected( item );
    }
}
