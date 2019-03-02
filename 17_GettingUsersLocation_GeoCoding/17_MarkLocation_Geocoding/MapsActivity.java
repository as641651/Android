package com.aravindsankaran.userlocation;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.jar.Manifest;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    LocationManager locationManager;
    LocationListener locationListener;

    void updateMapMarker(LatLng deviceLocation){
        mMap.clear();//remove previous markers
        mMap.addMarker(new MarkerOptions().position(deviceLocation).title("Device location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(deviceLocation));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                Log.i("Location ",location.toString());

                // Add a marker to current location
                LatLng deviceLocation = new LatLng(location.getLatitude(),location.getLongitude());
                updateMapMarker(deviceLocation);

                //Geocoding
                Geocoder geocoder = new Geocoder(MapsActivity.this, Locale.getDefault());

                try {
                    //get address corresponding to coords. Limit max address to 1.
                    List<Address> listAddresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);

                    if(listAddresses != null && listAddresses.size() > 0){
                        Log.i("Place info ", listAddresses.get(0).toString());

                        //make the address look ordered
                        String addressStr = "";

                        if(listAddresses.get(0).getSubThoroughfare() != null)
                            addressStr += listAddresses.get(0).getSubThoroughfare() + " ";
                        if(listAddresses.get(0).getThoroughfare() != null)
                            addressStr += listAddresses.get(0).getThoroughfare() + ",";
                        if(listAddresses.get(0).getLocality() != null)
                            addressStr += listAddresses.get(0).getLocality() + ", ";
                        if(listAddresses.get(0).getPostalCode() != null)
                            addressStr += listAddresses.get(0).getPostalCode() + ", ";
                        if(listAddresses.get(0).getCountryName() != null)
                            addressStr += listAddresses.get(0).getCountryName();

                        Toast.makeText(MapsActivity.this,addressStr,Toast.LENGTH_LONG).show();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        if(Build.VERSION.SDK_INT < 23){
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,10,locationListener);
        }else{
            if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this,new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},1);
            }else{
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,10,locationListener);

                // If the permission was already there, the listener may not be called and marker may not be set
                //so we get last known location
                Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                LatLng userLocation = new LatLng(lastKnownLocation.getLatitude(),lastKnownLocation.getLongitude());
                updateMapMarker(userLocation);
            }
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == 1){
            if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,10,locationListener);
        }
    }
}
