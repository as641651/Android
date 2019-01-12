package com.aravindsankaran.sql;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {

            //this.deleteDatabase("Users");

            SQLiteDatabase sqLiteDatabase = this.openOrCreateDatabase("Users", Context.MODE_PRIVATE, null);

            //Storing data
            //comment after first execution
            //NOTE: Database names and table names are different
            //sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS users (name VARCHAR,age INT(3))");
            //sqLiteDatabase.execSQL("INSERT INTO users (name,age) VALUES ('ABC',34)");
            //sqLiteDatabase.execSQL("INSERT INTO users (name,age) VALUES ('XYZ',4)");

            //delete items
            sqLiteDatabase.execSQL("INSERT INTO users (name,age) VALUES ('UVW',40)");
            sqLiteDatabase.execSQL("INSERT INTO users (name,age) VALUES ('UVW',40)");
            //IF more than one UVW are present, all are deleted. CAnt use LIMIT with DELETE in sqllite
            sqLiteDatabase.execSQL("DELETE FROM users WHERE name = 'UVW'");

            //update operation
            sqLiteDatabase.execSQL("UPDATE users SET age=2 WHERE name='XYZ'");

            //table with primary keys. These keys are updated everytime we add new entry
            sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS newusers (name VARCHAR, age INT(3), id INT PRIMARY KEY)");
            //sqLiteDatabase.execSQL("INSERT INTO newusers (name,age) VALUES ('ABC',34)");
            //sqLiteDatabase.execSQL("INSERT INTO newusers (name,age) VALUES ('XYZ',4)");

            //Retrieving data

            Cursor c = sqLiteDatabase.rawQuery("SELECT * FROM users", null);
            //Cursor c = sqLiteDatabase.rawQuery("SELECT * FROM users WHERE age < 18", null);
            //Cursor c = sqLiteDatabase.rawQuery("SELECT * FROM users WHERE name = 'kk' AND age = 22", null);
            //Find rows with name starting with A and limit results to 1
            //Cursor c = sqLiteDatabase.rawQuery("SELECT * FROM users WHERE name LIKE 'A%' LIMIT 1", null);

            int nameIndex = c.getColumnIndex("name");
            int ageIndex = c.getColumnIndex("age");

            //The cursor starts before the first result row,
            // so on the first iteration this moves to the first result if it exists
            while (c.moveToNext()) {
                Log.i("name ", c.getString(nameIndex));
                Log.i("age ", String.valueOf(c.getInt(ageIndex)));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
