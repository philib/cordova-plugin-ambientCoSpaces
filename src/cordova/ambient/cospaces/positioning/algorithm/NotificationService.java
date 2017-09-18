package ambient.cospaces.positioning.algorithm;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Process;
import android.preference.PreferenceManager;
import android.util.Log;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.json.JSONObject;

public class NotificationService extends Service {

    private Looper mServiceLooper;
    private HandlerThread thread;
    private String accountId;
    protected static final String TAG = "com.htwg.ambientcospaces.notification";
    private MqttAsyncClient client;

    @Override
    public void onCreate() {
        // To avoid cpu-blocking, we create a background handler to run our service
        thread = new HandlerThread("NotificationService",
                Process.THREAD_PRIORITY_BACKGROUND);
        // start the new handler thread
        thread.start();

        mServiceLooper = thread.getLooper();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        // Retrieve background mode and user information from shared preferences
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        try {
            Log.i(TAG, "Notification Service started!");
            this.accountId = sharedPref.getString("accountId", null);
            Log.i(TAG, "AccountID: " + this.accountId);
            //JSONObject user = new JSONObject(userString);
            if(this.accountId != null){
                //Subribe nur wenn User eingeloggt ist
                this.connect();
            }
        } catch (Exception e) {
            Log.i(TAG, "Error: " + e.getMessage());
            e.printStackTrace();
        }
        return Service.START_STICKY;
    }

    @Override
    public void onTaskRemoved(Intent intent) {
        Log.i(TAG, "onTaskRemoved!");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.unsubscribe();
        Log.i(TAG, "ondestroy!");
        thread.quit();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    private void unsubscribe() {
//        try {
//            Log.i(TAG, "Unsubscribe and disconnect");
//            this.client.unsubscribe(this.accountId);
//            this.client.disconnect();
//        } catch (MqttException e) {
//            e.printStackTrace();
//        }
    }

    private void connect() {
        MemoryPersistence persistence = new MemoryPersistence();
        try {
            client = new MqttAsyncClient("tcp://acs.in.htwg-konstanz.de:1883", accountId, persistence);
        } catch (MqttException e) {
            e.printStackTrace();
        }

        final MqttConnectOptions connOpts = new MqttConnectOptions();
        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                Log.i(TAG, "Connection Lost " + cause.toString());
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                Log.i(TAG, "topic is " + topic + ". payload is " + message.toString());
                JSONObject messageJSON = new JSONObject(message.toString());
                Notification n = new Notification.Builder(getApplicationContext())
                        .setContentTitle(messageJSON.getString("title"))
                        .setContentText(messageJSON.getString("message"))
                        .setSmallIcon(android.R.drawable.ic_notification_overlay).build();
                NotificationManager notificationManager =
                        (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                notificationManager.notify(message.getId(), n);
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                Log.i(TAG, "Delivery complete ");
            }
        });
        try {
            client.connect(connOpts, null, new IMqttActionListener() {
                public void onSuccess(IMqttToken iMqttToken) {
                    Log.i(TAG, "Connected");
                    try {
                        Log.i(TAG, "Subscribe");
                        client.subscribe(accountId, 2, null, new IMqttActionListener() {
                            public void onSuccess(IMqttToken iMqttToken) {
                                Log.i(TAG, "Subscribe successfull");
                            }

                            public void onFailure(IMqttToken iMqttToken, Throwable throwable) {
                                Log.i(TAG, "Subscribe failed");
                            }
                        });
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                }

                public void onFailure(IMqttToken iMqttToken, Throwable throwable) {
                    Log.i(TAG, "Connect failed");
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}


