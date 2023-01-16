package com.example.smartwaterbottle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;

import java.util.UUID;

public class Activity3 extends AppCompatActivity {

//    private static final String DEVICE_NAME = "Water Bottle";
//    private static final UUID SERVICE_UUID = UUID.fromString("3cfc9609-f2be-4336-a58e-a5010a43559e");
//    private static final UUID CHARACTERISTIC_UUID = UUID.fromString("8edb60b0-0b93-4403-8e39-44f1abf18e93");
//
//    private BluetoothAdapter mBluetoothAdapter;
//    private BluetoothLeScanner mBluetoothLeScanner;
//    private ScanCallback mScanCallback;
//    private BluetoothGatt mBluetoothGatt;
    private TextView mValueTextView;

    // CREATE OBJECTS
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ActionBarDrawerToggle drawerToggle;
    TextView intakeViewValue;

    private static final int REQUEST_ENABLE_BT = 1;
    private static final int REQUEST_LOCATION_PERMISSION = 2;

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

        mValueTextView = findViewById(R.id.textView);

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


