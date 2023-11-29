package com.app.spotsense.network.model.requestModel

data class FetchTokenRequestModel(
    var audience: String? = null,
    var grant_type: String? = null,
    var client_secret: String? = null,
    var client_id: String? = null
)