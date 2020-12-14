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
public class HistoryFragment extends Fragment {

    public HistoryFragment(){

    }

    public static HistoryFragment newInstance(int index) {
        HistoryFragment fragment = new HistoryFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_walker_history2, container, false);
        return root;
    }
}