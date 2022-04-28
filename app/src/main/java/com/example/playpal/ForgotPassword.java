package com.example.playpal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import org.jetbrains.annotations.NotNull;

public class ForgotPassword extends AppCompatActivity {
    private EditText emailF;
    private Button resetbtn;
    private Button backbtn1;

    FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_forgot_password);

        emailF = findViewById(R.id.emailF);
        resetbtn = findViewById(R.id.buttonF);
        backbtn1 = findViewById(R.id.buttonSignup1);

        auth = FirebaseAuth.getInstance();

        resetbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetPassword();
            }
        });

        backbtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ForgotPassword.this, Login.class));
            }
        });
    }



    private void resetPassword(){
        String resetEmail = emailF.getText().toString().trim();

        if (resetEmail.isEmpty()){
            emailF.setError("Email Id is required");
            emailF.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(resetEmail).matches()){
            emailF.setError("Please provide valid Email Id!");
            emailF.requestFocus();
            return;
        }

        auth.sendPasswordResetEmail(resetEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(ForgotPassword.this,"Check your email to reset your password",Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(ForgotPassword.this,"Try again!",Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}