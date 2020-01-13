package com.example.appw;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChatsFragment extends Fragment {

    private View privateChatView;
    private RecyclerView chatsList;
    private DatabaseReference chatsRef,usersRef;

    private FirebaseAuth mAuth;

    private String currentUserId;


    public ChatsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        privateChatView = inflater.inflate(R.layout.fragment_chats, container, false);

        mAuth = FirebaseAuth.getInstance();

        currentUserId = mAuth.getCurrentUser().getUid();

        chatsRef = FirebaseDatabase.getInstance().getReference().child("Contacts").child(currentUserId);

        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");

        chatsList = privateChatView.findViewById(R.id.chat_list);

        chatsList.setLayoutManager(new LinearLayoutManager(getContext()));

        return privateChatView;
    }


    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Contacts> options =
                new FirebaseRecyclerOptions.Builder<Contacts>()
                        .setQuery(chatsRef, Contacts.class)
                        .build();


        FirebaseRecyclerAdapter<Contacts, ChatsViewHolder> adapter
                = new FirebaseRecyclerAdapter<Contacts,ChatsViewHolder>(options)
        {

            @NonNull
            @Override
            public ChatsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_display_layout,parent,false);
                return new ChatsViewHolder(view);

            }

            @Override
            protected void onBindViewHolder(@NonNull final ChatsViewHolder chatsViewHolder, int i, @NonNull Contacts contacts) {

                final String userIds = getRef(i).getKey();
                final String[] retImage = {"default_image"};

                usersRef.child(userIds).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if(dataSnapshot.exists())
                        {


                            if (dataSnapshot.hasChild("image"))
                            {
                                retImage[0] = dataSnapshot.child("image").getValue().toString();
                                Picasso.get().load(retImage[0]).into(chatsViewHolder.profileImage);
                            }


                            final String retName = dataSnapshot.child("name").getValue().toString();
                            final String retStatus = dataSnapshot.child("status").getValue().toString();

                            chatsViewHolder.userName.setText(retName);
                            chatsViewHolder.userStatus.setText("Last Seen :"+"Date"+" Time");


                            if(dataSnapshot.child("userState").hasChild("state"))
                            {
                                String state = dataSnapshot.child("userState").child("state").getValue().toString();
                                String date = dataSnapshot.child("userState").child("date").getValue().toString();
                                String time = dataSnapshot.child("userState").child("time").getValue().toString();

                                if(state.equals("online"))
                                {
                                    chatsViewHolder.userStatus.setText("Online");
                                }
                                else if(state.equals("offline"))
                                {
                                    chatsViewHolder.userStatus.setText("Last Seen :"+date+" "+time);
                                }

                            }

                            else {
                                chatsViewHolder.userStatus.setText("Offline");
                            }


                            chatsViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    Intent chatIntent = new Intent(getContext(),ChatActivity.class);
                                    chatIntent.putExtra("visit_user_id",userIds);
                                    chatIntent.putExtra("visit_user_name",retName);
                                    chatIntent.putExtra("visit_image", retImage[0]);

                                    startActivity(chatIntent);

                                }
                            });


                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        };

        chatsList.setAdapter(adapter);
        adapter.startListening();
    }

    public static class ChatsViewHolder extends RecyclerView.ViewHolder
    {
        CircleImageView profileImage;
        TextView userStatus,userName;

        public ChatsViewHolder(@NonNull View itemView) {

            super(itemView);

            profileImage = itemView.findViewById(R.id.users_profile_image);
            userName = itemView.findViewById(R.id.users_profile_name);
            userStatus = itemView.findViewById(R.id.users_profile_status);





        }
    }
}
