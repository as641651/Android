package com.aravindsankaran.braintrainer;

import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    Handler handler;
    Runnable run;
    int correctOpt;
    int score = 0;
    int attempt = 0;
    CountDownTimer cntTimer;
    TextView finalScore;

    boolean gameActive = false;

    void setScore(){
        TextView scoreView = (TextView)findViewById(R.id.score);
        scoreView.setText(Integer.toString(score) + "/" + Integer.toString(attempt));
    }

    void showFinalScore(){
        finalScore.setText("Your Score is " + Integer.toString(score) + "/" + Integer.toString(attempt));
        finalScore.setVisibility(View.VISIBLE);
    }

    public void playFunction(View view){

        Button btn = (Button) findViewById(R.id.play);


        if(!gameActive) {

            gameActive = true;
            finalScore.setVisibility(View.INVISIBLE);

            score = 0;
            attempt = 0;
            setScore();

            handler.post(run);


            btn.setText("Quit");



            cntTimer.start();

        }else{
            cntTimer.cancel();
            gameActive = false;
            showFinalScore();
            btn.setText("Play again");
        }
    }

    public void checkAnswer(View view){
        if(gameActive){
            int ansGiven = Integer.parseInt(view.getTag().toString());

            if(ansGiven == correctOpt)
                score +=1;
            attempt +=1;

            setScore();

            handler.post(run);
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final TextView question = (TextView)findViewById(R.id.question);
        final View rootBtnLayout = findViewById(R.id.btnroot);

        finalScore = (TextView)findViewById(R.id.finalscore);
        finalScore.setVisibility(View.INVISIBLE);

        //could have simply used a function to generate question
        handler = new Handler();
        run = new Runnable() {
            @Override
            public void run() {
                Random random = new Random();
                int r1 = random.nextInt(99) + 1;
                int r2 = random.nextInt(99) + 1;
                question.setText(Integer.toString(r1) + "+" + Integer.toString(r2));

                int ans = r1 + r2;

                //set the correct answer to a random button
                correctOpt = random.nextInt(4) + 1;
                Log.i("Correct answer ",Integer.toString(correctOpt));

                //find the buttons with tag
                Button cbtn = (Button) rootBtnLayout.findViewWithTag(Integer.toString(correctOpt));
                cbtn.setText(Integer.toString(ans));

                for(int i=1;i<=4;i++){
                    if(i!=correctOpt){
                        Button btn = (Button) rootBtnLayout.findViewWithTag(Integer.toString(i));

                        //set other options +- correct answers
                        int minAns = ans - 50;
                        if(ans < 50)
                            minAns = ans;

                        btn.setText(Integer.toString(random.nextInt(ans+50-minAns) + minAns));
                    }
                }


            }
        };

        cntTimer = new CountDownTimer(30000, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                TextView timer = (TextView) findViewById(R.id.timer);
                String timeString = Integer.toString((int) millisUntilFinished/1000) + " s";
                timer.setText(timeString);
            }

            @Override
            public void onFinish() {
                gameActive=false;
                showFinalScore();
                Button btn = (Button) findViewById(R.id.play);
                btn.setText("Play again");

            }
        };

    }
}
