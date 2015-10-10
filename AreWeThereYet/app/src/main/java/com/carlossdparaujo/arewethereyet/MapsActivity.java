package com.carlossdparaujo.arewethereyet;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, TextView.OnEditorActionListener {

    private GoogleMap map;
    private GoogleApiClient apiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        addMapToActivity();
        setSearchBarEditListener();
    }

    private void addMapToActivity() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        moveMyLocationButtonToBottomOfScreen(getMyLocationButton(mapFragment));
    }

    private View getMyLocationButton(SupportMapFragment mapFragment) {
        // These Ids are the default for map fragment structure
        return ((View) mapFragment.getView().findViewById(1).getParent()).findViewById(2);
    }

    private void moveMyLocationButtonToBottomOfScreen(View locationButton) {
        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
        rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        rlp.setMargins(0, 0, 30, 30);
    }

    private void setSearchBarEditListener() {
        EditText searchBar = (EditText) findViewById(R.id.searchAddressTextField);
        searchBar.setImeActionLabel("Search", KeyEvent.KEYCODE_ENTER);
        searchBar.setRawInputType(InputType.TYPE_CLASS_TEXT);
        searchBar.setImeOptions(EditorInfo.IME_ACTION_GO);
        searchBar.setOnEditorActionListener(this);
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
        moveAndZoomMapCameraToLocation(userLocation);
        trackUserLocationOnMap();
    }

    private void moveAndZoomMapCameraToLocation(Location location) {
        LatLng userLatituteAndLongitute = new LatLng(location.getLatitude(), location.getLongitude());
        moveAndZoomMapCameraToLatLng(userLatituteAndLongitute);
    }

    private void moveAndZoomMapCameraToLatLng(LatLng latLng) {
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14));
    }

    private void trackUserLocationOnMap() {
        map.setMyLocationEnabled(true);
    }

    @Override
    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        List<Address> results = getSearchedAddressResults(textView.getText().toString());

        if (!results.isEmpty()) {
            setMarkerAndZoomOnFirstAddressFound(results);
        }

        textView.clearFocus();

        return true;
    }

    private List<Address> getSearchedAddressResults(String searchedAddress) {
        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            return geocoder.getFromLocationName(searchedAddress, 1);
        } catch (IOException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    private void setMarkerAndZoomOnFirstAddressFound(List<Address> addressList) {
        LatLng addressLatLng = getAddressLatLng(addressList.get(0));
        map.addMarker(new MarkerOptions().position(addressLatLng).title("Destination!"));
        moveAndZoomMapCameraToLatLng(addressLatLng);
    }

    private LatLng getAddressLatLng(Address address) {
        return new LatLng(address.getLatitude(), address.getLongitude());
    }
}
