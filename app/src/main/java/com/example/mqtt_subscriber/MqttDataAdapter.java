package com.example.mqtt_subscriber;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MqttDataAdapter extends RecyclerView.Adapter<MqttDataAdapter.ViewHolder> {
    private List<MqttDataItem> dataItems;

    public MqttDataAdapter(List<MqttDataItem> dataItems) {
        this.dataItems = dataItems;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mqtt_data, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        MqttDataItem item = dataItems.get(position);
        holder.payloadTextView.setText(item.getPayload());
    }

    @Override
    public int getItemCount() {
        return dataItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView payloadTextView;

        public ViewHolder(View view) {
            super(view);
            payloadTextView = view.findViewById(R.id.payloadTextView);
        }
    }
}
