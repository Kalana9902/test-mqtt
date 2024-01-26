package com.example.mqtt_subscriber;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;

import android.os.Bundle;
import android.util.Log;
import android.view.View;


public class MainActivity extends AppCompatActivity {

    private  DatabaseHelper databaseHelper;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        databaseHelper = new DatabaseHelper(this);
        cleanLocalStorage();

    }

    @Override
    protected void onDestroy() {
        cleanLocalStorage();
        super.onDestroy();
    }

    // Navigating to a Mqtt Data Page
    public void navigateToMqttData(View view) {
        Log.d("click", "navigateToSecondActivity: button clicked");
        Intent intent = new Intent(this, MqttData.class);
        startActivity(intent);
    }

    // Navigating to a Bluetooth Device Page
    public void navigateToBluetoothDevice(View view){
        Intent intent = new Intent(this, BLEScanner.class);
        startActivity(intent);
    }

    private void cleanLocalStorage() {
        // Perform cleanup tasks for local storage
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        // Delete data
        db.delete(DatabaseHelper.TABLE_NAME, null, null);
        db.close();
    }
}