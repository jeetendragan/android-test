package com.pocketcareplue.bluetoothscan;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    BluetoothAdapter mBluetoothAdapter;

    public ArrayList<BluetoothDevice> mBTDevices = new ArrayList<>();
    DeviceListAdapter mDeviceListAdapter;
    ListView lvDevicesList;

    Button btnOnOff;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.btnOnOff = (Button) findViewById(R.id.btnOnOff);

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
            IntentFilter btInten = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(mBroadcastReceiver, btInten);
        }
        else{
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(enableIntent);

            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoverableIntent);

            registerReceiver(mBroadcastReceiver, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));
            registerReceiver(mBroadcastReceiver, new IntentFilter(mBluetoothAdapter.ACTION_SCAN_MODE_CHANGED));
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

    }

    public void onDestroy(){
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver);
    }

    private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
            }

            if(BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)){
                int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, mBluetoothAdapter.ERROR);
                Log.println(Log.DEBUG, "TA", "state: "+state);

            }
        }
    };

}
