package com.ibrahimekinci.barcrowd.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Build;

import com.ibrahimekinci.barcrowd.util.AppLogger;

/**
 * Utility class for checking network connectivity in a modern, API-compatible way.
 */
public class ConnectivityUtil {
    /**
     * Checks if the device is connected to an active network (Wi-Fi or mobile data).
     * @param context The application context.
     * @return true if connected, false otherwise.
     */
    public static boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
            AppLogger.w("ConnectivityManager is null");
            return false;
        }
            // Use NetworkCapabilities for API 23+ (Android 6.0+)
            NetworkCapabilities capabilities = cm.getNetworkCapabilities(cm.getActiveNetwork());
            if (capabilities != null) {
                return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                        capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED);
            }
            return false;
    }
}