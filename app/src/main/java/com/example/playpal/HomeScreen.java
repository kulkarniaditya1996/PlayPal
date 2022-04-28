package com.example.playpal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Adapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;

public class HomeScreen extends AppCompatActivity {


    RecyclerView recyclerView;
    DatabaseReference database;
    MyAdapter myAdapter,newAdapter;
    ArrayList<PlayActivity> list,filteredList;
    String TAG = "hadsghhgasdhgasd";
    TextView textView;
    User user;

    public boolean isOnline() {
        ConnectivityManager conMgr = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMgr.getActiveNetworkInfo();

        if(netInfo == null || !netInfo.isConnected() || !netInfo.isAvailable()){
            Toast.makeText(HomeScreen.this, "No Internet connection!", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (isOnline()==false) {      HomeScreen.this.startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS)); }
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_home_screen);

        MaterialToolbar toolbar = findViewById(R.id.topAppBar);
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.navigation_view);
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
                        Toast.makeText(HomeScreen.this,"Home is Clicked",Toast.LENGTH_LONG);
                        Intent intentHome = new Intent(HomeScreen.this, HomeScreen.class);
                        startActivity(intentHome);
                        break;
                    case R.id.CreateActivity:
                        Toast.makeText(HomeScreen.this,"Create Activity is Clicked",Toast.LENGTH_LONG);
                        Intent intentCreate = new Intent(HomeScreen.this, CreateActivity.class);
                        startActivity(intentCreate);
                        break;

                    case R.id.profile:
                        Intent intent = new Intent(HomeScreen.this, UserProfile.class);
                        startActivity(intent);
                        break;
                }
                return true;
            }
        });

        Intent intent = getIntent();
        user = (User) intent.getSerializableExtra("User");

        recyclerView = findViewById(R.id.userList);
        database = FirebaseDatabase.getInstance().getReference(PlayActivity.class.getSimpleName());

    }

    @Override
    protected void onResume() {
        super.onResume();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        list = new ArrayList<>();

        /*filteredList = new ArrayList<>();
        for(PlayActivity playActivity : list){
            SimpleDateFormat formatter = new SimpleDateFormat("dd-M-yyyy hh:mm:ss a", Locale.ENGLISH);
            formatter.setTimeZone(TimeZone.getTimeZone("America/New_York"));
            String dateInString = playActivity.getDate()+ " "+playActivity.getTime();
            Date date = null;
            try {
                date = formatter.parse(dateInString);
                Date currentDate = new Date();
                if(date.compareTo(currentDate) > 0){
                    filteredList.add(playActivity);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

                    *//*if(playActivity.getSport().equalsIgnoreCase("track")){
                        list.add(playActivity);
                    }*//*


        }*/

        myAdapter = new MyAdapter(this, list, new MyAdapter.ItemClickListner() {
            @Override
            public void onItemClick(PlayActivity playActivity) {
                System.out.println(playActivity.getuID());
                func(playActivity);
                //Toast.makeText(HomeScreen.this,"Failed to login! Try again",Toast.LENGTH_SHORT).show();
            }
        });
        recyclerView.setAdapter(myAdapter);




        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    String key = dataSnapshot.getKey();
                    PlayActivity playActivity = dataSnapshot.getValue(PlayActivity.class);
                    playActivity.setuID(key);
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.ENGLISH);
                    formatter.setTimeZone(TimeZone.getTimeZone("America/New_York"));
                    String dateInString = playActivity.getDate()+ " "+playActivity.getTime();
                    Date date = null;
                    try {
                        date = formatter.parse(dateInString);
                        Date currentDate = new Date();


                        if(currentDate.compareTo(date) < 0){
                            list.add(playActivity);
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    /*if(playActivity.getSport().equalsIgnoreCase("track")){
                        list.add(playActivity);
                    }*/


                }
                myAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void func(PlayActivity playActivity) {
        Intent intent = new Intent(HomeScreen.this, JoinActivity.class);
        intent.putExtra("PlayActivity", playActivity);
        intent.putExtra("User",user);
        startActivity(intent);
        //Toast.makeText(HomeScreen.this,"Failed to login! Try again"+sport,Toast.LENGTH_SHORT).show();
    }
}