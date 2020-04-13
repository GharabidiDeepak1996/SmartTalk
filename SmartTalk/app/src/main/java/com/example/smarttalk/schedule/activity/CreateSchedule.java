package com.example.smarttalk.schedule.activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;


import com.example.smarttalk.R;
import com.example.smarttalk.Utils;
import com.example.smarttalk.constants.AppConstant;
import com.example.smarttalk.customViews.ContactCompletionView;
import com.example.smarttalk.databasehelper.DatabaseHelper;
import com.example.smarttalk.modelclass.ScheduleMessage;
import com.example.smarttalk.modelclass.User;
import com.example.smarttalk.schedule.services.MyBroadcastReceiver;
import com.example.smarttalk.schedule.adapter.TokenUserAdapter;
import com.tokenautocomplete.TokenCompleteTextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.example.smarttalk.constants.AppConstant.SharedPreferenceConstant.LOGGED_IN_USER_NAME;
import static com.example.smarttalk.constants.AppConstant.SharedPreferenceConstant.LOOGED_IN_USER_ID;

//https://github.com/splitwise/TokenAutoComplete --> just like gmailUI
public class CreateSchedule extends AppCompatActivity implements View.OnClickListener, TokenCompleteTextView.TokenListener<User> {
    List<User> listofdata;
    TextView txtDate, txtTime;
    EditText messagetype;
    Button btnDatePicker, btnTimePicker;
    private int mYear, mMonth, mDay, mHour, mMinute,mAM_PM;
    Toolbar toolbar;
    Calendar calendar;
    ContactCompletionView contactCompletionView;
    String receiverID,receiverName;
    private static final String TAG = "CreateSchedule";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_schedule_activity);
        btnDatePicker = (Button) findViewById(R.id.Bdate);
        btnTimePicker = (Button) findViewById(R.id.Btime);
        messagetype = findViewById(R.id.editTextMessage);
        txtDate = findViewById(R.id.textViewDate);
        txtTime = findViewById(R.id.textViewTime);
        toolbar = findViewById(R.id.toolbar);

//data fetch for user detail.
        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        listofdata = databaseHelper.display();

        TokenUserAdapter adapter = new TokenUserAdapter(this, R.layout.suggestiontoken, listofdata);
        contactCompletionView = findViewById(R.id.autocomplete_country);
        contactCompletionView.setTokenListener(this);
        contactCompletionView.setAdapter(adapter);

        calendar = Calendar.getInstance();


        btnDatePicker.setOnClickListener(this);
        btnTimePicker.setOnClickListener(this);
        toolbar();
    }

    public void toolbar() {
        toolbar.setTitle("Create Schedule");
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp); // your drawable
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    //date and time pick
    @Override
    public void onClick(View v) {
        if (v == btnDatePicker) {

            // Get Current Date
            final Calendar c = Calendar.getInstance();
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);


            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth) {

                            txtDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                            calendar.set(Calendar.YEAR, year);
                            calendar.set(Calendar.MONTH, (monthOfYear));
                            calendar.set(Calendar.DATE, dayOfMonth);
                        }
                    }, mYear, mMonth, mDay);
            datePickerDialog.show();
        }
        if (v == btnTimePicker) {

            // Get Current Time
            final Calendar c = Calendar.getInstance();
            mHour = c.get(Calendar.HOUR_OF_DAY);
            mMinute = c.get(Calendar.MINUTE);

            // Launch Time Picker Dialog
            TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                    new TimePickerDialog.OnTimeSetListener() {

                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                            calendar.set(Calendar.HOUR_OF_DAY,hourOfDay);
                            calendar.set(Calendar.MINUTE,minute);
                            //for Minute
                            SimpleDateFormat sdf = new SimpleDateFormat("mm");
                            int timeStampMinute = Integer.parseInt(sdf.format(new Date()));

                            SimpleDateFormat sdf2 = new SimpleDateFormat("h");
                            int timeStampHour = Integer.parseInt(sdf2.format(new Date()));
                            //convert militry to regular formate
                            if(hourOfDay>=13){
                                hourOfDay=hourOfDay-12;
                            }

                            if (timeStampHour!=hourOfDay || (timeStampMinute < minute && (timeStampMinute + 2) < minute)) {
                                txtTime.setText(hourOfDay + ":" + minute);

                            }else {
                                Toast.makeText(getApplicationContext(), "Please Check selected minute is greater than current minute.   ", Toast.LENGTH_LONG).show();
                            }
                        }
                    }, mHour, mMinute, false);
            timePickerDialog.show();
        }
    }

    @Override
    public void onTokenAdded(User token) {
        receiverID = token.getUserId();
        receiverName=token.getFirstname()+" "+token.getLastname();
    }

    @Override
    public void onTokenRemoved(User token) {

    }

    @Override
    public void onTokenIgnored(User token) {

    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void scheduledmessage(View view) {
        long inMili=calendar.getTimeInMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("a");
        String time = sdf.format(new Date(inMili));
        String messageBody = messagetype.getText().toString();

        String timeStamp=txtTime.getText().toString()+" "+time;

        SharedPreferences preferences = this.getSharedPreferences(AppConstant.SharedPreferenceConstant.SHARED_PREF_NAME, MODE_PRIVATE);
        //senderID ,senderName & senderImage
        String SenderID = preferences.getString(LOOGED_IN_USER_ID, "");
        String senderName=preferences.getString(LOGGED_IN_USER_NAME,"");
        String url = preferences.getString(AppConstant.ImageURI.ProfileImageUri, null);
        String senderMobileNumber=preferences.getString(AppConstant.SharedPreferenceConstant.LOGGED_IN_USER_CONTACT_NUMBER,null);



        //messageID
        String messageID = Utils.generateUniqueMessageId();

        Bundle bundle = new Bundle();
        bundle.putString("senderID",SenderID);
        bundle.putString("receiverID", receiverID);
        bundle.putString("messageBody", messageBody);
        bundle.putString("senderName",senderName);
        bundle.putString("messageID",messageID);
        bundle.putString("timeStamp",timeStamp);
        bundle.putString("senderimage",url);
        bundle.putString("sendermobilenumber",senderMobileNumber);

        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        ScheduleMessage message=new ScheduleMessage();
        message.setSenderID(SenderID);
        message.setReceiverID(receiverID);
        message.setMessageBody(messageBody);
        message.setMessageID(messageID);
        message.setTimeStamp(timeStamp);
        message.setSenderName(senderName);
        message.setReceiverName(receiverName);
        databaseHelper.schedulingMessage(message);

        Intent intent = new Intent(this, MyBroadcastReceiver.class);
        intent.putExtras(bundle);
        String actionUri = "com.scheduler.action.SMS_SEND";
        intent.setAction(actionUri);



        Log.d(TAG, "scheduledmessage: "+inMili +"----------->"+time);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);


        Toast.makeText(this, "Scheduled", Toast.LENGTH_LONG).show();
        finish();
    }
}