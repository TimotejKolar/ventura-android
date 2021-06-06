package com.example.ventura;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.osmdroid.api.IMapController;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Polyline;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

public class AdapterSession extends RecyclerView.Adapter<AdapterSession.ViewHolder> {
    private MyApplication app;
    private OnItemClickListener listener;
    private Sessions sessions;

    public AdapterSession(MyApplication app, OnItemClickListener listener, Sessions sessions) {
        this.app = app;
        this.listener = listener;
        this.sessions = sessions;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recycleview_layout, parent, false);
        view.getLayoutParams().height = 730;
        AdapterSession.ViewHolder viewHolder = new AdapterSession.ViewHolder(view);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        double distance = 0, durationHours= 0, durationMinutes = 0, durationSeconds;
        Session s = sessions.getSessionAtPos(position);
        distance = s.getDistance()/1000;
        distance = round(distance, 2);
        durationMinutes = s.getDuration()/60;
        if(durationMinutes >= 60) {
            durationHours++;
            durationMinutes -= 60;
        }
        durationSeconds = s.getDuration()%60;
        holder.textViewTitleRV.setText(s.getActivityName());
        holder.textViewDistanceValue.setText(Double.toString(distance) + " km");
        if(durationHours != 0) {
            holder.textViewDurationValue.setText(Integer.toString((int)durationHours) + "h " + Integer.toString((int)durationMinutes) + "m " + Integer.toString((int)durationSeconds) + "s");
        }
        else if(durationMinutes != 0){
            holder.textViewDurationValue.setText(Integer.toString((int)durationMinutes) + "m " + Integer.toString((int)durationSeconds) + "s");
        }
        else {
            holder.textViewDurationValue.setText(Integer.toString((int)durationSeconds) + "s");
        }
        drawPath(holder.map, s.getLatitude(), s.getLongtitude());
        Log.i("asd", "test");
    }

    public void drawPath(MapView map, ArrayList<Double> latitude, ArrayList<Double> longitude) {
        Polyline polyline = new Polyline(map);
        IMapController mapController = map.getController();
        GeoPoint startPoint = new GeoPoint(latitude.get(0), longitude.get(0));
        mapController.setCenter(startPoint);
        mapController.setZoom(16.0);
        for(int i = 0; i < latitude.size(); i++) {
            GeoPoint geoPoint = new GeoPoint(latitude.get(i), longitude.get(i));
            polyline.addPoint(geoPoint);
        }
        map.getOverlays().add(polyline);
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    @Override
    public int getItemCount() {return app.getSessions().getSessions().size();}

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewTitleRV;
        public TextView textViewDistanceValue;
        public TextView textViewDurationValue;
        public MapView map;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitleRV = itemView.findViewById(R.id.textViewTitleRV);
            textViewDistanceValue = itemView.findViewById(R.id.textViewDistanceValue);
            textViewDurationValue = itemView.findViewById(R.id.textViewDurationValue);
            map = itemView.findViewById(R.id.mapRV);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(listener != null) {
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(itemView, position);
                        }
                    }
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if(listener != null) {
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION) {
                            listener.onItemLongClick(itemView, position);
                            return true;
                        }
                    }
                    return false;
                }
            });
        }
    }
    public interface OnItemClickListener {
        void onItemClick(View itemView, int position);
        void onItemLongClick(View itemView, int position);
    }
}
