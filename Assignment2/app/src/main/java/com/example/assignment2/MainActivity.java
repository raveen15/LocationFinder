package com.example.assignment2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.SearchView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LocationListener{

    private RecyclerView rv;
    private List<Location> locationList;
    private LocationAdapter locationAdapter;
    private int locationClickedPosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton addLocation = (FloatingActionButton) findViewById(R.id.newLocation);

        addLocation.setOnClickListener(v -> startActivityForResult(new Intent(
                getApplicationContext(), AddLocation.class), 1)
        );

        rv = findViewById(R.id.recyclerView);
        rv.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        );

        locationList = new ArrayList<>();
        locationAdapter = new LocationAdapter(locationList, this);
        rv.setAdapter(locationAdapter);

        getLocations(1);

        EditText inputSearch = findViewById(R.id.inputSearch);
        inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                locationAdapter.cancelTimer();
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (locationList.size() != 0) {
                    locationAdapter.searchLocation(s.toString());
                }
            }
        });
    }

    @Override
    public void onLocationClicked(Location location, int position) {
        locationClickedPosition = position;
        Intent intent = new Intent(getApplicationContext(), ViewLocation.class);
        intent.putExtra("isViewOrUpdate",true);
        intent.putExtra("location",location);
        startActivityForResult(intent, 2);
    }

    private void getLocations(final int requestCode) {

        @SuppressLint("StaticFieldLeak")
        class GetLocationTask extends AsyncTask<Void, Void, List<Location>> {

            @Override
            protected List<Location> doInBackground(Void... voids) {
                return LocationDatabase.getLocationDatabase(getApplicationContext())
                        .locationDao().getAll();
            }

            @Override
            protected void onPostExecute(List<Location> location) {
                super.onPostExecute(location);
                if(requestCode == 1) {
                    locationList.addAll(location);
                    locationAdapter.notifyDataSetChanged();
                }else if (requestCode == 2){
                    rv.smoothScrollToPosition(0);
                }else if(requestCode == 3){
                    locationList.remove(locationClickedPosition);
                    locationList.add(locationClickedPosition, location.get(locationClickedPosition));
                    locationAdapter.notifyItemChanged(locationClickedPosition);
                }
            }
        }

        new GetLocationTask().execute();
    }
}