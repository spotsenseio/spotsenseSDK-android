package com.spotsense.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;

public final class SpotSenseNetworkUtils {

    public static boolean isConnected(Context context) {
        if (isInternetAvailable(context)) {
            return true;
        } else {
            SpotSenseAlertDialogUtils.showInternetAlert(context);
            return false;
        }
    }

    public static boolean isNetworkConnected(Context context) {
        return isInternetAvailable(context);
    }

    @SuppressLint("MissingPermission")
    private static boolean isInternetAvailable(Context context) {
        if (context != null) {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnectedOrConnecting();
        } else {
            return false;
        }
    }
}
