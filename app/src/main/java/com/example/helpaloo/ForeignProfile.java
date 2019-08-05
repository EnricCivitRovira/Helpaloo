package com.example.helpaloo;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Array;
import java.util.ArrayList;


public class ForeignProfile extends Fragment {

    private ImageView profilePic;
    private TextView profileName;
    private TextView profileSurname;
    private TextView numberValorations;
    private RatingBar profileValoration;
    private ListView mListValorationView;
    private Button newValoration;

    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;

    private String foreignUserID;
    private User foreignUser;

    private ValorationFragment foreignNewValoration;
    private ArrayList<Valoration> valorationList = new ArrayList<>();
    private CommentAdapter adapter;
    private String comment;
    private String userValorationID;
    private Object starRate;
    private ArrayList<String> blackList = new ArrayList<>();
    private int type;

    private Valoration val = new Valoration();

    public ForeignProfile(String foreignUserID, int type){
        this.foreignUserID = foreignUserID;
        this.type = type; // 0 -> Visto desde fuera. 1 -> visto desde dentro
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_foreign_profile, container, false);

        ((MenuActivity) getActivity()).setFragmentPosition(-1);


        // BIND
        profilePic = view.findViewById(R.id.foreignAvatar);
        profileName = view.findViewById(R.id.foreignName);
        profileSurname = view.findViewById(R.id.foreignSurname);
        profileValoration = view.findViewById(R.id.mediumValoration);
        mListValorationView = view.findViewById(R.id.commentList);
        numberValorations = view.findViewById(R.id.nValorationsView);
        newValoration = view.findViewById(R.id.openValoration);
        mListValorationView.setOnItemClickListener(null);
        if(type == 1){
            newValoration.setVisibility(view.GONE);
            ((MenuActivity) getActivity()).getSupportActionBar().setTitle("Mis Valoraciones");
        }else{
            ((MenuActivity) getActivity()).getSupportActionBar().setTitle("Perfil del publicante");
        }

        //FIREBASE
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        adapter = new CommentAdapter(getContext(), R.layout.comment, valorationList );
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

        newValoration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppCompatActivity activity = (AppCompatActivity) v.getContext();
                foreignNewValoration = new ValorationFragment(foreignUser);
                Log.i("BlackList: ",blackList.toString());
                Log.i("Nuevo usuario:", mAuth.getUid());
                if(blackList.contains(mAuth.getUid())){
                    Toast.makeText(getActivity(), "Ya has valorado a este usuario.", Toast.LENGTH_SHORT).show();
                }else{
                    activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment, foreignNewValoration).addToBackStack(null).commit();
                }
            }
        });


        return view;
    }

    private void showData(DataSnapshot dataSnapshot) {
        foreignUser = dataSnapshot.getValue(User.class);
        profileName.setText(foreignUser.name);
        profileSurname.setText(foreignUser.surname);
        profileValoration.setRating(foreignUser.mediumValoration/foreignUser.nValorations);
        numberValorations.setText(String.valueOf(foreignUser.nValorations)+" Valoraciones.");
        Picasso.get().load(foreignUser.route).fit().centerCrop().into(profilePic);
    }

    private void fillComments(final String userID) {

        mFirebaseDatabase.getReference("userValorations/"+ userID).addListenerForSingleValueEvent(new ValueEventListener() {
          @Override
          public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
              valorationList.clear();
              blackList.clear();
              Log.i("Lo que nos llega:", dataSnapshot.toString());
              for(DataSnapshot ds : dataSnapshot.getChildren()){
                  val = ds.getValue(Valoration.class);
                  valorationList.add(val);
                  blackList.add(val.getUserID());
                  Log.i("Nueva valoración: ", val.toString());
              }
              adapter.notifyDataSetChanged();
              Log.i("Lista actual:", valorationList.toString());
              Log.i("BlackList: ", blackList.toString());
          }

          @Override
          public void onCancelled(@NonNull DatabaseError databaseError) {

          }
      }
        );
    }
}
