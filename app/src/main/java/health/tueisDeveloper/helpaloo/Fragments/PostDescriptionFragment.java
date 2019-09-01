package health.tueisDeveloper.helpaloo.Fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import health.tueisDeveloper.helpaloo.Activities.MenuActivity;
import health.tueisDeveloper.helpaloo.Classes.Chat;
import health.tueisDeveloper.helpaloo.Classes.Post;
import health.tueisDeveloper.helpaloo.Classes.User;
import health.tueisDeveloper.helpaloo.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Objects;

@SuppressLint("ValidFragment")
public class PostDescriptionFragment extends Fragment {

    private Post post;
    private String userID;
    private Chat chat;
    private String userName;
    private String route;
    private ImageView profilePicSender;
    private TextView usernamePostDescription;
    private ForeignProfileFragment foreignProfileFragment;
    private User user;

    @SuppressLint("ValidFragment")
    public PostDescriptionFragment(Post post, User user) {
        this.post = post;
        this.user = user;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_post_description, container, false);

        ((MenuActivity) Objects.requireNonNull(getActivity())).setFragmentPosition(-1);
        Objects.requireNonNull(((MenuActivity) getActivity()).getSupportActionBar()).setTitle("Información de tarea");

        Button contact = view.findViewById(R.id.PDContact);
        ImageView postPhoto = view.findViewById(R.id.PDImage);
        TextView postTitle = view.findViewById(R.id.PDTitle);
        TextView postPrice = view.findViewById(R.id.PDPrice);
        TextView postDescription = view.findViewById(R.id.PDDescription);

        profilePicSender = view.findViewById(R.id.profilePicturePostDescription);
        usernamePostDescription = view.findViewById(R.id.userNamePostDescription);

        if(!post.getRoute().equals("")) {
            Picasso.get().load(post.getRoute()).fit().centerCrop().into(postPhoto);
        }

        postTitle.setText(post.getTitle());
        postPrice.setText(post.getPrice()+ " €");
        postDescription.setText(post.getDescription());

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getUid();

        FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
        userName = user.getName();


        mFirebaseDatabase.getReference("users/"+post.getUserId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userName = Objects.requireNonNull(dataSnapshot.getValue(User.class)).getName();
                route = Objects.requireNonNull(dataSnapshot.getValue(User.class)).getRoute();

                Picasso.get().load(route).fit().centerCrop().into(profilePicSender);
                usernamePostDescription.setText(userName);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppCompatActivity activity = (AppCompatActivity) v.getContext();
                chat = new Chat (userID, post.getUserId(), post.getPostId(), user.getName() , post.getPostNameUser(), post.getTitle());
                MessageBoxFragment newMessage = new MessageBoxFragment(chat, user);
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment, newMessage).addToBackStack(null).commit();
            }
        });

        profilePicSender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppCompatActivity activity = (AppCompatActivity) v.getContext();
                foreignProfileFragment = new ForeignProfileFragment(user,post.getUserId(), 0);
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment, foreignProfileFragment).addToBackStack(null).commit();
            }
        });

        return view;

    }
}
