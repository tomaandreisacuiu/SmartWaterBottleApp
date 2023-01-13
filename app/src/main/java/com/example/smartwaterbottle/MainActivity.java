package com.example.smartwaterbottle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothSocket;
import android.bluetooth.le.BluetoothLeScanner;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

//import com.example.smartwaterbottle.databinding.ActivityMainBinding;
import com.google.android.material.navigation.NavigationView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

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

    Button button;
    Button btn_ViewAll;
    View lv_alarmsList;
    ArrayAdapter alarmsArrayAdapter;
    DataBaseHelper dataBaseHelper;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //btn_ViewAll = findViewById(R.id.btn_ViewAll);
        lv_alarmsList = findViewById(R.id.textView);
        listView = (ListView) findViewById(R.id.listView);

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
                        Intent activity2Intent3 = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(activity2Intent3);
                        //Toast.makeText(MainActivity.this, "Schedule Selected", Toast.LENGTH_SHORT).show();
                        break;
                    }
                    case R.id.waterIntake:
                    {
                        Intent activity2Intent2 = new Intent(getApplicationContext(), Activity3.class);
                        startActivity(activity2Intent2);
                        //Toast.makeText(MainActivity.this, "water Intake Selected", Toast.LENGTH_SHORT).show();
                        break;

                    }
                    case R.id.setting:
                    {
                        //Toast.makeText(MainActivity.this, "settings Selected", Toast.LENGTH_SHORT).show();
                        Intent activity2Intent = new Intent(getApplicationContext(), Activity2.class);
                        startActivity(activity2Intent);
                        break;

                    }
                }
                return false;
            }
        });

        button = (Button) findViewById(R.id.editMyDetailsButton);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openNewActivity();
            }
        });
        
        dataBaseHelper = new DataBaseHelper(MainActivity.this);
        
        alarmsArrayAdapter = new ArrayAdapter<AlarmModel>(MainActivity.this, android.R.layout.simple_list_item_1, dataBaseHelper.getEveryone());
        listView.setAdapter(alarmsArrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AlarmModel clickedAlarm = (AlarmModel) parent.getItemAtPosition(position);
                dataBaseHelper.deleteOne(clickedAlarm);
                alarmsArrayAdapter = new ArrayAdapter<AlarmModel>(MainActivity.this, android.R.layout.simple_list_item_1, dataBaseHelper.getEveryone());
                listView.setAdapter(alarmsArrayAdapter);
                Toast.makeText(MainActivity.this, "Deleted! " + clickedAlarm.toString(), Toast.LENGTH_LONG).show();
            }
        });
    }


    public void openNewActivity(){
        Intent intent = new Intent(this, SetReminderActivity.class);
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