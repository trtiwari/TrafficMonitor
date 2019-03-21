package com.example.root.trafficmonitor;

import android.app.Service;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by root on 1/31/18.
 */

public class TrafficMonitorService extends Service
{
    @Override
    public void onCreate()
    {
        Log.d("Service.onCreate()","created service");
        return;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Log.d("onStartCommand()","starting service");
        NetworkMonitorThreadEmulator networkMonitorThreadEmulator = new NetworkMonitorThreadEmulator(getGoogleMapsUID());
        networkMonitorThreadEmulator.start();
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }

    @Override
    public void onDestroy()
    {
        return;
    }

    public int getGoogleMapsUID()
    {
        for (ApplicationInfo app : this.getPackageManager().getInstalledApplications(0)) {
            if (app.packageName.equals("com.google.android.apps.maps"))
            {
                Log.d(Integer.toString(app.uid), app.packageName);
                return app.uid;
            }
        }
        return -1; // android.os.Process.myUid();
    }
}
