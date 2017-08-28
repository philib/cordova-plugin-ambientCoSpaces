package ambient.cospaces.positioning.algorithm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Restarts BackgroundService after phone completed boooting process
 */
public class ServiceRestarter extends BroadcastReceiver {
    protected static final String TAG = "com.htwg.ambientcospaces";
    private boolean background;

    public ServiceRestarter() {

    }

    @Override
    public void onReceive(Context context, Intent intent) {

        Intent in = new Intent(context, BackgroundService.class);
        String action = intent.getAction();
        Log.i(TAG, "BroadcastReciever recieved action = " + action);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        this.background = sharedPref.getBoolean("background", false);

        if(this.background){
            context.startService(in);
            Log.i(TAG, "Restart Background Service After Boot");
        }
    }
}
