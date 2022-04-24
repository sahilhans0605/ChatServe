package com.sahilhans0605.firebaseusersignup.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.google.android.material.navigation.NavigationBarView;
import com.sahilhans0605.firebaseusersignup.Fragments.AddPostFragment;
import com.sahilhans0605.firebaseusersignup.Fragments.FollowersFragment;
import com.sahilhans0605.firebaseusersignup.Fragments.HomeFragment;
import com.sahilhans0605.firebaseusersignup.Fragments.ProfileFragment;
import com.sahilhans0605.firebaseusersignup.Fragments.SearchFragment;
import com.sahilhans0605.firebaseusersignup.R;
import com.sahilhans0605.firebaseusersignup.databinding.ActivityHomePostBinding;

public class HomeActivityPost extends AppCompatActivity {
    Fragment selectedFragment;
    ActivityHomePostBinding binding;
    private final int ID_HOME = 1;
    private final int ID_ADD = 2;
    private final int ID_SEARCH = 3;
    private final int ID_SETTINGS = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomePostBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

//        FragmentTransaction homeTransaction = getSupportFragmentManager().beginTransaction();
//        homeTransaction.replace(R.id.frameContainer, new HomeFragment());
//        homeTransaction.commit();

        binding.bottomNavigationBar.add(new MeowBottomNavigation.Model(ID_HOME, R.drawable.ic_action_home));
        binding.bottomNavigationBar.add(new MeowBottomNavigation.Model(ID_ADD, R.drawable.ic_add));
        binding.bottomNavigationBar.add(new MeowBottomNavigation.Model(ID_SEARCH, R.drawable.ic_baseline_search_24));
        binding.bottomNavigationBar.add(new MeowBottomNavigation.Model(ID_SETTINGS, R.drawable.ic_settings));
        binding.bottomNavigationBar.setOnShowListener(new MeowBottomNavigation.ShowListener() {
            @Override
            public void onShowItem(MeowBottomNavigation.Model item) {
//                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                Fragment fragment = null;
                switch (item.getId()) {

                    case ID_HOME:
                        fragment = new HomeFragment();
                        break;

                    case ID_ADD:
//                        Intent intent1 = new Intent(HomeActivityPost.this, AddPostActivity.class);
//                        startActivity(intent1);
////                        finish();
                        fragment = new AddPostFragment();
                        break;

                    case ID_SEARCH:
                        fragment = new SearchFragment();
                        break;
                    case ID_SETTINGS:
//                        Intent intent2 = new Intent(HomeActivityPost.this, SelfProfile.class);
//                        startActivity(intent2);
                        fragment = new ProfileFragment();

                        break;

                }
                loadFragment(fragment);

            }
        });
        binding.bottomNavigationBar.show(ID_HOME,true);
        binding.bottomNavigationBar.setOnClickMenuListener(new MeowBottomNavigation.ClickListener() {
            @Override
            public void onClickItem(MeowBottomNavigation.Model item) {

            }
        });

        binding.bottomNavigationBar.setOnReselectListener(new MeowBottomNavigation.ReselectListener() {
            @Override
            public void onReselectItem(MeowBottomNavigation.Model item) {
                Toast.makeText(HomeActivityPost.this, "Already selected...", Toast.LENGTH_SHORT).show();
            }
        });
//        binding.bottomNavigationBar.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
//            @Override
//            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//
//                switch (item.getItemId()) {
//
//                    case R.id.homeFragment:
//                        transaction.replace(R.id.frameContainer, new HomeFragment());
//                        break;
//
//                    case R.id.addPostFragment:
//                        Intent intent1 = new Intent(HomeActivityPost.this, AddPostActivity.class);
//                        startActivity(intent1);
////                        finish();
////                        transaction.replace(R.id.frameContainer, new AddPostFragment());
//                        break;
//
//                    case R.id.searchFragment:
//                        Intent intent = new Intent(HomeActivityPost.this, SearchActivity.class);
//                        startActivity(intent);
////                        finish();
//
//
////                        transaction.replace(R.id.frameContainer, new SearchFragment());
//                        break;
//                    case R.id.settingsFragment:
//                        Intent intent2 = new Intent(HomeActivityPost.this, SelfProfile.class);
//                        startActivity(intent2);
//                        break;
//
//                }
//                transaction.commit();
//
//
//                return true;
//            }
//        });


    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.frameContainer, fragment).commit();
    }
}