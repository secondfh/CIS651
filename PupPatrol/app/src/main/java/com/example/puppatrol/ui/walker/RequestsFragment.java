package com.example.puppatrol.ui.walker;


import android.content.Context;
import android.content.res.Resources;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.puppatrol.R;
import com.example.puppatrol.RequestListAdapter;
import com.example.puppatrol.RequestListGroup;
import com.example.puppatrol.RequestListItem;
import com.example.puppatrol.WalkRequest;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class RequestsFragment extends Fragment{
    private static final int REQ_NOT_FOUND = -1;

    private ExpandableListView requestListView;
    private Context mContext;
    private Resources res;
    private ArrayList<String> statusValue;
    List<RequestListGroup> requestGroups;;
    private RequestListAdapter requestListAdapter;
    private String currentUserId;
    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private final DatabaseReference requestsRef = database.getReference("Requests");
    private double postLat, postLng;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        res = context.getResources();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        statusValue = new ArrayList<>();
        statusValue.add(res.getString(R.string.request_created));
        statusValue.add(res.getString(R.string.request_accepted));
        statusValue.add(res.getString(R.string.request_started));
        statusValue.add(res.getString(R.string.request_completed));
        requestGroups = new ArrayList<>();
        requestGroups.add(new RequestListGroup(statusValue.get(0)));
        requestGroups.add(new RequestListGroup(statusValue.get(1)));
        requestGroups.add(new RequestListGroup(statusValue.get(2)));
        requestGroups.add(new RequestListGroup(statusValue.get(3)));
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_walker_requests, container, false);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        requestListView = view.findViewById(R.id.walker_request_list);
        requestListAdapter = new RequestListAdapter(requestListView, requestGroups);
        requestListView.setAdapter(requestListAdapter);
        DatabaseReference uref = database.getReference("Users").child(currentUserId);
        uref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    if (snapshot.child("currentpost").exists()){
                        String currentPost = snapshot.child("currentpost").getValue(String.class);
                        DatabaseReference postRef = database.getReference("Posts").child(currentPost);
                        postRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()){
                                    postLat = Location.convert(snapshot.child("lat").getValue(String.class));
                                    postLng = Location.convert(snapshot.child("lng").getValue(String.class));
                                    requestListAdapter.updateMyLocation(postLat, postLng);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        requestsRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String requestKey = snapshot.getKey();
                WalkRequest request = snapshot.getValue(WalkRequest.class);
                if (request.getWalker().equals(currentUserId) && statusValue.contains(request.getStatus())){
                    requestGroups.get(statusValue.indexOf(request.getStatus())).addItem(new RequestListItem(requestKey, request));
                    requestListAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String requestKey = snapshot.getKey();
                WalkRequest request = snapshot.getValue(WalkRequest.class);
                if (request.getWalker().equals(currentUserId) && statusValue.contains(request.getStatus())) {
                    int group = findRequestGroup(requestKey);
                    /* Should always exist for onChildChanged event */
                    if (group != REQ_NOT_FOUND) {
                        requestGroups.get(group).removeItemByKey(requestKey);
                        requestGroups.get(statusValue.indexOf(request.getStatus())).addItem(new RequestListItem(requestKey, request));
                        requestListAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                String requestKey = snapshot.getKey();
                WalkRequest request = snapshot.getValue(WalkRequest.class);
                if (request.getWalker().equals(currentUserId) && statusValue.contains(request.getStatus())){
                    int group = findRequestGroup(requestKey);
                    /* Should always exist for onChildRemoved event */
                    if (group != REQ_NOT_FOUND){
                        requestGroups.get(group).removeItemByKey(requestKey);
                        requestListAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private int findRequestGroup(String key){
        int retVal = REQ_NOT_FOUND;
        for (int i = 0; i < requestGroups.size(); i++){
            if (requestGroups.get(i).getItemByKey(key) != null){
                retVal = i;
                break;
            }
        }

        return retVal;
    }
}