package com.example.helpaloo;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.Timestamp;
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
    public Post post;
    private User user;
    public String emailFrom;
    public String emailTo;
    public Chat chat;

    FirebaseAuth mAuth;
    FirebaseDatabase mFirebaseDatabase;

    public MessageBox(Post post) {
        this.post = post;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_messagebox, container, false);
            send = view.findViewById(R.id.MBsendMessage);
            messageTV  = view.findViewById(R.id.MBmessageToSend);

            mAuth = FirebaseAuth.getInstance();
            final FirebaseUser user = mAuth.getCurrentUser();
            userIDFrom = user.getUid();
            mDatabase = FirebaseDatabase.getInstance().getReference();
            userIDTo = post.getUserId();
            chatID = idChatOrganizer(userIDFrom, userIDTo);

            mFirebaseDatabase = FirebaseDatabase.getInstance();
            mFirebaseDatabase.getReference("users/"+userIDFrom).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                emailFrom = dataSnapshot.getValue(User.class).email;

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
            });

            mFirebaseDatabase = FirebaseDatabase.getInstance();
            mFirebaseDatabase.getReference("users/"+userIDTo).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                emailTo = dataSnapshot.getValue(User.class).email;

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
            });


            send.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    message = messageTV.getText().toString();
                    insertMessage(message);
                }
            });
        return view;
    }

    private void insertMessage(final String message) {
        final String currentTime = Calendar.getInstance().getTime().toString();

        Chat newChat = new Chat(chatID, userIDFrom, userIDTo, emailFrom, post.getTitle(), post.getPostId());
        DatabaseReference refAll1 = mDatabase.child("chats_user").child(userIDFrom).child(chatID);
        DatabaseReference refAll2 = mDatabase.child("chats_user").child(userIDTo).child(chatID);
        refAll1.setValue(newChat);
        refAll2.setValue(newChat);

        Message messageObject = new Message(message, currentTime, userIDFrom, userIDTo, emailFrom, emailTo);
        DatabaseReference refAll3 = mDatabase.child("messages").child(chatID).push();
        refAll3.setValue(messageObject);
    }

    public String idChatOrganizer(String id1, String id2) {
        if(id1.compareTo(id2)>0){
            return id1+id2;
        }else{
            return id2+id1;
        }
    }


}
