package ambient.cospaces.positioning.algorithm;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.app.Service;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

public class BackgroundService extends Service {

    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;
    private BackgroundThread backgroundThread;
    private HandlerThread thread;
    private BeaconHandler beaconHandler;
    private boolean background;
    protected static final String TAG = "com.htwg.ambientcospaces";
    private Timer timer;


    public BackgroundService() {
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

        this.beaconHandler = new BeaconHandler(this.getApplicationContext());

        backgroundThread = new BackgroundThread(this.beaconHandler);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        ServiceRestarter serviceRestarter = new ServiceRestarter();
        //TODO do something useful
        // call a new service handler. The service ID can be used to identify the service
        Message message = mServiceHandler.obtainMessage();
        message.arg1 = startId;


        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        this.background = sharedPref.getBoolean("background", false);

        this.timer = new Timer();
        if (this.background) {
            Toast.makeText(getApplicationContext(), "Background started", Toast.LENGTH_SHORT).show();
            //mServiceHandler.sendMessage(message);
            //serviceRestarter.setAlarm(getApplicationContext());
            this.beaconHandler.startScan();
            TimerTask myTask = new TimerTask() {
                @Override
                public void run() {
                    Log.i(TAG, "Background Service is running");
                }
            };
            timer.schedule(myTask, 3000, 3000);
            return Service.START_STICKY;
        } else {
            Toast.makeText(getApplicationContext(), "Foreground started", Toast.LENGTH_SHORT).show();
            TimerTask myTask = new TimerTask() {
                @Override
                public void run() {
                    Log.i(TAG, "Foreground Service is running");
                }
            };
            timer.schedule(myTask, 3000, 3000);
            this.beaconHandler.startScan();
            return Service.START_NOT_STICKY;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.timer.cancel();
        if (this.background) {
            Toast.makeText(getApplicationContext(), "Background stopped", Toast.LENGTH_SHORT).show();
            //backgroundThread.cancel();
            this.beaconHandler.stopScan();
        } else {
            Toast.makeText(getApplicationContext(), "Foreground stopped", Toast.LENGTH_SHORT).show();
            this.beaconHandler.stopScan();
        }
        Log.i(TAG, "ondestroy!");
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

    public class BackgroundThread extends Thread {
        private boolean running = true;
        private BeaconHandler beaconHandler;
        private Timer timer;
        private final Handler toastHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Toast.makeText(getApplicationContext(), "Backgroundservice running", Toast.LENGTH_SHORT).show();
            }
        };

        public BackgroundThread(BeaconHandler beaconHandler) {
            this.beaconHandler = beaconHandler;
        }

        @Override
        public void run() {
            this.beaconHandler.startScan();
            timer = new Timer();
            TimerTask myTask = new TimerTask() {
                @Override
                public void run() {
                    toastHandler.sendEmptyMessage(0);
                }
            };

            timer.schedule(myTask, 3000, 3000);
            while (!running) {
                return;
            }
        }

        public void cancel() {
            timer.cancel();
            this.beaconHandler.stopScan();
            running = false;
        }

    }
}


