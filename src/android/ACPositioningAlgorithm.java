package cordova.ambient.cospaces.positioning.algoritm;

import org.apache.cordova.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

/**
 * This class echoes a string called from JavaScript.
 */
public class ACPositioningAlgorithm extends CordovaPlugin {

    private AlgorithmService algorithmService;
    private Intent in;
    private Activity context;

    public ACPositioningAlgorithm() {
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        this.context = cordova.getActivity();
        this.in = new Intent(context, AlgorithmService.class);
        if (action.equals("coolMethod")) {
            String message = args.getString(0);
            this.coolMethod(message, callbackContext);
        } else if (action.equals("startPositioning")) {
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
