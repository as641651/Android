package com.aravindsankaran.memorableplaces;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;


    String getAddress(LatLng latlng) throws IOException {
        String addressStr = "";

        Geocoder geocoder = new Geocoder(MapsActivity.this, Locale.getDefault());

        List<Address> listAddresses = geocoder.getFromLocation(latlng.latitude,latlng.longitude,1);

        if(listAddresses != null && listAddresses.size() > 0){
            if(listAddresses.get(0).getThoroughfare() != null)
                addressStr += listAddresses.get(0).getThoroughfare() + ",";
            if(listAddresses.get(0).getLocality() != null)
                addressStr += listAddresses.get(0).getLocality() + ", ";
            if(listAddresses.get(0).getPostalCode() != null)
                addressStr += listAddresses.get(0).getPostalCode() + ", ";
            if(listAddresses.get(0).getCountryName() != null)
                addressStr += listAddresses.get(0).getCountryName();
        }
        return addressStr;
    }

    void updateMapMarkerCamera(LatLng latLng,String address){
        mMap.clear();
        mMap.addMarker( new MarkerOptions().position(latLng).title(address)).showInfoWindow();
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
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

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                String addr = "";
                try {
                    addr = getAddress(latLng);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                //Add date time if address is still empty
                if(addr == ""){
                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm dd/MM/yyyy");
                    addr = sdf.format(new Date());
                }

                updateMapMarkerCamera(latLng,addr);

                //update arrays in main activity
                MainActivity.places.add(addr);
                MainActivity.latlngs.add(latLng);
                MainActivity.updateListView();
                MainActivity.updateSharedPreferences();

            }
        });


        Intent intent = getIntent();
        int position = intent.getIntExtra("placenumber",-1);

        if(position == -1){
            //if add new place is clicked
            updateMapMarkerCamera(new LatLng(13,80),"Chennai");
        }else{
            //if any places are clicked
            updateMapMarkerCamera(MainActivity.latlngs.get(position),MainActivity.places.get(position));
        }
    }
}
