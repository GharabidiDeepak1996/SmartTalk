package com.example.smarttalk.group.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.bumptech.glide.Glide;
import com.example.smarttalk.R;
import com.example.smarttalk.constants.AppConstant;
import com.example.smarttalk.constants.NetworkConstants;
import com.example.smarttalk.group.activity.SelectNewParticipants;
import com.example.smarttalk.modelclass.User;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.MODE_PRIVATE;

public class selectedUserListAdapter extends RecyclerView.Adapter<selectedUserListAdapter.viewHolder> {
private List<User> mlist;
private Context mcontext;
private NotifyMeInterface mcallback;
private ArrayList<String> model = new ArrayList<String>();

    public interface NotifyMeInterface {
        void handleData(User user, int requestCode);
    }

    public selectedUserListAdapter(List<User> list, Context context,NotifyMeInterface callback) {
        mcontext=context;
        mlist=list;
        mcallback=callback;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mcontext).inflate(R.layout.item_selected_user, parent, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        User muser = mlist.get(position);

        if (position == 0) {
holder.view.setVisibility(View.GONE);
        } else {
            holder.view.setVisibility(View.VISIBLE);
            holder.userName.setText(muser.getFirstname() + "  " + muser.getLastname());
            //color generator
            ColorGenerator generator = ColorGenerator.MATERIAL;    //color generator
            String x = muser.getFirstname();
            String[] myName = x.split(" ");
            for (String value : myName) {

                //https://github.com/amulyakhare/TextDrawable
                TextDrawable drawable2 = TextDrawable.builder()
                        .buildRound(String.valueOf(value.charAt(0)), generator.getRandomColor());

                Drawable d = new BitmapDrawable(drawableToBitmap(drawable2));
//image holder
                Glide.with(mcontext)
                        .load(muser.getProfileImageURI())
                        .placeholder(d)
                        .into(holder.profileImage);
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mcallback.handleData(mlist.get(position), NetworkConstants.USER_REMOVED);
                    mlist.remove(muser);
                    notifyDataSetChanged();
                }
            });
        }
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
        return mlist.size();
    }

    class viewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.user_name) TextView userName;
        @BindView(R.id.profile_Image) CircleImageView profileImage;
        @BindView(R.id.cancel_tik) CircleImageView cancelTik;
        @BindView(R.id.layout) View view;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }
    }
}
