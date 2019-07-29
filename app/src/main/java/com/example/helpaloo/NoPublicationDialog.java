package com.example.helpaloo;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

public class NoPublicationDialog extends AppCompatDialogFragment {
    protected NavigationView navigationView;
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Atención").setMessage("No tienes publicaciones");

        builder.setNegativeButton("De acuerdo", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ((MenuActivity) getActivity()).setFragment(2);
            }
        });

        builder .setPositiveButton("Crear Anuncio", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ((MenuActivity) getActivity()).setFragment(1);
            }
        });

        return builder.create();

    }
}
