package com.example.g29.msbandapp;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.microsoft.band.BandClient;
import com.microsoft.band.BandClientManager;
import com.microsoft.band.BandException;
import com.microsoft.band.BandInfo;
import com.microsoft.band.BandIOException;
import com.microsoft.band.ConnectionState;
import com.microsoft.band.InvalidBandVersionException;
import com.microsoft.band.UserConsent;
import com.microsoft.band.sensors.BandContactEvent;
import com.microsoft.band.sensors.BandContactEventListener;
import com.microsoft.band.sensors.BandContactState;
import com.microsoft.band.sensors.BandHeartRateEvent;
import com.microsoft.band.sensors.BandHeartRateEventListener;
import com.microsoft.band.sensors.GsrSampleRate;
import com.microsoft.band.sensors.HeartRateConsentListener;
import com.microsoft.band.sensors.BandRRIntervalEvent;
import com.microsoft.band.sensors.BandRRIntervalEventListener;
import com.microsoft.band.sensors.BandGsrEvent;
import com.microsoft.band.sensors.BandGsrEventListener;
import com.microsoft.band.sensors.BandSkinTemperatureEvent;
import com.microsoft.band.sensors.BandSkinTemperatureEventListener;
import com.microsoft.band.sensors.BandDistanceEvent;
import com.microsoft.band.sensors.BandDistanceEventListener;
import com.microsoft.band.sensors.BandAltimeterEvent;
import com.microsoft.band.sensors.BandAltimeterEventListener;
import com.microsoft.band.sensors.BandPedometerEvent;
import com.microsoft.band.sensors.BandPedometerEventListener;
import com.microsoft.band.sensors.BandCaloriesEvent;
import com.microsoft.band.sensors.BandCaloriesEventListener;

import android.os.AsyncTask;

import java.lang.ref.WeakReference;


public class HomeActivity extends Activity {
    private TextView WelcomeMsg, ErrorTxt, DataTxt;
    private Button LogoutBtn, StartBtn, StopBtn, UpdateBtn;
    private FirebaseAuth firebaseAuth;
    private BandClient client = null;
    private boolean hasConsent = false;

