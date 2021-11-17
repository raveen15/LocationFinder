package com.example.assignment2;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;
import java.util.Locale;

public class ViewLocation extends AppCompatActivity {
    private EditText latitudeEdit, longitudeEdit;
    private Location alreadyAvailableLocation;
    private Location location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_location);

        latitudeEdit = findViewById(R.id.latitudeEdit);
        longitudeEdit = findViewById(R.id.longitudeEdit);

        FloatingActionButton saveLocation = (FloatingActionButton) findViewById(R.id.saveLocation);
        saveLocation.setOnClickListener(v -> saveLocation());

        FloatingActionButton deleteLocation = (FloatingActionButton) findViewById(R.id.deleteButton);
        deleteLocation.setOnClickListener(v -> alertMessage());

        if(getIntent().getBooleanExtra("isViewOrUpdate",false)){
            alreadyAvailableLocation = (Location) getIntent().getSerializableExtra("location");
            setViewOrUpdateLocation();
        }

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            location = (Location) extras.get("location");
        }
    }

    private void setViewOrUpdateLocation() {
        latitudeEdit.setText((alreadyAvailableLocation.getLatitude()));
        longitudeEdit.setText((alreadyAvailableLocation.getLongitude()));
    }

    private void saveLocation(){
        EditText latitudeEdit = (EditText) findViewById(R.id.latitudeEdit);
        EditText longitudeEdit = (EditText) findViewById(R.id.longitudeEdit);

        String latitude = latitudeEdit.getText().toString();
        String longitude = longitudeEdit.getText().toString();

        final Location location = new Location();
        location.setLatitude(latitude);
        location.setLongitude(longitude);
        location.setAddress(addressGenerator(latitude, longitude));

        if(alreadyAvailableLocation != null){
            location.setId(alreadyAvailableLocation.getId());
        }

        @SuppressLint("StaticFieldLeak")
        class SaveLocationTask extends AsyncTask<Void, Void, Void> {
            @Override
            protected Void doInBackground(Void... voids) {
                LocationDatabase.getLocationDatabase(getApplicationContext()).locationDao().insert(location);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
                Intent intentTwo = new Intent(getApplicationContext(), MainActivity.class);
                startActivityForResult(intentTwo, 1);
            }
        }

        new SaveLocationTask().execute();
    }

    private String addressGenerator(String latitude, String longitude){
        Double latitudeNum = Double.parseDouble(latitude);
        Double longitudeNum = Double.parseDouble(longitude);
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        String strAddress = "";
        try {
            List<Address> addresses = geocoder.getFromLocation(latitudeNum, longitudeNum, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                strAddress = strReturnedAddress.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return strAddress;
    }
    private void deleteLocation() {

        @SuppressLint("StaticFieldLeak")
        class DeleteLocationTask extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... voids) {
                return LocationDatabase.getLocationDatabase(getApplicationContext()).locationDao().delete(location);
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                Intent intent = new Intent();
                intent.putExtra("isLocationDeleted", true);
                setResult(RESULT_OK, intent);
            }
        }

        new DeleteLocationTask().execute();
    }
    public void alertMessage(){
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        deleteLocation();
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivityForResult(intent, 1);
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        // No button clicked
                        // do nothing
                        Toast.makeText(ViewLocation.this, "No Clicked",
                                Toast.LENGTH_LONG).show();
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure?")
                .setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }
}