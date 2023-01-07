package com.example.smartwaterbottle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

public class Activity3 extends AppCompatActivity {

    // CREATE OBJECTS
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ActionBarDrawerToggle drawerToggle;

    TextView intakeViewValue;

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
        setContentView(R.layout.activity_3);

//        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
//        bluetoothAdapter = bluetoothManager.getAdapter();

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
                        //Toast.makeText(Activity3.this, "Schedule Selected", Toast.LENGTH_SHORT).show();
                        Intent activity2Intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(activity2Intent);
                        break;
                    }
                    case R.id.waterIntake:
                    {
                        //Toast.makeText(Activity3.this, "water Intake Selected", Toast.LENGTH_SHORT).show();
                        Intent activity2Intent2 = new Intent(getApplicationContext(), Activity3.class);
                        startActivity(activity2Intent2);
                        break;

                    }
                    case R.id.setting:
                    {
                        //Toast.makeText(Activity3.this, "settings Selected", Toast.LENGTH_SHORT).show();
                        Intent activity2Intent3 = new Intent(getApplicationContext(), Activity3.class);
                        startActivity(activity2Intent3);
                        break;

                    }
                }
                return false;
            }
        });

        // Display the suggested water intake level below
        intakeViewValue = findViewById(R.id.textIntakeViewValue);
        SharedPreferences prefs2 = getSharedPreferences("prefs2", MODE_PRIVATE);

        float intake = prefs2.getFloat("intake", 2);
        intakeViewValue = findViewById(R.id.textIntakeViewValue);

        intakeViewValue.setText(String.valueOf(intake) + " L");


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