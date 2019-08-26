package health.tueisDeveloper.helpaloo.Dialogs;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import health.tueisDeveloper.helpaloo.Activities.MenuActivity;
import health.tueisDeveloper.helpaloo.Classes.Chat;
import health.tueisDeveloper.helpaloo.Classes.User;
import health.tueisDeveloper.helpaloo.Fragments.ValorationFragment;
import health.tueisDeveloper.helpaloo.R;

import java.util.Objects;


@SuppressLint("ValidFragment")
public class NoPublicationDialog extends AppCompatDialogFragment {
    private int type;

    private Chat chat;
    private User user;
    private DatabaseReference mDatabase;

    @SuppressLint("ValidFragment")
    public NoPublicationDialog(int type) {
        this.type = type;
    }

    public NoPublicationDialog(Chat chat, User user) {
        this.type = 4;
        this.chat = chat;
        this.user = user;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        Log.i("Tipo = ", String.valueOf(type));

        mDatabase = FirebaseDatabase.getInstance().getReference();

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        if (type == 1) {
            builder.setTitle("Atención").setMessage("No tienes publicaciones");
            builder.setNegativeButton("De acuerdo", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ((MenuActivity) Objects.requireNonNull(getActivity())).setFragment(2);
                }
            });
            builder.setPositiveButton("Crear Anuncio", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ((MenuActivity) Objects.requireNonNull(getActivity())).setFragment(1);
                }
            });
        }else if(type == 4){
            builder.setTitle("Atención").setMessage("¿Estás seguro que la tarea esta completa?");
            builder.setNegativeButton("Sí", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    mDatabase.child("posts").child(chat.getChatToID()).child(chat.getChatPostID()).child("status").setValue(1);
                    mDatabase.child("allPosts").child(chat.getChatPostID()).child("status").setValue(1);

                    mDatabase.child("chats_user").child(chat.getChatFromID()).removeValue();
                    mDatabase.child("chats_user").child(chat.getChatToID()).removeValue();

                    ValorationFragment foreignNewValoration = new ValorationFragment(user);
                    Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment, foreignNewValoration, "findThisFragment")
                            .addToBackStack(null)
                            .commit();
                }
            });
            builder.setPositiveButton("No, está por completar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Objects.requireNonNull(getFragmentManager()).popBackStackImmediate();

                }
            });
        }else if (type == 5){
            builder.setTitle("Atención").setMessage("No tienes nuevos mensajes, chatea con gente para ver aqui tus chats.");
            builder.setPositiveButton("Vale!", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();

                }
            });
        } else {
            builder.setTitle("Atención").setMessage("No hay publicaciones en tu zona...");
            builder.setNegativeButton("De acuerdo", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ((MenuActivity) Objects.requireNonNull(getActivity())).setFragment(0);
                }
            });
            builder.setPositiveButton("Ampliar rango de zona", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ((MenuActivity) Objects.requireNonNull(getActivity())).setFragment(2);
                }
            });
        }
        return builder.create();

    }
}
