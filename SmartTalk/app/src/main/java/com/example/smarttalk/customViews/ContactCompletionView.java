package com.example.smarttalk.customViews;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;


import com.example.smarttalk.R;
import com.example.smarttalk.modelclass.User;
import com.tokenautocomplete.TokenCompleteTextView;

public class ContactCompletionView extends TokenCompleteTextView<User> {
    private Context mContext;
    TextView textView;


    public ContactCompletionView(Context context) {
        super(context);
        mContext = context;
    }

    public ContactCompletionView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public ContactCompletionView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
    }

    @Override
    protected View getViewForObject(User user) {
        //receive the values form adapter
        View view = LayoutInflater.from(mContext).inflate(R.layout.contact_token_view, null, false);
        textView = view.findViewById(R.id.show_contact);

        String fullName = user.getFirstname() + " " +user.getLastname();

        textView.setText(fullName);
        return view;
    }

    @Override
    protected User defaultObject(String completionText) {
        return null;
    }


}
