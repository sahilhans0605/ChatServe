package com.sahilhans0605.firebaseusersignup.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.sahilhans0605.firebaseusersignup.Activities.ImageDisplay;
import com.sahilhans0605.firebaseusersignup.R;
import com.sahilhans0605.firebaseusersignup.dataModel.postDataModel;
import com.sahilhans0605.firebaseusersignup.databinding.PostProfileUserAndPublicBinding;

import java.util.ArrayList;

public class postUserAdapterPublic extends RecyclerView.Adapter<postUserAdapterPublic.postPublicViewHolder> {
    Context context;
    ArrayList<postDataModel> postList;

    public postUserAdapterPublic(Context context, ArrayList<postDataModel> postList) {
        this.context = context;
        this.postList = postList;
    }

    @NonNull
    @Override
    public postPublicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.post_profile_user_and_public, parent, false);
        return new postPublicViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull postPublicViewHolder holder, int position) {
        postDataModel PostdataModel = postList.get(position);
        Glide.with(context).load(PostdataModel.getPurl()).apply(new RequestOptions().override(500, 500)).centerInside().into(holder.binding.postPublicImage);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ImageDisplay.class);
                intent.putExtra("postImage", PostdataModel.getPurl());
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public class postPublicViewHolder extends RecyclerView.ViewHolder {
        PostProfileUserAndPublicBinding binding;

        public postPublicViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = PostProfileUserAndPublicBinding.bind(itemView);
        }
    }
}
