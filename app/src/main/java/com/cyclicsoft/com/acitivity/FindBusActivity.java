package com.cyclicsoft.com.acitivity;

import android.Manifest;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cyclicsoft.com.R;
import com.cyclicsoft.com.common.Common;
import com.cyclicsoft.com.model.Admin;
import com.cyclicsoft.com.remote.IGoogleAPI;
import com.cyclicsoft.com.service.LocationService;
import com.cyclicsoft.com.settings.Constants;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.github.glomadrian.materialanimatedswitch.MaterialAnimatedSwitch;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.SquareCap;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FindBusActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    String selectedBusUserId = Constants.route1bus1id;

    double lat ,lng ;

    private SupportMapFragment mapFragment;
    private MaterialAnimatedSwitch location_switch;
//
//    GeoFire geoFire;
//
    DatabaseReference dbref;
    GeoFire geoFire;
    Marker mCurrent;
//    MaterialAnimatedSwitch location_switch;
//    SupportMapFragment mapFragment;

    //    Car Animation
    private List<LatLng> polyLineList;
    private Marker carMarker;
    private float v;
//    private double lat,lng;
    private Handler handler;
    private LatLng startPosition, endPosition, currentPosition;
    private int index, next;
    private TextView tvDriverContract;
    private EditText edtPlace;
    private String destination;
    private PolylineOptions polylineOptions, blackPolylineOptions;
    private Polyline blackPolyline, grayPolyline;

    private IGoogleAPI mService;
    FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private Admin admin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_bus);

        initialize();



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


        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Toast.makeText(FindBusActivity.this, "  snapshot", Toast.LENGTH_SHORT).show();

                if (dataSnapshot.getValue() != null) {
                    admin = dataSnapshot.getValue(Admin.class);
                    lat = Double.valueOf((String)dataSnapshot.child("lat").getValue());
                    lng = Double.valueOf((String)dataSnapshot.child("lng").getValue());
                    displayLocation();

                }else {
                    Toast.makeText(FindBusActivity.this, "  null", Toast.LENGTH_SHORT).show();

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(FindBusActivity.this, "  cn", Toast.LENGTH_SHORT).show();
            }
        });



        polyLineList = new ArrayList<>();
//        //Geo Fire
        dbref = FirebaseDatabase.getInstance().getReference("Drivers");
        geoFire = new GeoFire(dbref);
        mService = Common.getGoogleApi();
    }

    private void initialize() {
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_frag_findbus);
        tvDriverContract = (TextView)findViewById(R.id.tv_findbus_driver_contract);
        mapFragment.getMapAsync(this);
//        Init View
        location_switch = (MaterialAnimatedSwitch)findViewById(R.id.fundbus_locationSwitch);

         databaseReference= FirebaseDatabase.getInstance().getReference().child("Admin").child(selectedBusUserId);


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




    private void getDirection() {
        currentPosition = new LatLng(lat,lng);

        String requestApi = null;
        try {
            requestApi = "https://maps.googleapis.com/maps/api/directions/json?"+
                    "mode = driving&"+
                    "transit_routing_preference = less_driving&"+
                    "origin = "+currentPosition.latitude+","+currentPosition.longitude+"&"+
                    "destination = "+ destination +"&"+
                    "key = "+getResources().getString(R.string.google_direction_api);
            Log.d("EU",requestApi);

            mService.getPath(requestApi)
                    .enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response.body().toString());
                                JSONArray jsonArray = jsonObject.getJSONArray("routes");
                                for(int i=0;i<jsonArray.length();i++){
                                    JSONObject route = jsonArray.getJSONObject(i);
                                    JSONObject poly = route.getJSONObject("overview_polyline");
                                    String polyline = poly.getString("points");
                                    polyLineList = decodePoly(polyline);
                                }
//                                Adjusting Bounding
                                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                                for(LatLng latLng:polyLineList){
                                    builder.include(latLng);
                                }
                                LatLngBounds bounds = builder.build();
                                CameraUpdate mCameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds,2);
                                mMap.animateCamera(mCameraUpdate);

                                polylineOptions = new PolylineOptions();
                                polylineOptions.color(Color.GRAY);
                                polylineOptions.width(5);
                                polylineOptions.startCap(new SquareCap());
                                polylineOptions.endCap(new SquareCap());
                                polylineOptions.jointType(JointType.ROUND);
                                polylineOptions.addAll(polyLineList);
                                grayPolyline = mMap.addPolyline(polylineOptions);

                                blackPolylineOptions = new PolylineOptions();
                                blackPolylineOptions.color(Color.BLACK);
                                blackPolylineOptions.width(5);
                                blackPolylineOptions.startCap(new SquareCap());
                                blackPolylineOptions.endCap(new SquareCap());
                                blackPolylineOptions.jointType(JointType.ROUND);
                                blackPolyline = mMap.addPolyline(blackPolylineOptions);

                                mMap.addMarker(new MarkerOptions()
                                        .position(polyLineList.get(polyLineList.size()-1))
                                        .title("Pickup_Location"));

