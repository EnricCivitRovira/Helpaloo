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

        contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppCompatActivity activity = (AppCompatActivity) v.getContext();
                MessageBox newMessage = new MessageBox(post);
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment, newMessage).addToBackStack(null).commit();
            }
        });

        return view;

    }
}
