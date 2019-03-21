package com.example.root.trafficmonitor;


import android.net.TrafficStats;
import android.util.Log;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import java.util.concurrent.TimeUnit;


/**
 * Created by Trishita Tiwari on 1/31/18.
 */

public class NetworkMonitorThreadEmulator extends Thread {
    public int uid;
    public final int SERVER_PORT_DATA = 5000;
    public final int SERVER_PORT_CONTROL = 4500;
    public final String SERVER_IP = "128.197.128.238";
    private Socket socket_data,socket_control;
    private PrintWriter dataOutPutStream,controlOutPutStream;
    private DataInputStream controlInPutStream;
    public BufferedReader controlBufferedReader;
    public static String metaData;
    private Long currentTime;
    private String outputData;
    NetworkMonitorThreadEmulator(int uid)
    {
        this.uid = uid;
    }

    @Override
    public void run()
    {
        long mobileRxPackets, mobileRxBytes, mobileTxPackets, mobileTxBytes;
        long oldMobileRxPackets, oldMobileRxBytes, oldMobileTxPackets, oldMobileTxBytes;
        long deltaMobileRxPackets, deltaMobileRxBytes, deltaMobileTxPackets, deltaMobileTxBytes;
        long totalRxBytes, totalRxPackets, totalTxPackets, totalTxBytes;
        long oldTotalRxBytes, oldTotalRxPackets, oldTotalTxPackets, oldTotalTxBytes;
        long deltaTotalRxBytes, deltaTotalRxPackets, deltaTotalTxPackets, deltaTotalTxBytes;

        try {
            socket_data = new Socket(SERVER_IP, SERVER_PORT_DATA);
            socket_control = new Socket(SERVER_IP,SERVER_PORT_CONTROL);
            Log.d("Thread.run()","created socket connection");
            dataOutPutStream = new PrintWriter(socket_data.getOutputStream(), true);
            controlOutPutStream = new PrintWriter(socket_control.getOutputStream(), true);
            controlInPutStream = new DataInputStream(socket_control.getInputStream());
            controlBufferedReader = new BufferedReader(new InputStreamReader(controlInPutStream));
            Log.d("Thread.run()","created Streams");
            getMetaData metaDataReader = new getMetaData();
            metaDataReader.start();
            Log.d("Thread.run()","executed getMetaData");

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

        while(true)
        {
            try {TimeUnit.SECONDS.sleep(1);}
            catch (Exception e){
                e.printStackTrace();
                return;
            }
            currentTime = System.currentTimeMillis()/1000;;
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

            outputData = String.format("%s %s %d %d %d %d %d %d %d %d %d %d %d %d %d %d %d %d\n",
                        metaData,
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
            dataOutPutStream.printf(outputData);
            Log.d("loop",outputData);
        }
    }
    class getMetaData extends Thread {

        @Override
        public void run(){
            try
            {
                Log.d("metaData","running metadata thread");
                while ((metaData = controlBufferedReader.readLine()) != null)
                {
                    Log.d("metaData", "recieved metaData");
                    controlOutPutStream.printf("OK");
                    Log.d("metaData", "sent OK");
                }
            } catch (Exception e)
            {
                    e.printStackTrace();
            }

        }
    }
}