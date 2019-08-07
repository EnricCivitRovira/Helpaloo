package health.tueisDeveloper.helpaloo.Adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import health.tueisDeveloper.helpaloo.Fragments.AddPost;
import health.tueisDeveloper.helpaloo.Classes.Post;
import health.tueisDeveloper.helpaloo.Classes.User;
import health.tueisDeveloper.helpaloo.Fragments.PostDescription;
import health.tueisDeveloper.helpaloo.R;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    private final ArrayList<Post> postslist;

    private String userID;
    private int context;
    private User user;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    static class MyViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        @SuppressLint("StaticFieldLeak")
        static TextView postName;
        @SuppressLint("StaticFieldLeak")
        static TextView postPrice;
        @SuppressLint("StaticFieldLeak")
        static ImageView postPhoto;

        MyViewHolder(View itemView) {
            super(itemView);
            cv = itemView.findViewById(R.id.cv);
            postName = itemView.findViewById(R.id.postTitle);
            postPrice = itemView.findViewById(R.id.postPrice);
            postPhoto = itemView.findViewById(R.id.postPhoto);

        }

    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MyAdapter(ArrayList<Post> postslist, int context, User user) {
        this.postslist = postslist;
        this.context = context; // 0 -> AllPosts, 1 -> MyPosts
        this.user = user;
    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public MyAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.post, parent, false);
        MyViewHolder pvh = new MyViewHolder(v);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getUid();
        return pvh;

    }

    // Replace the contents of a view (invoked by the layout manager)
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final Post post = postslist.get(position);
        MyViewHolder.postName.setText(post.getTitle());
        MyViewHolder.postPrice.setText(post.getPrize()+ " €");
        String url = post.getRoute();
        if(!url.equals("")){
            Picasso.get().load(url).fit().centerCrop().into(MyViewHolder.postPhoto);
        }
        MyViewHolder.postPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(post.getUserId().equals(userID)){
                    AppCompatActivity activity = (AppCompatActivity) v.getContext();
                    AddPost myPost = new AddPost(post, 1, context, user);
                    activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment, myPost).addToBackStack(null).commit();
                }else {
                    AppCompatActivity activity = (AppCompatActivity) v.getContext();
                    PostDescription openPost = new PostDescription(post, user);
                    activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment, openPost).addToBackStack(null).commit();
                }

            }
        });

    }



    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return postslist.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}
