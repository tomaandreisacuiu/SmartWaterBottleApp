package com.example.smartwaterbottle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class Activity3 extends AppCompatActivity {

    // CREATE OBJECTS
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ActionBarDrawerToggle drawerToggle;
    TextView intakeViewValue;
    TextView textViewBT;

   private static final int MY_PERMISSIONS_REQUEST_BLUETOOTH = 1;

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onContextItemSelected(item);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_3);

        // BEGIN: DRAWER MENU
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
                    case R.id.schedule: {
                        //Toast.makeText(Activity3.this, "Schedule Selected", Toast.LENGTH_SHORT).show();
                        Intent activity2Intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(activity2Intent);
                        break;
                    }
                    case R.id.waterIntake: {
                        //Toast.makeText(Activity3.this, "water Intake Selected", Toast.LENGTH_SHORT).show();
                        Intent activity2Intent2 = new Intent(getApplicationContext(), Activity3.class);
                        startActivity(activity2Intent2);
                        break;

                    }
                    case R.id.setting: {
                        //Toast.makeText(Activity3.this, "settings Selected", Toast.LENGTH_SHORT).show();
                        Intent activity2Intent3 = new Intent(getApplicationContext(), Activity2.class);
                        startActivity(activity2Intent3);
                        break;

                    }
                }
                return false;
            }
        });
        // END: DRAWER MENU

        // BEGIN: Display the suggested water intake level below
        intakeViewValue = findViewById(R.id.textIntakeViewValue);
        SharedPreferences prefs2 = getSharedPreferences("prefs2", MODE_PRIVATE);
        float intake = prefs2.getFloat("intake", 2);
        intakeViewValue = findViewById(R.id.textIntakeViewValue);
        intakeViewValue.setText(String.valueOf(intake) + " L");
        // END: Display the suggested water intake level below

        textViewBT = findViewById(R.id.textViewBT1);
        //textViewBT.setText("NO");

        // Request the BLUETOOTH permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted, request it
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.BLUETOOTH},
                    MY_PERMISSIONS_REQUEST_BLUETOOTH);
        } else {
            // Permission is granted, proceed
            BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
            if (btAdapter == null) {
                System.out.println("Device does not support Bluetooth");
            } else if (!btAdapter.isEnabled()) {
                System.out.println("Please turn on Bluetooth");
            } else {
                System.out.println(btAdapter.getBondedDevices());

                for (BluetoothDevice device : btAdapter.getBondedDevices()) {
                    System.out.println(device.getName());
                }
            }

        }

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


