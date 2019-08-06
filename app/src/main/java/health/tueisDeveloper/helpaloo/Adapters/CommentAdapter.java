package health.tueisDeveloper.helpaloo.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import health.tueisDeveloper.helpaloo.R;
import health.tueisDeveloper.helpaloo.Classes.Valoration;
import java.util.ArrayList;
import java.util.Objects;

public class CommentAdapter extends ArrayAdapter<Valoration> {

    private Context mContext;
    private int mResource;

    public CommentAdapter(@NonNull Context context, int resource, ArrayList<Valoration> valorations) {
        super(context, resource, valorations);
        mContext = context;
        mResource = resource;
    }

    private static class ViewHolder {
        TextView comment;
    }

    @SuppressLint("ViewHolder")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        String comment = Objects.requireNonNull(getItem(position)).getComment();
        ViewHolder holder;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);
        holder= new ViewHolder();
        holder.comment = convertView.findViewById(R.id.commentValue);
        holder.comment.setText(comment);
        convertView.setTag(holder);
        return convertView;
    }
}
