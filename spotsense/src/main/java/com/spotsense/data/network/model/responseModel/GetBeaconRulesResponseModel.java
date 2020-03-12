package com.spotsense.data.network.model.responseModel;

import org.json.JSONObject;

import java.util.List;

public class GetBeaconRulesResponseModel {


    private List<BeaconRulesBean> beaconRules;

    public List<BeaconRulesBean> getBeaconRules() {
        return beaconRules;
    }

    public void setBeaconRules(List<BeaconRulesBean> beaconRules) {
        this.beaconRules = beaconRules;
    }

    public static class BeaconRulesBean {
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

        private String id;
        private String app;
        private boolean deleted;
        private String beaconName;
        private String namespace;
        private boolean enabled;
        private boolean userCreated;
        private String numberOfVisits;


        public String getIdandName() {
            JSONObject jo = new JSONObject();
            try {

                jo.put("id", getId());
                jo.put("name", getBeaconName());

            } catch (Exception e) {
            }
            return jo.toString();
        }

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

        public String getBeaconName() {
            return beaconName;
        }

        public void setBeaconName(String beaconName) {
            this.beaconName = beaconName;
        }

        public String getNamespace() {
            return namespace;
        }

        public void setNamespace(String namespace) {
            this.namespace = namespace;
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
    }
}
