package com.example.helpaloo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import static androidx.constraintlayout.widget.Constraints.TAG;

class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    private final ArrayList<Post> postslist;
    private Bitmap bmp;


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
    public MyAdapter(ArrayList<Post> postslist) {
        this.postslist = postslist;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MyAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.post, parent, false);
        MyViewHolder pvh = new MyViewHolder(v);
        return pvh;

    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        MyViewHolder.postName.setText(postslist.get(position).title);
        MyViewHolder.postPrice.setText(postslist.get(position).prize);
        //MyViewHolder.postPhoto.setImageBitmap();
        // MyViewHolder.postPhoto.setImageResource(postslist.get(position).route);

        String url = postslist.get(position).route;
        Log.i(TAG, postslist.get(position).route);

        //MyViewHolder.postPhoto.setImageBitmap(bmp);
        if(!url.equals("")){
            Picasso.get().load(url).into(MyViewHolder.postPhoto);
        }


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


}
