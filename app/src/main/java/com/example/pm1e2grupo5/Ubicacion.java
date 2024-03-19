package com.example.pm1e2grupo5;

import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class Ubicacion extends AppCompatActivity implements OnMapReadyCallback {
    private final int FINE_PERMISSION_CODE = 1;
    private GoogleMap myMap;
    Button abrirGoogleMaps;
    Float latitude, longitude;
    String contacto;
    Location currentLocation;
    FusedLocationProviderClient fusedLocationProviderClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Retrieve the content view that renders the map.
        setContentView(R.layout.activity_ubicacion);
        abrirGoogleMaps = findViewById(R.id.irGoogleMaps);

        Intent intent = getIntent();
        if (intent.hasExtra("contactoId")) {
           latitude = Float.valueOf(intent.getStringExtra("latitud"));
           longitude = Float.valueOf(intent.getStringExtra("longitud"));
           contacto =String.valueOf(intent.getStringExtra("nombre"));
        }
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // Get the SupportMapFragment and request notification when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        abrirGoogleMaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String label = "Ubicacion de " + contacto;
                Uri gmmIntentUri = Uri.parse("geo:" + latitude + "," + longitude + "?q=" + latitude + "," + longitude + "(" + label + ")");
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");

                if (mapIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(mapIntent);
                } else {
                    Toast.makeText(Ubicacion.this, "No se pudo abrir Google Maps", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        myMap = googleMap;
        if (latitude != null && longitude != null) {
            LatLng currentLatLng = new LatLng(latitude, longitude);
            myMap.addMarker(new MarkerOptions().position(currentLatLng).title("Mi Ubicación"));
            myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 10)); // Ajusta el nivel de zoom según sea necesario
        }
    }

}