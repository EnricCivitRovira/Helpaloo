package com.example.helpaloo;

import android.annotation.SuppressLint;
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

    @SuppressLint("ValidFragment")
    public PostDescription(Post post) {
        this.post = post;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_post_description, container, false);

        contact =  view.findViewById(R.id.PDContact);
        postPhoto =  view.findViewById(R.id.PDImage);
        postTitle = view.findViewById(R.id.PDTitle);
        postPrice = view.findViewById(R.id.PDPrice);
        postDescription = view.findViewById(R.id.PDDescription);

        if(!post.route.equals("")) {
            Picasso.get().load(post.route).fit().centerCrop().into(postPhoto);
        };

        postTitle.setText(post.getTitle());
        postPrice.setText(post.getPrize());
        postDescription.setText(post.getDescription());

        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getUid();

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseDatabase.getReference("users/"+userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userName = dataSnapshot.getValue(User.class).name;
                userSurname = dataSnapshot.getValue(User.class).surname;
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

        return view;

    }
}