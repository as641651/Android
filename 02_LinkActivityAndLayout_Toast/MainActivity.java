package com.aravindsankaran.linkmainactivityandlayout;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    // On clicking button function
    public void clickFunction(View view){
        Log.i("Info","Button clicked!");

        //Getting info from user
        EditText myTextField = (EditText) findViewById(R.id.myText);

        Log.i("Name",myTextField.getText().toString());

        //Temporary Pop up
        Toast.makeText(MainActivity.this,
                "Hi " + myTextField.getText().toString(),
                Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
