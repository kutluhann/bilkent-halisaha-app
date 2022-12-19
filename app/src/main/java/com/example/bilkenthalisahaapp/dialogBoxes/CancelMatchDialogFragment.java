package com.example.bilkenthalisahaapp.dialogBoxes;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.bilkenthalisahaapp.Firestore;
import com.example.bilkenthalisahaapp.appObjects.Match;
import com.example.bilkenthalisahaapp.appObjects.User;


public class CancelMatchDialogFragment extends DialogFragment {

    private Match match;
    public CancelMatchDialogFragment(Match match) {
        this.match = match;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Are you sure to cancel this match?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // cancel the match
                        cancelMatch();
                        Context context = getActivity().getApplicationContext();
                        CharSequence text = "The match is cancelled";
                        int duration = Toast.LENGTH_LONG;
                        Toast toast = Toast.makeText(context,text,duration);
                        toast.show();

                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();

    }

    public void setMatch(Match match){
        this.match = match;
    }

    private void cancelMatch(){
        Firestore.removeMatch(match);
    }
}
