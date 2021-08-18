package com.example.aryobee.MapActivity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.bumptech.glide.Glide;
import com.example.aryobee.About.About;
import com.example.aryobee.Feedback.RateActivity_of_Customer;
import com.example.aryobee.HistoryActivity;
import com.example.aryobee.Message.OwnerMessaging;
import com.example.aryobee.R;
import com.example.aryobee.Splash.MainActivity;
import com.example.aryobee.UsersAccountManagment.CustomerSettingsActivity;
import com.example.aryobee.UsersAccountManagment.commons;
import com.example.aryobee.UsersAccountManagment.userModel;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.ListenerRegistration;
import com.mxn.soul.flowingdrawer_core.FlowingDrawer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class CustomerMapActivity extends FragmentActivity implements OnMapReadyCallback {

    FlowingDrawer mDrawer;
    boolean firstloadMap=true;
    private GoogleMap mMap;
    Location mLastLocation;
    LatLng myLocation;
    LocationRequest mLocationRequest;
    ImageButton toolbar;
    CircleImageView profilepic;
    TextView customername,customerphone;

    ListenerRegistration RequestStatusListener;

    private FusedLocationProviderClient mFusedLocationClient;

    private Button mLogout, mRequest, mSettings, mHistory,mAbout,mMessage;

    private LatLng pickupLocation;

    private Boolean requestBol = false;

    private Marker pickupMarker;

    private SupportMapFragment mapFragment;

    private String destination, requestService;

    private LatLng destinationLatLng;

    private LinearLayout mMechanicInfo;

    private ImageView mMechanicProfileImage;

    private TextView mMechanicName, mMechanicPhone, mMechanicCarPlate;
  //  private RatingBar mRatingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_costumer_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.


        mDrawer = (FlowingDrawer) findViewById(R.id.drawerlayout);
        toolbar=findViewById(R.id.iv_Menu);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        profilepic=findViewById(R.id.customer_pp);
        customername=findViewById(R.id.customerprofile_name);
        customerphone=findViewById(R.id.customerprofile_phone);

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        destinationLatLng = new LatLng(0.0,0.0);

        mMechanicInfo = (LinearLayout) findViewById(R.id.mechanicInfo);

        mMechanicProfileImage = findViewById(R.id.mechanicProfileImage);

        mMechanicName = (TextView) findViewById(R.id.mechanicName);
        mMechanicPhone = (TextView) findViewById(R.id.mechanicPhone);
        mMechanicCarPlate = (TextView) findViewById(R.id.mechanicCar);

   //     mRatingBar = (RatingBar) findViewById(R.id.ratingBar);

        mLogout = (Button) findViewById(R.id.logout);
        mRequest = (Button) findViewById(R.id.request);
        mSettings = (Button) findViewById(R.id.settings);
        mHistory = (Button) findViewById(R.id.history);
        mMessage = (Button) findViewById(R.id.cmsg);
        mAbout = (Button) findViewById(R.id.about);

        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               mDrawer.openMenu(true);
            }
        });

        mLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(CustomerMapActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
                return;
            }
        });

        mRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (requestBol){
                    endRide();
                }else{
                    requestBol = true;

                    String userId = commons.mAuth.getUid();
                    DatabaseReference ref = commons.users.child(userId);
                    GeoFire geoFire = new GeoFire(ref);
                    geoFire.setLocation("location", new GeoLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude()), new GeoFire.CompletionListener() {
                        @Override
                        public void onComplete(String key, DatabaseError error) {
                            if(error!=null){
                            Toast.makeText(CustomerMapActivity.this,error.getMessage(),Toast.LENGTH_LONG).show();
                            }else{
                                pickupLocation = myLocation;
                                pickupMarker = mMap.addMarker(new MarkerOptions().position(pickupLocation).title("Pickup Here").icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_pickup)));
                                mRequest.setText("Getting your Mechanic....");
                                getClosestMechnic();
                            }
                        }
                    });
                }
            }
        });
        mSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CustomerMapActivity.this, CustomerSettingsActivity.class);
                startActivity(intent);
                return;
            }
        });

        mHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CustomerMapActivity.this, HistoryActivity.class);
                intent.putExtra("customerOrMechanic", "Customers");
                startActivity(intent);
                return;
            }
        });
        mMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CustomerMapActivity.this, OwnerMessaging.class);
                // intent.putExtra("customerOrMechanic", "Customers");
                startActivity(intent);
                return;
            }
        });
        mAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CustomerMapActivity.this, About.class);
               // intent.putExtra("customerOrMechanic", "Customers");
                startActivity(intent);
                return;
            }
        });


        onRequestStatusChange();
        loadProfileData();
    }
    private int radius = 1;
    private Boolean mechanicFound = false;
    private String mechanicFoundID;

    GeoQuery geoQuery;
    private void getClosestMechnic(){
        DatabaseReference mechanicLocation = commons.mechanicsAvailable;

        GeoFire geoFire = new GeoFire(mechanicLocation);
        geoQuery = geoFire.queryAtLocation(new GeoLocation(pickupLocation.latitude, pickupLocation.longitude), radius);
        geoQuery.removeAllListeners();

        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                if (!mechanicFound && requestBol){
                    DatabaseReference mCustomerDatabase = commons.users.child(key);
                    mCustomerDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0){
                                Map<String, Object> mechanicMap = (Map<String, Object>) dataSnapshot.getValue();
                                if (mechanicFound){
                                    return;
                                }
                                mechanicFound = true;
                                mechanicFoundID = dataSnapshot.getKey();
                                commons.assignedMechanicID = mechanicFoundID;

                                    DatabaseReference mechanicRef = commons.users.child(mechanicFoundID).child("customerRequest");
                                    String customerId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                    HashMap map = new HashMap();
                                    map.put("customerRideId", customerId);
                                    map.put("destination", destination);
                                    map.put("destinationLat", destinationLatLng.latitude);
                                    map.put("destinationLng", destinationLatLng.longitude);
                                mechanicRef.setValue(map);

                                    getMechanicLocation();
                                    getMechanicInfo();
                                    getHasRideEnded();
                                    mRequest.setText("Looking for Mechanic Location....");

                            }
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
                }
            }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {
                if (!mechanicFound)
                {
                    radius++;
                    getClosestMechnic();
                }
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });
    }
    /*-------------------------------------------- Map specific functions -----
    |  Function(s) getMechanicLocation
    |
    |  Purpose:  Get's most updated mechanic location and it's always checking for movements.
    |
    |  Note:
    |	   Even tho we used geofire to push the location of the mechanic we can use a normal
    |      Listener to get it's location with no problem.
    |
    |      0 -> Latitude
    |      1 -> Longitudde
    |
    *-------------------------------------------------------------------*/
    private Marker mMechanicMarker;
    private DatabaseReference mechanicLocationRef;
    private ValueEventListener mechanicLocationRefListener;

    private void getMechanicLocation(){
        mechanicLocationRef = commons.mechanicsWorking.child(mechanicFoundID).child("l");
        mechanicLocationRefListener = mechanicLocationRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && requestBol){
                    List<Object> map = (List<Object>) dataSnapshot.getValue();
                    double locationLat = 0;
                    double locationLng = 0;
                    if(map.get(0) != null){
                        locationLat = Double.parseDouble(map.get(0).toString());
                    }
                    if(map.get(1) != null){
                        locationLng = Double.parseDouble(map.get(1).toString());
                    }
                    LatLng mechanicLatLng = new LatLng(locationLat,locationLng);
                    if(mMechanicMarker != null){
                        mMechanicMarker.remove();
                    }
                    Location loc1 = new Location("");
                    loc1.setLatitude(pickupLocation.latitude);
                    loc1.setLongitude(pickupLocation.longitude);

                    Location loc2 = new Location("");
                    loc2.setLatitude(mechanicLatLng.latitude);
                    loc2.setLongitude(mechanicLatLng.longitude);

                    float distance = loc1.distanceTo(loc2);

                    if (distance<100){
                        mRequest.setText("Mechanic's Here");
                    }else{
                        mRequest.setText("Mechanic Found: " + String.valueOf(distance));
                    }

                    mMechanicMarker = mMap.addMarker(new MarkerOptions().position(mechanicLatLng).title("your mechanic").icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_car)));
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }

    /*-------------------------------------------- getMechanicInfo -----
    |  Function(s) getMechanicInfo
    |
    |  Purpose:  Get all the user information that we can get from the user's database.
    |
    |  Note: --
    |
    *-------------------------------------------------------------------*/
    private void getMechanicInfo(){
        mMechanicInfo.setVisibility(View.VISIBLE);
        DatabaseReference mCustomerDatabase = commons.Mechanics.child(mechanicFoundID);
        mCustomerDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0){
                    if(dataSnapshot.child("name")!=null){
                        mMechanicName.setText(dataSnapshot.child("name").getValue().toString());
                    }
                    if(dataSnapshot.child("phone")!=null){
                        mMechanicPhone.setText(dataSnapshot.child("phone").getValue().toString());
                    }
                    if(dataSnapshot.child("car")!=null){
                        mMechanicCarPlate.setText(dataSnapshot.child("car").getValue().toString());
                    }
                    if(dataSnapshot.child("profileImageUrl").getValue()!=null){
                        String img=dataSnapshot.child("profileImageUrl").getValue().toString();
                        Glide.with(getApplication()).load(img).into(mMechanicProfileImage);
                    }
/*
                    int ratingSum = 0;
                    float ratingsTotal = 0;
                    float ratingsAvg = 0;
                    for (DataSnapshot child : dataSnapshot.child("rating").getChildren()){
                        ratingSum = ratingSum + Integer.valueOf(child.getValue().toString());
                        ratingsTotal++;
                    }
                    if(ratingsTotal!= 0){
                        ratingsAvg = ratingSum/ratingsTotal;
                        mRatingBar.setRating(ratingsAvg);
                    }
                    */
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }

    private DatabaseReference mechanicHasEndedRef;
    private ValueEventListener mechanicHasEndedRefListener;

    private void getHasRideEnded(){
        mechanicHasEndedRef = commons.users.child(mechanicFoundID).child("customerRequest").child("customerRideId");
        mechanicHasEndedRefListener = mechanicHasEndedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){

                }else{
                    endRide();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void endRide(){
        requestBol = false;
        geoQuery.removeAllListeners();
        mechanicLocationRef.removeEventListener(mechanicLocationRefListener);
        mechanicHasEndedRef.removeEventListener(mechanicHasEndedRefListener);

        if (mechanicFoundID != null){
            DatabaseReference mechanicRef = commons.users.child(mechanicFoundID).child("customerRequest");
            mechanicRef.removeValue();
            mechanicFoundID = null;

        }
        mechanicFound = false;
        radius = 1;
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DatabaseReference ref = commons.customerRequest;
        GeoFire geoFire = new GeoFire(ref);
        geoFire.removeLocation(userId, new GeoFire.CompletionListener() {
            @Override
            public void onComplete(String key, DatabaseError error) {

            }
        });

        if(pickupMarker != null){
            pickupMarker.remove();
        }
        if (mMechanicMarker != null){
            mMechanicMarker.remove();
        }
        mRequest.setText("call Mechanic");

        mMechanicInfo.setVisibility(View.GONE);
        mMechanicName.setText("");
        mMechanicPhone.setText("");
        mMechanicCarPlate.setText("Destination: --");
        mMechanicProfileImage.setImageResource(R.mipmap.ic_default_user);
    }

    /*-------------------------------------------- Map specific functions -----
    |  Function(s) onMapReady, buildGoogleApiClient, onLocationChanged, onConnected
    |
    |  Purpose:  Find and update user's location.
    |
    |  Note:
    |	   The update interval is set to 1000Ms and the accuracy is set to PRIORITY_HIGH_ACCURACY,
    |      If you're having trouble with battery draining too fast then change these to lower values
    |
    |
    *-------------------------------------------------------------------*/
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                mMap.setMyLocationEnabled(true);
                mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
            }else{
                checkLocationPermission();
            }
        }
    }

    LocationCallback mLocationCallback = new LocationCallback(){
        @Override
        public void onLocationResult(LocationResult locationResult) {
            for(Location location : locationResult.getLocations()){
                if(getApplicationContext()!=null){
                    mLastLocation = location;

                    LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
                    myLocation=latLng;
                    if(firstloadMap){
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                        mMap.animateCamera(CameraUpdateFactory.zoomTo(11));
                        firstloadMap=false;
                    }
                    else{

                    }
                    if(!getMechanicsAroundStarted)
                        getMechanicsAround();
                }
            }
        }
    };

    /*-------------------------------------------- onRequestPermissionsResult -----
    |  Function onRequestPermissionsResult
    |
    |  Purpose:  Get permissions for our app if they didn't previously exist.
    |
    |  Note:
    |	requestCode: the nubmer assigned to the request that we've made. Each
    |                request has it's own unique request code.
    |
    *-------------------------------------------------------------------*/
    private void checkLocationPermission() {
        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                new android.app.AlertDialog.Builder(this)
                        .setTitle("give permission")
                        .setMessage("give permission message")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ActivityCompat.requestPermissions(CustomerMapActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                            }
                        })
                        .create()
                        .show();
            }
            else{
                ActivityCompat.requestPermissions(CustomerMapActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
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

    boolean getMechanicsAroundStarted = false;
    List<Marker> markers = new ArrayList<Marker>();
    private void getMechanicsAround(){
        getMechanicsAroundStarted = true;
        DatabaseReference mechanicLocation = commons.mechanicsAvailable;

        GeoFire geoFire = new GeoFire(mechanicLocation);
        GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(mLastLocation.getLongitude(), mLastLocation.getLatitude()), 999999999);

        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {

                for(Marker markerIt : markers){
                    if(markerIt.getTag().equals(key))
                        return;
                }

                LatLng mechanicLocation = new LatLng(location.latitude, location.longitude);

                Marker mMechanicMarker = mMap.addMarker(new MarkerOptions().position(mechanicLocation).title(key).icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_car)));
                mMechanicMarker.setTag(key);

                markers.add(mMechanicMarker);
            }

            @Override
            public void onKeyExited(String key) {
                for(Marker markerIt : markers){
                    if(markerIt.getTag().equals(key)){
                        markerIt.remove();
                    }
                }
            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {
                for(Marker markerIt : markers){
                    if(markerIt.getTag().equals(key)){
                        markerIt.setPosition(new LatLng(location.latitude, location.longitude));
                    }
                }
            }

            @Override
            public void onGeoQueryReady() {
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });
    }

    public void onRequestStatusChange(){
        DatabaseReference databaseReference =commons.users.child(commons.mAuth.getCurrentUser().getUid());
         databaseReference.addValueEventListener(new ValueEventListener() {
             @Override
             public void onDataChange(@NonNull DataSnapshot snapshot) {
                 Map<String, Object> map=(Map<String, Object>) snapshot.getValue();
                 if(map.containsKey("status")){
                     if(map.get("status").toString().equals("1")){
                         startActivity(new Intent(getApplicationContext(), RateActivity_of_Customer.class));
                     }
                 }
             }

             @Override
             public void onCancelled(@NonNull DatabaseError error) {

             }
         });
        }

    public void loadProfileData(){
        commons.Customers.child(commons.mAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                userModel user;
                if(task.isSuccessful()){
                    user=task.getResult().getValue(userModel.class);
                    if(user!=null && user.getProfileImageUrl()!=null && user.getProfileImageUrl()!=""){
                        Uri url = Uri.parse(user.getProfileImageUrl());
                        Glide.with(CustomerMapActivity
                                .this).load(url).into(profilepic);
                        customername.setText(user.getName());
                        customerphone.setText(user.getPhone());
                    }
                }
            }});
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadProfileData();
    }
}
