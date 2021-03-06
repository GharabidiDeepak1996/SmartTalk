package com.example.smarttalk.activity;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.smarttalk.R;
import com.example.smarttalk.Utils;
import com.example.smarttalk.adapter.MessageAdapter;
import com.example.smarttalk.constants.AppConstant;
import com.example.smarttalk.databasehelper.DatabaseHelper;
import com.example.smarttalk.modelclass.User;
import com.example.smarttalk.retrofit.BaseApplication;
import com.example.smarttalk.retrofit.Data;
import com.example.smarttalk.retrofit.FCMAPI;
import com.example.smarttalk.retrofit.MessageEntity;
import com.example.smarttalk.constants.AppConstant.SharedPreferenceConstant;
import com.example.smarttalk.modelclass.Message;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static com.example.smarttalk.constants.AppConstant.SharedPreferenceConstant.*;
import static com.example.smarttalk.constants.NetworkConstants.MESSAGEID_STATUS_UPDATE;
import static com.example.smarttalk.constants.NetworkConstants.PICK_IMAGE;
import static com.example.smarttalk.constants.NetworkConstants.STATUS_CHECKER;
import static com.example.smarttalk.constants.NetworkConstants.THIS_BROADCAST;
import static com.example.smarttalk.constants.NetworkConstants.UPDATE_MESSAGE_STATUS_BRODCAST;

public class MessageActivity extends AppCompatActivity {
    private static final String TAG = "MessageActivity";
    @BindView(R.id.toolbar) Toolbar mtoolbar;
    @BindView(R.id.recycler_view) RecyclerView recyclerView;
    @BindView(R.id.Emessage) EditText text_send;
    @BindView(R.id.profile_name) TextView profileName;
    @BindView(R.id.status) TextView textViewStatus;
    @BindView(R.id.Image) CircleImageView imageView;
    @BindView(R.id.floating_button) FloatingActionButton floatingActionButton;
    @BindView(R.id.attach_images) ImageButton attchImages;
    String firstFourChars = "";
    public String ReceiverUserID, SenderID, Mobileno, Name, MessageID, timeStamp, profileImage, status, typing, number;
    MessageAdapter messageAdapter;
    Context mcontext;
    List<Message> message1;
    SharedPreferences preferences;
    DatabaseHelper databaseHelper;

    //AutoUpdate Internet Status.
    private NetworkChangeReceiver receiver;
    private boolean isConnected = false;
    public boolean isTyping = false;
    private Timer timer = new Timer();
    private final long DELAY = 3000; // milliseconds
    ProgressDialog progressDialog;

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

        //STATUS CHECKER
        IntentFilter intentFilterstatusChecker = new IntentFilter(STATUS_CHECKER);
        registerReceiver(statusChecker, intentFilterstatusChecker);

        //messageRecyclerview
        recyclerView.setHasFixedSize(true);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(false);
        recyclerView.setLayoutManager(linearLayoutManager);
        //receiving from myrecycleradapter.
        Intent intent = getIntent();
        ReceiverUserID = intent.getStringExtra("ReceiverUserID");
        Mobileno = intent.getStringExtra("number");
        Name = intent.getStringExtra("name");
        profileImage = intent.getStringExtra("imageView");
        profileName.setText(Name);
        Log.d(TAG, "onCreate58: "+ReceiverUserID);

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

        }
        preferences = getSharedPreferences(SharedPreferenceConstant.SHARED_PREF_NAME, MODE_PRIVATE);
        SenderID = preferences.getString(LOOGED_IN_USER_ID, "");

        try {

            databaseHelper = new DatabaseHelper(this);
            message1 = new ArrayList<>();
            message1 = databaseHelper.getConversionID(ReceiverUserID);
            messageAdapter = new MessageAdapter(MessageActivity.this, message1);
            recyclerView.setAdapter(messageAdapter);

             //smooth scrolling from bottom when new message insert
            messageAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                @Override
                public void onItemRangeInserted(int positionStart, int itemCount) {
                    super.onItemRangeInserted(positionStart, itemCount);
                    linearLayoutManager.smoothScrollToPosition(recyclerView, null, messageAdapter.getItemCount() - 1);

                }
            });
        } catch (Exception ignored) {

        }
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dx == 0 && dy == 0) {
                    floatingActionButton.hide();
                } else if (dy < 0) {
                    floatingActionButton.show();
                } else if (dy > 0) {
                    floatingActionButton.hide();
                }

                floatingActionButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //  recyclerView.smoothScrollToPosition(0); //scroll to top
                        linearLayoutManager.scrollToPosition(message1.size() - 1); //sroll to down
                    }
                });

            }
        });
        text_send.addTextChangedListener(watcher);
        floatingActionButton.hide();
