package com.aravindsankaran.sharedpreferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences sharedPreferences = this.getSharedPreferences(getPackageName(),Context.MODE_PRIVATE);

        //Entering data when run for first time
        //sharedPreferences.edit().putString("username","aravind").apply();

        //retrieving data. Provide the key and a default value
        String username = sharedPreferences.getString("username","");

        Log.i("username",username);

        //saving arrays in shared preferences

        ArrayList<String> friends = new ArrayList<>();
        friends.add("AA");
        friends.add("BB");

        try {
            //arrays have to be serialized to strings. We have added a class ObjectSerializer to the project
            sharedPreferences.edit().putString("friends", ObjectSerializer.serialize(friends)).apply();
        } catch (IOException e) {
            e.printStackTrace();
        }

        ArrayList<String> newFriends =new ArrayList<>();


        try {
            //get the string and deserialize and provide default string value
            newFriends = (ArrayList<String>)ObjectSerializer.deserialize(
                    sharedPreferences.getString("friends",ObjectSerializer.serialize(new ArrayList<>())));
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.i("Friends",newFriends.toString());


    }
}
