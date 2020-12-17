package com.example.puppatrol;

import android.content.Context;
import android.content.res.Resources;
import android.database.DataSetObserver;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class RequestListAdapter extends BaseExpandableListAdapter {
    private final Context mContext;
    private final Resources res;
    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private List<RequestListGroup> requestGroups;
    private double myLat, myLng;
    private boolean locationSet = false;
    DecimalFormat format2d = new DecimalFormat("#.##");

    public RequestListAdapter(ExpandableListView listView, List<RequestListGroup> requestGroups){
        mContext = listView.getContext();
        res = mContext.getResources();
        if (requestGroups == null)
            this.requestGroups = new ArrayList<>();
        else
            this.requestGroups = requestGroups;

    }

    public void updateMyLocation(double lat, double lng){
        myLat = lat;
        myLng = lng;
        locationSet = true;
        notifyDataSetChanged();
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public RequestListGroup getGroup(int groupPosition) {
        if (requestGroups.size() > 0)
            return requestGroups.get(groupPosition);
        else
            return null;
    }

    @Override
    public int getGroupCount() {
        return requestGroups.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if (convertView == null){
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.request_list_group, parent, false);
        }
        TextView groupTitle = convertView.findViewById(R.id.request_list_group);
        groupTitle.setText(requestGroups.get(groupPosition).getGroupName());
        return convertView;
    }

    @Override
    public RequestListItem getChild(int groupPosition, int childPosition) {
        return requestGroups.get(groupPosition).getItem(childPosition);
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return requestGroups.get(groupPosition).size();
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        RequestListItem request = requestGroups.get(groupPosition).getItem(childPosition);
        if (request != null){
            final View view = (convertView != null ?
                    convertView : LayoutInflater.from(mContext).inflate(R.layout.request_list_item, parent, false));
            final String requestKey = request.getKey();
            final WalkRequest walkRequest = request.getRequest();
            final ImageView profImgView = view.findViewById(R.id.request_profimg);
            final TextView distanceView = view.findViewById(R.id.request_distance);
            final TextView nameView = view.findViewById(R.id.request_name);
            final TextView offerView = view.findViewById(R.id.request_offer);
            final Button actionBtn1 = view.findViewById(R.id.request_action_btn1);
            final Button actionBtn2 = view.findViewById(R.id.request_action_btn2);
            actionBtn1.setVisibility(View.GONE);
            actionBtn2.setVisibility(View.GONE);
            DatabaseReference clientRef = database.getReference("Users").child(walkRequest.getClient());
            clientRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        //User u = (User) snapshot.getValue();
                        nameView.setText(snapshot.child("displayname").getValue(String.class));
                        offerView.setText(walkRequest.getOffer());
                        if (locationSet) {
                            float result[] = new float[1];
                            Location.distanceBetween(myLat, myLng, Location.convert(walkRequest.getLat()),
                                    Location.convert(walkRequest.getLng()), result);
                            double miles = result[0] / 160.9344f;
                            String dist = format2d.format(miles) + " mi";
                            distanceView.setText(dist);
                        } else {
                            distanceView.setText("");
                        }
                        if(snapshot.child("url").exists()){
                            Picasso.get().load(snapshot.child("url").getValue().toString()).into(profImgView);
                        } else {
                            profImgView.setImageResource(R.drawable.icon_prof_generic);
                        }
                        String status = walkRequest.getStatus();
                        final DatabaseReference statusRef = database.getReference("Requests").child(requestKey).child("status");
                        /* If current status is "Created" */
                        if (status.compareToIgnoreCase(res.getString(R.string.request_created)) == 0) {
                            actionBtn1.setText(res.getString(R.string.action_accept));
                            actionBtn1.setVisibility(View.VISIBLE);
                            actionBtn1.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    statusRef.setValue(res.getString(R.string.request_accepted));
                                }
                            });
                            actionBtn2.setText(res.getString(R.string.action_reject));
                            actionBtn2.setVisibility(View.VISIBLE);
                            actionBtn2.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    statusRef.setValue(res.getString(R.string.request_rejected));
                                }
                            });
                        }
                        /* If current status is "Accepted" */
                        if (status.compareToIgnoreCase(res.getString(R.string.request_accepted)) ==0){
                            actionBtn1.setText(res.getString(R.string.action_start));
                            actionBtn1.setVisibility(View.VISIBLE);
                            actionBtn1.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    statusRef.setValue(res.getString(R.string.request_started));
                                }
                            });
                            actionBtn2.setVisibility(View.GONE);
                        }
                        /* If current status is "Started" */
                        if (status.compareToIgnoreCase(res.getString(R.string.request_started)) == 0){
                            actionBtn2.setText(res.getString(R.string.action_reject));
                            actionBtn2.setVisibility(View.VISIBLE);
                            actionBtn2.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    statusRef.setValue(res.getString(R.string.request_rejected));
                                }
                            });
                            actionBtn1.setVisibility(View.GONE);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            return view;
        } else {
            return null;
        }
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public void onGroupExpanded(int groupPosition) {

    }

    @Override
    public void onGroupCollapsed(int groupPosition) {

    }

    @Override
    public long getCombinedChildId(long groupId, long childId) {
        return 0;
    }

    @Override
    public long getCombinedGroupId(long groupId) {
        return 0;
    }

}
