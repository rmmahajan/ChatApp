package com.example.appw;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {


    private Button UpdateAccountSettings;
    private EditText userName,userStatus;
    private CircleImageView userProfileImage;
    private String currentUserID;
    private FirebaseAuth mAuth;

    private Uri ImageUri;

    private static final int GalleryPick = 1;

    private ProgressDialog loadingBar;

    private StorageReference userProfileImageRef;

    private DatabaseReference RootRef;

    private Toolbar settingsToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        RootRef = FirebaseDatabase.getInstance().getReference();
        userProfileImageRef = FirebaseStorage.getInstance().getReference().child("Profile Images");

        InitializeFields();

        userName.setVisibility(View.INVISIBLE);

        UpdateAccountSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                UpdateSettings();

            }
        });

        RetrieveUserInfo();


        userProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_PICK);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent,GalleryPick);

            }
        });
    }


    private void InitializeFields() {

        UpdateAccountSettings = findViewById(R.id.update_settings_button);
        userName = findViewById(R.id.set_user_name);
        userStatus = findViewById(R.id.set_profile_status);
        userProfileImage = findViewById(R.id.set_profile_image);
        loadingBar = new ProgressDialog(this);
        settingsToolbar = findViewById(R.id.settings_toolbar);
        setSupportActionBar(settingsToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setTitle("Account Settings");

    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GalleryPick && resultCode == RESULT_OK && data!=null)
        {

            ImageUri = data.getData();



          /*  CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this);*/
            //}

            //if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            //  CropImage.ActivityResult result = CropImage.getActivityResult(data);


            // if(resultCode == RESULT_OK)
            //{

            loadingBar.setTitle("Set Profile Image");
            loadingBar.setMessage("Uploading....");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();
            //Uri resultUri = result.getUri();

            final StorageReference filePath = userProfileImageRef.child(currentUserID + ".jpg");

            // final  UploadTask uploadTask = filePath.putFile(ImageUri);

            filePath.putFile(ImageUri).addOnCompleteListener(this, new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                    if(task.isSuccessful())
                    {
                        Toast.makeText(SettingsActivity.this, "Image is successfully uploaded", Toast.LENGTH_SHORT).show();


                        // String ss="";
                        filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri downloadPhotoUrl) {
                                // System.out.println("---------"+downloadPhotoUrl.toString());
                                String  ss=downloadPhotoUrl.toString();
                                ///////////////////////////////
                                RootRef.child("Users").child(currentUserID).child("image")
                                        .setValue(ss)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                                if(task.isSuccessful()) {
                                                    userProfileImage.setImageURI(ImageUri);
                                                    Toast.makeText(SettingsActivity.this, "Image is successfully saved", Toast.LENGTH_SHORT).show();
                                                    loadingBar.dismiss();
                                                }
                                                else
                                                {
                                                    Toast.makeText(SettingsActivity.this, "Error", Toast.LENGTH_SHORT).show();
                                                    loadingBar.dismiss();
                                                }

                                            }
                                        });

                                ///////////////////////////////
                            }
                        });


                        //  System.out.println("----------------"+ downloadUrl);

                          /* Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                               @Override
                               public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {

                                   if(!task.isSuccessful())
                                   {
                                       throw task.getException();
                                   }
                                   downloadImageUrl = filePath.getDownloadUrl().toString();
                                   return filePath.getDownloadUrl();

                               }
                           }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                               @Override
                               public void onComplete(@NonNull Task<Uri> task) {

                                   if(task.isSuccessful())
                                   {
                                       Toast.makeText(SettingsActivity.this, "Image is successfully saved", Toast.LENGTH_SHORT).show();
                                       loadingBar.dismiss();
                                   }
                                   else
                                   {
                                       Toast.makeText(SettingsActivity.this, "Error", Toast.LENGTH_SHORT).show();
                                       loadingBar.dismiss();
                                   }

                               }
                           });*/

                    }
                    else
                    {
                        Toast.makeText(SettingsActivity.this, "Image uploading failed", Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                    }

                }
            });


            filePath.putFile(ImageUri).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    Toast.makeText(SettingsActivity.this, "Image uploading failed", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();

                }
            });
        }


        //}
    }


    private void UpdateSettings() {

        String setUserName = userName.getText().toString();
        String setStatus = userStatus.getText().toString();

        if (TextUtils.isEmpty(setUserName)) {
            Toast.makeText(SettingsActivity.this, "Please write your username", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(setStatus)) {
            Toast.makeText(SettingsActivity.this, "Please write your status", Toast.LENGTH_SHORT).show();
        } else {
            HashMap<String, Object> profileMap = new HashMap<>();

            profileMap.put("uid", currentUserID);
            profileMap.put("name", setUserName);
            profileMap.put("status", setStatus);

            RootRef.child("Users").child(currentUserID).updateChildren(profileMap)
                    .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                        {

                            if (task.isSuccessful())
                            {
                                sendUserToMainActivity();
                                Toast.makeText(SettingsActivity.this, "Updated Successfully", Toast.LENGTH_LONG).show();
                            }
                            else
                            {
                                Toast.makeText(SettingsActivity.this, "Error...", Toast.LENGTH_LONG).show();
                            }

                        }
                    });
        }
    }


    private void RetrieveUserInfo() {

        RootRef.child("Users").child(currentUserID)
                .addValueEventListener(new ValueEventListener(){


                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if((dataSnapshot.exists()) && (dataSnapshot.hasChild("name") && dataSnapshot.hasChild("image")))
                        {
                            String retrieveUserName = dataSnapshot.child("name").getValue().toString();
                            String retrieveStatus = dataSnapshot.child("status").getValue().toString();
                            String retrieveProfileImage = dataSnapshot.child("image").getValue().toString();

                            userName.setText(retrieveUserName);
                            userStatus.setText(retrieveStatus);
                            Picasso.get().load(retrieveProfileImage).into(userProfileImage);
                        }
                        else if((dataSnapshot.exists()) && (dataSnapshot.hasChild("name")))
                        {
                            String retrieveUserName = dataSnapshot.child("name").getValue().toString();
                            String retrieveStatus = dataSnapshot.child("status").getValue().toString();

                            userName.setText(retrieveUserName);
                            userStatus.setText(retrieveStatus);
                        }
                        else
                        {
                            userName.setVisibility(View.VISIBLE);
                            Toast.makeText(SettingsActivity.this,"Please set and update your info",Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


    }


    private void sendUserToMainActivity() {

        Intent mainIntent = new Intent(SettingsActivity.this,MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }




}
