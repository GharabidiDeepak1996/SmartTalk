package com.example.smarttalk.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.smarttalk.R;
import com.example.smarttalk.constants.AppConstant;
import com.example.smarttalk.constants.NetworkConstants;
import com.example.smarttalk.modelclass.Message;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.Optional;

import static android.content.Context.MODE_PRIVATE;
import static com.example.smarttalk.constants.NetworkConstants.MIDDLE_DATE_SHOW;
import static com.example.smarttalk.constants.NetworkConstants.MSG_TYPE_LEFT;
import static com.example.smarttalk.constants.NetworkConstants.MSG_TYPE_RIGHT;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {


    String firstThirtyEightChars ;

    private Context mContext;
    private List<Message> mChat;

    private static final String TAG = "MessageAdapter";
    public MessageAdapter(Context mcontext, List<Message> mchat) {
        mContext = mcontext;
        mChat = mchat;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
View view;
        Log.d(TAG, "onCreateViewHolder2346: "+viewType);
       switch (viewType){
           case MSG_TYPE_RIGHT:
                view = LayoutInflater.from(mContext).inflate(R.layout.message_on_rightside, parent, false);
               return new MessageAdapter.ViewHolder(view);
           case MSG_TYPE_LEFT:
                view = LayoutInflater.from(mContext).inflate(R.layout.message_on_leftside, parent, false);
               return new MessageAdapter.ViewHolder(view);
           case MIDDLE_DATE_SHOW:
                view = LayoutInflater.from(mContext).inflate(R.layout.conversation_message_item_sender_date_view, parent, false);
               return new MessageAdapter.ViewHolder(view);
       }
       return null;
    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.ViewHolder holder, int position) {
        Message message = mChat.get(position);
        Log.d(TAG, "onBindViewHolder568: "+message.getBody());
        String url=mChat.get(position).getBody();

        holder.listTextView.get(1).setText(message.getTimeStamp());
        SharedPreferences mPreference = mContext.getSharedPreferences(AppConstant.SharedPreferenceConstant.SHARED_PREF_NAME, MODE_PRIVATE);
        String UserID = mPreference.getString(AppConstant.SharedPreferenceConstant.LOOGED_IN_USER_ID, null);

        if (message.getSenderID().equals(UserID)) {
            if (AppConstant.SharedPreferenceConstant.MESSAGE_PENDING.equalsIgnoreCase(message.getDeliveryStatus())) {
               holder.imageView.setImageResource(R.drawable.ic_access_time_black_24dp);
            } else {
                holder.imageView.setImageResource(R.drawable.ic_done_black_24dp);
            }
        }
        if(url.length()>38 ) {
            firstThirtyEightChars = url.substring(0, 38);
        }else{
            firstThirtyEightChars=url;
        }
        if(firstThirtyEightChars.equals("https://firebasestorage.googleapis.com")){
            //this for Images
            holder.listTextView.get(0).setVisibility(View.GONE);
            holder.showImages.setVisibility(View.VISIBLE);

            Glide.with(mContext)
                    .load(url)
                    .placeholder(R.mipmap.avatar)
                    .into(holder.showImages);
        }else {
            //this for text
            holder.showImages.setVisibility(View.GONE);
            holder.listTextView.get(0).setVisibility(View.VISIBLE);
            holder.listTextView.get(0).setText(message.getBody());
        }
    }

    public void updateMessageToAdapter(Message messageData) {
        int indexToUpdate = -1;
        for (Message message : mChat) {
            if (message.getMessageID().equalsIgnoreCase(messageData.getMessageID())) {
                indexToUpdate = mChat.indexOf(message);
                break;
            }
        }
        if (indexToUpdate != -1) {
            mChat.set(indexToUpdate, messageData);
            notifyItemChanged(indexToUpdate);
        }
    }

    public void addMessageToAdapter(Message message) {
        mChat.add(message);
        this.notifyItemInserted(mChat.size() - 1);
    }

    @Override
    public int getItemViewType(int position) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(AppConstant.SharedPreferenceConstant.SHARED_PREF_NAME, MODE_PRIVATE);
        String SenderID = sharedPreferences.getString(AppConstant.SharedPreferenceConstant.LOOGED_IN_USER_ID, "");
        if(mChat.get(position).getSenderID().equals(SenderID)){
            return MSG_TYPE_RIGHT;
        }else if(mChat.get(position).getSenderID()!=(SenderID)){
            return MSG_TYPE_LEFT;
        }else if(mChat.get(position).getSenderID().equals(SenderID)){
            return MIDDLE_DATE_SHOW;
        }
        return -1;
    }

    @Override
    public int getItemCount() {
        return mChat.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
   //   @BindViews(  {R.id.show_message,R.id.timestamp} ) TextView show_message,time_stamp;
        //or
        @BindViews({R.id.show_message, R.id.timestamp}) List<TextView> listTextView;
        @Nullable
        @BindView(R.id.clock) ImageView imageView;
        @Nullable
        @BindView(R.id.show_image) ImageView showImages;
        @Nullable
        @BindView(R.id.setDate) TextView middleDateShow;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }
    }
}
