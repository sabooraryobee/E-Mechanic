package com.example.aryobee.MapActivity;


import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.bumptech.glide.Glide;
import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.example.aryobee.About.About;
import com.example.aryobee.HistoryActivity;
import com.example.aryobee.Message.OwnerMessaging;
import com.example.aryobee.Models.Customer;
import com.example.aryobee.OnSopteSale.OnSpoteSale;
import com.example.aryobee.R;
import com.example.aryobee.Splash.MainActivity;
import com.example.aryobee.UsersAccountManagment.MechanicSettingsActivity;
import com.example.aryobee.UsersAccountManagment.commons;
import com.example.aryobee.UsersAccountManagment.userModel;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.mxn.soul.flowingdrawer_core.FlowingDrawer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class MechanicMapActivity extends FragmentActivity implements OnMapReadyCallback, RoutingListener {

    private GoogleMap mMap;
    Location mLastLocation;
    LocationRequest mLocationRequest;
    ImageButton toolbar;
    Customer customer;
    CircleImageView profilepic;

    FlowingDrawer mDrawar;

    private FusedLocationProviderClient mFusedLocationClient;
    private Button mLogout, mSettings, mRideStatus, mHistory,mAbout,mOnSpoteSale,completefix,mMessage;

    private Switch mWorkingSwitch;

    private int status = 1;

    private String customerId = "";
    String destination = "";
    private LatLng destinationLatLng, pickupLatLng;
    private float rideDistance;

    private Boolean isLoggingOut = false;

    private SupportMapFragment mapFragment;

    private LinearLayout mCustomerInfo;

     ImageView mCustomerProfileImage;

    private TextView mCustomerName, mCustomerPhone, mCustomerDestination;
    TextView username,userphone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mechanic_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        polylines = new ArrayList<>();
        mDrawar=findViewById(R.id.drawerlayoutm);
        toolbar=findViewById(R.id.iv_Menum);
        profilepic=findViewById(R.id.profilepic);


        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawar.openMenu(true);
            }
        });

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);



        mCustomerInfo = (LinearLayout) findViewById(R.id.customerInfo);

        mCustomerProfileImage =findViewById(R.id.customerProfileImage);

        mCustomerName = (TextView) findViewById(R.id.customerName);
        mCustomerPhone = (TextView) findViewById(R.id.customerPhone);
        mCustomerDestination = (TextView) findViewById(R.id.customerDestination);

        mWorkingSwitch = findViewById(R.id.workingSwitch);
        mWorkingSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked==true){
                    connectMechanic();
                }else {
                    disconnectMechanic();
                }
            }
        });

        mSettings = (Button) findViewById(R.id.settings);
        mLogout = (Button) findViewById(R.id.logout);
        mRideStatus = (Button) findViewById(R.id.rideStatus);
        mHistory = (Button) findViewById(R.id.history);
        mMessage = (Button) findViewById(R.id.mmsg);
        mAbout = (Button) findViewById(R.id.about);
        mOnSpoteSale = (Button) findViewById(R.id.OnSpS);
        username=findViewById(R.id.name);
        userphone=findViewById(R.id.phone);
        mRideStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch(status){
                    case 1:
                        erasePolylines();
                        if(destinationLatLng.latitude!=0.0 && destinationLatLng.longitude!=0.0){
                            getRouteToMarker(destinationLatLng);
                        }
                        mRideStatus.setText("Mechanic Arrived");
                        status=2;
                        break;
                    case 2:
                        recordRide();
                        endRide();
                        break;
                }
            }
        });

        mLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(MechanicMapActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
                return;
            }
        });

        mSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MechanicMapActivity.this, MechanicSettingsActivity.class);
                startActivity(intent);
                return;
            }
        });
        mHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MechanicMapActivity.this, HistoryActivity.class);
                intent.putExtra("customerOrMechanic", "Mechanics");
                startActivity(intent);
                return;
            }
        });
        mOnSpoteSale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MechanicMapActivity.this, OnSpoteSale.class);
               // intent.putExtra("customerOrMechanic", "Mechanics");
                startActivity(intent);
                return;
            }
        });
        mMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), OwnerMessaging.class);
                //    intent.putExtra("customerOrMechanic", "Mechanics");
                startActivity(intent);
                return;
            }
        });
        mAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MechanicMapActivity.this, About.class);
            //    intent.putExtra("customerOrMechanic", "Mechanics");
                startActivity(intent);
                return;
            }
        });

        getAssignedCustomer();
        loadProfileData();
    }

    private void getAssignedCustomer(){
        String mechanicId = commons.mAuth.getUid();
        DatabaseReference assignedCustomerRef = commons.users.child(mechanicId).child("customerRequest").child("customerRideId");
        assignedCustomerRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    customerId = dataSnapshot.getValue().toString();
                    getAssignedCustomerPickupLocation();
                    getAssignedCustomerDestination();
                    getAssignedCustomerInfo();
                }else{
                    endRide();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    Marker pickupMarker;
    private DatabaseReference assignedCustomerPickupLocationRef;
    private ValueEventListener assignedCustomerPickupLocationRefListener;
    private void getAssignedCustomerPickupLocation(){
        commons.customerID=customerId;
        assignedCustomerPickupLocationRef = commons.users.child(customerId).child("location").child("l");
        assignedCustomerPickupLocationRefListener = assignedCustomerPickupLocationRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && !customerId.equals("")){
                    List<Object> map = (List<Object>) dataSnapshot.getValue();
                    double locationLat = 0;
                    double locationLng = 0;
                    Log.i("ppppp",map.toString());
                    if(map.get(0) != null){
                        locationLat = Double.parseDouble(map.get(0).toString());
                    }
                    if(map.get(1) != null){
                        locationLng = Double.parseDouble(map.get(1).toString());
                    }
                    pickupLatLng = new LatLng(locationLat,locationLng);
                    pickupMarker = mMap.addMarker(new MarkerOptions().position(pickupLatLng).title("pickup location").icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_pickup)));
                    getRouteToMarker(pickupLatLng);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void getRouteToMarker(LatLng pickupLatLng) {
        if (pickupLatLng != null && mLastLocation != null){
            Routing routing = new Routing.Builder()
                    .travelMode(AbstractRouting.TravelMode.DRIVING)
                    .withListener(this)
                    .key("AIzaSyBYy0ibyCnsEhwe39BmMtPRS-3PAyFlMT8")
                    .alternativeRoutes(false)
                    .waypoints(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()), pickupLatLng)
                    .build();
            routing.execute();
        }
    }

    private void getAssignedCustomerDestination(){
        String mechanicId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference assignedCustomerRef = commons.users.child(mechanicId).child("customerRequest");
        assignedCustomerRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    if(map.get("destination")!=null){
                        destination = map.get("destination").toString();
                        mCustomerDestination.setText("Destination: " + destination);
                    }
                    else{
                        mCustomerDestination.setText("Destination: --");
                    }

                    Double destinationLat = 0.0;
                    Double destinationLng = 0.0;
                    if(map.get("destinationLat") != null){
                        destinationLat = Double.valueOf(map.get("destinationLat").toString());
                    }
                    if(map.get("destinationLng") != null){
                        destinationLng = Double.valueOf(map.get("destinationLng").toString());
                        destinationLatLng = new LatLng(destinationLat, destinationLng);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }


    private void getAssignedCustomerInfo(){
        mCustomerInfo.setVisibility(View.VISIBLE);
        DatabaseReference mCustomerDatabase = commons.Customers.child(this.customerId);
        mCustomerDatabase.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(task.getResult().exists()){
                    customer=new Customer();
                    customer=task.getResult().getValue(Customer.class);
                    if(customer!=null) {
                        mCustomerName.setText(customer.getName());
                        mCustomerPhone.setText(customer.getPhone());
                        if (!customer.getProfileImageUrl().equals("")) {
                            Glide.with(getApplication()).load(customer.getProfileImageUrl()).into(mCustomerProfileImage);
                        }
                    }
                }
            }
        });
    }


    private void endRide(){
        mRideStatus.setText("picked customer");
        erasePolylines();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference mechanicRef = commons.users.child(userId).child("customerRequest");
        mechanicRef.removeValue();

        DatabaseReference ref = commons.customerRequest;
        GeoFire geoFire = new GeoFire(ref);
        geoFire.removeLocation(customerId, new GeoFire.CompletionListener() {
            @Override
            public void onComplete(String key, DatabaseError error) {

            }
        });

        rideDistance = 0;
        Map<String,Object>map=new HashMap<>();
        map.put("status",1);
        commons.users.child(customerId).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                customerId="";
            }
        });

        if(pickupMarker != null){
            pickupMarker.remove();
        }
        if (assignedCustomerPickupLocationRefListener != null){
            assignedCustomerPickupLocationRef.removeEventListener(assignedCustomerPickupLocationRefListener);
        }
        mCustomerInfo.setVisibility(View.GONE);
        mCustomerName.setText("");
        mCustomerPhone.setText("");
        mCustomerDestination.setText("Destination: --");
        mCustomerProfileImage.setImageResource(R.mipmap.ic_default_user);
    }

    private void recordRide(){
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference mechanicRef = commons.users.child(userId).child("history");
        DatabaseReference customerRef = commons.users.child(customerId).child("history");
        String requestId = commons.history.push().getKey();
        mechanicRef.child(requestId).setValue(true);
        customerRef.child(requestId).setValue(true);

        HashMap map = new HashMap();
        map.put("mechanic", userId);
        map.put("customer", customerId);
   //     map.put("rating", 0);
        map.put("timestamp", getCurrentTimestamp());
        map.put("destination", destination);
        Log.i("lllllllllng",pickupLatLng.longitude+"");
        map.put("from-lat", pickupLatLng.latitude);
        map.put("from-lng", pickupLatLng.longitude);
        map.put("to-lat", destinationLatLng.latitude);
        map.put("to-lng", destinationLatLng.longitude);
        map.put("distance", rideDistance);
        commons.history.child(requestId).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

            }
        });
    }

    private Long getCurrentTimestamp() {
        Long timestamp = System.currentTimeMillis()/1000;
        return timestamp;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){

            }
            else {
                checkLocationPermission();
            }
        }
    }


    LocationCallback mLocationCallback = new LocationCallback(){
        @Override
        public void onLocationResult(LocationResult locationResult) {
            for(Location location : locationResult.getLocations()){
                if(getApplicationContext()!=null){

                    if(!customerId.equals("") && mLastLocation!=null && location != null){
                        rideDistance += mLastLocation.distanceTo(location)/1000;
                    }
                    mLastLocation = location;


                    LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(11));

                    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    DatabaseReference refAvailable = commons.mechanicsAvailable;
                    DatabaseReference refWorking = commons.mechanicsWorking;
                    GeoFire geoFireAvailable = new GeoFire(refAvailable);
                    GeoFire geoFireWorking = new GeoFire(refWorking);

                    switch (customerId){
                        case "":
                            geoFireWorking.removeLocation(customerId, new GeoFire.CompletionListener() {
                                @Override
                                public void onComplete(String key, DatabaseError error) {
                                }
                            });

                            geoFireAvailable.setLocation(userId, new GeoLocation(location.getLatitude(), location.getLongitude()), new GeoFire.CompletionListener() {
                                @Override
                                public void onComplete(String key, DatabaseError error) {

                                }
                            });
                            break;

                        default:
                            geoFireAvailable.removeLocation(userId, new GeoFire.CompletionListener() {
                                @Override
                                public void onComplete(String key, DatabaseError error) {
                                }
                            });
                            geoFireWorking.setLocation(userId, new GeoLocation(location.getLatitude(), location.getLongitude()), new GeoFire.CompletionListener() {
                                @Override
                                public void onComplete(String key, DatabaseError error) {

                                }
                            });

                            break;
                    }
                }
            }
        }
    };

    private void checkLocationPermission() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                new AlertDialog.Builder(this)
                        .setTitle("give permission")
                        .setMessage("give permission message")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ActivityCompat.requestPermissions(MechanicMapActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                            }
                        })
                    .create()
                    .show();
            }
            else{
                ActivityCompat.requestPermissions(MechanicMapActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch(requestCode){
            case 1:{
                if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                        mMap.setMyLocationEnabled(true);
                    }
                } else{
                    Toast.makeText(getApplicationContext(), "Please provide the permission", Toast.LENGTH_LONG).show();
                }
                break;
            }
        }
    }

    private void connectMechanic(){
        checkLocationPermission();
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
        mMap.setMyLocationEnabled(true);
    }

    private void disconnectMechanic(){
        if(mFusedLocationClient != null){
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
        }
        DatabaseReference ref = commons.mechanicsAvailable;
        GeoFire geoFire = new GeoFire(ref);
        geoFire.removeLocation(commons.mAuth.getCurrentUser().getUid(), new GeoFire.CompletionListener() {
            @Override
            public void onComplete(String key, DatabaseError error) {

            }
        });
    }

    private List<Polyline> polylines;
    private static final int[] COLORS = new int[]{R.color.primary_dark_material_light};
    @Override
    public void onRoutingFailure(RouteException e) {
        if(e != null) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }else {
            Toast.makeText(this, "Something went wrong, Try again", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onRoutingStart() {
    }
    @Override
    public void onRoutingSuccess(ArrayList<Route> route, int shortestRouteIndex) {
        if(polylines.size()>0) {
            for (Polyline poly : polylines) {
                poly.remove();
            }
        }

        polylines = new ArrayList<>();
        //add route(s) to the map.
        for (int i = 0; i <route.size(); i++) {

            //In case of more than 5 alternative routes
            int colorIndex = i % COLORS.length;

            PolylineOptions polyOptions = new PolylineOptions();
            polyOptions.color(getResources().getColor(COLORS[colorIndex]));
            polyOptions.width(10 + i * 3);
            polyOptions.addAll(route.get(i).getPoints());
            Polyline polyline = mMap.addPolyline(polyOptions);
            polylines.add(polyline);

            Toast.makeText(getApplicationContext(),"Route "+ (i+1) +": distance - "+ route.get(i).getDistanceValue()+": duration - "+ route.get(i).getDurationValue(),Toast.LENGTH_SHORT).show();
        }

    }
    @Override
    public void onRoutingCancelled() {
    }
    private void erasePolylines(){
        for(Polyline line : polylines){
            line.remove();
        }
        polylines.clear();
    }

    public void loadProfileData(){
        commons.Mechanics.child(commons.mAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                userModel user;
                if(task.isSuccessful()){
                user=task.getResult().getValue(userModel.class);
                if(user!=null && user.getProfileImageUrl()!=null && user.getProfileImageUrl()!=""){
                    Uri url = Uri.parse(user.getProfileImageUrl());
                    Glide.with(MechanicMapActivity
                    .this).load(url).into(profilepic);
                 username.setText(user.getName());
                 userphone.setText(user.getPhone());
                }
            }
        }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadProfileData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disconnectMechanic();
    }
}