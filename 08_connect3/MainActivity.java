package com.aravindsankaran.connect3;

import android.annotation.TargetApi;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {


    //0 : red, 1: yellow
    int activePlayer = 0;

    boolean gameActive = true;

    //maintain memory state for each slot
    //2: not player, 1: yellow, 0: red
    int[] gameState = {2,2,2,2,2,2,2,2,2};

    int[][] winningPositions =
            {{0,1,2},{3,4,5},{6,7,8},
            {0,3,6},{1,4,7},{2,5,8},
            {0,4,8},{2,4,6}};

    boolean checkWin(){
        for(int[] wp : winningPositions){
            if(gameState[wp[0]] == gameState[wp[1]] && gameState[wp[1]] == gameState[wp[2]]){
                if(gameState[wp[0]]!=2){

                    Button playAgain = (Button) findViewById(R.id.button39);
                    playAgain.setVisibility(View.VISIBLE);
                    gameActive = false;
                    return true;
                }
            }

        }
        return false;
    }

    boolean checkDraw(){
        for( int t : gameState)
            if(t==2)
                return false;

        Button playAgain = (Button) findViewById(R.id.button39);
        playAgain.setVisibility(View.VISIBLE);
        gameActive = false;
        return true;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void clickFunction(View view){

        //Since the click corresponds to current view, we dont have to grab by id
        ImageView img = (ImageView) view;

        int tappedCounter = Integer.parseInt(img.getTag().toString());

        //To stop from playing on the same spot and when game is finished
        if(gameState[tappedCounter]==2 && gameActive) {

            //update memory to player id
            gameState[tappedCounter] = activePlayer;

            final TextView turnTxt = (TextView) findViewById(R.id.textView20);

            img.setScaleX(0f);
            img.setScaleY(0f);

            if (activePlayer == 0) {
                img.setImageResource(R.drawable.redcoin);
                activePlayer = 1;
            } else {
                img.setImageResource(R.drawable.yellowcoin);
                activePlayer = 0;
            }


            img.animate().scaleX(1f).scaleY(1f).rotation(360f).setDuration(1000).withEndAction(new Runnable() {
                @Override
                public void run() {

                    boolean won = checkWin();
                    boolean draw = checkDraw();

                    if(won){
                        turnTxt.setText("WON");
                        turnTxt.setTextColor(Color.GREEN);
                    }else if(draw){
                        turnTxt.setText("DRAW");
                        turnTxt.setTextColor(Color.DKGRAY);
                    }
                    else if (activePlayer == 1) {
                        turnTxt.setText("YELLOW");
                        turnTxt.setTextColor(Color.rgb(168, 112, 10));
                    } else {
                        turnTxt.setText("RED");
                        turnTxt.setTextColor(Color.RED);
                    }
                }
            });

        }
    }

    public void playAgain(View view){
        View root = findViewById(R.id.linearLayout);

        ArrayList<View> out = new ArrayList<>();

        root.findViewsWithText(out,"img",View.FIND_VIEWS_WITH_CONTENT_DESCRIPTION);

        for(View im : out){
            ((ImageView) im).setImageResource(0);
        }

        view.setVisibility(View.GONE);

        //Reset game state

        for(int i=0; i< gameState.length;i++ )
            gameState[i] = 2;

        activePlayer = 0;
        gameActive = true;

        TextView turnTxt = (TextView) findViewById(R.id.textView20);
        turnTxt.setText("RED");
        turnTxt.setTextColor(Color.RED);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


}
