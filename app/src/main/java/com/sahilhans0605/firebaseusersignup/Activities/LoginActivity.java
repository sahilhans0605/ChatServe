package com.sahilhans0605.firebaseusersignup.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
import com.sahilhans0605.firebaseusersignup.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {
    ActivityLoginBinding binding;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    ProgressDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        dialog = new ProgressDialog(this);
        dialog.setMessage("Please wait while we are signing you in");

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
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
                        Intent intent = new Intent(LoginActivity.this, BottomNavigationBarActivity.class);
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