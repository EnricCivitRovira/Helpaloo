package com.example.helpaloo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class ChatListAdapter extends ArrayAdapter<Chat> {

    private Context mContext;
    private int mResource;
    private int lastPosition = -1;

    public ChatListAdapter(@NonNull Context context, int resource, ArrayList<Chat> chats) {
        super(context, resource, chats);
        mContext = context;
        mResource = resource;
    }

    private static class ViewHolder {
        TextView name;
        TextView title;
    }

    @SuppressLint("ViewHolder")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        String name = getItem(position).getChatFromName();
        String title = getItem(position).getChatTitle();

        final View result;

        ViewHolder holder;

        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);
        holder= new ViewHolder();
        holder.name = (TextView) convertView.findViewById(R.id.chatPerson);
        holder.title = (TextView) convertView.findViewById(R.id.chatTitle);

        result = convertView;

        convertView.setTag(holder);

        lastPosition = position;
        Log.i("ChatID:" , "AdapterChatID "+ name + title);
        holder.name.setText(name);
        holder.title.setText(title);

        return convertView;

    }
}
