package com.example.appw;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class RegisterActivity extends AppCompatActivity {


    private Button createAccountButton;
    private EditText userEmail,userPassword;
    private TextView AlreadyHaveAnAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        
        IntializeFields();

        AlreadyHaveAnAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sendUserToLoginActivity();
            }
        });
    }

    private void IntializeFields() {

        createAccountButton = findViewById(R.id.buttonRegister);
        userEmail = findViewById(R.id.register_email);
        userPassword = findViewById(R.id.register_password);
        AlreadyHaveAnAccount = findViewById(R.id.already_have_an_account_link);

    }

    private void sendUserToLoginActivity() {

        Intent loginIntent = new Intent(RegisterActivity.this,LoginActivity.class);
        startActivity(loginIntent);
    }
}
