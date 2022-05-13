package com.sahilhans0605.firebaseusersignup.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sahilhans0605.firebaseusersignup.R;
import com.sahilhans0605.firebaseusersignup.dataModel.DataModel;
import com.sahilhans0605.firebaseusersignup.databinding.ActivityLoginBinding;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {
    ActivityLoginBinding binding;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    ProgressDialog dialog;
    ProgressDialog dialog2;
    FirebaseUser user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar customActionBar = getSupportActionBar();
        customActionBar.setDisplayShowCustomEnabled(true);
        customActionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        customActionBar.setCustomView(R.layout.custom_action_bar);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        dialog2 = new ProgressDialog(this);
        dialog2.setTitle("Initializing");
        dialog2.setMessage("Logging you in automatically...");
        dialog2.setCanceledOnTouchOutside(false);

        user = FirebaseAuth.getInstance().getCurrentUser();
        dialog = new ProgressDialog(this);
        dialog.setCanceledOnTouchOutside(false);
        getWindow().setStatusBarColor(ContextCompat.getColor(LoginActivity.this, R.color.statusBar));
        dialog.setMessage("Please wait while we are signing you in");
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        if (user != null) {
            Intent intent1 = new Intent(LoginActivity.this, HomeActivityPost.class);
            startActivity(intent1);
            finishAffinity();
        }


        binding.goToRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                finishAffinity();
            }
        });
        binding.signinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                authenticationCheck();
            }
        });
    }

    private void authenticationCheck() {
        String email = binding.emailLogin.getText().toString();
        String password = binding.password.getText().toString();
        FirebaseAuth auth = FirebaseAuth.getInstance();

        if (!email.matches(emailPattern)) {
            binding.emailLogin.setError("Enter Correct Email");

        } else if (password.isEmpty() || password.length() < 6) {
            binding.password.setError("Add a strong Password");


        } else {
            dialog.show();

            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Intent intent = new Intent(LoginActivity.this, HomeActivityPost.class);
                        intent.putExtra("Email", email);
                        intent.putExtra("Uid", auth.getCurrentUser().getUid());
                        startActivity(intent);
                        finishAffinity();
                        Toast.makeText(LoginActivity.this, "Successfully Logged In", Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                        binding.emailLogin.setText("");
                        binding.password.setText("");
                    } else {
                        Toast.makeText(LoginActivity.this, "Invalid credentials", Toast.LENGTH_LONG).show();
                        Log.i("Info", task.getException() + "");
                        dialog.dismiss();

                    }
                }
            });
        }
    }
}