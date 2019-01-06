package com.aravindsankaran.squareortriangular;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    class Number {
        public int number=0;

        boolean isSquare(){
            double sqrtNumber = Math.sqrt(number);
            boolean ret = false;

            if(sqrtNumber == Math.floor(sqrtNumber))
                ret = true;
            return ret;
        }

        boolean isTriangular(){

            int trinum = 0;

            for(int i=1;trinum<number;i++)
                trinum += i;

            boolean ret=false;

            if(trinum==number)
                ret=true;

            return ret;

        }
    }

    public void clickFunction(View view){
        EditText numEditText = (EditText) findViewById(R.id.editText);

        String message = "";

        Number number = new Number();

        boolean success = true;
        try {
            number.number = Integer.parseInt(numEditText.getText().toString());
        }
        catch (Exception e){
            message = "Enter a number!";
            success = false;
        }

        if(success) {
            if (number.isSquare() && number.isTriangular())
                message = number.number + " is both square and triangular";
            else if (number.isSquare())
                message = number.number + " is square";
            else if (number.isTriangular())
                message = number.number + " is triangular";
            else
                message = number.number + " is neither square nor triangular";
        }

        Toast.makeText(MainActivity.this,message,Toast.LENGTH_LONG).show();

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
