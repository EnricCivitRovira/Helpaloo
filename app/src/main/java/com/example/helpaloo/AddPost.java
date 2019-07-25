package com.example.helpaloo;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;


import static android.app.Activity.RESULT_OK;


@SuppressLint("ValidFragment")
public class AddPost extends Fragment {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private FirebaseStorage storage;
    StorageReference storageReference;
    private FirebaseUser currentUser;
    private EditText introducedTitle;
    private EditText introducedDescription;
    private EditText introducedPrize;
    private Spinner introducedTime;

    private static final int PICK_IMAGE_REQUEST = 1;

    private Button uploadImage;
    private Button uploadPost;
    private ImageView imageView;
    private Uri imageUri;

    private  String postid;
    private String userid;
    private Task<Uri> route;
    private static String routeString;
    private FirebaseDatabase mFirebaseDatabase;
    private Button deletePost;

    private String userName;
    private String userSurname;

    private Post post;
    private int type;

    @SuppressLint("ValidFragment")
    AddPost(Post post, int type) {
        this.type = type;
        if (type == 1) {
            this.post = post;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_add_post, container, false);

        uploadImage = view.findViewById(R.id.addImage);
        uploadPost = view.findViewById(R.id.addPost);
        introducedTitle = view.findViewById(R.id.postTitle);
        introducedDescription = view.findViewById(R.id.postDescription);
        introducedPrize = view.findViewById(R.id.postPrice);
        introducedTime = view.findViewById(R.id.postTime);
        imageView = view.findViewById(R.id.postImage);
        deletePost = view.findViewById(R.id.deletePost);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        mDatabase = FirebaseDatabase.getInstance().getReference();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        mFirebaseDatabase = FirebaseDatabase.getInstance();

        if(type == 0) {
            ((MenuActivity) getActivity()).getSupportActionBar().setTitle("Añadir Anuncio");

            deletePost.setVisibility(view.GONE);

            userid = currentUser.getUid();
            postid = mDatabase.child("posts").child(userid).push().getKey();


            mFirebaseDatabase.getReference("users/" + userid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    userName = dataSnapshot.getValue(User.class).name;
                    userSurname = dataSnapshot.getValue(User.class).surname;
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


        }else {

            deletePost.setVisibility(view.VISIBLE);

            ((MenuActivity) getActivity()).getSupportActionBar().setTitle("Editar Anuncio");
            Picasso.get().load(post.route).into(imageView);
            introducedTitle.setText(post.title);
            introducedDescription.setText(post.description);
            introducedPrize.setText(post.prize);

            deletePost.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deletePostElement(post.postId, post.userId);
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
                if(type == 0) {
                    uploadImage(postid);
                }else{
                    updatePost(post.postId, post.userId);
                }
            }
        });


        return view;
}

    private void deletePostElement(String postId, String userid) {
        mDatabase.child("posts").child(userid).child(postId).removeValue();
        mDatabase.child("allPosts").child(postId).removeValue();
        Toast.makeText(getActivity(), "Articulo Eliminado", Toast.LENGTH_SHORT).show();

                // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getFragmentManager();
        SearchPost myPosts = new SearchPost(1);
        fragmentManager.beginTransaction().replace(R.id.fragment, myPosts)
                .commit();
    }

    private void updatePost(String postId, final String userid) {
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Editando Post...");
        progressDialog.show();

        if(imageUri != null)
        {
            String[] filename = imageUri.getLastPathSegment().split("/");
            Log.i("ImageRoute = ",imageUri.getPath() );
            final StorageReference ref = storageReference.child("images/"+mAuth.getUid()+"/"+postid+"/"+filename[filename.length-1]);

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
                                    routeString = route.getResult().toString();
                                    insertPost(postid, userid, introducedTitle.getText().toString(), introducedDescription.getText().toString(), introducedPrize.getText().toString(), introducedTime.getSelectedItem().toString(), routeString, 0);
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
            updatePostPart(postId, userid, introducedTitle.getText().toString(), introducedDescription.getText().toString(), introducedPrize.getText().toString(), introducedTime.getSelectedItem().toString(), "noedited", 0);
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
            String[] filename = imageUri.getLastPathSegment().split("/");
            final StorageReference ref = storageReference.child("images/"+mAuth.getUid()+"/"+postid+"/"+filename[filename.length-1]);

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
                                    insertPost(postid, userid, introducedTitle.getText().toString(), introducedDescription.getText().toString(), introducedPrize.getText().toString(), introducedTime.getSelectedItem().toString(), routeString, 0);
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
            insertPost(postid, userid, introducedTitle.getText().toString(), introducedDescription.getText().toString(), introducedPrize.getText().toString(), introducedTime.getSelectedItem().toString(), "", 0);
            progressDialog.dismiss();
            Toast.makeText(getActivity(), "Articulo Subido", Toast.LENGTH_SHORT).show();
        }

    }


    private void insertPost(String postId, String userId, String title, String description, String prize, String time, String route, int type) {
        if(route.equals("")){
            route = "https://firebasestorage.googleapis.com/v0/b/helpaloo.appspot.com/o/images%2FW8ImpGhMsShzkZ3qVkbcytf05uB3%2F-LjGqfKpgmWgP58ip6pC%2Fdownload.jpg?alt=media&token=2aa54b1d-e114-49a4-886c-567ca06b7b82";
        }
        Post post = new Post (postId,userId,title,description, prize, time, route, userName, userSurname);
        Log.w("POST: "+userId+" postid: "+ postId, "TEST");
        if (type == 0){
            Log.i("AllPosts: ", postId);
            DatabaseReference refAll = mDatabase.child("allPosts").child(postId);
            refAll.setValue(post);
        }
        DatabaseReference ref = mDatabase.child("posts").child(userId).child(postId);
        ref.setValue(post);
    }

    private void updatePostPart(String postId, String userId, String title, String description, String prize, String time, String route, int type) {

        Post post = new Post (postId,userId,title,description, prize, time, route, userName, userSurname);

        DatabaseReference refAll = mDatabase.child("allPosts").child(postId);
        DatabaseReference ref = mDatabase.child("posts").child(userId).child(postId);

        refAll.child("title").setValue(post.title);
        ref.child("title").setValue(post.title);

        refAll.child("description").setValue(post.description);
        ref.child("description").setValue(post.description);

        refAll.child("prize").setValue(post.prize);
        ref.child("prize").setValue(post.prize);

        if(!route.equals("noedited")){
            refAll.child(route).setValue(post.route);
            ref.child(route).setValue(post.route);
        }

        Log.i("Articulo Editado: ", post.description + post.prize);
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
}
