package com.example.playpal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class JoinActivity extends AppCompatActivity {
    TextView sp,loc,dt,ti;
    Button join,chat,otherUser,backJ;
    User user;
    PlayActivity playActivity;
    DatabaseReference databaseReference;
    private FirebaseAuth fAuth;
    private String currentUserID,currentUserName,currentUserEmail,count;
    private DatabaseReference mAuth,UsersRef;
    User owner;
    List<User> list;
    public boolean isOnline() {
        ConnectivityManager conMgr = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMgr.getActiveNetworkInfo();

        if(netInfo == null || !netInfo.isConnected() || !netInfo.isAvailable()){
            Toast.makeText(JoinActivity.this, "No Internet connection!", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (isOnline()==false) {      JoinActivity.this.startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS)); }
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_join);
        sp = findViewById(R.id.sport);
        loc = findViewById(R.id.location);
        dt = findViewById(R.id.date);
        ti = findViewById(R.id.time);
        join = findViewById(R.id.join);
        chat = findViewById(R.id.chat);
        otherUser = findViewById(R.id.other_user);
        backJ = findViewById(R.id.backJoin);
        chat.setEnabled(false);
        chat.setVisibility(View.INVISIBLE);
        databaseReference = FirebaseDatabase.getInstance().getReference(PlayActivity.class.getSimpleName());
        count = "0";


        Intent intent = getIntent();

        backJ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(JoinActivity.this,HomeScreen.class));
            }
        });


        fAuth = FirebaseAuth.getInstance();
        currentUserID = fAuth.getCurrentUser().getUid();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        System.out.println("YES BITCH"+currentUserID);
        UsersRef.child(currentUserID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                currentUserName = snapshot.child("fullName").getValue().toString();
                currentUserEmail = snapshot.child("email").getValue().toString();
                if(snapshot.hasChild("count")){
                    count = snapshot.child("count").getValue().toString();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        playActivity = (PlayActivity) intent.getSerializableExtra("PlayActivity");

        owner = playActivity.getOwner();

        databaseReference.child(playActivity.getuID()).child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    User currentuser = dataSnapshot.getValue(User.class);
                    if(currentUserID.equals(currentuser.getuID())){
                        //System.out.println("GONEEEEEEEE");
                        join.setEnabled(false);
                        join.setVisibility(View.INVISIBLE);
                        chat.setEnabled(true);
                        chat.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        list = playActivity.getUsers();

        sp.setText(playActivity.getSport());
        loc.setText(playActivity.getLocation());
        dt.setText(playActivity.getDate());
        ti.setText(playActivity.getTime());

        otherUser.setText("View "+owner.getFullName()+"'s Profile");

        join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addUser(view);
            }
        });

        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                message(view);
            }
        });
        otherUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ctherUserProfile(view);
            }
        });

    }
    public void showDialogAllPlayers(View view){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("All Players");
        StringBuilder message = new StringBuilder();
        for(int i =0; i < list.size(); i++){
            message.append((i+1)+". "+list.get(i).getFullName()+"\n");
        }
        alert.setMessage(message.toString());
        alert.create().show();
    }

    private void ctherUserProfile(View view) {
        if (isOnline()==false) {      JoinActivity.this.startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS)); }
        Intent intent = new Intent(JoinActivity.this, OtherUserProfile.class);
        intent.putExtra("Owner",owner);
        startActivity(intent);
    }

    private void message(View view) {
        if (isOnline()==false) {      JoinActivity.this.startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS)); }
        Intent intent = new Intent(JoinActivity.this, ChatActivity.class);
        intent.putExtra("PlayActivity",playActivity);
        startActivity(intent);
    }

    private void addUser(View view) {
        int temp = Integer.parseInt(count);
        temp++;
        User user = new User(currentUserID,currentUserName,currentUserEmail,String.valueOf(temp));
        playActivity.addUser(user);
        System.out.println(playActivity.getuID());
        databaseReference.child(playActivity.getuID()).setValue(playActivity)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
        UsersRef.child(currentUserID).setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

    }
}