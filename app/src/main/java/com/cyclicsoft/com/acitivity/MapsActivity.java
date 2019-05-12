package com.cyclicsoft.com.acitivity;

//<!--This activity is insted of welcome activity-->


import android.Manifest;
import android.animation.ValueAnimator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;

import com.cyclicsoft.com.common.Common;
import com.cyclicsoft.com.R;
import com.cyclicsoft.com.model.Admin;
import com.cyclicsoft.com.remote.IGoogleAPI;
//import android.location.LocationListener;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;
import android.widget.Toast;


import com.cyclicsoft.com.service.LocationService;
import com.cyclicsoft.com.settings.Constants;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.github.glomadrian.materialanimatedswitch.MaterialAnimatedSwitch;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
//import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback{

    // Map
    private GoogleMap mMap;
    private Location mLastLocation;



    DatabaseReference dbref;
    GeoFire geoFire;

    Marker mCurrent;
    MaterialAnimatedSwitch location_switch;
    SupportMapFragment mapFragment;

    // latitude longitude
    private double lat,lng;


    //    Car Animation
    private List <LatLng> polyLineList;
    private Marker carMarker;
    private float v;
    private Handler handler;
    private LatLng startPosition, endPosition, currentPosition;
    private int index, next;
    private String destination;
    private PolylineOptions polylineOptions, blackPolylineOptions;
    private Polyline blackPolyline, grayPolyline;

    private IGoogleAPI mService;

    private TextView tv_username;

    // user id from login
    private String userid;
    private DatabaseReference dbrefAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        userid = getIntent().getStringExtra("userid");

        // Call for initialization
        initFields();

        LocalBroadcastManager.getInstance(this).registerReceiver(
                new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        String latitude = intent.getStringExtra(LocationService.EXTRA_LATITUDE);
                        String longitude = intent.getStringExtra(LocationService.EXTRA_LONGITUDE);

                        if (latitude != null && longitude != null) {

                            Admin admin = new Admin();
                            admin.setaID(userid);
                            admin.setLat(latitude);
                            admin.setLng(longitude);

                            dbrefAdmin.child(admin.getaID()).child("lat").setValue(admin.getLat())
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(getApplicationContext(),"Lat  updated",Toast.LENGTH_SHORT).show();
//                                               // Snackbar.make(adminRegLayout, "REGISTRATION SUCCESSFUL!!", Snackbar.LENGTH_SHORT).show();



                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(getApplicationContext(),"FAILED updating lat!!    "+e.getMessage(),Toast.LENGTH_SHORT).show();
//                                               // Snackbar.make(adminRegLayout, "FAILED"+e.getMessage(), Snackbar.LENGTH_SHORT).show();

                                        }
                                    });

                            dbrefAdmin.child(admin.getaID()).child("lng").setValue(admin.getLng())
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(getApplicationContext(),"Lng  updated",Toast.LENGTH_SHORT).show();
//                                               // Snackbar.make(adminRegLayout, "REGISTRATION SUCCESSFUL!!", Snackbar.LENGTH_SHORT).show();



                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(getApplicationContext(),"FAILED updating lng!!    "+e.getMessage(),Toast.LENGTH_SHORT).show();
