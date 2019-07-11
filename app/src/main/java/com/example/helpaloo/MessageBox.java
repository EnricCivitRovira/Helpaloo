package com.example.helpaloo;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

@SuppressLint("ValidFragment")
public class MessageBox extends Fragment {
    public Button send;
    public EditText messageTV;
    public String message;
    public DatabaseReference mDatabase;
    public String userIDFrom;
    public String userIDTo;
    public String chatID;
    public Chat chat;
    public String senderName;
    public String recieverName;
    public Integer type;
    public ArrayList<Message> messageList;
    public ListView messageListView;
    public MessagesListAdapter adapter;


    FirebaseAuth mAuth;
    FirebaseDatabase mFirebaseDatabase;

    public MessageBox(Chat chat, Integer type) {
        this.chat = chat;
        this.type = type;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_messagebox, container, false);
            send = view.findViewById(R.id.MBsendMessage);
            messageTV  = view.findViewById(R.id.MBmessageToSend);
            messageListView = view.findViewById(R.id.messagesList);

            messageList = new ArrayList<Message>();

            adapter = new MessagesListAdapter(getContext(), R.layout.message, messageList);

            mFirebaseDatabase = FirebaseDatabase.getInstance();

            chatID = chat.chatID;
            if(type == 0) {
                userIDFrom = chat.chatFromID;
                userIDTo = chat.chatToID;
                senderName = chat.nameFrom;
                recieverName = chat.nameTo;
            } else {
                mAuth = FirebaseAuth.getInstance();
                final FirebaseUser user = mAuth.getCurrentUser();

                userIDFrom = user.getUid();
                userIDTo = chat.chatFromID;


                mFirebaseDatabase.getReference("users/"+userIDFrom).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        senderName = dataSnapshot.getValue(User.class).name;
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });

                mFirebaseDatabase.getReference("users/"+userIDTo).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        recieverName = dataSnapshot.getValue(User.class).name;
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });

                mFirebaseDatabase.getReference("messages/"+chatID).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        showMessagesFirstTime(dataSnapshot);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                mFirebaseDatabase.getReference().addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        insertIntroducedMessage(dataSnapshot);
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


            }
            mDatabase = FirebaseDatabase.getInstance().getReference();

            Log.i("ChatInfo: ", senderName + recieverName);

            send.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    message = messageTV.getText().toString();
                    insertMessage(message);
                }
            });

        return view;
    }

    private void insertIntroducedMessage(DataSnapshot dataSnapshot) {
        for(DataSnapshot ds : dataSnapshot.getChildren()){
            Message message = ds.getValue(Message.class);
            if(message.message != null) {
                Log.i("Mensaje", message.toString());
                messageList.add(message);
                adapter.notifyDataSetChanged();
            }
        }

    }

    private void showMessagesFirstTime(DataSnapshot dataSnapshot) {
        for(DataSnapshot ds : dataSnapshot.getChildren()){
            Message message = ds.getValue(Message.class);
            messageList.add(message);
        }
        Log.i("Messages: ",  messageList.toString());
        messageListView.setAdapter(adapter);
    }

    private void insertMessage(final String message) {
        final String currentTime = Calendar.getInstance().getTime().toString();

        DatabaseReference refAll1 = mDatabase.child("chats_user").child(userIDFrom).child(chatID);
        DatabaseReference refAll2 = mDatabase.child("chats_user").child(userIDTo).child(chatID);
        refAll1.setValue(chat);
        refAll2.setValue(chat);

        Message messageObject = new Message(message, currentTime, userIDFrom, userIDTo, senderName, recieverName);
        Log.i("Message", messageObject.toString());
        DatabaseReference refAll3 = mDatabase.child("messages").child(chatID).push();
        refAll3.setValue(messageObject);
    }

}
