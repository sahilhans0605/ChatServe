package com.sahilhans0605.firebaseusersignup.Activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.sahilhans0605.firebaseusersignup.Adapters.MessagesAdapter;
import com.sahilhans0605.firebaseusersignup.dataModel.DataModel;
import com.sahilhans0605.firebaseusersignup.dataModel.Messages;
import com.sahilhans0605.firebaseusersignup.databinding.ActivityChatBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {
    ActivityChatBinding binding;
    MessagesAdapter adapter;
    ArrayList<Messages> messagesList;
    String senderRoom, receiverRoom;
    FirebaseDatabase database;
    Uri browsedImage;
    ProgressDialog dialog;
    FirebaseUser user;
    FirebaseStorage storage;
    String senderUid;
    String receiverUid;
    String name;
    String token;
    String receiverName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        database = FirebaseDatabase.getInstance();
        messagesList = new ArrayList<>();
        adapter = new MessagesAdapter(this, messagesList);
        dialog = new ProgressDialog(this);
        dialog.setMessage("Sending Image...");
        dialog.setCanceledOnTouchOutside(false);
        storage = FirebaseStorage.getInstance();


        ActivityResultLauncher<String> getImage = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
                if (result != null) {
                    dialog.show();
                    Calendar calendar = Calendar.getInstance();
                    StorageReference ref = storage.getReference().child("chats").child(String.valueOf(calendar.getTimeInMillis()));
                    ref.putFile(result).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if (task.isSuccessful()) {
                                ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        dialog.dismiss();
                                        Date date = new Date();
                                        String messageTxt = binding.messageBoxEditText.getText().toString();
                                        Messages messages = new Messages(messageTxt, senderUid, date.getTime());
                                        messages.setPurl(uri.toString());
                                        messages.setMessage("Image");
                                        binding.messageBoxEditText.setText("");
                                        database.getReference().child("chats").child(senderRoom).child("messages").push().setValue(messages).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                database.getReference().child("chats").child(receiverRoom).child("messages").push().setValue(messages).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {

                                                    }
                                                });
                                            }
                                        });

                                    }
                                });
                            }
                        }
                    });

                }
            }
        });
        name = getIntent().getStringExtra("name");
        receiverUid = getIntent().getStringExtra("uid");
        token = getIntent().getStringExtra("token");

//        Toast.makeText(ChatActivity.this, token + "", Toast.LENGTH_LONG).show();
        getSupportActionBar().setTitle(name);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        binding.recyclerViewChat.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        binding.recyclerViewChat.setLayoutManager(layoutManager);

//        toh jo current user login hua va hoga uski uid aajayegi
        senderUid = FirebaseAuth.getInstance().getUid();
        senderRoom = senderUid + receiverUid;
        receiverRoom = receiverUid + senderUid;
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference dbRef = db.getReference();
        dbRef.child("Users").child(senderUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                DataModel data = snapshot.getValue(DataModel.class);
                receiverName = data.getName() + "_" + data.getSkills() + "_" + data.getUniversityCollege();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        database.getReference().child("chats").child(senderRoom).child("messages").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messagesList.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {

                    Messages messages = snapshot1.getValue(Messages.class);

                    messagesList.add(messages);

                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        binding.sendBtnIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Date date = new Date();
                String messageTxt = binding.messageBoxEditText.getText().toString();
                if (!messageTxt.isEmpty()) {
                    Messages messages = new Messages(messageTxt, senderUid, date.getTime());
                    binding.messageBoxEditText.setText("");
                    database.getReference().child("chats").child(senderRoom).child("messages").push().setValue(messages).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            database.getReference().child("chats").child(receiverRoom).child("messages").push().setValue(messages).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    sendMessageNotification(receiverName, messageTxt, token);
                                }
                            });
                        }
                    });
                }
            }

        });


        binding.attachmentIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dexter.withContext(ChatActivity.this).withPermission(Manifest.permission.READ_EXTERNAL_STORAGE).withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        getImage.launch("image/*");
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
//                        if user denies the permission and if user presses browse button again then permission dialog will appear again
                    }
                }).check();

            }
        });


    }


    void sendMessageNotification(String name, String message, String token) {
        RequestQueue queue = Volley.newRequestQueue(this);
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
//                    Toast.makeText(ChatActivity.this, error.getLocalizedMessage()+"", Toast.LENGTH_LONG).show();

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

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }


}