package com.sahilhans0605.firebaseusersignup.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.sahilhans0605.firebaseusersignup.Activities.HomeActivityPost;
import com.sahilhans0605.firebaseusersignup.R;
import com.sahilhans0605.firebaseusersignup.dataModel.postDataModel;
import com.sahilhans0605.firebaseusersignup.databinding.ActivityPostDescriptionBinding;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class postDescription extends AppCompatActivity {
    ActivityPostDescriptionBinding binding;
    Uri browsedImage;
    ProgressDialog dialog;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPostDescriptionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ActionBar customActionBar = getSupportActionBar();
        customActionBar.setDisplayShowCustomEnabled(true);
        customActionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        customActionBar.setCustomView(R.layout.custom_action_bar_search);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        dialog = new ProgressDialog(postDescription.this);
        dialog.setMessage("Uploading Data");
        dialog.setCanceledOnTouchOutside(false);
        user = FirebaseAuth.getInstance().getCurrentUser();
        browsedImage = getIntent().getParcelableExtra("postImage");
        binding.postButtonNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadDataToFirebase();
            }
        });
    }

    private void uploadDataToFirebase() {
        if (binding.postDescriptionFragment.getText().toString().isEmpty()) {
            binding.postDescriptionFragment.setError("Add Something");

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
                            Calendar calDate = Calendar.getInstance();
                            SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
                            SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm");
                            String postDate = currentDate.format(calDate.getTime()) + " •" + currentTime.format(calDate.getTime());
                            String postId = dbRef.push().getKey();
                            postDataModel data = new postDataModel(postDescription, uri.toString(), id, postId,postDate);
                            dbRef.child(postId).setValue(data);
                            Toast.makeText(com.sahilhans0605.firebaseusersignup.Activities.postDescription.this, "Post Added", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(com.sahilhans0605.firebaseusersignup.Activities.postDescription.this, HomeActivityPost.class);
                            startActivity(intent);
                        }
                    });
                    binding.postDescriptionFragment.setText("");
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