package com.example.helpaloo;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.MenuItem;
import android.widget.TextView;

public class MenuActivity extends AppCompatActivity {

    private TextView mTextMessage;
    private FirebaseAuth mAuth;
    private Post newPost;

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
        setFragment(0);
        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        // updateUI(currentUser);
    }



    public void setFragment(int position) {
        FragmentManager fragmentManager;
        FragmentTransaction fragmentTransaction;
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();

        switch (position) {
            case 1:
                AddPost addPost = new AddPost();
                fragmentTransaction.replace(R.id.fragment, addPost);
                fragmentTransaction.commit();
                break;
            case 0:

                SearchPost searchPost = new SearchPost();
                fragmentTransaction.replace(R.id.fragment, searchPost);
                fragmentTransaction.commit();
                break;
            case 2:
                Profile myProfile = new Profile();
                fragmentTransaction.replace(R.id.fragment, myProfile);
                fragmentTransaction.commit();
                break;
            case 3:
                MessageListFragment chatList = new MessageListFragment();
                fragmentTransaction.replace(R.id.fragment, chatList);
                fragmentTransaction.commit();
                break;
        }
    }


}
