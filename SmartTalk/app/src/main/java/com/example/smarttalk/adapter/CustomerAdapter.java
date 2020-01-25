package com.example.smarttalk.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.example.smarttalk.R;
import com.example.smarttalk.activity.SchedulingMessageActivity;
import com.example.smarttalk.modelclass.User;

import java.util.ArrayList;
import java.util.List;

public class CustomerAdapter extends ArrayAdapter<User> {
    private List<User> tempCustomer;
    private List<User> suggestions;
    private String s;
    Context mcontext;
    private static final String TAG = "CustomerAdapter";
    public CustomerAdapter(Context context, List<User> objects) {

        super(context, android.R.layout.simple_list_item_1, objects);
        mcontext=context;
        this.tempCustomer = new ArrayList<>(objects);
        this.suggestions = new ArrayList<>(objects);
        Log.d(TAG, "CustomerAdapter: "+objects);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        User muser = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.contact_card_view, parent, false);
        }
        assert muser != null;
        Log.d(TAG, "getView: "+muser.getFirstname());
        TextView Name = convertView.findViewById(R.id.name);
        TextView Number = convertView.findViewById(R.id.number);
        ImageView Imageicon = convertView.findViewById(R.id.image_view);
        ColorGenerator generator = ColorGenerator.MATERIAL;    //color generator
        String x = muser.getFirstname();
        String[] myName = x.split(" ");
        for (String value : myName) {
            s = value;
        }


        //https://github.com/amulyakhare/TextDrawable
        TextDrawable drawable2 = TextDrawable.builder()
                .buildRound(String.valueOf(s.charAt(0)), generator.getRandomColor());
        Imageicon.setImageDrawable(drawable2);
        Name.setText(muser.getFirstname().concat(muser.getLastname()));
        Number.setText(muser.getMobilenumber());


        return convertView;
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return myFilter;
    }

    private Filter myFilter = new Filter() {
        @Override
        public CharSequence convertResultToString(Object resultValue) {
            User muser = (User) resultValue;

            return muser.getUserId();
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            if (constraint != null) {
                suggestions.clear();
                for (User people : tempCustomer) {
                    try {
                        if (people.getFirstname().toLowerCase().startsWith(constraint.toString().toLowerCase())) {
                            suggestions.add(people);
                        }
                    }catch (Exception e){

                    }
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = suggestions;
                filterResults.count = suggestions.size();
                return filterResults;
            } else {
                return new FilterResults();
            }
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            List<User> c = (ArrayList<User>) results.values;
            if (results != null && results.count > 0) {
                clear();
                for (User cust : c) {
                    add(cust);
                    notifyDataSetChanged();
                }
            }
        }
    };
}
