package com.example.mqtt_subscriber;

public class MqttDataItem {
    private String payload;

    public MqttDataItem(String payload) {
        this.payload = payload;
    }

    public String getPayload() {
        return payload;
    }
}
