package com.sahilhans0605.firebaseusersignup.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sahilhans0605.firebaseusersignup.Activities.ChatActivity;
import com.sahilhans0605.firebaseusersignup.R;
import com.sahilhans0605.firebaseusersignup.dataModel.DataModel;
import com.sahilhans0605.firebaseusersignup.dataModel.postDataModel;
import com.sahilhans0605.firebaseusersignup.databinding.SamplePostBinding;

import java.util.ArrayList;

public class HomePostAdapter extends RecyclerView.Adapter<HomePostAdapter.myViewHolderPost> {

    ArrayList<postDataModel> postData;
    Context context;
    FirebaseDatabase db;


    public HomePostAdapter(ArrayList<postDataModel> postData, Context context) {
        this.postData = postData;
        this.context = context;
    }

    @NonNull
    @Override
    public myViewHolderPost onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.sample_post, parent, false);
        return new myViewHolderPost(view);
    }

    @Override
    public void onBindViewHolder(@NonNull myViewHolderPost holder, int position) {
        db = FirebaseDatabase.getInstance();
        postDataModel PostdataModel = postData.get(position);
        holder.binding.postDescriptionpostActivity.setText(PostdataModel.getDescription());
        Glide.with(context).load(PostdataModel.getPurl()).into(holder.binding.postimage);
        publisherInfo(holder.userImage, holder.username, holder.UniversityName, PostdataModel.getId());
        noOfLikes(PostdataModel.getPostId(), holder.binding.noOfLikes);
        isLiked(PostdataModel.getPostId(),holder.binding.likeButton);
        holder.binding.likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.binding.likeButton.getTag().equals("unliked")) {
                    db.getReference("Likes").child(PostdataModel.getPostId()).child(FirebaseAuth.getInstance().getUid()).setValue(true);
                    holder.binding.likeButton.setTag("liked");
                    holder.binding.likeButton.setImageResource(R.drawable.filledlike);

                } else {
                    db.getReference("Likes").child(PostdataModel.getPostId()).child(FirebaseAuth.getInstance().getUid()).removeValue();
                    holder.binding.likeButton.setTag("unliked");
                    holder.binding.likeButton.setImageResource(R.drawable.emptylike);

                }
            }
        });


    }

    private void isLiked(String postId, ImageView likeButton) {
        FirebaseDatabase.getInstance().getReference().child("Likes").child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(FirebaseAuth.getInstance().getUid()).exists()){
                    likeButton.setTag("liked");
                    likeButton.setImageResource(R.drawable.filledlike);
                }else{
                    likeButton.setTag("unliked");
                    likeButton.setImageResource(R.drawable.emptylike);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return postData.size();
    }


    public class myViewHolderPost extends RecyclerView.ViewHolder {

        SamplePostBinding binding;

        ImageView userImage;
        TextView username, UniversityName;


        public myViewHolderPost(@NonNull View itemView) {
            super(itemView);
            binding = SamplePostBinding.bind(itemView);

            userImage = itemView.findViewById(R.id.userImagePost);
            username = itemView.findViewById(R.id.usernamePost);
            UniversityName = itemView.findViewById(R.id.universityNamePost);


        }
    }

    public void publisherInfo(ImageView profileImage, TextView username, TextView universityName, String userId) {
        db = FirebaseDatabase.getInstance();

        DatabaseReference dbRef = db.getReference("Users").child(userId);
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                DataModel data = snapshot.getValue(DataModel.class);
                username.setText(data.getName());
                universityName.setText(data.getUniversityCollege());
                Glide.with(context).load(data.getPurl()).into(profileImage);

//                yha pe humne for loop nhi lgaya kyoki...saara data fetch nhi krwana sirf particular userid ka hi data fetch krwana h....i.e recycler view mein saara data nhi laana


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void noOfLikes(String postId, TextView textView) {
        db.getReference("Likes").child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getChildrenCount() == 0) {
                    textView.setText("0 likes");
                } else {
                    textView.setText(snapshot.getChildrenCount() + " Likes");

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
