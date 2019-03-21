package com.example.root.trafficmonitor;


import android.net.TrafficStats;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Created by trishita on 4/25/18.
 */


public class NetworkMonitorThreadRealDevice extends Thread
{
    private Long currentTime;
    private String outputData;
    public String file;
    private FileWriter descriptor;
    public int uid;
    private boolean keepRunning;

    NetworkMonitorThreadRealDevice (int uid, String file)
    {
        this.keepRunning = true;
        this.uid = uid;
        this.file = file;
    }

    private void init()
    {
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), this.file + ".txt");
        try
        {
            if (!file.exists())
            {
                file.createNewFile();
            }
            this.descriptor = new FileWriter(file, true);
        }
        catch (IOException ioe)
        {
            ioe.printStackTrace();
            return;
        }
    }

    @Override
    public void run()
    {

        this.init();
        long mobileRxPackets, mobileRxBytes, mobileTxPackets, mobileTxBytes;
        long oldMobileRxPackets, oldMobileRxBytes, oldMobileTxPackets, oldMobileTxBytes;
        long deltaMobileRxPackets, deltaMobileRxBytes, deltaMobileTxPackets, deltaMobileTxBytes;
        long totalRxBytes, totalRxPackets, totalTxPackets, totalTxBytes;
        long oldTotalRxBytes, oldTotalRxPackets, oldTotalTxPackets, oldTotalTxBytes;
        long deltaTotalRxBytes, deltaTotalRxPackets, deltaTotalTxPackets, deltaTotalTxBytes;

        try {

            oldMobileRxBytes = TrafficStats.getMobileRxPackets();
            oldMobileRxPackets = TrafficStats.getMobileTxBytes();
            oldMobileTxBytes = TrafficStats.getMobileTxPackets();
            oldMobileTxPackets = TrafficStats.getMobileRxBytes();

            oldTotalRxBytes = TrafficStats.getTotalRxBytes();
            oldTotalRxPackets = TrafficStats.getTotalRxPackets();
            oldTotalTxBytes = TrafficStats.getTotalTxBytes();
            oldTotalTxPackets = TrafficStats.getTotalTxPackets();

        }
        catch (Exception e) {
            e.printStackTrace();
            return;
        }

        while(this.keepRunning)
        {
            try {
                TimeUnit.MILLISECONDS.sleep(500);}
            catch (Exception e){
                e.printStackTrace();
                return;
            }
            currentTime = System.currentTimeMillis() / 1000;
            mobileRxPackets = TrafficStats.getMobileRxPackets();
            mobileTxBytes = TrafficStats.getMobileTxBytes();
            mobileTxPackets = TrafficStats.getMobileTxPackets();
            mobileRxBytes = TrafficStats.getMobileRxBytes();

            deltaMobileRxBytes = mobileRxBytes - oldMobileRxBytes;
            deltaMobileRxPackets = mobileRxPackets - oldMobileRxPackets;
            deltaMobileTxBytes = mobileTxBytes - oldMobileTxBytes;
            deltaMobileTxPackets = mobileTxPackets - oldMobileTxPackets;

            oldMobileRxBytes = mobileRxBytes;
            oldMobileRxPackets = mobileRxPackets;
            oldMobileTxBytes = mobileTxBytes;
            oldMobileTxPackets = mobileTxPackets;

            totalRxBytes = TrafficStats.getTotalRxBytes();
            totalRxPackets = TrafficStats.getTotalRxPackets();
            totalTxBytes = TrafficStats.getTotalTxBytes();
            totalTxPackets = TrafficStats.getTotalTxPackets();

            deltaTotalRxBytes = totalRxBytes - oldTotalRxBytes;
            deltaTotalRxPackets = totalRxPackets - oldTotalRxPackets;
            deltaTotalTxBytes = totalTxBytes - oldTotalTxBytes;
            deltaTotalTxPackets = totalTxPackets - oldTotalTxPackets;

            oldTotalRxBytes = totalRxBytes;
            oldTotalRxPackets = totalRxPackets;
            oldTotalTxBytes = totalTxBytes;
            oldTotalTxPackets = totalTxPackets;

            outputData = String.format("%s,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d\n",
                    Long.toString(currentTime),
                    deltaMobileTxPackets,
                    deltaMobileRxPackets,
                    totalTxBytes,
                    totalRxBytes,
                    deltaTotalTxBytes,
                    deltaTotalRxBytes,
                    mobileTxBytes,
                    mobileRxBytes,
                    deltaMobileTxBytes,
                    deltaMobileRxBytes,
                    deltaTotalRxPackets,
                    deltaTotalTxPackets,
                    mobileRxPackets,
                    mobileTxPackets,
                    totalRxPackets,
                    totalTxPackets
            );
            try {
                this.writeToFile(outputData,descriptor);
            }
            catch (IOException ioe)
            {
                ioe.printStackTrace();
            }
            Log.d("loop",outputData);
        }
    }

    public void writeToFile(String data,FileWriter descriptor) throws IOException
    {
        descriptor.append(data);
        descriptor.flush();
        return;
    }

    public void exit()
    {
        try
        {
            this.descriptor.close();
        }
        catch (IOException ioe)
        {
            ioe.printStackTrace();
        }

        this.keepRunning = false;
    }
}
