package com.example.mqtt_subscriber;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;


public class DeviceListAdapter extends RecyclerView.Adapter<DeviceListAdapter.ViewHolder> {

    private final List<String> deviceList;
    private List<BluetoothDevice> bluetoothDevices;
    private Context context;
    private Activity activity = null;
    private BluetoothGattCallback gattCallback;

    public DeviceListAdapter(List<String> deviceList, List<BluetoothDevice> bluetoothDevices, Context context) {
        this.deviceList = deviceList;
        this.bluetoothDevices = bluetoothDevices;
        this.activity = activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.device_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        String deviceInfo = deviceList.get(position);
        holder.deviceTextView.setText(deviceInfo);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("DeviceListAdapter", "onClick: bluetoothDevices size: " + bluetoothDevices.size() + ", position: " + position);
                // Handle item click event
                if (!bluetoothDevices.isEmpty() && position < bluetoothDevices.size()) {
                    connectToDevice(bluetoothDevices.get(position), v.getContext());
                } else {
                    // Handle the case where the list is empty or the position is out of bounds
                    Toast.makeText(DeviceListAdapter.this.context, "Invalid device selection", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return deviceList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView deviceTextView;

        public ViewHolder(View view) {
            super(view);
            deviceTextView = view.findViewById(R.id.device_text_view);
        }
    }

    private void connectToDevice(BluetoothDevice device, Context context) {
         gattCallback = new BluetoothGattCallback() {
            @Override
            public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                super.onConnectionStateChange(gatt, status, newState);

                // Ensure that the Toast is shown on the main UI thread
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("before", "run: hit");

                        if (status == BluetoothGatt.GATT_SUCCESS) {
                            // Connection successful logic
                            if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                                return;
                            }
                            Toast.makeText(context, "Connected to " + gatt.getDevice().getName(), Toast.LENGTH_SHORT).show();
                            // Navigate to another activity with connected device details
                             //navigateToConnectedDeviceActivity(gatt.getDevice());
                        } else {
                            // Handle connection error
                            Toast.makeText(context, "Connection Error: " + status, Toast.LENGTH_SHORT).show();

                            // Retry the connection after a delay (adjust the delay as needed)
                            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    // Attempt to reconnect
                                    if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                                        return;
                                    }
                                    BluetoothGatt bluetoothGatt = device.connectGatt(context, false, gattCallback);
                                }
                            }, 1000);
                        }
                    }
                });
            }
        };

        // Establish GATT connection
        BluetoothGatt bluetoothGatt = device.connectGatt(context, false, gattCallback);
    }

    private void navigateToConnectedDeviceActivity(BluetoothDevice connectedDevice) {
        // Create an Intent to navigate to the ConnectedDeviceActivity
        Intent intent = new Intent(context, ConncetedDeviceActivity.class);

        // Pass connected device details to the next activity
        if (ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        intent.putExtra("DEVICE_NAME", connectedDevice.getName());
        intent.putExtra("DEVICE_ADDRESS", connectedDevice.getAddress());

        // Start the activity
        context.startActivity(intent);
    }

}
