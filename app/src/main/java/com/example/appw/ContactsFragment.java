package com.example.appw;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
public class ContactsFragment extends Fragment {

    private View contactsView;
    private RecyclerView myContactList;
    private DatabaseReference contactsRef,usersRef;
    private FirebaseAuth mAuth;
    private String currentUserId;

    public ContactsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        contactsView = inflater.inflate(R.layout.fragment_contacts, container, false);

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();

        contactsRef = FirebaseDatabase.getInstance().getReference().child("Contacts").child(currentUserId);

        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");


        myContactList = contactsView.findViewById(R.id.contacts_list);
        myContactList.setLayoutManager(new LinearLayoutManager(getContext()));
        return contactsView;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions options =
                new FirebaseRecyclerOptions.Builder<Contacts>()
                        .setQuery(contactsRef,Contacts.class)
                        .build();


        FirebaseRecyclerAdapter<Contacts,ContactsViewHolder> adapter
                = new FirebaseRecyclerAdapter<Contacts,ContactsViewHolder>(options){


            @NonNull
            @Override
            public ContactsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_display_layout,parent,false);
                ContactsViewHolder viewHolder = new ContactsViewHolder(view);

                return viewHolder;
            }

            @Override
            protected void onBindViewHolder(@NonNull final ContactsViewHolder contactsViewHolder, int i, @NonNull Contacts contacts) {

                String userId = getRef(i).getKey();

                usersRef.child(userId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if(dataSnapshot.exists())
                        {


                            if(dataSnapshot.child("userState").hasChild("state"))
                            {
                                String state = dataSnapshot.child("userState").child("state").getValue().toString();
                                String date = dataSnapshot.child("userState").child("date").getValue().toString();
                                String time = dataSnapshot.child("userState").child("time").getValue().toString();

                                if(state.equals("online"))
                                {
                                    contactsViewHolder.onlineIcon.setVisibility(View.VISIBLE);
                                }
                                else if(state.equals("offline"))
                                {
                                    contactsViewHolder.onlineIcon.setVisibility(View.INVISIBLE);
                                }

                            }

                            else {
                                contactsViewHolder.onlineIcon.setVisibility(View.INVISIBLE);
                            }



                            if(dataSnapshot.hasChild("image"))
                            {
                                String userImage = dataSnapshot.child("image").getValue().toString();
                                String profileName = dataSnapshot.child("name").getValue().toString();
                                String profileStatus = dataSnapshot.child("status").getValue().toString();

                                contactsViewHolder.userName.setText(profileName);
                                contactsViewHolder.userStatus.setText(profileStatus);
                                Picasso.get().load(userImage).placeholder(R.drawable.profile_image).into(contactsViewHolder.profileImage);

                            }
                            else
                            {
                                String profileName = dataSnapshot.child("name").getValue().toString();
                                String profileStatus = dataSnapshot.child("status").getValue().toString();

                                contactsViewHolder.userName.setText(profileName);
                                contactsViewHolder.userStatus.setText(profileStatus);
                            }
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            }
        };

        myContactList.setAdapter(adapter);
        adapter.startListening();

    }


    public static class ContactsViewHolder extends RecyclerView.ViewHolder
    {

        TextView userName,userStatus;
        CircleImageView profileImage;
        ImageView onlineIcon;

        public ContactsViewHolder(@NonNull View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.users_profile_name);
            userStatus = itemView.findViewById(R.id.users_profile_status);
            profileImage = itemView.findViewById(R.id.users_profile_image);
            onlineIcon = itemView.findViewById(R.id.user_online_status);


        }
    }

}
