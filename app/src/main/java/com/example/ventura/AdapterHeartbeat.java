package com.example.ventura;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.osmdroid.views.MapView;

import java.text.SimpleDateFormat;
import java.util.Date;

public class AdapterHeartbeat extends RecyclerView.Adapter<AdapterHeartbeat.ViewHolder>{
    private MyApplication app;
    private Heartbeats heartbeats;

    public AdapterHeartbeat(MyApplication app, Heartbeats heartbeats) {
        this.app = app;
        this.heartbeats = heartbeats;
    }

    @NonNull
    @Override
    public AdapterHeartbeat.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.heartbeatrecycleview_layout, parent, false);
        AdapterHeartbeat.ViewHolder viewHolder = new AdapterHeartbeat.ViewHolder(view);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull AdapterHeartbeat.ViewHolder holder, int position) {
        Heartbeat h = app.getHeartbeats().getHeartbeatAtPos(position);
        String dateString = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date(h.getDate()));
        holder.textViewHeartbeats.setText(Integer.toString(h.getAvgHeartbeat()));
        holder.textViewDate.setText(dateString);
        Log.i("asd", "test");
    }

    @Override
    public int getItemCount() {
        return app.getHeartbeats().getNumberOfHeartbeats();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewHeartbeats;
        public TextView textViewDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewHeartbeats = itemView.findViewById(R.id.textViewHeartbeat);
            textViewDate = itemView.findViewById(R.id.textViewDate);

        }
    }
    public interface OnItemClickListener {
        void onItemClick(View itemView, int position);
        void onItemLongClick(View itemView, int position);
    }
}
