package com.example.smartwaterbottle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

public class Activity2 extends AppCompatActivity {

    // DECLARE OBJECTS
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ActionBarDrawerToggle drawerToggle;
    TextView weightTextView;
    Button button;
    TextView genderTextView;
    TextView walkingView;
    TextView cyclingView;
    TextView runningView;
    TextView gymView;
    TextView swimView;
    TextView suggested;

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_2);

        //SHARED PREFERANCES
        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        String gender = prefs.getString("gender", "No gender selected");

        // VARIABLES FOR TEXTVIEWS
        int weight = prefs.getInt("weight", 80);
        int walking = prefs.getInt("walking", 3);
        int cycling = prefs.getInt("cycling", 2);
        int running = prefs.getInt("running", 1);
        int gym = prefs.getInt("gym", 0);
        int swim = prefs.getInt("swim", 0);

        // Display the gender in the TextView
        genderTextView = findViewById(R.id.genderViewValue);
        weightTextView = findViewById(R.id.weightViewValue);
        walkingView = findViewById(R.id.walkingViewValue);
        cyclingView = findViewById(R.id.cyclingViewValue);
        runningView = findViewById(R.id.runningViewValue);
        gymView = findViewById(R.id.gymViewValue);
        swimView = findViewById(R.id.swimViewValue);
        suggested = findViewById(R.id.intakeViewValue);

        // SET TEXTS
        genderTextView.setText(gender);
        weightTextView.setText(String.valueOf(weight));
        walkingView.setText(String.valueOf(walking));
        cyclingView.setText(String.valueOf(cycling));
        runningView.setText(String.valueOf(running));
        gymView.setText(String.valueOf(gym));
        swimView.setText(String.valueOf(swim));

        // FORMULA FOR SUGGESTED WATER INTAKE GOAL
        float intake = 0;
        int male = 0; // initially a male

        if ( !gender.equals("Male")) {
            male = 1;
        }

        float helper = (walking*4 + cycling*8 + running*10 + gym*6 + swim*4)/7;
        intake = (30*weight - 500*male + (weight*helper )) / 1000;

        suggested.setText(String.valueOf(intake) + " L");

        SharedPreferences prefs2 = getSharedPreferences("prefs2", MODE_PRIVATE);
        SharedPreferences.Editor editor2 = prefs2.edit();

        editor2.putFloat("intake", intake);

        editor2.apply();

        // DRAWER MENU
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.schedule:
                    {
                        //Toast.makeText(Activity2.this, "Schedule Selected", Toast.LENGTH_SHORT).show();
                        Intent activity2Intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(activity2Intent);
                        break;
                    }
                    case R.id.waterIntake:
                    {
                        Intent activity2Intent2 = new Intent(getApplicationContext(), Activity3.class);
                        startActivity(activity2Intent2);
                        //Toast.makeText(Activity2.this, "water Intake Selected", Toast.LENGTH_SHORT).show();
                        break;

                    }
                    case R.id.setting:
                    {
                        Intent activity2Intent3 = new Intent(getApplicationContext(), Activity2.class);
                        startActivity(activity2Intent3);
                        //Toast.makeText(Activity2.this, "settings Selected", Toast.LENGTH_SHORT).show();
                        break;

                    }
                }
                return false;
            }
        });

        // EDIT DETAILS BUTTON
        button = (Button) findViewById(R.id.editMyDetailsButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openNewActivity();
            }
        });
    }

    public void openNewActivity(){
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

}