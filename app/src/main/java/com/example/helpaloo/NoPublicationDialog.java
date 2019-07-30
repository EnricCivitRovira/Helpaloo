package com.example.helpaloo;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;


@SuppressLint("ValidFragment")
public class NoPublicationDialog extends AppCompatDialogFragment {
    int type;
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
                    ((MenuActivity) getActivity()).setFragment(2);
                }
            });
            builder.setPositiveButton("Crear Anuncio", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ((MenuActivity) getActivity()).setFragment(1);
                }
            });
        }else {
            builder.setTitle("Atención").setMessage("No hay publicaciones en tu zona...");
            builder.setNegativeButton("De acuerdo", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ((MenuActivity) getActivity()).setFragment(0);
                }
            });
            builder.setPositiveButton("Ampliar rango de zona", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ((MenuActivity) getActivity()).setFragment(2);
                }
            });
        }
        return builder.create();

    }
}
