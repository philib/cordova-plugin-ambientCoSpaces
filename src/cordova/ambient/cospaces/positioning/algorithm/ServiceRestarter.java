package ambient.cospaces.positioning.algorithm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.List;

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

        if(action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)){
            Log.i(TAG, "Network State Changed");
        }else if(action.equals(ConnectivityManager.CONNECTIVITY_ACTION)){
            Log.i(TAG, "Connectivity Changed");
        }else if(action.equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)){
            WifiManager wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
            List<ScanResult> scanResults = wifiManager.getScanResults();
            Log.i(TAG, "ScanResults Available : "+scanResults.toString());
            for(int i = 0; i < scanResults.size(); i++){
                Log.i(TAG, "ScanResults SSID : "+scanResults.get(i).SSID);
                if(scanResults.get(0).SSID.equals("eduroam")){
                    context.startService(in);
                    Log.i(TAG, "Restart Background Service After Eduroamfound");
                }
            }
        }else if(action.equals(Intent.ACTION_BOOT_COMPLETED)){
            if(this.background){
                context.startService(in);
                Log.i(TAG, "Restart Background Service After Boot");
            }

            in = new Intent(context, NotificationService.class);
            action = intent.getAction();
            Log.i(TAG, "BroadcastReciever recieved action = " + action);
            context.startService(in);
            Log.i(TAG, "Restart Notification Service After Boot");
        }
    }
}
