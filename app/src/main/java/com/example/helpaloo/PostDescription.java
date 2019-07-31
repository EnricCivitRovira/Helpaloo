package com.example.helpaloo;

import android.annotation.SuppressLint;
import android.media.Image;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;


@SuppressLint("ValidFragment")
public class PostDescription extends Fragment {

    public Post post;
    private Button contact;
    private ImageView postPhoto;
    private TextView postTitle;
    private TextView postPrice;
    private TextView postDescription;
    private String userID;
    private Chat chat;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private String userName;
    private String userSurname;
    private String route;
    private ImageView profilePicSender;
    private TextView usernamePostDescription;
    private Profile senderProfile;
    private User user;

    @SuppressLint("ValidFragment")
    public PostDescription(Post post, User user) {
        this.post = post;
        this.user = user;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_post_description, container, false);

        ((MenuActivity) getActivity()).setFragmentPosition(-1);

        contact =  view.findViewById(R.id.PDContact);
        postPhoto =  view.findViewById(R.id.PDImage);
        postTitle = view.findViewById(R.id.PDTitle);
        postPrice = view.findViewById(R.id.PDPrice);
        postDescription = view.findViewById(R.id.PDDescription);

        profilePicSender = view.findViewById(R.id.profilePicturePostDescription);
        usernamePostDescription = view.findViewById(R.id.userNamePostDescription);

        if(!post.route.equals("")) {
            Picasso.get().load(post.route).fit().centerCrop().into(postPhoto);
        };

        postTitle.setText(post.getTitle());
        postPrice.setText(post.getPrize()+ " €");
        postDescription.setText(post.getDescription());

        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getUid();

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        userName = user.name;
        userSurname = user.surname;

        mFirebaseDatabase.getReference("users/"+post.userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userName = dataSnapshot.getValue(User.class).name;
                userSurname = dataSnapshot.getValue(User.class).surname;
                route = dataSnapshot.getValue(User.class).route;

                Picasso.get().load(route).fit().centerCrop().into(profilePicSender);
                usernamePostDescription.setText(userName);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppCompatActivity activity = (AppCompatActivity) v.getContext();
                chat = new Chat (userID, post.userId, post.postId, userName , post.postNameUser, post.getTitle());
                MessageBox newMessage = new MessageBox(chat, 0);
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment, newMessage).addToBackStack(null).commit();
            }
        });

        profilePicSender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppCompatActivity activity = (AppCompatActivity) v.getContext();
                // TODO realizar nueva classe para mostrar información de los otros usuarios
                // senderProfile = new Profile (post.userId, 1);
                //activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment, senderProfile).addToBackStack(null).commit();
            }
        });

        return view;

    }
}
