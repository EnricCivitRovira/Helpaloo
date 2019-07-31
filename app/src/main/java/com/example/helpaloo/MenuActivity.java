package com.example.helpaloo;

import android.app.Application;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

public class MenuActivity extends AppCompatActivity {

    private TextView mTextMessage;
    private FirebaseAuth mAuth;
    private Post newPost;
    BottomNavigationView navigation;
    private int fragmentPosition = 99;
    private String userID;
    private User user;

    private FirebaseUser currentUser;
    private FirebaseDatabase mFirebaseDatabase;


    public int getFragmentPosition() {
        return fragmentPosition;
    }

    public void setFragmentPosition(int fragmentPosition) {
        this.fragmentPosition = fragmentPosition;
    }

    public BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.searchPostMenu:
                    setFragment(0);
                    return true;
                case R.id.addPostMenu:
                    setFragment(1);
                    return true;
                case R.id.messagesMenu:
                    setFragment(3);
                    return true;
                case R.id.profileMenu:
                    setFragment(2);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Cargando página principal...");
        progressDialog.show();
        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getUid();

        mFirebaseDatabase = FirebaseDatabase.getInstance();

        mFirebaseDatabase.getReference("users/"+userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(User.class);
                if(fragmentPosition == 99){
                    setFragment(0);
                    progressDialog.dismiss();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressDialog.dismiss();
            }
        });

        mTextMessage = (TextView) findViewById(R.id.message);
        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

    }

    public void setFragment(int position) {
        FragmentManager fragmentManager;
        FragmentTransaction fragmentTransaction;
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        switch (position) {
            case 0:
                    if(fragmentPosition != 0) {
                        SearchPost searchPost = new SearchPost(0, user);
                        fragmentTransaction.replace(R.id.fragment, searchPost);
                        fragmentTransaction.commit();
                        fragmentPosition = 0;
                    }
                break;
            case 1:
                if(fragmentPosition != 1) {
                    navigation.getMenu().findItem(R.id.addPostMenu).setChecked(true);
                    Post post = new Post();
                    AddPost addPost = new AddPost(post, 0, 0, user);
                    fragmentTransaction.replace(R.id.fragment, addPost);
                    fragmentTransaction.commit();
                    fragmentPosition = 1;
                }
                break;
            case 2:
                if(fragmentPosition != 2) {
                    navigation.getMenu().findItem(R.id.profileMenu).setChecked(true);
                    Profile myProfile = new Profile(user);
                    fragmentTransaction.replace(R.id.fragment, myProfile);
                    fragmentTransaction.commit();
                    fragmentPosition = 2;
                }
                break;
            case 3:
                if(fragmentPosition != 3) {
                    navigation.getMenu().findItem(R.id.messagesMenu).setChecked(true);
                    MessageListFragment chatList = new MessageListFragment();
                    fragmentTransaction.replace(R.id.fragment, chatList);
                    fragmentTransaction.commit();
                    fragmentPosition = 3;
                }
                break;
            case 4:
                    navigation.getMenu().findItem(R.id.searchPostMenu).setChecked(true);
                    SearchPost searchPosts = new SearchPost(0, user);
                    fragmentTransaction.replace(R.id.fragment, searchPosts);
                    fragmentTransaction.commit();
                    fragmentPosition = 0;
                break;
            case 5:
                    SearchPost profilePosts = new SearchPost(1, user);
                    fragmentTransaction.replace(R.id.fragment, profilePosts);
                    fragmentTransaction.commit();
                    fragmentPosition = 5;

                break;
        }
    }


}
