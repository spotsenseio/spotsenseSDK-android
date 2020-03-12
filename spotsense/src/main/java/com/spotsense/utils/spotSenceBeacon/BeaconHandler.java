package com.spotsense.utils.spotSenceBeacon;

import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;
import com.spotsense.data.network.APIHandler;
import com.spotsense.data.network.ResponseCallback;
import com.spotsense.data.network.model.responseModel.GetRulesResponseModel;
import com.spotsense.utils.SpotSenseConstants;
import com.spotsense.utils.SpotSenseGlobalMethods;
import com.spotsense.utils.sportSenseGeofencing.SpotSence;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.Region;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import okhttp3.RequestBody;
import retrofit2.Call;
import static com.spotsense.data.network.APIHandler.getApiServices;
import static com.spotsense.utils.SpotSenseConstants.DO_ENTER_BEACON;
import static com.spotsense.utils.SpotSenseConstants.DO_EXIT_BEACON;
import static com.spotsense.utils.sportSenseGeofencing.SpotSence.clientID;
import static com.spotsense.utils.sportSenseGeofencing.SpotSence.token;
import static com.spotsense.utils.sportSenseGeofencing.SpotSenseGeo.mBeaconeList;

public class BeaconHandler {
    private static final String TAG = BeaconHandler.class.getSimpleName();
    private BeaconManager beaconManager;
    Context context;
    public static Map<String /* device address */, Long> deviceToBeaconMap = new HashMap<>();
    private static BeaconHandler beaconHandler_instance = null;

    public static BeaconHandler getInstance() {
        if (beaconHandler_instance == null) {
            beaconHandler_instance = new BeaconHandler();
        }
        return beaconHandler_instance;
    }

    public BeaconManager getBeaconManager() {
        return beaconManager;
    }

    public void initBeaconManager(Context context) {

        Log.e("initbeaconmanagercalleds", "true");
        this.context = context;
        setbeaconManagerData(context);
    }

    BeaconConsumer beaconConsumer = new BeaconConsumer() {
        @Override
        public void onBeaconServiceConnect() {
            beaconManager.removeAllMonitorNotifiers();

            try {
                if (mBeaconeList.size() > 0) {
                    for (int i = 0; i < mBeaconeList.size(); i++) {
                        Log.e("mnnnnn", "" + mBeaconeList.get(i).getNamespace());
                        try {

                            Identifier namespaceId = Identifier.parse(mBeaconeList.get(i).getNamespace());
                            //Region regionsname = new Region("" + mBeaconeList.get(i).getBeaconName(), namespaceId, null, null);
                            Region regionsname = new Region("" + mBeaconeList.get(i).getIdandName(), namespaceId, null, null);

                            beaconManager.startMonitoringBeaconsInRegion(regionsname);
                            //    beaconManager.addMonitorNotifier(mn);
                        } catch (Exception e) {
                            Toast.makeText(context, "namespace not valid please enter valid namespace data otherwise beacon not work", Toast.LENGTH_LONG).show();
                            // onDestroys();
                            break;
                        }

                    }
                    beaconManager.addMonitorNotifier(mn);
                    Log.e("mbeaconstatus", "greaterthen0");
                } else {
                    Log.e("mbeaconstatus", "notgreaterthen0");
                }

            } catch (Exception e) {
                Log.e("errorrererer", "I have just switched from seeing/not seeing beacons: ");
                e.printStackTrace();
            }

        }

        @Override
        public Context getApplicationContext() {
            return context;
        }

        @Override
        public void unbindService(ServiceConnection serviceConnection) {
            context.unbindService(serviceConnection);
        }

        @Override
        public boolean bindService(Intent intent, ServiceConnection serviceConnection, int i) {
            return context.bindService(intent, serviceConnection, i);
        }
    };


