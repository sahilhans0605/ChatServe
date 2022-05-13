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

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
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
import com.sahilhans0605.firebaseusersignup.Fragments.ProfileFragment;
import com.sahilhans0605.firebaseusersignup.R;
import com.sahilhans0605.firebaseusersignup.dataModel.DataModel;
import com.sahilhans0605.firebaseusersignup.databinding.ActivityEditprofileBinding;

import java.util.Date;
import java.util.HashMap;

public class EditprofileActivity extends AppCompatActivity {
    ActivityEditprofileBinding binding;
    FirebaseDatabase db;
    FirebaseUser user;
    String updatedName;
    String updatedUniversity;
    String updatedCourse;
    String updatedSkills;
    Uri browsedImage;
    FirebaseStorage storage;
    ActivityResultLauncher<String> getImage = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
        @Override
        public void onActivityResult(Uri result) {
            binding.userprofileEdit.setImageURI(result);
            browsedImage = result;
        }
    });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditprofileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        db = FirebaseDatabase.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        storage = FirebaseStorage.getInstance();
        binding.userprofileEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dexter.withContext(EditprofileActivity.this).withPermission(Manifest.permission.READ_EXTERNAL_STORAGE).withListener(new PermissionListener() {
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

        DatabaseReference ref = db.getReference().child("Users").child(user.getUid());
        binding.UpdateprofileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updatedSkills = binding.SkillsEdit.getText().toString();
                updatedName = binding.nameEdit.getText().toString();
                updatedCourse = binding.CoursenameEdit.getText().toString();
                updatedUniversity = binding.UniversitynameEdit.getText().toString();
                updateProfile(updatedName, updatedSkills, updatedUniversity, updatedCourse);
                Toast.makeText(EditprofileActivity.this, "Your profile will be updated in few moments...", Toast.LENGTH_LONG).show();

            }
        });

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                DataModel model = snapshot.getValue(DataModel.class);
                binding.nameEdit.setText(model.getName());
                binding.UniversitynameEdit.setText(model.getUniversityCollege());
                binding.SkillsEdit.setText(model.getSkills());
                binding.CoursenameEdit.setText(model.getCourse());

                Glide.with(getApplicationContext()).load(model.getPurl()).placeholder(R.drawable.ic_user_image_2).into(binding.userprofileEdit);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void updateProfile(String updatedName, String updatedSkills, String updatedUniversity, String updatedCourse) {
        DatabaseReference ref = db.getReference().child("Users").child(user.getUid());


        Date date = new Date();
        StorageReference StorageRef = storage.getReference("UserProfilesUpdated " + date.getTime());
        if (browsedImage != null) {
            StorageRef.putFile(browsedImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    StorageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("universityCollege", updatedUniversity);
                            hashMap.put("skills", updatedSkills);
                            hashMap.put("name", updatedName);
                            hashMap.put("course", updatedCourse);
                            hashMap.put("purl", uri.toString());
                            ref.updateChildren(hashMap);
                        }
                    });

                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {

                    float percent = 100 * (snapshot.getBytesTransferred() / snapshot.getTotalByteCount());

                }
            });

        } else {
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("universityCollege", updatedUniversity);
            hashMap.put("skills", updatedSkills);
            hashMap.put("name", updatedName);
            hashMap.put("course", updatedCourse);
            ref.updateChildren(hashMap);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}