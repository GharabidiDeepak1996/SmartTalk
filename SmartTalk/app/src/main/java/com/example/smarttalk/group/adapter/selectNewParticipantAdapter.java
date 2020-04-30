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
import androidx.recyclerview.widget.RecyclerView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.bumptech.glide.Glide;
import com.example.smarttalk.R;
import com.example.smarttalk.constants.NetworkConstants;
import com.example.smarttalk.modelclass.User;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;


public class selectNewParticipantAdapter extends RecyclerView.Adapter<selectNewParticipantAdapter.viewHolder> {
    private List<User> mlist;
    private Context mcontext;
    private ArrayList<String> model = new ArrayList<String>();
    private NotifyMeInterface mcallback;
    String userID;

    private static final String TAG = "selectNewParticipantAda";

    public interface NotifyMeInterface {
        void handleData(User user, int requestCode);
    }

    public selectNewParticipantAdapter(List<User> list, Context context, NotifyMeInterface callback) {
        mlist = list;
        mcontext = context;
        mcallback = callback;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mcontext).inflate(R.layout.item_user_cardview, parent, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        final User muser = mlist.get(position);

        if (muser.getFirstname() == null) {
            Log.d(TAG, "onBindViewHolder: ");
        } else {
            holder.listTextView.get(0).setText(muser.getFirstname().concat(muser.getLastname()));
            holder.listTextView.get(1).setText(muser.getMobilenumber());

            Log.d(TAG, "onBindViewHolder56: "+muser.getRegistrationTokenID());

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
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick45: " + "add" + model.size());

                if (model.contains(muser.getUserId())) {
                    model.remove(muser.getUserId());
                    holder.rightTik.setVisibility(View.GONE);
                    mcallback.handleData(mlist.get(position), NetworkConstants.USER_REMOVED);

                } else {

                    model.add(muser.getUserId());
                    holder.rightTik.setVisibility(View.VISIBLE);
                    mcallback.handleData(mlist.get(position), NetworkConstants.USER_ADDED); //interface communicator

                }
            }
        });

    }


    //converter is required for circleimageview does not support the textdrawable to drawable
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

    public void restView(User list) {


    }

    @Override
    public int getItemCount() {
        return mlist.size();
    }


    class viewHolder extends RecyclerView.ViewHolder {
        @BindViews({R.id.name, R.id.number})
        List<TextView> listTextView;
        @BindView(R.id.image_view)
        CircleImageView image;
        @BindView(R.id.rightTik)
        CircleImageView rightTik;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }
    }
}
