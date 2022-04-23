package com.sahilhans0605.firebaseusersignup.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

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
        Toast.makeText(this, name+"'s"+" Profile :)", Toast.LENGTH_LONG).show();

//        if (id.equals(firebaseUser.getUid().toString())) {
//            binding.collabButton.setText("Edit Profile");
//        }
        Glide.with(PublicProfile.this).load(profileImage).placeholder(R.drawable.user_image).into(binding.imageView);
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


}