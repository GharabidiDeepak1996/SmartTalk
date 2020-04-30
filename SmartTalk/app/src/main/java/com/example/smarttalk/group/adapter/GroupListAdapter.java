package com.example.smarttalk.group.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.bumptech.glide.Glide;
import com.example.smarttalk.R;
import com.example.smarttalk.activity.MessageActivity;
import com.example.smarttalk.adapter.ContactAdapter;
import com.example.smarttalk.constants.AppConstant;
import com.example.smarttalk.group.activity.GroupMessageActivity;
import com.example.smarttalk.group.pojo.GroupModel;
import com.example.smarttalk.modelclass.User;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.MODE_PRIVATE;
import static com.example.smarttalk.constants.AppConstant.SharedPreferenceConstant.LOGGED_IN_USER_CONTACT_NUMBER;
import static com.example.smarttalk.constants.AppConstant.SharedPreferenceConstant.LOOGED_IN_USER_ID;

public class GroupListAdapter extends RecyclerView.Adapter<GroupListAdapter.ViewHolder> {
private Context mcontext;
private List<GroupModel> mgroupModelList;
    private static final String TAG = "GroupListAdapter";
    public GroupListAdapter(Context context, List<GroupModel> groupModelList) {
        mcontext=context;
        mgroupModelList=groupModelList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mcontext).inflate(R.layout.contact_card_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        GroupModel model=mgroupModelList.get(position);
        holder.groupName.setText(model.getGroupName());

            StringBuilder builder = new StringBuilder();
            for(int i=0;i<model.members.size();i++){
                builder.append(model.members.get(i).getFirstname() + " "+model.members.get(i).getLastname() +",");
            }

        holder.noOfMembers.setText(builder);
        //color generator
        ColorGenerator generator = ColorGenerator.MATERIAL;    //color generator
        String x = model.getGroupName();
        String[] myName = x.split(" ");
        for (String value : myName) {
            String s = value;

            //https://github.com/amulyakhare/TextDrawable
            TextDrawable drawable2 = TextDrawable.builder()
                    .buildRound(String.valueOf(s.charAt(0)), generator.getRandomColor());

            Drawable bitmapDrawable = new BitmapDrawable(drawableToBitmap(drawable2));
//image holder
            Glide.with(mcontext)
                    .load(model.getGroupImage())
                    .placeholder(bitmapDrawable)
                    .into(holder.image);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences preferences = mcontext.getSharedPreferences(AppConstant.SharedPreferenceConstant.SHARED_PREF_NAME, MODE_PRIVATE);
                String mobileNumber = preferences.getString(LOGGED_IN_USER_CONTACT_NUMBER, "");

                List<String> tokenID =new ArrayList<>();
                for(int i=0;model.members.size()>i;i++){
                    if(!model.members.get(i).getMobilenumber().equals(mobileNumber)){
                        tokenID.add(model.members.get(i).getRegistrationTokenID());
                    }
                }
                List<String> members=new ArrayList<>();
                for(int i=0;model.members.size()>i;i++){
                    members.add(model.members.get(i).getFirstname() +" "+model.members.get(i).getLastname());
                }

                Intent intent=new Intent(mcontext, GroupMessageActivity.class);
                intent.putExtra("registrationID", (Serializable) tokenID);
                intent.putExtra("members", (Serializable) members);
                intent.putExtra("groupName",model.getGroupName());
                intent.putExtra("groupImage",model.getGroupImage());
                intent.putExtra("groupID",model.getGroupID());
                mcontext.startActivity(intent);

            }
        });
    }

    private static Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }


        int width = drawable.getIntrinsicWidth();
        width = width > 0 ? width : 96; // Replaced the 1 by a 96
        int height = drawable.getIntrinsicHeight();
        height = height > 0 ? height : 96; // Replaced the 1 by a 96

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    @Override
    public int getItemCount() {
        return mgroupModelList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.name) TextView groupName;
        @BindView(R.id.number) TextView noOfMembers;
        @BindView(R.id.image_view) CircleImageView image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }
    }
}
