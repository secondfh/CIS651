package com.example.puppatrol;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Calendar;

import android.os.Handler;
import android.renderscript.Sampler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryDataEventListener;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class MyRecyclerAdapter
        extends RecyclerView.Adapter<MyRecyclerAdapter.ViewHolder> {


    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference usersRef = database.getReference("Posts");
    DatabaseReference requestRef = database.getReference("Requests");
    ChildEventListener usersRefListener;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private List<String> keyList;
    private HashMap<String, PostModel> key_to_Post;
    private RecyclerView r;
    private Marker currentMarker = null;
    private ItemClickListener itemClickListener;
    public String refKey;


    public MyRecyclerAdapter(HashMap<String, PostModel> kp, List<String> kl, ItemClickListener _itemClickListener, RecyclerView recyclerView) {
        r = recyclerView;
        keyList = kl;
        key_to_Post = kp;
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        itemClickListener = _itemClickListener;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view, parent, false);
        final ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final PostModel u = key_to_Post.get(keyList.get(position));
        String uid = u.uid;
        LocationManager locationManager;

        if (ActivityCompat.checkSelfPermission(r.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(r.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        locationManager = (LocationManager)r.getContext().getSystemService(r.getContext().LOCATION_SERVICE);
        final Location locationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        holder.uref = database.getReference("Posts/" + u.postKey);
        holder.uref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                double lat = locationGPS.getLatitude();
                double longi = locationGPS.getLongitude();
                float[] result = new float[1];
                String wlat, wlong;
                wlat = dataSnapshot.child("lat").getValue().toString();
                wlong = dataSnapshot.child("lng").getValue().toString();
                Location.distanceBetween(lat, longi, Double.parseDouble(wlat),Double.parseDouble(wlong), result);
                double conv = result[0] / 1609;
                holder.distance.setText(roundTwoDecimals(conv) + " miles");
                u.m.setPosition(new LatLng(Double.parseDouble(wlat), Double.parseDouble(wlong)));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        double lat = locationGPS.getLatitude();
        double longi = locationGPS.getLongitude();
        float[] result = new float[1];
        Location.distanceBetween(lat, longi, Double.parseDouble(u.lat),Double.parseDouble(u.longi), result);
        double conv = result[0] / 1609;
        holder.distance.setText(roundTwoDecimals(conv) + " miles");
                if(holder.uref!=null && holder.urefListener!=null)
                {
                    holder.uref.removeEventListener(holder.urefListener);
                }

                final FirebaseDatabase database = FirebaseDatabase.getInstance();
                holder.uref = database.getReference("Users").child(uid);
                holder.uref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        holder.fname_v.setText("First Name: " +dataSnapshot.child("displayname").getValue().toString());
                        holder.email_v.setText("Email:  " + dataSnapshot.child("email").getValue().toString());
                        holder.phone_v.setText("Phone Num:  " + dataSnapshot.child("phone").getValue().toString());
                        holder.date_v.setText("Date Created: "+u.date);
                        if(dataSnapshot.child("url").exists()){
                            Picasso.get().load(dataSnapshot.child("url").getValue().toString()).into(holder.imageButton);

                        } else{
                            holder.imageButton.setImageDrawable(ContextCompat.getDrawable(holder.imageButton.getContext(), R.mipmap.ic_launcher));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                holder.uref = database.getReference("Users").child(uid);
                holder.uref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        //add if statement to handle if no rating
                        if(!dataSnapshot.child("walker_rating").exists()){
                            holder.ratingBar.setRating(0);}
                        else{
                        holder.ratingBar.setRating(Float.parseFloat(dataSnapshot.child("walker_rating").getValue().toString()));
                        holder.reviewcount.setText("(" + dataSnapshot.child("walker_reviews").getValue().toString() + ")");
                    }}

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                holder.imageButton.setOnClickListener(new View.OnClickListener(){

                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(r.getContext(), MessageActivity.class);
                        intent.putExtra("Users", u.uid);
                        r.getContext().startActivity(intent);
                    }
                });

                holder.imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (currentMarker!=null)
                            currentMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.green_dog));

                        u.m.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.green_dog));
                        currentMarker=u.m;
                        if (itemClickListener!=null)
                            itemClickListener.onItmeClick(currentMarker.getPosition());
                    }
                });

                StorageReference pathReference = FirebaseStorage.getInstance().getReference("images/"+u.url);
                pathReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).into(holder.imageView);
                    }
                });

                holder.reqbtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final String time = null;
                        final String status = r.getContext().getResources().getString(R.string.request_created);

                        if(holder.reqbtn.getText().toString().equals("Request") ){
                            final EditText offer = new EditText(r.getContext());
                            final AlertDialog.Builder builder = new AlertDialog.Builder(r.getContext());
                            offer.setHint("Enter Offer and Walk Duration");
                            builder.setView(offer);
                            builder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    final String offer1 = offer.getText().toString();
                                    RequestInfo(currentUser.getUid(), u.postKey , status, u.uid, offer1);
                                }
                            });
                            AlertDialog alert = builder.create();
                            alert.show();
                            holder.reqbtn.setText("Cancel");

                        }
                        else{
                            if(holder.reqbtn.getText().toString().equals("Cancel")){
                                holder.reqbtn.setText("Request");
                                requestRef.child(u.postKey).removeValue();

                            }
                        }

                    }
                });

                holder.rref = requestRef.child(u.postKey);
                holder.rref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(!snapshot.exists()){
                            //do nothing
                        }
                        else{if(snapshot.child("status").getValue().toString().equals("accepted")){
                            holder.reqbtn.setText("In Progress");
                            holder.reqbtn.setBackgroundColor(Color.parseColor("#32AD03"));
                        }else{
                            if(snapshot.child("status").getValue().toString().equals("rejected")){

                                requestRef.child(u.postKey).removeValue();
                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {

                                        holder.reqbtn.setText("Request");
                                        holder.reqbtn.setBackgroundResource(R.drawable.signup_button);


                                    }
                                }, 2000);
                                holder.reqbtn.setText("Rejected");
                                holder.reqbtn.setBackgroundColor(Color.parseColor("#FF0000"));


                            }
                        }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


            }

            double roundTwoDecimals(double d)
            {
                DecimalFormat twoDForm = new DecimalFormat("#.##");
                return Double.valueOf(twoDForm.format(d));
            }
            public void removeListener(){
                if(usersRef!=null && usersRefListener!=null)
                    usersRef.removeEventListener(usersRefListener);
            }

            @Override
            public int getItemCount() {
                return keyList.size();
            }

            public void RequestInfo(String client, String requestKey, String status, String walker, String offer) {

                //DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("client", client);
                hashMap.put("timestamp", ServerValue.TIMESTAMP);
                hashMap.put("status", status);
                hashMap.put("walker", walker);
                hashMap.put("offer", offer);

                //refKey = reference.child("Requests").push().getKey();
                requestRef.child(requestKey).setValue(hashMap);

            }

            public void statusChange(){

            }





            public static class ViewHolder extends RecyclerView.ViewHolder{
                public TextView fname_v;
                public TextView email_v;
                public TextView phone_v;
                public TextView date_v;
                public TextView distance, reviewcount;
                public ImageView imageView;
                public ImageButton imageButton;
                public RatingBar ratingBar;
                public Button reqbtn;
                public String lati, longit;
                DatabaseReference uref, rref;
                ValueEventListener urefListener;

                public ViewHolder(View v){
                    super(v);
                    fname_v = v.findViewById(R.id.fname_view);
                    email_v =  v.findViewById(R.id.email_view);
                    phone_v =  v.findViewById(R.id.phone_view);
                   date_v =  v.findViewById(R.id.date_view);
                   ratingBar=v.findViewById(R.id.rating);
                   imageView=v.findViewById(R.id.postImg);
                   imageButton = v.findViewById(R.id.profilePic);
                   reqbtn = v.findViewById(R.id.requestbtn);
                   distance = v.findViewById(R.id.distance);
                   reviewcount = v.findViewById(R.id.review_count);
                }
            }

}
