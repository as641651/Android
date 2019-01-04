package com.aravindsankaran.currencyconverter;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    public void clickFunction(View view){
        EditText usd = (EditText) findViewById(R.id.editText);
        TextView inr = (TextView) findViewById(R.id.editText2);

        //Use try catch
        if(!usd.getText().toString().isEmpty()) {
            Double usd_d = Double.parseDouble(usd.getText().toString());
            Double inr_d = usd_d * 69.64;

            Double roundTwoDigitsInr = Math.round(inr_d * 100.0) / 100.0;

            inr.setText(roundTwoDigitsInr.toString() + " INR");

            //Another way to roundoff
            //inr.setText(String.format("%.2f",inr_d) + " INR");
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
