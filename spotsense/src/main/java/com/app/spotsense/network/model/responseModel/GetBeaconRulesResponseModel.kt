package com.app.spotsense.network.model.responseModel

import org.json.JSONObject

data class GetBeaconRulesResponseModel(var beaconRules: List<BeaconRulesBean>?) {
    data class BeaconRulesBean(
        var id: String?,
        var app: String?,
        var isDeleted: Boolean = false,
        var beaconName: String?,
        var namespace: String?,
        var isEnabled: Boolean = false,
        var isUserCreated: Boolean = false,
        var numberOfVisits: String?,
        val idandName: String
    ) {
        /**
         * id : 2f940880-490b-11ea-8fb9-87dec0399abc
         * app : dI8a61WR80hSw1Y5RYqPNyVYjoUCCaee
         * deleted : false
         * beaconName : Lounge
         * namespace : 4385395b88eb3dc1206d
         * enabled : true
         * userCreated : true
         * numberOfVisits : 0
         */
    }
}