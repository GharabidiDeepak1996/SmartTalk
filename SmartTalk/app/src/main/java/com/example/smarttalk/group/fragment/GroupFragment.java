package com.example.smarttalk.group.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.smarttalk.R;
import com.example.smarttalk.constants.AppConstant;
import com.example.smarttalk.group.activity.SelectNewParticipants;
import com.example.smarttalk.group.adapter.GroupListAdapter;
import com.example.smarttalk.group.pojo.GroupModel;
import com.example.smarttalk.modelclass.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;
import uk.co.deanwild.materialshowcaseview.ShowcaseTooltip;

import static android.content.Context.MODE_PRIVATE;
import static com.example.smarttalk.constants.AppConstant.SharedPreferenceConstant.LOGGED_IN_USER_CONTACT_NUMBER;

public class GroupFragment extends Fragment {
    @BindView(R.id.list_of_group)
    RecyclerView recyclerView;
    private static final String SHOWCASE_ID = "tooltip example";
    FloatingActionButton button;


    private static final String TAG = "GroupFragment";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_group, container, false);
        ButterKnife.bind( this,view );

        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);

        DividerItemDecoration itemDecor = new DividerItemDecoration( getActivity(), linearLayoutManager.getOrientation() );
        recyclerView.addItemDecoration( itemDecor );

        SharedPreferences preferences = getActivity().getSharedPreferences(AppConstant.SharedPreferenceConstant.SHARED_PREF_NAME, MODE_PRIVATE);
        String mobileNumber = preferences.getString(LOGGED_IN_USER_CONTACT_NUMBER, "");


        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("Groups");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<GroupModel> groupModelList=new ArrayList<>();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    GroupModel groupModel = postSnapshot.getValue( GroupModel.class );
                    for(int j=0;groupModel.members.size()>j;j++ ){
                        if(groupModel.members.get(j).getMobilenumber().equals(mobileNumber)) {
                            GroupModel model=new GroupModel();
                            model.setGroupID(groupModel.getGroupID());
                            model.setGroupName(groupModel.getGroupName());
                            model.setGroupImage(groupModel.getGroupImage());
                            model.setMembers(groupModel.members);

                            groupModelList.add(model);
                        }
                    }
                }

                GroupListAdapter groupListAdapter=new GroupListAdapter(getActivity(),groupModelList);
                recyclerView.setAdapter(groupListAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        }) ;

         button=view.findViewById(R.id.floating_button_create_new_group);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(), SelectNewParticipants.class);
                getActivity().startActivity(intent);
            }
        });
      return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        introduceFirstTime();

    }

    private void introduceFirstTime() {
        MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(getActivity(), SHOWCASE_ID);

        ShowcaseTooltip toolTip = ShowcaseTooltip.build(getActivity())
                .corner(30)
                .textColor(Color.parseColor("#007686"))
                .text("This is button for <b>Create Group</b> ");

        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder(getActivity())
                        .setTarget(button)
                        .setToolTip(toolTip)
                        .setTooltipMargin(30)
                        .setShapePadding(50)
                        .setDismissOnTouch(true)
                        .setMaskColour(getResources().getColor(R.color.tooltip_mask))
                        .build()
        );

        sequence.start();
    }
}
