package ambient.cospaces.positioning.algorithm;


import android.content.Intent;
import android.content.SharedPreferences;
import android.app.Service;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Process;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

public class BackgroundService extends Service {

    private Looper mServiceLooper;
    private HandlerThread thread;
    private BeaconHandler beaconHandler;
    private boolean background;
    protected static final String TAG = "com.htwg.ambientcospaces";
    private Timer timer;

    @Override
    public void onCreate() {
        // To avoid cpu-blocking, we create a background handler to run our service
        thread = new HandlerThread("BackgroundService",
                Process.THREAD_PRIORITY_BACKGROUND);
        // start the new handler thread
        thread.start();

        mServiceLooper = thread.getLooper();
        // start the service using the background handler
        this.beaconHandler = new BeaconHandler(this.getApplicationContext());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        this.background = sharedPref.getBoolean("background", false);

        this.timer = new Timer();
        if (this.background) {
            Toast.makeText(getApplicationContext(), "Background started", Toast.LENGTH_SHORT).show();
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
        return null;
    }
}


