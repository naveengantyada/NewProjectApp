package com.example.newprojectapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private DatabaseReference rootRef;
    private Button logout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Paper.init(this);
        mAuth= FirebaseAuth.getInstance();
        currentUser=mAuth.getCurrentUser();
        rootRef= FirebaseDatabase.getInstance().getReference();
logout=(Button)findViewById(R.id.logout_btn);
logout.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {

       mAuth.signOut();
       sendUserToLoginActivity();
    }
});
    }
    @Override
    protected void onStart() {
        super.onStart();

        if(currentUser==null){
            sendUserToLoginActivity();
        }else{
            verifyUserExistance();
        }

    }
    private void verifyUserExistance() {
        String useruid=mAuth.getCurrentUser().getUid();
        final String userEmail=mAuth.getCurrentUser().getEmail();
        rootRef.child("Users").child(useruid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    Toast.makeText(MainActivity.this, "Welcome.."+userEmail, Toast.LENGTH_SHORT).show();
                }
                else{
                   sendUserToLoginActivity();                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void sendUserToLoginActivity() {

        Intent intent=new Intent(MainActivity.this,LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();

    }
}