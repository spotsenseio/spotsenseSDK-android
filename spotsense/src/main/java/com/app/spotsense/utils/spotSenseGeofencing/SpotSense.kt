package com.app.spotsense.utils.spotSenseGeofencing

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.LocationManager
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.app.spotsense.R
import com.app.spotsense.interfaces.GetSpotSenseData
import com.app.spotsense.network.APIHandler
import com.app.spotsense.network.APIHandler.Companion.getApiServices
import com.app.spotsense.network.APIInterface
import com.app.spotsense.network.ResponseCallback
import com.app.spotsense.network.model.requestModel.FetchTokenRequestModel
import com.app.spotsense.network.model.responseModel.FetchTokenResponseModel
import com.app.spotsense.network.model.responseModel.GetAppInfoResponseModel
import com.app.spotsense.network.model.responseModel.GetBeaconRulesResponseModel
import com.app.spotsense.network.model.responseModel.GetRulesResponseModel
import com.app.spotsense.utils.SpotSenseConstants.GET_APP_INFO
import com.app.spotsense.utils.SpotSenseConstants.GET_BEACON_RULES
import com.app.spotsense.utils.SpotSenseConstants.GET_RULES
import com.app.spotsense.utils.SpotSenseConstants.USER_CREATE
import com.app.spotsense.utils.SpotSenseConstants.USER_EXITS
import okhttp3.MediaType
import okhttp3.RequestBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/*
======================================================================================
======================================================================================
= =>firststep Check token exist or not. if not exist call gettoken api               =
= =>then call get info api for get information pass token in api header              =
= =>then call get rules api in get rules api you get geofencing arraylist and all    =
======================================================================================
======================================================================================*/
class SpotSense(
   var  context: Context,
    var clientId: String,
    var clientSecretId: String,
    var getSpotSenseData: GetSpotSenseData
) {
    private val eventName = "your.package.goes.here.EVENT"
    private var apiRequest: APIHandler? = null
    var getAppInfoResponseModel: GetAppInfoResponseModel? = null
    var getRulesResponseModel: GetRulesResponseModel? = null
    var getBeaconRulesResponseModel: GetBeaconRulesResponseModel? = null
    fun start() {
        checkPermisionsAndGoToNext()
    }

    fun checkPermisionsAndGoToNext() {
        val manager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

        //check location permission and gps status
        if (!isPermissionGranted || !manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            registerEventReceiver()
            checkPermission()
        } else if (mBluetoothAdapter == null) {
            Toast.makeText(
                context,
                "Device does not support Bluetooth. Beacons will not work for this device",
                Toast.LENGTH_SHORT
            ).show()
            initApp()
        } else if (!mBluetoothAdapter.isEnabled) {
            registerEventReceiver()
            checkPermission()
            // Bluetooth is not enabled :)
        } else {
            initApp()
        }
    }

    private fun registerEventReceiver() {
        Log.e("TEST_CHECK", "registerEventReceiver: ", )
        val eventFilter = IntentFilter()
        eventFilter.addAction(eventName)
        context.registerReceiver(eventReceiver, eventFilter)
    }

    private val eventReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            Log.e("broadcastcalled", "a" + (intent.extras?.getBoolean("permissionStatus")))
            if (intent.extras?.getBoolean("permissionStatus") == true) {
                context.unregisterReceiver(this)
                Log.e("unregistersuccess", "true")
                initApp()
            } else {
                Log.e("unregistersuccess", "false")
                //  initApp();
                checkPermisionsAndGoToNext()
            }

            //This code will be executed when the broadcast in activity B is launched
        }
    }
    private val isPermissionGranted: Boolean
        get() {
            val permissionState: Int = ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            return permissionState == PackageManager.PERMISSION_GRANTED
        }

    private fun checkPermission() {
        val i = Intent(context, SpotSensePermissionActivity::class.java)
        i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(i)
    }

    private fun initApp() {
        Log.e("initappcalled", "true")
        this.token
    }

    private val token: Unit
        /* Token Fetching */ get() {
            if (Companion.token != null) {
                appInfo
            } else {
                fetchToken()
            }
        }

    //Fetch Token Api called
    private fun fetchToken() {
        val apiInterface: APIInterface = APIHandler.authClient.create(APIInterface::class.java)
        val requestCall: Call<FetchTokenResponseModel> = apiInterface.getToken(
            tokenParam
        )!!
        requestCall.enqueue(object : Callback<FetchTokenResponseModel?> {
            override fun onResponse(
                call: Call<FetchTokenResponseModel?>,
                response: Response<FetchTokenResponseModel?>
            ) {
                if (response.body() == null) {
                    Toast.makeText(
                        context,
                        "Your Client Id or Secret key is not valid",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    val fetchTokenResponseModel: FetchTokenResponseModel? = response.body()
                    if (fetchTokenResponseModel != null) {
                        Companion.token = fetchTokenResponseModel.access_token
                        Log.e("OnSuccessTokenRespons", fetchTokenResponseModel.access_token!!)
                    }
                    appInfo
                }
            }

            override fun onFailure(call: Call<FetchTokenResponseModel?>, t: Throwable) {
                Toast.makeText(context, "Something Want Wrong in token api", Toast.LENGTH_SHORT)
                    .show()
                Log.e("OnFailTokenResponse", "fail" + t.localizedMessage)
            }
        })
    }

    val appInfo: Unit
        //getAppInfo
        get() {
            apiRequest = APIHandler()
            val requestCall: Call<GetAppInfoResponseModel> =
                Companion.token?.let { getApiServices(it)?.getAppInfo("" + clientID) }!!
            apiRequest!!.commonAPI(context, requestCall, responseCallback, GET_APP_INFO)
        }

    //check user already exits or not
    fun userExists() {
        apiRequest = APIHandler()
        val uid = "$clientID-$deviceID"
        val requestCall: Call<Any?> =
            Companion.token?.let { getApiServices(it)?.userExist("" + clientID, uid) }!!
        apiRequest!!.commonAPI(context, requestCall, responseCallback, USER_EXITS)
    }

    fun createUser() {
        apiRequest = APIHandler()
        val jo = JSONObject()
        try {
            jo.put("deviceID", deviceID)
            jo.put("customID", "Waddup from the SDK Playground")
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        val body: RequestBody =
            RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jo.toString())
        val requestCall: Call<Any?> =
            Companion.token?.let { getApiServices(it)?.createUser("" + clientID, body) }!!
        apiRequest!!.commonAPI(context, requestCall, responseCallback, USER_CREATE)
    }

    fun updateLocation() {
        apiRequest = APIHandler()
        val jo = JSONObject()
        try {
            jo.put("deviceID", deviceID)
            jo.put(
                "location",
                "<+30.26297701,-97.73160763> +/- 40.26m (speed 0.00 mps / course 251.13) @ 6/15/21,"
            )
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        val body: RequestBody =
            RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jo.toString())
        val requestCall: Call<Any?> =
            Companion.token?.let { getApiServices(it)?.updateLocation("" + clientID, body) }!!
        apiRequest!!.commonAPI(context, requestCall, responseCallback, USER_CREATE)
    }

    val rules: Unit
        //getRulesData
        get() {
            apiRequest = APIHandler()
            val requestCall: Call<GetRulesResponseModel> =
                Companion.token?.let { getApiServices(it)?.getRules("" + clientID) }!!
            apiRequest!!.commonAPI(context, requestCall, responseCallback, GET_RULES)
        }
    val beaconRules: Unit
        get() {
            apiRequest = APIHandler()
            val requestCall: Call<GetBeaconRulesResponseModel> =
                Companion.token?.let { getApiServices(it)?.getBeaconRules("" + clientID) }!!
            apiRequest!!.commonAPI(context, requestCall, responseCallback, GET_BEACON_RULES)
        }
    private var responseCallback: ResponseCallback = object : ResponseCallback {
        override fun onSuccess(`object`: Any?, name: String?) {
            if (`object` != null) {
                if (name.equals(GET_APP_INFO, ignoreCase = true)) {
                    getAppInfoResponseModel = `object` as GetAppInfoResponseModel?
                    userExists()
                } else if (name.equals(GET_RULES, ignoreCase = true)) {
                    getRulesResponseModel = `object` as GetRulesResponseModel?
                    beaconRules
                } else if (name.equals(USER_EXITS, ignoreCase = true)) {
                    if (!`object`.toString().contains("errorMessage")) {
                        rules
                    } else {
                        createUser()
                    }
                } else if (name.equals(USER_CREATE, ignoreCase = true)) {
                    rules
                } else if (name.equals(GET_BEACON_RULES, ignoreCase = true)) {
                    getBeaconRulesResponseModel = `object` as GetBeaconRulesResponseModel?
                    if (!getRulesResponseModel?.rules.isNullOrEmpty() && !getBeaconRulesResponseModel?.beaconRules.isNullOrEmpty()) {
                        val spotSenseGeo = SpotSenseGeo(
                            context,
                            getRulesResponseModel?.rules!!,
                            getBeaconRulesResponseModel?.beaconRules!!,
                            15,
                            "ChannelId",
                            "Welcome Zak From Background",
                            R.drawable.notification,
                            R.drawable.notification,
                            true,
                            getSpotSenseData
                        )
                        spotSenseGeo.addSpotSenseGeofences()
                    }

                }
            }
        }

        override fun onFail(`object`: Any?) {
            Log.e("OnFailApiCall", "True")
        }
    }

    init {
        clientID = clientId
        clientSecret = clientSecretId
        deviceID = Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ANDROID_ID
        )

        Log.d("TEST_CHECK", "init : $clientID - $clientSecret", )
    }

    private val tokenParam: FetchTokenRequestModel
        //method for getTokenRequest
        get() {
            val fetchTokenRequestModel = FetchTokenRequestModel()
            fetchTokenRequestModel.client_id = clientID
            fetchTokenRequestModel.client_secret = clientSecret
            fetchTokenRequestModel.audience = "https://api.spotsense.io/beta"
            fetchTokenRequestModel.grant_type = "client_credentials"
            return fetchTokenRequestModel
        }

    companion object {
        var clientID: String? = null
        var clientSecret: String? = null
        var deviceID: String? = null
        var token: String? = null
    }
}