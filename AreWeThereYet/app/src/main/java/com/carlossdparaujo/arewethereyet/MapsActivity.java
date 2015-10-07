package com.carlossdparaujo.arewethereyet;

import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks {

    private GoogleMap map;
    private GoogleApiClient apiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        addMapToActivity();
    }

    private void addMapToActivity() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        createAndConnectToGoogleApiClient();
    }

    private void createAndConnectToGoogleApiClient() {
        apiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .build();

        apiClient.connect();
    }

    @Override
    public void onConnectionSuspended(int cause) {

    }

    @Override
    public void onConnected(Bundle connectionHint) {
        Location userLocation = LocationServices.FusedLocationApi.getLastLocation(apiClient);
        moveMapCameraToUserLastKnownLocation(userLocation);
    }

    private void moveMapCameraToUserLastKnownLocation(Location userLocation) {
        LatLng userLatituteAndLongitute = new LatLng(userLocation.getLatitude(), userLocation.getLongitude());
        map.addMarker(new MarkerOptions().position(userLatituteAndLongitute).title("You are here!"));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(userLatituteAndLongitute, 14));
    }
}
