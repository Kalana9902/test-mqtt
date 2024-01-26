package com.example.mqtt_subscriber;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class BLEScanner extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 2;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothLeScanner bluetoothLeScanner;
    private ScanCallback scanCallback;
    private TextView deviceListTextView;
    private List<String> deviceList;
    private RecyclerView recyclerView;
    private DeviceListAdapter deviceListAdapter;
    private Context context;
    private Activity activity;
    List<BluetoothDevice> bluetoothDevices = new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blescanner);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_CODE);
        }

        // Initialize Bluetooth
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Log.e("BLEScanner", "Bluetooth not supported");
            finish();
            return;
        }

        

        // Check permissions
        if (!hasPermissions()) {
            //requestPermissions();
            Log.d("permission", "onCreate: permission not granted");
        } else {

            Log.d("TAG", "onCreate: scan started");

        }

        startScanning();



        // Initialize UI
        recyclerView = findViewById(R.id.device_recycler_view);
        deviceList = new ArrayList<>();
        //bluetoothDevices = new ArrayList<>();

        Log.d("list", "onCreate: created" + deviceList);
        deviceListAdapter = new DeviceListAdapter(deviceList, bluetoothDevices, this);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(deviceListAdapter);
    }

    private boolean hasPermissions() {
        return checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                checkSelfPermission(android.Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.BLUETOOTH_CONNECT}, 1);
    }

    private void startScanning() {
        Log.d("TAG", "startScanning: scaning ");
        scanCallback = new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, ScanResult result) {
                super.onScanResult(callbackType, result);
                BluetoothDevice device = result.getDevice();
                Log.d("scan page", "onScanResult: Scaninng");

                if (!bluetoothDevices.contains(device)) {
                    bluetoothDevices.add(device);

                    // Notify the adapter that the data set has changed
                    deviceListAdapter.notifyDataSetChanged();
                }

                if (ActivityCompat.checkSelfPermission(BLEScanner.this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                String deviceName = device.getName() != null ? device.getName() : "[Unnamed]";
                String deviceAddress = device.getAddress();
                String rssi = String.valueOf(result.getRssi());

                // Update device list text view
                deviceList.add(deviceName + " (" + deviceAddress + ") - RSSI: " + rssi);
                updateDeviceListText();
            }

            @Override
            public void onScanFailed(int errorCode) {
                super.onScanFailed(errorCode);
                Log.e("BLEScanner", "Scan failed with code: " + errorCode);
            }
        };

        bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
//        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
//            return;
//        }
        bluetoothLeScanner.startScan(scanCallback);
    }

    private void updateDeviceListText() {
        deviceListAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (bluetoothLeScanner != null) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            bluetoothLeScanner.stopScan(scanCallback);
        }
    }

    @Override public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                startScanning();
            } else {
                Log.e("BLEScanner", "Permissions not granted");
            }
        }
    }
}