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
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
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

public class Activity3<a5010a43559> extends AppCompatActivity {

    // CREATE OBJECTS
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ActionBarDrawerToggle drawerToggle;
    TextView intakeViewValue;
    TextView textViewBT;
    TextView textViewIntakeValue;

    private static final int MY_PERMISSIONS_REQUEST_BLUETOOTH = 1;

    private static final UUID serviceUUID = UUID.fromString("3cfc9609-f2be-4336-a58e-a5010a43559f");
    private static final UUID characteristicUUID = UUID.fromString("8edb60b0-0b93-4403-8e39-44f1abf18e93");

    private BluetoothDevice device;
    private BluetoothGatt mBluetoothGatt;

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
        textViewIntakeValue = findViewById(R.id.textIntakeBT);
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
                Toast.makeText(this, "Device does not support Bluetooth", Toast.LENGTH_SHORT).show();
                return;
            } else if (!btAdapter.isEnabled()) {
                System.out.println("Please turn on Bluetooth");
                Toast.makeText(this, "Bluetooth is not enabled", Toast.LENGTH_SHORT).show();
                return;
            }

            Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();

            if (pairedDevices == null) {
                Toast.makeText(this, "No device found", Toast.LENGTH_SHORT).show();
                System.out.println("No device found");
                return;
            }

            // check in the terminal if the result is good
            System.out.println(pairedDevices);
            for (BluetoothDevice device : pairedDevices) {
                System.out.println(device.getName());
            }
            // end checking

            // putting all the bonded devices in a textview
            StringBuilder sb = new StringBuilder();
            for (BluetoothDevice device : pairedDevices) {
                sb.append(device.getName() + "\n");
            }
            textViewBT.setText(sb.toString());
            // end putting bonded devices in textview

            // Find the device we want to connect to
            for (BluetoothDevice d : pairedDevices) {
                if (d.getName().equals("Water Bottle Code_Near")) {
                    device = d;
                    System.out.println("DEVICE FOUND!");
                    break;
                }
            }

            if (device == null) {
                Toast.makeText(this, "Device not found", Toast.LENGTH_LONG).show();
                System.out.println("Device not found");
                return;
            }

            // Connect to the device
            mBluetoothGatt = device.connectGatt(this, true, new BluetoothGattCallback() {
                @Override
                public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                    if (status == BluetoothGatt.GATT_SUCCESS) {
                        BluetoothGattService service = mBluetoothGatt.getService(serviceUUID);
                        if (service == null) {
                            Toast.makeText(Activity3.this, "Service not found", Toast.LENGTH_SHORT).show();
                            System.out.println("Service not found");
                            return;
                        }
                        BluetoothGattCharacteristic characteristic = service.getCharacteristic(characteristicUUID);
                        if (characteristic == null) {
                            Toast.makeText(Activity3.this, "Characteristic not found", Toast.LENGTH_SHORT).show();
                            System.out.println("Characteristic not found");
                            return;
                        }

                        // Check if the required permissions are granted
                        if (ContextCompat.checkSelfPermission(Activity3.this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED ||
                                ContextCompat.checkSelfPermission(Activity3.this, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED) {
                            // Request the permissions
                            ActivityCompat.requestPermissions(Activity3.this, new String[]{Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN}, MY_PERMISSIONS_REQUEST_BLUETOOTH);
                        } else {
                            // Permissions are granted, you can call readCharacteristic()
                            if (!mBluetoothGatt.readCharacteristic(characteristic)) {
                                Toast.makeText(Activity3.this, "Failed to read characteristic", Toast.LENGTH_SHORT).show();
                                System.out.println("Failed to read characteristic");
                            }
                        }
                    } else {
                        Toast.makeText(Activity3.this, "Failed to discover services", Toast.LENGTH_SHORT).show();
                        System.out.println("Failed to discover services");

                    }

                }

                @Override
                public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                    if (status == BluetoothGatt.GATT_SUCCESS) {
                        byte[] value = characteristic.getValue();
                        textViewIntakeValue.setText(new String(value));
                    } else {
                        Toast.makeText(Activity3.this, "Failed to read characteristic", Toast.LENGTH_SHORT).show();
                        System.out.println("Failed to read characteristic");
                    }
                }

                @Override
                public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                    if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                        if (ContextCompat.checkSelfPermission(Activity3.this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED ||
                                ContextCompat.checkSelfPermission(Activity3.this, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED) {
                            // Request the permissions
                            ActivityCompat.requestPermissions(Activity3.this, new String[]{Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN}, MY_PERMISSIONS_REQUEST_BLUETOOTH);
                        } else {
                            // Permissions are granted, you can call close()
                            if (mBluetoothGatt != null) {
                                mBluetoothGatt.close();
                                mBluetoothGatt = null;
                            }
                        }
                        Toast.makeText(Activity3.this, "Device disconnected", Toast.LENGTH_SHORT).show();
                        System.out.println("Device disconeected");
                    }
                }
            });
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBluetoothGatt != null) {
            // Check if the required permissions are granted
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED) {
                // Request the permissions
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN}, MY_PERMISSIONS_REQUEST_BLUETOOTH);
            } else {
                // Permissions are granted, you can call close()
                if (mBluetoothGatt != null) {
                    mBluetoothGatt.close();
                    mBluetoothGatt = null;
                }
            }

            //mBluetoothGatt = null;
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


