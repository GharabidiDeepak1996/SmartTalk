package com.example.smarttalk.fragment;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smarttalk.activity.AuthenticationActivity;
import com.example.smarttalk.modelclass.User;
import com.firebase.ui.auth.AuthUI;

import com.bumptech.glide.Glide;
import com.example.smarttalk.R;
import com.example.smarttalk.constants.AppConstant;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
import java.util.Arrays;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;
import static com.example.smarttalk.constants.AppConstant.SharedPreferenceConstant.LOOGED_IN_USER_ID;
import static com.example.smarttalk.constants.NetworkConstants.PICK_IMAGE;
import static com.example.smarttalk.constants.NetworkConstants.REQUEST_CAMERA;
import static com.firebase.ui.auth.AuthUI.getApplicationContext;

public class ProfileFragment extends Fragment implements View.OnClickListener {
    private CircleImageView Profileimage;
    private Context mcontext;
    private String userID;
    private StorageReference filepath;
    private FirebaseAuth mAuth;
    private Uri imageUri;
    public static final String THIS_BROADCAST_FOR_PROFILE_IMAGE = "this is for profile image";
    ProgressDialog progressDialog;
    private static final String TAG = "ProfileFragment";

    //https://www.simplifiedcoding.net/firebase-storage-example/

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);


        TextView TName = view.findViewById(R.id.Name);
        TextView TNumber = view.findViewById(R.id.Number);
        Profileimage = view.findViewById(R.id.circularimage);
         mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getCurrentUser().getPhoneNumber();

        Button button = view.findViewById(R.id.signout);
        //getUserInfo();
        mcontext = getActivity();
        assert mcontext != null;
        //getting image url form sharedPreference
        SharedPreferences sharedPreferences = mcontext.getSharedPreferences(AppConstant.SharedPreferenceConstant.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        String Name = sharedPreferences.getString(AppConstant.SharedPreferenceConstant.LOGGED_IN_USER_NAME, null);
        String number = sharedPreferences.getString(AppConstant.SharedPreferenceConstant.LOGGED_IN_USER_CONTACT_NUMBER, null);
        TName.setText(Name);
        TNumber.setText(String.valueOf(number));

        Profileimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final CharSequence[] items = {"Take Photo", "Choose from Library", "Remove Photo"};

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Profile Photo");
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        if (items[item].equals("Take Photo")) {
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(intent, REQUEST_CAMERA);

                        } else if (items[item].equals("Choose from Library")) {
                            //THIS IS FOR  PICK  FORM GALLERY
                            Intent gallery = new Intent(Intent.ACTION_PICK);
                            gallery.setType("image/*"); // we set type of images
                            startActivityForResult(gallery, PICK_IMAGE);
                              } else if (items[item].equals("Remove Photo")) {

                               Glide.with(mcontext)
                                       .load(R.mipmap.avatar)
                                      .into(Profileimage);

                               //null image view set
                            SharedPreferences sharedPreferences = mcontext.getSharedPreferences(AppConstant.SharedPreferenceConstant.SHARED_PREF_NAME, Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString(AppConstant.ImageURI.ProfileImageUri, "");
                            editor.apply();

                           dialog.dismiss(); //dismiss the dialog when an option is selected
                        }
                    }
                });
                builder.show(); //finally, show this dialog upon button click
            }
        });
        String url = sharedPreferences.getString(AppConstant.ImageURI.ProfileImageUri, null);

        //Send the broadcast to homeActivity
        Intent intent=new Intent(THIS_BROADCAST_FOR_PROFILE_IMAGE);
        intent.putExtra("profileImage",url);
        mcontext.sendBroadcast(intent);
//fetch image url
        Glide.with(mcontext)
                .load(url)
                .placeholder(R.mipmap.avatar)
                .into(Profileimage);
        button.setOnClickListener(this);
        return view;
    }

    //THIS METHOD FOR RECEIVE THE IMAGES OF PICKED FROM THE GALLERY
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null && data.getData()!=null) {
           // mcontext.getContentResolver().delete(data.getData(), null, null);

            imageUri = data.getData();
            progressDialog=new ProgressDialog(getActivity());
            progressDialog.setMessage("Sending Image....");
            progressDialog.setTitle("Please Wait for 2 Sec");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.show();

           // Profileimage.setImageURI(imageUri);
            uploadFile();
            //fetching the uploaded image for the firebase
            filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    String url = uri.toString();

                    Glide.with(mcontext)
                            .load(url)
                            .placeholder(R.mipmap.avatar)
                            .into(Profileimage);
                    progressDialog.dismiss();

                    //put image url into SharedPreference
                    SharedPreferences sharedPreferences = mcontext.getSharedPreferences(AppConstant.SharedPreferenceConstant.SHARED_PREF_NAME, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(AppConstant.ImageURI.ProfileImageUri, url);
                    editor.apply();
                    //insert in to sharedperf
                    String base64id=sharedPreferences.getString(LOOGED_IN_USER_ID,null);
                    FirebaseDatabase database= FirebaseDatabase.getInstance();
                    DatabaseReference myRef =database.getReference("User").child(base64id.concat("=="));

                    HashMap<String,Object> hashMap=new HashMap<>();
                    hashMap.put("profileImageURI",url);
                    myRef.updateChildren(hashMap);
                }
            });

        }
    }

    private void uploadFile() {


        if (imageUri != null) {
            filepath = FirebaseStorage.getInstance().getReference().child("Profile_Image").child(userID);
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(mcontext.getContentResolver(), imageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

            byte[] data1 = baos.toByteArray();
            //upload in firebase.
            UploadTask uploadTask = filepath.putBytes(data1);
            //Log.d(TAG, "uploadFile: "+uploadTask.getSnapshot().getUploadSessionUri());
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(mcontext, "Image Unsucessfull Uploaded", Toast.LENGTH_LONG).show();

                }
            });
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(mcontext, "Image Sucessfull Uploaded", Toast.LENGTH_LONG).show();

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
