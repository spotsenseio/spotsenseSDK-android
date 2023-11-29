package com.app.spotsense.network.model.responseModel

data class FetchTokenResponseModel(
    var access_token: String?,
    var scope: String?,
    var token_type: String?,
    var expires_in: String?
)