package com.app.spotsense.network.model.responseModel

data class GetAppInfoResponseModel(
    var numberOfTriggers: String?,
    var clientID: String?,
    var createdBy: String?,
    var hasAccess: List<String>,
    var created: String?,
    var name: String?,
    var description: String?,
    var clientSecret: String?,
    var rules: List<String>,
    var iOSBundleID: String?,
    var users: List<String>
)