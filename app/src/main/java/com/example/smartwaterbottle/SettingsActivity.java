package com.example.smartwaterbottle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.android.material.navigation.NavigationView;

public class SettingsActivity extends AppCompatActivity {

    // DECLARE OBJECTS
//    DrawerLayout drawerLayout;
//    NavigationView navigationView;
//    ActionBarDrawerToggle drawerToggle;
    Button button;
    Button button2;
    Spinner genderSpinner;
    EditText weightEditText;
    EditText walkingEditText;
    EditText cyclingEditText;
    EditText runningEditText;
    EditText gymEditText;
    EditText swimEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // BUTTONS
        button = (Button) findViewById(R.id.editMyDetailsButton);
        button2 = (Button) findViewById(R.id.discardButton);

        // FIND VIEW BY IDS
        genderSpinner = findViewById(R.id.genders_spinner);
        weightEditText = findViewById(R.id.weightEditText);
        walkingEditText = findViewById(R.id.walkingEditText);
        cyclingEditText = findViewById(R.id.cyclingEditText);
        runningEditText = findViewById(R.id.runningEditText);
        gymEditText = findViewById(R.id.gymEditText);
        swimEditText = findViewById(R.id.swimEditText);

        // SPINNER
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.genders, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(adapter);

        // APPLY CHANGES BUTTON
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openNewActivity();
                String gender = genderSpinner.getSelectedItem().toString();
                String weightString = weightEditText.getText().toString();
                String walkingString = walkingEditText.getText().toString();
                String cyclingString = cyclingEditText.getText().toString();
                String runningString = runningEditText.getText().toString();
                String gymString = gymEditText.getText().toString();
                String swimString = swimEditText.getText().toString();

                // VARIABLES TAKEN FROM EDITTEXTS FIELDS
                int weight = Integer.parseInt(weightString);
                int walking = Integer.parseInt(walkingString);
                int cycling = Integer.parseInt(cyclingString);
                int running = Integer.parseInt(runningString);
                int gym = Integer.parseInt(gymString);
                int swim = Integer.parseInt(swimString);

                // SHARED PREFERENCES
                SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();

                // PUT STRINGS/ INTS IN EDITOR
                editor.putString("gender", gender);
                editor.putInt("weight", weight);
                editor.putInt("walking", walking);
                editor.putInt("cycling", cycling);
                editor.putInt("running", running);
                editor.putInt("gym", gym);
                editor.putInt("swim", swim);

                editor.apply();
                openNewActivity();

            }
        });

//        drawerLayout = findViewById(R.id.drawer_layout);
//        navigationView = findViewById(R.id.nav_view);
//        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
//        drawerLayout.addDrawerListener(drawerToggle);
//        drawerToggle.syncState();
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
//            @Override
//            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//                switch (item.getItemId()) {
//                    case R.id.schedule:
//                    {
//                        Intent activity2Intent3 = new Intent(getApplicationContext(), MainActivity.class);
//                        startActivity(activity2Intent3);
//                        //Toast.makeText(MainActivity.this, "Schedule Selected", Toast.LENGTH_SHORT).show();
//                        break;
//                    }
//                    case R.id.waterIntake:
//                    {
//                        Intent activity2Intent2 = new Intent(getApplicationContext(), Activity3.class);
//                        startActivity(activity2Intent2);
//                        //Toast.makeText(MainActivity.this, "water Intake Selected", Toast.LENGTH_SHORT).show();
//                        break;
//
//                    }
//                    case R.id.setting:
//                    {
//                        //Toast.makeText(MainActivity.this, "settings Selected", Toast.LENGTH_SHORT).show();
//                        Intent activity2Intent = new Intent(getApplicationContext(), Activity2.class);
//                        startActivity(activity2Intent);
//                        break;
//
//                    }
//                }
//                return false;
//            }
//        });

        // DISCARD CHANGES BUTTON
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openNewActivity();
            }
        });
    }


    public void openNewActivity(){
        Intent intent = new Intent(this, Activity2.class);
        startActivity(intent);
    }

}