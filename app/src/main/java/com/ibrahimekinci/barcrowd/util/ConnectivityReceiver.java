package com.ibrahimekinci.barcrowd.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.ibrahimekinci.barcrowd.BarCrowdApplication;
import com.ibrahimekinci.barcrowd.data.repository.LiveUpdateRepository;

/**
 * Broadcast receiver to sync pending updates when connectivity changes to online.
 */
public class ConnectivityReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (ConnectivityUtil.isOnline(context)) {
            AppLogger.i("Connectivity restored; queueing sync...");


            final PendingResult pendingResult = goAsync();


            new Thread(() -> {
                try {

                    if (context != null) {
                        BarCrowdApplication app = (BarCrowdApplication) context.getApplicationContext();
                        LiveUpdateRepository repo = app.getDependencyInjector().getLiveUpdateRepository();

                        repo.syncPending();

                        AppLogger.i("Background sync of pending updates complete.");
                    } else {
                        AppLogger.w("ConnectivityReceiver context is null, sync skipped.");
                    }
                } catch (Exception e) {
                    AppLogger.e("Background sync failed", e);
                } finally {
                    pendingResult.finish();
                }
            }).start();
        }
    }
}