package com.pervasivecomputing.pervasivecomputing;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONObject;

import java.util.List;
import java.util.Map;

public class MapsActivity extends FragmentActivity {

    public static GoogleMap mMap; // Might be null if Google Play services APK is not available.

    public static Location location;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();

    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();

    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */

    public static Map<String, User> s;

    private void setUpMap() {
        // Get a reference to our posts
        ref = new Firebase("https://blinding-heat-6209.firebaseio.com/users");

// Attach an listener to read the data at our posts reference

        // Get a reference to our posts

        // Attach an listener to read the data at our posts reference

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                System.out.println("There are " + snapshot.getChildrenCount() + " blog posts");
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    System.out.println("THE SNAPSHOTS " + postSnapshot.child("location").child("latitude").getValue());


                    mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(
                                    (double)postSnapshot.child("location").child("latitude").getValue(),
                                    (double)postSnapshot.child("location").child("longitude").getValue()))
                            .title("ddfdff"));
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });




        locationUpdater();

        //mMap.setMyLocationEnabled(true);
    }


       private String locationProvider;
        private LocationManager locationManager;
    private AuthData data;
    Firebase ref;

    public void locationUpdater(){
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        locationProvider = LocationManager.NETWORK_PROVIDER;



        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                try {
                    // Location loc = locationManager.getLastKnownLocation(locationProvider);
                    //fireB3.setText("YOU ARE NOW AUTHENTICATED" + location.getLatitude() +" ; "+ location.getLongitude());
                    if (data != null) {
                        Firebase alanRef = ref.child("users").child(data.getUid());

                        User alan = new User(data.getProviderData().get("displayName").toString(), location);
                        alanRef.setValue(alan);
                    }
                } catch (Exception e){

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
           // fireB3.setText("NO LOC");
        }
    }
}
