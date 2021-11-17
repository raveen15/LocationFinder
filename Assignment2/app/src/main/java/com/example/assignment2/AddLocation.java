package com.example.assignment2;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.EditText;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;
import java.util.Locale;

public class AddLocation extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_location);

        FloatingActionButton saveLocation = (FloatingActionButton) findViewById(R.id.saveLocation);
        saveLocation.setOnClickListener(v -> saveLocation());
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

        @SuppressLint("StaticFieldLeak")
        class SaveLocationTask extends AsyncTask<Void, Void, Void> {
            @Override
            protected Void doInBackground(Void... voids) {
                LocationDatabase.getLocationDatabase(getApplicationContext()).locationDao().insertAll(location);
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
}