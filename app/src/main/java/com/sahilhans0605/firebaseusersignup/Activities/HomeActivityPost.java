package com.sahilhans0605.firebaseusersignup.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sahilhans0605.firebaseusersignup.Adapters.HomePostAdapter;
import com.sahilhans0605.firebaseusersignup.R;
import com.sahilhans0605.firebaseusersignup.dataModel.DataModel;
import com.sahilhans0605.firebaseusersignup.dataModel.postDataModel;
import com.sahilhans0605.firebaseusersignup.databinding.ActivityHomePostBinding;

import java.util.ArrayList;

public class HomeActivityPost extends AppCompatActivity {
    ActivityHomePostBinding binding;
    ArrayList<postDataModel> postlist;
    ArrayList<String> followingList;
    HomePostAdapter adapterHomePost;
    FirebaseDatabase database;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        binding = ActivityHomePostBinding.inflate(getLayoutInflater());
//        setContentView(binding.getRoot());
//        postlist = new ArrayList<postDataModel>();
//        database = FirebaseDatabase.getInstance();
//        user = FirebaseAuth.getInstance().getCurrentUser();
////        Log.i("user waali",user.getUid());
////        Log.i("non user waali",FirebaseAuth.getInstance().getUid());
//
//        adapterHomePost = new HomePostAdapter(postlist, HomeActivityPost.this);
//        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
//        binding.recyclerView.setAdapter(adapterHomePost);
//        getFollowingListOfCurrentUsers();
    }

    private void getFollowingListOfCurrentUsers() {
        followingList = new ArrayList<>();
        DatabaseReference ref = database.getReference("collab").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        ref.child("collaborating").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                followingList.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    followingList.add(snapshot1.getKey());
                }
                readPosts();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readPosts() {
        DatabaseReference ref = database.getReference("Posts");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postlist.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    postDataModel dataModel = snapshot1.getValue(postDataModel.class);
                    for (String id : followingList) {
                        if (dataModel.getId().equals(id)) {
                            postlist.add(dataModel);
                        }
                    }
                    adapterHomePost.notifyDataSetChanged();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


}