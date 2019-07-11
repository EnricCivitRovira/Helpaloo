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

public class MessagesListAdapter extends ArrayAdapter<Message> {

    private Context mContext;
    private int mResource;
    private int lastPosition = -1;
    private ArrayList<Message> messages = new ArrayList<Message>();

    public MessagesListAdapter(@NonNull Context context, int resource, ArrayList<Message> messages) {
        super(context, resource, messages);
        mContext = context;
        mResource = resource;
    }

    private static class ViewHolder {
        TextView message;
        TextView from;
    }

    public void refreshList(ArrayList<Message> messages){
        this.messages.clear();
        this.messages.addAll(messages);
        notifyDataSetChanged();
    }


    @SuppressLint("ViewHolder")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        String messageInfo = getItem(position).message;
        String fromName = getItem(position).nameFrom;

        final View result;

        ViewHolder holder;

        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);
        holder= new ViewHolder();
        holder.message = (TextView) convertView.findViewById(R.id.messageInfo);
        holder.from = (TextView) convertView.findViewById(R.id.messageFrom);

        result = convertView;

        convertView.setTag(holder);

        lastPosition = position;
        // Log.i("ChatID:" , "AdapterChatID "+ name + title);
        holder.message.setText(messageInfo);
        holder.from .setText(fromName);

        return convertView;

    }
}
