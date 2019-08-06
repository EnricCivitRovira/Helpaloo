package com.example.helpaloo;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import static android.app.Activity.RESULT_OK;


@SuppressLint("ValidFragment")
public class AddPost extends Fragment {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private StorageReference storageReference;
    private EditText introducedTitle;
    private EditText introducedDescription;
    private EditText introducedPrize;
    private Spinner introducedTime;

    private static final int PICK_IMAGE_REQUEST = 1;

    private ImageView imageView;
    private Uri imageUri;

    private  String postid;
    private String userid;
    private Task<Uri> route;
    private static String routeString;

    private String userName;
    private String userSurname;

    private Post post;
    private int type;
    private int context;
    private User user;

    @SuppressLint("ValidFragment")
    AddPost(Post post, int type, int context, User user) {
        this.type = type; // 0 -> No editable 1 -> editable
        this.context = context; // 0 -> All Posts, 1-> MyPosts
        if (type == 1) {
            this.post = post;
        }
        this.user = user;
    }



    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_add_post, container, false);
        // BIND
        Button uploadImage = view.findViewById(R.id.addImage);
        Button uploadPost = view.findViewById(R.id.addPost);
        introducedTitle = view.findViewById(R.id.postTitle);
        introducedDescription = view.findViewById(R.id.postDescription);
        introducedPrize = view.findViewById(R.id.postPrice);
        introducedTime = view.findViewById(R.id.postTime);
        imageView = view.findViewById(R.id.postImage);
        Button deletePost = view.findViewById(R.id.deletePost);

        // firebase
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        // Añadir Anuncio
        if(type == 0) {
            Objects.requireNonNull(((MenuActivity) Objects.requireNonNull(getActivity())).getSupportActionBar()).setTitle("Añadir Anuncio");

            deletePost.setVisibility(View.GONE);

            userid = Objects.requireNonNull(currentUser).getUid();
            postid = mDatabase.child("posts").child(userid).push().getKey();
            userName = user.getName();
            userSurname = user.getSurname();

        // Editar Anuncio
        }else {

            ((MenuActivity) Objects.requireNonNull(getActivity())).setFragmentPosition(-1);

            deletePost.setVisibility(View.VISIBLE);

            Objects.requireNonNull(((MenuActivity) getActivity()).getSupportActionBar()).setTitle("Editar Anuncio");
            Picasso.get().load(post.getRoute()).into(imageView);
            introducedTitle.setText(post.getTitle());
            introducedDescription.setText(post.getDescription());
            introducedPrize.setText(post.getPrize());

            deletePost.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deletePostElement(post.getPostId(), post.getUserId());
                    if (context == 0) {
                        ((MenuActivity) Objects.requireNonNull(getActivity())).setFragment(4);
                    }else if(context == 1){
                        ((MenuActivity) Objects.requireNonNull(getActivity())).setFragment(5);
                    }


                }
            });

        }

        uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });
        uploadPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateForm()){
                    if(type == 0) {
                        uploadImage(postid);
                    }else{
                        updatePost(post.getPostId(), post.getUserId());
                    }
                }

            }
        });


        return view;
}

    private void deletePostElement(String postId, String userid) {
        mDatabase.child("posts").child(userid).child(postId).removeValue();
        mDatabase.child("allPosts").child(postId).removeValue();
        Toast.makeText(getActivity(), "Articulo Eliminado", Toast.LENGTH_SHORT).show();

        FragmentManager fragmentManager = getFragmentManager();
        SearchPost myPosts = new SearchPost(1, 1);
        Objects.requireNonNull(fragmentManager).beginTransaction().replace(R.id.fragment, myPosts)
                .commit();
    }

    private void updatePost(final String postId, final String userid) {
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Editando Post...");
        progressDialog.show();

        if(imageUri != null)
        {
            String[] filename = Objects.requireNonNull(imageUri.getLastPathSegment()).split("/");
            final StorageReference ref = storageReference.child("images/"+mAuth.getUid()+"/"+postId+"/"+filename[filename.length-1]);

            Log.w("POST PATH: "+ref, "TEST");
            ref.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(getActivity(), "Editado", Toast.LENGTH_SHORT).show();
                            route = ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    routeString = Objects.requireNonNull(route.getResult()).toString();
                                    insertPost(postId, userid, introducedTitle.getText().toString(), introducedDescription.getText().toString(), introducedPrize.getText().toString(), introducedTime.getSelectedItem().toString(), routeString, user.getLatitude(), user.getLongitude());
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
            updatePostPart(postId, userid, introducedTitle.getText().toString(), introducedDescription.getText().toString(), introducedPrize.getText().toString(), introducedTime.getSelectedItem().toString(), user.getLatitude(),user.getLongitude());
            progressDialog.dismiss();
            Toast.makeText(getActivity(), "Articulo Editado", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadImage(final String postid) {
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Subiendo Post...");
        progressDialog.show();

        if(imageUri != null)
        {
            String[] filename = Objects.requireNonNull(imageUri.getLastPathSegment()).split("/");
            final StorageReference ref = storageReference.child("images/"+mAuth.getUid()+"/"+postid+"/"+filename[filename.length-1]);

            Log.w("POST PATH: "+ref, "TEST");
            ref.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            route = ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    routeString = Objects.requireNonNull(route.getResult()).toString();
                                    insertPost(postid, userid, introducedTitle.getText().toString(), introducedDescription.getText().toString(), introducedPrize.getText().toString(), introducedTime.getSelectedItem().toString(), routeString, user.getLatitude(), user.getLongitude());
                                    Toast.makeText(getActivity(), "Subido", Toast.LENGTH_SHORT).show();
                                    ((MenuActivity) Objects.requireNonNull(getActivity())).setFragment(4);
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
            insertPost(postid, userid, introducedTitle.getText().toString(), introducedDescription.getText().toString(), introducedPrize.getText().toString(), introducedTime.getSelectedItem().toString(), "",user.getLatitude(), user.getLongitude());
            progressDialog.dismiss();
            Toast.makeText(getActivity(), "Articulo Subido", Toast.LENGTH_SHORT).show();
            ((MenuActivity) Objects.requireNonNull(getActivity())).setFragment(4);
        }


    }


    private void insertPost(String postId, String userId, String title, String description, String prize, String time, String route, double latitude, double longitude) {
        if(route.equals("")){
            route = "https://firebasestorage.googleapis.com/v0/b/helpaloo.appspot.com/o/DefaultPics%2Fdownload.jpg?alt=media&token=90322697-5e5d-4a37-81d4-7acde7b326f9";
        }
        Post post = new Post (postId,userId,title,description, prize, time, route, userName, userSurname, latitude, longitude);

        DatabaseReference refAll = mDatabase.child("allPosts").child(postId);
        refAll.setValue(post);

        DatabaseReference ref = mDatabase.child("posts").child(userId).child(postId);
        ref.setValue(post);
    }

    private void updatePostPart(String postId, String userId, String title, String description, String prize, String time, double latitude, double longitude) {

        Post post = new Post (postId,userId,title,description, prize, time, "noedited", userName, userSurname, latitude, longitude);

        DatabaseReference refAll = mDatabase.child("allPosts").child(postId);
        DatabaseReference ref = mDatabase.child("posts").child(userId).child(postId);

        refAll.child("title").setValue(post.getTitle());
        ref.child("title").setValue(post.getTitle());

        refAll.child("description").setValue(post.getDescription());
        ref.child("description").setValue(post.getDescription());

        refAll.child("prize").setValue(post.getPrize());
        ref.child("prize").setValue(post.getPrize());

        refAll.child("latitude").setValue(post.getLatitude());
        ref.child("latitude").setValue(post.getLatitude());

        refAll.child("longitude").setValue(post.getLongitude());
        ref.child("longitude").setValue(post.getLongitude());

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
            Picasso.get().load(imageUri).into(imageView);
        }

    }

    private boolean validateForm() {
        boolean valid = true;

        String title = introducedTitle.getText().toString();
        if (TextUtils.isEmpty(title)) {
            introducedTitle.setError("Debes introducir un Titulo en la tarea.");
            valid = false;
        } else {
            introducedTitle.setError(null);
        }

        String price = introducedPrize.getText().toString();
        if (TextUtils.isEmpty(price)) {
            introducedPrize.setError("Debes introducir un precio para tu tarea");
            valid = false;
        } else if(!TextUtils.isDigitsOnly(price)){
            introducedPrize.setError("El precio debe ser un número");
            valid = false;
        }
        else {
            introducedPrize.setError(null);
        }

        String description = introducedDescription.getText().toString();
        if (TextUtils.isEmpty(description)) {
            introducedDescription.setError("Debes introducir una breve descripción");
            valid = false;
        } else {
            introducedDescription.setError(null);
        }

        return valid;
    }
}
