package com.example.smarttalk.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smarttalk.R;
import com.example.smarttalk.adapter.ChatAdapter;
import com.example.smarttalk.databasehelper.DatabaseHelper;
import com.example.smarttalk.modelclass.Chat;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.smarttalk.MessageActivity.THIS_BROADCAST;


public class ChatsFragment extends Fragment {
    private static final String TAG = "ChatsFragment";
    @BindView(R.id.chat_recycler_view)
    RecyclerView mrecyclerview;
    List<Chat> mchat;
    Boolean isExists;
    int indexToremove;
    DatabaseHelper databaseHelper;
    ChatAdapter chatAdapter;


    ChatAdapter mChatAdapter;

    public static final String THIS_BROADCAST_FOR_CHAT_SEARCHBAR = "this is for searchBar";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate( R.layout.fragment_chats, container, false );
        ButterKnife.bind( this, view );
        //chat
        IntentFilter intentFilter = new IntentFilter( THIS_BROADCAST );
        getActivity().registerReceiver( broadcastReceiver, intentFilter );

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager( getActivity() );
        mrecyclerview.setLayoutManager( linearLayoutManager );
        mrecyclerview.setHasFixedSize( true );
        //line divider bewt the cardview
        DividerItemDecoration itemDecor = new DividerItemDecoration( getActivity(), linearLayoutManager.getOrientation() );
        mrecyclerview.addItemDecoration( itemDecor );

        // context=container.getContext();
        DatabaseHelper databaseHelper = new DatabaseHelper( container.getContext() );
        mchat = new ArrayList<>();
        mchat = databaseHelper.chatList();
        Log.d( TAG, "list: " + mchat );

        mChatAdapter = new ChatAdapter( getActivity(), mchat );
        mrecyclerview.setAdapter( mChatAdapter );
        return view;
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            String ConversionID = bundle.getString( "ConversionID" );
            Log.d( TAG, "conversion12: " + ConversionID );
            Receiver( ConversionID);
        }
    };

    public void Receiver(String ConversionID) {
     DatabaseHelper databaseHelper=new DatabaseHelper( getActivity() );
try {
    Chat currentChat = databaseHelper.getChatByConversationId( ConversionID );
    for (Chat chat : mchat) {

        if (chat.message.getConversionID().equals( ConversionID )) {
            isExists = true;
            indexToremove = mchat.indexOf( chat );
            Log.d( TAG, "Receiver153: " + indexToremove );
            break;
        }

    }

    Log.d( TAG, "Final Indext to update: " + indexToremove );
    mChatAdapter.updateChatList( currentChat, indexToremove, isExists );
}catch (Exception e){

}

    }
private BroadcastReceiver broadcastforsearchbar=new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {

        List<Chat> data = (List<Chat>) intent.getSerializableExtra("data");

        mChatAdapter.setCollection( data );
        Log.d( TAG, "onReceive: "+data );
    }
};
    @Override
    public void onResume() {
        super.onResume();
        //search
        IntentFilter intentFilter1=new IntentFilter( THIS_BROADCAST_FOR_CHAT_SEARCHBAR );
        getActivity().registerReceiver( broadcastforsearchbar,intentFilter1 );
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver( broadcastforsearchbar);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getActivity().unregisterReceiver( broadcastReceiver );
    }
}
