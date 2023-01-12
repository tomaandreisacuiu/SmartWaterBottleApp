package com.example.smartwaterbottle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

public class SettingsActivity extends AppCompatActivity {

    // DECLARE OBJECTS
//    DrawerLayout drawerLayout;
//    NavigationView navigationView;
//    ActionBarDrawerToggle drawerToggle;
    Button button;
    Button button2;
    Spinner genderSpinner;
    EditText weightEditText;
    EditText walkingEditText;
    EditText cyclingEditText;
    EditText runningEditText;
    EditText gymEditText;
    EditText swimEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // BUTTONS
        button = (Button) findViewById(R.id.editMyDetailsButton);
        button2 = (Button) findViewById(R.id.discardButton);

        // FIND VIEW BY IDS
        genderSpinner = findViewById(R.id.genders_spinner);
        weightEditText = findViewById(R.id.weightEditText);
        walkingEditText = findViewById(R.id.walkingEditText);
        cyclingEditText = findViewById(R.id.cyclingEditText);
        runningEditText = findViewById(R.id.runningEditText);
        gymEditText = findViewById(R.id.gymEditText);
        swimEditText = findViewById(R.id.swimEditText);

        // SPINNER
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.genders, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(adapter);

        // APPLY CHANGES BUTTON
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //openNewActivity();

                // SHARED PREFERENCES
                SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();

                String gender = genderSpinner.getSelectedItem().toString();

                try {
                    String weightString = weightEditText.getText().toString();
                    String walkingString = walkingEditText.getText().toString();
                    String cyclingString = cyclingEditText.getText().toString();
                    String runningString = runningEditText.getText().toString();
                    String gymString = gymEditText.getText().toString();
                    String swimString = swimEditText.getText().toString();

                    int weight = Integer.parseInt(weightString);
                    int walking = Integer.parseInt(walkingString);
                    int cycling = Integer.parseInt(cyclingString);
                    int running = Integer.parseInt(runningString);
                    int gym = Integer.parseInt(gymString);
                    int swim = Integer.parseInt(swimString);

                    editor.putString("gender", gender);
                    editor.putInt("weight", weight);
                    editor.putInt("walking", walking);
                    editor.putInt("cycling", cycling);
                    editor.putInt("running", running);
                    editor.putInt("gym", gym);
                    editor.putInt("swim", swim);

                    editor.apply();
                    openNewActivity();

                    Toast.makeText(SettingsActivity.this, "Changes done!",
                            Toast.LENGTH_LONG).show();

                } catch (Exception e) {
                    Toast.makeText(SettingsActivity.this, "Wrong input, try again",
                            Toast.LENGTH_LONG).show();
                }

            }
        });

        // DISCARD CHANGES BUTTON
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openNewActivity();
                Toast.makeText(SettingsActivity.this, "No changes were made",
                        Toast.LENGTH_LONG).show();
            }
        });
    }


    public void openNewActivity(){
        Intent intent = new Intent(this, Activity2.class);
        startActivity(intent);
    }

    private void alertDialog() {
        AlertDialog.Builder dialog=new AlertDialog.Builder(this);
        dialog.setMessage("Please introduce an integer");
        dialog.setTitle("Input error");
        dialog.setPositiveButton("YES",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        Toast.makeText(getApplicationContext(),"Yes is clicked",Toast.LENGTH_LONG).show();
                    }
                });
        dialog.setNegativeButton("cancel",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplicationContext(),"cancel is clicked",Toast.LENGTH_LONG).show();
            }
        });
        AlertDialog alertDialog=dialog.create();
        alertDialog.show();
    }

}