//                                Animation
                                ValueAnimator polyLineAnimator = ValueAnimator.ofInt(0,100);
                                polyLineAnimator.setDuration(2000);
                                polyLineAnimator.setInterpolator(new LinearInterpolator());
                                polyLineAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                    @Override
                                    public void onAnimationUpdate(ValueAnimator valueAnimator) {
                                        List<LatLng> points = grayPolyline.getPoints();
                                        int percentValue = (int)valueAnimator.getAnimatedValue();
                                        int size = points.size();
                                        int newPoints = (int)(size*(percentValue/100.0f));
                                        List<LatLng> p = points.subList(0,newPoints);
                                        blackPolyline.setPoints(p);
                                    }
                                });
                                polyLineAnimator.start();
                                carMarker = mMap.addMarker(new MarkerOptions().position(currentPosition)
                                        .flat(true)
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.car)));

                                handler = new Handler();
                                index = -1;
                                next = 1;
                                handler.postDelayed(drawPathRunnable,3000);

                            }
                            catch (JSONException e){
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            Toast.makeText(FindBusActivity.this, ""+t.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    });
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

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
        displayLocation();
    }













//    private void buildGoogleApiClient() {
//        mGoogleApiClient = new GoogleApiClient.Builder(this)
//                .addConnectionCallbacks(this)
//                .addOnConnectionFailedListener(this)
//                .addApi(LocationServices.API)
//                .build();
//        mGoogleApiClient.connect();
//    }







    private void stopLocationUpdates() {
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION )!= PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION )!= PackageManager.PERMISSION_GRANTED){
            return;
        }
        //LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient,this);

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
                        Log.d("ppppp","before marker"+lat);
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




    private void rotateMarker(final Marker mCurrent, final float i, GoogleMap mMap) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        final float startRotation = mCurrent.getRotation();
        final long duration = 1500;
        final Interpolator interpolator = new LinearInterpolator();
        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float)elapsed/duration);
                float rot = t*i+(1-t)*startRotation;
                mCurrent.setRotation(-rot>180?rot/2:rot);
                if(t<1.0){
                    handler.postDelayed(this, 16);
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
//        Intent intent = new Intent(this, LocationService.class);
//        startService(intent);
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



    /**
     * For inflating menu
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_routes,menu);
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {

            //Route 1
            case R.id.route1Bus1Id:
                selectedBusUserId = Constants.route1bus1id;
                updateMap();
                return true;
            case R.id.route1Bus2Id:
                selectedBusUserId = Constants.route1bus2id;
                updateMap();
                return true;
            case R.id.route1Bus3Id:
                selectedBusUserId = Constants.route1bus3id;
                updateMap();
                return true;
            case R.id.route1Bus4Id:
                selectedBusUserId = Constants.route1bus4id;
                updateMap();
                return true;
            case R.id.route1Bus5Id:
                selectedBusUserId = Constants.route1bus5id;
                updateMap();
                return true;

                //Route 2
            case R.id.route2Bus1Id:
                selectedBusUserId = Constants.route2bus1id;
                updateMap();
                return true;
            case R.id.route2Bus2Id:
                selectedBusUserId = Constants.route2bus2id;
                updateMap();
                return true;
            case R.id.route2Bus3Id:
                selectedBusUserId = Constants.route2bus3id;
                updateMap();
                return true;
            case R.id.route2Bus4Id:
                selectedBusUserId = Constants.route2bus4id;
                updateMap();
                return true;
            case R.id.route2Bus5Id:
                selectedBusUserId = Constants.route2bus5id;
                updateMap();
                return true;

            //Route 3
            case R.id.route3Bus1Id:
                selectedBusUserId = Constants.route3bus1id;
                updateMap();
                return true;
            case R.id.route3Bus2Id:
                selectedBusUserId = Constants.route3bus2id;
                updateMap();
                return true;
            case R.id.route3Bus3Id:
                selectedBusUserId = Constants.route3bus3id;
                updateMap();
                return true;
            case R.id.route3Bus4Id:
                selectedBusUserId = Constants.route3bus4id;
                updateMap();
                return true;
            case R.id.route3Bus5Id:
                selectedBusUserId = Constants.route3bus5id;
                updateMap();
                return true;

            //Route 4
            case R.id.route4Bus1Id:
                selectedBusUserId = Constants.route4bus1id;
                updateMap();
                return true;
            case R.id.route4Bus2Id:
                selectedBusUserId = Constants.route4bus2id;
                updateMap();
                return true;
            case R.id.route4Bus3Id:
                selectedBusUserId = Constants.route4bus3id;
                updateMap();
                return true;
            case R.id.route4Bus4Id:
                selectedBusUserId = Constants.route4bus4id;
                updateMap();
                return true;
            case R.id.route4Bus5Id:
                selectedBusUserId = Constants.route4bus5id;
                updateMap();
                return true;

            //Route 5
            case R.id.route5Bus1Id:
                selectedBusUserId = Constants.route5bus1id;
                updateMap();
                return true;
            case R.id.route5Bus2Id:
                selectedBusUserId = Constants.route5bus2id;
                updateMap();
                return true;
            case R.id.route5Bus3Id:
                selectedBusUserId = Constants.route5bus3id;
                updateMap();
                return true;
            case R.id.route5Bus4Id:
                selectedBusUserId = Constants.route5bus4id;
                updateMap();
                return true;
            case R.id.route5Bus5Id:
                selectedBusUserId = Constants.route5bus5id;
                updateMap();
                return true;


            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateMap() {
        databaseReference= FirebaseDatabase.getInstance().getReference().child("Admin").child(selectedBusUserId);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Toast.makeText(FindBusActivity.this, "  snapshot2", Toast.LENGTH_SHORT).show();

                if (dataSnapshot.getValue() != null) {
                    admin = dataSnapshot.getValue(Admin.class);
                    Log.d("popo",""+dataSnapshot.toString());
                    Log.d("popo",""+admin.toString());


                    if(!(admin.getLat().isEmpty() && admin.getLng().isEmpty())) {
                        Log.d("popop",""+admin.toString());
                        lat = Double.valueOf(admin.getLat());
                        lng = Double.valueOf(admin.getLng());
                        displayLocation();
                    }else{
                        Toast.makeText(FindBusActivity.this, "Driver Location off"+dataSnapshot.toString(), Toast.LENGTH_SHORT).show();
                        Log.d("popo",""+dataSnapshot.toString());
                    }

                }else {
                    Toast.makeText(FindBusActivity.this, "  null", Toast.LENGTH_SHORT).show();

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        tvDriverContract.setText(admin.getPhone());
        Toast.makeText(this, "update map from "+selectedBusUserId+ admin.getPhone(), Toast.LENGTH_SHORT).show();
    }

}
