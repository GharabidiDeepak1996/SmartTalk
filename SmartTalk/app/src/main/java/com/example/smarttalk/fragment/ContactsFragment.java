package com.example.smarttalk.fragment;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.smarttalk.adapter.ContactAdapter;
import com.example.smarttalk.databasehelper.DatabaseHelper;
import com.example.smarttalk.modelclass.User;
import com.example.smarttalk.R;
import com.example.smarttalk.constants.AppConstant;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.content.Context.MODE_PRIVATE;


public class ContactsFragment extends Fragment {
    // https://www.simplifiedcoding.net/firebase-storage-example/

    @BindView(R.id.recyclerview) RecyclerView recyclerView;

    private ContactAdapter mfetchAdapter;
    private String mLoggedInUserContactNumber;
    private Context mContext;
    private SharedPreferences mPreference;
    private String FirstName, LastName, MobileNumber, UserID;
    DatabaseHelper databaseHelper;
    private List<User> contactmodel;

    private static final String TAG = "ContactsFragment";
    //private static final String TAG = "MyFirebaseMessagingServ";

    public ContactsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate( R.layout.fragment_contacts, container, false );
        ButterKnife.bind( this,view );

        //Notification ChannelID
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel( "mynotification", "mynotification", NotificationManager.IMPORTANCE_DEFAULT );
            NotificationManager notificationManager = getActivity().getSystemService( NotificationManager.class );
            notificationManager.createNotificationChannel( channel );
        }

        recyclerView.setHasFixedSize( true ); //setHasFixedSize to true when changing the contents of the adapter does not change it's height or the width.
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager( getActivity() );
        recyclerView.setLayoutManager( linearLayoutManager );
//line divider bewt the cardview
        DividerItemDecoration itemDecor = new DividerItemDecoration( getActivity(), linearLayoutManager.getOrientation() );
        recyclerView.addItemDecoration( itemDecor );
        Log.d( TAG, "Context: "+getActivity() );
//sharedpreferences
        mContext = getActivity();
        mPreference = mContext.getSharedPreferences( AppConstant.SharedPreferenceConstant.SHARED_PREF_NAME, MODE_PRIVATE );
        mLoggedInUserContactNumber = mPreference.getString( AppConstant.SharedPreferenceConstant.LOGGED_IN_USER_CONTACT_NUMBER, null );

        Log.d( TAG, "onCreateView: " + mLoggedInUserContactNumber );

        databaseHelper = new DatabaseHelper( getActivity() );
        contactmodel = new ArrayList<>();
        contactmodel = databaseHelper.display();
        Log.d( TAG, "contactmodel: "+contactmodel );
        mfetchAdapter = new ContactAdapter( getActivity(), contactmodel );
        recyclerView.setAdapter( mfetchAdapter );

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference( "User" );

        databaseReference.addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d( TAG, "onDataChange 1: " + dataSnapshot );
                //iterating through all the values in database
                List<User> contactList = new ArrayList<>(  );

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    //all the user id will access here in userID
                    String userId = postSnapshot.getKey();

                    User user = postSnapshot.getValue( User.class );
                    if (user != null ) {
                        user.setUserId( userId );
                    }
                    if (user.getUserId().contains( "==" )) {
                        user.setUserId( user.getUserId().replace( "==", "" ) );
                    }

                    if (mLoggedInUserContactNumber != null && !mLoggedInUserContactNumber.equalsIgnoreCase( user.getMobilenumber() )) {

                        Log.d( TAG, "UserID55: "+user.getUserId());
                        UserID = user.getUserId();
                        FirstName = user.getFirstname();
                        LastName = user.getLastname();
                        MobileNumber = user.getMobilenumber();
                        Log.d( TAG, "onDataChange: 0+"+MobileNumber );
                        //Offline data will save in databas
                        User contact = new User();
                        contact.setUserId( UserID );
                        contact.setFirstname( FirstName );
                        contact.setLastname( LastName );
                        contact.setMobilenumber( MobileNumber );
                        Log.d( TAG, "UserID: "+UserID );
                        databaseHelper.insert( contact );
                        contactList.add( contact );
                    } else {

                        checkForCurrentLoggedInUser( user );
                    }
                }
                //fetch first time
                mfetchAdapter.setContactList( contactList );
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w( TAG, "Failed to read value.", error.toException() );
            }
        } );
        return view;
    }

    private void checkForCurrentLoggedInUser(final User user) {
       /* if (user.getUserId().contains( "==" )) {
            user.setUserId( user.getUserId().replace( "==", "" ) );
            //senderID send to messageActivity
        }*/
        Log.d( TAG, "checkForCurrentLoggedInUser: user Contanct Number : " + user.getUserId());

        //save the data login user in sharedpreference TO MESSAGE ACTIVITY
        SharedPreferences.Editor edit = mPreference.edit();
        edit.putString( AppConstant.SharedPreferenceConstant.LOGGED_IN_USER_NAME, user.getFirstname() + " " + user.getLastname() );
        edit.putString( AppConstant.SharedPreferenceConstant.LOOGED_IN_USER_ID, user.getUserId() );
        Log.d( TAG, "userID: " + user.getUserId() );
        edit.apply();

//subscribe FCM sender topic should be subscribe because of receiver the message from receiver side.
        FirebaseMessaging.getInstance().subscribeToTopic( user.getUserId() )
                .addOnCompleteListener( new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = getString( R.string.msg_subscribed );
                        if (!task.isSuccessful()) {
                            msg = getString( R.string.msg_subscribe_failed );
                        }
                        Log.d( TAG, "onComplete: " + task.isSuccessful() );
                        Log.d( TAG, "onComplete: User ID : " + user.getUserId() );
                        //Toast.makeText(ContactsFragment.this, msg, Toast.LENGTH_SHORT).show();
                    }
                } );
    }


}
