package com.example.smarttalk.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smarttalk.R;
import com.example.smarttalk.database.model.Chat;

import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;


public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.viewHolder> {
    Context mcontext;
    List<Chat> mchat;
    private static final String TAG = "ChatAdapter";
    public ChatAdapter(Context context, List<Chat> nchat) {
        mcontext=context;
        mchat=nchat;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from( mcontext ).inflate( R.layout.chat_card_view,parent,false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
    Chat chat=mchat.get( position );
        Log.d( TAG, "onBindViewHolder123: "+position);

   /* holder.listTextView.get(0).setText( chat.contact.getFirstName().concat( chat.contact.getLastName() ) );
    holder.body.setText( chat.message.getBody() );
    holder.timestamp.setText( chat.message.getTimeStamp() );*/
    }

    @Override
    public int getItemCount() {
        return mchat.size();
    }

    class viewHolder extends RecyclerView.ViewHolder {
        @BindViews({R.id.name_textview,R.id.body_textview,R.id. timestamp_textview} ) List<TextView> listTextView ;

        ImageView drawimage;
        public viewHolder(@NonNull View itemView) {
            super( itemView );
     ButterKnife.bind( this,itemView );


        }
    }
}
