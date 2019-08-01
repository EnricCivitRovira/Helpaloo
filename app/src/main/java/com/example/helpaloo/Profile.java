package com.example.helpaloo;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import static android.app.Activity.RESULT_OK;
import static androidx.constraintlayout.widget.Constraints.TAG;


@SuppressLint("ValidFragment")
public class Profile extends Fragment {

    public TextView showKilometers;
    public ImageView profilePic;
    public EditText profileEmail;
    public EditText profileName;
    public EditText profileSurname;
    public Button newProfilePic;
    public Button signOut;
    public Button resetPass;
    public Button saveChanges;
    public Button myValorations;
    private Button myPosts;

    public SeekBar distancePosts;
    private FirebaseStorage storage;
    StorageReference storageReference;
    private FirebaseUser currentUser;
    private FirebaseDatabase mFirebaseDatabase;
    private String userID;
    private FirebaseAuth mAuth;
    private User user;
    private Uri imageUri;
    private static final int PICK_IMAGE_REQUEST = 1;
    private static String routeString;
    private Task<Uri> route;

    private int type;
    private int pval;

    @SuppressLint("ValidFragment")
    public Profile(User user) {
        this.user = user;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // BIND
        profilePic = view.findViewById(R.id.profilePicture);
        newProfilePic = view.findViewById(R.id.addProfilePicture);
        profileEmail = view.findViewById(R.id.profileEmail);
        profileName = view.findViewById(R.id.profileName);
        signOut = view.findViewById(R.id.signOut);
        resetPass = view.findViewById(R.id.resetPassword);
        saveChanges = view.findViewById(R.id.saveChanges);
        myPosts = view.findViewById(R.id.myPosts);
        profileSurname = view.findViewById(R.id.profileSurname);
        distancePosts = view.findViewById(R.id.distancePosts);
        showKilometers = view.findViewById(R.id.distancePostShow);
        myValorations = view.findViewById(R.id.myValorations);

        // FIREBASE
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();

        // DATA
        ((MenuActivity) getActivity()).getSupportActionBar().setTitle("Mi Perfil");
        userID = user.userID;
        showData(user);


        newProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();

            }
        });

        resetPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPass();
            }
        });

        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });

        saveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage(userID);
            }
        });

        myPosts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (type == 0) {
                    SearchPost myPosts = new SearchPost(1, user);
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment, myPosts, "findThisFragment")
                            .addToBackStack(null)
                            .commit();
                }else if(type == 1){
                    SearchPost theirPosts = new SearchPost(2, userID);
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment, theirPosts, "findThisFragment")
                            .addToBackStack(null)
                            .commit();
                }
            }
        });

        myValorations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ForeignProfile myValorations = new ForeignProfile(userID, 1);
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment, myValorations, "findThisFragment")
                        .addToBackStack(null)
                        .commit();
            }
        });

        return view;
    }

    private void uploadImage(final String userID) {
        final ProgressDialog progressDialog = new ProgressDialog(getContext());

        progressDialog.setTitle("Subiendo Nueva Imagen de perfil...");
        progressDialog.show();

        if(imageUri != null)
        {
            String[] filename = imageUri.getLastPathSegment().split("/");
            final StorageReference ref = storageReference.child("images/"+mAuth.getUid()+"/ProfilePic/"+filename[filename.length-1]);

            Log.w("POST PATH: "+ref, "TEST");
            ref.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(getActivity(), "Perfil Editado", Toast.LENGTH_SHORT).show();
                            route = ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    routeString = route.getResult().toString();
                                    uploadProfileData(userID, routeString, profileName.getText().toString(), profileSurname.getText().toString(), profileEmail.getText().toString(), pval);
                                }
                            });

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(getActivity(), "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded "+(int)progress+"%");


                        }
                    });
        }else {
            uploadProfileData(userID, user.route, profileName.getText().toString(), profileSurname.getText().toString(), profileEmail.getText().toString(), pval);
            progressDialog.dismiss();
            Toast.makeText(getActivity(), "Perfil Editado", Toast.LENGTH_SHORT).show();

        }

    }

    private void uploadProfileData(String userID, String route, String name, String surname, String email, int distance) {
        mFirebaseDatabase.getReference("users").child(userID).child("route").setValue(route);
        mFirebaseDatabase.getReference("users").child(userID).child("name").setValue(name);
        mFirebaseDatabase.getReference("users").child(userID).child("surname").setValue(surname);
        mFirebaseDatabase.getReference("users").child(userID).child("email").setValue(email);
        mFirebaseDatabase.getReference("users").child(userID).child("distanceToShowPosts").setValue(distance);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.getCurrentUser().updateEmail(email);
    }

    private void resetPass() {
        mAuth.sendPasswordResetEmail(user.email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getContext(), "Check email to reset your password!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Fail to send reset password email!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void showData(User user) {
        profileEmail.setText(user.email);
        profileName.setText(user.name);
        profileSurname.setText(user.surname);
        pval = user.distanceToShowPosts;
        distancePosts.setMax(300);
        distancePosts.setProgress(pval);
        showKilometers.setText(Integer.toString(pval)+ " Km");
        distancePosts.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                pval = progress;
                showKilometers.setText(Integer.toString(pval)+" Km");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                showKilometers.setText(Integer.toString(pval)+ " Km");
            }
        });
        Log.i("Picture: ", user.toString());
        Picasso.get().load(user.route).fit().centerCrop().into(profilePic);

    }
    private void signOut() {
        mAuth.signOut();
        Intent intent = new Intent(getContext(), MainActivity.class);
        startActivity(intent);
        getActivity().finish();

    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_IMAGE_REQUEST && resultCode ==  RESULT_OK && data != null && data.getData() != null){

            imageUri = data.getData();
            Picasso.get().load(imageUri).into(profilePic);
        }

    }
}
