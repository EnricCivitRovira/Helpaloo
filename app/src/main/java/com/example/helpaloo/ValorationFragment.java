package com.example.helpaloo;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class ValorationFragment extends Fragment {

    private User foreignUser;

    private Button newValoration;
    private EditText valorationText;
    private RatingBar valorationValue;

    private Valoration newVal;

    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private float totalValoration;


    public ValorationFragment(User foreignUser) {
        this.foreignUser = foreignUser;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_valoration, container, false);
        ((MenuActivity) getActivity()).setFragmentPosition(-1);
        //BIND
        newValoration = view.findViewById(R.id.sendValoration);
        valorationText = view.findViewById(R.id.commentValoration);
        valorationValue = view.findViewById(R.id.rateValoration);

        //AUTH
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        newValoration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertNewValoration(valorationText.getText().toString(), valorationValue.getRating());
            }
        });


        return view;
    }

    private void insertNewValoration(String comment, float rating) {
        newVal = new Valoration(comment, rating, foreignUser.userID);
        totalValoration = foreignUser.mediumValoration + rating;
        mDatabaseReference.child("userValorations").child(foreignUser.userID).push().setValue(newVal);
        mDatabaseReference.child("users").child(foreignUser.userID).child("nValorations").setValue(foreignUser.nValorations+1);
        mDatabaseReference.child("users").child(foreignUser.userID).child("mediumValoration").setValue(totalValoration);
        getFragmentManager().popBackStackImmediate();

    }


}
