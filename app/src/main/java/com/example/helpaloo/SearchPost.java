package com.example.helpaloo;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static androidx.constraintlayout.widget.Constraints.TAG;


@SuppressLint("ValidFragment")
public class SearchPost extends Fragment {

    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabase;
    private String userID;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<Post> posts = new ArrayList<>();
    private RecyclerView rv;
    private int type ;


    @SuppressLint("ValidFragment")
    public SearchPost(int type) {
        this.type = type;
    }

    public SearchPost(int type, String userID) {
        this.type = type;
        this.userID = userID;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_search_post, container, false);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        if (type != 2) {
            userID = user.getUid();
        }
        rv = view.findViewById(R.id.rv);

        if(type == 0) {
            ((MenuActivity) getActivity()).getSupportActionBar().setTitle("Buscar Anuncios");

            mFirebaseDatabase.getReference("allPosts").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    showData(dataSnapshot);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }

            });
        }else if (type == 1){
            ((MenuActivity) getActivity()).getSupportActionBar().setTitle("Mis Anuncios");
            mFirebaseDatabase.getReference("posts/"+userID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    showData(dataSnapshot);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }

            });
        }else if (type == 2){
            ((MenuActivity) getActivity()).getSupportActionBar().setTitle("Sus Anuncios");
            Log.i("Posts de:", userID);
            mFirebaseDatabase.getReference("posts/"+userID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    showData(dataSnapshot);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }

            });

        }
        return view;
    }

    private void showData(DataSnapshot dataSnapshot) {
        for(DataSnapshot ds : dataSnapshot.getChildren()){
            Post post = ds.getValue(Post.class);
            posts.add(post);
        }

        if (posts.size() == 0){
            openDialog();
            // Toast.makeText(getActivity(), "No tienes publicaciones :( ", Toast.LENGTH_SHORT).show();
        }

        rv.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        rv.setLayoutManager(layoutManager);
        MyAdapter adapter =  new MyAdapter(posts);
        rv.setAdapter(adapter);

    }

    private void openDialog() {
        NoPublicationDialog dialog = new NoPublicationDialog();
        dialog.show(getFragmentManager(), "No Publication Dialog");
    }
}