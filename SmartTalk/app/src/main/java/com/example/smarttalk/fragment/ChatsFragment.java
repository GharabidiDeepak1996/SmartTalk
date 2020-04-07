package com.example.smarttalk.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smarttalk.R;
import com.example.smarttalk.schedule.activity.CreateSchedule;
import com.example.smarttalk.schedule.activity.MessageSchedule;
import com.example.smarttalk.adapter.ChatAdapter;
import com.example.smarttalk.databasehelper.DatabaseHelper;
import com.example.smarttalk.modelclass.Chat;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.smarttalk.activity.MessageActivity.THIS_BROADCAST;


public class ChatsFragment extends Fragment {
    @BindView(R.id.chat_recycler_view)
    RecyclerView mrecyclerview;
    private List<Chat> mchat;
    private Boolean isExists;
    private int indexToremove;
    private ChatAdapter mChatAdapter;
    private FloatingActionButton febMenu,febCreate,febClose;
    public boolean isClick=false;
    Float translationY=100f;
    OvershootInterpolator interpolator=new OvershootInterpolator();

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
        //line divider bewt the cardview
        DividerItemDecoration itemDecor = new DividerItemDecoration( getActivity(), linearLayoutManager.getOrientation() );
        mrecyclerview.addItemDecoration( itemDecor );

        // context=container.getContext();
        DatabaseHelper databaseHelper= new DatabaseHelper( container.getContext() );
        mchat = new ArrayList<>();
       mchat = databaseHelper.chatList();
        mChatAdapter = new ChatAdapter( getActivity(), mchat );
        mrecyclerview.setAdapter( mChatAdapter );
        febMenu=view.findViewById(R.id.floating_button_menu);
        febCreate=view.findViewById(R.id.floating_button_create);
        febClose=view.findViewById(R.id.floating_button_cancel);
        floatingButton();
        floatingAnimation();
onClickFloatingButtonCancelScheduleMessages();
onClickFloatingButtonCreateMessageSchedule();

        return view;
    }

    //coming form message Activity
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            String ConversionID = bundle.getString( "ConversionID" );
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
            break;
        }

    }

    mChatAdapter.updateChatList( currentChat, indexToremove, isExists );
}catch (Exception e){

}

    }
private BroadcastReceiver broadcastforsearchbar=new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {

        List<Chat> data = (List<Chat>) intent.getSerializableExtra("data");

        mChatAdapter.setCollection( data );

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
    public void onClickFloatingButtonCreateMessageSchedule(){
febCreate.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        Intent intent = new Intent(getActivity(), CreateSchedule.class);

        startActivity(intent);
    }
});
    }
    public void onClickFloatingButtonCancelScheduleMessages(){
febClose.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        Intent intent = new Intent(getActivity(), MessageSchedule.class);
        startActivity(intent);
    }
});
    }
   public void floatingButton(){
        febMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isClick==true){
                    closeSubMenusFab();
                } else {
                    openSubMenusFab();
                }
            }
        });
   }
   public void floatingAnimation(){
        febClose.setAlpha(0f);
        febCreate.setAlpha(0f);

        febCreate.setTranslationY(translationY);
       febClose.setTranslationY(translationY);
   }
    //closes FAB submenus
    private void closeSubMenusFab(){
        febMenu.animate().setInterpolator(interpolator).rotationBy(0f).setDuration(300).start();

        febCreate.animate().translationY(translationY).alpha(0f).setInterpolator(interpolator).setDuration(300).start();
        febClose.animate().translationY(translationY).alpha(0f).setInterpolator(interpolator).setDuration(300).start();
        febMenu.setImageResource(R.drawable.ic_add_black_24dp);
        isClick = false;
    }

    //Opens FAB submenus
    private void openSubMenusFab(){
        febMenu.animate().setInterpolator(interpolator).rotationBy(45f).setDuration(300).start();


        febCreate.animate().translationY(0f).alpha(1f).setInterpolator(interpolator).setDuration(300).start();
        febClose.animate().translationY(0f).alpha(1f).setInterpolator(interpolator).setDuration(300).start();
        febMenu.setImageResource(R.drawable.ic_cancel_black_24dp);
        isClick = true;
    }
}
