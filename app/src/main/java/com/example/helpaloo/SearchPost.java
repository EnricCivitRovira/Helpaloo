package com.example.helpaloo;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
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
    private int context;
    private User user;
    float distance;
    Location locationPost = new Location("PostLocation");
    Location locationUser = new Location("UserLocation");

    @SuppressLint("ValidFragment")
    public SearchPost(int type, User user) {
        this.type = type; // 0 -> All posts, 1 -> My posts, 2-> Their Posts
        this.user = user;
    }

    public SearchPost(int type, String userID) {
        this.type = type;
        this.userID = userID;
    }

    public SearchPost(int type, int context) {
        this.type = type;
        this.context = context; // 0 -> Coming From Edit MyPost, 1 -> Coming From new Post
    }



    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_search_post, container, false);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser userFirebase = mAuth.getCurrentUser();

        if (type != 2) {
            userID = userFirebase.getUid();
        }
        rv = view.findViewById(R.id.rv);

        if(type == 0) {

            locationUser.setLatitude(user.latitude);
            locationUser.setLongitude(user.longitude);

            ((MenuActivity) getActivity()).getSupportActionBar().setTitle("Buscar Anuncios");

            mFirebaseDatabase.getReference("allPosts").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    showData(dataSnapshot, type);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }

            });
        }else if (type == 1){
            ((MenuActivity) getActivity()).setFragmentPosition(-1);
            ((MenuActivity) getActivity()).getSupportActionBar().setTitle("Mis Anuncios");
            mFirebaseDatabase.getReference("posts/"+userID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    showData(dataSnapshot, type);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }

            });
        }else if (type == 2){
            ((MenuActivity) getActivity()).setFragmentPosition(-1);
            ((MenuActivity) getActivity()).getSupportActionBar().setTitle("Sus Anuncios");
            Log.i("Posts de:", userID);
            mFirebaseDatabase.getReference("posts/"+userID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    showData(dataSnapshot, type);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }

            });

        }
        return view;
    }

    @SuppressLint("LongLogTag")
    private void showData(DataSnapshot dataSnapshot, int type ) {
        for(DataSnapshot ds : dataSnapshot.getChildren()){
            Post post = ds.getValue(Post.class);
            if(type == 0) {
                locationPost.setLatitude(post.latitude);
                locationPost.setLongitude(post.longitude);
                distance = locationUser.distanceTo(locationPost)/1000;
                Log.i(
                        "DISTANCIA ", "entre: Lat "+ locationPost.getLatitude() +
                        " Long " + locationPost.getLongitude() +
                        " y Lat: "+ locationUser.getLatitude() +
                                " Long: "+ locationUser.getLongitude() +" es de: "+ String.valueOf(distance)+ " Kilometros");
                if(user.distanceToShowPosts>distance) {
                    posts.add(post);
                }
            }else{
                posts.add(post);
            }
        }
        if (posts.size() == 0){
            if (context != 1) {
                openDialog();
            }
            // Toast.makeText(getActivity(), "No tienes publicaciones :( ", Toast.LENGTH_SHORT).show();
        }
        rv.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        rv.setLayoutManager(layoutManager);
        MyAdapter adapter =  new MyAdapter(posts, type, user);
        rv.setAdapter(adapter);
    }

    private void openDialog() {
        FragmentManager manager = getActivity().getSupportFragmentManager();
        Log.i("MANAGER: ", manager.toString());
        NoPublicationDialog dialog = new NoPublicationDialog(type);
        dialog.show(manager, "No Publication Dialog");
    }

}