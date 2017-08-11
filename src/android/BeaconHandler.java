package cordova.ambient.cospaces.positioning.algoritm;

import org.altbeacon.beacon.*;

import android.provider.Settings.Secure;
import android.content.Context;
import android.os.RemoteException;
import android.content.Intent;
import android.util.Log;
import android.content.ServiceConnection;

import java.util.Collection;

public class BeaconHandler implements BeaconConsumer {
    protected static final String TAG = "com.htwg.ambientcospaces";
    private BeaconManager beaconManager;
    private Region region;
    private Context context;

    public BeaconHandler(Context context){
        this.context = context;
        this.beaconManager = BeaconManager.getInstanceForApplication(this.getApplicationContext());
        this.region = new Region("beacons", null, null, null);
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));
    }

    public void startScan(){
        beaconManager.bind(this);
        beaconManager.setRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<org.altbeacon.beacon.Beacon> beacons, Region region) {
                Log.i(TAG, "BEACONS: " + beacons.size());
                if (beacons.size() > 0) {
                    Log.i(TAG, "The first beacon I see is about " + beacons.iterator().next().getDistance() + " meters away.");
                    PositioningAlgorithm pa = new PositioningAlgorithm();

                    Identifier beacon = beacons.iterator().next().getId3();
                    Position p = pa.calculatePos(beacon.toInt());
                    Log.i(TAG, "Position is : " + p.x + " " + p.y);
                    String android_id = Secure.getString(getApplicationContext().getContentResolver(),
                            Secure.ANDROID_ID);
                    Log.i(TAG, "Device Uuid is : " + android_id);
                    RestClient c = new RestClient(getApplicationContext());
                    c.postPosition(p.x,p.y);

                }
            }
        });
    }

    public void stopScan()  {
        Log.i(TAG, "Stop Scan");
        try {
            beaconManager.stopRangingBeaconsInRegion(region);
        } catch (RemoteException e) {
            Log.i(TAG, "Error stopscan "+e.getMessage());
        }
        beaconManager.setRangeNotifier(null);
        beaconManager.unbind(this);
    }

    //////// IBeaconConsumer implementation /////////////////////

    public Context getApplicationContext() {
        return this.context;
    }

    @Override
    public void unbindService(ServiceConnection connection) {
        Log.i(TAG, "Unbind to IBeacon service");
        this.getApplicationContext().unbindService(connection);
    }

    @Override
    public boolean bindService(Intent intent, ServiceConnection connection, int mode) {
        Log.i(TAG, "Bind to IBeacon service");
        return this.getApplicationContext().bindService(intent, connection, mode);
    }

    @Override
    public void onBeaconServiceConnect() {
        try {
            Log.i(TAG, "Start Ranging in Region");
            beaconManager.startRangingBeaconsInRegion(this.region);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
