package com.akapps.etutor;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    TextView textView;
    double l1= 23.77, l2= 90.39;
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        textView = findViewById(R.id.textView36);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        sharedPreferences = this.getSharedPreferences("com.akapps.etutor", Context.MODE_PRIVATE);
        textView.setOnClickListener( v -> {
            sharedPreferences.edit().putString("p1", String.valueOf(l1)).apply();
            sharedPreferences.edit().putString("p2", String.valueOf(l2)).apply();
            finish();
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        LatLng sydney = new LatLng(23.77, 90.39);
        googleMap.addMarker(new MarkerOptions().position(sydney).title("Dhaka"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        googleMap.setOnMapClickListener(latLng -> {
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            googleMap.clear();
            googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
            googleMap.addMarker(markerOptions);
            l1 = latLng.latitude;
            l2 = latLng.longitude;
        });
    }

    @Override
    public void onBackPressed() {
        sharedPreferences.edit().putString("p1", String.valueOf(l1)).apply();
        sharedPreferences.edit().putString("p2", String.valueOf(l2)).apply();
        finish();
    }
}