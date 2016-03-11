package com.persistent.beacondetector;

import android.app.Application;
import android.os.RemoteException;
import android.util.Log;

import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.startup.BootstrapNotifier;
import org.altbeacon.beacon.startup.RegionBootstrap;

public class Beacons extends Application implements BootstrapNotifier{
    private static final String TAG = ".MyApplicationName";
    private RegionBootstrap regionBootstrap;
    private BeaconManager beaconManager;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "App started up");
        beaconManager = BeaconManager.getInstanceForApplication(this);
        beaconManager.getBeaconParsers().add(new BeaconParser()
                .setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"));
        // wake up the app when any beacon is seen (you can specify specific id filers in the parameters below)
        Region region = new Region("com.persistent.beacondetector.boostrapRegion", null,null,null);
//        Region region = new Region("com.persistent.beacondetector.boostrapRegion", Identifier.parse("f7826da6-4fa2-4e98-8024-bc5b71e0893e"), Identifier.parse("1"), Identifier.parse("10"));
//        Region region2 = new Region("com.persistent.beacondetector.boostrapRegion", Identifier.parse("f7826da6-4fa2-4e98-8024-bc5b71e0893a"), Identifier.parse("1"), Identifier.parse("3"));
        regionBootstrap = new RegionBootstrap(this, region);
    }

    @Override
    public void didEnterRegion(Region region) {
        try {
            beaconManager.startRangingBeaconsInRegion(region);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void didExitRegion(Region region) {
        try {
            beaconManager.stopRangingBeaconsInRegion(region);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void didDetermineStateForRegion(int i, Region region) {

    }
}
