package com.example.smarttalk.schedule.adapter;

import android.content.Context;
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
import com.example.smarttalk.R;
import com.example.smarttalk.modelclass.ScheduleMessage;
import com.example.smarttalk.schedule.activity.MessageSchedule;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class MessageScheduleAdapter extends RecyclerView.Adapter<MessageScheduleAdapter.messageScheduleViewHolder> {
    private static final String TAG = "MessageScheduleAdapter";
    private Context context;
    private List<ScheduleMessage> list;
    String s;

    public MessageScheduleAdapter(Context mcontext, List<ScheduleMessage> mList) {
        context = mcontext;
        list = mList;
    }

    @NonNull
    @Override
    public messageScheduleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_schedule_card_view, parent, false);

        return new messageScheduleViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull messageScheduleViewHolder holder, int position) {
        ScheduleMessage  message= list.get(position);
        Log.d(TAG, "onBindViewHolder: "+message.getReceiverName());

        holder.Name.setText(message.getSenderName() +" TO  " +message.getReceiverName());
        holder.Body.setText(message.getMessageBody());
        holder.Timestamp.setText(message.getTimeStamp());

        //color generator
        ColorGenerator generator=ColorGenerator.MATERIAL;    //color generator
        String x=message.getSenderName();
        String[] myName = x.split(" ");
        for (int i = 0; i < myName.length; i++) {
            s = myName[i];
        }
        //https://github.com/amulyakhare/TextDrawable
        TextDrawable drawable2 = TextDrawable.builder().buildRound( String.valueOf( s.charAt( 0 ) ), generator.getRandomColor() );
        holder.profileImage.setImageDrawable(drawable2);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void removeItem(int position) {
        list.remove(position);
        notifyItemRemoved(position);
    }

    public void restoreItem(ScheduleMessage item, int position) {
        list.add(position, item);
        notifyItemInserted(position);
    }
public void updateList(List<ScheduleMessage> mlist){
        list=mlist;
        notifyDataSetChanged();

}
    public List<ScheduleMessage> getData() {
        return list;
    }

    public class messageScheduleViewHolder extends RecyclerView.ViewHolder {
        @BindView( R.id.name_textview )
        TextView Name;
        @BindView( R.id.body_textview)TextView Body;
        @BindView( R.id.timestamp_textview )TextView Timestamp;
        @BindView( R.id.image )
        ImageView profileImage;

        public messageScheduleViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind( this,itemView );

        }
    }
}
