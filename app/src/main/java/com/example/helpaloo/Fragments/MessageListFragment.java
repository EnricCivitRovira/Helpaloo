package com.example.helpaloo.Fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.helpaloo.Activities.MenuActivity;
import com.example.helpaloo.Adapters.ChatListAdapter;
import com.example.helpaloo.Classes.Chat;
import com.example.helpaloo.Fragments.MessageBox;
import com.example.helpaloo.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import java.util.ArrayList;
import java.util.Objects;

public class MessageListFragment extends Fragment {

    private ArrayList<Chat> chatList = new ArrayList<>();
    private ChatListAdapter adapter;
    private String chatFromID;
    private String chatPostID;
    private String chatTitle;
    private String chatToID;
    private String nameFrom;
    private String nameTo;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_messagelist_list, container, false);
        Objects.requireNonNull(((MenuActivity) Objects.requireNonNull(getActivity())).getSupportActionBar()).setTitle("Mensajes");
        ListView mListView = view.findViewById(R.id.listView);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseDatabase mFirebaseDatabase;
        FirebaseUser user = mAuth.getCurrentUser();
        String userID = Objects.requireNonNull(user).getUid();
        adapter = new ChatListAdapter(Objects.requireNonNull(getContext()), R.layout.chat, chatList);
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Chat chat = adapter.getItem(position);
                AppCompatActivity activity = (AppCompatActivity) view.getContext();
                MessageBox openChat = new MessageBox(chat);
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment, openChat).addToBackStack(null).commit();
            }
        });
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseDatabase.getReference("chats_user/"+ userID).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                showData(dataSnapshot);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return view;
    }

    private void showData(DataSnapshot dataSnapshot) {

        for(DataSnapshot ds : dataSnapshot.getChildren()){
            switch (Objects.requireNonNull(ds.getKey())) {
                case "chatFromID":
                    chatFromID = Objects.requireNonNull(ds.getValue()).toString();
                    break;
                case "chatPostID":
                    chatPostID = Objects.requireNonNull(ds.getValue()).toString();
                    break;
                case "chatTitle":
                    chatTitle = Objects.requireNonNull(ds.getValue()).toString();
                    break;
                case "chatToID":
                    chatToID = Objects.requireNonNull(ds.getValue()).toString();
                    break;
                case "nameFrom":
                    nameFrom = Objects.requireNonNull(ds.getValue()).toString();
                    break;
                case "nameTo":
                    nameTo = Objects.requireNonNull(ds.getValue()).toString();
                    break;
            }

        }
        Chat newChat = new Chat(chatFromID, chatToID, chatPostID, nameFrom,  nameTo,  chatTitle);
        chatList.add(newChat);
        adapter.notifyDataSetChanged();
    }
}