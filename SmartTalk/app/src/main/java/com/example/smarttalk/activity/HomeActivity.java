package com.example.smarttalk.activity;

import android.animation.Animator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.example.smarttalk.R;
import com.example.smarttalk.adapter.TabLayoutAdapter;
import com.example.smarttalk.databasehelper.DatabaseHelper;
import com.example.smarttalk.fragment.ProfileFragment;
import com.example.smarttalk.modelclass.User;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener {
    //https://www.androidhive.info/2015/09/android-material-design-working-with-tabs/

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tablayout)
    TabLayout tabLayout;
    @BindView(R.id.viewpager)
    ViewPager viewPager;
    @BindView(R.id.search_bar)
    MaterialSearchView materialSearchView;
     AppBarLayout appBar;


    private static final String TAG = "HomeActivity";
    private NetworkConnection receiver;
    private boolean isConnected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.home_activity );
        ButterKnife.bind( this );

        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        receiver = new NetworkConnection();
        registerReceiver(receiver, filter);
        //setting the title
        toolbar.setTitle( "SmartTalk" );
        //placing toolbar in place of actionbar
        setSupportActionBar( toolbar );
        tabLayout.setupWithViewPager( viewPager );

        List<String> list = new ArrayList<>();
        list.add( "Chats" );
        list.add( "Contacts" );
        list.add("Profile");
        TabLayoutAdapter tabLayoutAdapter = new TabLayoutAdapter( getSupportFragmentManager(), list );
        viewPager.setAdapter( tabLayoutAdapter );

//for search bar and tool bar combin
        appBar = findViewById(R.id.appBar);
        DatabaseHelper databaseHelper = new DatabaseHelper( this );
        //this is for searchview

        materialSearchView.setOnQueryTextListener( new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

               databaseHelper.Contactsearch( newText );
               databaseHelper.Chatsearch(newText);
                Log.d( TAG, "onQueryTextChange: "+newText );

                return false;
            }
        } );

        viewPager.addOnPageChangeListener( this );

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d( TAG, "onCreateOptionsMenu: " + menu );
        getMenuInflater().inflate( R.menu.menu_item, menu );
        MenuItem menuItem = menu.findItem( R.id.action_search );
        materialSearchView.setMenuItem( menuItem );
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button


        AnimatorSet set = new AnimatorSet();
        set.playTogether(
                ObjectAnimator.ofFloat(appBar, "translationY", -tabLayout.getHeight()),
                ObjectAnimator.ofFloat(viewPager, "translationY", -tabLayout.getHeight()),
                ObjectAnimator.ofFloat(appBar, "alpha", 0)
        );
        set.setDuration(100).addListener(new com.nineoldandroids.animation.Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(com.nineoldandroids.animation.Animator animation) {

            }

            @Override
            public void onAnimationEnd(com.nineoldandroids.animation.Animator animation) {
                appBar.setVisibility( View.GONE);
            }

            @Override
            public void onAnimationCancel(com.nineoldandroids.animation.Animator animation) {

            }

            @Override
            public void onAnimationRepeat(com.nineoldandroids.animation.Animator animation) {

            }
        });
        set.start();
        return super.onOptionsItemSelected(item);

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        if (materialSearchView.isSearchOpen()) {
            materialSearchView.closeSearch();
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
    public class NetworkConnection extends BroadcastReceiver {

        @Override
        public void onReceive(final Context context, final Intent intent) {


            isNetworkAvailable(context);

        }


        private boolean isNetworkAvailable(Context context) {
            ConnectivityManager connectivity = (ConnectivityManager)
                    context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivity != null) {
                NetworkInfo[] info = connectivity.getAllNetworkInfo();
                if (info != null) {
                    for (int i = 0; i < info.length; i++) {
                        if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                            if(!isConnected){

                                View parentLayout = findViewById(android.R.id.content);
                                Snackbar snackbar = Snackbar.make(parentLayout, "Internet Connection is Active", Snackbar.LENGTH_SHORT);
                                snackbar.getView().setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                                snackbar.show();

                                isConnected = true;

                            }
                            return true;
                        }
                    }
                }
            }
            View parentLayout = findViewById(android.R.id.content);
            Snackbar snackbar = Snackbar.make(parentLayout, "Internet Connection is Deactive", Snackbar.LENGTH_SHORT);
            snackbar.getView().setBackgroundColor(getResources().getColor(android.R.color.holo_red_light));
            snackbar.show();
            isConnected = false;
            return false;
        }
    }
    @Override
    protected void onDestroy() {

        super.onDestroy();

        unregisterReceiver(receiver);

    }
}

