package com.example.smarttalk.Adapter;


import android.content.BroadcastReceiver;
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
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.example.smarttalk.MessageActivity;
import com.example.smarttalk.ModelClass.User;
import com.example.smarttalk.R;
import com.example.smarttalk.database.model.Contact;

import java.util.List;

public class MyRecyclerAdapter  extends RecyclerView.Adapter<MyRecyclerAdapter.ViewHolder>  {
    private static final String TAG = "MyRecyclerAdapter";
public Context mcontext;
   // public List<User> users;
   public List<Contact> contacts;
    String s;

    public MyRecyclerAdapter(Context context, List<Contact> contactmodel) {
        mcontext=context;
        contacts=contactmodel;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mcontext)
                     .inflate( R.layout.activity_card_view, parent, false);
        return new ViewHolder(view);
       // return new ViewHolder( LayoutInflater.from(mcontext).inflate( R.layout.card_view_activity, parent, false)); this is also one way
    }
      @RequiresApi(api = Build.VERSION_CODES.N)
      @Override
        public void onBindViewHolder( ViewHolder holder, int position) {

         // final User uploadCurrent = users.get( position );  //getter & Setter
          final  Contact mcontact=contacts.get( position );
         // holder.Tname.setText( uploadCurrent.getFirstname().concat( uploadCurrent.getLastname() ) );
          holder.Tname.setText( mcontact.getFirstName() +" "+ mcontact.getLastName()  );
          Log.d( TAG, "onBindViewHolder: "+mcontact.getFirstName());
          holder.Tnumber.setText( mcontact.getMobileNmuber() );

          //color generator
          ColorGenerator generator=ColorGenerator.MATERIAL;    //color generator
          String x=mcontact.getFirstName();
          String[] myName = x.split(" ");
          for (int i = 0; i < myName.length; i++) {
               s = myName[i];
          }
          //https://github.com/amulyakhare/TextDrawable
          TextDrawable drawable2 = TextDrawable.builder()
                  .buildRound( String.valueOf( s.charAt( 0 ) ), generator.getRandomColor() );
          holder.image.setImageDrawable( drawable2 );

          holder.itemView.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View view) {
                  Intent intent = new Intent( mcontext, MessageActivity.class );
                  intent.putExtra( "ReceiverUserID", mcontact.getUserID() );
                  intent.putExtra( "number",mcontact.getMobileNmuber());
                  intent.putExtra( "name", mcontact.getFirstName() + " " + mcontact.getLastName() );
                  mcontext.startActivity( intent );
              }
          });
           // holder.Tnumber.setText(users.get(position).getNumber() ); this is also one way
        }
    @Override
    public int getItemCount() {

        return contacts.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {
           TextView Tname,Tnumber;
        ImageView image;
        ViewHolder(@NonNull View itemView) {
            super( itemView );
            Log.d( TAG, "ViewHolder: "+itemView );
            Tname=itemView.findViewById( R.id.name );
            Tnumber=itemView.findViewById( R.id.number );
             image = itemView.findViewById(R.id.image_view);

        }
    }
}
