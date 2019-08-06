package com.example.appw;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;


public class GroupChatActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private ImageButton SendMessageButton;
    private EditText userMessageInput;
    private ScrollView mScrollView;
    private TextView displayTextMessage;

    private String currentGroupName;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);



        currentGroupName = getIntent().getExtras().get("groupName").toString();

        Toast.makeText(GroupChatActivity.this,currentGroupName,Toast.LENGTH_SHORT).show();

        IntializeFields();
    }

    private void IntializeFields() {

        mToolbar = findViewById(R.id.group_chat_bar_layout);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Group Name");

        SendMessageButton = findViewById(R.id.send_message_button);
        userMessageInput = findViewById(R.id.input_group_message);
        mScrollView = findViewById(R.id.my_scroll_view);
        displayTextMessage = findViewById(R.id.group_chat_text_display);

    }
}