//                                               // Snackbar.make(adminRegLayout, "FAILED"+e.getMessage(), Snackbar.LENGTH_SHORT).show();

                                        }
                                    });


                            lat = Double.valueOf(latitude);
                            lng = Double.valueOf(longitude);

                            // Display location in map
                            displayLocation();
                                                    }
                    }
                }, new IntentFilter(LocationService.ACTION_LOCATION_BROADCAST)
        );

        location_switch.setOnCheckedChangeListener(new MaterialAnimatedSwitch.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(boolean isOnline) {
                if(isOnline){
                    displayLocation();
                    Toast.makeText(getApplicationContext(),"You are online",Toast.LENGTH_SHORT).show();
                }
                else {
                    mCurrent.remove();
                    mMap.clear();
                    handler.removeCallbacks(drawPathRunnable);
                    Toast.makeText(getApplicationContext(),"You are offline",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void initFields() {
        //
        polyLineList = new ArrayList<>();

        //Geo Fire
        dbref = FirebaseDatabase.getInstance().getReference("Drivers");
        geoFire = new GeoFire(dbref);
        mService = Common.getGoogleApi();

        // Firebase ref
        dbrefAdmin = FirebaseDatabase.getInstance().getReference("Admin");

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        // Init View
        location_switch = (MaterialAnimatedSwitch)findViewById(R.id.locationSwitch);

        setUpLocation();
    }


    Runnable drawPathRunnable = new Runnable() {
        @Override
        public void run() {
            if(index<polyLineList.size()-1){
                index++;
                next = index + 1;
            }
            if(index<polyLineList.size()-1){
                startPosition = polyLineList.get(index);
                endPosition = polyLineList.get(next);
            }

            final ValueAnimator valueAnimator = ValueAnimator.ofFloat(0,1);
            valueAnimator.setDuration(3000);
            valueAnimator.setInterpolator(new LinearInterpolator());
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator1) {
                    v = valueAnimator.getAnimatedFraction();
                    lng = v*endPosition.longitude+(1-v)*startPosition.longitude;
                    lat = v*endPosition.latitude+(1-v)*startPosition.latitude;
                    LatLng newPos = new LatLng(lat,lng);
                    carMarker.setPosition(newPos);
                    carMarker.setAnchor(0.5f,0.5f);
                    carMarker.setRotation(getBearing(startPosition,newPos));
                    mMap.moveCamera(CameraUpdateFactory.newCameraPosition(
                            new CameraPosition.Builder()
                            .target(newPos)
                            .zoom(15.5f)
                            .build()
                    ));
                }
            });
            valueAnimator.start();
            handler.postDelayed(this,3000);
        }
    };
    private float getBearing(LatLng startPosition, LatLng endPosition) {
        double lat = Math.abs(startPosition.latitude- endPosition.latitude);
        double lng = Math.abs(startPosition.longitude- endPosition.longitude);
        if(startPosition.latitude < endPosition.latitude && startPosition.longitude < endPosition.longitude){
            return (float) (Math.toDegrees(Math.atan(lng/lat)));
        }
        else if(startPosition.latitude >= endPosition.latitude && startPosition.longitude < endPosition.longitude){
            return (float) ((90-Math.toDegrees(Math.atan(lng/lat)))+90);
        }
        else if(startPosition.latitude >= endPosition.latitude && startPosition.longitude >= endPosition.longitude){
            return (float) (Math.toDegrees(Math.atan(lng/lat))+180);
        }
        else if(startPosition.latitude < endPosition.latitude && startPosition.longitude >= endPosition.longitude) {
            return (float) ((90 - Math.toDegrees(Math.atan(lng / lat))) + 270);
        }

        return -1;
    }




//    private void getDirection() {
//        currentPosition = new LatLng(mLastLocation.getLatitude(),mLastLocation.getLongitude());
//
//        String requestApi = null;
//        try {
//            requestApi = "https://maps.googleapis.com/maps/api/directions/json?"+
//                    "mode = driving&"+
//                    "transit_routing_preference = less_driving&"+
//                    "origin = "+currentPosition.latitude+","+currentPosition.longitude+"&"+
//                    "destination = "+ destination +"&"+
//                    "key = "+getResources().getString(R.string.google_direction_api);
//            Log.d("EU",requestApi);
//
//            mService.getPath(requestApi)
//                    .enqueue(new Callback<String>() {
//                        @Override
//                        public void onResponse(Call<String> call, Response<String> response) {
//                            try {
//                                JSONObject jsonObject = new JSONObject(response.body().toString());
//                                JSONArray jsonArray = jsonObject.getJSONArray("routes");
//                                for(int i=0;i<jsonArray.length();i++){
//                                    JSONObject route = jsonArray.getJSONObject(i);
//                                    JSONObject poly = route.getJSONObject("overview_polyline");
//                                    String polyline = poly.getString("points");
//                                    polyLineList = decodePoly(polyline);
//                                }
////                                Adjusting Bounding
//                                LatLngBounds.Builder builder = new LatLngBounds.Builder();
//                                for(LatLng latLng:polyLineList){
//                                    builder.include(latLng);
//                                }
//                                LatLngBounds bounds = builder.build();
//                                CameraUpdate mCameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds,2);
//                                mMap.animateCamera(mCameraUpdate);
//
//                                polylineOptions = new PolylineOptions();
//                                polylineOptions.color(Color.GRAY);
//                                polylineOptions.width(5);
//                                polylineOptions.startCap(new SquareCap());
//                                polylineOptions.endCap(new SquareCap());
//                                polylineOptions.jointType(JointType.ROUND);
//                                polylineOptions.addAll(polyLineList);
//                                grayPolyline = mMap.addPolyline(polylineOptions);
//
//                                blackPolylineOptions = new PolylineOptions();
//                                blackPolylineOptions.color(Color.BLACK);
//                                blackPolylineOptions.width(5);
//                                blackPolylineOptions.startCap(new SquareCap());
//                                blackPolylineOptions.endCap(new SquareCap());
//                                blackPolylineOptions.jointType(JointType.ROUND);
//                                blackPolyline = mMap.addPolyline(blackPolylineOptions);
//
//                                mMap.addMarker(new MarkerOptions()
//                                .position(polyLineList.get(polyLineList.size()-1))
//                                .title("Pickup_Location"));
//
////                                Animation
//                                ValueAnimator polyLineAnimator = ValueAnimator.ofInt(0,100);
//                                polyLineAnimator.setDuration(2000);
//                                polyLineAnimator.setInterpolator(new LinearInterpolator());
//                                polyLineAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//                                    @Override
//                                    public void onAnimationUpdate(ValueAnimator valueAnimator) {
//                                        List<LatLng> points = grayPolyline.getPoints();
//                                        int percentValue = (int)valueAnimator.getAnimatedValue();
//                                        int size = points.size();
//                                        int newPoints = (int)(size*(percentValue/100.0f));
//                                        List<LatLng> p = points.subList(0,newPoints);
//                                        blackPolyline.setPoints(p);
//                                    }
//                                });
//                                polyLineAnimator.start();
//                                carMarker = mMap.addMarker(new MarkerOptions().position(currentPosition)
//                                .flat(true)
//                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.car)));
//
//                                handler = new Handler();
//                                index = -1;
//                                next = 1;
//                                handler.postDelayed(drawPathRunnable,3000);
//
//                            }
//                            catch (JSONException e){
//                                e.printStackTrace();
//                            }
//                        }
//
//                        @Override
//                        public void onFailure(Call<String> call, Throwable t) {
//                            Toast.makeText(MapsActivity.this, ""+t.getMessage(),Toast.LENGTH_SHORT).show();
//                        }
//                    });
//        }
//        catch (Exception e){
//            e.printStackTrace();
//        }
//    }

    private List decodePoly(String encoded) {

        List poly = new ArrayList();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;

            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);

            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;
            shift = 0;
            result = 0;

            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);

            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;
            LatLng p = new LatLng((((double) lat / 1E5)),(((double) lng / 1E5)));
            poly.add(p);
        }
        return poly;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case Constants.MY_PERMISSION_REQUEST_CODE:
                if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    if(checkPlayServices()){
                       // buildGoogleApiClient();
                        createLocationRequest();
                        if(location_switch.isChecked()){
                            displayLocation();
                        }
                    }
                }

        }
    }




    private void setUpLocation() {
        displayLocation();
    }




    private void createLocationRequest() {
    }




