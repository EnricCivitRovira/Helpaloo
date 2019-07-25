package com.example.helpaloo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
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


public class Profile extends Fragment {

    public ImageView profilePic;
    public TextView profileEmail;
    public TextView profileName;
    public Button newProfilePic;
    public Button signOut;
    public Button resetPass;
    public Button saveChanges;
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
    private Button myPosts;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_profile, container, false);

        ((MenuActivity) getActivity()).getSupportActionBar().setTitle("Mi Perfil");

        // BIND
        profilePic = view.findViewById(R.id.profilePicture);
        newProfilePic = view.findViewById(R.id.addProfilePicture);
        profileEmail = view.findViewById(R.id.profileEmail);
        profileName = view.findViewById(R.id.profileName);
        signOut = view.findViewById(R.id.signOut);
        resetPass = view.findViewById(R.id.resetPassword);
        saveChanges = view.findViewById(R.id.saveChanges);
        myPosts = view.findViewById(R.id.myPosts);

        // FIREBASE

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        // DATA
        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getUid();

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseDatabase.getReference("users/"+userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                showData(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

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
                SearchPost myPosts = new SearchPost(1);
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment, myPosts, "findThisFragment")
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
                            Toast.makeText(getActivity(), "Subido", Toast.LENGTH_SHORT).show();
                            route = ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    routeString = route.getResult().toString();
                                    uploadProfilePic(userID, routeString);
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
            //insertPost(postid, userid, introducedTitle.getText().toString(), introducedDescription.getText().toString(), introducedPrize.getText().toString(), introducedTime.getSelectedItem().toString(), "", 0);
            uploadProfilePic(userID, routeString);
            progressDialog.dismiss();
            Toast.makeText(getActivity(), "Imagen Subida", Toast.LENGTH_SHORT).show();

        }

    }

    private void uploadProfilePic(String userID, String route) {
        mFirebaseDatabase.getReference("users").child(userID).child("route").setValue(route);
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

    private void showData(DataSnapshot dataSnapshot) {
        user = dataSnapshot.getValue(User.class);
        profileEmail.setText(user.email);
        profileName.setText(user.name);
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
