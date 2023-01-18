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
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
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

    private static final UUID serviceUUID = UUID.fromString("3cfc9609-f2be-4336-a58e-a5010a43559e");
    private static final UUID characteristicUUID = UUID.fromString("8edb60b0-0b93-4403-8e39-44f1abf18e93");
    private static final UUID getCharacteristicUUIDCurrIntake = UUID.fromString("6d29b2f4-5726-4e37-9658-762311292d45");

    private BluetoothDevice device;
    private BluetoothGatt mBluetoothGatt;

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onContextItemSelected(item);
    }


    private void connectDevice(String deviceName) {
        System.out.println("reached here 1");

        //BluetoothManager manager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
       // BluetoothAdapter adapter = manager.getAdapter();
        //BluetoothLeScanner scanner = adapter.getBluetoothLeScanner();
//        ScanCallback scanCallback = new ScanCallback() {
//            @Override
//            public void onScanResult(int callbackType, ScanResult result) {
//                super.onScanResult(callbackType, result);
//                BluetoothDevice device = result.getDevice();
//                if (ActivityCompat.checkSelfPermission(Activity3.this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
//                    ActivityCompat.requestPermissions(Activity3.this,
//                            new String[]{Manifest.permission.BLUETOOTH},
//                            MY_PERMISSIONS_REQUEST_BLUETOOTH);
//                }
//                String deviceName = device.getName();
//                if (deviceName != null && deviceName.equals("Smart Water Bottle")) {
//                    System.out.println("reached here 2");
//
//                    scanner.stopScan(this);
//                    mBluetoothGatt = device.connectGatt(Activity3.this, false, mGattCallback);
//
//                    System.out.println("reached here 2.1");
//
//                }
//            }
//        };
//        scanner.startScan(scanCallback);
//

        if (ActivityCompat.checkSelfPermission(Activity3.this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(Activity3.this,
                    new String[]{Manifest.permission.BLUETOOTH},
                    MY_PERMISSIONS_REQUEST_BLUETOOTH);
        }

        //String deviceName = device.getName();

        if (deviceName != null && deviceName.equals("Smart Water Bottle")) {
                    System.out.println("reached here 2");

                    //scanner.stopScan(this);
                    mBluetoothGatt = device.connectGatt(Activity3.this, false, mGattCallback);

                    System.out.println("reached here 2.1");

                }

    }

    private void readCharacteristicValue(BluetoothGattCharacteristic characteristic) {
        System.out.println("reached here 3");

        if (mBluetoothGatt == null || characteristic == null) {
            textViewIntakeValue.setText("null");
            return;
        }
        if (ContextCompat.checkSelfPermission(Activity3.this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(Activity3.this, new String[]{Manifest.permission.BLUETOOTH}, MY_PERMISSIONS_REQUEST_BLUETOOTH);
        }
        mBluetoothGatt.readCharacteristic(characteristic);
    }

    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                if (ContextCompat.checkSelfPermission(Activity3.this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                System.out.println("reached here 4");
                gatt.discoverServices();
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            System.out.println("reached here 5");

            super.onServicesDiscovered(gatt, status);
            BluetoothGattService service = gatt.getService(serviceUUID);

            System.out.println(service.toString());

            BluetoothGattCharacteristic characteristic = service.getCharacteristic(characteristicUUID);
            System.out.println("got the characteristic");

            if (ContextCompat.checkSelfPermission(Activity3.this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
                // Permission is not granted, request it
                ActivityCompat.requestPermissions(Activity3.this, new String[]{Manifest.permission.BLUETOOTH}, MY_PERMISSIONS_REQUEST_BLUETOOTH);
            } else {
                // Permission has already been granted, call setCharacteristicNotification
                mBluetoothGatt.setCharacteristicNotification(characteristic, true);
            }


            readCharacteristicValue(characteristic);

            if (status == BluetoothGatt.GATT_SUCCESS) {
                System.out.println("reached here 7.2");

                //String characteristicValue = new String(characteristic.getValue().toString());
                //textViewIntakeValue = findViewById(R.id.textIntakeBT);
                //textViewIntakeValue = findViewById(R.id.textIntakeBT);
                System.out.println("null");

                //byte[] value = characteristic.getValue();
                if (characteristic.getValue() != null) {

                    System.out.println(characteristic.getValue());

                    System.out.println(characteristic.getValue().toString());

                    String characteristicValue = new String(characteristic.getValue().toString());
                    //textViewIntakeValue = findViewById(R.id.textIntakeBT);
                    textViewIntakeValue.setText(characteristicValue);
                }

                System.out.println("reached here 8");

            }

            System.out.println("reached here 6");

        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic,
                                         int status) {
            System.out.println("reached here 7.1");

            if (status == BluetoothGatt.GATT_SUCCESS) {
                System.out.println("reached here 7.2");

                byte[] value = characteristic.getValue();
                System.out.println(new String(value));

                String characteristicValue = new String(value);
                textViewIntakeValue = findViewById(R.id.textIntakeBT);
                textViewIntakeValue.setText(characteristicValue);

                System.out.println("reached here 8");

            }
        }
    };


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
                if (d.getName().equals("Smart Water Bottle")) {
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

        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSIONS_REQUEST_BLUETOOTH);
        } else {
            connectDevice(device.getName());
            System.out.println("Connecting");
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


