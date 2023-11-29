package com.app.spotsense.network.model.responseModel

data class GetRulesResponseModel(var rules: List<RulesBean>? = null) {
    data class RulesBean(
        var id: String? = null,
        var app: String? = null,
        var isDeleted: Boolean = false,
        var geofenceName: String? = null,
        var isEnabled: Boolean = false,
        var isUserCreated: Boolean = false,
        var numberOfVisits: String? = null,
        var geofence: GeofenceBean? = null
    ) {
        /**
         * id : dc63b7f0-232b-11ea-9a97-0d180baa5409
         * app : LRArBuRr3RAbDLDfRcu0YaxZ1ghRWn36
         * deleted : false
         * geofenceName : first geofence usmanpura
         * enabled : true
         * userCreated : true
         * numberOfVisits : 0
         * geofence : {"radiusSize":578.5535347135097,"center":{"lat":23.048707269783353,"long":72.56865521321413}}
         */

        data class GeofenceBean(
            var radiusSize: Double = 0.0,
            var center: CenterBean? = null
        ) {
            /**
             * radiusSize : 578.5535347135097
             * center : {"lat":23.048707269783353,"long":72.56865521321413}
             */

            data class CenterBean(
                var lat: Double = 0.0,
                var longX: Double = 0.0
            ) {
                /**
                 * lat : 23.048707269783353
                 * long : 72.56865521321413
                 */
            }
        }
    }
}