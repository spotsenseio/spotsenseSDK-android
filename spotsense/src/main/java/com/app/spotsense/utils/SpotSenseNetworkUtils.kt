package com.app.spotsense.utils

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import com.app.spotsense.utils.SpotSenseAlertDialogUtils.showInternetAlert

object SpotSenseNetworkUtils {
    fun isConnected(context: Context?): Boolean {
        return if (isInternetAvailable(context)) true else {
            showInternetAlert(context)
            false
        }
    }

    fun isNetworkConnected(context: Context?): Boolean {
        return isInternetAvailable(context)
    }

    @SuppressLint("MissingPermission")
    private fun isInternetAvailable(context: Context?): Boolean {
        return if (context != null) {
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            connectivityManager.activeNetworkInfo != null && connectivityManager.activeNetworkInfo!!.isConnectedOrConnecting
        } else false
    }
}