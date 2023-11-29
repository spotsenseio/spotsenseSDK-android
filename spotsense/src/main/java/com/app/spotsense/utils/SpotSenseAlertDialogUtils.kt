package com.app.spotsense.utils

import android.app.AlertDialog
import android.content.Context

object SpotSenseAlertDialogUtils {
    fun showInternetAlert(context: Context?) {
        try {
            if (context == null) return
            AlertDialog.Builder(context).setIcon(0)
                .setTitle(SpotSenseConstants.STR_INTERNET_ALERT_TITLE)
                .setMessage(SpotSenseConstants.STR_INTERNET_ALERT_MESSAGE)
                .setCancelable(false).setNeutralButton("OK", null).show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}