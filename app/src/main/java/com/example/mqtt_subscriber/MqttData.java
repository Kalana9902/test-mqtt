package com.example.mqtt_subscriber;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class MqttData extends AppCompatActivity implements MqttHandler.MqttListener{

    private static final String TAG = "MqttData";
    private static final String BROKER_URL = "tcp://192.168.1.26:1883";
    private static final String CLIENT_ID = "xxxx";
    private MqttHandler mqttHandler;
    private  DatabaseHelper databaseHelper;
    private RecyclerView recyclerView;
    private MqttDataAdapter adapter;
    private List<MqttDataItem> dataItems;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mqtt_data);

        recyclerView = findViewById(R.id.RecyclerView);
        dataItems = new ArrayList<>();
        adapter = new MqttDataAdapter(dataItems);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        databaseHelper = new DatabaseHelper(this);
        mqttHandler = new MqttHandler(databaseHelper);
        mqttHandler.setMqttListener(this);
        new MqttData.ConnectTask().execute(BROKER_URL, CLIENT_ID);
    }
    @Override
    public void onMessageReceived(String payload) {
        Log.d("MainActivity", "onMessageReceived: " + payload);

        runOnUiThread(() -> {
            // Update the RecyclerView with the new data
            MqttDataItem newItem = new MqttDataItem(payload);
            dataItems.add(newItem);
            adapter.notifyDataSetChanged();

        });
    }

    @Override
    public void onConnectionLost() {

    }

    private class ConnectTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            String brokerUrl = params[0];
            String clientId = params[1];
            mqttHandler.connect(brokerUrl, clientId);
            return null;
        }
    }
}