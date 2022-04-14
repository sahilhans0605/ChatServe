package com.sahilhans0605.firebaseusersignup.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

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
import com.sahilhans0605.firebaseusersignup.databinding.ActivitySelfProfileBinding;

import java.util.ArrayList;

public class SelfProfile extends AppCompatActivity {
    ActivitySelfProfileBinding binding;
    FirebaseDatabase db;
    FirebaseUser user;
    postUserAdapterPublic adapter;
    ArrayList<postDataModel> postList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySelfProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        db = FirebaseDatabase.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        postList = new ArrayList<>();
        adapter = new postUserAdapterPublic(this, postList);
        binding.logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(SelfProfile.this, LoginActivity.class);
                startActivity(intent);
                finishAffinity();
            }
        });
        binding.EditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SelfProfile.this, EditprofileActivity.class);
                startActivity(intent);
            }
        });
        LinearLayoutManager layoutManager = new GridLayoutManager(this, 3);
        layoutManager.setOrientation(GridLayoutManager.VERTICAL);
        binding.recyclerViewPostSelf.setLayoutManager(layoutManager);
        binding.recyclerViewPostSelf.setAdapter(adapter);
        DatabaseReference ref = db.getReference().child("Users").child(user.getUid());
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                DataModel model = snapshot.getValue(DataModel.class);
                binding.Self.setText(model.getName());
                binding.universityNameSelf.setText(model.getUniversityCollege());
                binding.descriptionSelf.setText(model.getSkills());
                Glide.with(SelfProfile.this).load(model.getPurl()).placeholder(R.drawable.user_image).into(binding.imageView);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        peopleCollaboratedWithYou();
        readRecyclerViewPosts();
    }

    public void peopleCollaboratedWithYou() {
        DatabaseReference refer = db.getReference().child("collab").child(user.getUid()).child("collaboraters");
        refer.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getChildrenCount() != 0) {
                    binding.collaborationsSelf.setText(snapshot.getChildrenCount() + " Collaborations ");

                } else {
                    binding.collaborationsSelf.setText("No Collaborations Yet");

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readRecyclerViewPosts() {
        DatabaseReference reference = db.getReference("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    postDataModel dataModel = snapshot1.getValue(postDataModel.class);
                    if (dataModel.getId().equals(user.getUid())) {
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


}