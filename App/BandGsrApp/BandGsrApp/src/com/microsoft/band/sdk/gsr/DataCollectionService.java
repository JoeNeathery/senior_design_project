package com.microsoft.band.sdk.gsr;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;

import com.microsoft.band.BandClient;
import com.microsoft.band.BandClientManager;
import com.microsoft.band.BandException;
import com.microsoft.band.BandIOException;
import com.microsoft.band.BandInfo;
import com.microsoft.band.ConnectionState;
import com.microsoft.band.sensors.BandAltimeterEvent;
import com.microsoft.band.sensors.BandAltimeterEventListener;
import com.microsoft.band.sensors.BandAmbientLightEvent;
import com.microsoft.band.sensors.BandAmbientLightEventListener;
import com.microsoft.band.sensors.BandBarometerEvent;
import com.microsoft.band.sensors.BandBarometerEventListener;
import com.microsoft.band.sensors.BandCaloriesEvent;
import com.microsoft.band.sensors.BandCaloriesEventListener;
import com.microsoft.band.sensors.BandContactEvent;
import com.microsoft.band.sensors.BandContactEventListener;
import com.microsoft.band.sensors.BandContactState;
import com.microsoft.band.sensors.BandDistanceEvent;
import com.microsoft.band.sensors.BandDistanceEventListener;
import com.microsoft.band.sensors.BandGsrEvent;
import com.microsoft.band.sensors.BandGsrEventListener;
import com.microsoft.band.sensors.BandGyroscopeEvent;
import com.microsoft.band.sensors.BandGyroscopeEventListener;
import com.microsoft.band.sensors.BandPedometerEvent;
import com.microsoft.band.sensors.BandPedometerEventListener;
import com.microsoft.band.sensors.BandSensorManager;
import com.microsoft.band.sensors.BandAccelerometerEvent;
import com.microsoft.band.sensors.BandAccelerometerEventListener;
import com.microsoft.band.sensors.BandSkinTemperatureEvent;
import com.microsoft.band.sensors.BandSkinTemperatureEventListener;
import com.microsoft.band.sensors.BandUVEvent;
import com.microsoft.band.sensors.BandUVEventListener;
import com.microsoft.band.sensors.GsrSampleRate;
import com.microsoft.band.sensors.MotionType;
import com.microsoft.band.sensors.SampleRate;
import com.microsoft.band.sensors.BandHeartRateEvent;
import com.microsoft.band.sensors.BandHeartRateEventListener;
//import com.microsoft.band.sensors.HeartRateConsentListener;
import com.microsoft.band.sensors.BandRRIntervalEvent;
import com.microsoft.band.sensors.BandRRIntervalEventListener;
import com.microsoft.band.sensors.UVIndexLevel;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.ref.WeakReference;

/**
 * Created by wenbing on 9/10/17.
 */
public class DataCollectionService extends Service {
    //private int gsrValue = 0;
    //private long ts = 0;
    private GSRData gsrdata = new GSRData();
    private AccData accdata = new AccData();
    private HRData hrdata = new HRData();
    private RRData rrdata = new RRData();

    private BandClient client = null;







    //public

    public class LocalBinder extends Binder {
        com.microsoft.band.sdk.gsr.DataCollectionService getService() {
            System.out.println("localBinder getService");

            return com.microsoft.band.sdk.gsr.DataCollectionService.this;
        }
    }

    public GSRData getGSRData() {
        return this.gsrdata;
    }

    public HRData getHRData() {
        return this.hrdata;
    }

    public AccData getAccData() {
        return this.accdata;
    }

    public RRData getRRData() {
        return this.rrdata;
    }

    @Override
    public void onCreate() {
        System.out.println("service onCreate");


        new DataCollectionService.SensorsSubscriptionTask().execute();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        System.out.println("service onStartCommand");
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        if (client != null) {
            try {
                client.getSensorManager().unregisterGsrEventListener(mGsrEventListener);
            } catch (BandIOException e) {
                //appendToUI(e.getMessage());
            }
        }
    }

    private final IBinder mBinder = new LocalBinder();

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }




    private class SensorsSubscriptionTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try {
                if (getConnectedBandClient()) {
                    int hardwareVersion = Integer.parseInt(client.getHardwareVersion().await());
                    if (hardwareVersion >= 20) {
                        //appendToUI("Band is connected.\n");
                        BandSensorManager mgr = client.getSensorManager();
                        mgr.registerGsrEventListener(mGsrEventListener, GsrSampleRate.MS200);
                        mgr.registerAccelerometerEventListener(mAccelerometerEventListener, SampleRate.MS128);
                        mgr.registerHeartRateEventListener(mHeartRateEventListener);
                        mgr.registerRRIntervalEventListener(mRRIntervalEventListener);
                        mgr.registerAltimeterEventListener(mAltimeterEventListener);
                        mgr.registerAmbientLightEventListener(mAmbientLightEventListener);
                        mgr.registerBarometerEventListener(mBarometerEventListener);
                        mgr.registerCaloriesEventListener(mCaloriesEventListener);
                        mgr.registerContactEventListener(mContactEventListener);
                        mgr.registerDistanceEventListener(mDistanceEventListener);
                        mgr.registerGyroscopeEventListener(mGyroscopeEventListener,SampleRate.MS128);
                        mgr.registerPedometerEventListener(mPedometerEventListener);
                        mgr.registerSkinTemperatureEventListener(mSkinTemperatureEventListener);
                        mgr.registerUVEventListener(mUVEventListener);

                    } else {
                        //appendToUI("The Gsr sensor is not supported with your Band version. Microsoft Band 2 is required.\n");
                    }
                } else {
                    System.out.println("Band isn't connected. Please make sure bluetooth is on and the band is in range.\n");
                }
            } catch (BandException e) {


            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
            return null;
        }
    }


}


