package health.tueisDeveloper.helpaloo.Fragments;

import android.annotation.SuppressLint;
import android.location.Location;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import health.tueisDeveloper.helpaloo.Activities.MenuActivity;
import health.tueisDeveloper.helpaloo.Adapters.MyAdapter;
import health.tueisDeveloper.helpaloo.Classes.Post;
import health.tueisDeveloper.helpaloo.Classes.User;
import health.tueisDeveloper.helpaloo.Dialogs.NoPublicationDialog;
import health.tueisDeveloper.helpaloo.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Objects;

@SuppressLint("ValidFragment")
public class SearchPost extends Fragment {

    private String userID;
    private ArrayList<Post> posts = new ArrayList<>();
    private RecyclerView rv;
    private int type ;
    private int context;
    private User user;
    private Location locationPost = new Location("PostLocation");
    private Location locationUser = new Location("UserLocation");

    @SuppressLint("ValidFragment")
    public SearchPost(int type, User user) {
        this.type = type; // 0 -> All posts, 1 -> My posts, 2-> Their Posts
        this.user = user;
    }

    public SearchPost(int type, int context) {
        this.type = type;
        this.context = context; // 0 -> Coming From Edit MyPost, 1 -> Coming From new Post
    }



    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_search_post, container, false);

        FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser userFirebase = mAuth.getCurrentUser();

        if (type != 2) {
            userID = Objects.requireNonNull(userFirebase).getUid();
        }
        rv = view.findViewById(R.id.rv);

        if(type == 0) {

            locationUser.setLatitude(user.getLatitude());
            locationUser.setLongitude(user.getLongitude());

            Objects.requireNonNull(((MenuActivity) Objects.requireNonNull(getActivity())).getSupportActionBar()).setTitle("Buscar Anuncios");

            mFirebaseDatabase.getReference("allPosts").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    showData(dataSnapshot, type);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }

            });
        }else if (type == 1){
            ((MenuActivity) Objects.requireNonNull(getActivity())).setFragmentPosition(-1);
            Objects.requireNonNull(((MenuActivity) getActivity()).getSupportActionBar()).setTitle("Mis Anuncios");
            mFirebaseDatabase.getReference("posts/"+userID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    showData(dataSnapshot, type);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }

            });
        }else if (type == 2){
            ((MenuActivity) Objects.requireNonNull(getActivity())).setFragmentPosition(-1);
            Objects.requireNonNull(((MenuActivity) getActivity()).getSupportActionBar()).setTitle("Sus Anuncios");
            mFirebaseDatabase.getReference("posts/"+user.getUserID()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    showData(dataSnapshot, type);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }

            });

        }
        return view;
    }

    @SuppressLint("LongLogTag")
    private void showData(DataSnapshot dataSnapshot, int type ) {
        for(DataSnapshot ds : dataSnapshot.getChildren()){
            Post post;
            post = ds.getValue(Post.class);
            if(type == 0) {
                locationPost.setLatitude(Objects.requireNonNull(post).getLatitude());
                locationPost.setLongitude(post.getLongitude());
                float distance = locationUser.distanceTo(locationPost) / 1000;
                if(user.getDistanceToShowPosts() > distance) {
                    posts.add(post);
                }
            }else{
                posts.add(post);
            }
        }
        if (posts.size() == 0){
            if (context != 1) {
                openDialog();
            }
        }
        rv.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        rv.setLayoutManager(layoutManager);
        MyAdapter adapter =  new MyAdapter(posts, type, user);
        rv.setAdapter(adapter);
    }

    private void openDialog() {
        FragmentManager manager = Objects.requireNonNull(getActivity()).getSupportFragmentManager();
        NoPublicationDialog dialog = new NoPublicationDialog(type);
        dialog.show(manager, "No Publication Dialog");
    }

}