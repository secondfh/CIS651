package com.example.puppatrol.ui.walker;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.puppatrol.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class RequestsFragment extends Fragment {

    public RequestsFragment(){

    }

    public static RequestsFragment newInstance(int index) {
        RequestsFragment fragment = new RequestsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_walker_requests2, container, false);
        return root;
    }
}