package com.example.helpaloo;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import static androidx.constraintlayout.widget.Constraints.TAG;

class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    private final ArrayList<Post> postslist;

    private String userID;
    private FirebaseAuth mAuth;
    private int context;
    private User user;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        static TextView postName;
        static TextView postPrice;
        static ImageView postPhoto;

        MyViewHolder(View itemView) {
            super(itemView);
            cv = itemView.findViewById(R.id.cv);
            postName = itemView.findViewById(R.id.postTitle);
            postPrice = itemView.findViewById(R.id.postPrice);
            postPhoto = itemView.findViewById(R.id.postPhoto);

        }

    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MyAdapter(ArrayList<Post> postslist, int context, User user) {
        this.postslist = postslist;
        this.context = context; // 0 -> AllPosts, 1 -> MyPosts
        this.user = user;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MyAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.post, parent, false);
        MyViewHolder pvh = new MyViewHolder(v);

        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getUid();
        return pvh;

    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final Post post = postslist.get(position);
        MyViewHolder.postName.setText(post.title);
        MyViewHolder.postPrice.setText(post.prize+ " €");
        String url = post.route;
        if(!url.equals("")){
            Picasso.get().load(url).fit().centerCrop().into(MyViewHolder.postPhoto);
        }
        MyViewHolder.postPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(post.userId.equals(userID)){
                    AppCompatActivity activity = (AppCompatActivity) v.getContext();
                    AddPost myPost = new AddPost(post, 1, context, user);
                    activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment, myPost).addToBackStack(null).commit();
                }else {
                    AppCompatActivity activity = (AppCompatActivity) v.getContext();
                    PostDescription openPost = new PostDescription(post, user);
                    activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment, openPost).addToBackStack(null).commit();
                }

            }
        });

    }



    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return postslist.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}
