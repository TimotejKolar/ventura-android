package com.example.ventura;

public class Heartbeat {
    private long date;
    private int avgHeartbeat;
    private String userId;

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public int getAvgHeartbeat() {
        return avgHeartbeat;
    }

    public void setAvgHeartbeat(int avgHeartbeat) {
        this.avgHeartbeat = avgHeartbeat;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Heartbeat() {
    }

    public Heartbeat(long date, int avgHeartbeat, String userId) {
        this.date = date;
        this.avgHeartbeat = avgHeartbeat;
        this.userId = userId;
    }
}
