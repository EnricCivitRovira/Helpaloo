package com.example.helpaloo;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;




public class MessageListFragment extends Fragment {

    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabase;
    private String userID;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<Chat> chatList = new ArrayList<>();
    private ListView mListView;
    private TextView messageFromView;
    private TextView titleMessageView;
    public ChatListAdapter adapter;

    public String chatFromID, chatID, chatPostID, chatTitle, chatToID, nameFrom, nameTo;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_messagelist_list, container, false);
        ((MenuActivity) getActivity()).getSupportActionBar().setTitle("Mensajes");
        mListView = view.findViewById(R.id.listView);
        messageFromView = view.findViewById(R.id.messageFromView);
        titleMessageView = view.findViewById(R.id.messageTitleView);
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        userID = user.getUid();
        adapter = new ChatListAdapter(getContext(), R.layout.chat, chatList);
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Chat chat = adapter.getItem(position);
                AppCompatActivity activity = (AppCompatActivity) view.getContext();
                MessageBox openChat = new MessageBox(chat, 1);
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment, openChat).addToBackStack(null).commit();
            }
        });
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseDatabase.getReference("chats_user/"+userID).addChildEventListener(new ChildEventListener() {
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
        Log.i("Lo que nos llega:", dataSnapshot.toString());
        for(DataSnapshot ds : dataSnapshot.getChildren()){
            switch (ds.getKey()) {
                case "chatFromID":
                    chatFromID = ds.getValue().toString();
                    break;
                case "chatID":
                    chatID = ds.getValue().toString();
                    break;
                case "chatPostID":
                    chatPostID = ds.getValue().toString();
                    break;
                case "chatTitle":
                    chatTitle = ds.getValue().toString();
                    break;
                case "chatToID":
                    chatToID = ds.getValue().toString();
                    break;
                case "nameFrom":
                    nameFrom = ds.getValue().toString();
                    break;
                case "nameTo":
                    nameTo = ds.getValue().toString();
                    break;
            }

        }

        Chat newChat = new Chat(chatFromID, chatToID, chatPostID, nameFrom,  nameTo,  chatTitle);
        Log.i("Chat a introducir: ", newChat.toString());
        chatList.add(newChat);
        adapter.notifyDataSetChanged();
        Log.i("ListInfo:", chatList.toString());



    }
}
