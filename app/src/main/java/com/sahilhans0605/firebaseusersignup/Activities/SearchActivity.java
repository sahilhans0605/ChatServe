package com.sahilhans0605.firebaseusersignup.Activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.sahilhans0605.firebaseusersignup.R;
import com.sahilhans0605.firebaseusersignup.dataModel.DataModel;
import com.sahilhans0605.firebaseusersignup.Adapters.myAdapter;
import com.sahilhans0605.firebaseusersignup.databinding.ActivitySearchBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class SearchActivity extends AppCompatActivity {
    ActivitySearchBinding binding;
    FirebaseDatabase db;
    ArrayList<DataModel> data;
    FirebaseAuth auth;
    myAdapter usersLoggedInAdapter;
    FirebaseUser user;
    ValueEventListener valueEventListener;
    ProgressDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ActionBar customActionBar = getSupportActionBar();
        customActionBar.setDisplayShowCustomEnabled(true);
        customActionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        customActionBar.setCustomView(R.layout.custom_action_bar_search);
        dialog = new ProgressDialog(this);
        dialog.setMessage("Fetching data....");
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        binding.recyclerView.hasFixedSize();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Toast.makeText(this, "Search for people by their skills...", Toast.LENGTH_LONG).show();
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
        usersLoggedInAdapter = new myAdapter(SearchActivity.this, data);
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

                    if (!dataModel.getId().equals(FirebaseAuth.getInstance().getUid())) {
                        data.add(dataModel);

                    }


                }
                dialog.dismiss();
                usersLoggedInAdapter.notifyDataSetChanged();
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
