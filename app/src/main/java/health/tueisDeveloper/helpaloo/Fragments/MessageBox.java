package health.tueisDeveloper.helpaloo.Fragments;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import health.tueisDeveloper.helpaloo.Activities.MenuActivity;
import health.tueisDeveloper.helpaloo.Adapters.MessagesListAdapter;
import health.tueisDeveloper.helpaloo.Classes.Chat;
import health.tueisDeveloper.helpaloo.Classes.Message;
import health.tueisDeveloper.helpaloo.Classes.User;
import health.tueisDeveloper.helpaloo.Classes.Valoration;
import health.tueisDeveloper.helpaloo.Dialogs.NoPublicationDialog;
import health.tueisDeveloper.helpaloo.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

@SuppressLint("ValidFragment")
public class MessageBox extends Fragment {
    private EditText messageTV;
    private String message;
    private DatabaseReference mDatabase;
    private String userIDTo;
    private String userIDFrom;
    private String chatID;
    private Chat chat;
    private String senderName;
    private String recieverName;
    private final ArrayList<Message> messageList = new ArrayList<>();
    private MessagesListAdapter adapter;
    private String messageText = "";
    private String nameFrom = "";
    private String nameTo = "";
    private String timestamp = "";
    private String userIDFromValue = "";
    private String userIDToValue = "";
    private ListView messageListView;
    private ImageView profilePic;
    private TextView profileName;
    private Button completedPost;
    private ValorationFragment foreignNewValoration;
    private User user;

    public MessageBox(Chat chat, User user) {
        this.chat = chat;
        this.user = user;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_messagebox, container, false);

        ((MenuActivity) Objects.requireNonNull(getActivity())).setFragmentPosition(-1);
        //dialog
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Cargando Mensajes...");
        progressDialog.show();
        // BIND
        Button send = view.findViewById(R.id.MBsendMessage);
        messageTV  = view.findViewById(R.id.MBmessageToSend);
        messageListView = view.findViewById(R.id.messagesList);
        messageListView.setOnItemClickListener(null);
        profileName = view.findViewById(R.id.mbUserName);
        profilePic = view.findViewById(R.id.mbProfile);
        completedPost = view.findViewById(R.id.completedPost);

        messageList.clear();

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        final FirebaseUser userF = mAuth.getCurrentUser();
        adapter = new MessagesListAdapter(Objects.requireNonNull(getContext()), R.layout.message, messageList, Objects.requireNonNull(userF).getUid());
        messageListView.setAdapter(adapter);
        FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        Objects.requireNonNull(((MenuActivity) getActivity()).getSupportActionBar()).hide();

        String me = userF.getUid();
        chatID = chat.getChatID();
        if(chat.getChatFromID().equals(me)){
            userIDFrom = chat.getChatFromID();
            userIDTo = chat.getChatToID();
            senderName = chat.getNameFrom();
            recieverName = chat.getNameTo();
            Objects.requireNonNull(((MenuActivity) getActivity()).getSupportActionBar()).setTitle(recieverName);
            completedPost.setVisibility(View.GONE);
        }else{
            userIDFrom = chat.getChatToID();
            userIDTo = chat.getChatFromID();
            senderName = chat.getNameTo();
            recieverName = chat.getNameFrom();
            Objects.requireNonNull(((MenuActivity) getActivity()).getSupportActionBar()).setTitle(recieverName);
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

        mFirebaseDatabase.getReference("users/"+ userIDTo).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                showProfileInfo(dataSnapshot);
                progressDialog.dismiss();
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

        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ForeignProfile theirValorations = new ForeignProfile(userIDTo, 0);
                Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment, theirValorations, "ForeignProfile")
                        .addToBackStack("MessageBox")
                        .commit();
            }
        });

        completedPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = Objects.requireNonNull(getActivity()).getSupportFragmentManager();
                NoPublicationDialog dialog = new NoPublicationDialog(chat, user);
                dialog.show(manager, "No Publication Dialog");
            }
        });

        return view;
    }



    private void showProfileInfo(DataSnapshot dataSnapshot) {
        User user = dataSnapshot.getValue(User.class);
        profileName.setText(Objects.requireNonNull(user).getName());
        Picasso.get().load(user.getRoute()).fit().centerCrop().into(profilePic);
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
