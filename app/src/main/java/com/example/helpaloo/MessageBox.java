package com.example.helpaloo;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;

@SuppressLint("ValidFragment")
public class MessageBox extends Fragment {
    private EditText messageTV;
    private String message;
    private DatabaseReference mDatabase;
    private String me;
    private String userIDTo;
    private String userIDFrom;
    private String chatID;
    private Chat chat;
    private String senderName;
    private String recieverName;
    private Integer type;
    private final ArrayList<Message> messageList = new ArrayList<Message>();
    private MessagesListAdapter adapter;
    private String messageText = "";
    private String nameFrom = "";
    private String nameTo = "";
    private String timestamp = "";
    private String userIDFromValue = "";
    private String userIDToValue = "";
    private ListView messageListView;

    FirebaseAuth mAuth;

    MessageBox(Chat chat, Integer type) {
        this.chat = chat;
        this.type = type;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_messagebox, container, false);

        ((MenuActivity) getActivity()).setFragmentPosition(-1);

        // BIND
        Button send = view.findViewById(R.id.MBsendMessage);
        messageTV  = view.findViewById(R.id.MBmessageToSend);
        messageListView = view.findViewById(R.id.messagesList);
        messageListView.setOnItemClickListener(null);

        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = mAuth.getCurrentUser();
        adapter = new MessagesListAdapter(getContext(), R.layout.message, messageList, user.getUid());
        messageListView.setAdapter(adapter);
        FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

            me = user.getUid();
            chatID = chat.chatID;
            if(chat.chatFromID.equals(me)){
                userIDFrom = chat.chatFromID;
                userIDTo = chat.chatToID;
                senderName = chat.nameFrom;
                recieverName = chat.nameTo;
                ((MenuActivity) getActivity()).getSupportActionBar().setTitle(recieverName);

            }else{
                userIDFrom = chat.chatToID;
                userIDTo = me;
                senderName = chat.nameTo;
                recieverName = chat.nameFrom;
                ((MenuActivity) getActivity()).getSupportActionBar().setTitle(recieverName);
            }

            mFirebaseDatabase.getReference("messages/"+chatID).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                insertIntroducedMessage(dataSnapshot);

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

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                message = messageTV.getText().toString();
                if(!message.equals("")) {
                    insertMessage(message);
                    messageTV.setText("");
                }
            }
        });

        return view;
    }

    private void insertIntroducedMessage(DataSnapshot dataSnapshot) {
        for (DataSnapshot ds : dataSnapshot.getChildren()) {

            switch (Objects.requireNonNull(ds.getKey())) {
                case "message":
                    messageText = Objects.requireNonNull(ds.getValue()).toString();
                    break;
                case "nameFrom":
                    nameFrom = Objects.requireNonNull(ds.getValue()).toString();
                    break;
                case "nameTo":
                    nameTo = Objects.requireNonNull(ds.getValue()).toString();
                    break;
                case "timestamp":
                    timestamp = Objects.requireNonNull(ds.getValue()).toString();
                    break;
                case "userIDFrom":
                    userIDFromValue = Objects.requireNonNull(ds.getValue()).toString();
                    break;
                case "userIDTo":
                    userIDToValue = Objects.requireNonNull(ds.getValue()).toString();
                    break;
            }
        }
        Message message = new Message(messageText, timestamp, userIDFromValue, userIDToValue, nameFrom, nameTo);
        Log.i("Mensaje a añadir: ", message.toString());

        messageList.add(message);
        adapter.notifyDataSetChanged();
        messageListView.setSelection(adapter.getCount() -1) ;

    }

    private void insertMessage(final String message) {
        final String currentTime = Calendar.getInstance().getTime().toString();

        DatabaseReference refAll1 = mDatabase.child("chats_user").child(userIDFrom).child(chatID);
        DatabaseReference refAll2 = mDatabase.child("chats_user").child(userIDTo).child(chatID);
        refAll1.setValue(chat);
        refAll2.setValue(chat);

        Message messageObject = new Message(message, currentTime, userIDFrom, userIDTo, senderName, recieverName);
        DatabaseReference refAll3 = mDatabase.child("messages").child(chatID).push();
        refAll3.setValue(messageObject);
    }

}