attchImages();
    }

    TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (s.toString().trim().length() == 0) {
                checkTypingStatus("noOne");
            } else {
                String number = preferences.getString(LOGGED_IN_USER_CONTACT_NUMBER, null);
                checkTypingStatus(number + " " + s);
            }

            if (!isTyping) {
                // Send notification for start typing event
                isTyping = true;
            }
            timer.cancel();
            timer = new Timer();
            timer.schedule(new TimerTask() {
                               @Override
                               public void run() {
                                   isTyping = false;
                                   // Send notification for stopped typing event
                               }
                           }, DELAY
            );
        }
    };


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

        } else {
            Toast.makeText(MessageActivity.this, "You can't send empty message", Toast.LENGTH_SHORT).show();
        }
        //"" this indicate the black
        text_send.setText("");
    }


    //This for sender side.
    private void sendMessage(String SenderId, final String ReceiverId, String messageBody) {
        String name = preferences.getString(LOGGED_IN_USER_NAME, null);
        String url = preferences.getString(AppConstant.ImageURI.ProfileImageUri, null);
        String number=preferences.getString(LOGGED_IN_USER_CONTACT_NUMBER,null);

        Retrofit retrofit = BaseApplication.getRetrofitInstance();
        FCMAPI api = retrofit.create(FCMAPI.class);

       //modelcLass
        Data data = new Data();
        data.SenderID = SenderId;
        data.ReceiverID = ReceiverId;
        data.Body = messageBody;
        data.MessageID = MessageID;
        data.TimeStamp = timeStamp;
        data.SenderName = name;
        data.SenderImage=url;
        data.SenderMobileNumber=number;

        MessageEntity messageEntity = new MessageEntity();
        messageEntity.data = data;
        messageEntity.to = "/topics/" + ReceiverUserID ;
        api.sendMessage(messageEntity).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.code() == 200) {
                    Log.d(TAG, "onResponse: "+"on");
                    databaseHelper.updateMessagestatus(MESSAGE_SUCCESSFULL_SENDED, MessageID);
                }
            }
            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Log.d(TAG, "onFailure: "+"off");
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
//sender Side
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

                                if (preferences.contains(PENDING_MESSAGE_SENDTO_DATABASE)) {
                                    String result = preferences.getString(AppConstant.SharedPreferenceConstant.PENDING_MESSAGE_SENDTO_DATABASE, null);
                                    Gson gson = new Gson();
                                    Message[] favoriteItems = gson.fromJson(result, Message[].class);
                                    message1 = Arrays.asList(favoriteItems);
                                    for (Message data : message1) {
                                        databaseHelper.updateMessagestatus(MESSAGE_SUCCESSFULL_SENDED, data.getMessageID());
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

    //contactFragment
    BroadcastReceiver statusChecker = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "onReceiveStatusChecker: ");
            number = intent.getStringExtra("number");
            typing = intent.getStringExtra("isTyping");
            if (number.equals(Mobileno)) {
                status = intent.getStringExtra("statuscheck");
                typing = intent.getStringExtra("isTyping");

                if (typing.length() > 13) {
                    firstFourChars = typing.substring(0, 13);
                } else {
                    firstFourChars = typing;
                }

                if (firstFourChars.equals(number)) {
                    textViewStatus.setText("typing...");
                } else {
                    textViewStatus.setText(status);
                }
            }

        }
    };

    public void checkTypingStatus(String typing) {
        String base64id = preferences.getString(LOOGED_IN_USER_ID, null);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        assert base64id != null;
        DatabaseReference myRef = database.getReference("User").child(base64id.concat("=="));

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("isTyping", typing);
        myRef.updateChildren(hashMap);
    }

    public void status(String status) {
        String base64id = preferences.getString(LOOGED_IN_USER_ID, null);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        assert base64id != null;
        DatabaseReference myRef = database.getReference("User").child(base64id.concat("=="));

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", status);
        myRef.updateChildren(hashMap);
    }

    @Override
    protected void onResume() {
        super.onResume();
        status("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
       //unregisterReceiver(receiver);

        SimpleDateFormat sdf = new SimpleDateFormat("h:mm a  MMMM dd, yyyy");
        String timeStamp1 = sdf.format(new Date());
        status("Last Seen :" + timeStamp1);
        checkTypingStatus("noOne");
    }


    //click buttonto import image
    public void attchImages(){
        attchImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gallery = new Intent(Intent.ACTION_PICK);
                gallery.setType("image/*"); // we set type of images
                startActivityForResult(gallery, PICK_IMAGE );
            }
        });
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE  && resultCode == RESULT_OK && data != null && data.getData() != null) {
           Uri uri=data.getData();
           String  uniqueID = Utils.generateUniqueMessageId();
            uploadImageUrl(uri,uniqueID);
            //progress bar
            progressDialog=new ProgressDialog(this);
            progressDialog.setMessage("Sending Image....");
            progressDialog.setTitle("Please Wait for 2 Sec");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.show();

            Log.d(TAG, "onSuccess45: 3");

        }
    }
    private void uploadImageUrl(Uri imageUri,String uniqID) {
        Log.d(TAG, "uploadFile546: "+imageUri+"2.--->"+uniqID);
        if (imageUri != null) {
            StorageReference storage = FirebaseStorage.getInstance().getReference().child("conversion_images").child(uniqID);
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),imageUri);

            } catch (IOException e) {
                e.printStackTrace();
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data1 = baos.toByteArray();
            //uploadLoading image Url in firebase.
            storage.putBytes(data1).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    //getImage Url from firebase

                    storage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            MessageID = Utils.generateUniqueMessageId();
                            //Timestamp.
                            SimpleDateFormat sdf = new SimpleDateFormat("h:mm a");
                            timeStamp = sdf.format(new Date());
                            sendMessage(SenderID, ReceiverUserID, uri.toString()); //send to retrofit method
                            senderMessage(SenderID, ReceiverUserID, MessageID, uri.toString(), timeStamp, MESSAGE_PENDING); //send to database

                            progressDialog.dismiss();                        }
                    });
                  }
            });

        }
    }

    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
        unregisterReceiver(UpdateBroadcastReceiver);
        unregisterReceiver(receiver);
        unregisterReceiver(statusChecker);

    }

}
