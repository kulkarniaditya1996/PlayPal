package com.example.playpal;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.content.PackageManagerCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.icu.text.SimpleDateFormat;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.security.acl.Owner;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class OtherUserProfile extends AppCompatActivity {




    TextView name,activity_count,contact;

    private DatabaseReference UsersRef;

    private String currentUserName;

    CircleImageView imageView;
    StorageReference storageReference;


    User owner;
    public boolean isOnline() {
        ConnectivityManager conMgr = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMgr.getActiveNetworkInfo();

        if(netInfo == null || !netInfo.isConnected() || !netInfo.isAvailable()){
            Toast.makeText(OtherUserProfile.this, "No Internet connection!", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (isOnline()==false) {      OtherUserProfile.this.startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS)); }
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_other_user_profile);
        activity_count=findViewById(R.id.activity_count);
        MaterialToolbar toolbar = findViewById(R.id.topAppBarP);
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layoutP);
        NavigationView navigationView = findViewById(R.id.navigation_viewP);
        Button logout = findViewById(R.id.logout);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
                int id = item.getItemId();
                drawerLayout.closeDrawer(GravityCompat.START);
                switch (id){
                    case R.id.home:
                        Toast.makeText(OtherUserProfile.this,"Home is Clicked",Toast.LENGTH_LONG);
                        Intent intentHome = new Intent(OtherUserProfile.this, HomeScreen.class);
                        startActivity(intentHome);
                        break;
                    case R.id.CreateActivity:
                        Toast.makeText(OtherUserProfile.this,"Create Activity is Clicked",Toast.LENGTH_LONG);
                        Intent intentCreate = new Intent(OtherUserProfile.this, CreateActivity.class);
                        startActivity(intentCreate);
                        break;

                    case R.id.profile:
                        Intent intent = new Intent(OtherUserProfile.this, UserProfile.class);
                        startActivity(intent);
                }
                return true;
            }
        });


        name = findViewById(R.id.name);
        imageView = findViewById(R.id.image);
        contact=findViewById(R.id.contact);
        owner = (User) getIntent().getSerializableExtra("Owner");
        storageReference = FirebaseStorage.getInstance().getReference();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");


        contact.setText("Contact "+owner.getFullName());
        StorageReference profileRef = storageReference.child(owner.getuID() +".jpg");
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(imageView);
            }
        });

        GetUserInfo();


    }

    public void showDialogContact(View view){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Contact");
        alert.setMessage("Email id : "+owner.getEmail());
        alert.create().show();
    }

    private void GetUserInfo()
    {
        UsersRef.child(owner.getuID()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                currentUserName = snapshot.child("fullName").getValue().toString();
                name.setText(currentUserName);
                if(snapshot.hasChild("count")){
                    String count = snapshot.child("count").getValue().toString();
                    activity_count.setText(count);
                }else{
                    activity_count.setText("0");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}