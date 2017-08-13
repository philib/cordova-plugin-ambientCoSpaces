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

public class BackgroundService extends Service {

    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;
    private BackgroundThread backgroundThread;
    private HandlerThread thread;
    private BeaconHandler beaconHandler;
    private boolean background;


    public BackgroundService() {
        this.beaconHandler = new BeaconHandler(this);
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
        // call a new service handler. The service ID can be used to identify the service
        Message message = mServiceHandler.obtainMessage();
        message.arg1 = startId;
        this.background = intent.getBooleanExtra("background", false);
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
        }else {
            Toast.makeText(getApplicationContext(), "Foreground stopped", Toast.LENGTH_SHORT).show();
            this.beaconHandler.stopScan();
        }
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

        public BackgroundThread(Context context) {
            beaconHandler = new BeaconHandler(context);
        }

        @Override
        public void run() {
            this.beaconHandler.startScan();
        }

        public void cancel() {
            this.beaconHandler.stopScan();
            this.interrupt();
        }

    }
}


