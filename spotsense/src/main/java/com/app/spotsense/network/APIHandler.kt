package com.app.spotsense.network

import android.content.Context
import android.util.Log
import com.app.spotsense.network.loggingInterceptor.Level
import com.app.spotsense.network.loggingInterceptor.LoggingInterceptor
import com.app.spotsense.utils.SpotSenseAlertDialogUtils
import com.app.spotsense.utils.SpotSenseConstants
import com.app.spotsense.utils.SpotSenseNetworkUtils
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import org.altbeacon.beacon.BuildConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.concurrent.TimeUnit

class APIHandler {
    fun <T> commonAPI(
        context: Context?,
        requestCall: Call<T>,
        callback: ResponseCallback,
        name: String?
    ) {
        if (SpotSenseNetworkUtils.isConnected(context)) {
            try {
                //Call<CommonResponseModel> requestCall = getApiServices().logout(param);
                requestCall.enqueue(object : Callback<T?> {
                    override fun onResponse(call: Call<T?>, response: Response<T?>) {
                        Log.e("onResponse", "true" + response.body())
                        if (response.body() != null) {
                            Log.e("responses", "" + response.body().toString())
                            callback.onSuccess(response.body(), name)
                        }
                    }

                    override fun onFailure(call: Call<T?>, t: Throwable) {
                        try {
                            if (context != null) {
                                Log.e("faildapicalls", "true")
                                //   Toast.makeText(context, "Faild api call", Toast.LENGTH_SHORT).show();
                            }
                        } catch (e: Exception) {
                        }
                        Log.e("responseserror", "" + t.localizedMessage)
                        callback.onFail(t.message)
                    }
                })
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("TEST_CHECK", "commonAPI: $e", )
            }
        } else {
            SpotSenseAlertDialogUtils.showInternetAlert(context)
        }
    }

    companion object {
        private val HTTP_TIMEOUT = TimeUnit.SECONDS.toMillis(60)
        private var apiInterface: APIInterface? = null
        private var apiToken: String? = null
        private fun getRetrofitInstance(token: String): Retrofit {
            val client: OkHttpClient.Builder = OkHttpClient.Builder()
            client.connectTimeout(HTTP_TIMEOUT, TimeUnit.MILLISECONDS)
            client.writeTimeout(HTTP_TIMEOUT, TimeUnit.MILLISECONDS)
            client.readTimeout(HTTP_TIMEOUT, TimeUnit.MILLISECONDS)
            client.addInterceptor(
                LoggingInterceptor.Builder()
                    .loggable(BuildConfig.DEBUG)
                    .setLevel(Level.BASIC)
                    .log(Log.ERROR)
                    .request("Request")
                    .response("Response")
                    .addHeader("Version", BuildConfig.VERSION_NAME)
                    .build()
            )
            client.addInterceptor { chain ->
                val request: Request =
                    chain.request().newBuilder().addHeader("Authorization", "Bearer $token")
                        .build()
                chain.proceed(request)
            }
            val okHttpClient: OkHttpClient = client.build()
            okHttpClient.retryOnConnectionFailure()
            apiToken = token
            val gson: Gson = GsonBuilder()
                .setLenient()
                .create()
            return Retrofit.Builder()
                .baseUrl(SpotSenseConstants.SPOT_SENSE_URL) //  .baseUrl(BuildConfig.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(okHttpClient)
                .build()
        }

        val authClient: Retrofit
            get() {
                val client: OkHttpClient.Builder = OkHttpClient.Builder()
                client.connectTimeout(HTTP_TIMEOUT, TimeUnit.MILLISECONDS)
                client.writeTimeout(HTTP_TIMEOUT, TimeUnit.MILLISECONDS)
                client.readTimeout(HTTP_TIMEOUT, TimeUnit.MILLISECONDS)
                client.addInterceptor(
                    LoggingInterceptor.Builder()
                        .loggable(BuildConfig.DEBUG)
                        .setLevel(Level.BASIC)
                        .log(Log.ERROR)
                        .request("Request")
                        .response("Response")
                        .addHeader("Version", BuildConfig.VERSION_NAME)
                        .build()
                )
                val okHttpClient: OkHttpClient = client.build()
                return Retrofit.Builder()
                    .baseUrl("https://spotsense.auth0.com/oauth/")
                    .addConverterFactory(
                        GsonConverterFactory.create(
                            GsonBuilder().disableHtmlEscaping().setLenient().create()
                        )
                    )
                    .client(okHttpClient)
                    .build()
            }

        fun getApiServices(token: String): APIInterface? {
            return if (apiInterface != null && apiToken === token) {
                apiInterface
            } else {
                apiInterface = getRetrofitInstance(token).create<APIInterface>(
                    APIInterface::class.java
                )
                apiInterface
            }
        }
    }
}