package com.example.ankit.smartattendancesystem;

/**
 * Created by Ankit on 1/18/2015.
 */
public class UserGroup
{
    private String latitude;
    private String longitude;
    private String name;
    private String role;
    private int ID;
    private Boolean attendance;
    private long duration;
    private float percentage;

    public UserGroup(String latitude, String longitude, String name, String role, int ID, Boolean attendance, long duration, float percentage) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.name = name;
        this.role = role;
        this.ID = ID;
        this.attendance = attendance;
        this.duration = duration;
        this.percentage = percentage;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getName() {
        return name;
    }

    public String getRole() {
        return role;
    }

    public int getID() {
        return ID;
    }

    public Boolean getAttendance() {
        return attendance;
    }

    public long getDuration() {
        return duration;
    }

    public float getPercentage() {
        return percentage;
    }

    @Override
    public String toString() {
        return "UserGroup{" +
                "latitude='" + latitude + '\'' +
                ", longitude='" + longitude + '\'' +
                ", name='" + name + '\'' +
                ", role='" + role + '\'' +
                ", ID=" + ID +
                ", attendance=" + attendance +
                ", duration=" + duration +
                ", percentage=" + percentage +
                '}';
    }
}
