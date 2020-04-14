package com.example.smarttalk.activity;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.example.smarttalk.R;
import com.example.smarttalk.adapter.TabLayoutAdapter;
import com.example.smarttalk.constants.AppConstant;
import com.example.smarttalk.databasehelper.DatabaseHelper;
import com.example.smarttalk.fragment.ProfileFragment;
import com.example.smarttalk.modelclass.User;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.smarttalk.constants.AppConstant.SharedPreferenceConstant.LOOGED_IN_USER_ID;
import static com.example.smarttalk.fragment.ProfileFragment.THIS_BROADCAST_FOR_PROFILE_IMAGE;

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
    @BindView(R.id.profile_image)
    CircleImageView profile_image;
    @BindView(R.id.title)
    TextView title;
    MenuItem menuItem;

    private NetworkConnection receiver;
    private boolean isConnected = false;
    SharedPreferences sharedPreferences;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);
        ButterKnife.bind(this);

        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        receiver = new NetworkConnection();
        registerReceiver(receiver, filter);

        IntentFilter intentFilter1 = new IntentFilter(THIS_BROADCAST_FOR_PROFILE_IMAGE);
        registerReceiver(broadcastReceiverForprofileImage, intentFilter1);

        //setting the title
        title.setText("SmartTalk");
        //placing toolbar in place of actionbar
        setSupportActionBar(toolbar);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.addOnPageChangeListener(this);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        sharedPreferences = this.getSharedPreferences(AppConstant.SharedPreferenceConstant.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        String url = sharedPreferences.getString(AppConstant.ImageURI.ProfileImageUri, null);
        Glide.with(this)
                .load(url)
                .placeholder(R.mipmap.avatar)
                .into(profile_image);

        List<String> list = new ArrayList<>();
        list.add("Chats");
        list.add("Group");
        list.add("Contacts");//
        list.add("Profile");
        TabLayoutAdapter tabLayoutAdapter = new TabLayoutAdapter(getSupportFragmentManager(), list);
        viewPager.setAdapter(tabLayoutAdapter);


        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        //this is for searchview
/*materialSearchView.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        tabLayout.setVisibility(View.GONE);
    }
});*/
        materialSearchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                databaseHelper.Contactsearch(newText);
                databaseHelper.Chatsearch(newText);
                return false;
            }
        });

    }

    BroadcastReceiver broadcastReceiverForprofileImage = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String url = intent.getStringExtra("profileImage");
            Glide.with(context)
                    .load(url)
                    .placeholder(R.mipmap.avatar)
                    .into(profile_image);
        }
    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_item, menu);
        menuItem = menu.findItem(R.id.action_search);
        materialSearchView.setMenuItem(menuItem);
        return true;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        if (position == 3) {
            // menuItem.setVisible(false);
            getSupportActionBar().hide();
        } else {
            //  menuItem.setVisible(true);
            getSupportActionBar().show();
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
            ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivity != null) {
                NetworkInfo[] info = connectivity.getAllNetworkInfo();
                if (info != null) {
                    for (int i = 0; i < info.length; i++) {
                        if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                            if (!isConnected) {

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
        unregisterReceiver(broadcastReceiverForprofileImage);
        unregisterReceiver(receiver);
    }

    public void status(String status) {
        String base64id = sharedPreferences.getString(LOOGED_IN_USER_ID, null);
        if (base64id == null) {
        } else {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference("User").child(base64id.concat("=="));
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("status", status);
            myRef.updateChildren(hashMap);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        status("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        SimpleDateFormat sdf = new SimpleDateFormat("h:mm a MMMM dd, yyyy");
        String timeStamp1 = sdf.format(new Date());
        status("Last Seen :" + timeStamp1);
    }
}

