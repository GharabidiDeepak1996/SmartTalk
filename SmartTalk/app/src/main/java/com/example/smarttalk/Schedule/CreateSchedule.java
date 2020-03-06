package com.example.smarttalk.Schedule;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;


import com.example.smarttalk.R;
import com.example.smarttalk.adapter.CustomerAdapter;
import com.example.smarttalk.databasehelper.DatabaseHelper;
import com.example.smarttalk.modelclass.User;

import java.util.Calendar;
import java.util.List;

public class CreateSchedule extends AppCompatActivity  implements
        View.OnClickListener {
    private static final String TAG = "SchedulingMessageActivi";
    List<User> listofdata;
    AutoCompleteTextView contact;
    TextView txtDate, txtTime;
    EditText messagetype;
    Button btnDatePicker, btnTimePicker;
    private int mYear, mMonth, mDay, mHour, mMinute;
    Calendar calendar;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_schedule_activity);
        btnDatePicker=(Button)findViewById(R.id.Bdate);
        btnTimePicker=(Button)findViewById(R.id.Btime);
        messagetype=findViewById(R.id.editTextMessage);
        txtDate=findViewById(R.id.textViewDate);
        txtTime=findViewById(R.id.textViewTime);
        calendar = Calendar.getInstance();
        toolbar=findViewById(R.id.toolbar);

        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        listofdata = databaseHelper.display();

        CustomerAdapter adapter = new CustomerAdapter(this, listofdata);
        contact = findViewById(R.id.autocomplete_country);
        contact.setAdapter(adapter);
        contact.setTextColor(Color.RED);
        btnDatePicker.setOnClickListener(this);
        btnTimePicker.setOnClickListener(this);
        toolbar();
    }
    public void toolbar(){
        toolbar.setTitle( "Create Schedule" );
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp); // your drawable
        setSupportActionBar(toolbar);
    }

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
                        public void onTimeSet(TimePicker view, int hourOfDay,
                                              int minute) {

                            txtTime.setText(hourOfDay + ":" + minute);
                        }
                    }, mHour, mMinute, true);
            timePickerDialog.show();
        }
    }
    public void scheduledmessage(View view) {
        String message = messagetype.getText().toString();
        String receiverid = contact.getText().toString();


        Bundle bundle = new Bundle();
        bundle.putString("receiver", receiverid);
        bundle.putString("message", message);

        Intent intent = new Intent(this, MyBroadcastReceiver.class);
        intent.putExtras(bundle);
        String actionUri = "com.scheduler.action.SMS_SEND";
        intent.setAction(actionUri);

        int id = (int) System.currentTimeMillis();
        calendar.set(mYear, mMonth, mDay, mHour, mMinute);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this,id,intent,0);
        AlarmManager alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        Toast.makeText(this, "Scheduled", Toast.LENGTH_LONG).show();
        startActivity(new Intent(this, MessageSchedule.class));
        finish();
    }



}