package com.example.puppatrol;

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Calendar;
import android.renderscript.Sampler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryDataEventListener;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
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
        extends RecyclerView.Adapter<MyRecyclerAdapter.ViewHolder>
        {


            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference usersRef = database.getReference("Posts");
            ChildEventListener usersRefListener;
            private FirebaseAuth mAuth;
            private FirebaseUser currentUser;
            private List<String> keyList;
            private HashMap<String,PostModel> key_to_Post;
            private RecyclerView r;
            private Marker currentMarker =null;
            private  ItemClickListener itemClickListener;

            public MyRecyclerAdapter(HashMap<String,PostModel> kp, List<String> kl, ItemClickListener _itemClickListener, RecyclerView recyclerView){
                r = recyclerView;
                keyList=kl;
                key_to_Post= kp;
                mAuth = FirebaseAuth.getInstance();
                currentUser = mAuth.getCurrentUser();
                itemClickListener =_itemClickListener;

            }
            @NonNull
            @Override
            public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view, parent,false);
                final ViewHolder vh = new ViewHolder(v);
                return vh;
            }

            @Override
            public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
                final PostModel u =key_to_Post.get(keyList.get(position));
                String uid=u.uid;
                String postid = database.getReference("Posts").getKey();
                final Date dateTime = Calendar.getInstance().getTime();
                final SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                final Float mrating = u.rating;
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

                holder.uref = database.getReference("Posts").child(postid);
                holder.uref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        holder.ratingBar.setRating(mrating);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });



                holder.ratingBar.setRating(Float.parseFloat(u.rating.toString()));
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
                        String time = sdf.format(dateTime);
                        RequestInfo(currentUser.getUid(), time , "started", u.uid);
                    }
                });



            }
            public void removeListener(){
                if(usersRef!=null && usersRefListener!=null)
                    usersRef.removeEventListener(usersRefListener);
            }

            @Override
            public int getItemCount() {
                return keyList.size();
            }

            public void RequestInfo(String client, String requestDate, String status, String walker) {

                DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("client", client);
                hashMap.put("requestDate", requestDate);
                hashMap.put("status", status);
                hashMap.put("walker", walker);

                reference.child("Requests").push().setValue(hashMap);

            }



            public static class ViewHolder extends RecyclerView.ViewHolder{
                public TextView fname_v;
                public TextView email_v;
                public TextView phone_v;
                public TextView date_v;
                public TextView description_v;
                public ImageView imageView;
                public ImageButton imageButton;
                public RatingBar ratingBar;
                public Button reqbtn;
                DatabaseReference uref;
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
                }
            }

}
