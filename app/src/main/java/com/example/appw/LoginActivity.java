package com.example.appw;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    FirebaseUser currentUser;
    private Button LoginButton,PhoneLoginButton;
    private EditText userEmail,userPassword;
    private TextView NeedNewAccountLink,ForgotPasswordLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        InitilizeFields();

        NeedNewAccountLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sendUserToRegisterActivity();

            }
        });
    }

    private void InitilizeFields() {

        LoginButton = findViewById(R.id.buttonLogin);
        PhoneLoginButton = findViewById(R.id.phone_button_login);
        userEmail = findViewById(R.id.login_email);
        userPassword = findViewById(R.id.login_password);
        NeedNewAccountLink = findViewById(R.id.need_new_account_link);
        ForgotPasswordLink = findViewById(R.id.forgot_password_link);
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


    private void sendUserToRegisterActivity() {

        Intent registerIntent = new Intent(LoginActivity.this,RegisterActivity.class);
        startActivity(registerIntent);
    }


}
