package com.example.ventura;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class Session {
    private ArrayList<Double> speeds;
    private ArrayList<Double> elevation;
    private ArrayList<Double> longtitude;
    private ArrayList<Double> latitude;
    private String activityName;
    private String activityType;
    private double distance;
    private long startTime;
    private long endTime;
    private long duration;
    private String uuid;

    public String getActivityType() {
        return activityType;
    }

    public void setActivityType(String activityType) {
        this.activityType = activityType;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public String getActivityName() {
        return activityName;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public ArrayList<Double> getLongtitude() {
        return longtitude;
    }

    public void setLongtitude(ArrayList<Double> longtitude) {
        this.longtitude = longtitude;
    }

    public ArrayList<Double> getLatitude() {
        return latitude;
    }

    public void setLatitude(ArrayList<Double> latitude) {
        this.latitude = latitude;
    }

    @Override
    public String toString() {
        return "Activity{" +
                ", speeds=" + speeds +
                ", inclination=" + elevation +
                ", distance=" + distance +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                '}';
    }



    public void setSpeeds(ArrayList<Double> speeds) {
        this.speeds = speeds;
    }

    public List<Double> getSpeeds() {
        return speeds;
    }

    public List<Double> getElevation() {
        return elevation;
    }

    public void setElevation(ArrayList<Double> elevation) {
        this.elevation = elevation;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }





    public double getDistance() {
        return distance;
    }
    public double getAverageSpeed(){
        double speedSum = 0;
        for (int i = 0; i < speeds.size(); ++i){
            speedSum += speeds.get(i);
        }
        return speedSum / speeds.size();
    }
    public double getMaxSpeed(){
        return Collections.max(speeds);
    }

    public double getInclinationDifference(){
        return Collections.max(elevation) - Collections.min(elevation);
    }
    public Double getMaxInclination(){
        return Collections.max(elevation);
    }


    public Session(ArrayList<Double> speeds, ArrayList<Double> inclination, double distance) {

        this.speeds = speeds;
        this.elevation = inclination;
        this.distance = distance;
        this.uuid = UUID.randomUUID().toString().replace("-","");
    }
    public Session(){
        latitude = new ArrayList<Double>();
        longtitude = new ArrayList<Double>();
        speeds = new ArrayList<Double>();
        elevation = new ArrayList<Double>();
        this.uuid = UUID.randomUUID().toString().replace("-","");
    }
    public void addLatitude(double lat){
        latitude.add(lat);
    }
    public void addLongtitude(double lon){
        longtitude.add(lon);
    }
    public void addSpeed(double speed){
        speeds.add(speed);
    }
    public void addElevation(double ele){
        elevation.add(ele);
    }
}
