package cordova.ambient.cospaces.positioning.algoritm;


import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.app.Service;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.util.Log;
import android.widget.Toast;
import android.os.RemoteException;

import java.util.Timer;
import java.util.TimerTask;
import java.util.Collection;

public class BackgroundService extends Service {

    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;
    private BackgroundThread backgroundThread;
    private HandlerThread thread;


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

    public class BackgroundThread extends Thread{

        private BeaconHandler beaconHandler;
        volatile boolean running = true;
        private Timer timer;
        private final Handler toastHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Toast.makeText(getApplicationContext(), "test", Toast.LENGTH_SHORT).show();
            }
        };

        public BackgroundThread(Context context) {
            beaconHandler = new BeaconHandler(context);
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

            timer.schedule(myTask, 2000, 2000);
            while (!running) {
                return;
            }
        }

        public void cancel() {
            this.beaconHandler.stopScan();
            timer.cancel();
            running = false;
        }

    }
}


