package com.example.root.trafficmonitor;

import android.Manifest;
import android.app.Activity;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity
{

    // Storage Permissions
    // this is needed if we want to store the network data we
    // collect on the device itself. It is NOT essential, merely a convenience.
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
            };
    NetworkMonitorThreadRealDevice networkMonitorThreadRealDevice;

    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
         final Intent serviceIntent = new Intent(this,TrafficMonitorService.class);
         startService(serviceIntent);
         return;

         // this will vary depending on whether you want the app to have a UI or not.
        // if no UI is necessary, just start the service -- no need to handle all the
        // filenames and button clicks
        // if UI is necessary, uncomment the lines below

//        this.verifyStoragePermissions(this);
//        final Button startRecordingButton = findViewById(R.id.startRecording);
//        final Button stopRecordingButton = findViewById(R.id.stopRecording);
//        stopRecordingButton.setEnabled(false);
//        startRecordingButton.setOnClickListener( new View.OnClickListener()
//        {
//            @Override
//            public void onClick(View v)
//            {
//                startRecordingButton.setEnabled(false);
//                stopRecordingButton.setEnabled(true);
//                EditText editText = findViewById(R.id.fileName);
//                String file = editText.getText().toString();
//                networkMonitorThreadRealDevice = new NetworkMonitorThreadRealDevice(getGoogleMapsUID(),file);
//                networkMonitorThreadRealDevice.start();
//            }
//        });
//
//        stopRecordingButton.setOnClickListener( new View.OnClickListener()
//        {
//            @Override
//            public void onClick(View v)
//            {
//                networkMonitorThreadRealDevice.exit();
//                stopRecordingButton.setEnabled(false);
//            }
//        });
//
//        return;
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
