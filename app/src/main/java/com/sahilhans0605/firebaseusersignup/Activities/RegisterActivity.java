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
import com.google.firebase.auth.FirebaseUser;
import com.sahilhans0605.firebaseusersignup.databinding.ActivityRegisterBinding;

public class RegisterActivity extends AppCompatActivity {
    ActivityRegisterBinding binding;
    FirebaseUser user;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    ProgressDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityRegisterBinding.inflate(getLayoutInflater());


        setContentView(binding.getRoot());
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            Intent intent = new Intent(RegisterActivity.this, BottomNavigationBarActivity.class);
            startActivity(intent);
            finishAffinity();
        }


        dialog = new ProgressDialog(this);
        dialog.setMessage("Registering you...");
        binding.goToSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });
//to hide status bar from the app where time and network is shown in mobile phone
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        binding.signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                authenticationCheck();
            }
        });
    }

    private void authenticationCheck() {
        String email = binding.email.getText().toString();
        String password = binding.password.getText().toString();
        FirebaseAuth auth = FirebaseAuth.getInstance();

        if (!email.matches(emailPattern)) {
            binding.email.setError("Enter Correct Email");

        } else if (password.isEmpty() || password.length() < 6) {
            binding.password.setError("Add a strong Password");


        } else {
            dialog.show();
            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(RegisterActivity.this, "Successfully Registered", Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                        binding.email.setText("");
                        binding.password.setText("");
                        startActivity(new Intent(RegisterActivity.this, UniversityDetails.class));
                        finishAffinity();

                    } else {
                        Toast.makeText(RegisterActivity.this, "" + task.getException(), Toast.LENGTH_LONG).show();
                        Log.i("Info", task.getException() + "");
                        dialog.dismiss();

                    }
                }
            });
        }
    }
}