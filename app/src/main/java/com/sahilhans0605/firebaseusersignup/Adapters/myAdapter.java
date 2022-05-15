package com.sahilhans0605.firebaseusersignup.Adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sahilhans0605.firebaseusersignup.Activities.ChatActivity;
import com.sahilhans0605.firebaseusersignup.Activities.PublicProfile;
import com.sahilhans0605.firebaseusersignup.R;
import com.sahilhans0605.firebaseusersignup.dataModel.DataModel;
import com.sahilhans0605.firebaseusersignup.databinding.SampleRowBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class myAdapter extends RecyclerView.Adapter<myAdapter.myViewHolder> {
    Context context;
    List<DataModel> data;
    FirebaseUser firebaseUser;

    public myAdapter(Context context, ArrayList<DataModel> data) {
        this.context = context;
        this.data = data;
    }


    public void filterList(List<DataModel> filteredList) {
        data = filteredList;
        notifyDataSetChanged();


    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.sample_row, parent, false);

        return new myViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull myViewHolder holder, int position) {
        DataModel dataModel = data.get(position);
        holder.binding.universityNameSampleRow.setText(dataModel.getUniversityCollege());
        holder.binding.nameSampleRow.setText(dataModel.getName());
        holder.binding.descriptionSampleRow.setText(dataModel.getSkills());
//        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
//        holder.binding.buttonCollab.setVisibility(View.VISIBLE);
//        if (dataModel.getId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
//            holder.binding.buttonChat.setVisibility(View.GONE);
//        }
        Glide.with(context).load(dataModel.getPurl()).placeholder(R.drawable.ic_user_image_2).into(holder.binding.userImage);
//        isFollowing(dataModel.getId(), holder.binding.buttonCollab);

//        holder.binding.buttonCollab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (holder.binding.buttonCollab.getText().toString().equals("collab")) {
//                    FirebaseDatabase.getInstance().getReference().child("collab").child(firebaseUser.getUid()).
//                            child("collaborating").child(dataModel.getId()).setValue(true);
//
//                    FirebaseDatabase.getInstance().getReference().child("collab").child(dataModel.getId()).
//                            child("collaboraters").child(firebaseUser.getUid()).setValue(true);

//                    sendMessageNotification(dataModel.getName(), dataModel.getName(), dataModel.getToken());

//                } else {
//                    FirebaseDatabase.getInstance().getReference().child("collab").child(firebaseUser.getUid()).
//                            child("collaborating").child(dataModel.getId()).removeValue();
//
//                    FirebaseDatabase.getInstance().getReference().child("collab").child(dataModel.getId()).
//                            child("collaboraters").child(firebaseUser.getUid()).removeValue();
//
////                    sendMessageNotification(dataModel.getName(), dataModel.getName() , dataModel.getToken());
//
//                }


//            }
//
//        });
        holder.binding.chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("name", dataModel.getName());
                intent.putExtra("uid", dataModel.getId());
                intent.putExtra("token", dataModel.getToken());
                context.startActivity(intent);

            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(context, PublicProfile.class);
                intent1.putExtra("name", dataModel.getName());
                intent1.putExtra("university", dataModel.getUniversityCollege());
                intent1.putExtra("skills", dataModel.getSkills());
                intent1.putExtra("profileImage", dataModel.getPurl());
                intent1.putExtra("id", dataModel.getId());

                context.startActivity(intent1);
            }
        });

    }

    @Override
    public int getItemCount() {
        return data.size();
    }



    public class myViewHolder extends RecyclerView.ViewHolder {

        SampleRowBinding binding;
        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = SampleRowBinding.bind(itemView);

        }
    }

//    private void isFollowing(String id, Button btn) {
//
//        FirebaseDatabase db = FirebaseDatabase.getInstance();
//        DatabaseReference ref = db.getReference();
//        ref.child("collab").child(firebaseUser.getUid()).child("collaborating").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//
//                if (snapshot.child(id).exists()) {
//                    btn.setText("collaborating");
//                } else {
//                    btn.setText("collab");
//
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//    }

    void sendMessageNotification(String name, String message, String token) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = "https://fcm.googleapis.com/fcm/send";
        JSONObject data = new JSONObject();
        try {
            data.put("title", name);
            data.put("body", message);

            JSONObject notificationData = new JSONObject();
            notificationData.put("notification", data);
            notificationData.put("to", token);
            JsonObjectRequest request = new JsonObjectRequest(url, notificationData, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
//                    Toast.makeText(ChatActivity.this, "Success", Toast.LENGTH_LONG).show();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(context, error.getMessage()+"", Toast.LENGTH_LONG).show();

                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> map = new HashMap<>();
//                    writing Key= is important
                    String key = "Key=AAAAs5k_AYw:APA91bGa3fkCX7MYRrGs0FMG8A4H5h9XPlzkokSKjXZrjwVtgPVCSbO8A1JQ3yIPotzPvGEg-fQEkT2iMoH0hPyhYGMtlWQPpBtUBKHBaTWhBsZYxYHqO2zzNBmbYyZVMW9XhXsDQoT2";
                    map.put("Authorization", key);
                    map.put("Content-Type", "application/json");

                    return map;
                }
            };
            queue.add(request);
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

}
