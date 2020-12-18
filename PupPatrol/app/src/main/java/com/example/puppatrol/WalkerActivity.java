package com.example.puppatrol;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class WalkerActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    public static final String WALKER_NOT_AVAILABLE = "N";
    public static final String WALKER_AVAILABLE = "Y";

    DrawerLayout drawerLayout;
    Toolbar toolbar;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private String mAvailable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_walker);
        toolbar = findViewById(R.id.walker_toolbar);
        setSupportActionBar(toolbar);
        drawerLayout = findViewById(R.id.walker_drawer_layout);
        NavigationView navigationView = findViewById(R.id.walker_nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close){
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.walker_nav_requests:
                // TODO: load walker requests fragment
                break;
            case R.id.walker_nav_reviews:
                // TODO: load walker history fragment
                break;
            case R.id.walker_nav_home:
                // TODO: launch chat activity
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}
