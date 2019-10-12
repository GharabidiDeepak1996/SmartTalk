package com.example.smarttalk.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.smarttalk.R;
import com.example.smarttalk.Retrofit.Data;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
    public static  final int MSG_TYPE_LEFT = 0;
    public static  final int MSG_TYPE_RIGHT = 1;

    private Context mContext;
    private List<Data> mChat;

    public MessageAdapter(Context mcontext,List<Data> mchat) {
        mContext=mcontext;
        mChat=mchat;
    }

    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MSG_TYPE_RIGHT) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_right, parent, false);
            return new MessageAdapter.ViewHolder(view);
        } else {
            View view = LayoutInflater.from(mContext).inflate( R.layout.chat_item_left, parent, false);
            return new MessageAdapter.ViewHolder(view);
        }
    }
    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.ViewHolder holder, int position) {
        Data chat = mChat.get(position);
        holder.show_message.setText(chat.Body);
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
  /*  @Override
    public int getItemViewType(int position) {

        if (mChat.get(position).SenderID.equals(new Data().SenderID )){
            return MSG_TYPE_RIGHT;
        } else {
            return MSG_TYPE_LEFT;
        }
    }*/
}
