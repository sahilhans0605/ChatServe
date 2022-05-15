package com.sahilhans0605.firebaseusersignup.Fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.sahilhans0605.firebaseusersignup.Activities.AddPostActivity;
import com.sahilhans0605.firebaseusersignup.Activities.EditprofileActivity;
import com.sahilhans0605.firebaseusersignup.Activities.HomeActivityPost;
import com.sahilhans0605.firebaseusersignup.Activities.ImageDisplay;
import com.sahilhans0605.firebaseusersignup.Activities.LoginActivity;
import com.sahilhans0605.firebaseusersignup.Activities.SelfProfile;
import com.sahilhans0605.firebaseusersignup.Activities.UserImageDisplay;
import com.sahilhans0605.firebaseusersignup.Adapters.HomePostAdapter;
import com.sahilhans0605.firebaseusersignup.Adapters.postUserAdapterPublic;
import com.sahilhans0605.firebaseusersignup.R;
import com.sahilhans0605.firebaseusersignup.dataModel.DataModel;
import com.sahilhans0605.firebaseusersignup.dataModel.postDataModel;
import com.sahilhans0605.firebaseusersignup.databinding.ActivityHomePostBinding;
import com.sahilhans0605.firebaseusersignup.databinding.ActivitySelfProfileBinding;
import com.sahilhans0605.firebaseusersignup.databinding.FragmentHomeBinding;
import com.sahilhans0605.firebaseusersignup.databinding.FragmentProfileBinding;
import com.sahilhans0605.firebaseusersignup.databinding.FragmentSearchBinding;

import java.util.ArrayList;
import java.util.HashMap;


public class ProfileFragment extends Fragment {
    FragmentProfileBinding binding;
    FirebaseDatabase db;
    FirebaseUser user;
    postUserAdapterPublic adapter;
    ArrayList<postDataModel> postList;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        binding = FragmentProfileBinding.bind(view);
        db = FirebaseDatabase.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        postList = new ArrayList<>();
        adapter = new postUserAdapterPublic(getContext(), postList);
        Toast.makeText(getContext(), "Your profile :)", Toast.LENGTH_LONG).show();
        binding.logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getContext(), LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });
        binding.EditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), EditprofileActivity.class);
                startActivity(intent);
            }
        });
        LinearLayoutManager layoutManager = new GridLayoutManager(getContext(), 3);
        layoutManager.setOrientation(GridLayoutManager.VERTICAL);
        binding.recyclerViewPostSelf.setLayoutManager(layoutManager);
        binding.recyclerViewPostSelf.setAdapter(adapter);

        DatabaseReference ref = db.getReference().child("Users").child(user.getUid());
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                DataModel model = snapshot.getValue(DataModel.class);
                binding.imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getContext(), UserImageDisplay.class);
                        intent.putExtra("userProfile", model.getPurl());
                        startActivity(intent);
                    }
                });
                binding.Self.setText(model.getName());
                binding.universityNameSelf.setText(model.getUniversityCollege());
                binding.descriptionSelf.setText(model.getSkills());
                if (getActivity() != null) {
                    Glide.with(getContext()).load(model.getPurl()).placeholder(R.drawable.ic_user_image_2).apply(new RequestOptions().override(500, 500)).centerInside().into(binding.imageView);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        peopleCollaboratedWithYou();
        readRecyclerViewPosts();

        return view;

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