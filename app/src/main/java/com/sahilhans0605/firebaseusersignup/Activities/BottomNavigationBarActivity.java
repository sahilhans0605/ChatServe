package com.sahilhans0605.firebaseusersignup.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.navigation.NavigationBarView;
import com.sahilhans0605.firebaseusersignup.Fragments.FollowersFragment;
import com.sahilhans0605.firebaseusersignup.Fragments.HomeFragment;
import com.sahilhans0605.firebaseusersignup.Fragments.ProfileFragment;
import com.sahilhans0605.firebaseusersignup.R;
import com.sahilhans0605.firebaseusersignup.databinding.BottomNavigationBarBinding;

public class BottomNavigationBarActivity extends AppCompatActivity {
    BottomNavigationBarBinding binding;
    Fragment selectedFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = BottomNavigationBarBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        FragmentTransaction homeTransaction = getSupportFragmentManager().beginTransaction();
        homeTransaction.replace(R.id.frameContainer, new HomeFragment());
        homeTransaction.commit();

        binding.bottomNavigationBar.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

                switch (item.getItemId()) {

                    case R.id.homeFragment:
                        transaction.replace(R.id.frameContainer, new HomeFragment());
                        break;

                    case R.id.addPostFragment:
                        Intent intent1 = new Intent(BottomNavigationBarActivity.this, AddPostActivity.class);
                        startActivity(intent1);
//                        finish();
//                        transaction.replace(R.id.frameContainer, new AddPostFragment());
                        break;

                    case R.id.searchFragment:
                        Intent intent = new Intent(BottomNavigationBarActivity.this, SearchActivity.class);
                        startActivity(intent);
//                        finish();


//                        transaction.replace(R.id.frameContainer, new SearchFragment());
                        break;
                    case R.id.settingsFragment:
                        Intent intent2 = new Intent(BottomNavigationBarActivity.this, SelfProfile.class);
                        startActivity(intent2);
                        break;

                }
                transaction.commit();


                return true;
            }
        });


    }
}