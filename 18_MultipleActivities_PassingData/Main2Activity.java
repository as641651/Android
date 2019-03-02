package com.aravindsankaran.multipleactivities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class Main2Activity extends AppCompatActivity {

    public void toFirstActivity(View view){
        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        //Get the intent that got to this activity
        Intent  intent = getIntent();
        //get the variable from other activity
        Toast.makeText(this,intent.getStringExtra("username"),Toast.LENGTH_LONG).show();
    }
}
