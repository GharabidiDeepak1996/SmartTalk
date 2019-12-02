package com.example.smarttalk.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.example.smarttalk.MessageActivity;
import com.example.smarttalk.R;
import com.example.smarttalk.fragment.ChatsFragment;
import com.example.smarttalk.modelclass.Chat;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.viewHolder>  {
    Context mcontext;
    List<Chat> mchat;
    String s;

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
        final Chat chat = mchat.get( position );
        Log.d( TAG, "onBindViewHolder123: " +position );

        holder.Name.setText( chat.user.getFirstname() + "  " + (chat.user.getLastname()) );
        holder.Body.setText( chat.message.getBody() );
        holder.Timestamp.setText( chat.message.getTimeStamp() );

        //color generator
        ColorGenerator generator=ColorGenerator.MATERIAL;    //color generator
        String x=chat.user.getFirstname();
        String[] myName = x.split(" ");
        for (int i = 0; i < myName.length; i++) {
            s = myName[i];
        }
        //https://github.com/amulyakhare/TextDrawable
        TextDrawable drawable2 = TextDrawable.builder()
                .buildRound( String.valueOf( s.charAt( 0 ) ), generator.getRandomColor() );
        holder.drawimage.setImageDrawable( drawable2 );

        holder.itemView.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent( mcontext, MessageActivity.class );
                intent.putExtra( "ReceiverUserID", chat.user.getUserId() );
                intent.putExtra( "number",chat.user.getMobilenumber());
                intent.putExtra( "name", chat.user.getFirstname() + " " + chat.user.getLastname());
                mcontext.startActivity( intent );
            }
        } );

    }

    public void updateChatList(Chat chat, int indexToRemove, boolean isAlreaduExits) {
        Log.d( TAG, "isAlreaduExits: "+isAlreaduExits );
        if(isAlreaduExits){
            mchat.remove( indexToRemove);
        }
        mchat.add( 0, chat );
        notifyDataSetChanged();
    }

    public void setCollection(List<Chat> chatCollection) {
        mchat = chatCollection;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mchat.size();
    }

    class viewHolder extends RecyclerView.ViewHolder  {
       // @BindViews({R.id.name_textview,R.id.body_textview,R.id. timestamp_textview} ) List<TextView> listTextView ;
        @BindView( R.id.name_textview )TextView Name;
        @BindView( R.id.body_textview)TextView Body;
        @BindView( R.id.timestamp_textview )TextView Timestamp;
        @BindView( R.id.image ) ImageView drawimage;
        public viewHolder(@NonNull View itemView) {
            super( itemView );
     ButterKnife.bind( this,itemView );


        }
    }
}
