package com.sahilhans0605.firebaseusersignup.Fragments;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.sahilhans0605.firebaseusersignup.Activities.AddPostActivity;
import com.sahilhans0605.firebaseusersignup.Activities.HomeActivityPost;
import com.sahilhans0605.firebaseusersignup.Adapters.HomePostAdapter;
import com.sahilhans0605.firebaseusersignup.R;
import com.sahilhans0605.firebaseusersignup.dataModel.postDataModel;
import com.sahilhans0605.firebaseusersignup.databinding.ActivityAddPostBinding;
import com.sahilhans0605.firebaseusersignup.databinding.ActivityHomePostBinding;
import com.sahilhans0605.firebaseusersignup.databinding.FragmentAddPostBinding;
import com.sahilhans0605.firebaseusersignup.databinding.FragmentHomeBinding;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;


public class AddPostFragment extends Fragment {
    FragmentAddPostBinding binding;
    Uri browsedImage;
    ProgressDialog dialog;
    FirebaseUser user;
    ActivityResultLauncher<String> getImage = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
        @Override
        public void onActivityResult(Uri result) {
            binding.postImageSelectedFragment.setImageURI(result);
            browsedImage = result;
        }
    });


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_post, container, false);
//        Intent intent = new Intent(getContext(), AddPostActivity.class);
//        startActivity(intent);
        binding = FragmentAddPostBinding.bind(view);
        dialog = new ProgressDialog(getContext());
        dialog.setMessage("Uploading Data");
        Toast.makeText(getContext(), "Add new post here..", Toast.LENGTH_SHORT).show();
        user = FirebaseAuth.getInstance().getCurrentUser();
        binding.postImageSelectedFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dexter.withContext(getContext()).withPermission(Manifest.permission.READ_EXTERNAL_STORAGE).withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        getImage.launch("image/*");
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
//                        if user denies the permission and if user presses browse button again then permission dialog will appear again
                    }
                }).check();

            }
        });
        binding.postButtonFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadDataToFirebase();
            }
        });



        return view;

    }

    private void uploadDataToFirebase() {
        if (binding.postDescriptionFragment.getText().toString().isEmpty()) {
            binding.postDescriptionFragment.setError("Add Something");

        } else if (browsedImage == null) {

            Toast.makeText(getContext(), "Add an image for the post!", Toast.LENGTH_LONG).show();
        } else {

            String postDescription;
            String id;
            id = user.getUid();
            postDescription = binding.postDescriptionFragment.getText().toString();
            dialog.show();

            Date date = new Date();
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference ref = storage.getReference("UserPosts " + date.getTime());
            ref.putFile(browsedImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            dialog.dismiss();
                            FirebaseDatabase db = FirebaseDatabase.getInstance();
                            DatabaseReference dbRef = db.getReference().child("Posts");
                            String postId = dbRef.push().getKey();
                            postDataModel data = new postDataModel(postDescription, uri.toString(), id, postId);
                            dbRef.child(postId).setValue(data);
                            Toast.makeText(getContext(), "Post Added", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(getContext(), HomeActivityPost.class);
                            startActivity(intent);
                        }
                    });
                    binding.postDescriptionFragment.setText("");
                    binding.postImageSelectedFragment.setImageResource(R.drawable.newpost);
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {

                    float percent = 100 * (snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                    dialog.setMessage("Uploaded " + (int) percent + " % ");

                }
            });

        }

    }



}
