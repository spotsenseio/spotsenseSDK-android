package com.spotsense.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;
import com.spotsense.R;


public final class SpotSenseAlertDialogUtils {

    public static void showInternetAlert(Context context) {
        try {
            if (context == null) {
                return;
            }
            new AlertDialog.Builder(context).setIcon(0).setTitle(SpotSenseConstants.STR_INTERNET_ALERT_TITLE)
                    .setMessage(SpotSenseConstants.STR_INTERNET_ALERT_MESSAGE)
                    .setCancelable(false).setNeutralButton("OK", null).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
