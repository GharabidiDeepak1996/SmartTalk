package com.example.smarttalk.group.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.bumptech.glide.Glide;
import com.example.smarttalk.R;
import com.example.smarttalk.group.activity.GroupDetailsActivity;
import com.example.smarttalk.modelclass.User;

import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class ParticipantsAdapter extends RecyclerView.Adapter<ParticipantsAdapter.viewHolder> {
    private Context mcontext;
    private List<User> muserList;
    private static final String TAG = "ParticipantsAdapter";

    public ParticipantsAdapter(Context context, List<User> userList) {
        mcontext=context;
        muserList=userList;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mcontext).inflate(R.layout.group_detail, parent, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        final User muser = muserList.get(position);
        if (position == 0) {
holder.view.setVisibility(View.GONE);
        } else {
            holder.view.setVisibility(View.VISIBLE);

            if (muser.getFirstname() == null) {
                Log.d(TAG, "onBindViewHolder: ");
            } else {
                holder.listTextView.get(0).setText(muser.getFirstname().concat(muser.getLastname()));
                holder.listTextView.get(1).setText(muser.getMobilenumber());

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
                            .into(holder.image);
                }

            }

        }
    }
    //converter is required for circleimageview does not support the textdrawable to drawable
    public static Bitmap drawableToBitmap(Drawable drawable) {
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
        return muserList.size();
    }


    class viewHolder extends RecyclerView.ViewHolder{
        @BindViews({R.id.name, R.id.number})
        List<TextView> listTextView;
        @BindView(R.id.image_view)
        CircleImageView image;

        @Nullable
        @BindView(R.id.rightTik)
        CircleImageView rightTik;

        @BindView(R.id.group_detail) View view;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }
    }
}
