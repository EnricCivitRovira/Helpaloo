package com.example.helpaloo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class MessagesListAdapter extends ArrayAdapter<Message> {

    private Context mContext;
    private int mResource;
    private int lastPosition = -1;
    private ArrayList<Message> messages = new ArrayList<Message>();
    private String userID;

    public MessagesListAdapter(@NonNull Context context, int resource, ArrayList<Message> messages, String userID) {
        super(context, resource, messages);
        mContext = context;
        mResource = resource;
        this.userID = userID;
    }

    private static class ViewHolder {
        TextView message;
        TextView from;
    }

    @SuppressLint("ViewHolder")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        Message message = getItem(position);
        String messageInfo = message.getMessage();
        String[] date_parts = message.timestamp.split(" ");
        String[] timestamp_parts = date_parts[3].split(":");
        String timestamp = timestamp_parts[0]+":"+timestamp_parts[1];

        final View result;

        ViewHolder holder;

        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);
        holder= new ViewHolder();
        holder.message = convertView.findViewById(R.id.messageInfo);
        holder.from = convertView.findViewById(R.id.messageFrom);

        result = convertView;

        convertView.setTag(holder);

        lastPosition = position;
        // Log.i("ChatID:" , "AdapterChatID "+ name + title);
        holder.message.setText(messageInfo);
        if(!message.userIDFrom.equals(userID)) {
            holder.message.setGravity(Gravity.RIGHT);
            holder.message.setTextColor(Color.BLUE);
        }else{
            holder.message.setTextColor(Color.BLACK);
        }

        holder.from .setText(timestamp);

        return convertView;

    }
}
