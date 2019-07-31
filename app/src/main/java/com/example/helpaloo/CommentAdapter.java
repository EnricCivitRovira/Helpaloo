package com.example.helpaloo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.w3c.dom.Comment;

import java.util.ArrayList;

public class CommentAdapter extends ArrayAdapter<Valoration> {

    private Context mContext;
    private int mResource;
    private int lastPosition = -1;

    public CommentAdapter(@NonNull Context context, int resource, ArrayList<Valoration> valorations) {
        super(context, resource, valorations);
        mContext = context;
        mResource = resource;
    }

    private static class ViewHolder {
        TextView comment;
    }

    @SuppressLint("ViewHolder")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        String comment = getItem(position).getComment();

        final View result;

        ViewHolder holder;

        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);
        holder= new ViewHolder();
        holder.comment = convertView.findViewById(R.id.commentValue);

        result = convertView;

        convertView.setTag(holder);

        lastPosition = position;

        holder.comment.setText(comment);

        return convertView;

    }
}
