package com.example.playpal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import android.app.ActionBar;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.provider.Settings;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
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

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.concurrent.Executor;

import static android.view.View.VISIBLE;
import static androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG;
import static androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL;

public class Login extends AppCompatActivity implements View.OnClickListener{


    private static final int REQUEST_CODE = 10101;
    private TextView signup,FPass;
    private EditText emailL, passwordL;
    private Button login;
    private ImageView imageView;
    private Executor executor;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;
    SharedPreferences sharedPreferences;
//    private LottieAnimationView lottieAnimationViewL;
    private LottieAnimationView LoginBTN;

    String TAG = "Login";

    private FirebaseAuth mAuth;
    FirebaseUser mUser;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

//        lottieAnimationViewL = findViewById(R.id.lottieL);

        LoginBTN = findViewById(R.id.loginBTN);

        imageView = findViewById(R.id.imageView);

        FPass = findViewById(R.id.Fpassword);
        FPass.setOnClickListener(this);

        signup = findViewById(R.id.signup);
        signup.setOnClickListener(this);

//        login = findViewById(R.id.button);


        emailL = findViewById(R.id.emailL);
        passwordL = findViewById(R.id.passwordL);


        LoginBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String stremail = emailL.getText().toString().trim();
                String strpassword = passwordL.getText().toString().trim();

                if (stremail.isEmpty()){
                    emailL.setError("Email Id is required");
                    emailL.requestFocus();
                    return;
                }

                if (!Patterns.EMAIL_ADDRESS.matcher(stremail).matches()){
                    emailL.setError("Please provide valid Email Id!");
                    emailL.requestFocus();
                    return;
                }

                if (strpassword.isEmpty()){
                    passwordL.setError("Password is required");
                    passwordL.requestFocus();
                    return;
                }

                if (strpassword.length() < 6){
                    passwordL.setError("Minimum length should 6 characters!");
                    passwordL.requestFocus();
                    return;
                }

                userLogin(stremail,strpassword);
            }
        });

        sharedPreferences = getSharedPreferences("data",MODE_PRIVATE);
        boolean isLogin = sharedPreferences.getBoolean("isLogin",false);
        if (isLogin){

            imageView.setVisibility(View.VISIBLE);
        }

        BiometricManager biometricManager = BiometricManager.from(this);
        switch (biometricManager.canAuthenticate(BIOMETRIC_STRONG | DEVICE_CREDENTIAL)) {
            case BiometricManager.BIOMETRIC_SUCCESS:
                break;
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                Log.e("MY_APP_TAG", "No biometric features available on this device.");
                break;
            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                Log.e("MY_APP_TAG", "Biometric features are currently unavailable.");
                break;
            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                // Prompts the user to create credentials that your app accepts.
                final Intent enrollIntent = new Intent(Settings.ACTION_BIOMETRIC_ENROLL);
                enrollIntent.putExtra(Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                        BIOMETRIC_STRONG | DEVICE_CREDENTIAL);
                startActivityForResult(enrollIntent, REQUEST_CODE);
                break;
        }
        executor = ContextCompat.getMainExecutor(this);
        biometricPrompt = new BiometricPrompt(Login.this,
                executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode,
                                              @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Toast.makeText(getApplicationContext(),
                        "Authentication error: " + errString, Toast.LENGTH_SHORT)
                        .show();
            }

            @Override
            public void onAuthenticationSucceeded(
                    @NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                String emailT = sharedPreferences.getString("email","");
                String passwordT = sharedPreferences.getString("password","");

                userLogin(emailT, passwordT);
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(getApplicationContext(), "Authentication failed",
                        Toast.LENGTH_SHORT)
                        .show();
            }
        });

        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Biometric login for my app")
                .setSubtitle("Log in using your biometric credential")
                .setNegativeButtonText("Use account password")
                .build();

        // Prompt appears when user clicks "Log in".
        // Consider integrating with the keystore to unlock cryptographic operations,
        // if needed by your app.

        imageView.setOnClickListener(view -> {
            biometricPrompt.authenticate(promptInfo);
        });


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.signup:
                startActivity(new Intent(this,signupActivity.class));
                break;

//            case R.id.button:
//                userLogin(email,password);
//                break;

            case R.id.Fpassword:
                startActivity(new Intent(this,ForgotPassword.class));
        }
    }

    private void userLogin(String stremail, String strpassword) {
        mAuth.signInWithEmailAndPassword(stremail,strpassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    //Redirect to user profile

                    FirebaseUser fUser = mAuth.getCurrentUser();
                    User user = new User(fUser.getUid(),fUser.getDisplayName(),fUser.getEmail());
                    //System.out.println("DISPLAY NAME: "+ fUser.);
                    SharedPreferences.Editor editor = getSharedPreferences("data",MODE_PRIVATE).edit();
                    editor.putString("email",stremail);
                    editor.putString("password",strpassword);
                    editor.putBoolean("isLogin",true);
                    editor.apply();
//                    lottieAnimationViewL.setVisibility(VISIBLE);
                    LoginBTN.playAnimation();

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {

//                            lottieAnimationViewL.cancelAnimation();
                            Intent intent = new Intent(Login.this, HomeScreen.class);
                            intent.putExtra("User", user);
                            startActivity(intent);
                        }
                    },4000);


                }
                else {
                    Toast.makeText(Login.this,"User not registered. Sign Up!",Toast.LENGTH_SHORT).show();
                }

            }
        });

    }
}