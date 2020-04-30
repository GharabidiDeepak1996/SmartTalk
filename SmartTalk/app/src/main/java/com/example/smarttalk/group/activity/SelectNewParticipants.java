package com.example.smarttalk.group.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.smarttalk.R;
import com.example.smarttalk.constants.AppConstant;
import com.example.smarttalk.constants.NetworkConstants;
import com.example.smarttalk.databasehelper.DatabaseHelper;
import com.example.smarttalk.group.adapter.selectNewParticipantAdapter;
import com.example.smarttalk.group.adapter.selectedUserListAdapter;
import com.example.smarttalk.modelclass.User;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class SelectNewParticipants extends AppCompatActivity {
    @BindView(R.id.toolbar) Toolbar mtoolbar;
    @BindView(R.id.list_of_contact_data) RecyclerView recyclerView_userList;
    @BindView(R.id.addedList_recyclerView) RecyclerView recyclerView_selectedUser;

    List<User> contactmodel;
  public List<User> selectedUserList =new  ArrayList<User>();
    selectedUserListAdapter adapter1;
    private static final String TAG = "SelectNewParticipants";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_new_participants);
        ButterKnife.bind(this);
        setupToolbar();
       //userList
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        recyclerView_userList.setLayoutManager(linearLayoutManager);
        //selectedUser
        LinearLayoutManager linearLayoutManager1=new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        recyclerView_selectedUser.setLayoutManager(linearLayoutManager1);

        DividerItemDecoration itemDecor = new DividerItemDecoration( this, linearLayoutManager.getOrientation() );
        recyclerView_userList.addItemDecoration( itemDecor );

        DatabaseHelper databaseHelper=new DatabaseHelper(this);
        contactmodel = new ArrayList<>();
        contactmodel= databaseHelper.display();
        SharedPreferences mPreference = this.getSharedPreferences( AppConstant.SharedPreferenceConstant.SHARED_PREF_NAME, MODE_PRIVATE );
        String mLoggedInUserContactNumber = mPreference.getString( AppConstant.SharedPreferenceConstant.LOGGED_IN_USER_CONTACT_NUMBER, null );
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("User");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    User user = postSnapshot.getValue( User.class );
                    if (mLoggedInUserContactNumber != null && mLoggedInUserContactNumber.equals( user.getMobilenumber() )) {
                        User contact = new User();
                        contact.setUserId(user.getUserId());
                        contact.setFirstname(user.getFirstname());
                        contact.setLastname(user.getLastname());
                        contact.setMobilenumber(user.getMobilenumber());
                        contact.setRegistrationTokenID(user.getRegistrationTokenID());
                        selectedUserList.add(contact);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        }) ;
         //userList Adapter
        selectNewParticipantAdapter adapter=new selectNewParticipantAdapter(contactmodel,this, new selectNewParticipantAdapter.NotifyMeInterface() {
            @Override
            public void handleData(User user, int requestCode) {
                switch(requestCode){
                    case  NetworkConstants.USER_REMOVED :
                        selectedUserList.remove(user);
                        adapter1.notifyDataSetChanged();
                        break;
                    case  NetworkConstants.USER_ADDED :
                        selectedUserList.add(user);
                        adapter1.notifyDataSetChanged();
                        break;
                    default:
                }

                if(selectedUserList.size()>1){
                  recyclerView_selectedUser.setVisibility(View.VISIBLE);
                }else {
                    recyclerView_selectedUser.setVisibility(View.GONE);
                }
            }
        });
        recyclerView_userList.setAdapter(adapter);
        //selectedUser

         adapter1=new selectedUserListAdapter(selectedUserList, this, new selectedUserListAdapter.NotifyMeInterface() {
            @Override
            public void handleData(User user, int requestCode) {
                switch (requestCode){
                    case NetworkConstants.USER_REMOVED :
                      adapter.restView(user);
                        break;
                    default:

                }
            }
        });
        recyclerView_selectedUser.setAdapter(adapter1);

    }

    private void setupToolbar() {
            //https://medium.com/android-grid/how-to-implement-back-up-button-on-toolbar-android-studio-c272bbc0f1b0
            setSupportActionBar(mtoolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp);
            mtoolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }



    public void next(View view) {
        if (selectedUserList.size() >= 2 && selectedUserList.size()<= 6) {

            Intent intent = new Intent(this,GroupDetailsActivity.class);
            Bundle args = new Bundle();
            args.putSerializable("ARRAYLIST",(Serializable)selectedUserList);
            intent.putExtra("BUNDLE",args);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Number of members should be more than 2 and less than 7", Toast.LENGTH_LONG).show();
        }
    }
}
