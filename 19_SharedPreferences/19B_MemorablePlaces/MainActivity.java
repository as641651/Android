package com.aravindsankaran.memorableplaces;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    static ArrayList<String> places;
    static ArrayList<LatLng> latlngs;
    static SharedPreferences sharedPreferences;
    static ListView listview;
    private static ArrayAdapter arrayAdapter;

    public void openMap(View view){
        Intent intent = new Intent(this,MapsActivity.class);
        intent.putExtra("placenumber",-1);
        startActivity(intent);
    }

    public void clearAll(View view){
        sharedPreferences.edit().clear().apply();

        //re create array as places array has changed. and reset adapters
        places = new ArrayList<>();
        latlngs = new ArrayList<>();
        arrayAdapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,places);
        listview.setAdapter(arrayAdapter);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = this.getSharedPreferences(getPackageName(),Context.MODE_PRIVATE);
        //sharedPreferences.edit().clear().apply();

        loadHistory();

        listview = (ListView) findViewById(R.id.listView);
        arrayAdapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,places);

        listview.setAdapter(arrayAdapter);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(),MapsActivity.class);
                intent.putExtra("placenumber",position);
                startActivity(intent);
            }
        });



    }

    void loadHistory(){
        try {
            places = (ArrayList<String>)ObjectSerializer.deserialize(sharedPreferences.getString("places",ObjectSerializer.serialize(new ArrayList<>())));

            //cannot serialize latlngs. So we get the coors seprately and store them
            ArrayList<String> lats;
            ArrayList<String> lons;
            lats = (ArrayList<String>)ObjectSerializer.deserialize(sharedPreferences.getString("lats",ObjectSerializer.serialize(new ArrayList<>())));
            lons = (ArrayList<String>)ObjectSerializer.deserialize(sharedPreferences.getString("lons",ObjectSerializer.serialize(new ArrayList<>())));

            //re create latlngs with coords
            latlngs = new ArrayList<>();
            for(int i=0;i<lats.size();i++){
                latlngs.add(new LatLng(Double.valueOf(lats.get(i)),Double.valueOf(lons.get(i))));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void updateListView(){
        arrayAdapter.notifyDataSetChanged();
    }

    public static void updateSharedPreferences(){
        try {
            sharedPreferences.edit().putString("places",ObjectSerializer.serialize(places)).apply();

            ArrayList<String> lats = new ArrayList<>();
            ArrayList<String> lons = new ArrayList<>();

            for(LatLng coords : latlngs){
                lats.add(String.valueOf(coords.latitude));
                lons.add(String.valueOf(coords.longitude));
            }
            sharedPreferences.edit().putString("lats",ObjectSerializer.serialize(lats)).apply();
            sharedPreferences.edit().putString("lons",ObjectSerializer.serialize(lons)).apply();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
