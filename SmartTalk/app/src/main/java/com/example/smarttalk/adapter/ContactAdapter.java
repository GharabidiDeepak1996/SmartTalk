package com.example.smarttalk.adapter;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.smarttalk.activity.MessageActivity;
import com.example.smarttalk.R;
import com.example.smarttalk.constants.AppConstant;
import com.example.smarttalk.modelclass.User;

import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder> {
    private Context mcontext;
    private List<User> contacts;
    private String s;

    public ContactAdapter(Context context, List<User> contactmodel) {
        mcontext = context;
        contacts = contactmodel;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mcontext).inflate(R.layout.contact_card_view, parent, false);
        return new ViewHolder(view);
        // return new ViewHolder( LayoutInflater.from(mcontext).inflate( R.layout.card_view_activity, parent, false)); this is also one way
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        // final User uploadCurrent = users.get( position );  //getter & Setter
        final User muser = contacts.get(position);

        if (muser.getFirstname() == null) {

        } else {
            holder.listTextView.get(0).setText(muser.getFirstname().concat(muser.getLastname()));
            holder.listTextView.get(1).setText(muser.getMobilenumber());

            //color generator
            ColorGenerator generator = ColorGenerator.MATERIAL;    //color generator
            String x = muser.getFirstname();
            String[] myName = x.split(" ");
            for (String value : myName) {
                s = value;

                //https://github.com/amulyakhare/TextDrawable
                TextDrawable drawable2 = TextDrawable.builder()
                        .buildRound(String.valueOf(s.charAt(0)), generator.getRandomColor());

                Drawable d = new BitmapDrawable(drawableToBitmap(drawable2));
//image holder
                Glide.with(mcontext)
                        .load(muser.getProfileImageURI())
                        .placeholder(d)
                        .into(holder.image);
            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent(mcontext, MessageActivity.class);
                    intent.putExtra("ReceiverUserID", muser.getUserId());
                    intent.putExtra("number", muser.getMobilenumber());
                    intent.putExtra("name", muser.getFirstname() + " " + muser.getLastname());
                    intent.putExtra("imageView", muser.getProfileImageURI());
                    mcontext.startActivity(intent);


                }
            });


            if (muser.getStatus() != null && muser.getStatus().equals("online")) {
                holder.status.setImageResource(R.color.online);
            } else {
                holder.status.setImageResource(R.color.offline);
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

        return contacts.size();
    }

    public void setContactList(List<User> contactList) {
        if (contacts != null)
            contacts.clear();

        contacts = contactList;
        notifyDataSetChanged();
    }

    public void setCollection(List<User> chatCollection) {
        contacts = chatCollection;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindViews({R.id.name, R.id.number})
        List<TextView> listTextView;
        @BindView(R.id.image_view)
        CircleImageView image;
        @BindView(R.id.statusChecker)
        CircleImageView status;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }
    }
}
