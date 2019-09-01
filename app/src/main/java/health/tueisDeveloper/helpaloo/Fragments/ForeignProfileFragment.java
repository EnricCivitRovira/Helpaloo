package health.tueisDeveloper.helpaloo.Fragments;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import health.tueisDeveloper.helpaloo.Adapters.CommentAdapter;
import health.tueisDeveloper.helpaloo.Classes.User;
import health.tueisDeveloper.helpaloo.Activities.MenuActivity;
import health.tueisDeveloper.helpaloo.R;
import health.tueisDeveloper.helpaloo.Classes.Valoration;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.Objects;

public class ForeignProfileFragment extends Fragment {

    private ImageView profilePic;
    private TextView profileName;
    private TextView profileSurname;
    private TextView numberValorations;
    private RatingBar profileValoration;

    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;

    private String foreignUserID;
    private User foreignUser;
    private User user;

    private ArrayList<Valoration> valorationList = new ArrayList<>();
    private CommentAdapter adapter;
    private int type;

    private Valoration val = new Valoration();

    public ForeignProfileFragment(User user, String foreignUserID, int type){
        this.user = user;
        this.foreignUserID = foreignUserID;
        this.type = type; // 0 -> Visto desde fuera. 1 -> visto desde dentro
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_foreign_profile, container, false);

        ((MenuActivity) Objects.requireNonNull(getActivity())).setFragmentPosition(-1);


        // BIND
        profilePic = view.findViewById(R.id.foreignAvatar);
        profileName = view.findViewById(R.id.foreignName);
        profileSurname = view.findViewById(R.id.foreignSurname);
        profileValoration = view.findViewById(R.id.mediumValoration);
        ListView mListValorationView = view.findViewById(R.id.commentList);
        numberValorations = view.findViewById(R.id.nValorationsView);
        Button theirPosts = view.findViewById(R.id.theirPosts);

        mListValorationView.setOnItemClickListener(null);
        if(type == 1){
            Objects.requireNonNull(((MenuActivity) getActivity()).getSupportActionBar()).setTitle("Mis Valoraciones");
        }else{
            Objects.requireNonNull(((MenuActivity) getActivity()).getSupportActionBar()).setTitle("Perfil del publicante");
        }

        //FIREBASE
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        adapter = new CommentAdapter(Objects.requireNonNull(getContext()), R.layout.comment, valorationList );
        mListValorationView.setAdapter(adapter);
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Cargando información de usuario...");
        progressDialog.show();

        fillComments(foreignUserID);

        mFirebaseDatabase.getReference("users/"+foreignUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                showData(dataSnapshot);
                progressDialog.dismiss();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressDialog.dismiss();
            }
        });



        theirPosts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchPostFragment theirPosts = new SearchPostFragment(2, user,  foreignUser);
                Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment, theirPosts, "findThisFragment")
                        .addToBackStack(null)
                        .commit();
            }
        });


        return view;
    }

    @SuppressLint("SetTextI18n")
    private void showData(DataSnapshot dataSnapshot) {
        foreignUser = dataSnapshot.getValue(User.class);

        profileName.setText(Objects.requireNonNull(foreignUser).getName());
        profileSurname.setText(foreignUser.getSurname());
        profileValoration.setRating(foreignUser.getMediumValoration()/foreignUser.getnValorations());
        numberValorations.setText(foreignUser.getnValorations()+" Valoraciones.");
        Picasso.get().load(foreignUser.getRoute()).fit().centerCrop().into(profilePic);
    }

    private void fillComments(final String userID) {

        mFirebaseDatabase.getReference("userValorations/"+ userID).addListenerForSingleValueEvent(new ValueEventListener() {
          @Override
          public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
              valorationList.clear();
              for(DataSnapshot ds : dataSnapshot.getChildren()){
                  val = ds.getValue(Valoration.class);
                  valorationList.add(val);
              }
              adapter.notifyDataSetChanged();
          }

          @Override
          public void onCancelled(@NonNull DatabaseError databaseError) {

          }
      }
        );
    }
}
