package ambient.cospaces.positioning.algorithm;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import org.apache.cordova.*;
import org.json.JSONArray;
import org.json.JSONException;
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
        } else if (action.equals("startNotificationSubscribtionService")) {
            JSONObject user = args.getJSONObject(0);
            this.setSharedPrefsNotification(user);
            this.start(callbackContext);
        } else if (action.equals("stopNotificationSubscribtionService")) {
            JSONObject user = args.getJSONObject(0);
            this.setSharedPrefsNotification(user);
            this.start(callbackContext);
        } else {
            return false;
        }
        return true;
    }

    /**
     * Starts the backgroudn service and Notification Service
     * @param callbackContext
     */
    private void start(CallbackContext callbackContext) {
        this.stopPositioning(callbackContext);
        if(!isMyServiceRunning()){
            this.context.startService(in);
        }
        this.stopNotification(callbackContext);
        if(!isMyNotificationServiceRunning()){
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
     * Stops the background service if running
     * @param callbackContext
     */
    private void stopNotification(CallbackContext callbackContext) {
        if(isMyNotificationServiceRunning()){
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
     * Checks whether the BackgroundService is running or not
     * @return True if already running, false if not
     */
    private boolean isMyNotificationServiceRunning() {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (NotificationService.class.getName().equals(service.service.getClassName())) {
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

    /**
     * Saves user and backgroudn mode as shared preferences.
     * @param user user json object
     * @param background boolean whether service should be running in background or foreground mode
     */
    private void setSharedPrefsNotification(JSONObject user){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this.context);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("user",user.toString());
        editor.commit();
    }

}
