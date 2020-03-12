package com.spotsense.data.network.model.responseModel;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GetRulesResponseModel {

    private List<RulesBean> rules;

    public List<RulesBean> getRules() {
        return rules;
    }

    public void setRules(List<RulesBean> rules) {
        this.rules = rules;
    }

    public static class RulesBean {
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

        private String id;
        private String app;
        private boolean deleted;
        private String geofenceName;
        private boolean enabled;
        private boolean userCreated;
        private String numberOfVisits;
        private GeofenceBean geofence;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getApp() {
            return app;
        }

        public void setApp(String app) {
            this.app = app;
        }

        public boolean isDeleted() {
            return deleted;
        }

        public void setDeleted(boolean deleted) {
            this.deleted = deleted;
        }

        public String getGeofenceName() {
            return geofenceName;
        }

        public void setGeofenceName(String geofenceName) {
            this.geofenceName = geofenceName;
        }

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public boolean isUserCreated() {
            return userCreated;
        }

        public void setUserCreated(boolean userCreated) {
            this.userCreated = userCreated;
        }

        public String getNumberOfVisits() {
            return numberOfVisits;
        }

        public void setNumberOfVisits(String numberOfVisits) {
            this.numberOfVisits = numberOfVisits;
        }

        public GeofenceBean getGeofence() {
            return geofence;
        }

        public void setGeofence(GeofenceBean geofence) {
            this.geofence = geofence;
        }

        public static class GeofenceBean {
            /**
             * radiusSize : 578.5535347135097
             * center : {"lat":23.048707269783353,"long":72.56865521321413}
             */

            private double radiusSize;
            private CenterBean center;

            public double getRadiusSize() {
                return radiusSize;
            }

            public void setRadiusSize(double radiusSize) {
                this.radiusSize = radiusSize;
            }

            public CenterBean getCenter() {
                return center;
            }

            public void setCenter(CenterBean center) {
                this.center = center;
            }

            public static class CenterBean {
                /**
                 * lat : 23.048707269783353
                 * long : 72.56865521321413
                 */

                private double lat;
                @SerializedName("long")
                private double longX;

                public double getLat() {
                    return lat;
                }

                public void setLat(double lat) {
                    this.lat = lat;
                }

                public double getLongX() {
                    return longX;
                }

                public void setLongX(double longX) {
                    this.longX = longX;
                }
            }
        }
    }
}
