package com.example.smarttalk.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.smarttalk.adapter.ChatAdapter;
import com.example.smarttalk.R;
import com.example.smarttalk.database.databasehelper.DatabaseHelper;
import com.example.smarttalk.database.model.Chat;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChatsFragment extends Fragment {
    private static final String TAG = "ChatsFragment";
    @BindView( R.id.chat_recycler_view ) RecyclerView mrecyclerview;
    List<Chat> mchat;

    public ChatsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate( R.layout.fragment_chats, container, false );
        ButterKnife.bind( this,view );


        LinearLayoutManager linearLayoutManager=new LinearLayoutManager( getActivity() );
        mrecyclerview.setLayoutManager(linearLayoutManager  );
        mrecyclerview.setHasFixedSize( true );

       // context=container.getContext();
        DatabaseHelper databaseHelper=new DatabaseHelper( container.getContext());
        mchat=new ArrayList<>(  );
        mchat=databaseHelper.chatList();
        Log.d( TAG, "list: "+mchat);

        ChatAdapter mchatAdapter=new ChatAdapter(getActivity(),mchat);
        mrecyclerview.setAdapter( mchatAdapter);

        return view;
    }
}
