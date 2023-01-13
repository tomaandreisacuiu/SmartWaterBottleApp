package com.example.smartwaterbottle;

public class AlarmModel {

    // PROPERTIES OF AN ALARM OBJECT
    private int id;
    private String medicine_Name;
    private int hour;
    private int minutes;
    private boolean isRepeating;
    private String pill_Container;
    private String pill_Number;

    // CONSTRUCTOR FOR THIS CLASS
    public AlarmModel(int id, String medicine_Name, int hour, int minutes, boolean isRepeating,
                      String pill_Container, String pill_Number) {
        this.id = id;
        this.medicine_Name = medicine_Name;
        this.hour = hour;
        this.minutes = minutes;
        this.isRepeating = isRepeating;
        this.pill_Container = pill_Container;
        this.pill_Number = pill_Number;
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

    public String getPill_Container() {
        return pill_Container;
    }

    public void setPill_Container(String pill_Container) {
        this.pill_Container = pill_Container;
    }

    public String getpill_Number() {
        return pill_Number;
    }

    public void setpill_Number(String pill_Number) {
        this.pill_Number = pill_Number;
    }

    // ToString METHOD
    @Override
    public String toString() {
        return "AlarmModel{" +
                "medicine_Name='" + medicine_Name + '\'' +
                ", hour=" + hour +
                ", minutes=" + minutes +
                ", isRepeating=" + isRepeating +
                ", pillBox=" + pill_Container +
                ", pillNumber=" + pill_Number +
                '}';
    }

}
