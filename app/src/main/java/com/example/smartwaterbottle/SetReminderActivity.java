package com.example.smartwaterbottle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;
import java.util.UUID;

public class SetReminderActivity extends AppCompatActivity implements View.OnClickListener{

    DrawerLayout drawerLayout;
    Switch repeatSwitch;
    Spinner spinnerPillBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_reminder);

        // BUTTONS SETUP
        findViewById(R.id.setBtn).setOnClickListener(this);
        findViewById(R.id.cancelBtn).setOnClickListener(this);

        // SWITCH FOR REPEAT SETUP
        repeatSwitch = findViewById(R.id.switch1);
        spinnerPillBox = findViewById(R.id.spinnerPillBox);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.pill_boxes, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPillBox.setAdapter(adapter);
    }

    private int notificationIdCounter = 0;

    @Override
    public void onClick(View view) {
        EditText editText = findViewById(R.id.textEditMedicine);
        TimePicker timePicker = findViewById(R.id.timePicker);
        EditText textEditPills = findViewById(R.id.textEditPills);

        // Create a unique request code for the PendingIntent
        int requestCode = (int) System.currentTimeMillis();

        //Set notificaton ID and text
        System.out.println(spinnerPillBox.getSelectedItem().toString());
        Intent intent  = new Intent(SetReminderActivity.this, AlarmReceiver.class);
        intent.putExtra("notificationId", notificationIdCounter);
        intent.putExtra("todo", "Take " + textEditPills.getText().toString() +
                " pills of " + editText.getText().toString() + " from box " +
                spinnerPillBox.getSelectedItem().toString());
        notificationIdCounter++;

        PendingIntent alarmIntent = PendingIntent.getBroadcast(SetReminderActivity.this,
                requestCode, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        AlarmManager alarm = (AlarmManager) getSystemService(ALARM_SERVICE);

//        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
//                R.array.pill_boxes, android.R.layout.simple_spinner_item);
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spinnerPillBox.setAdapter(adapter);

        switch (view.getId()) {
            case R.id.setBtn:
                int hour = timePicker.getCurrentHour();
                int minute = timePicker.getCurrentMinute();

                Calendar startTime = Calendar.getInstance();
                startTime.set(Calendar.HOUR_OF_DAY, hour);
                startTime.set(Calendar.MINUTE, minute);
                startTime.set(Calendar.SECOND, 0);
                long alarmStartTime = startTime.getTimeInMillis();

                //
                DataBaseHelper dataBaseHelper;
                AlarmModel alarmModel;

                // CODE FOR REPEATING THE ALARM DAILY
                long repeatInterval = AlarmManager.INTERVAL_DAY;


                if(repeatSwitch.isChecked()){

                    alarmModel = new AlarmModel(-1, editText.getText().toString(),
                                hour, minute, true,
                            spinnerPillBox.getSelectedItem().toString(), textEditPills.getText().toString());

                    alarm.setRepeating(AlarmManager.RTC_WAKEUP, alarmStartTime, repeatInterval, alarmIntent);

                    dataBaseHelper = new DataBaseHelper(SetReminderActivity.this);
                    boolean success = dataBaseHelper.addOne(alarmModel);
                    Toast.makeText(this, "Success = " + success, Toast.LENGTH_LONG).show();

                } else {

                    alarmModel = new AlarmModel(-1, editText.getText().toString(),
                            hour, minute, false,
                            spinnerPillBox.getSelectedItem().toString(), textEditPills.getText().toString());

                    alarm.set(AlarmManager.RTC_WAKEUP, alarmStartTime, alarmIntent);

                    dataBaseHelper = new DataBaseHelper(SetReminderActivity.this);
                    boolean success = dataBaseHelper.addOne(alarmModel);
                    Toast.makeText(this, "Success = " + success, Toast.LENGTH_LONG).show();

                }

                Intent activity2Intent3 = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(activity2Intent3);
                break;

            case R.id.cancelBtn:
                alarm.cancel(alarmIntent);
                Toast.makeText(this, "Canceled", Toast.LENGTH_SHORT).show();
                Intent activity2Intent4 = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(activity2Intent4);
                break;
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