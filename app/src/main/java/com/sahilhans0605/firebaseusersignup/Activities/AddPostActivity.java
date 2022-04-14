package com.sahilhans0605.firebaseusersignup.Activities;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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
import com.sahilhans0605.firebaseusersignup.R;
import com.sahilhans0605.firebaseusersignup.dataModel.postDataModel;
import com.sahilhans0605.firebaseusersignup.databinding.ActivityAddPostBinding;

import java.util.Date;

public class AddPostActivity extends AppCompatActivity {
    ActivityAddPostBinding binding;
    Uri browsedImage;
    ProgressDialog dialog;
    FirebaseUser user;
    ActivityResultLauncher<String> getImage = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
        @Override
        public void onActivityResult(Uri result) {
            binding.postImageSelected.setImageURI(result);
            browsedImage = result;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddPostBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        dialog = new ProgressDialog(this);
        dialog.setMessage("Uploading Data");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        user = FirebaseAuth.getInstance().getCurrentUser();
        binding.postImageSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dexter.withContext(AddPostActivity.this).withPermission(Manifest.permission.READ_EXTERNAL_STORAGE).withListener(new PermissionListener() {
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
        binding.postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadDataToFirebase();
            }
        });

    }

    private void uploadDataToFirebase() {
        if (binding.postDescription.getText().toString().isEmpty()) {
            binding.postDescription.setError("Add Something");

        } else if (browsedImage == null) {

            Toast.makeText(this, "Add an image for the post!", Toast.LENGTH_LONG).show();
        } else {

            String postDescription;
            String id;
            id = user.getUid();
            postDescription = binding.postDescription.getText().toString();
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
                            Toast.makeText(AddPostActivity.this, "Post Added", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(AddPostActivity.this, BottomNavigationBarActivity.class);
                            startActivity(intent);
                        }
                    });
                    binding.postDescription.setText("");
                    binding.postImageSelected.setImageResource(R.drawable.newpost);
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

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}