//    private void buildGoogleApiClient() {
//        mGoogleApiClient = new GoogleApiClient.Builder(this)
//                .addConnectionCallbacks(this)
//                .addOnConnectionFailedListener(this)
//                .addApi(LocationServices.API)
//                .build();
//        mGoogleApiClient.connect();
//    }




    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if(resultCode != ConnectionResult.SUCCESS){
            if(GooglePlayServicesUtil.isUserRecoverableError(resultCode)){
//                Getting Error Dialogue----------------------------------------------------------------------------------------------------------
                GooglePlayServicesUtil.getErrorDialog(resultCode,this, Constants.PLAY_SERVICE_RES_REQUEST).show();
            }
            else{
                Toast.makeText(getApplicationContext(),"Opps!! Device is not compatible",Toast.LENGTH_SHORT).show();
                finish();
            }
            return false;
        }
        return true;
    }

    private void displayLocation() {
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION )!= PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION )!= PackageManager.PERMISSION_GRANTED){
//                                        Log.d("displayLocation 1","poooooooo--------------------------------------------"); Not Working
            return;
        }
        //mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if(true){
            if(location_switch.isChecked()){
//                final double latitude = mLastLocation.getLatitude();
//                final double longitude = mLastLocation.getLongitude();

//                //Update to firebase
                geoFire.setLocation(FirebaseAuth.getInstance().getCurrentUser().getUid(), new GeoLocation(lat, lng), new GeoFire.CompletionListener() {
                    @Override
                    public void onComplete(String key, DatabaseError error) {
//                        Log.d("displayLocation 2","poooooooo--------------------------------------------");
//                        Add marker
                        if(mCurrent != null) {
//                            Log.d("displayLocation 3", "poooooooo--------------------------------------------");
                            mCurrent.remove();
                        }
                            mCurrent = mMap.addMarker(new MarkerOptions()
                                    .position(new LatLng(lat, lng))
                            .title("EU BUS"));

//                            MOve camera to this position
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), 15.0f));
//                            Draw an animation rotate marker
//                            rotateMarker(mCurrent, -360, mMap);
//                            Log.d("update to firebase3","poooooooo--------------------------------------------");
                    }
                });
            }
        }
        else {
            Log.d("Error","Cannot get your location");
        }
    }




