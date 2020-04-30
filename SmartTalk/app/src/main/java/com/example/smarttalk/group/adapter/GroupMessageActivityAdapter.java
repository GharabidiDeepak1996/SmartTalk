package com.example.smarttalk.group.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.smarttalk.R;
import com.example.smarttalk.adapter.MessageAdapter;
import com.example.smarttalk.constants.AppConstant;
import com.example.smarttalk.group.activity.GroupMessageActivity;
import com.example.smarttalk.group.pojo.GroupMessages;

import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;

import static android.content.Context.MODE_PRIVATE;
import static com.example.smarttalk.constants.NetworkConstants.MIDDLE_DATE_SHOW;
import static com.example.smarttalk.constants.NetworkConstants.MSG_TYPE_LEFT;
import static com.example.smarttalk.constants.NetworkConstants.MSG_TYPE_RIGHT;

public class GroupMessageActivityAdapter extends RecyclerView.Adapter<GroupMessageActivityAdapter.ViewHolder> {
    Context mcontext;
    List<GroupMessages> mgroupMessagesList;
    String firstThirtyEightChars ;
    private static final String TAG = "GroupMessageActivityAda";
    public GroupMessageActivityAdapter(Context context, List<GroupMessages> groupMessagesList) {
        mcontext=context;
        mgroupMessagesList=groupMessagesList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        switch (viewType){
            case MSG_TYPE_RIGHT:
                view = LayoutInflater.from(mcontext).inflate(R.layout.message_on_rightside, parent, false);
                return new ViewHolder(view);
            case MSG_TYPE_LEFT:
                view = LayoutInflater.from(mcontext).inflate(R.layout.message_on_leftside, parent, false);
                return new ViewHolder(view);
            case MIDDLE_DATE_SHOW:
                view = LayoutInflater.from(mcontext).inflate(R.layout.conversation_message_item_sender_date_view, parent, false);
                return new ViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
       GroupMessages groupMessages=  mgroupMessagesList.get(position);

        String url=mgroupMessagesList.get(position).messageBody;

        holder.listTextView.get(1).setText(groupMessages.getTimeStamp());

        if(url.length()>38 ) {
            firstThirtyEightChars = url.substring(0, 38);
        }else{
            firstThirtyEightChars=url;
        }
        if(firstThirtyEightChars.equals("https://firebasestorage.googleapis.com")){
            //this for Images
            holder.listTextView.get(0).setVisibility(View.GONE);
            holder.showImages.setVisibility(View.VISIBLE);
          //  holder.textName.setText(groupMessages.senderName);

            Glide.with(mcontext)
                    .load(url)
                    .placeholder(R.mipmap.avatar)
                    .into(holder.showImages);
        }else {
            //this for text
            holder.showImages.setVisibility(View.GONE);
            holder.listTextView.get(0).setVisibility(View.VISIBLE);
            holder.listTextView.get(0).setText(groupMessages.getMessageBody());
            Log.d(TAG, "onBindViewHolder653: "+groupMessages.senderName);
             holder.textName.setText(groupMessages.senderName);
        }
    }
    @Override
    public int getItemViewType(int position) {
        SharedPreferences sharedPreferences = mcontext.getSharedPreferences(AppConstant.SharedPreferenceConstant.SHARED_PREF_NAME, MODE_PRIVATE);
        String SenderID = sharedPreferences.getString(AppConstant.SharedPreferenceConstant.LOOGED_IN_USER_ID, "");
        if(mgroupMessagesList.get(position).getSenderID().equals(SenderID)){
            return MSG_TYPE_RIGHT;
        }else {
            return MSG_TYPE_LEFT;
        }

    }

    @Override
    public int getItemCount() {
        return mgroupMessagesList.size();
    }
public void notifyToList(List<GroupMessages> groupMessagesList){
    mgroupMessagesList=groupMessagesList;
    notifyDataSetChanged();

}
    class ViewHolder extends RecyclerView.ViewHolder{
        @BindViews({R.id.show_message, R.id.timestamp}) List<TextView> listTextView;
        @Nullable
        @BindView(R.id.clock)
        ImageView imageView;
        @Nullable
        @BindView(R.id.show_image) ImageView showImages;
        @Nullable
        @BindView(R.id.members_name) TextView textName;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }
    }
}
