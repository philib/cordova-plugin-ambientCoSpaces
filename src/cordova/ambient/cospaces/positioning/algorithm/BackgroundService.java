package ambient.cospaces.positioning.algorithm;


import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.app.Service;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
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
        //TODO do something useful
        // call a new service handler. The service ID can be used to identify the service
        Message message = mServiceHandler.obtainMessage();
        message.arg1 = startId;
        if(intent != null){
            this.background = intent.getBooleanExtra("background", false);
        }else {
            this.background = false;
        }

        if(this.background){
            Toast.makeText(getApplicationContext(), "Background started", Toast.LENGTH_SHORT).show();
            mServiceHandler.sendMessage(message);
        }else {
            Toast.makeText(getApplicationContext(), "Foreground started", Toast.LENGTH_SHORT).show();
            this.beaconHandler.startScan();
        }

        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        if(this.background){
            Toast.makeText(getApplicationContext(), "Background stopped", Toast.LENGTH_SHORT).show();
            backgroundThread.cancel();
            thread.quit();
        }else {
            Toast.makeText(getApplicationContext(), "Foreground stopped", Toast.LENGTH_SHORT).show();
            this.beaconHandler.stopScan();
        }
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

    public class BackgroundThread extends Thread{
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


