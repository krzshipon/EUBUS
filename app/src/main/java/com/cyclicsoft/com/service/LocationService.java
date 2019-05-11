package com.cyclicsoft.com.service;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import com.cyclicsoft.com.settings.Constants;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static com.google.android.gms.location.LocationServices.SettingsApi;

public class LocationService extends Service implements LocationListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {


    // google api client
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    PendingResult<LocationSettingsResult> result;
    final static int REQUEST_LOCATION = 199;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;




    private static final String TAG = "LocationService";

    //private  final Context mContext;

    // flag for GPS status
    boolean isGPSEnabled = false;

    // flag for Network status
    boolean isNetworkEnabled = false;

    // flag for location status
    boolean canGetLocation;

    Location location; // location
    double latitude;   // latitude
    double longitude;  // longitude


    // Declaring a location Manager
    protected LocationManager locationManager;

    public static final String ACTION_LOCATION_BROADCAST = LocationService.class.getName() + "LocationBroadcast";
    public static final String EXTRA_LATITUDE = "extra_latitude";
    public static final String EXTRA_LONGITUDE = "extra_longitude";



    public LocationService(){
        super();
    }



//    public LocationService(Context context){
//        this.mContext = context;
//
//
////        // Connect to location service via google api client
////        mGoogleApiClient = new GoogleApiClient.Builder(mContext)
////                .addApi(LocationServices.API)
////                .addConnectionCallbacks(this)
////                .addOnConnectionFailedListener(this).build();
////        mGoogleApiClient.connect();
//
//
//        // update location
//        getLocation();
//    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("busses");

       getLocation();
        Log.d("ppp","start"+latitude);


        return super.onStartCommand(intent, flags, startId);

        
    }

    @Override
    public void onConnected(Bundle bundle) {

        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(Constants.MIN_TIME_BW_UPDATE);
        mLocationRequest.setFastestInterval(Constants.FASTEST_LOCATION_INTERVAL);

        //getLocation();

        // Turn on Gps if off
        if(!isGPSEnabled && !isNetworkEnabled){
            // gps is off turn it on
            showSettingAlert();
        }

    }








    /**
     * For getting the current location of the
     * device
     * @return location the location of device
     *                  based on GPS or NetworkProvider
     */
    public Location getLocation() {
        try{
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_COARSE);
            criteria.setAltitudeRequired(false);
            criteria.setBearingRequired(false);
            criteria.setCostAllowed(true);

            String provider = locationManager.getBestProvider(criteria, true);
            locationManager.requestLocationUpdates(provider, Constants.MIN_TIME_BW_UPDATE, Constants.MIN_DISTANCE_PER_UPDATE, this);

            //getting GPS status
            isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            //getting Network Status
            isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if(!isGPSEnabled && !isNetworkEnabled){
                showSettingAlert();
            } else {
                this.canGetLocation = true;

                // First get location from Network Provider
                if(isNetworkEnabled){
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            Constants.MIN_TIME_BW_UPDATE,
                            Constants.MIN_DISTANCE_PER_UPDATE, this
                    );

                    Log.d(TAG, "Network");
                    if(locationManager != null){
                        location = locationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                        if (location != null){
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                }

                if(isGPSEnabled){
                    if (location ==null){
                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                Constants.MIN_TIME_BW_UPDATE,
                                Constants.MIN_DISTANCE_PER_UPDATE, this
                        );

                        Log.d(TAG, "GPS");
                        if(locationManager != null){
                            location = locationManager
                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if(location != null){
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }

                        }


                    }
                }
            }

        }catch (SecurityException e){

        }
        return location;
    }




    public void showSettingAlert(){
        canGetLocation = true;


//        /**
//         * Use this for
//         * Turn GPS on Without going to device setting
//         */
//
//        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
//                .addLocationRequest(mLocationRequest);
//        builder.setAlwaysShow(true);
//
//        result = SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
//
//        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
//            @Override
//            public void onResult(LocationSettingsResult result) {
//                final Status status = result.getStatus();
//                //final LocationSettingsStates state = result.getLocationSettingsStates();
//                switch (status.getStatusCode()) {
//                    case LocationSettingsStatusCodes.SUCCESS:
//                        // All location settings are satisfied. The client can initialize location
//                        // requests here.
//                        //...
//                        break;
//                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
//                        // Location settings are not satisfied. But could be fixed by showing the user
//                        // a dialog.
//                        try {
//                            // Show the dialog by calling startResolutionForResult(),
//                            // and check the result in onActivityResult().
//                            status.startResolutionForResult(
//                                    (Activity) mContext,
//                                    REQUEST_LOCATION);
//                            canGetLocation = true;
//                        } catch (IntentSender.SendIntentException e) {
//                            // Ignore the error.
//                        }
//                        break;
//                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
//                        // Location settings are not satisfied. However, we have no way to fix the
//                        // settings so we won't show the dialog.
//                        //...
//                        break;
//                }
//            }
//        });



        /**
         * Use this for
         * Turn GPS on BY going to device setting
         */


        AlertDialog.Builder alBuilder = new AlertDialog.Builder(this);

        //Setting Dialog Title
        alBuilder.setTitle("GPS Setting");

        // Dialog message
        alBuilder.setMessage("Gps is not enabled. Do you want to enable it now?");

        // On pressing setting button
        alBuilder.setPositiveButton("setting", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                getApplication().startActivity(intent);
            }
        });

        // on pressing cancel button
        alBuilder.setNegativeButton("canel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alBuilder.show();
    }


    /**
     * Stop using GPS listener
     * Calling this function will stop using GPS in your app
     * */
    public void stopUsingGPS(){
        if(locationManager != null){
            locationManager.removeUpdates(LocationService.this);
        }
    }



    /**
     * Function to get latitude
     * */

    public double getLatitude(){
        if(location != null){
            latitude = location.getLatitude();
        }

        // return latitude
        return latitude;
    }

    /**
     * Function to get longitude
     * */

    public double getLongitude(){
        if(location != null){
            longitude = location.getLongitude();
        }

        // return longitude
        return longitude;
    }

    /**
     * Function to check GPS/wifi enabled
     * @return boolean
     * */

    public boolean canGetLocation() {
        return this.canGetLocation;
    }






    @Override
    public void onLocationChanged(Location location) {
        this.location = location ;
        Log.d("ppp","changed"+latitude);




        if (location != null) {
            Log.d(TAG, "== location != null");

            //Send result to activities
            sendMessageToUI(String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()));
        }

    }
    private void sendMessageToUI(String lat, String lng) {

        Log.d(TAG, "Sending info...");

        Intent intent = new Intent(ACTION_LOCATION_BROADCAST);
        intent.putExtra(EXTRA_LATITUDE, lat);
        intent.putExtra(EXTRA_LONGITUDE, lng);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        getLocation();

    }

    @Override
    public void onProviderEnabled(String provider) {
        getLocation();

    }

    @Override
    public void onProviderDisabled(String provider) {

        showSettingAlert();
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d("ppp","onbind"+latitude);
        getLocation();
        return null;
    }



    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}
