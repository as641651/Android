package com.aravindsankaran.higherorlower;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    int randomNumer; //forward declaration. This variable will be set up onCreate method

    //refactor reused code into a method
    private void makeToast(String str){
        Toast.makeText(MainActivity.this,str,Toast.LENGTH_LONG).show();
    }

    public void clickFunction(View view){
        EditText guessText = (EditText) findViewById(R.id.editText);

        if (!guessText.getText().toString().isEmpty()){
            Integer guess_i = Integer.parseInt(guessText.getText().toString());

            if (guess_i < randomNumer)
                makeToast("Your guess is lower");
            else if (guess_i > randomNumer)
                makeToast("Your guess is higher");
            else
                makeToast("You are RIGHT!");

        }
    }

    //All code that must be executed on start up comes here
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Generate random number between 1 and 20
        Random rand = new Random();
        randomNumer = rand.nextInt(20) + 1;


    }
}
