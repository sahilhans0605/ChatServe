package com.sahilhans0605.firebaseusersignup.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sahilhans0605.firebaseusersignup.Adapters.postUserAdapterPublic;
import com.sahilhans0605.firebaseusersignup.R;
import com.sahilhans0605.firebaseusersignup.dataModel.DataModel;
import com.sahilhans0605.firebaseusersignup.dataModel.postDataModel;
import com.sahilhans0605.firebaseusersignup.databinding.ActivityPublicProfileBinding;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;

public class PublicProfile extends AppCompatActivity {
    ActivityPublicProfileBinding binding;

    FirebaseUser firebaseUser;
    String id;
    String name;
    String university;
    String skills;
    String profileImage;
    postUserAdapterPublic adapter;
    ArrayList<postDataModel> postList;
    FirebaseDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPublicProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        postList = new ArrayList<>();
        ActionBar customActionBar = getSupportActionBar();
        customActionBar.setDisplayShowCustomEnabled(true);
        customActionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        customActionBar.setCustomView(R.layout.custom_action_bar_public);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        adapter = new postUserAdapterPublic(this, postList);
        LinearLayoutManager layoutManager = new GridLayoutManager(this, 3);
        layoutManager.setOrientation(GridLayoutManager.VERTICAL);
        binding.recyclerViewPost.setAdapter(adapter);
        binding.recyclerViewPost.setLayoutManager(layoutManager);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseDatabase.getInstance();
        DatabaseReference ref = db.getReference();
        id = getIntent().getStringExtra("id");
        name = getIntent().getStringExtra("name");
        university = getIntent().getStringExtra("university");
        skills = getIntent().getStringExtra("skills");
        profileImage = getIntent().getStringExtra("profileImage");
        binding.name.setText(name);
        binding.universityNamePublicprofile.setText(university);
        binding.description.setText(skills);

        db = FirebaseDatabase.getInstance();

        DatabaseReference dbRef = db.getReference("Users").child(id);
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                DataModel data = snapshot.getValue(DataModel.class);
                binding.name.setText(data.getName());
                binding.universityNamePublicprofile.setText(data.getUniversityCollege());
                binding.description.setText(data.getSkills());
                Glide.with(PublicProfile.this).load(data.getPurl()).into(binding.imageView);

//                yha pe humne for loop nhi lgaya kyoki...saara data fetch nhi krwana sirf particular userid ka hi data fetch krwana h....i.e recycler view mein saara data nhi laana
                binding.imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(PublicProfile.this, UserImageDisplay.class);
                        intent.putExtra("userProfile", data.getPurl());
                        startActivity(intent);
                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


//        if (id.equals(firebaseUser.getUid().toString())) {
//            binding.collabButton.setText("Edit Profile");
//        }
        Glide.with(PublicProfile.this).load(profileImage).placeholder(R.drawable.ic_user_image_2).into(binding.imageView);
        binding.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PublicProfile.this, UserImageDisplay.class);
                intent.putExtra("userProfile", profileImage);
                startActivity(intent);
            }
        });

        ref.child("collab").child(firebaseUser.getUid()).child("collaborating").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.child(id).exists()) {
                    binding.collabButton.setText("collaborating");
                } else {
                    binding.collabButton.setText("collab");

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        binding.collabButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                if (binding.collabButton.getText().toString().equals("collab")) {
                    FirebaseDatabase.getInstance().getReference().child("collab").child(firebaseUser.getUid()).
                            child("collaborating").child(id).setValue(true);

                    FirebaseDatabase.getInstance().getReference().child("collab").child(id).
                            child("collaboraters").child(firebaseUser.getUid()).setValue(true);

                } else {
                    FirebaseDatabase.getInstance().getReference().child("collab").child(firebaseUser.getUid()).
                            child("collaborating").child(id).removeValue();

                    FirebaseDatabase.getInstance().getReference().child("collab").child(id).
                            child("collaboraters").child(firebaseUser.getUid()).removeValue();
                }
            }
        });



        binding.chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                db = FirebaseDatabase.getInstance();

                DatabaseReference dbRef = db.getReference("Users").child(id);
                dbRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        DataModel data = snapshot.getValue(DataModel.class);

//                yha pe humne for loop nhi lgaya kyoki...saara data fetch nhi krwana sirf particular userid ka hi data fetch krwana h....i.e recycler view mein saara data nhi laana
                        Intent intent = new Intent(PublicProfile.this, ChatActivity.class);
                        intent.putExtra("name", data.getName());
                        intent.putExtra("uid", id);
                        intent.putExtra("Token", data.getToken());
                        startActivity(intent);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });




            }
        });




        peopleCollaboratedWithYou();
        readRecyclerViewPosts();
    }


    private void readRecyclerViewPosts() {
        DatabaseReference reference = db.getReference("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    postDataModel dataModel = snapshot1.getValue(postDataModel.class);
                    if (dataModel.getId().equals(id)) {
                        postList.add(dataModel);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void peopleCollaboratedWithYou() {
        DatabaseReference refer = db.getReference().child("collab").child(id).child("collaboraters");
        refer.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getChildrenCount() != 0) {
                    binding.collaborations.setText(snapshot.getChildrenCount() + " Collaborations ");

                } else {
                    binding.collaborations.setText("No Collaborations Yet");

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

}