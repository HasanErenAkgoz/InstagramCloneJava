package com.erenakgz.instagramclonejava.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.erenakgz.instagramclonejava.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser=mAuth.getCurrentUser();
        if (firebaseUser!=null){
            Intent intent=new Intent(MainActivity.this,FeedActivity.class);
            startActivity(intent);
            finish();
        }


    }
    public  void  SıgnInClick(View view)
    {
        String email=binding.emailText.getText().toString();
        String password=binding.passwordText.getText().toString();
        if (email.equals("")||password.equals(""))
        {
            Toast.makeText(this, "Enter Email and Password", Toast.LENGTH_SHORT).show();
        }
        else
        {
             mAuth.signInWithEmailAndPassword(email,password).addOnSuccessListener(authResult -> {
                 Intent intent=new Intent(MainActivity.this,FeedActivity.class);
                 startActivity(intent);
                 finish();
             }).addOnFailureListener(e -> {
                 Toast.makeText(MainActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
             });
        }

    }
    public void SıgnUpClik(View view)
    {
        String email=binding.emailText.getText().toString();
        String password=binding.passwordText.getText().toString();
        if (email.equals("")||password.equals(""))
        {
            Toast.makeText(this, "Enter Email and Password", Toast.LENGTH_SHORT).show();
        }
        else {
            mAuth.createUserWithEmailAndPassword(email,password).addOnSuccessListener(authResult -> {
                Intent intent =new Intent(MainActivity.this,FeedActivity.class);
                startActivity(intent);
                finish();

            }).addOnFailureListener(e -> {
                Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            });
        }
    }
}