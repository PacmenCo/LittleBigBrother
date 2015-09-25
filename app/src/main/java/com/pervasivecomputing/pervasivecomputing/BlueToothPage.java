package com.pervasivecomputing.pervasivecomputing;

import android.bluetooth.BluetoothAdapter;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

/**
 * Created by Rasmus on 25/09/15.
 */
public class BlueToothPage extends FragmentActivity {


    BluetoothAdapter btAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (btAdapter == null){
            btAdapter = BluetoothAdapter.getDefaultAdapter();
        }
        if (btAdapter.isDiscovering()){
            btAdapter.cancelDiscovery();
        }
        btAdapter.startDiscovery();
    }
}
