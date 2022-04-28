package com.example.playpal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.icu.util.Calendar;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.text.InputType;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class CreateActivity extends AppCompatActivity {
    private DatabaseReference mAuth,UsersRef;
    private EditText sp;
    private EditText loc;
    private EditText dt;
    private EditText ti;
    private Button create;
    private String item ="";
    private String itemL ="";
    private FirebaseAuth fAuth;
    private String currentUserID,currentUserName,count,currentUserEmail;
    DatePickerDialog date_picker;
    TimePickerDialog time_picker;
    String[] items = {"Basketball","Soccer","Tennis","Badminton"};
    String[] itemsL = {"Degheri","Bellomy Field","Stephen Schott Stadium","Pat Malley Center Basketball Court 1","Pat Malley Center Basketball Court 2","Pat Malley Center Basketball Court 3","Pat Malley Center Badminton Court 1","Pat Malley Center Badminton Court 2","Pat Malley Center Badminton Court 3"};
    AutoCompleteTextView autoCompleteTextView, autoCompleteTextViewL;

    ArrayAdapter<String> adapterItems,adapterItemsL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (isOnline()==false) {      CreateActivity.this.startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS)); }
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_create_activity);
        //sp = findViewById(R.id.sport);
//        loc = findViewById(R.id.location);
        dt = findViewById(R.id.date);
        ti = findViewById(R.id.time);
        create = findViewById(R.id.create);
        autoCompleteTextView = findViewById(R.id.autoComplete);
        autoCompleteTextViewL = findViewById(R.id.autoCompleteL);

        adapterItems = new ArrayAdapter<String>(this,R.layout.list_item,items);
        adapterItemsL = new ArrayAdapter<String>(this,R.layout.list_item_loc,itemsL);

        autoCompleteTextView.setAdapter(adapterItems);
        autoCompleteTextViewL.setAdapter(adapterItemsL);

        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                item = adapterView.getItemAtPosition(i).toString();
                System.out.println(item);

            }
        });

        autoCompleteTextViewL.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                itemL = adapterView.getItemAtPosition(i).toString();

            }
        });

        count = "0";
        MaterialToolbar toolbar = findViewById(R.id.topAppBarC);
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layoutC);
        NavigationView navigationView = findViewById(R.id.navigation_viewC);
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
                        Toast.makeText(CreateActivity.this,"Home is Clicked",Toast.LENGTH_LONG).show();
                        Intent intentHome = new Intent(CreateActivity.this, HomeScreen.class);
                        startActivity(intentHome);
                        break;
                    case R.id.CreateActivity:
                        Toast.makeText(CreateActivity.this,"Create Activity is Clicked",Toast.LENGTH_LONG).show();
                        Intent intentCreate = new Intent(CreateActivity.this, CreateActivity.class);
                        startActivity(intentCreate);
                        break;

                    case R.id.profile:
                        Intent intent = new Intent(CreateActivity.this, UserProfile.class);
                        startActivity(intent);
                }
                return true;
            }
        });

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createActivity(view);
            }
        });


        dt.setInputType(InputType.TYPE_NULL);
        dt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);
                // date picker dialog
                date_picker = new DatePickerDialog(CreateActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                                dt.setText(year+"-"+dayOfMonth+"-"+(monthOfYear+1));
                            }
                        }, year, month, day);
                date_picker.show();
            }
        });


        ti.setInputType(InputType.TYPE_NULL);
        ti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int hour = cldr.get(Calendar.HOUR_OF_DAY);
                int minutes = cldr.get(Calendar.MINUTE);
                // time picker dialog
                time_picker = new TimePickerDialog(CreateActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker tp, int sHour, int sMinute) {
                                ti.setText(sHour + ":" + sMinute);
                            }
                        }, hour, minutes, false);
                time_picker.show();
            }
        });

    }
    @Override
    protected void onResume() {
        super.onResume();
        fAuth = FirebaseAuth.getInstance();
        currentUserID = fAuth.getCurrentUser().getUid();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
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
    }

    public boolean isOnline() {
        ConnectivityManager conMgr = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMgr.getActiveNetworkInfo();

        if(netInfo == null || !netInfo.isConnected() || !netInfo.isAvailable()){
            Toast.makeText(CreateActivity.this, "No Internet connection!", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    private void createActivity(View view) {
        if (isOnline()==false) {      CreateActivity.this.startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS)); }
        mAuth = FirebaseDatabase.getInstance().getReference(PlayActivity.class.getSimpleName());
//      String sport = sp.getText().toString();
        String date = dt.getText().toString();
        String time = "";
        if(!ti.getText().toString().isEmpty()){
            time = ti.getText().toString()+":00";
        }
       

        if(item.isEmpty() || itemL.isEmpty() || date.isEmpty() || time.isEmpty()){
            Toast.makeText(this,"Fields are empty",Toast.LENGTH_SHORT).show();
        }

        else{

            try {
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.ENGLISH);
                formatter.setTimeZone(TimeZone.getTimeZone("America/New_York"));
                String dateInString = date+ " "+time;
                Date check = null;
                check = formatter.parse(dateInString);
                List<User> list = new LinkedList<>();
                int temp = Integer.parseInt(count);
                temp++;
                User user = new User();
                user.setuID(currentUserID);
                user.setFullName(currentUserName);
                user.setEmail(currentUserEmail);
                user.setCount(String.valueOf(temp));
                UsersRef.child(currentUserID).setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
                list.add(user);
                PlayActivity playActivity = new PlayActivity(list,item,itemL,date,time,user);
                mAuth.push().setValue(playActivity);
                Toast.makeText(this,"Activity Created",Toast.LENGTH_SHORT).show();
                Intent intentHome = new Intent(CreateActivity.this, HomeScreen.class);
                startActivity(intentHome);

            }catch (ParseException e) {
                Toast.makeText(this,"Date or Time Format is wrong",Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }


    }
}