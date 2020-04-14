package com.example.smarttalk.group.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.smarttalk.R;
import com.example.smarttalk.group.activity.SelectNewParticipants;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class GroupFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_group, container, false);



      return view;
    }
    private void floatingButton(){

        FloatingActionButton button=getActivity().findViewById(R.id.floating_button_create_new_group);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(), SelectNewParticipants.class);
                getActivity().startActivity(intent);
            }
        });
    }
}
