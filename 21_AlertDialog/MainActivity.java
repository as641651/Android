package com.aravindsankaran.alert;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {

    String preferedLang;
    TextView langText;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = this.getSharedPreferences(getPackageName(),Context.MODE_PRIVATE);

        //sharedPreferences.edit().clear().apply();

        preferedLang = sharedPreferences.getString("language","");

        langText = (TextView) findViewById(R.id.textView);

        if(preferedLang == ""){
            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_btn_speak_now)
                    .setTitle("Preferred language.")
                    .setMessage("Choose your language")
                    .setPositiveButton("English", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            setLanguage("English");
                        }
                    })
                    .setNegativeButton("Spanish", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            setLanguage("Spanish");
                        }
                    })
                    .show();
        }else{
            langText.setText(preferedLang);
        }

    }

    void setLanguage(String language){
        preferedLang = language;
        sharedPreferences.edit().putString("language",preferedLang).apply();
        langText.setText(preferedLang);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        if(item.getItemId()==R.id.english)
            setLanguage("English");
        else if(item.getItemId()==R.id.spanish)
            setLanguage("Spanish");
        else
            return false;

        return true;
    }
}
