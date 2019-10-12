package com.example.smarttalk;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.example.smarttalk.Adapter.MyTabLayoutAdapter;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {
    //https://www.androidhive.info/2015/09/android-material-design-working-with-tabs/
    Toolbar toolbar;
    TabLayout tabLayout;
    ViewPager viewPager;

    private static final String TAG = "HomeActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.home_activity );


        toolbar = findViewById( R.id.toolbar );
        //setting the title
        toolbar.setTitle( "SmartTalk" );
        //placing toolbar in place of actionbar
        setSupportActionBar( toolbar );

        tabLayout = findViewById( R.id.tablayout );
        viewPager = findViewById( R.id.viewpager );
        tabLayout.setupWithViewPager( viewPager );

        List<String> list = new ArrayList<>();
        list.add( "Chats" );
        list.add( "Contacts" );

        MyTabLayoutAdapter myTabLayoutAdapter = new MyTabLayoutAdapter( getSupportFragmentManager(), list );
        viewPager.setAdapter( myTabLayoutAdapter );


    }
}
