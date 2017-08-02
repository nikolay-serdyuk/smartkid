package com.nserdyuk.smartkid.common;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import com.nserdyuk.smartkid.R;

public final class Dialogs {
    public static void showExitDialog(final AppCompatActivity activity) {
        new AlertDialog.Builder(activity)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(activity.getResources().getString(R.string.exitQuestion))
                .setPositiveButton(activity.getResources().getString(R.string.yesAnswer), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        activity.finish();
                    }
                })
                .setNegativeButton(activity.getResources().getString(R.string.noAnswer), null)
                .show();

    }

    private Dialogs() {
    }
}
