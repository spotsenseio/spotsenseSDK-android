package com.spotsense.data.network;

public interface ResponseCallback {

    void onSuccess(Object object, String name);

    void onFail(Object object);
/*
    class APIRequest {

        private Retrofit retrofit;
        private static final long HTTP_TIMEOUT = TimeUnit.SECONDS.toMillis(120);

        private static APIRequest singletonAPIRequest;

        private APIRequest() {}

        public static APIRequest getInstance(){
            if (singletonAPIRequest == null){ //if there is no instance available... create new one
                singletonAPIRequest = new APIRequest();
            }
            return singletonAPIRequest;
        }

        public APIInterface getAPIInterface() {
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

            retrofit = new Retrofit.Builder()
                    .baseUrl(BuildConfig.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().disableHtmlEscaping().setLenient().create()))
                    .client(okHttpClient)
                    .build();

            return retrofit.create(APIInterface.class);
        }
    }*/
}
