package cordova.ambient.cospaces.positioning.algoritm;


import android.content.Context;

import org.apache.cordova.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.BleNotAvailableException;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.widget.Toast;
import android.os.RemoteException;

import java.util.Timer;
import java.util.TimerTask;
import java.util.Collection;

public class AlgorithmService extends Service {

    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;
    private BackgroundThread backgroundThread;
    private HandlerThread thread;


    public AlgorithmService() {
    }

    @Override
    public void onCreate() {
        // To avoid cpu-blocking, we create a background handler to run our service
        thread = new HandlerThread("BackgroundService",
                Process.THREAD_PRIORITY_BACKGROUND);
        // start the new handler thread
        thread.start();

        mServiceLooper = thread.getLooper();
        // start the service using the background handler
        mServiceHandler = new ServiceHandler(mServiceLooper);

        backgroundThread = new BackgroundThread(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //TODO do something useful
        Toast.makeText(getApplicationContext(), "service start", Toast.LENGTH_SHORT).show();

        // call a new service handler. The service ID can be used to identify the service
        Message message = mServiceHandler.obtainMessage();
        message.arg1 = startId;
        mServiceHandler.sendMessage(message);

        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        Toast.makeText(getApplicationContext(), "service done", Toast.LENGTH_SHORT).show();
        backgroundThread.cancel();
        thread.quit();
    }


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        //throw new UnsupportedOperationException("Not yet implemented");
        return null;
    }

    // Object responsible for
    private final class ServiceHandler extends Handler {

        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            backgroundThread.start();
        }
    }

    public class BackgroundThread extends Thread implements BeaconConsumer {

        protected static final String TAG = "com.htwg.ambientcospaces";
        private BeaconManager beaconManager = BeaconManager.getInstanceForApplication(getApplicationContext());
        private BluetoothAdapter bluetoothAdapter;

        volatile boolean running = true;
        private Timer timer;
        private Context context;
        private final Handler toastHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Toast.makeText(getApplicationContext(), "test", Toast.LENGTH_SHORT).show();
            }
        };

        public BackgroundThread(Context context){
            this.context = context;
        }


        @Override
        public void run() {
            try {
                bluetoothAdapter.enable();
                beaconManager.bind(this);
            } catch (Exception e){
                Log.e(TAG, "Thread run error : " + e.getCause());
            }


            timer = new Timer();
            TimerTask myTask = new TimerTask() {
                @Override
                public void run() {
                    toastHandler.sendEmptyMessage(0);
                }
            };

            timer.schedule(myTask, 2000, 2000);
            while (!running){
                beaconManager.unbind(this);
                return;
            }
        }

        @Override
        public void onBeaconServiceConnect() {
            beaconManager.setRangeNotifier(new RangeNotifier() {
                @Override
                public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                    if (beacons.size() > 0) {
                        Log.i(TAG, "The first beacon I see is about "+beacons.iterator().next().getDistance()+" meters away.");
                    }
                }
            });

            try {
                beaconManager.startRangingBeaconsInRegion(new Region("myRangingUniqueId", null, null, null));
            } catch (RemoteException e) {    }
        }

        public void cancel() {
            timer.cancel();
            running = false;
        }
        //////// IBeaconConsumer implementation /////////////////////

        @Override
        public Context getApplicationContext() {
            return this.context;
        }

        @Override
        public void unbindService(ServiceConnection connection) {
            Log.i(TAG,"Unbind from IBeacon service");
            this.getApplicationContext().unbindService(connection);
        }

        @Override
        public boolean bindService(Intent intent, ServiceConnection connection, int mode) {
            Log.i(TAG,"Bind to IBeacon service");
            return this.getApplicationContext().bindService(intent, connection, mode);
        }
    }
}


