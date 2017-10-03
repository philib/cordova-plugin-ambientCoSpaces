package ambient.cospaces.positioning.algorithm;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import org.apache.cordova.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * This class echoes a string called from JavaScript.
 */
public class AmbientCoSpaces extends CordovaPlugin {

    private Activity context;
    protected static final String TAG = "com.htwg.ambientcospaces";
    private Intent backgroundService;
    private Intent notificationService;

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

        boolean startForegroundPositioning = action.equals("startForegroundPositioning");
        boolean startBackgroundPositioning = action.equals("startBackgroundPositioning");
        boolean stopPositioning = action.equals("stopPositioning");
        boolean startNotification = action.equals("startNotificationSubscriptionService");
        boolean stopNotification = action.equals("stopNotificationSubscriptionService");
        boolean startBackground = startBackgroundPositioning || startForegroundPositioning;

        this.backgroundService = new Intent(context, BackgroundService.class);
        this.notificationService = new Intent(context, NotificationService.class);

        if(startBackground){
            JSONObject user = args.getJSONObject(0);
            this.setSharedPrefs(user,startBackgroundPositioning, args.getString(1));
            this.context.stopService(this.backgroundService);
            this.context.startService(this.backgroundService);
        } else if (stopPositioning) {
            this.context.stopService(this.backgroundService);
        } else if (startNotification) {
            Log.i(TAG,"Login");
            JSONObject user = args.getJSONObject(0);
            this.setSharedPrefsNotification(user);
            this.startNotification(callbackContext);
        } else if (stopNotification) {
            Log.i(TAG,"Logout clickt");
            this.stopNotification(callbackContext);
        } else {
            return false;
        }
        return true;
    }

    /**
     * Starts the backgroudn service and Notification Service
     * @param callbackContext
     */
    private void startNotification(CallbackContext callbackContext) {
        if(!isMyNotificationServiceRunning()){
            this.context.startService(this.notificationService);
        }
    }

    /**
     * Stops the background service if running
     * @param callbackContext
     */
    private void stopNotification(CallbackContext callbackContext) {
        if(isMyNotificationServiceRunning()){
            this.context.stopService(this.notificationService);
            //Delete shared pref key account id after loggin out
            SharedPreferences mySPrefs = PreferenceManager.getDefaultSharedPreferences(this.context);
            SharedPreferences.Editor editor = mySPrefs.edit();
            editor.remove("accountId");
            editor.apply();
        }
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
    private void setSharedPrefs(JSONObject user, boolean background, String backendUrl){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this.context);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("user",user.toString());
        editor.putBoolean("background", background);
        editor.putString("backendUrl", backendUrl);
        editor.commit();
    }

    /**
     * Saves user and backgroudn mode as shared preferences.
     * @param user user json object
     */
    private void setSharedPrefsNotification(JSONObject user){
        String accountId = "";
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this.context);
        SharedPreferences.Editor editor = sharedPref.edit();
        try {
            accountId = user.getString("id");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        editor.putString("accountId", accountId);
        editor.commit();
    }

}
