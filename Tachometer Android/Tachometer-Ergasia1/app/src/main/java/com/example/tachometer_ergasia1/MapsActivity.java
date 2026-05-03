package com.example.tachometer_ergasia1;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements GoogleMap.OnMyLocationButtonClickListener,GoogleMap.OnMyLocationClickListener,OnMapReadyCallback {
    SQLiteDatabase db;
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        db = openOrCreateDatabase("SpeedRecords", MODE_PRIVATE, null);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng defaultcamera = new LatLng(37.983810, 23.727539);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(defaultcamera));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(defaultcamera, 10));
        Cursor cursor = db.rawQuery("SELECT * FROM Records", null);
        if (cursor.getCount() == 0) {
            Toast.makeText(this, "There is no marker yet", Toast.LENGTH_SHORT).show();
        } else {
            for (int i = 0; i < cursor.getCount(); i++) {
                while (cursor.moveToNext()) {
                    LatLng location = new LatLng(cursor.getDouble(1), cursor.getDouble(0));
                    mMap.addMarker(new MarkerOptions().position(location).title(cursor.getString(3)));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(location));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 10));
                }
            }
        }
        //Κουμπί για την τοποθεσία του χρήστη
        mMap.setMyLocationEnabled(true);
        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMyLocationClickListener(this);
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        Toast.makeText(this, "Current location:\n" + location, Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "Searching for your location", Toast.LENGTH_SHORT).show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }
}
