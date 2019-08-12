package health.tueisDeveloper.helpaloo.Fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;

import health.tueisDeveloper.helpaloo.Activities.MenuActivity;
import health.tueisDeveloper.helpaloo.Classes.User;
import health.tueisDeveloper.helpaloo.Classes.Valoration;
import health.tueisDeveloper.helpaloo.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.Objects;

public class ValorationFragment extends Fragment {

    private User foreignUser;

    private EditText valorationText;
    private RatingBar valorationValue;
    private String userID;
    private DatabaseReference mDatabaseReference;

    public ValorationFragment(User foreignUser) {
        this.foreignUser = foreignUser;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_valoration, container, false);
        ((MenuActivity) Objects.requireNonNull(getActivity())).setFragmentPosition(-1);
        //BIND
        Button newValoration = view.findViewById(R.id.sendValoration);
        valorationText = view.findViewById(R.id.commentValoration);
        valorationValue = view.findViewById(R.id.rateValoration);

        //AUTH
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getUid();
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
        Valoration newVal = new Valoration(comment, rating, userID);
        float totalValoration = foreignUser.getMediumValoration() + rating;
        mDatabaseReference.child("userValorations").child(foreignUser.getUserID()).push().setValue(newVal);
        mDatabaseReference.child("users").child(foreignUser.getUserID()).child("nValorations").setValue(foreignUser.getnValorations()+1);
        mDatabaseReference.child("users").child(foreignUser.getUserID()).child("mediumValoration").setValue(totalValoration);
        ((MenuActivity)getActivity()).setFragment(3);
    }


}
