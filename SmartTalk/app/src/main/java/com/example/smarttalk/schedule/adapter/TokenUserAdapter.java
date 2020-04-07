package com.example.smarttalk.schedule.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.smarttalk.R;
import com.example.smarttalk.modelclass.User;
import com.tokenautocomplete.FilteredArrayAdapter;

import java.util.List;


public class TokenUserAdapter extends FilteredArrayAdapter<User> {
    private static final String TAG = "TokenUserAdapter";

    public TokenUserAdapter(Context context, int resource, User[] objects) {
        super(context, resource, objects);
    }

    public TokenUserAdapter(Context context, int resource, int textViewResourceId, User[] objects) {
        super(context, resource, textViewResourceId, objects);
    }

    public TokenUserAdapter(Context context, int resource, List<User> objects) {
        super(context, resource, objects);
    }

    public TokenUserAdapter(Context context, int resource, int textViewResourceId, List<User> objects) {
        super(context, resource, textViewResourceId, objects);
    }
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {


            LayoutInflater l = (LayoutInflater)getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = l.inflate(R.layout.suggestiontoken, parent, false);

        }

        User p = getItem(position);

        String name = p.getFirstname().toString().trim().substring(0,1).toUpperCase() + p.getFirstname().toString().trim().substring(1).toLowerCase();

        String surname = p.getLastname().toString().trim().substring(0,1).toUpperCase() + p.getLastname().toString().trim().substring(1).toLowerCase();

        String fullName = name + " " + surname;


        ((TextView)convertView.findViewById(R.id.suggestion)).setText(fullName);

        return convertView;
    }

    @Override
    protected boolean keepObject(User obj, String mask) {
        mask= mask.toLowerCase();
        return obj.getFirstname().toLowerCase().startsWith(mask) || obj.getMobilenumber().startsWith(mask);

    }
}
