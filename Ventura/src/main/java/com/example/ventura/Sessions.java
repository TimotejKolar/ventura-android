package com.example.ventura;

import java.util.ArrayList;

public class Sessions {
    private ArrayList<Session> sessions;
    private static int numberOfSessions;

    public ArrayList<Session> getSessions() {
        return sessions;
    }

    public int getNumberOfActivities() {
        return numberOfSessions;
    }

    public Sessions() {
        this.sessions = new ArrayList<Session>();
        numberOfSessions = 0;
    }
    public int getNumberOfSessions(){
        return sessions.size();
    }
    public void addSession(Session a){
        sessions.add(a);
    }

    public double getLongestDistance(){
        double max = 0;
        for (int i = 0; i < sessions.size(); ++i){
            if(sessions.get(i).getDistance() > max){
                max = sessions.get(i).getDistance();
            }
        }
        return max;
    }
    public int size(){
        return sessions.size();
    }
    public Session getSessionAtPos(int pos){
        return sessions.get(pos);
    }

    public void addActivity(Session sesh) {
    }
}
