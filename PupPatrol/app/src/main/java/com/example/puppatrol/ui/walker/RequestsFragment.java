package com.example.puppatrol.ui.walker;


import android.content.Context;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class RequestsFragment extends Fragment {
    private ExpandableListView requestListView;
    private RequestListAdapter requestListAdapter;
    private Context mContext;
    List<String> listHeaders;
    HashMap<String, List<String>> listData;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_walker_requests, container, false);
        requestListView = root.findViewById(R.id.walker_request_list);

        listHeaders = new ArrayList<>();
        listData = new HashMap<>();

        listHeaders.add("Pending");
        listHeaders.add("Accepted");
        listHeaders.add("Started");
        listHeaders.add("Completed");

        List<String> listPending = new ArrayList<>();
        listPending.add("Pending 1");
        listPending.add("Pending 2");
        List<String> listAccepted = new ArrayList<>();
        listAccepted.add("Accepted 1");
        listAccepted.add("Accepted 2");
        List<String> listStarted = new ArrayList<>();
        listStarted.add("Started 1");
        listStarted.add("Started 2");
        List<String> listCompleted = new ArrayList<>();
        listCompleted.add("Completed 1");
        listCompleted.add("Completed 2");

        listData.put(listHeaders.get(0), listPending);
        listData.put(listHeaders.get(1), listAccepted);
        listData.put(listHeaders.get(2), listStarted);
        listData.put(listHeaders.get(3), listCompleted);

        requestListAdapter = new RequestListAdapter(mContext, listHeaders, listData);
        requestListView.setAdapter(requestListAdapter);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

}