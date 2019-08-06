package health.tueisDeveloper.helpaloo.Fragments;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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

import health.tueisDeveloper.helpaloo.Activities.ContactUs;
import health.tueisDeveloper.helpaloo.Activities.MainActivity;
import health.tueisDeveloper.helpaloo.Activities.MenuActivity;
import health.tueisDeveloper.helpaloo.Classes.User;
import health.tueisDeveloper.helpaloo.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.util.Objects;

import static android.app.Activity.RESULT_OK;

@SuppressLint("ValidFragment")
public class Profile extends Fragment {

    private TextView showKilometers;
    private ImageView profilePic;
    private EditText profileEmail;
    private EditText profileName;
    private EditText profileSurname;

    private SeekBar distancePosts;
    private StorageReference storageReference;
    private FirebaseDatabase mFirebaseDatabase;
    private String userID;
    private FirebaseAuth mAuth;
    private User user;
    private Uri imageUri;
    private static final int PICK_IMAGE_REQUEST = 1;
    private static String routeString;
    private Task<Uri> route;

    private int pval;

    @SuppressLint("ValidFragment")
    public Profile(User user) {
        this.user = user;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // BIND
        profilePic = view.findViewById(R.id.profilePicture);
        Button newProfilePic = view.findViewById(R.id.addProfilePicture);
        profileEmail = view.findViewById(R.id.profileEmail);
        profileName = view.findViewById(R.id.profileName);
        Button signOut = view.findViewById(R.id.signOut);
        Button resetPass = view.findViewById(R.id.resetPassword);
        Button saveChanges = view.findViewById(R.id.saveChanges);
        Button myPosts = view.findViewById(R.id.myPosts);
        profileSurname = view.findViewById(R.id.profileSurname);
        distancePosts = view.findViewById(R.id.distancePosts);
        showKilometers = view.findViewById(R.id.distancePostShow);
        Button myValorations = view.findViewById(R.id.myValorations);
        ImageView helpaloo = view.findViewById(R.id.helpaloo);

        // FIREBASE
        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();

        // DATA
        Objects.requireNonNull(((MenuActivity) Objects.requireNonNull(getActivity())).getSupportActionBar()).setTitle("Mi Perfil");
        userID = user.getUserID();
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
                  SearchPost myPosts = new SearchPost(1, user);
                    Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment, myPosts, "findThisFragment")
                            .addToBackStack(null)
                            .commit();
            }
        });

        myValorations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ForeignProfile myValorations = new ForeignProfile(userID, 1);
                Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment, myValorations, "findThisFragment")
                        .addToBackStack(null)
                        .commit();
            }
        });

        helpaloo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ContactUs.class);
                intent.putExtra("ContactUs", "");
                startActivity(intent);
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
            String[] filename = Objects.requireNonNull(imageUri.getLastPathSegment()).split("/");
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
                                    routeString = Objects.requireNonNull(route.getResult()).toString();
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
            uploadProfileData(userID, user.getRoute(), profileName.getText().toString(), profileSurname.getText().toString(), profileEmail.getText().toString(), pval);
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
        Objects.requireNonNull(auth.getCurrentUser()).updateEmail(email);
    }

    private void resetPass() {
        mAuth.sendPasswordResetEmail(user.getEmail()).addOnCompleteListener(new OnCompleteListener<Void>() {
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

    @SuppressLint("SetTextI18n")
    private void showData(User user) {
        profileEmail.setText(user.getEmail());
        profileName.setText(user.getName());
        profileSurname.setText(user.getSurname());
        pval = user.getDistanceToShowPosts();
        distancePosts.setMax(300);
        distancePosts.setProgress(pval);
        showKilometers.setText(pval+ " Km");
        distancePosts.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                pval = progress;
                showKilometers.setText(pval+" Km");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                showKilometers.setText(pval+ " Km");
            }
        });
        Picasso.get().load(user.getRoute()).fit().centerCrop().into(profilePic);

    }
    private void signOut() {
        mAuth.signOut();
        Intent intent = new Intent(getContext(), MainActivity.class);
        startActivity(intent);
        Objects.requireNonNull(getActivity()).finish();

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
