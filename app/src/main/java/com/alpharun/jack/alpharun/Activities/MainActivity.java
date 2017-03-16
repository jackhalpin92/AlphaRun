package com.alpharun.jack.alpharun.Activities;


import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;

import com.alpharun.jack.alpharun.Database.RunDbHelper;
import com.alpharun.jack.alpharun.Database.RunTrackerContract;
import com.alpharun.jack.alpharun.R;
import com.facebook.stetho.Stetho;

public class MainActivity extends AppCompatActivity{



    //Code for Permission request used to check if user has given permission for GPS
    protected static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;

    // Method to create an options menu in action bar
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);


        return true;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //Run stetho. Accessed from chrome: chrome://inspect
        Stetho.initializeWithDefaults(this);
        RunDbHelper runDbHelper = new RunDbHelper(this);

        SQLiteDatabase db = runDbHelper.getWritableDatabase();
        runDbHelper.onCreate(db);


        //Quick test for the databse
        ContentValues values = new ContentValues();
        values.put(RunTrackerContract.RunEntry.DISTANCE_COLUMN, 300);
        values.put(RunTrackerContract.RunEntry.TIME_COLUMN, 39);
        long newRowId = db.insert(RunTrackerContract.RunEntry.TABLE_NAME, null, values);

        //Create the projection. This basically is an array of the rows that you actually want to keep from the results
        String[] projection = { RunTrackerContract.RunEntry._ID, RunTrackerContract.RunEntry.TIME_COLUMN, RunTrackerContract.RunEntry.DISTANCE_COLUMN };

        Cursor cursor = db.query(
                RunTrackerContract.RunEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null
        );

        while (cursor.moveToNext()){
            long itemid = cursor.getLong(cursor.getColumnIndexOrThrow(RunTrackerContract.RunEntry._ID));
            Log.e("itemid: ", Long.toString(itemid));
        }

        //Before the app runs we want to make sure that the app has the permission to track location.
        //For the sake of running accuracy we're going to be using FINE_LOCATION
        //Android 23 and onwards: Need to check permissions at runtime.
        int permissionCheck = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION);


        //TODO: Hande the event that the user denies access to GPS
        //If the permission is denied we have to request it.
        if (permissionCheck == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }

        //Get the database ready
        //DBManager db = new DBManager();

    }

    public void startRunCallback(View view){
        Intent intent = new Intent(this, RunActivity.class);
        startActivity(intent);
    }
}
