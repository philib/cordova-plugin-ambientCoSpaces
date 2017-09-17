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
import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.json.JSONObject;

public class NotificationService extends Service {

    private Looper mServiceLooper;
    private HandlerThread thread;
    private String accountId;
    protected static final String TAG = "com.htwg.ambientcospaces";
    private MqttAndroidClient client;

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
            this.accountId = sharedPref.getString("accountId", null);
            //JSONObject user = new JSONObject(userString);
            this.subscribe();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Service.START_NOT_STICKY;
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

    private void subscribe(){
        this.client = new MqttAndroidClient(this.getApplicationContext(),
                "tcp://acs.in.htwg-konstanz.de:1883", //URI
                this.accountId,
                new MemoryPersistence()); //ClientId
        try {
            client.setCallback(new MqttCallback() {

                @Override
                public void connectionLost(Throwable cause) { //Called when the client lost the connection to the broker
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    JSONObject messageJSON = new JSONObject(message.getPayload().toString());

                    Notification n  = new Notification.Builder(getApplicationContext())
                            .setContentTitle(messageJSON.getString("title"))
                            .setContentText(messageJSON.getString("message")).build();

                    NotificationManager notificationManager =
                            (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

                    notificationManager.notify(0, n);
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {//Called when a outgoing publish is complete
                }
            });

            MqttConnectOptions options = new MqttConnectOptions();
            options.setUserName("guest");
            options.setPassword("guest".toCharArray());

            this.client.connect(options);

            this.client.subscribe(this.accountId, 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void unsubscribe(){
        try {
            this.client.unsubscribe(this.accountId);
            this.client.disconnect();
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}


