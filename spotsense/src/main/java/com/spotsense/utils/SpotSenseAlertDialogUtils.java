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

    public static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    public static void showSnakeBar(String message, View view, Context context) {
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG);
        View v = snackbar.getView();
        v.setBackgroundColor(ContextCompat.getColor(context, android.R.color.black));
        TextView tv = v.findViewById(com.google.android.material.R.id.snackbar_text);
        tv.setTextColor(ContextCompat.getColor(context, android.R.color.white));
        snackbar.show();
    }

    public static void showAlert(Context context, String msg) {
        new AlertDialog.Builder(context).setIcon(0).setTitle(context.getString(R.string.app_name)).setMessage(msg).setCancelable(false)
                .setNeutralButton("OK", null).show();
    }

    public static void showAlertSignup(Context context, String msg, DialogInterface.OnClickListener onYesClick) {
        new AlertDialog.Builder(context).setIcon(0).setTitle(context.getString(R.string.app_name)).setMessage(msg).setCancelable(false)
                .setNeutralButton("OK", onYesClick).show();
    }

    public static void CustomAlert(Context context, String title, String message, String Positive_text, String Negative_text, DialogInterface.OnClickListener PositiveListener, DialogInterface.OnClickListener NegativeListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context).setTitle(title).setMessage(message)
                .setNeutralButton(Positive_text, PositiveListener).setNeutralButton(Negative_text, NegativeListener);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public static void showConfirmAlert(Context context, String msg, DialogInterface.OnClickListener onYesClick) {
        new AlertDialog.Builder(context).setIcon(0).setTitle(context.getString(R.string.app_name)).setMessage(msg).setCancelable(false)
                .setNegativeButton("No", null)
                .setPositiveButton("Yes", onYesClick).show();
    }

    public static void showConfirmAlertWithNO(Context context, String msg, DialogInterface.OnClickListener onYesClick, DialogInterface.OnClickListener onNoClick) {
        new AlertDialog.Builder(context).setIcon(0).setTitle(context.getString(R.string.app_name)).setMessage(msg).setCancelable(false)
                .setNegativeButton("No", onNoClick)
                .setPositiveButton("Yes", onYesClick).show();
    }

    public static void showConfirmAlertWithButtonName(Context context, String msg, DialogInterface.OnClickListener onYesClick, String strPositiveButton, String strNegaviteButton) {
        new AlertDialog.Builder(context).setIcon(0).setTitle(context.getString(R.string.app_name)).setMessage(msg).setCancelable(false)
                .setNegativeButton(strNegaviteButton, null)
                .setPositiveButton(strPositiveButton, onYesClick).show();
    }

    public static void showLogoutAlert(Context context, String msg, DialogInterface.OnClickListener onYesClick) {
        new AlertDialog.Builder(context).setIcon(0).setTitle(context.getString(R.string.app_name)).setMessage(msg).setCancelable(false)
                .setNegativeButton("No", null)
                .setPositiveButton("Yes", onYesClick).show();
    }

    public static void showDeleteAlert(Context context, String msg, DialogInterface.OnClickListener onYesClick) {
        new AlertDialog.Builder(context).setIcon(0).setTitle(context.getString(R.string.app_name)).setMessage(msg).setCancelable(false)
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Delete", onYesClick).show();
    }

    public static void showAlert(Context context, String msg, DialogInterface.OnClickListener onYesClick) {
        new AlertDialog.Builder(context).setIcon(0).setTitle(context.getString(R.string.app_name)).setMessage(msg).setCancelable(false)
                .setNeutralButton("OK", onYesClick).show();
    }

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
