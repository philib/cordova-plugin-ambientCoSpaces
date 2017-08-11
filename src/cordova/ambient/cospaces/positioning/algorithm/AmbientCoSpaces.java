package ambient.cospaces.positioning.algorithm;

import org.apache.cordova.*;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.content.Intent;

/**
 * This class echoes a string called from JavaScript.
 */
public class AmbientCoSpaces extends CordovaPlugin {

    private Intent in;
    private Activity context;

    public AmbientCoSpaces() {
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        this.context = cordova.getActivity();
        this.in = new Intent(context, BackgroundService.class);
        if (action.equals("startPositioning")) {
            this.startPositioning(callbackContext);
        } else if (action.equals("stopPositioning")) {
            this.stopPositioning(callbackContext);
        } else {
            return false;
        }
        return true;
    }

    private void coolMethod(String message, CallbackContext callbackContext) {
        if (message != null && message.length() > 0) {
            callbackContext.success(message);
        } else {
            callbackContext.error("Expected one non-empty string argument.");
        }
    }

    private void startPositioning(CallbackContext callbackContext) {
        this.context.stopService(in);
        this.context.startService(in);
    }

    private void stopPositioning(CallbackContext callbackContext) {
        this.context.stopService(in);
    }

}
