package com.example.bilkenthalisahaapp.dialogBoxes;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.bilkenthalisahaapp.MatchInfo;
import com.example.bilkenthalisahaapp.appObjects.Player;
import com.example.bilkenthalisahaapp.appObjects.User;

public class KickPlayerDialogFragment extends DialogFragment {

    Player kickedPlayer;
    User kickedUser;
    MatchInfo fragment;

    public KickPlayerDialogFragment(Player kickedPlayer, User kickedUser, MatchInfo fragment) {
        this.kickedPlayer = kickedPlayer;
        this.kickedUser = kickedUser;
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Are you sure to kick "+ kickedUser.getName() + " " + kickedUser.getSurname()+  " from the match?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // kick the player from the match
                        fragment.kickPlayerFromMatch(kickedPlayer);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                        // return back

                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();

    }

}
