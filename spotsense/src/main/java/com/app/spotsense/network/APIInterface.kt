package com.app.spotsense.network

import com.app.spotsense.network.model.requestModel.FetchTokenRequestModel
import com.app.spotsense.network.model.responseModel.FetchTokenResponseModel
import com.app.spotsense.network.model.responseModel.GetAppInfoResponseModel
import com.app.spotsense.network.model.responseModel.GetBeaconRulesResponseModel
import com.app.spotsense.network.model.responseModel.GetRulesResponseModel
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface APIInterface {
    @POST("token")
    fun getToken(@Body requestBody: FetchTokenRequestModel?): Call<FetchTokenResponseModel>?

    // @Headers({ "Content-Type:application/json"})
    @GET("apps/{id}")
    fun getAppInfo(@Path("id") id: String? /*, @Header("Authorization") String auth*/): Call<GetAppInfoResponseModel>?

    @GET("{id}/rules")
    fun getRules(@Path("id") id: String?): Call<GetRulesResponseModel>?

    @GET("{id}/beaconRules")
    fun getBeaconRules(@Path("id") id: String?): Call<GetBeaconRulesResponseModel>?

    @POST("{id}/rules/{ruleId}/enter")
    fun enter(
        @Path("id") id: String?,
        @Path("ruleId") ruleId: String?,
        @Body requestBody: RequestBody?
    ): Call<GetRulesResponseModel?>?

    @POST("{id}/rules/{ruleId}/exit")
    fun exit(
        @Path("id") id: String?,
        @Path("ruleId") ruleId: String?,
        @Body requestBody: RequestBody?
    ): Call<GetRulesResponseModel?>?

    @GET("{id}/users/{UserId}")
    fun userExist(@Path("id") id: String?, @Path("UserId") userId: String?): Call<Any?>?

    @POST("{id}/users/")
    fun createUser(@Path("id") id: String?, @Body requestBody: RequestBody?): Call<Any?>?

    @POST("{id}/locations")
    fun updateLocation(@Path("id") id: String?, @Body requestBody: RequestBody?): Call<Any?>?

    @POST("{id}/beaconRules/{ruleId}/enter")
    fun beaconEnter(
        @Path("id") id: String?,
        @Path("ruleId") ruleId: String?,
        @Body requestBody: RequestBody?
    ): Call<GetRulesResponseModel?>?

    @POST("{id}/beaconRules/{ruleId}/exit")
    fun beaconExit(
        @Path("id") id: String?,
        @Path("ruleId") ruleId: String?,
        @Body requestBody: RequestBody?
    ): Call<GetRulesResponseModel?>?
}