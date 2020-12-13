package com.example.puppatrol;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class ChooseRoleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_role);

        TextView textView = findViewById(R.id.roleText);
        textView.setText("Hello!\nWhich would you like to log in as?");
    }

    public void GoToClient(View view) {
        Log.d("ChooseRole", "Waiting for Client Activity");
    }

    public void GoToWalker(View view) {
//        Log.d("ChooseRole", "Waiting for Walker Activity");
        startActivity(new Intent(this, WalkerActivity.class));
    }
}
