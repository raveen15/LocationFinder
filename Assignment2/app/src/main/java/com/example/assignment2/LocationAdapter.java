package com.example.assignment2;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.LocationViewHolder> {
    private List<Location> location;
    private Timer timer;
    private List<Location> locationSource;
    private LocationListener locationListener;

    public LocationAdapter(List<Location> location, LocationListener locationListener) {
        this.location = location;
        locationSource = location;
        this.locationListener = locationListener;
    }

    @NonNull
    @Override
    public LocationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new LocationViewHolder(
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_container_location, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull LocationViewHolder holder, int position) {
        holder.setLocation(location.get(position));
        holder.layoutLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                locationListener.onLocationClicked(location.get(position), position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return location.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    static class LocationViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView textLatitude, textLongitude, textAddress;
        LinearLayout layoutLocation;

        public LocationViewHolder(@NonNull View itemView) {
            super(itemView);
            textLatitude = itemView.findViewById(R.id.textLatitude);
            textLongitude = itemView.findViewById(R.id.textLongitude);
            textAddress = itemView.findViewById(R.id.textAddress);
            layoutLocation = itemView.findViewById(R.id.layoutLocation);
        }

        void setLocation(Location location) {
            textLatitude.setText(location.getLatitude());
            textLongitude.setText(location.getLongitude());
            textAddress.setText(location.getAddress());

//            GradientDrawable gradientDrawable = (GradientDrawable) layoutLocation.getBackground();
//            gradientDrawable.setColor(Color.parseColor("#333333"));

        }

        public void onClick(View v, Location location){
            int mLocation = getLayoutPosition();
            String latitude = location.getLatitude();
            String longitude = location.getLongitude();
            String address = location.getAddress();
        }

        @Override
        public void onClick(View view) {

        }
    }

    public void searchLocation(final String searchKeyword) {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (searchKeyword.trim().isEmpty()) {
                    location = locationSource;
                } else {
                    ArrayList<Location> temp = new ArrayList<>();
                    for (Location location : locationSource) {
                        if (location.getLatitude().toLowerCase().contains(searchKeyword.toLowerCase()) ||
                                location.getLongitude().toLowerCase().contains(searchKeyword.toLowerCase()) ||
                                location.getAddress().toLowerCase().contains(searchKeyword.toLowerCase())) {
                            temp.add(location);
                        }
                    }
                    location = temp;
                }

                new Handler(Looper.getMainLooper()).post(() -> notifyDataSetChanged());
            }
        }, 500);
    }

    public void cancelTimer() {
        if (timer != null) {
            timer.cancel();
        }
    }
}
