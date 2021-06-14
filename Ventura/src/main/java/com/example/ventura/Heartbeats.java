package com.example.ventura;

import java.util.ArrayList;

public class Heartbeats {
    private ArrayList<Heartbeat> heartbeats;

    public ArrayList<Heartbeat> getHeartbeats(){
        return heartbeats;
    }
    public void addHeartbeat(Heartbeat h){
        heartbeats.add(h);
    }
    public int getNumberOfHeartbeats(){
        return heartbeats.size();
    }
    public Heartbeat getHeartbeatAtPos(int pos){
        return heartbeats.get(pos);
    }
    public Heartbeats(){
        heartbeats = new ArrayList<Heartbeat>();
    }

    public void clear() {
        heartbeats.clear();
    }
}
