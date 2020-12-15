package com.example.puppatrol.ui.walker;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatRatingBar;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.example.puppatrol.PostModel;
import com.example.puppatrol.R;
import com.example.puppatrol.WalkerActivity2;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryDataEventListener;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;
import java.util.List;

public class HomeFragment extends Fragment implements OnMapReadyCallback {
    private static final int REQUEST_FOR_LOCATION = 0012;
    private long UPDATE_INTERVAL = 10 * 1000;  /* 10 secs */
    private long FASTEST_INTERVAL = 2000; /* 2 sec */
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private String currentUid, postKey;
    private DatabaseReference currentUserRef, requestsRef, postsRef;
    TextView reqPendingView, reqAcceptedView, reqStartedView, reqCompletedView, numReviewsView;
    AppCompatRatingBar ratingBar;
    SwitchCompat statusSwitch, availabilitySwitch;
    private int reqPending, reqAccepted, reqStarted, reqCompleted;
    private Context mContext;
    private LocationCallback locationCallback;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest mLocationRequest;
    private GeoQuery geoQuery = null;
    private GoogleMap mMap;
    final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("/geofire");
    final GeoFire geoFire = new GeoFire(ref);

    public static class WalkerPost {
        public String uid;
        public String url;
        public Object timestamp;
        public String description;
        public String lat, lng;
        public boolean available;

        public WalkerPost(String uid, String url, String description, String lat, String Lng) {
            this.uid = uid;
            this.url = url;
            this.description = description;
            this.timestamp = ServerValue.TIMESTAMP;
            this.lat = lat;
            this.lng = Lng;
            this.available = true;
        }

        public WalkerPost() {

        }

    }

    public HomeFragment() {

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        currentUid = currentUser.getUid();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        currentUserRef = database.getReference("Users").child(currentUid);
        requestsRef = database.getReference("Requests");
        postsRef = database.getReference("Posts");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_walker_home2, container, false);
        reqPendingView = root.findViewById(R.id.walker_home_pending_requests);
        reqAcceptedView = root.findViewById(R.id.walker_home_accepted_requests);
        reqStartedView = root.findViewById(R.id.walker_home_started_requests);
        reqCompletedView = root.findViewById(R.id.walker_home_completed_requests);
        numReviewsView = root.findViewById(R.id.review_count);
        ratingBar = root.findViewById(R.id.walker_home_rating_bar);
        statusSwitch = root.findViewById(R.id.walker_switch_status);
        availabilitySwitch = root.findViewById(R.id.walker_switch_availability);

        currentUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("walker_rating").exists()){
                    ratingBar.setRating(snapshot.child("walker_rating").getValue(float.class));
                } else {
                    ratingBar.setRating(0f);
                }
                if (snapshot.child("walker_reviews").exists()){
                    String txt = "(" + snapshot.child("walker_reviews").getValue(int.class) + ")";
                    numReviewsView.setText(txt);
                }
                if (snapshot.child("currentpost").exists()){
                    statusSwitch.setChecked(true);
                    postKey = snapshot.child("currentpost").getValue(String.class);
                } else {
                    statusSwitch.setChecked(false);
                    postKey = null;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        requestsRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (snapshot.child("walker").getValue(String.class).equals(currentUid)){
                    String status = snapshot.child("status").getValue(String.class);
                    Resources resources = mContext.getResources();
                    if (status.equals(resources.getString(R.string.request_created)))
                        reqPending++;
                    if (status.equals(resources.getString(R.string.request_accepted)))
                        reqAccepted++;
                    if (status.equals(resources.getString(R.string.request_started)))
                        reqStarted++;
                    if (status.equals(resources.getString(R.string.request_complete)))
                        reqCompleted++;
                    reqPendingView.setText(Integer.toString(reqPending));
                    reqAcceptedView.setText(Integer.toString(reqAccepted));
                    reqStartedView.setText(Integer.toString(reqStarted));
                    reqCompletedView.setText(Integer.toString(reqCompleted));
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(20);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        LocationSettingsRequest locationSettingsRequest = builder.build();
        SettingsClient settingsClient = LocationServices.getSettingsClient(mContext);
        settingsClient.checkLocationSettings(locationSettingsRequest);
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                Location lastLocation = locationResult.getLastLocation();
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude())).zoom(14).build();
                if (mMap != null)
                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
        };
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.walker_map);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(mContext);
        if (mapFragment != null)
            mapFragment.getMapAsync(this);

        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(mContext, "We need permission to access your location.", Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_FOR_LOCATION);
            return root;
        }
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, locationCallback, Looper.getMainLooper());
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        statusSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                    if (postKey != null && !postKey.equals("")){
                        postsRef.child(postKey).removeValue();
                        geoFire.removeLocation(postKey);
                        currentUserRef.child("currentpost").removeValue();
                    }
                } else {
                    if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    mFusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            final String lat = String.valueOf(location.getLatitude());
                            final String lng = String.valueOf(location.getLongitude());
                            final DatabaseReference postRef = postsRef.push();
                            postKey = postRef.getKey();
                            postRef.setValue(new WalkerPost(currentUid, "", "", lat, lng))
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            geoFire.setLocation(postRef.getKey(), new GeoLocation(Double.parseDouble(lat),Double.parseDouble(lng)));
                                        }
                                    });
                            currentUserRef.child("currentpost").setValue(postKey);
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_FOR_LOCATION) {// If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //This next check is to eliminate a compilation error. We know that we have the permission here.
                if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                    return;
                }
                mFusedLocationClient.requestLocationUpdates(mLocationRequest, locationCallback, Looper.getMainLooper());
            } else {
                Toast.makeText(mContext, "The app will not perform correctly without your permission to access the device location.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            mContext, R.raw.darkmapstyle));

            if (!success) {
                Log.e("UTARZ", "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e("UTARZ", "Can't find style. Error: ", e);
        }
        mMap = googleMap;


        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                return false;
            }
        });
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
    }
}
