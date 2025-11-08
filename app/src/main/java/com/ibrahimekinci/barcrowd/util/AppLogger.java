package com.ibrahimekinci.barcrowd.util;

import android.util.Log;

public class AppLogger {
    private static final String TAG = "BarCrowdApp";

    public static void v(String message) {
        Log.v(TAG, message);
    }

    public static void d(String message) {
        Log.d(TAG, message);
    }

    public static void i(String message) {
        Log.i(TAG, message);
    }

    public static void w(String message) {
        Log.w(TAG, message);
    }

    public static void w(String message,Exception ex) {
        Log.w(TAG, message, ex);
    }

    public static void e(String message, Throwable t) {
        Log.e(TAG, message, t);
    }

    public static void e(String message) {
        Log.e(TAG, message);
    }
}