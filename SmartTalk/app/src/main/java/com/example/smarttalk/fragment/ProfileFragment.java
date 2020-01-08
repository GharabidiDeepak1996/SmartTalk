package com.example.smarttalk.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.smarttalk.R;
import com.example.smarttalk.constants.AppConstant;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.http.Url;

import static android.app.Activity.RESULT_OK;
import static com.firebase.ui.auth.AuthUI.getApplicationContext;

public class ProfileFragment extends Fragment {
    private TextView TName, TNumber;
    CircleImageView Profileimage;
    Context mcontext;
    private static final int PICK_IMAGE = 1;
    private FirebaseAuth mAuth;
    private String userID;
    StorageReference filepath;

    Uri imageUri;
    private static final String TAG = "ProfileFragment";
    //https://www.simplifiedcoding.net/firebase-storage-example/

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        TName = view.findViewById(R.id.Name);
        TNumber = view.findViewById(R.id.Number);
        Profileimage = view.findViewById(R.id.circularimage);
        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getCurrentUser().getUid();

        //getUserInfo();
        mcontext = getActivity();
        SharedPreferences sharedPreferences = mcontext.getSharedPreferences(AppConstant.SharedPreferenceConstant.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        String Name = sharedPreferences.getString(AppConstant.SharedPreferenceConstant.LOGGED_IN_USER_NAME, null);
        String number = sharedPreferences.getString(AppConstant.SharedPreferenceConstant.LOGGED_IN_USER_CONTACT_NUMBER, null);
        TName.setText(Name);
        TNumber.setText(String.valueOf(number));
        Profileimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gallery = new Intent(Intent.ACTION_PICK);
                gallery.setType("image/*"); // we set type of images
                startActivityForResult(gallery, PICK_IMAGE);
            }
        });

        String url = sharedPreferences.getString(AppConstant.ImageURI.ProfileImageUri,null);
        Glide.with(mcontext.getApplicationContext()).load(url).into(Profileimage);

        return view;
    }

    //Result
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            Profileimage.setImageURI(imageUri);

            uploadFile();
            filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {

                @Override
                public void onSuccess(Uri uri) {
                    String url = uri.toString();
                    SharedPreferences sharedPreferences = mcontext.getSharedPreferences(AppConstant.SharedPreferenceConstant.SHARED_PREF_NAME, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(AppConstant.ImageURI.ProfileImageUri, url);
                    editor.apply();
                    Log.d(TAG, "onSuccess: "+url);

                }
            });

        }
    }

    public void uploadFile() {
        if (imageUri != null) {

            filepath = FirebaseStorage.getInstance().getReference().child("Profile_Imamge").child(userID);
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(mcontext.getContentResolver(), imageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data1 = baos.toByteArray();
            UploadTask uploadTask = filepath.putBytes(data1);

            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {


                }
            });
        }
    }
}
