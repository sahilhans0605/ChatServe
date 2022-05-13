package com.sahilhans0605.firebaseusersignup.Fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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
import com.sahilhans0605.firebaseusersignup.Activities.HomeActivityPost;
import com.sahilhans0605.firebaseusersignup.Activities.SearchActivity;
import com.sahilhans0605.firebaseusersignup.Adapters.HomePostAdapter;
import com.sahilhans0605.firebaseusersignup.R;
import com.sahilhans0605.firebaseusersignup.dataModel.postDataModel;
import com.sahilhans0605.firebaseusersignup.databinding.ActivityHomePostBinding;
import com.sahilhans0605.firebaseusersignup.databinding.FragmentHomeBinding;

import java.util.ArrayList;
import java.util.HashMap;


public class HomeFragment extends Fragment {
    FragmentHomeBinding binding;
    ArrayList<postDataModel> postlist;
    ArrayList<String> followingList;
    HomePostAdapter adapterHomePost;
    FirebaseDatabase database;
    FirebaseUser user;
    ProgressDialog dialog;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        binding = FragmentHomeBinding.bind(view);
        binding.shimmerFrameLayout.startShimmer();
        postlist = new ArrayList<postDataModel>();
        dialog = new ProgressDialog(getContext());
        dialog.setMessage("Fetching data....");
        dialog.setCanceledOnTouchOutside(false);
        database = FirebaseDatabase.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        adapterHomePost = new HomePostAdapter(postlist, getContext());
        binding.recyclerViewHome.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerViewHome.setAdapter(adapterHomePost);
        getFollowingListOfCurrentUsers();
        return view;

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
//                        Intent intent = new Intent(getContext(), SearchActivity.class);

                    }

//                    dialog.dismiss();

                }

                if (postlist.isEmpty()) {
                    ref.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot snapshot1 : snapshot.getChildren()){
                                postDataModel dataModel = snapshot1.getValue(postDataModel.class);
                                postlist.add(dataModel);
                            }
                            binding.shimmerFrameLayout.stopShimmer();
                            binding.shimmerFrameLayout.hideShimmer();

                            binding.shimmerFrameLayout.setVisibility(View.GONE);
                            binding.recyclerViewHome.setVisibility(View.VISIBLE);

                            adapterHomePost.notifyDataSetChanged();

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }else{
                    binding.shimmerFrameLayout.stopShimmer();
                    binding.shimmerFrameLayout.setVisibility(View.GONE);
                    binding.recyclerViewHome.setVisibility(View.VISIBLE);
                    adapterHomePost.notifyDataSetChanged();
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


}
