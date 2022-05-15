package com.sahilhans0605.firebaseusersignup.Activities;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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
import com.sahilhans0605.firebaseusersignup.R;
import com.sahilhans0605.firebaseusersignup.dataModel.DataModel;
import com.sahilhans0605.firebaseusersignup.databinding.UniversityDetailsBinding;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.Date;

public class UniversityDetails extends AppCompatActivity {
    UniversityDetailsBinding binding;

    Uri browsedImage;
    ProgressDialog dialog;
    ProgressDialog dialog1;
    FirebaseAuth auth;
    DatabaseReference dbRef;

    ActivityResultLauncher<String> getImage = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
        @Override
        public void onActivityResult(Uri result) {
            binding.userImage.setImageURI(result);
            browsedImage = result;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = UniversityDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        auth = FirebaseAuth.getInstance();
        dialog1 = new ProgressDialog(this);
        dialog = new ProgressDialog(this);
        dialog1.setMessage("Processing...");
        dialog1.setCanceledOnTouchOutside(false);
        dialog.setMessage("Uploading Data ");
        dialog.setCanceledOnTouchOutside(false);
        ActionBar customActionBar = getSupportActionBar();
        customActionBar.setDisplayShowCustomEnabled(true);
        customActionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        customActionBar.setCustomView(R.layout.custom_action_bar);

        binding.userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dexter.withContext(UniversityDetails.this).withPermission(Manifest.permission.READ_EXTERNAL_STORAGE).withListener(new PermissionListener() {
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


    }


    public void signUp(View view) {

        String name = binding.nameUniversityDetails.getText().toString();
        String universityCollege = binding.universityCollegeInstitute.getText().toString();
        String course = binding.course.getText().toString();
        String skills = binding.skills.getText().toString();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(universityCollege) || TextUtils.isEmpty(course) || TextUtils.isEmpty(skills)) {
            Toast.makeText(this, "All fields are required.", Toast.LENGTH_SHORT).show();
        } else {
            String email = getIntent().getStringExtra("EMAIL");
            String password = getIntent().getStringExtra("PASSWORD");

            RegisterUser(name, universityCollege, course, skills, email, password);
            dialog1.show();
        }


    }

    private void RegisterUser(String name, String universityCollege, String course, String skills, String email, String password) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(UniversityDetails.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser firebaseUser = auth.getCurrentUser();
                    String userid = firebaseUser.getUid();
                    dialog1.dismiss();
                    dialog.show();
                    Date date = new Date();
                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    StorageReference ref = storage.getReference("UserProfiles " + date.getTime());
                    if (browsedImage != null) {
                        ref.putFile(browsedImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        dialog.dismiss();
                                        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(new OnSuccessListener<String>() {
                                            @Override
                                            public void onSuccess(String s) {
                                                dbRef = FirebaseDatabase.getInstance().getReference().child("Users").child(userid);
                                                DataModel data = new DataModel(userid, universityCollege, name, skills, course, uri.toString());
                                                data.setToken(s);
                                                dbRef.setValue(data);
                                                Toast.makeText(UniversityDetails.this, "Profile Updated...", Toast.LENGTH_LONG).show();
                                                Intent intent = new Intent(UniversityDetails.this, HomeActivityPost.class);
                                                startActivity(intent);
                                                finishAffinity();
                                            }
                                        });

                                    }
                                });
//                                binding.universityCollegeInstitute.setText("");
//                                binding.course.setText("");
//                                binding.nameUniversityDetails.setText("");
//                                binding.skills.setText("");
//                                binding.userImage.setImageResource(R.drawable.ic_user_image_2);
                            }
                        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {

                                float percent = 100 * (snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                                dialog.setMessage("Uploaded " + (int) percent + " % ");

                            }
                        });

                    } else {
                        dialog1.dismiss();
                        dialog.show();
                        binding.userImage.setImageResource(R.drawable.ic_user_image_2);
                        FirebaseUser fUser = auth.getCurrentUser();
                        String uid = fUser.getUid();
                        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
                        DataModel data = new DataModel(uid, universityCollege, name, skills, course, "No image");
                        dbRef.setValue(data);
                        Toast.makeText(UniversityDetails.this, "Profile Updated...", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(UniversityDetails.this, HomeActivityPost.class);
                        startActivity(intent);
                        finishAffinity();

                    }
//                    binding.universityCollegeInstitute.setText("");
//                    binding.course.setText("");
//                    binding.nameUniversityDetails.setText("");
//                    binding.skills.setText("");
//                    binding.userImage.setImageResource(R.drawable.ic_user_image_2);
                } else {
                    dialog1.dismiss();
                    Toast.makeText(UniversityDetails.this, "" + "User with this email already exists", Toast.LENGTH_LONG).show();
                    Log.i("Info", task.getException() + "");

                }
            }
        });


    }
}