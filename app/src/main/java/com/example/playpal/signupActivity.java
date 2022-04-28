package com.example.playpal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;

import static android.view.View.VISIBLE;

public class signupActivity extends AppCompatActivity implements View.OnClickListener{
    private FirebaseAuth mAuth;
    private EditText nameS;
    private EditText emailS;
    private EditText passwordS;
    private LottieAnimationView SignupBTN;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_signup);

        mAuth = FirebaseAuth.getInstance();

        TextView banner = findViewById(R.id.banner);
        banner.setOnClickListener(this);

        nameS = findViewById(R.id.name);
        emailS = findViewById(R.id.email);
        passwordS = findViewById(R.id.password);

        Button backsignup = findViewById(R.id.buttonSignup);
        backsignup.setOnClickListener(this);

        TextView login = findViewById(R.id.txtlogin);

//        Button signupButton = findViewById(R.id.sign);
        SignupBTN = findViewById(R.id.signupBTN);
        SignupBTN.setOnClickListener(this);


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),Login.class);
                startActivity(intent);
            }
        });

        backsignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),Login.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.signupBTN:
                registerUser();

                break;
        }
    }

    private void registerUser() {
        String strname = nameS.getText().toString().trim();
        String stremail = emailS.getText().toString().trim();
        String strpassword = passwordS.getText().toString().trim();

        if (strname.isEmpty()){
            nameS.setError("Full name is required");
            nameS.requestFocus();
            return;
        }

        if (stremail.isEmpty()){
            emailS.setError("Email Id is required");
            emailS.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(stremail).matches()){
            emailS.setError("Please provide valid Email Id!");
            emailS.requestFocus();
            return;
        }

        if (strpassword.isEmpty()){
            passwordS.setError("Password is required");
            passwordS.requestFocus();
            return;
        }

        if (strpassword.length() < 6){
            passwordS.setError("Minimum length should 6 characters!");
            passwordS.requestFocus();
            return;
        }

        mAuth.createUserWithEmailAndPassword(stremail,strpassword)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<AuthResult> task) {

                        if (task.isSuccessful()){
                            User user = new User(strname,stremail,"0");

                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull @NotNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        Toast.makeText(signupActivity.this,"User has been registered successfully!",Toast.LENGTH_SHORT).show();
                                        SignupBTN.playAnimation();
                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {

                                                SignupBTN.cancelAnimation();
                                                startActivity(new Intent(signupActivity.this,HomeScreen.class));
                                            }
                                        },4000);

                                        //Redirect to login layout
                                    }
                                    else {
                                        Toast.makeText(signupActivity.this,"Failed to register! Try again",Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                        else {
                            Toast.makeText(signupActivity.this,"Failed to register! Try again",Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }
}