package com.example.puppatrol;

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.renderscript.Sampler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
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
                if(holder.uref!=null && holder.urefListener!=null)
                {
                    holder.uref.removeEventListener(holder.urefListener);
                }
                if(holder.likesRef!=null && holder.likesRefListener!=null)
                {
                    holder.likesRef.removeEventListener(holder.likesRefListener);
                }
                if(holder.likeCountRef!=null && holder.likeCountRefListener!=null)
                {
                    holder.likeCountRef.removeEventListener(holder.likeCountRefListener);
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
                        if(dataSnapshot.child("profilePicture").exists()){
                            Picasso.get().load(dataSnapshot.child("profilePicture").getValue().toString()).into(holder.imageButton);

                        } else{
                            holder.imageButton.setImageDrawable(ContextCompat.getDrawable(holder.imageButton.getContext(), R.mipmap.ic_launcher));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                holder.likeCountRef=
                database.getReference("Posts/"+u.postKey+"/likeCount");
                Log.d("LIKEC ", u.postKey);
               holder.likeCountRefListener=holder.likeCountRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        //Log.d("CRASH", dataSnapshot.toString());
                        if(dataSnapshot.getValue()!=null)
                         holder.likeCount.setText(dataSnapshot.getValue().toString()+" Likes");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

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
                holder.description_v.setText(u.description);
                StorageReference pathReference = FirebaseStorage.getInstance().getReference("images/"+u.url);
                pathReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).into(holder.imageView);
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
            public static class ViewHolder extends RecyclerView.ViewHolder{
                public TextView fname_v;
                public TextView email_v;
                public TextView phone_v;
                public TextView date_v;
                public TextView description_v;
                public ImageView imageView;
                public  ImageView likeBtn;
                public TextView likeCount;
                public ImageButton imageButton;
                DatabaseReference uref;
                ValueEventListener urefListener;

                DatabaseReference likeCountRef;
                ValueEventListener likeCountRefListener;

                DatabaseReference likesRef;
                ValueEventListener likesRefListener;
                public ViewHolder(View v){
                    super(v);
                    fname_v = (TextView) v.findViewById(R.id.fname_view);
                    email_v = (TextView) v.findViewById(R.id.email_view);
                    phone_v = (TextView) v.findViewById(R.id.phone_view);
                   date_v = (TextView) v.findViewById(R.id.date_view);
                   description_v=v.findViewById(R.id.description);
                   imageView=v.findViewById(R.id.postImg);
                   imageButton = v.findViewById(R.id.profilePic);
                }
            }

}