//    private void rotateMarker(final Marker mCurrent, final float i, GoogleMap mMap) {
//         final Handler handler = new Handler();
//         final long start = SystemClock.uptimeMillis();
//         final float startRotation = mCurrent.getRotation();
//         final long duration = 1500;
//        final Interpolator interpolator = new LinearInterpolator();
//        handler.post(new Runnable() {
//            @Override
//            public void run() {
//                long elapsed = SystemClock.uptimeMillis() - start;
//                float t = interpolator.getInterpolation((float)elapsed/duration);
//                float rot = t*i+(1-t)*startRotation;
//                mCurrent.setRotation(-rot>180?rot/2:rot);
//                if(t<1.0){
//                    handler.postDelayed(this, 16);
//                }
//            }
//        });
//    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, LocationService.class);
        startService(intent);
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.setTrafficEnabled(false);
        mMap.setIndoorEnabled(false);
        mMap.setBuildingsEnabled(false);
        mMap.getUiSettings().setZoomControlsEnabled(true);
    }


//    @Override
//    protected void onStop() {
////        Toast.makeText(locationService, "onstop", Toast.LENGTH_SHORT).show();
//
//        super.onStop();
//    }
//
//    @Override
//    protected void onRestart() {
//      //  Toast.makeText(locationService, "onstop", Toast.LENGTH_SHORT).show();
//
//        super.onRestart();
//
//
//    }
//
//    @Override
//    protected void onPause() {
//       // Toast.makeText(locationService, "paused", Toast.LENGTH_SHORT).show();
//
//        super.onPause();
//    }
}
