package com.example.helpaloo.Dialogs;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.helpaloo.Activities.MenuActivity;

import java.util.Objects;


@SuppressLint("ValidFragment")
public class NoPublicationDialog extends AppCompatDialogFragment {
    private int type;
    @SuppressLint("ValidFragment")
    public NoPublicationDialog(int type) {
        this.type = type;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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
        }else {
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
