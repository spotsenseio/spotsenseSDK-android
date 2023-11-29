package com.app.spotsense.network.loggingInterceptor

object I {
    @JvmStatic
    fun Log(type: Int, tag: String?, msg: String?) {
        when (type) {
            android.util.Log.VERBOSE -> {}
            android.util.Log.DEBUG -> {}
            android.util.Log.ERROR -> android.util.Log.e(tag, msg!!)
            android.util.Log.INFO -> {}
            android.util.Log.WARN -> {}
            android.util.Log.ASSERT -> {}
        }
    }
}