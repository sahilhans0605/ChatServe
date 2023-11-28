package com.sahilhans0605.firebaseusersignup.Fragments;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.facebook.shimmer.ShimmerFrameLayout;
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
import com.sahilhans0605.firebaseusersignup.Adapters.myAdapter;
import com.sahilhans0605.firebaseusersignup.R;
import com.sahilhans0605.firebaseusersignup.dataModel.DataModel;
import com.sahilhans0605.firebaseusersignup.dataModel.postDataModel;
import com.sahilhans0605.firebaseusersignup.databinding.ActivityHomePostBinding;
import com.sahilhans0605.firebaseusersignup.databinding.ActivitySearchBinding;
import com.sahilhans0605.firebaseusersignup.databinding.FragmentHomeBinding;
import com.sahilhans0605.firebaseusersignup.databinding.FragmentSearchBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class SearchFragment extends Fragment {
    FragmentSearchBinding binding;
    FirebaseDatabase db;
    ArrayList<DataModel> data;
    FirebaseAuth auth;
    myAdapter usersLoggedInAdapter;
    FirebaseUser user;
    ProgressDialog dialog;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

//        Intent intent = new Intent(getContext(), SearchActivity.class);
//        startActivity(intent);
        binding = FragmentSearchBinding.bind(view);
        dialog = new ProgressDialog(getContext());
        dialog.setMessage("Fetching data....");
        dialog.setCanceledOnTouchOutside(false);

//        binding.shimmerFrameLayout.startShimmer();
        Toast.makeText(getContext(), "Search for people by their skills...", Toast.LENGTH_LONG).show();
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String s) {
//                this string is the token...each device has it's own different token
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("token", s);
                FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getUid()).updateChildren(hashMap);
//                Toast.makeText(SearchActivity.this, s, Toast.LENGTH_LONG).show();
            }
        });

        db = FirebaseDatabase.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        auth = FirebaseAuth.getInstance();
        data = new ArrayList<>();
        usersLoggedInAdapter = new myAdapter(getContext(), data);
        binding.recyclerView.setAdapter(usersLoggedInAdapter);
//        dialog = new ProgressDialog(this);
//        dialog.setMessage("Fetching users...");


        db = FirebaseDatabase.getInstance();


        readUsers();
        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return true;
            }
        });


        return view;

    }

    private void filter(String newText) {
        List<DataModel> filteredItemsList = new ArrayList<>();
        for (DataModel item : data) {
            if (item.getSkills().toLowerCase().contains(newText.toLowerCase())) {
                filteredItemsList.add(item);
            }
        }
        usersLoggedInAdapter.filterList(filteredItemsList);
    }


    private void readUsers() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                data.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {

                    DataModel dataModel = snapshot1.getValue(DataModel.class);

//                    if (!dataModel.getId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){

                        data.add(dataModel);
//                }
                }

//                binding.shimmerFrameLayout.stopShimmer();
//                binding.shimmerFrameLayout.setVisibility(View.GONE);
                binding.recyclerView.setVisibility(View.VISIBLE);

//                dialog.dismiss();
                usersLoggedInAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
//

}
