package com.example.g29.msbandapp;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;

import com.microsoft.band.BandClient;
import com.microsoft.band.BandClientManager;
import com.microsoft.band.BandException;
import com.microsoft.band.BandIOException;
import com.microsoft.band.BandInfo;
import com.microsoft.band.ConnectionState;
import com.microsoft.band.InvalidBandVersionException;
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
 * Created by wenbing 9/10/17
 */
public class DataCollectionService extends Service {
    private GSRData gsrdata = new GSRData();
    private AccData accdata = new AccData();
    private HRData hrdata = new HRData();
    private RRData rrdata = new RRData();
    private ContData contdata = new ContData();

    private BandClient client = null;

    private BandAltimeterEventListener mAltimeterEventListener = new BandAltimeterEventListener() {
        @Override
        public void onBandAltimeterChanged(BandAltimeterEvent event) {
            if (event != null) {
                long ts = event.getTimestamp();
                long flightsAscended = event.getFlightsAscended();
                long flightsDescended = event.getFlightsDescended();
                float rate = event.getRate();
                long steppingGain = event.getSteppingGain();
                long steppingLoss = event.getSteppingLoss();
                long stepsAscended = event.getStepsAscended();
                long stepsDescended = event.getStepsDescended();
                long totalGain = event.getTotalGain();
                long totalLoss = event.getTotalLoss();

                String data = ts + "," + flightsAscended + "," + flightsDescended + "," + rate + "," + steppingGain + "," + steppingLoss + "," +
                        stepsAscended + "," + stepsDescended + "," + totalGain + "," + totalLoss;
                appendDataToLog("altimeterLog",data);
            }
        }
    };


    private BandAmbientLightEventListener mAmbientLightEventListener = new BandAmbientLightEventListener() {
        @Override
        public void onBandAmbientLightChanged(BandAmbientLightEvent event) {
            if (event != null) {
                long ts = event.getTimestamp();
                int brightness = event.getBrightness();
                String data = ts + "," + brightness;
                appendDataToLog("lightLog",data);

            }
        }
    };

    private BandBarometerEventListener mBarometerEventListener = new BandBarometerEventListener() {
        @Override
        public void onBandBarometerChanged(BandBarometerEvent event) {
            if (event != null) {
                long ts = event.getTimestamp();
                double airPressure = event.getAirPressure();
                double temperature = event.getTemperature();
                String data = ts + "," + airPressure + "," + temperature;
                appendDataToLog("barometerLog",data);

            }
        }
    };

    private BandCaloriesEventListener mCaloriesEventListener = new BandCaloriesEventListener() {
        @Override
        public void onBandCaloriesChanged(BandCaloriesEvent event) {
            if (event != null) {
                long ts = event.getTimestamp();
                long calories = event.getCalories();
                String data = ts + "," + calories;
                appendDataToLog("caloriesLog",data);
            }
        }
    };

    private BandContactEventListener mContactEventListener = new BandContactEventListener() {
        @Override
        public void onBandContactChanged(BandContactEvent event) {
            if (event != null) {
                long ts = event.getTimestamp();
                BandContactState calories = event.getContactState();
                contdata.ts = ts;
                contdata.contactState = calories;
                String data = ts + "," + calories;
                appendDataToLog("contactLog",data);
            }
        }
    };

    private BandDistanceEventListener mDistanceEventListener = new BandDistanceEventListener() {
        @Override
        public void onBandDistanceChanged(BandDistanceEvent event) {
            if (event != null) {
                long ts = event.getTimestamp();
                MotionType t = event.getMotionType();
                float pace = event.getPace();
                float speed = event.getSpeed();
                long totalDistance = event.getTotalDistance();
                String data = ts + "," + t + "," + pace + "," + speed + "," + totalDistance;
                appendDataToLog("distanceLog",data);
            }
        }
    };

    private BandGsrEventListener mGsrEventListener = new BandGsrEventListener() {
        @Override
        public void onBandGsrChanged(final BandGsrEvent event) {
            if (event != null) {
                gsrdata.gsrValue = event.getResistance();
                gsrdata.ts = event.getTimestamp();
                appendDataToLog("gsrLog", + gsrdata.ts + "," + gsrdata.gsrValue);

            }
        }
    };

    private BandAccelerometerEventListener mAccelerometerEventListener = new BandAccelerometerEventListener() {
        @Override
        public void onBandAccelerometerChanged(final BandAccelerometerEvent event) {
            if (event != null) {
                accdata.x = event.getAccelerationX();
                accdata.y = event.getAccelerationY();
                accdata.z = event.getAccelerationZ();
                accdata.ts = event.getTimestamp();

                String accentry = String.format("%.5f,%.5f,%.5f", accdata.x, accdata.y, accdata.z);
                appendDataToLog("accLog",accdata.ts + "," + accentry);
            }
        }
    };