    void setbeaconManagerData(Context context) {
        //deviceToBeaconMap.clear();
        beaconManager = BeaconManager.getInstanceForApplication(context);

        beaconManager.getBeaconParsers().add(new BeaconParser().
                setBeaconLayout(BeaconParser.EDDYSTONE_UID_LAYOUT));
        beaconManager.setDebug(true);
        beaconManager.setRegionStatePersistenceEnabled(false);
        beaconManager.setForegroundBetweenScanPeriod(25000L);
        beaconManager.setForegroundScanPeriod(10000L);

        beaconManager.setMaxTrackingAge(10000);
        beaconManager.setRegionExitPeriod(10000L);
        beaconManager.setBackgroundMode(false);
//        beaconManager.setBackgroundScanPeriod(10000L);
        //      beaconManager.setBackgroundBetweenScanPeriod(25000L);
        beaconManager.setAndroidLScanningDisabled(true);

        beaconManager.bind(beaconConsumer);
    }


    //beacon codes

    MonitorNotifier mn = new MonitorNotifier() {
        @Override
        public void didEnterRegion(Region region) {


            String beaconName = "";
            try {
                JSONObject jo = new JSONObject(region.getUniqueId());
                beaconName = jo.getString("name");
                Log.e("iscontainkey", "" + jo.getString("id"));
                if (deviceToBeaconMap.containsKey(jo.getString("id"))) {
                    deviceToBeaconMap.put(jo.getString("id"), System.currentTimeMillis());

                } else {
                    deviceToBeaconMap.put(jo.getString("id"), System.currentTimeMillis());
                    doEnter(jo.getString("id"));

                    try {
                        DateFormat df = new SimpleDateFormat("d MMM yyyy, HH:mm a");
                        String date = df.format(Calendar.getInstance().getTime());
                        Log.e("geofencenames", "Enterd: " + beaconName + " using Beacon" + date);
                        SpotSence.mydb.insertContact("Enterd: " + beaconName + " using Beacon", date);


                    } catch (Exception e) {

                    }
                    //end database


                    Toast.makeText(context, "You Entered in " + beaconName + " using beacon", Toast.LENGTH_SHORT).show();
                    if (SpotSenseConstants.getSpotSenseData != null) {
                        SpotSenseConstants.getSpotSenseData.getSpotSenceBeaconData("Enter", "Enterd: " + beaconName + " using Beacon");
                    } else {
                        SpotSenseGlobalMethods.sendNotification(context, SpotSenseConstants.Notification_ID, SpotSenseConstants.CHANNEL_ID, "you entered in" + beaconName + "using beacon", SpotSenseConstants.NOTIFICATION_MESSAGE/*, SpotSenseConstants.sClass*/, SpotSenseConstants.smallIcon, SpotSenseConstants.largeIcon);

                    }


                    Log.e(TAG + "enter", "I just saw an beacon for the first time!" + "asasasuuui" + "getUniqueId" + region.getUniqueId() + "getBluetoothAddress" + region.getBluetoothAddress() + "getId1" + region.getId1() + "getId2" + region.getId2() + "getId3" + region.getId3() + "desk" + region.describeContents());
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        @Override
        public void didExitRegion(Region region) {
            Log.e("exitbeconwithdelay", "calledsasasa");
            String beaconName = "";
            try {
                Log.e("exitbeconwithdelay", "called11212");
                JSONObject jo = new JSONObject(region.getUniqueId());
                beaconName = jo.getString("name");
                //doExit(jo.getString("id"));
                exitBeaconWithdelay(jo.getString("id"), region);
                Log.e("exitbeconwithdelay", "called434343");
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e("exitbeconwithdelay", "calledlo");
            }

        }

        @Override
        public void didDetermineStateForRegion(int state, Region region) {

            Log.e(TAG, "I have just switched from seeing/not seeing beacons: " + state);
        }
    };


    void exitBeaconWithdelay(final String id, final Region region) {
        final Handler handler = new Handler();
        Log.e("exitbeconwithdelay", "called");
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.e("exitbeconwithdelay", "1" + deviceToBeaconMap.containsKey(id));
                if (deviceToBeaconMap.containsKey(id)) {
                    Log.e("exitbeconwithdelay", "2");
                    long time = System.currentTimeMillis();
                    Log.e("times", "" + time);
                    Log.e("times2", "" + deviceToBeaconMap.get(id));
                    Log.e("times3", "" + (time - deviceToBeaconMap.get(id)));

                    if ((time - deviceToBeaconMap.get(id)) >= 15000) {
                        Log.e("exitbeconwithdelay", "3");
                        deviceToBeaconMap.remove(id);
                        exitLogic(region);
                        Log.e("exitbeconwithdelay", "4");
                    }
                    Log.e("exitbeconwithdelay", "5");
                }
                Log.e("exitbeconwithdelay", "6");
                //Do something after 100ms
            }
        }, 10000);
    }

