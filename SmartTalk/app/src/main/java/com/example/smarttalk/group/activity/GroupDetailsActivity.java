package com.example.smarttalk.group.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.smarttalk.R;
import com.example.smarttalk.Utils;
import com.example.smarttalk.constants.AppConstant;
import com.example.smarttalk.group.adapter.ParticipantsAdapter;
import com.example.smarttalk.group.pojo.GroupModel;
import com.example.smarttalk.modelclass.Chat;
import com.example.smarttalk.modelclass.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.smarttalk.constants.AppConstant.SharedPreferenceConstant.LOOGED_IN_USER_ID;
import static com.example.smarttalk.constants.AppConstant.SharedPreferenceConstant.MESSAGE_PENDING;

public class GroupDetailsActivity extends AppCompatActivity {
    @BindView(R.id.toolBar)
    Toolbar mtoolbar;
    @BindView(R.id.groupName) TextInputEditText groupName;
    @BindView(R.id.recycler_view_group)
    RecyclerView recyclerViewGroup;
    @BindView(R.id.btn_creategroup)
    MaterialButton createGroup;
    @BindView(R.id.tv_no_of_participants) TextView noOfParticipants;
    @BindView(R.id.group_profileImage) CircleImageView groupProfileImage;
    ProgressDialog progressDialog;
    private static final int PICK_IMAGE = 1;
     String imageUrl=null;
    List<User> object;
    private static final String TAG = "GroupDetailsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_details);
        ButterKnife.bind(this);
        toolBar();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerViewGroup.setLayoutManager(linearLayoutManager);

        DividerItemDecoration itemDecor = new DividerItemDecoration(this, linearLayoutManager.getOrientation());
        recyclerViewGroup.addItemDecoration(itemDecor);

        Intent intent = getIntent();
        Bundle args = intent.getBundleExtra("BUNDLE");
        object = (ArrayList<User>) args.getSerializable("ARRAYLIST");
        Log.d(TAG, "onReceive4589: " + object.size());

        ParticipantsAdapter participantsAdapter = new ParticipantsAdapter(this, object);
        recyclerViewGroup.setAdapter(participantsAdapter);
        noOfParticipants.setText("Participants :" + (object.size()-1));

    }

    public void toolBar() {
        //androidx.appcompat.widget.Toolbar
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp);
        mtoolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void selectImage(View view) {
        //THIS IS FOR  PICK  FORM GALLERY
        Intent gallery = new Intent(Intent.ACTION_PICK);
        gallery.setType("image/*"); // we set type of images
        startActivityForResult(gallery, PICK_IMAGE);
    }

    //THIS METHOD FOR RECEIVE THE IMAGES OF PICKED FROM THE GALLERY
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            String uniqueID = Utils.generateUniqueMessageId();
            uploadingImageUrlToFirebase(uri, uniqueID);

            progressDialog=new ProgressDialog(this);
            progressDialog.setMessage("Uploading Image....");
            progressDialog.setTitle("Please Wait for 2 Sec");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.show();
        }
    }

    public void uploadingImageUrlToFirebase(Uri imageUri, String imageID) {
        if (imageUri != null) {
            StorageReference storage = FirebaseStorage.getInstance().getReference().child("Group_images").child(imageID);
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);

            } catch (IOException e) {
                e.printStackTrace();
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data1 = baos.toByteArray();

            //uploadLoading image Url in firebase.
            storage.putBytes(data1).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    //getImage Url from firebase
                    storage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            imageUrl=uri.toString();
                            Picasso.get()
                                    .load(uri)
                                    .placeholder(R.mipmap.avatar)
                                    .into(groupProfileImage);

                         progressDialog.dismiss();
                        }
                    });
                }
            });

        }
    }

    public void createGroupButton(View view) {
        GroupModel groupModel=new GroupModel();

       DatabaseReference myRef=FirebaseDatabase.getInstance().getReference("Groups");
       String  GroupID=myRef.push().getKey();
        String grpName= groupName.getText().toString();

        groupModel.setGroupID(GroupID);
        groupModel.setGroupName(grpName);
        groupModel.setGroupImage(imageUrl);

        List<User> user2= new ArrayList<>();
        user2=object;
        for(int i=0; i<=object.size()-1;i++){
            user2.get(i).setUserId(object.get(i).getUserId());
            Log.d(TAG, "createGroupButton: ");
            user2.get(i).setFirstname(object.get(i).getFirstname());
            user2.get(i).setLastname(object.get(i).getLastname());
            user2.get(i).setMobilenumber(object.get(i).getMobilenumber());
            user2.get(i).setProfileImageURI(object.get(i).getProfileImageURI());
            user2.get(i).setIsTyping(object.get(i).getIsTyping());
            user2.get(i).setStatus(object.get(i).getStatus());
            user2.get(i).setRegistrationTokenID(object.get(i).getRegistrationTokenID());
            Log.d(TAG, "createGroupButton: "+object.get(i).getUserId());
            groupModel.setMembers(user2);
        }
        assert GroupID != null;
        myRef.child(GroupID).setValue(groupModel);
        finish();
    }
}
