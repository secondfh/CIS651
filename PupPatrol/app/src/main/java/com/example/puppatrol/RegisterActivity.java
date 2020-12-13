package com.example.puppatrol;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private EditText email, password, displayname, phonenumber;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        email=findViewById(R.id.registerEmail);
        password=findViewById(R.id.registerPassword);
        displayname=findViewById(R.id.registerName);
        phonenumber=findViewById(R.id.registerPhone);

        email.setText(getIntent().getStringExtra("email"));

        mAuth = FirebaseAuth.getInstance();
    }

    public void Register(View view) {
        Log.d("Register", "HELLO");

        if(email.getText().toString().equals("")|| password.getText().toString().equals("")
                || phonenumber.getText().toString().equals("")|| displayname.getText().toString().equals("")){
            Toast.makeText(this, "Please provide all information", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString())
            .addOnSuccessListener(this, new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {
                    currentUser=authResult.getUser();
                    currentUser.sendEmailVerification().addOnSuccessListener(RegisterActivity.this, new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(RegisterActivity.this, "Signup successful. Verification email sent!", Toast.LENGTH_SHORT).show();
                            registerUser();
                            startActivity(new Intent(RegisterActivity.this, SignupLogin.class));
                            finish();
                        }
                    }).addOnFailureListener(RegisterActivity.this, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            })
            .addOnFailureListener(this, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        );
    }

    private void registerUser(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference usersRef = database.getReference("Users");
        usersRef.child(currentUser.getUid()).setValue(new User(displayname.getText().toString(),
                email.getText().toString(), phonenumber.getText().toString()));

    }
}
