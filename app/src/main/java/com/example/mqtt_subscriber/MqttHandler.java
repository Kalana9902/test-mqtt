package com.example.mqtt_subscriber;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;


public class MqttHandler {
    private MqttClient client;

    private String topic = "";

    private TextView displayTextView; // Add TextView reference
    private DatabaseHelper databaseHelper;
    private MqttListener mqttListener;

    public MqttHandler(DatabaseHelper databaseHelper) {

        this.databaseHelper = databaseHelper;
    }

    public void setMqttListener(MqttListener listener) {
        this.mqttListener = listener;
    }

    public void connect(String brokerUrl, String clientId) {
        topic = "sensor_data";
        Log.d("WQewqe","connect: function hit");
        try {
            // Set up the persistence layer
            MemoryPersistence persistence = new MemoryPersistence();

            // Initialize the MQTT client
            client = new MqttClient(brokerUrl, clientId, persistence);

            // Set up the connection options
            MqttConnectOptions connectOptions = new MqttConnectOptions();
            connectOptions.setCleanSession(true);

            // Connect to the broker
            client.connect(connectOptions);
            Log.d("success", "connect: connected");
            subscribe(topic);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void disconnect() {
        try {
            client.disconnect();
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void subscribe(String topic) {
        try {
            client.subscribe(topic);
            client.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    Log.d("connection", "connectionLost: lost");
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    Log.d("TAG", "message: " + new String(message.getPayload()));
                    String payload = new String(message.getPayload());
                    updateUI(payload);
                    saveToDatabase(payload);
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {

                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    private void updateUI(final String payload) {
        if (mqttListener != null) {
            mqttListener.onMessageReceived(payload);
        }
    }

@SuppressLint("StaticFieldLeak")
private void saveToDatabase(final String payload) {
        if (databaseHelper != null){
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... params) {
                    try {
                        SQLiteDatabase db = databaseHelper.getWritableDatabase();
                        ContentValues values = new ContentValues();
                        values.put(DatabaseHelper.COLUMN_PAYLOAD, payload);
                        db.insert(DatabaseHelper.TABLE_NAME, null, values);
                        db.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return null;
                }
            }.execute();

        } else {
            Log.e("MqttHandler", "DatabaseHelper is null");
        }

}

    public interface MqttListener {
        void onMessageReceived(String payload);
        void onConnectionLost();
    }
}
