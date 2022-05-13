package com.sahilhans0605.firebaseusersignup.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.sahilhans0605.firebaseusersignup.R;
import com.sahilhans0605.firebaseusersignup.databinding.ActivityImageDisplayBinding;

public class UserImageDisplay extends AppCompatActivity {
    String image_url;
    String user_image;
    ActivityImageDisplayBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityImageDisplayBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();
        image_url = getIntent().getStringExtra("userProfile");
        Glide.with(this).load(image_url).placeholder(R.drawable.ic_user_image_2).apply(new RequestOptions().override(1000, 1000)).centerInside().into(binding.imageView2);
//        user_image = getIntent().getStringExtra("userProfile");
//        Glide.with(this).load(user_image).into(binding.imageView2);

    }
}