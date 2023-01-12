package com.example.smartwaterbottle;

public class AlarmModel {

    // PROPERTIES OF AN ALARM OBJECT
    private int id;
    private String medicine_Name;
    private int hour;
    private int minutes;
    private boolean isRepeating;

    // CONSTRUCTOR FOR THIS CLASS
    public AlarmModel(int id, String medicine_Name, int hour, int minutes, boolean isRepeating) {
        this.id = id;
        this.medicine_Name = medicine_Name;
        this.hour = hour;
        this.minutes = minutes;
        this.isRepeating = isRepeating;
    }

    // GETTERS AND SETTERS
    public String getMedicine_Name() {
        return medicine_Name;
    }

    public void setMedicine_Name(String medicine_Name) {
        this.medicine_Name = medicine_Name;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinutes() {
        return minutes;
    }

    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }

    public boolean isRepeating() {
        return isRepeating;
    }

    public void setRepeating(boolean repeating) {
        isRepeating = repeating;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    // ToString METHOD
    @Override
    public String toString() {
        return "AlarmModel{" +
                "medicine_Name='" + medicine_Name + '\'' +
                ", hour=" + hour +
                ", minutes=" + minutes +
                ", isRepeating=" + isRepeating +
                '}';
    }

}
