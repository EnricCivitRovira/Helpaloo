package com.example.helpaloo;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
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


public class Profile extends Fragment {

    public ImageView profilePic;
    public TextView profileEmail;
    public TextView profileName;
    public Button signOut;
    public Button resetPass;
    private FirebaseDatabase mFirebaseDatabase;
    private String userID;
    private FirebaseAuth mAuth;
    private User user;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_profile, container, false);

        ((MenuActivity) getActivity()).getSupportActionBar().setTitle("Mi Perfil");

        // BIND
        profilePic = view.findViewById(R.id.profilePicture);
        profileEmail = view.findViewById(R.id.profileEmail);
        profileName = view.findViewById(R.id.profileName);
        signOut = view.findViewById(R.id.signOut);
        resetPass = view.findViewById(R.id.resetPassword);

        // DATA
        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getUid();

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseDatabase.getReference("users/"+userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                showData(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });

        return view;
    }

    private void showData(DataSnapshot dataSnapshot) {
        user = dataSnapshot.getValue(User.class);
        profileEmail.setText(user.email);
        profileName.setText(user.username);
    }
    private void signOut() {
        mAuth.signOut();
        getActivity().finish();
    }
}
