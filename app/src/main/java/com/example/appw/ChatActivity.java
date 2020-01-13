package com.example.appw;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    private String messageReceiverId,messageReceiverName,MessageReceiverImage,messageSenderId;

    private TextView userName,userLastSeen;

    private CircleImageView userImage;

    private FirebaseAuth mAuth;

    private DatabaseReference rootref;

    private ProgressDialog loadingBar;

    private ImageButton sendMessageButton,sendFilesButton;
    private EditText messageInputText;

    private Toolbar chatToolbar;

    private String saveCurrentTime, saveCurrentDate;

    private String checker="", myUrl="";

    private StorageTask uploadTask;

    private Uri fileUri;

    private final List<Messages> messagesList = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private MessageAdapter messageAdapter;

    private RecyclerView userMessagesList;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mAuth = FirebaseAuth.getInstance();

        messageSenderId = mAuth.getCurrentUser().getUid();

        rootref = FirebaseDatabase.getInstance().getReference();

        messageReceiverId = getIntent().getExtras().get("visit_user_id").toString();
        messageReceiverName = getIntent().getExtras().get("visit_user_name").toString();
        MessageReceiverImage = getIntent().getExtras().get("visit_image").toString();

        intializeControllers();

        userName.setText(messageReceiverName);
        Picasso.get().load(MessageReceiverImage).placeholder(R.drawable.profile_image).into(userImage);


        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sendMessage();


            }
        });

        DisplayLastSeen();


        sendFilesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CharSequence options[] = new CharSequence[]
                        {
                                "Images",
                                "PDF Files",
                                "Ms Word Files"
                        };
                AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this);

                builder.setTitle("Select the File");


                builder.setItems(options, new DialogInterface.OnClickListener(){

                    @Override
                    public void onClick(DialogInterface dialog, int i) {

                        if(i==0)
                        {
                            checker = "image";

                            Intent intent = new Intent();

                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            intent.setType("image/*");
                            startActivityForResult(intent.createChooser(intent,"Select Image"),438);
                        }
                        if(i==1)
                        {
                            checker = "pdf";

                            Intent intent = new Intent();

                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            intent.setType("application/pdf");
                            startActivityForResult(intent.createChooser(intent,"Select PDF"),438);

                        }
                        if(i==2)
                        {
                            checker = "docx";

                            Intent intent = new Intent();

                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            intent.setType("application/msword");
                            startActivityForResult(intent.createChooser(intent,"Select Doc"),438);
                        }

                    }
                });

                builder.show();

            }
        });


    }

    private void sendMessage()
    {

        String messageText = messageInputText.getText().toString();
        if(TextUtils.isEmpty(messageText))
        {
            Toast.makeText(ChatActivity.this, "Can't send empty message", Toast.LENGTH_SHORT).show();
        }
        else
        {
            String messageSenderRef = "Messages/" + messageSenderId + "/" + messageReceiverId;

            String messageReceiverRef = "Messages/" + messageReceiverId + "/" + messageSenderId;

            DatabaseReference userMessageKeyRef = rootref.child("Messages")
                    .child(messageSenderId).child(messageReceiverId).push();

            String messagePushId = userMessageKeyRef.getKey();

            Map messageTextBody = new HashMap();
            messageTextBody.put("message",messageText);
            messageTextBody.put("type","text");
            messageTextBody.put("from",messageSenderId);
            messageTextBody.put("to",messageReceiverId);
            messageTextBody.put("messageId",messagePushId);
            messageTextBody.put("time",saveCurrentTime);
            messageTextBody.put("date",saveCurrentDate);

            Map messageBodyDetails = new HashMap();

            messageBodyDetails.put(messageSenderRef + "/" + messagePushId,messageTextBody);
            messageBodyDetails.put(messageReceiverRef + "/" + messagePushId,messageTextBody);


            rootref.updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {

                    if(task.isSuccessful())
                    {
                        Toast.makeText(ChatActivity.this, "Successful", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Toast.makeText(ChatActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    }

                    messageInputText.setText("");

                }
            });
        }

    }




    private void intializeControllers()
    {
        chatToolbar = findViewById(R.id.chat_toolbar);


        setSupportActionBar(chatToolbar);

        ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);

        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View actionBarView = layoutInflater.inflate(R.layout.custom_chat_bar,null);
        actionBar.setCustomView(actionBarView);

        userName = findViewById(R.id.custom_profile_name);
        userImage = findViewById(R.id.custom_profile_image);
        userLastSeen = findViewById(R.id.custom_user_last_seen);
        sendMessageButton = findViewById(R.id.send_message_btn);
        sendFilesButton = findViewById(R.id.send_files_btn);
        messageInputText = findViewById(R.id.input_messages);




        messageAdapter = new MessageAdapter(messagesList);
        userMessagesList = findViewById(R.id.private_messages_list_of_users);
        linearLayoutManager = new LinearLayoutManager(this);
        userMessagesList.setLayoutManager(linearLayoutManager);
        userMessagesList.setAdapter(messageAdapter);


        loadingBar = new ProgressDialog(this);


        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm a");
        saveCurrentTime = currentTime.format(calendar.getTime());

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == 438 && requestCode == RESULT_OK && data != null && data.getData() != null)
        {

            loadingBar.setTitle("Sending File...");
            loadingBar.setMessage("Please Wait....");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            fileUri = data.getData();

            if(!checker.equals("image"))
            {

                StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Document Files");

                final String messageSenderRef = "Messages/" + messageSenderId + "/" + messageReceiverId;

                final String messageReceiverRef = "Messages/" + messageReceiverId + "/" + messageSenderId;

                DatabaseReference userMessageKeyRef = rootref.child("Messages")
                        .child(messageSenderId).child(messageReceiverId).push();

                final String messagePushId = userMessageKeyRef.getKey();

                final StorageReference filePath = storageReference.child(messagePushId +"."+checker);

                filePath.putFile(fileUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                        if(task.isSuccessful())
                        {

                        }
                    }
                });

            }
            else if(checker.equals("image"))
            {
                StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Image Files");

                final String messageSenderRef = "Messages/" + messageSenderId + "/" + messageReceiverId;

                final String messageReceiverRef = "Messages/" + messageReceiverId + "/" + messageSenderId;

                DatabaseReference userMessageKeyRef = rootref.child("Messages")
                        .child(messageSenderId).child(messageReceiverId).push();

                final String messagePushId = userMessageKeyRef.getKey();

                final StorageReference filePath = storageReference.child(messagePushId +"."+"jpg");

                uploadTask = filePath.putFile(fileUri);

                uploadTask.continueWithTask(new Continuation() {
                    @Override
                    public Object then(@NonNull Task task) throws Exception {

                        if(!task.isSuccessful())
                        {
                            throw task.getException();
                        }
                        return filePath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {

                        if(task.isSuccessful())
                        {
                            Uri downloadUri = task.getResult();
                            myUrl = downloadUri.toString();




                            Map messageTextBody = new HashMap();
                            messageTextBody.put("message",myUrl);
                            messageTextBody.put("name",fileUri.getLastPathSegment());
                            messageTextBody.put("type",checker);
                            messageTextBody.put("from",messageSenderId);
                            messageTextBody.put("to",messageReceiverId);
                            messageTextBody.put("messageId",messagePushId);
                            messageTextBody.put("time",saveCurrentTime);
                            messageTextBody.put("date",saveCurrentDate);

                            Map messageBodyDetails = new HashMap();

                            messageBodyDetails.put(messageSenderRef + "/" + messagePushId,messageTextBody);
                            messageBodyDetails.put(messageReceiverRef + "/" + messagePushId,messageTextBody);


                            rootref.updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull Task task) {

                                    if(task.isSuccessful())
                                    {
                                        loadingBar.dismiss();
                                        Toast.makeText(ChatActivity.this, "Successful", Toast.LENGTH_SHORT).show();
                                    }
                                    else
                                    {
                                        loadingBar.dismiss();
                                        Toast.makeText(ChatActivity.this, "Error", Toast.LENGTH_SHORT).show();
                                    }

                                    messageInputText.setText("");

                                }
                            });



                        }

                    }
                });
            }
            else
            {
                loadingBar.dismiss();
                Toast.makeText(this, "Nothing Selected.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void DisplayLastSeen()
    {
        rootref.child("Users").child(messageReceiverId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot)
                    {
                        if (dataSnapshot.child("userState").hasChild("state"))
                        {
                            String state = dataSnapshot.child("userState").child("state").getValue().toString();
                            String date = dataSnapshot.child("userState").child("date").getValue().toString();
                            String time = dataSnapshot.child("userState").child("time").getValue().toString();

                            if (state.equals("online"))
                            {
                                userLastSeen.setText("online");
                            }
                            else if (state.equals("offline"))
                            {
                                userLastSeen.setText("Last Seen: " + date + "\n" + time);
                            }
                        }
                        else
                        {
                            userLastSeen.setText("offline");
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }



    @Override
    protected void onStart() {
        super.onStart();


        rootref.child("Messages").child(messageSenderId).child(messageReceiverId)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s)
                    {

                        Messages messages = dataSnapshot.getValue(Messages.class);

                        messagesList.add(messages);
                        messageAdapter.notifyDataSetChanged();

                        userMessagesList.smoothScrollToPosition(userMessagesList.getAdapter().getItemCount());

                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }
}
