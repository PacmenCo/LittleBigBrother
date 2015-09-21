package com.pervasivecomputing.pervasivecomputing;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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

public class LandingPage extends FragmentActivity {

    private Button b1, b2;   //banananer i pyjamas
    private TextView fireB3;

    private LocationManager locationManager;
    public static Firebase ref;

    private Location loca = null;

    String locationProvider;


    protected void onStart() {
        super.onStart();
        b1 = (Button) findViewById(R.id.gotomap);
        //b2 = (Button) findViewById(R.id.mapbutton);
        fireB3 = (TextView) findViewById(R.id.userName);


        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mapScreen = new Intent();
                mapScreen.setClass(getApplicationContext(), MapsActivity.class);
                startActivity(mapScreen);
            }
        });

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
    protected void onResume() {
        super.onResume();

    }

}
