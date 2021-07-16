package com.spotsense.utils.spotSenseGeofencing;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;
import com.google.android.gms.location.Geofence;
import com.spotsense.LocationUpdatesService;
import com.spotsense.data.network.model.responseModel.GetBeaconRulesResponseModel;
import com.spotsense.data.network.model.responseModel.GetRulesResponseModel;
import com.spotsense.interfaces.GetSpotSenseData;
import com.spotsense.utils.SpotSenseConstants;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

 public class SpotSenseGeo {

    public static ArrayList<Geofence> mGeofenceList = new ArrayList<>();
    public static ArrayList<GetBeaconRulesResponseModel.BeaconRulesBean> mBeaconeList = new ArrayList<>();

    private List<GetRulesResponseModel.RulesBean> mSpotSenseGeofenceList;
    private List<GetBeaconRulesResponseModel.BeaconRulesBean> mBeaconRulesBeansList;

    private Context context;

    public SpotSenseGeo(Context context, List<GetRulesResponseModel.RulesBean> mSpotSenseGeofenceList, List<GetBeaconRulesResponseModel.BeaconRulesBean> mBeaconRulesBeans, int Notification_ID, String CHANNEL_ID, String notificationMessage, int smallIcon, int largeIcon, boolean showNotification, GetSpotSenseData getSpotSenseData) {
        this.context = context;
        this.mSpotSenseGeofenceList = mSpotSenseGeofenceList;
        this.mBeaconRulesBeansList = mBeaconRulesBeans;

        SpotSenseConstants.CHANNEL_ID = CHANNEL_ID;
        SpotSenseConstants.Notification_ID = Notification_ID;
        SpotSenseConstants.NOTIFICATION_MESSAGE = notificationMessage;

        SpotSenseConstants.smallIcon = smallIcon;
        SpotSenseConstants.largeIcon = largeIcon;
        SpotSenseConstants.showNotification = showNotification;
        SpotSenseConstants.getSpotSenseData = getSpotSenseData;

        mGeofenceList.clear();
        populateGeofenceList();
        populateBeaconList();

    }

    void startMyServices() {
        if (isMyServiceRunning(LocationUpdatesService.class)) {
            Intent stopIntent = new Intent(context, LocationUpdatesService.class);
            stopIntent.setAction("stopfroeground");
            context.startService(stopIntent);
            return;

        }

        Log.e("checkrunning", "" + isMyServiceRunning(LocationUpdatesService.class));
        Log.e("myversions", "" + Build.VERSION.SDK_INT);

        if (Build.VERSION.SDK_INT >= 26) {
            context.startForegroundService(new Intent(context, LocationUpdatesService.class));
        } else {
            context.startService(new Intent(context, LocationUpdatesService.class));
        }

    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private void populateBeaconList() {
        for (int i = 0; i < mBeaconRulesBeansList.size(); i++) {
            boolean isenabled = mBeaconRulesBeansList.get(i).isEnabled();

            Log.e("notificatuionnambeacon", "" + mBeaconRulesBeansList.get(i).getBeaconName() + "" + mBeaconRulesBeansList.get(i).isEnabled());
            if (isenabled) {
                mBeaconeList.add(mBeaconRulesBeansList.get(i));
            }
        }
    }

    private void populateGeofenceList() {

        for (int i = 0; i < mSpotSenseGeofenceList.size(); i++) {
            boolean isenabled = mSpotSenseGeofenceList.get(i).isEnabled();
            ;
            //   Log.e("notificatuionnam", "" + mSpotSenseGeofenceList.get(i).getGeofenceName() + "" + mSpotSenseGeofenceList.get(i).isEnabled());
            if (isenabled) {
                JSONObject jo = new JSONObject();
                try {

                    jo.put("id", mSpotSenseGeofenceList.get(i).getId());
                    jo.put("name", mSpotSenseGeofenceList.get(i).getGeofenceName());

                } catch (Exception e) {
                }
                Log.e("notificatuionnam", "" + mSpotSenseGeofenceList.get(i).getGeofenceName());
                mGeofenceList.add(new Geofence.Builder()
                        // Set the request ID of the geofence. This is a string to identify this
                        // geofence.
                  
                        .setRequestId(jo.toString())
                        // Set the circular region of this geofence.
                        .setCircularRegion(
                                mSpotSenseGeofenceList.get(i).getGeofence().getCenter().getLat(),
                                mSpotSenseGeofenceList.get(i).getGeofence().getCenter().getLongX(),
                                (float) mSpotSenseGeofenceList.get(i).getGeofence().getRadiusSize()
                        )

                        // Set the expiration duration of the geofence. This geofence gets automatically
                        // removed after this period of time.
                        //exdur  .setExpirationDuration(mSpotSenseGeofenceList.get(i).getExpiration_in_milliseconds())
                        .setExpirationDuration(Geofence.NEVER_EXPIRE)

                        // Set the transition types of interest. Alerts are only generated for these
                        // transition. We track entry and exit transitions in this sample.
                        .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                                Geofence.GEOFENCE_TRANSITION_EXIT)

                        // Create the geofence.
                        .build());

            }
        }
        //     Log.e("geofencesize",""+mGeofenceList.size());

    }


    public void addSpotSenseGeofences() {
        if (mGeofenceList != null && mGeofenceList.size() > 0) {
            startMyServices();
        } else if (mBeaconeList != null && mBeaconeList.size() > 0) {
            startMyServices();
        } else {
            Toast.makeText(context, "No Geofence and beacon Available", Toast.LENGTH_LONG).show();
        }

    }

}
