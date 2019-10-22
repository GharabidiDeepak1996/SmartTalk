package com.example.smarttalk.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.smarttalk.R;
import com.example.smarttalk.constants.AppConstant;
import com.example.smarttalk.database.model.Message;

import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
    public static  final int MSG_TYPE_LEFT = 0;
    public static  final int MSG_TYPE_RIGHT = 1;
    private static final String TAG = "MessageAdapter";

    private Context mContext;
    private List<Message> mChat;

    public MessageAdapter(Context mcontext,List<Message> mchat) {
        mContext=mcontext;
        mChat=mchat;
    }

    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MSG_TYPE_RIGHT) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.message_on_rightside, parent, false);
            return new MessageAdapter.ViewHolder(view);
        } else {
            View view = LayoutInflater.from(mContext).inflate( R.layout.message_on_leftside, parent, false);
            return new MessageAdapter.ViewHolder(view);
        }
    }
    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.ViewHolder holder, int position) {
        Message chat = mChat.get(position);
        holder.show_message.setText(chat.getBody());
    }

    public void addMessageToAdapter(Message message){
        mChat.add( message );
        this.notifyItemInserted( mChat.size()-1 );
    }
    @Override
    public int getItemViewType(int position) {
        SharedPreferences sharedPreferences=mContext.getSharedPreferences( AppConstant.SharedPreferenceConstant.SHARED_PREF_NAME, MODE_PRIVATE );
       String SenderID=  sharedPreferences.getString( AppConstant.SharedPreferenceConstant.LOOGED_IN_USER_ID,"" );
        Log.d( TAG, "getItemViewType 1: "+mChat.get(position).getSenderID());
        Log.d( TAG, "getItemViewType 2: "+SenderID );
        if (mChat.get(position).getSenderID().equals(SenderID )){
            return MSG_TYPE_RIGHT;
        } else {
            return MSG_TYPE_LEFT;
        }
    }
    @Override
    public int getItemCount() {
        return mChat.size() ;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView show_message;
        public ViewHolder(@NonNull View itemView) {
            super( itemView );

            show_message=itemView.findViewById( R.id.show_message);
        }
    }
}
