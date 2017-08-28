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

    /**
     * Is executed each time the cordova app triggers an plugin action. Depending on the action the corresponding function will be triggered
     * @param action the method wich was called by the app
     * @param args passed parameters
     * @param callbackContext callback possiblity for app
     * @return
     * @throws JSONException
     */

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        this.context = cordova.getActivity();
        this.in = new Intent(context, BackgroundService.class);

        if (action.equals("startForegroundPositioning")) {
            JSONObject user = args.getJSONObject(0);
            this.setSharedPrefs(user,false);
            this.start(callbackContext);
        } else if (action.equals("startBackgroundPositioning")) {
            JSONObject user = args.getJSONObject(0);
            this.setSharedPrefs(user,true);
            this.start(callbackContext);
        } else if (action.equals("stopPositioning")) {
            this.stopPositioning(callbackContext);
        } else {
            return false;
        }
        return true;
    }

    /**
     * Starts the backgroudn service
     * @param callbackContext
     */
    private void start(CallbackContext callbackContext) {
        this.stopPositioning(callbackContext);
        if(!isMyServiceRunning()){
            this.context.startService(in);
        }
    }

    /**
     * Stops the background service if running
     * @param callbackContext
     */
    private void stopPositioning(CallbackContext callbackContext) {
        if(isMyServiceRunning()){
            this.context.stopService(in);
        }
    }

    /**
     * Checks whether the BackgroundService is running or not
     * @return True if already running, false if not
     */
    private boolean isMyServiceRunning() {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (BackgroundService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Saves user and backgroudn mode as shared preferences.
     * @param user user json object
     * @param background boolean whether service should be running in background or foreground mode
     */
    private void setSharedPrefs(JSONObject user, boolean background){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this.context);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("user",user.toString());
        editor.putBoolean("background", background);
        editor.commit();
    }

}
