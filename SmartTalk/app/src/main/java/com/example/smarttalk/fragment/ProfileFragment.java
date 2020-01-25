package com.example.smarttalk.fragment;

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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smarttalk.activity.AuthenticationActivity;
import com.firebase.ui.auth.AuthUI;

import com.bumptech.glide.Glide;
import com.example.smarttalk.R;
import com.example.smarttalk.constants.AppConstant;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

public class ProfileFragment extends Fragment implements View.OnClickListener {
    private CircleImageView Profileimage;
    private Context mcontext;
    private static final int PICK_IMAGE = 1;
    private String userID;
    private StorageReference filepath;
    private Uri imageUri;
    private static final String TAG = "ProfileFragment";
    //https://www.simplifiedcoding.net/firebase-storage-example/

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);


        TextView TName = view.findViewById(R.id.Name);
        TextView TNumber = view.findViewById(R.id.Number);
        Profileimage = view.findViewById(R.id.circularimage);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getCurrentUser().getUid();
        Button button = view.findViewById(R.id.signout);
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

        String url = sharedPreferences.getString(AppConstant.ImageURI.ProfileImageUri, null);
        if (url == null) {
            Profileimage.setImageResource(R.mipmap.avatar);
        } else {

            Glide.with(mcontext.getApplicationContext()).load(url).into(Profileimage);
        }

        button.setOnClickListener(this::onClick);
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
                    Log.d(TAG, "onSuccessuri: " + uri);
                    SharedPreferences sharedPreferences = mcontext.getSharedPreferences(AppConstant.SharedPreferenceConstant.SHARED_PREF_NAME, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(AppConstant.ImageURI.ProfileImageUri, url);
                    editor.apply();
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
            Toast.makeText(mcontext, "Image Sucessfull Uploaded", Toast.LENGTH_LONG).show();
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

    @Override
    public void onClick(View v) {
        AuthUI.getInstance()
                .signOut(mcontext)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(mcontext, "User Signed Out", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(mcontext, AuthenticationActivity.class);
                        mcontext.startActivity(intent);
                    }
                });
    }
}
