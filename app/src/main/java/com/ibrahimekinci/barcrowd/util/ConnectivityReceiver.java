package com.ibrahimekinci.barcrowd.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;

import com.ibrahimekinci.barcrowd.BarCrowdApplication;
import com.ibrahimekinci.barcrowd.data.repository.LiveUpdateRepository;

/**
 * Broadcast receiver to sync pending updates when connectivity changes to online.
 */
public class ConnectivityReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (ConnectivityUtil.isOnline(context)) {
            BarCrowdApplication app = (BarCrowdApplication) context.getApplicationContext();
            LiveUpdateRepository repo = app.getDependencyInjector().getLiveUpdateRepository();
            repo.syncPending();
            AppLogger.i("Connectivity restored; syncing pending updates");
        }
    }
}