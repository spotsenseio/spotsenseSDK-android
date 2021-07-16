package com.spotsense.utils.spotSenseGeofencing;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.spotsense.R;
import com.spotsense.data.network.APIHandler;
import com.spotsense.data.network.APIInterface;
import com.spotsense.data.network.ResponseCallback;
import com.spotsense.data.network.model.requestModel.FetchTokenRequestModel;
import com.spotsense.data.network.model.responseModel.FetchTokenResponseModel;
import com.spotsense.data.network.model.responseModel.GetAppInfoResponseModel;
import com.spotsense.data.network.model.responseModel.GetBeaconRulesResponseModel;
import com.spotsense.data.network.model.responseModel.GetRulesResponseModel;
import com.spotsense.interfaces.GetSpotSenseData;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.spotsense.data.network.APIHandler.getApiServices;
import static com.spotsense.utils.SpotSenseConstants.GET_APP_INFO;
import static com.spotsense.utils.SpotSenseConstants.GET_BEACON_RULES;
import static com.spotsense.utils.SpotSenseConstants.GET_RULES;
import static com.spotsense.utils.SpotSenseConstants.USER_CREATE;
import static com.spotsense.utils.SpotSenseConstants.USER_EXITS;


/*
======================================================================================
======================================================================================
= =>firststep Check token exist or not. if not exist call gettoken api               =
= =>then call get info api for get information pass token in api header              =
= =>then call get rules api in get rules api you get geofencing arraylist and all    =
======================================================================================
======================================================================================*/

public class SpotSense {
    public static String clientID;
    public static String clientSecret;
    public static String deviceID;
    public static String token;
    final String eventName = "your.package.goes.here.EVENT";

    private APIHandler apiRequest;
    Context context;
    GetAppInfoResponseModel getAppInfoResponseModel;
    GetRulesResponseModel getRulesResponseModel;
    GetBeaconRulesResponseModel getBeaconRulesResponseModel;
    GetSpotSenseData getSpotSenseData;


    public SpotSense(Context context, String clientID, String clientSecret, GetSpotSenseData getSpotSenseData) {
        this.clientID = clientID;
        this.clientSecret = clientSecret;
        this.context = context;
        deviceID = Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        this.getSpotSenseData = getSpotSenseData;
    }


    public void start() {
        checkPermisionsAndGoToNext();
    }

    void checkPermisionsAndGoToNext() {
        final LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        //check location permission and gps status
        if (!isPermissionGranted() || !manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            registerEventReceiver();
            checkPermission();
        }
        //check if bluetooth is supported or not for the device
        else if (mBluetoothAdapter == null) {
            Toast.makeText(context, "Device does not support Bluetooth. Beacons will not work for this device", Toast.LENGTH_SHORT).show();
            initApp();
        }
        //check if bluetooth is on or off
        else if (!mBluetoothAdapter.isEnabled()) {
            registerEventReceiver();
            checkPermission();
            // Bluetooth is not enabled :)
        } else {
            initApp();
        }
    }

    private void registerEventReceiver() {
        IntentFilter eventFilter = new IntentFilter();
        eventFilter.addAction(eventName);
        context.registerReceiver(eventReceiver, eventFilter);
    }

    private BroadcastReceiver eventReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e("broadcastcalled", "a" + intent.getExtras().getBoolean("permissionStatus"));
            if (intent.getExtras().getBoolean("permissionStatus")) {
                context.unregisterReceiver(eventReceiver);

                Log.e("unregistersuccess", "true");
                initApp();
            } else {
                Log.e("unregistersuccess", "false");
                //  initApp();
                checkPermisionsAndGoToNext();
            }

