package com.example.helpaloo.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Objects;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.example.helpaloo.Classes.Chat;
import com.example.helpaloo.R;
import com.google.firebase.auth.FirebaseAuth;

public class ChatListAdapter extends ArrayAdapter<Chat> {

    private Context mContext;
    private int mResource;

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

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String name;
        Chat chat = getItem(position);
        String currentUser = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

        if(Objects.requireNonNull(chat).getChatToID().equals(currentUser)) {
           name = chat.getNameFrom();
        }else{
           name = chat.getNameTo();
        }
        String title = chat.getChatTitle();


        ViewHolder holder;

        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);
        holder= new ViewHolder();
        holder.name =  convertView.findViewById(R.id.chatPerson);
        holder.title =  convertView.findViewById(R.id.chatTitle);

        convertView.setTag(holder);
        holder.name.setText(name);
        holder.title.setText(title);

        return convertView;

    }
}
