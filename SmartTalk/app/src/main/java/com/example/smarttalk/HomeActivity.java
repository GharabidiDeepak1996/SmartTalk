package com.example.smarttalk;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.example.smarttalk.adapter.TabLayoutAdapter;
import com.example.smarttalk.databasehelper.DatabaseHelper;
import com.example.smarttalk.modelclass.User;
import com.google.android.material.tabs.TabLayout;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeActivity extends AppCompatActivity {
    //https://www.androidhive.info/2015/09/android-material-design-working-with-tabs/

    @BindView( R.id.toolbar ) Toolbar toolbar;
    @BindView( R.id.tablayout )TabLayout tabLayout;
    @BindView( R.id.viewpager  ) ViewPager viewPager;
    @BindView( R.id.search_bar ) MaterialSearchView materialSearchView;
    List<User> contactmodel;

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

      /*  //this is for searchview
        contactmodel = new ArrayList<>();
        DatabaseHelper databaseHelper = new DatabaseHelper( this );
        contactmodel = databaseHelper.display();
        Log.d( TAG, "contactmodel56: " + contactmodel.size() );

        materialSearchView.setOnQueryTextListener( new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {


                return true;
            }
        } );

        materialSearchView.setOnQueryTextListener( new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        } );

        materialSearchView.setOnSearchViewListener( new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {

            }

            @Override
            public void onSearchViewClosed() {

            }
        } );*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d( TAG, "onCreateOptionsMenu: "+menu );
        getMenuInflater().inflate( R.menu.menu_item,menu );
        MenuItem menuItem=menu.findItem( R.id.action_search );
        materialSearchView.setMenuItem( menuItem );
        return  true;
    }

}
