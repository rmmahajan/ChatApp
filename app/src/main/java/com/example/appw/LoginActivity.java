package com.example.appw;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class LoginActivity extends AppCompatActivity {

    //FirebaseUser currentUser;
    FirebaseAuth mAuth;
    private Button LoginButton,PhoneLoginButton;
    private EditText userEmail,userPassword;
    private TextView NeedNewAccountLink,ForgotPasswordLink;
    private ProgressDialog loadingBar;
    private DatabaseReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");

        //currentUser = mAuth.getCurrentUser();

        InitilizeFields();

        NeedNewAccountLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sendUserToRegisterActivity();

            }
        });

        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AllowUserToLogin();
            }
        });


        PhoneLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(LoginActivity.this, PhoneLoginActivity.class);
                startActivity(intent);


            }
        });


    }

    private void AllowUserToLogin() {

        String email = userEmail.getText().toString();
        String password = userPassword.getText().toString();

        if(TextUtils.isEmpty(email))
        {
            Toast.makeText(this,"Enter email",Toast.LENGTH_SHORT).show();
        }

        if(TextUtils.isEmpty(password))
        {
            Toast.makeText(this,"Enter password",Toast.LENGTH_SHORT).show();
        }

        else
        {

            loadingBar.setTitle("Signing In...");
            loadingBar.setMessage("Please Wait....");
            loadingBar.setCanceledOnTouchOutside(true);
            loadingBar.show();

            mAuth.signInWithEmailAndPassword(email,password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if(task.isSuccessful())
                            {

                                String currentUserId = mAuth.getCurrentUser().getUid();
                                String deviceToken  = FirebaseInstanceId.getInstance().getToken();

                                usersRef.child(currentUserId).child("device_token")
                                        .setValue(deviceToken)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                                if(task.isSuccessful())
                                                {
                                                    sendUserToMainActivity();
                                                    Toast.makeText(LoginActivity.this,"Logged in successfully",Toast.LENGTH_SHORT).show();
                                                    loadingBar.dismiss();
                                                }

                                            }
                                        });
                            }
                            else
                            {
                                String message = task.getException().toString();
                                Toast.makeText(LoginActivity.this,"Error"+ message,Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            }
                        }
                    });

        }
    }

    private void InitilizeFields() {

        LoginButton = findViewById(R.id.buttonLogin);
        PhoneLoginButton = findViewById(R.id.phone_button_login);
        userEmail = findViewById(R.id.login_email);
        userPassword = findViewById(R.id.login_password);
        NeedNewAccountLink = findViewById(R.id.need_new_account_link);
        ForgotPasswordLink = findViewById(R.id.forgot_password_link);
        loadingBar = new ProgressDialog(this);
    }


    /*protected void onStart() {

        super.onStart();
        if(currentUser != null)
        {
            sendUserToMainActivity();
        }
    }*/


    private void sendUserToMainActivity() {

        Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        startActivity(mainIntent);
        finish();

    }


    private void sendUserToRegisterActivity() {

        Intent registerIntent = new Intent(LoginActivity.this,RegisterActivity.class);
        startActivity(registerIntent);
        finish();
    }


}
