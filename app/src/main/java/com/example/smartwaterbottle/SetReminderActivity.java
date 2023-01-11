package com.example.smartwaterbottle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import java.util.Calendar;

public class SetReminderActivity extends AppCompatActivity implements View.OnClickListener{

    //private int notificationId = 1;

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ActionBarDrawerToggle drawerToggle;
    Switch repeatSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_reminder);

        // BUTTONS SETUP
        findViewById(R.id.setBtn).setOnClickListener(this);
        findViewById(R.id.cancelBtn).setOnClickListener(this);

        // SWITCH FOR REPEAT SETUP
        repeatSwitch = findViewById(R.id.switch1);
    }

    private int notificationIdCounter = 0;

    @Override
    public void onClick(View view) {
        EditText editText = findViewById(R.id.textEditMedicine);
        TimePicker timePicker = findViewById(R.id.timePicker);

        //Set notificaton ID and text
        Intent intent  = new Intent(SetReminderActivity.this, AlarmReceiver.class);
        intent.putExtra("notificationId", notificationIdCounter);
        intent.putExtra("todo", editText.getText().toString());
        notificationIdCounter++;

        PendingIntent alarmIntent = PendingIntent.getBroadcast(SetReminderActivity.this,
                0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        AlarmManager alarm = (AlarmManager) getSystemService(ALARM_SERVICE);

        switch (view.getId()) {
            case R.id.setBtn:
                int hour = timePicker.getCurrentHour();
                int minute = timePicker.getCurrentMinute();

                Calendar startTime = Calendar.getInstance();
                startTime.set(Calendar.HOUR_OF_DAY, hour);
                startTime.set(Calendar.MINUTE, minute);
                startTime.set(Calendar.SECOND, 0);
                long alarmStartTime = startTime.getTimeInMillis();

                //alarm.set(AlarmManager.RTC_WAKEUP, alarmStartTime, alarmIntent);

                // BEGIN: CODE FOR REPEATING THE ALARM DAILY
                long repeatInterval = AlarmManager.INTERVAL_DAY;
                //long alarmStartTime;
                //long repeatInterval = 600000;

                if(repeatSwitch.isChecked()){
                    alarm.setRepeating(AlarmManager.RTC_WAKEUP, alarmStartTime, repeatInterval, alarmIntent);
                    Toast.makeText(this, "Done! Alarm will repeat daily", Toast.LENGTH_SHORT).show();
                } else {
                    alarm.set(AlarmManager.RTC_WAKEUP, alarmStartTime, alarmIntent);
                    Toast.makeText(this, "Done!", Toast.LENGTH_SHORT).show();
                }
                // END: CODE FOR REPEATING THE ALARM DAILY

                Toast.makeText(this, "Done!", Toast.LENGTH_SHORT).show();
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