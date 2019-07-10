package com.example.helpaloo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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


import static android.app.Activity.RESULT_OK;


public class AddPost extends Fragment {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private FirebaseStorage storage;
    StorageReference storageReference;
    private FirebaseUser currentUser;
    private EditText introducedTitle;
    private EditText introducedDescription;
    private EditText introducedPrize;
    private EditText introducedTime;

    private static final int PICK_IMAGE_REQUEST = 1;

    private Button uploadImage;
    private Button uploadPost;
    private ImageView imageView;
    private Uri imageUri;

    private  String postid;
    private String userid;
    private Task<Uri> route;
    private static String routeString;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_add_post, container, false);

        ((MenuActivity) getActivity()).getSupportActionBar().setTitle("Añadir Anuncio");

        uploadImage = (Button) view.findViewById(R.id.addImage);
        uploadPost = (Button) view.findViewById(R.id.addPost);

        introducedTitle = (EditText) view.findViewById(R.id.postTitle);
        introducedDescription = (EditText) view.findViewById(R.id.postDescription);
        introducedPrize = (EditText) view.findViewById(R.id.postPrice);
        introducedTime = (EditText) view.findViewById(R.id.postTime);

        imageView = (ImageView) view.findViewById(R.id.postImage);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        userid = currentUser.getUid();
        postid = mDatabase.child("posts").child(userid).push().getKey();

        uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        uploadPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage(postid);
            }
        });



        return view;
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
                                    insertPost(postid, userid, introducedTitle.getText().toString(), introducedDescription.getText().toString(), introducedPrize.getText().toString(), introducedTime.getText().toString(), routeString, 0);
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
            insertPost(postid, userid, introducedTitle.getText().toString(), introducedDescription.getText().toString(), introducedPrize.getText().toString(), introducedTime.getText().toString(), "", 0);
            progressDialog.dismiss();
            Toast.makeText(getActivity(), "Articulo Subido", Toast.LENGTH_SHORT).show();

        }

    }




    private void insertPost(String postId, String userId, String title, String description, String prize, String time, String route, int type) {

        if(route.equals("")){
            route = "https://firebasestorage.googleapis.com/v0/b/helpaloo.appspot.com/o/images%2FW8ImpGhMsShzkZ3qVkbcytf05uB3%2F-LjGqfKpgmWgP58ip6pC%2Fdownload.jpg?alt=media&token=2aa54b1d-e114-49a4-886c-567ca06b7b82";
        }
        Post post = new Post (postId,userId,title,description, prize, time, route);
        Log.w("POST: "+userId+" postid: "+ postId, "TEST");
        if (type == 0){
            DatabaseReference refAll = mDatabase.child("allPosts").child(postId);
            refAll.setValue(post);
        }
        DatabaseReference ref = mDatabase.child("posts").child(userId).child(postId);
        ref.setValue(post);
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