            //This code will be executed when the broadcast in activity B is launched
        }
    };


    private boolean isPermissionGranted() {
        int permissionState = ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    private void checkPermission() {

        Intent i = new Intent(context, SpotSensePermissionActivity.class);
         i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }

    private void initApp() {
        Log.e("initappcalled", "true");
        getToken();
    }


    /* Token Fetching */
    private void getToken() {
        if (token != null) {
            getAppInfo();
        } else {
            fetchToken();
        }
    }


    //Fetch Token Api called
    private void fetchToken() {
        APIInterface apiInterface = APIHandler.getAuthClient().create(APIInterface.class);

        Call<FetchTokenResponseModel> requestCall = apiInterface.getToken(getTokenParam());
        requestCall.enqueue(new Callback<FetchTokenResponseModel>() {
            @Override
            public void onResponse(Call<FetchTokenResponseModel> call, Response<FetchTokenResponseModel> response) {
                if (response.body() == null) {
                    Toast.makeText(context, "Your Client Id or Secret key is not valid", Toast.LENGTH_LONG).show();
                } else {
                    FetchTokenResponseModel fetchTokenResponseModel = response.body();
                    token = fetchTokenResponseModel.getAccess_token();
                    Log.e("OnSuccessTokenRespons", fetchTokenResponseModel.getAccess_token());
                    getAppInfo();
                }
            }

            @Override
            public void onFailure(Call<FetchTokenResponseModel> call, Throwable t) {
                if (context != null) {
                    Toast.makeText(context, "Something Want Wrong in token api", Toast.LENGTH_SHORT).show();
                }
                Log.e("OnFailTokenResponse", "fail" + t.getLocalizedMessage());
            }
        });

    }

    //getAppInfo
    void getAppInfo() {
        apiRequest = new APIHandler();
        Call<GetAppInfoResponseModel> requestCall = getApiServices(token).getAppInfo("" + clientID);
        apiRequest.CommonAPI(context, requestCall, responseCallback, GET_APP_INFO);
    }

    //check user already exits or not
    void userExists() {
        apiRequest = new APIHandler();
        String uid = "" + clientID + "-" + SpotSense.deviceID;
        Call<Object> requestCall = getApiServices(token).userExist("" + clientID, uid);
        apiRequest.CommonAPI(context, requestCall, responseCallback, USER_EXITS);

    }

    void createUser() {
        apiRequest = new APIHandler();
        JSONObject jo = new JSONObject();
        try {
            jo.put("deviceID", SpotSense.deviceID);
            jo.put("customID", "Waddup from the SDK Playground");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), (jo).toString());

        Call<Object> requestCall = getApiServices(token).createUser("" + clientID, body);
        apiRequest.CommonAPI(context, requestCall, responseCallback, USER_CREATE);

    }

    void updateLocation() {
        apiRequest = new APIHandler();
        JSONObject jo = new JSONObject();
        try {
            jo.put("deviceID", SpotSense.deviceID);
            jo.put("location", "<+30.26297701,-97.73160763> +/- 40.26m (speed 0.00 mps / course 251.13) @ 6/15/21,");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), (jo).toString());

        Call<Object> requestCall = getApiServices(token).updateLocation("" + clientID, body);
        apiRequest.CommonAPI(context, requestCall, responseCallback, USER_CREATE);

    }

    //getRulesData
    void getRules() {
        apiRequest = new APIHandler();
        Call<GetRulesResponseModel> requestCall = getApiServices(token).getRules("" + clientID);
        apiRequest.CommonAPI(context, requestCall, responseCallback, GET_RULES);

    }

    void getBeaconRules() {
        apiRequest = new APIHandler();
        Call<GetBeaconRulesResponseModel> requestCall = getApiServices(token).getBeaconRules("" + clientID);
        apiRequest.CommonAPI(context, requestCall, responseCallback, GET_BEACON_RULES);

    }


    ResponseCallback responseCallback = new ResponseCallback() {
        @Override
        public void onSuccess(Object object, String name) {
            if (object != null) {
                if (name.equalsIgnoreCase(GET_APP_INFO)) {
                    getAppInfoResponseModel = (GetAppInfoResponseModel) object;
                    userExists();
                } else if (name.equalsIgnoreCase(GET_RULES)) {

                    getRulesResponseModel = (GetRulesResponseModel) object;
                    getBeaconRules();
                } else if (name.equalsIgnoreCase(USER_EXITS)) {
                    if (!object.toString().contains("errorMessage")) {
                        getRules();
                    } else {
                        createUser();
                    }
                } else if (name.equalsIgnoreCase(USER_CREATE)) {
                    getRules();

                } else if (name.equalsIgnoreCase(GET_BEACON_RULES)) {
                    getBeaconRulesResponseModel = (GetBeaconRulesResponseModel) object;
                    SpotSenseGeo spotSenseGeo = new SpotSenseGeo(context, getRulesResponseModel.getRules(), getBeaconRulesResponseModel.getBeaconRules(), 15, "ChannelId", "Welcome Zak From Background", R.drawable.notification, R.drawable.notification, true, getSpotSenseData);
                    spotSenseGeo.addSpotSenseGeofences();
                }
            }
        }

        @Override
        public void onFail(Object object) {

            if (context != null) {
                Log.e("OnFailApiCall", "True");
            }
        }

    };


    //method for getTokenRequest
    FetchTokenRequestModel getTokenParam() {
        FetchTokenRequestModel fetchTokenRequestModel = new FetchTokenRequestModel();
        fetchTokenRequestModel.setClient_id(clientID);
        fetchTokenRequestModel.setClient_secret(clientSecret);
        fetchTokenRequestModel.setAudience("https://api.spotsense.io/beta");
        fetchTokenRequestModel.setGrant_type("client_credentials");
        return fetchTokenRequestModel;
    }

}
