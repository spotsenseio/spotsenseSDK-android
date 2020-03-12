package com.spotsense.data.network.loggingInterceptor;

import android.util.Log;

class I {
    static void Log(int type, String tag, String msg) {
        switch (type) {
            case Log.VERBOSE:
//                Log.v(tag, msg);
                break;
            case Log.DEBUG:
//                Log.d(tag, msg);
                break;
            case Log.ERROR:
                Log.e(tag, msg);
                break;
            case Log.INFO:
//                Log.i(tag, msg);
                break;
            case Log.WARN:
//                Log.w(tag, msg);
                break;
            case Log.ASSERT:
//                Log.wtf(tag, msg);
                break;
        }
    }
}