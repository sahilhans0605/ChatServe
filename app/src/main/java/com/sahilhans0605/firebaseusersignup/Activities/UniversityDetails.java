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

import java.util.Date;

public class UniversityDetails extends AppCompatActivity {
    UniversityDetailsBinding binding;


    FirebaseUser user;
    Uri browsedImage;
    ProgressDialog dialog;
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

        dialog = new ProgressDialog(this);
        dialog.setMessage("Uploading Data");
        dialog.setCanceledOnTouchOutside(false);

        user = FirebaseAuth.getInstance().getCurrentUser();
        binding.browsebutton.setOnClickListener(new View.OnClickListener() {
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

    private void uploadDataToFirebase() {
        if (binding.universityCollegeInstitute.getText().toString().isEmpty()) {
            binding.universityCollegeInstitute.setError("Field Required*");
        } else if (binding.course.getText().toString().isEmpty()) {
            binding.course.setError("Field Required*");

        } else if (binding.nameUniversityDetails.getText().toString().isEmpty()) {
            binding.nameUniversityDetails.setError("Field Required*");

        } else if (binding.skills.getText().toString().isEmpty()) {
            binding.skills.setError("Field Required*");

        } else {
            String name;
            String course;
            String universityCollege;
            String skills;
            String id;
            id = user.getUid();
            name = binding.nameUniversityDetails.getText().toString();
            universityCollege = binding.universityCollegeInstitute.getText().toString();
            course = binding.course.getText().toString();
            skills = binding.skills.getText().toString();
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
                                        FirebaseDatabase db = FirebaseDatabase.getInstance();
                                        DatabaseReference dbRef = db.getReference().child("Users").child(id);
                                        DataModel data = new DataModel(id, universityCollege, name, skills, course, uri.toString());
                                        data.setToken(s);

                                        dbRef.setValue(data);
                                        Toast.makeText(UniversityDetails.this, "Data Added", Toast.LENGTH_LONG).show();
                                        Intent intent = new Intent(UniversityDetails.this, BottomNavigationBarActivity.class);
                                        startActivity(intent);
                                        finishAffinity();
                                    }
                                });


                            }
                        });
                        binding.universityCollegeInstitute.setText("");
                        binding.course.setText("");
                        binding.nameUniversityDetails.setText("");
                        binding.skills.setText("");
                        binding.userImage.setImageResource(R.drawable.user);
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {

                        float percent = 100 * (snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                        dialog.setMessage("Uploaded " + (int) percent + " % ");

                    }
                });

            } else {
                dialog.dismiss();
                FirebaseDatabase db = FirebaseDatabase.getInstance();
                DatabaseReference dbRef = db.getReference().child("Users").child(id);
                DataModel data = new DataModel(id, universityCollege, name, skills, course, "No image");
                dbRef.setValue(data);
                Toast.makeText(UniversityDetails.this, "Data Added", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(UniversityDetails.this, BottomNavigationBarActivity.class);
                startActivity(intent);
                finishAffinity();

            }
            binding.universityCollegeInstitute.setText("");
            binding.course.setText("");
            binding.nameUniversityDetails.setText("");
            binding.skills.setText("");
            binding.userImage.setImageResource(R.drawable.user);
        }
    }


    public void signUp(View view) {
        uploadDataToFirebase();

    }
}