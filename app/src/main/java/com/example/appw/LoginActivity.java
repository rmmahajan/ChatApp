package com.example.appw;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }


    protected void onStart() {

        super.onStart();
        if(currentUser != null)
        {
            sendUserToMainActivity();
        }
    }


    private void sendUserToMainActivity() {

        Intent loginIntent = new Intent(LoginActivity.this,MainActivity.class);
        startActivity(loginIntent);
    }


}
