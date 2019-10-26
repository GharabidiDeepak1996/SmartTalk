package com.example.smarttalk.Fragment;

import android.content.Context;
import android.content.SharedPreferences;
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

import com.example.smarttalk.ModelClass.User;
import com.example.smarttalk.Adapter.MyRecyclerAdapter;
import com.example.smarttalk.R;
import com.example.smarttalk.constants.AppConstant;
import com.example.smarttalk.database.DatabaseHelper.DatabaseHelper;
import com.example.smarttalk.database.model.Contact;
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

import static android.content.Context.MODE_PRIVATE;


public class ContactsFragment extends Fragment {
    // https://www.simplifiedcoding.net/firebase-storage-example/

    private RecyclerView recyclerView;
    // private ArrayList<User> mUserList;
    private MyRecyclerAdapter mfetchAdapter;
    private String mLoggedInUserContactNumber;
    private Context mContext;
    private SharedPreferences mPreference;
    private String FirstName, LastName, MobileNumber, UserID;
    DatabaseHelper databaseHelper;
    private List<Contact> contactmodel;

    private static final String TAG = "ContactsFragment";
    //private static final String TAG = "MyFirebaseMessagingServ";

    public ContactsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate( R.layout.fragment_contacts, container, false );
        recyclerView = view.findViewById( R.id.recyclerview );

        recyclerView.setHasFixedSize( true ); //setHasFixedSize to true when changing the contents of the adapter does not change it's height or the width.
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager( getActivity() );
        recyclerView.setLayoutManager( linearLayoutManager );

        DividerItemDecoration itemDecor = new DividerItemDecoration( getActivity(), linearLayoutManager.getOrientation() );
        recyclerView.addItemDecoration( itemDecor );
//sharedpreferences
        mContext = getActivity();
        mPreference = mContext.getSharedPreferences( AppConstant.SharedPreferenceConstant.SHARED_PREF_NAME, MODE_PRIVATE );
        mLoggedInUserContactNumber = mPreference.getString( AppConstant.SharedPreferenceConstant.LOGGED_IN_USER_CONTACT_NUMBER, null );

        Log.d( TAG, "onCreateView: " + mLoggedInUserContactNumber );

        databaseHelper = new DatabaseHelper( getActivity() );
        contactmodel = new ArrayList<>();
        contactmodel = databaseHelper.display();

        mfetchAdapter = new MyRecyclerAdapter( getActivity(), contactmodel );
        recyclerView.setAdapter( mfetchAdapter );

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference( "User" );

        databaseReference.addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d( TAG, "onDataChange 1: " + dataSnapshot );
                //iterating through all the values in database

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    //all the user id will access here in userID
                    String userId = postSnapshot.getKey();
                    User user = postSnapshot.getValue( User.class );

                    if (user != null) {
                        user.setUserId( userId );
                    }
                    //Log.d( TAG, "onDataChange: " + user );

                    if (mLoggedInUserContactNumber != null && !mLoggedInUserContactNumber.equalsIgnoreCase( user.getMobilenumber() )) {
                        UserID = user.getUserId();
                        FirstName = user.getFirstname();
                        LastName = user.getLastname();
                        MobileNumber = user.getMobilenumber();
                        Log.d( TAG, "onDataChange: 0+"+MobileNumber );
                        //Offline data will save in databas
                        Contact contact = new Contact();
                        contact.setUserID( UserID );
                        contact.setFirstName( FirstName );
                        contact.setLastName( LastName );
                        contact.setMobileNmuber( MobileNumber );

                        databaseHelper.insert( contact );
                    } else {

                        checkForCurrentLoggedInUser( user );
                    }
                }
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
        if (user.getUserId().contains( "==" )) {
            user.setUserId( user.getUserId().replace( "==", "" ) );
            //senderID send to messageActivity

        }
        Log.d( TAG, "checkForCurrentLoggedInUser: user Contanct Number : " + user.getMobilenumber() );

        //save the data login user in sharedpreference
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
