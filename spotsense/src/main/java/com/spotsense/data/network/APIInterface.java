package com.spotsense.data.network;

import com.spotsense.data.network.model.requestModel.FetchTokenRequestModel;
import com.spotsense.data.network.model.responseModel.FetchTokenResponseModel;
import com.spotsense.data.network.model.responseModel.GetAppInfoResponseModel;
import com.spotsense.data.network.model.responseModel.GetBeaconRulesResponseModel;
import com.spotsense.data.network.model.responseModel.GetRulesResponseModel;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface APIInterface {

    @POST("token")
    Call<FetchTokenResponseModel> getToken(@Body FetchTokenRequestModel requestBody);

    // @Headers({ "Content-Type:application/json"})
    @GET("apps/{id}")
    Call<GetAppInfoResponseModel> getAppInfo(@Path("id") String id/*, @Header("Authorization") String auth*/);

    @GET("{id}/rules")
    Call<GetRulesResponseModel> getRules(@Path("id") String id);

    @GET("{id}/beaconRules")
    Call<GetBeaconRulesResponseModel> getBeaconRules(@Path("id") String id);

    @POST("{id}/rules/{ruleId}/enter")
    Call<GetRulesResponseModel> enter(@Path("id") String id, @Path("ruleId") String ruleId, @Body RequestBody requestBody);

    @POST("{id}/rules/{ruleId}/exit")
    Call<GetRulesResponseModel> exit(@Path("id") String id, @Path("ruleId") String ruleId, @Body RequestBody requestBody);


    @GET("{id}/users/{UserId}")
    Call<Object> userExist(@Path("id") String id, @Path("UserId") String userId);

    @POST("{id}/users/")
    Call<Object> createUser(@Path("id") String id, @Body RequestBody requestBody);



    @POST("{id}/beaconRules/{ruleId}/enter")
    Call<GetRulesResponseModel> beaconEnter(@Path("id") String id, @Path("ruleId") String ruleId, @Body RequestBody requestBody);


    @POST("{id}/beaconRules/{ruleId}/exit")
    Call<GetRulesResponseModel> beaconExit(@Path("id") String id, @Path("ruleId") String ruleId, @Body RequestBody requestBody);


}