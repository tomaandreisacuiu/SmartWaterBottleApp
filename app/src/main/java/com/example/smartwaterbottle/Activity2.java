package com.example.smartwaterbottle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

public class Activity2 extends AppCompatActivity {


    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ActionBarDrawerToggle drawerToggle;

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