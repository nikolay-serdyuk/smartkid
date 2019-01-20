package com.nserdyuk.smartkid.common;

import android.app.Dialog;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import com.nserdyuk.smartkid.R;

public final class Dialogs {
    private Dialogs() {
    }

    public static Dialog showYesNoDialog(AppCompatActivity activity, String title, String yesMsg,
            DialogInterface.OnClickListener onYesListener, String noMsg) {
        return new AlertDialog.Builder(activity)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(title)
                .setPositiveButton(yesMsg, onYesListener)
                .setNegativeButton(noMsg, null)
                .show();
    }

    public static Dialog showExitDialog(final AppCompatActivity activity) {
        return showYesNoDialog(activity,
                activity.getResources().getString(R.string.exit_question),
                activity.getResources().getString(R.string.yes_answer),
                (dialog, which) -> activity.finish(),
                activity.getResources().getString(R.string.no_answer));
    }
}
