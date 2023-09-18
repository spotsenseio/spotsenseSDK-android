package com.spotsense.data.network;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.spotsense.BuildConfig;
import com.spotsense.data.network.loggingInterceptor.Level;
import com.spotsense.data.network.loggingInterceptor.LoggingInterceptor;
import com.spotsense.utils.SpotSenseAlertDialogUtils;
import com.spotsense.utils.SpotSenseConstants;
import com.spotsense.utils.SpotSenseNetworkUtils;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class APIHandler {

    private static final long HTTP_TIMEOUT = TimeUnit.SECONDS.toMillis(60);
    private static APIInterface apiInterface;
    private static String apiToken;


    private static Retrofit getRetrofitInstance(final String token) {

        OkHttpClient.Builder client = new OkHttpClient.Builder();

        client.connectTimeout(HTTP_TIMEOUT, TimeUnit.MILLISECONDS);
        client.writeTimeout(HTTP_TIMEOUT, TimeUnit.MILLISECONDS);
        client.readTimeout(HTTP_TIMEOUT, TimeUnit.MILLISECONDS);

        client.addInterceptor(new LoggingInterceptor.Builder()
                .loggable(BuildConfig.DEBUG)
                .setLevel(Level.BASIC)
                .log(Log.ERROR)
                .request("Request")
                .response("Response")
                .addHeader("Version", BuildConfig.VERSION_NAME)
                .build());

        client.addInterceptor(new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                Request request = chain.request().newBuilder().addHeader("Authorization", "Bearer " + token).build();
                return chain.proceed(request);
            }
        });

        OkHttpClient okHttpClient = client.build();
        okHttpClient.retryOnConnectionFailure();
        apiToken = token;
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        return new Retrofit.Builder()
                .baseUrl(SpotSenseConstants.SPOT_SENSE_URL)
                //  .baseUrl(BuildConfig.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(okHttpClient)
                .build();
    }


    public static Retrofit getAuthClient() {

        OkHttpClient.Builder client = new OkHttpClient.Builder();

        client.connectTimeout(HTTP_TIMEOUT, TimeUnit.MILLISECONDS);
        client.writeTimeout(HTTP_TIMEOUT, TimeUnit.MILLISECONDS);
        client.readTimeout(HTTP_TIMEOUT, TimeUnit.MILLISECONDS);

        client.addInterceptor(new LoggingInterceptor.Builder()
                .loggable(BuildConfig.DEBUG)
                .setLevel(Level.BASIC)
                .log(Log.ERROR)
                .request("Request")
                .response("Response")
                .addHeader("Version", BuildConfig.VERSION_NAME)
                .build());

        OkHttpClient okHttpClient = client.build();


        return new Retrofit.Builder()
                .baseUrl("https://spotsense.auth0.com/oauth/")
                .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().disableHtmlEscaping().setLenient().create()))
                .client(okHttpClient)
                .build();
    }


    public static APIInterface getApiServices(String token) {

        if (apiInterface != null && apiToken == token) {
            return apiInterface;

        } else {
            apiInterface = getRetrofitInstance(token).create(APIInterface.class);
            return apiInterface;
        }


    }

    public <T> void CommonAPI(final Context context, Call<T> requestCall, final ResponseCallback callback, final String name) {
        if (SpotSenseNetworkUtils.isConnected(context)) {
            try {
                //Call<CommonResponseModel> requestCall = getApiServices().logout(param);
                requestCall.enqueue(new Callback<T>() {

                    @Override
                    public void onResponse(Call<T> call, Response<T> response) {
                        Log.e("onResponse", "true" + response.body());
                        if (response.body() != null) {
                            Log.e("responses",""+ response.body().toString());
                            callback.onSuccess(response.body(), name);
                        }

                    }

                    @Override
                    public void onFailure(Call<T> call, Throwable t) {
                        try {
                            if (context != null) {
                             Log.e("faildapicalls","true");
                                //   Toast.makeText(context, "Faild api call", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                        }
                        Log.e("responseserror",""+t.getLocalizedMessage());
                        callback.onFail(t.getMessage());
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            SpotSenseAlertDialogUtils.showInternetAlert(context);
        }
    }


}