    void exitLogic(Region region) {

        String beaconName = "";
        try {
            JSONObject jo = new JSONObject(region.getUniqueId());
            beaconName = jo.getString("name");
            doExit(jo.getString("id"));

        } catch (JSONException e) {
            e.printStackTrace();
        }


        //database

        try {
            DateFormat df = new SimpleDateFormat("d MMM yyyy, HH:mm a");
            String date = df.format(Calendar.getInstance().getTime());
            Log.e("geofencenames", "Exited: " + beaconName + " using Beacon" + date);
            SpotSence.mydb.insertContact("Exited: " + beaconName + " using Beacon", date);


        } catch (Exception e) {

        }
        //end database


        Toast.makeText(context, "You exited from " + beaconName + " using beacon", Toast.LENGTH_SHORT).show();

        if (SpotSenseConstants.getSpotSenseData != null) {
            SpotSenseConstants.getSpotSenseData.getSpotSenceBeaconData("Exit", "Exited: " + beaconName + " using Beacon");
        } else {
            SpotSenseGlobalMethods.sendNotification(context, SpotSenseConstants.Notification_ID, SpotSenseConstants.CHANNEL_ID, "you exit from" + beaconName + "using beacon", SpotSenseConstants.NOTIFICATION_MESSAGE/*, SpotSenseConstants.sClass*/, SpotSenseConstants.smallIcon, SpotSenseConstants.largeIcon);

        }

        Log.e(TAG + "exit", "I no longer see an beacon" + region.getUniqueId() + "sasa" + region.getUniqueId() + "sasasasa" + region.getBluetoothAddress() + "Asasasasa" + region.getId1() + "asasasa" + region.getId2() + "Asasasa" + region.getId3());

    }


    void doEnter(final String ruleId) {
        APIHandler apiRequest = new APIHandler();
        JSONObject jo = new JSONObject();
        try {
            jo.put("userID", "" + clientID + "-" + SpotSence.deviceID);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), (jo).toString());

        Call<GetRulesResponseModel> requestCall = getApiServices(token).beaconEnter("" + clientID, "" + ruleId, body);
        apiRequest.CommonAPI(context, requestCall, new ResponseCallback() {
            @Override
            public void onSuccess(Object object, String name) {
                Log.e("beaconapiresulenter", "success");
            }

            @Override
            public void onFail(Object object) {
                Log.e("beaconapiresulrule", "failed");

            }
        }, DO_ENTER_BEACON);

    }


    void doExit(String ruleId) {
        APIHandler apiRequest = new APIHandler();
        JSONObject jo = new JSONObject();
        try {
            jo.put("userID", "" + clientID + "-" + SpotSence.deviceID);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), (jo).toString());

        Call<GetRulesResponseModel> requestCall = getApiServices(token).beaconExit("" + clientID, "" + ruleId, body);
        apiRequest.CommonAPI(context, requestCall, new ResponseCallback() {
            @Override
            public void onSuccess(Object object, String name) {
                Log.e("apiresulrule", "success");
            }

            @Override
            public void onFail(Object object) {
                Log.e("apiresulExit", "failed");

            }
        }, DO_EXIT_BEACON);

    }


    public void onDestroys() {
        //deviceToBeaconMap.clear();
        //beaconManager.removeAllMonitorNotifiers();
        beaconManager.unbind(beaconConsumer);

    }

}