    private DataCollectionService mBoundService;
    boolean mIsBound;

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // This is called when the connection with the service has been
            // established, giving us the service object we can use to
            // interact with the service.  Because we have bound to a explicit
            // service that we know is running in our own process, we can
            // cast its IBinder to a concrete class and directly access it.
            mBoundService = ((DataCollectionService.LocalBinder) service).getService();
            System.out.println("onServiceConnected is called");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            // This is called when the connection with the service has been
            // unexpectedly disconnected -- that is, its process crashed.
            // Because it is running in our same process, we should never
            // see this happen.
            mBoundService = null;
        }
    };

    void doBindService(){
        // Establish a connection with the service.  We use an explicit
        // class name because we want a specific service implementation that
        // we know will be running in our own process (and thus won't be
        // supporting component replacement by other applications).
        bindService(new Intent(this, DataCollectionService.class),
                    mConnection, Context.BIND_AUTO_CREATE);
        mIsBound = true;
        System.out.println("doBindService");
    }

    void doUnbindService(){
        if(mIsBound){
            //Detach our existing connection
            unbindService(mConnection);
            mIsBound = false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        setupUIViews();
        welcomeUser();
        final WeakReference<Activity> reference = new WeakReference<Activity>(this);


        LogoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //unbind Service
                doUnbindService();
                //sign user out and return to login screen
                firebaseAuth.signOut();
                Intent logIntent = new Intent(HomeActivity.this, LoginActivity.class);
                startActivity(logIntent);
            }
        });


        StartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //clear Data UI and Error UI on Start
                appendToUI(1, "");
                appendToUI(3, "");


                //ask for sensor consent, if not given
                if(!hasConsent){
                    new HeartRateConsentTask().execute(reference);
                }

                //disable start btn, enable stop and update btn
                StopBtn.setEnabled(true);
                UpdateBtn.setEnabled(true);
                StartBtn.setEnabled(false);

                doBindService();
                updateData();
            }
        });

        StopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopDataCollection();
            }
        });

        UpdateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateData();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        doBindService();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (client != null) {
            stopDataCollection();
        }
    }

    @Override
    protected void onDestroy() {
        if (client != null) {
            try {
                stopDataCollection();
                client.disconnect().await();
            } catch (InterruptedException e) {
                // Do nothing as this is happening during destroy
            } catch (BandException e) {
                // Do nothing as this is happening during destroy
            }
        }
        super.onDestroy();
    }

    private void setupUIViews() {
        WelcomeMsg = findViewById(R.id.welcomeMsg);
        LogoutBtn = findViewById(R.id.signOutBtn);
        firebaseAuth = FirebaseAuth.getInstance();
        ErrorTxt = findViewById(R.id.errorTxt);
        DataTxt = findViewById(R.id.dataTxt);
        StartBtn = findViewById(R.id.startBtn);
        StopBtn = findViewById(R.id.stopBtn);
        UpdateBtn = findViewById(R.id.updateBtn);
    }

    private void welcomeUser() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        appendToUI(2, "Welcome " + user.getDisplayName() + "!");

    }

    private void appendToUI(final int ui, final String message){
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                switch(ui){
                    case 1:
                        //DataTxt
                        DataTxt.setText(message);
                        break;
                    case 2:
                        //WelcomeTxt
                        WelcomeMsg.setText(message);
                        break;
                    default:
                        //ErrorTxt
                        ErrorTxt.setText(message);
                        break;
                }
            }
        });
    }

    private void updateData(){
        if(mIsBound && mBoundService != null){
            ContData contData = mBoundService.getContData();

            //band is being worn, so collect rest of data
            if(contData.contactState == BandContactState.WORN){
                GSRData gsrData = mBoundService.getGsrdata();
                AccData accData = mBoundService.getAccData();
                HRData hrData = mBoundService.getHrdata();
                RRData rrData = mBoundService.getRRData();
                String text = "Skin Resistance = "+gsrData.gsrValue+" kOhms\nHeart Rate = "+hrData.hr+" beats per min.\nRR = "+rrData.rr;
                appendToUI(1, text);
            }else{
                //throw error
                appendToUI(3, "Please ensure Band is being worn.");
            }
        }
    }

    private void stopDataCollection(){
        appendToUI(3, "Data Collection has been STOPPED.");
        appendToUI(1, "");

        //disable stop and update btn, enable start btn
        StopBtn.setEnabled(false);
        StartBtn.setEnabled(true);
        UpdateBtn.setEnabled(true);

        doUnbindService();
    }

    private boolean getConnectedBandClient() throws InterruptedException, BandException {
        if (client == null) {
            BandInfo[] devices = BandClientManager.getInstance().getPairedBands();
            if (devices.length == 0) {
                appendToUI(3, "Band isn't paired with your phone.\n");
                return false;
            }
            client = BandClientManager.getInstance().create(getBaseContext(), devices[0]);
        } else if (ConnectionState.CONNECTED == client.getConnectionState()) {
            return true;
        }

        System.out.println("Band is connecting...\n");
        return ConnectionState.CONNECTED == client.connect().await();
    }

    private class HeartRateConsentTask extends AsyncTask<WeakReference<Activity>, Void, Void> {
        protected Void doInBackground(WeakReference<Activity>... params) {
            try {
                if (getConnectedBandClient()) {
                    if (params[0].get() != null) {
                        client.getSensorManager().requestHeartRateConsent(params[0].get(), new HeartRateConsentListener() {
                            @Override
                            public void userAccepted(boolean consentGiven) {
                                hasConsent = true;
                            }
                        });
                    }
                } else {
                    //band still not connected, even after reconnection attempt. Throw error
                    appendToUI(3, "Band not connected.\n " +
                            "Please ensure Band is connected to your device.");
                }
            } catch (Exception e) {
                appendToUI(3, e.getMessage());
            }

            return null;
        }
    }
}




