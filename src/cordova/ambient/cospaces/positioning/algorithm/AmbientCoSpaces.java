package ambient.cospaces.positioning.algorithm;

import android.app.ActivityManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import org.apache.cordova.*;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.content.Intent;
import org.json.JSONObject;

/**
 * This class echoes a string called from JavaScript.
 */
public class AmbientCoSpaces extends CordovaPlugin {

    private Intent in;
    private Activity context;
    protected static final String TAG = "com.htwg.ambientcospaces";

    public AmbientCoSpaces() {
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        this.context = cordova.getActivity();
        this.in = new Intent(context, BackgroundService.class);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this.context);
        SharedPreferences.Editor editor = sharedPref.edit();

        if (action.equals("startForegroundPositioning")) {
            JSONObject user = args.getJSONObject(0);
            editor.putString("user",user.toString());
            editor.putBoolean("background", false);
            editor.commit();
            this.start(callbackContext);
        } else if (action.equals("startBackgroundPositioning")) {
            JSONObject user = args.getJSONObject(0);
            editor.putString("user",user.toString());
            editor.putBoolean("background", true);
            editor.commit();
            this.start(callbackContext);
        } else if (action.equals("stopPositioning")) {
            this.stopPositioning(callbackContext);
        } else {
            return false;
        }
        return true;
    }

    private void start(CallbackContext callbackContext) {
        this.stopPositioning(callbackContext);
        if(!isMyServiceRunning()){
            this.context.startService(in);
        }
    }

    private void stopPositioning(CallbackContext callbackContext) {
        if(isMyServiceRunning()){
            this.context.stopService(in);
        }
    }

    private boolean isMyServiceRunning() {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (BackgroundService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

}
