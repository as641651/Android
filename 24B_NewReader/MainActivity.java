package com.aravindsankaran.newsreader;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    ListView listView;
    ArrayList<String> titles;
    static ArrayList<String> articleUrls;
    static ArrayList<String> articleIDs;
    ArrayAdapter<String> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView) findViewById(R.id.listView);
        titles = new ArrayList<>();
        articleUrls = new ArrayList<>();
        articleIDs = new ArrayList<>();

        //populate articles
        if(isConnected()) {

            DownloadTask task = new DownloadTask();

            try {
                String result = task.execute("https://hacker-news.firebaseio.com/v0/topstories.json?print=pretty").get();
                Log.i("Result", result);

                JSONArray arr = new JSONArray(result);
                int max_article = 10;
                if (arr.length() < max_article)
                    max_article = arr.length();

                for (int i = 0; i < max_article; i++) {
                    String articleId = arr.getString(i);
                    String articleInfoURL = "https://hacker-news.firebaseio.com/v0/item/" + articleId + ".json?print=pretty";

                    DownloadTask articleTask = new DownloadTask();
                    String articleInfo = articleTask.execute(articleInfoURL).get();

                    JSONObject jsonObject = new JSONObject(articleInfo);
                    if (!jsonObject.isNull("title") && !jsonObject.isNull("url")) {
                        titles.add(jsonObject.getString("title"));
                        articleUrls.add(jsonObject.getString("url"));
                        articleIDs.add(jsonObject.getString("id"));
                    }
                }
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            saveToDB();
        }else{
            Toast.makeText(getApplicationContext(),"No internet",Toast.LENGTH_LONG).show();
            loadFromDB();
        }
        arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,titles);
        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(),WebActivity.class);
                intent.putExtra("position",position);
                intent.putExtra("id",articleIDs.get(position));
                startActivity(intent);
            }
        });

    }

    class DownloadTask extends AsyncTask<String,Void,String>{

        @Override
        protected String doInBackground(String... strings) {

            String result = getContentStringFromURL(strings[0]);

            return result;
        }

        String getContentStringFromURL(String urlStr){
            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;

            try{
                url = new URL(urlStr);
                urlConnection = (HttpURLConnection)url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);

                int data = reader.read();

                while(data!=-1){
                    char current = (char)data;
                    result+=current;
                    data = reader.read();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        }
    }


    void saveToDB(){

        SQLiteDatabase sqLiteDatabase = getApplicationContext().openOrCreateDatabase("history",Context.MODE_PRIVATE,null);

        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS cache (id VARCHAR,title VARCHAR, url VARCHAR)");
        sqLiteDatabase.execSQL("DELETE FROM cache");

        String sql = "INSERT INTO cache (id,title,url) VALUES (? , ? , ?)";
        SQLiteStatement statement = sqLiteDatabase.compileStatement(sql);

        for(int i=0;i<articleIDs.size();i++){
            statement.bindString(1,articleIDs.get(i));
            statement.bindString(2,titles.get(i));
            statement.bindString(3,articleUrls.get(i));
            statement.execute();
        }

        Log.i("DB","Saved DB");
    }

    void loadFromDB(){
        try {
            SQLiteDatabase sqLiteDatabase = getApplicationContext().openOrCreateDatabase("history", Context.MODE_PRIVATE, null);
            Cursor c = sqLiteDatabase.rawQuery("SELECT * FROM cache", null);
            int idIndex = c.getColumnIndex("id");
            int titleIndex = c.getColumnIndex("title");
            int urlIndex = c.getColumnIndex("url");
            while (c.moveToNext()) {
                articleIDs.add(c.getString(idIndex));
                titles.add(c.getString(titleIndex));
                articleUrls.add(c.getString(urlIndex));
            }
            Log.i("DB", "Loaded titles");
        }catch (SQLException e){
            e.printStackTrace();
            Toast.makeText(getApplicationContext(),"No Internet and No data in cache",Toast.LENGTH_LONG).show();
        }
    }

    boolean isConnected(){
        ConnectivityManager cm =
                (ConnectivityManager)getApplicationContext().getSystemService(getApplicationContext().CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean connection = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        return connection;
    }

    //Create settings menu to clear FileDirectory
    void clearArchieves(){
        for(File child : getFilesDir().listFiles()){
            if(child.getAbsolutePath().endsWith(".mht"))
                child.delete();
        }
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

        if(item.getItemId() == R.id.clearCache){
            clearArchieves();
            return true;
        }
        return false;

    }
}
