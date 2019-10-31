package com.example.smarttalk;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.example.smarttalk.adapter.TabLayoutAdapter;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeActivity extends AppCompatActivity {
    //https://www.androidhive.info/2015/09/android-material-design-working-with-tabs/

    @BindView( R.id.toolbar ) Toolbar toolbar;
    @BindView( R.id.tablayout )TabLayout tabLayout;
    @BindView( R.id.viewpager  ) ViewPager viewPager;
    private static final String TAG = "HomeActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.home_activity );
        ButterKnife.bind( this );

        //setting the title
        toolbar.setTitle( "SmartTalk" );
        //placing toolbar in place of actionbar
        setSupportActionBar( toolbar );
        tabLayout.setupWithViewPager( viewPager );

        List<String> list = new ArrayList<>();
        list.add( "Chats" );
        list.add( "Contacts" );

        TabLayoutAdapter tabLayoutAdapter = new TabLayoutAdapter( getSupportFragmentManager(), list );
        viewPager.setAdapter( tabLayoutAdapter );
    }
}
