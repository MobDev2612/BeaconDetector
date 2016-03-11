package com.persistent.beacondetector;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.ListView;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.powersave.BackgroundPowerSaver;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MainActivity extends AppCompatActivity implements BeaconConsumer {
    protected static final String TAG = "RangingActivity";
    private BeaconManager beaconManager;
    ListView listView;
        CustomAdapter customAdapter;
    List<Beacon> beaconList;
    List<String> beaconList1;
    Beacon beacon1, beacon2;
    private BackgroundPowerSaver backgroundPowerSaver;

    enum STATE {IN, OUT, UNDETERMINED}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = (ListView) findViewById(R.id.list_view);
        beaconManager = BeaconManager.getInstanceForApplication(this);
        beaconManager.getBeaconParsers().add(new BeaconParser()
                .setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"));

        beaconManager.bind(this);
//        backgroundPowerSaver = new BackgroundPowerSaver(this);
//        beaconManager
        beacon1 = new Beacon.Builder()
                .setId1("f7826da6-4fa2-4e98-8024-bc5b71e0893e")
                .setId2("1")
                .setId3("10")
                .build();
        beacon2 = new Beacon.Builder()
                .setId1("f7826da6-4fa2-4e98-8024-bc5b71e0893a")
                .setId2("1")
                .setId3("3")
                .build();
        beaconList1 = new ArrayList<>();
        beaconList = new ArrayList<>();
        customAdapter = new CustomAdapter(MainActivity.this,R.layout.item_layout,beaconList);
        listView.setAdapter(customAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        beaconManager.unbind(this);
    }

    @Override
    public void onBeaconServiceConnect() {
        final Region region = new Region("myBeacons", null, null, null);
        beaconManager.setMonitorNotifier(new MonitorNotifier() {
            @Override
            public void didEnterRegion(Region region) {
                try {
                    Log.d(TAG, "didEnterRegion");
                    beaconManager.startRangingBeaconsInRegion(region);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void didExitRegion(Region region) {
                try {
                    Log.d(TAG, "didExitRegion");
                    beaconManager.stopRangingBeaconsInRegion(region);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void didDetermineStateForRegion(int i, Region region) {

            }

        });

        beaconManager.setRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(final Collection<Beacon> beacons, Region region) {
                for (Beacon oneBeacon : beacons) {

////                    Log.d(TAG+"test", "distance: " + oneBeacon.getDistance() + " id:" + oneBeacon.getId1() + "/" + oneBeacon.getId2() + "/" + oneBeacon.getId3());
//                    Log.d(TAG,oneBeacon.toString());
                    if (oneBeacon.getDistance() <= 0.7) {
                        STATE notif = saveBeacon(oneBeacon);
                        if( notif== STATE.IN || notif == STATE.OUT){
                            showNotification(notif);
                        }
                    }
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setData(beacons);
                    }
                });
            }
        });

        try {
            beaconManager.startMonitoringBeaconsInRegion(region);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    private void setData(Collection<Beacon> beacons ){
        beaconList = new ArrayList<>(beacons);
        customAdapter = new CustomAdapter(MainActivity.this,R.layout.item_layout,beaconList);
        listView.setAdapter(customAdapter);
    }


    private STATE saveBeacon(Beacon beacon) {
        String id = beacon.getId1().toString();
        if (!beaconList1.contains(id)) {
            beaconList1.add(id);
//            Toast.makeText(this, "Detected", Toast.LENGTH_SHORT).show();
        }
        if (beaconList1.size() == 2) {
            return direction();
        } else {
            return STATE.UNDETERMINED;
        }
    }

    private STATE direction() {
        if (beaconList1.get(0).equals(beacon1.getId1().toString()) && beaconList1.get(1).equals(beacon2.getId1().toString())) {
            return STATE.IN;
        } else if (beaconList1.get(0).equals(beacon2.getId1().toString()) && beaconList1.get(1).equals(beacon1.getId1().toString())) {
            return STATE.OUT;
        } else {
            return STATE.UNDETERMINED;
        }
    }

    private void showNotification(STATE value) {
        beaconList1.clear();
        String message;
        if (value == STATE.IN) {
            message = "Thank You!! Have a Nice Day !!!";
        } else {
            message = "Welcome to Persistent";
        }
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        android.support.v4.app.NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("myBuddy")
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());

    }

}
