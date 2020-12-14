package com.example.puppatrol;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private EditText email, password, displayname, phonenumber;
    private ImageView profilePic;
    private Uri imageUri;

    private static final int OPEN_FILE=0012;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        email=findViewById(R.id.registerEmail);
        password=findViewById(R.id.registerPassword);
        displayname=findViewById(R.id.registerName);
        phonenumber=findViewById(R.id.registerPhone);
        profilePic = findViewById(R.id.profileImagePreview);

        email.setText(getIntent().getStringExtra("email"));

        mAuth = FirebaseAuth.getInstance();

        profilePic.setVisibility(View.GONE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==OPEN_FILE && resultCode==RESULT_OK) {
            imageUri = data.getData();
            profilePic.setImageURI(imageUri);
            profilePic.setVisibility(View.VISIBLE);
        }
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
        final DatabaseReference usersRef = database.getReference("Users");
        if (imageUri == null) {
            usersRef.child(currentUser.getUid()).setValue(new User(displayname.getText().toString(),
                    email.getText().toString(), phonenumber.getText().toString()));
        } else {
            FirebaseStorage storage= FirebaseStorage.getInstance();
            final String fileNameInStorage= UUID.randomUUID().toString();
            String path="images/"+ fileNameInStorage+".jpg";
            final StorageReference imageRef=storage.getReference(path);

            imageRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            usersRef.child(currentUser.getUid()).setValue(new User(displayname.getText().toString(),
                                    email.getText().toString(), phonenumber.getText().toString(), uri.toString()));
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public void UploadFile(View view) {
        Intent intent = new Intent().setType("*/*") //when un commented the argument here shall be "start/star"
                .setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select a file"), OPEN_FILE);
    }
}
