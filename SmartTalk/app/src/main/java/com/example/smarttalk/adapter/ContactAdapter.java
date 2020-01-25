package com.example.smarttalk.adapter;


import android.content.Context;
import android.content.Intent;
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
import com.example.smarttalk.activity.MessageActivity;
import com.example.smarttalk.R;
import com.example.smarttalk.modelclass.User;

import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder>  {
    private static final String TAG = "ContactAdapter";
public Context mcontext;
   // public List<User> users;
   public List<User> contacts;
    String s;

    public ContactAdapter(Context context, List<User> contactmodel) {
        mcontext=context;
        contacts=contactmodel;
        Log.d( TAG, "context12: "+contactmodel );
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mcontext)
                .inflate( R.layout.contact_card_view, parent, false);
        return new ViewHolder(view);
       // return new ViewHolder( LayoutInflater.from(mcontext).inflate( R.layout.card_view_activity, parent, false)); this is also one way
    }
      @RequiresApi(api = Build.VERSION_CODES.N)
      @Override
        public void onBindViewHolder( ViewHolder holder, int position) {

         // final User uploadCurrent = users.get( position );  //getter & Setter
          final  User mcontact=contacts.get( position );
          Log.d(TAG, "contactadaper: "+mcontact.getFirstname());


              holder.listTextView.get(0).setText(mcontact.getFirstname().concat(mcontact.getLastname()));
              holder.listTextView.get(1).setText(mcontact.getMobilenumber());

              //color generator
              ColorGenerator generator = ColorGenerator.MATERIAL;    //color generator
              String x = mcontact.getFirstname();
              String[] myName = x.split(" ");
              for (int i = 0; i < myName.length; i++) {
                  s = myName[i];
              }

              //https://github.com/amulyakhare/TextDrawable
              TextDrawable drawable2 = TextDrawable.builder()
                      .buildRound(String.valueOf(s.charAt(0)), generator.getRandomColor());
              holder.image.setImageDrawable(drawable2);

              holder.itemView.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View view) {
                      Intent intent = new Intent(mcontext, MessageActivity.class);
                      intent.putExtra("ReceiverUserID", mcontact.getUserId());
                      intent.putExtra("number", mcontact.getMobilenumber());
                      intent.putExtra("name", mcontact.getFirstname() + " " + mcontact.getLastname());
                      mcontext.startActivity(intent);
                  }
              });

           // holder.Tnumber.setText(users.get(position).getNumber() ); this is also one way
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
           @BindViews({R.id.name,R.id.number } ) List<TextView> listTextView ;
           @BindView( R.id.image_view ) ImageView image;

         ViewHolder(@NonNull View itemView) {
            super( itemView );
           ButterKnife.bind( this,itemView );


        }
    }
}
