package com.pocketcareplue.bluetoothscan;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    BluetoothAdapter mBluetoothAdapter;
    public ArrayList<BTDevice> mBTDevices;
    DeviceListAdapter mDeviceListAdapter;
    ListView lvDevicesList;

    Button btnOnOff;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.btnOnOff = (Button) findViewById(R.id.btnOnOff);
        this.lvDevicesList = (ListView) findViewById(R.id.lvNewDevices);
        this.mBTDevices = new ArrayList<BTDevice>();

        this.btnOnOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enableDisableBt();
            }
        });
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    private void enableDisableBt() {
        if(mBluetoothAdapter == null){
            Log.println(Log.DEBUG, "TA", "BLuethhth not found on device!");
            return;
        }

        if(mBluetoothAdapter.isEnabled()){
            mBluetoothAdapter.disable();
        }
        else{
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(enableIntent);

            /*Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoverableIntent);*/
        }
    }

    public void startService(View view){
        String input = "hey!";
        Intent serviceIntent = new Intent(this, BluethoothScanService.class);
        serviceIntent.putExtra("inputExtra", input);
        startService(serviceIntent);
    }

    public void stopService(View view){
        Intent serviceIntent = new Intent(this, BluethoothScanService.class);
        stopService(serviceIntent);
    }

    public void btnDiscover(View view) {
        mBTDevices.clear();

        SharedPreferences sh = getSharedPreferences("BTDeviceData", MODE_PRIVATE);
        Map<String, ?> allEntries = sh.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            Log.d("map values", entry.getKey() + ": " + entry.getValue().toString());

            mBTDevices.add(new BTDevice(entry.getKey(), entry.getValue().toString()));

            Toast.makeText(getApplicationContext(), "map values"+entry.getKey() + ": " + entry.getValue().toString(),
                    Toast.LENGTH_SHORT).show();
        }

        mDeviceListAdapter = new DeviceListAdapter(getApplicationContext(), R.layout.devie_adapter_view, mBTDevices);
        lvDevicesList.setAdapter(mDeviceListAdapter);

        if ( allEntries.size() == 0){
            Toast.makeText(getApplicationContext(), "No devices found", Toast.LENGTH_LONG).show();
        }

    }

    public void onDestroy(){
        super.onDestroy();
    }

}
