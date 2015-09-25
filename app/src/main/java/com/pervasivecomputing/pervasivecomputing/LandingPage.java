package com.pervasivecomputing.pervasivecomputing;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;
import java.util.Set;

public class LandingPage extends FragmentActivity {

    protected static final int DISCOVERY_REQUEST = 1;


    private Button b1, b2;   //banananer i pyjamas
    private TextView fireB3;

    private LocationManager locationManager;
    public static Firebase ref;

    private Location loca = null;

    String locationProvider;
    public String toastText="";
    private BluetoothDevice remoteDevice;



    BluetoothAdapter btAdapter;
    BroadcastReceiver bluetoothState = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String prevStateExtra=BluetoothAdapter.EXTRA_PREVIOUS_STATE;
            String stateExtra = BluetoothAdapter.EXTRA_STATE;
            int state = intent.getIntExtra(stateExtra,-1);
            int previousState = intent.getIntExtra(prevStateExtra,-1);
            String toastText = "";
            switch(state) {
                case BluetoothAdapter.STATE_TURNING_ON:
                    toastText = "Bluetooth turning on";
                    Toast.makeText(LandingPage.this,toastText,Toast.LENGTH_SHORT).show();;
                    break;
                case BluetoothAdapter.STATE_ON:
                    BTconnect();
                    toastText = "Bluetooth on";
                    b2.setText("DC Bluetooth");
                    Toast.makeText(LandingPage.this,toastText,Toast.LENGTH_SHORT).show();;
                    break;
                case BluetoothAdapter.STATE_TURNING_OFF:
                    toastText = "Bluetooth turning off";
                    Toast.makeText(LandingPage.this,toastText,Toast.LENGTH_SHORT).show();;
                    break;
                case BluetoothAdapter.STATE_OFF:
                    toastText = "Bluetooth off";
                    b2.setText("Connect");
                    Toast.makeText(LandingPage.this,toastText,Toast.LENGTH_SHORT).show();;
                    break;
            }
        }

    };


    private void BTconnect(){
        b2.setText("Disconnect");
        String scanModeChanged = BluetoothAdapter.ACTION_SCAN_MODE_CHANGED;
        String beDiscoverable = BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE;
        IntentFilter filter = new IntentFilter(scanModeChanged);
        registerReceiver(bluetoothState,filter);
        startActivityForResult(new Intent(beDiscoverable), DISCOVERY_REQUEST);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if (requestCode == DISCOVERY_REQUEST){
            Toast.makeText(LandingPage.this,"Discovery in progress", Toast.LENGTH_SHORT).show();
            findDevices();
        }
    }

    private void findDevices(){
        String lastUsedRemoteDevice = getLastUsedRemoteDevice();
        if (lastUsedRemoteDevice != null){
            toastText = "Checking for known paired devices:"+ lastUsedRemoteDevice;
            Toast.makeText(LandingPage.this, toastText,Toast.LENGTH_SHORT).show();
            Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();
            for (BluetoothDevice device : pairedDevices){
                toastText = "Found device: " + device.getName()+"@"+lastUsedRemoteDevice;
                Toast.makeText(LandingPage.this,toastText,Toast.LENGTH_SHORT).show();
                remoteDevice = device;
            }
        } else if (remoteDevice == null){
            toastText = "Starting discovery for remote devices...";
            Toast.makeText(LandingPage.this,toastText,Toast.LENGTH_SHORT).show();
            if (btAdapter.startDiscovery()){
                toastText = "Scanning for devices...";
                Toast.makeText(LandingPage.this, toastText, Toast.LENGTH_SHORT).show();
                registerReceiver(discoveryResult, new IntentFilter(BluetoothDevice.ACTION_FOUND));
            }
        }
    }

    BroadcastReceiver discoveryResult = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String remoteDeviceName = intent.getStringExtra(BluetoothDevice.EXTRA_NAME);
            BluetoothDevice remoteDevice;
            remoteDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            toastText = "Discovered: " + remoteDeviceName;
            Toast.makeText(LandingPage.this,toastText,Toast.LENGTH_SHORT).show();
        }
    };



    private String getLastUsedRemoteDevice(){
        SharedPreferences prefs = getPreferences(MODE_PRIVATE);
        return prefs.getString("LAST_REMOTE_DEVICE_ADDRESS", null);

    }


    protected void onStart() {
        super.onStart();
        b1 = (Button) findViewById(R.id.gotomap);
        b2 = (Button) findViewById(R.id.btConnect);
        fireB3 = (TextView) findViewById(R.id.userName);


        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mapScreen = new Intent();
                mapScreen.setClass(getApplicationContext(), MapsActivity.class);
                startActivity(mapScreen);
            }
        });



        btAdapter = BluetoothAdapter.getDefaultAdapter();
        if (btAdapter.isEnabled()){
            b2.setText("Disconnect BT");
        } else {
            b2.setText("Connect BT");
        }




        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startBlueTooth();
            }
        });
    }

    private void startBlueTooth(){
        if (btAdapter.isEnabled()){
            btAdapter.disable();
            b2.setText("Connect BT");

        } else {
            String actionStateChanged = BluetoothAdapter.ACTION_STATE_CHANGED;
            String actionRequestEnable = BluetoothAdapter.ACTION_REQUEST_ENABLE;
            IntentFilter filter = new IntentFilter(actionStateChanged);
            registerReceiver(bluetoothState,filter);
            startActivityForResult(new Intent(actionRequestEnable),0);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Firebase.setAndroidContext(this);
        setContentView(R.layout.firebase);




        String json = "hi";

        try {
            JSONObject obj = new JSONObject(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        ref = new Firebase("https://blinding-heat-6209.firebaseio.com");

        onFacebookAccessTokenChange(MainActivity.token);

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        locationProvider = LocationManager.NETWORK_PROVIDER;



        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                try {
                   // Location loc = locationManager.getLastKnownLocation(locationProvider);
                    if (MapsActivity.s != null){
                        fireB3.setText("MAP" + MapsActivity.s);
                        return;
                    }
                    fireB3.setText("YOU ARE NOW AUTHENTICATED" + location.getLatitude() +" ; "+ location.getLongitude());
                    if (data != null) {
                        Firebase alanRef = ref.child("users").child(data.getUid()+"2");

                        User alan = new User(data.getProviderData().get("displayName").toString(), location);
                        alanRef.setValue(alan);
                    }
                } catch (Exception e){
                    fireB3.setText("NO ");
                }

            }

            public void onStatusChanged(String provider, int status, Bundle extras) {}

            public void onProviderEnabled(String provider) {}

            public void onProviderDisabled(String provider) {}
        };

// Register the listener with the Location Manager to receive location updates

        try {
        locationManager.requestLocationUpdates(locationProvider, 0, 0, locationListener);
        } catch (Exception e){
            fireB3.setText("NO LOC");
        }

    }




    AuthData data;

    private void onFacebookAccessTokenChange(AccessToken token) {
        if (token != null) {
            ref.authWithOAuthToken("facebook", token.getToken(), new Firebase.AuthResultHandler() {
                @Override
                public void onAuthenticated(AuthData authData) {
                    // The Facebook user is now authenticated with your Firebase app
                    data = authData;
                   // fireB3.setText("YOU ARE NOW AUTHENTICATED" + LocationManager.getLastKnownLocation(locationProvider));
                    //Firebase alanRef = ref.child("users").child(data.getUid());

                    //User alan = new User(data.getProviderData().get("displayName").toString(), null);

                    //alanRef.setValue(alan);
                }
                @Override
                public void onAuthenticationError(FirebaseError firebaseError) {
                    // there was an error
                }
            });
        } else {
        /* Logged out of Facebook so do a logout from the Firebase app */
            ref.unauth();
        }
    }



    @Override
    protected void onPause() {
        super.onPause();
        synchronized (this) {

        }

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

}
