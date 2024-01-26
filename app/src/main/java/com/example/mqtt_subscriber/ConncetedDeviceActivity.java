package com.example.mqtt_subscriber;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class ConncetedDeviceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connceted_device);
        String deviceName = getIntent().getStringExtra("DEVICE_NAME");
        String deviceAddress = getIntent().getStringExtra("DEVICE_ADDRESS");

        // Display connected device details in TextViews or any other UI elements
        TextView nameTextView = findViewById(R.id.device_name_text_view);
        TextView addressTextView = findViewById(R.id.device_address_text_view);

        nameTextView.setText("Device Name: " + deviceName);
        addressTextView.setText("Device Address: " + deviceAddress);
    }
}