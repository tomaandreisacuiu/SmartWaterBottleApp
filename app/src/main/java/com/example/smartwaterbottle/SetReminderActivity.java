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
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import java.util.Calendar;

public class SetReminderActivity extends AppCompatActivity implements View.OnClickListener{

    private int notificationId = 1;

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ActionBarDrawerToggle drawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_reminder);

        findViewById(R.id.setBtn).setOnClickListener(this);
        findViewById(R.id.cancelBtn).setOnClickListener(this);

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

    }


    @Override
    public void onClick(View view) {
        EditText editText = findViewById(R.id.textEditMedicine);
        TimePicker timePicker = findViewById(R.id.timePicker);

        //Set notificaton ID and text
        Intent intent  = new Intent(SetReminderActivity.this, AlarmReceiver.class);
        intent.putExtra("notificationId", notificationId);
        intent.putExtra("todo", editText.getText().toString());

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

                alarm.set(AlarmManager.RTC_WAKEUP, alarmStartTime, alarmIntent);

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