    private BandGyroscopeEventListener mGyroscopeEventListener = new BandGyroscopeEventListener() {
        @Override
        public void onBandGyroscopeChanged(BandGyroscopeEvent event) {
            if (event != null) {
                long ts = event.getTimestamp();
                double avx = event.getAngularVelocityX();
                double avy = event.getAngularVelocityY();
                double avz = event.getAngularVelocityZ();
                String gyrodata = String.format("%.5f,%.5f,%.5f", avx, avy, avz);

                String data = ts + "," + gyrodata;
                appendDataToLog("gyroLog",data);
            }
        }
    };

    private BandHeartRateEventListener mHeartRateEventListener = new BandHeartRateEventListener() {
        @Override
        public void onBandHeartRateChanged(final BandHeartRateEvent event) {
            if (event != null) {
                hrdata.ts = event.getTimestamp();
                hrdata.hr = event.getHeartRate();
                hrdata.quality = event.getQuality().toString();

                String hrentry = hrdata.ts + "," + hrdata.hr + "," + hrdata.quality;
                System.out.println("heart rate update: " + hrentry);
                appendDataToLog("hrLog", hrentry);
            }
        }
    };

    private BandPedometerEventListener mPedometerEventListener = new BandPedometerEventListener() {
        @Override
        public void onBandPedometerChanged(BandPedometerEvent event) {
            if (event != null) {
                long ts = event.getTimestamp();
                long totalSteps = event.getTotalSteps();

                String data = ts + "," + totalSteps;
                appendDataToLog("pedometerLog", data);
            }
        }
    };

    private BandRRIntervalEventListener mRRIntervalEventListener = new BandRRIntervalEventListener() {
        @Override
        public void onBandRRIntervalChanged(final BandRRIntervalEvent event) {
            if (event != null) {
                rrdata.ts = event.getTimestamp();
                rrdata.rr = event.getInterval();

                String rrentry = rrdata.ts + "," + rrdata.rr;
                System.out.println("RR update: " + rrentry);
                appendDataToLog("rrLog", rrentry);
            }
        }
    };

    private BandSkinTemperatureEventListener mSkinTemperatureEventListener = new BandSkinTemperatureEventListener() {
        @Override
        public void onBandSkinTemperatureChanged(BandSkinTemperatureEvent event) {
            if (event != null) {
                long ts = event.getTimestamp();
                double temperature = event.getTemperature();
                String data = ts + "," + temperature;
                appendDataToLog("skintempLog", data);
            }
        }
    };

    private BandUVEventListener mUVEventListener = new BandUVEventListener() {
        @Override
        public void onBandUVChanged(BandUVEvent event) {
            if (event != null) {
                long ts = event.getTimestamp();
                UVIndexLevel indexLevel = event.getUVIndexLevel();
                String data = ts + "," + indexLevel;
                appendDataToLog("uvLog", data);
            }
        }
    };

    public class LocalBinder extends Binder{
        com.example.g29.msbandapp.DataCollectionService getService(){
            System.out.println("localBinder getService");

            return com.example.g29.msbandapp.DataCollectionService.this;
        }
    }

    public GSRData getGsrdata() {
        return this.gsrdata;
    }

    public HRData getHrdata() {
        return this.hrdata;
    }

    public AccData getAccData() {
        return this.accdata;
    }

    public RRData getRRData() {
        return this.rrdata;
    }

    public ContData getContData(){ return this.contdata; }

    @Override
    public void onCreate(){
        System.out.println("service onCreate");

        new DataCollectionService.SensorsSubscriptionTask().execute();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        System.out.println("service onStartCommand");
        return START_STICKY;
    }

    @Override
    public void onDestroy(){
    }

    private final IBinder mBinder = new LocalBinder();

    @Override
    public IBinder onBind(Intent intent) { return mBinder; }

    private boolean getConnectedBandClient() throws InterruptedException, BandException {
        if (client == null) {
            BandInfo[] devices = BandClientManager.getInstance().getPairedBands();
            if (devices.length == 0) {
                //appendToUI("Band isn't paired with your phone.\n");
                return false;
            }
            client = BandClientManager.getInstance().create(getBaseContext(), devices[0]);
        } else if (ConnectionState.CONNECTED == client.getConnectionState()) {
            return true;
        }

        System.out.println("Band is connecting...\n");
        return ConnectionState.CONNECTED == client.connect().await();
    }

    private class SensorsSubscriptionTask extends AsyncTask<Void, Void, Void>{
        @Override
        protected Void doInBackground(Void... params){
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
            }catch (BandException e){

            }catch (InterruptedException e){

            }catch (InvalidBandVersionException e){

            }
            return null;
        }
    }

    private void appendDataToLog(String filename, String text) {
        File dir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "msbanddata");
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                System.out.println("cannot create directory");
            }
        }

        String filePath = dir.getPath().toString() + "/" + filename + ".csv";
        //String filePath = getFilesDir().getPath().toString() + "/log.csv";
        // String filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)+"/log.csv";
        // System.out.println("filepath: "+filePath);


        File logFile = new File(filePath);
        if (!logFile.exists()) {
            try {
                logFile.createNewFile();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        try {
            //BufferedWriter for performance, true to set append to file flag
            BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
            buf.append(text);
            buf.newLine();
            buf.flush();
            buf.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
