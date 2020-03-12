package com.spotsense.data.network.model.responseModel;

public class GetAppInfoResponseModel {

    private String numberOfTriggers;

    private String clientID;

    private String createdBy;

    private String[] hasAccess;

    private String created;

    private String name;

    private String description;

    private String clientSecret;

    private String[] rules;

    private String iOSBundleID;

    private String[] users;

    public String getNumberOfTriggers() {
        return numberOfTriggers;
    }

    public void setNumberOfTriggers(String numberOfTriggers) {
        this.numberOfTriggers = numberOfTriggers;
    }

    public String getClientID() {
        return clientID;
    }

    public void setClientID(String clientID) {
        this.clientID = clientID;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String[] getHasAccess() {
        return hasAccess;
    }

    public void setHasAccess(String[] hasAccess) {
        this.hasAccess = hasAccess;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public String[] getRules() {
        return rules;
    }

    public void setRules(String[] rules) {
        this.rules = rules;
    }

    public String getIOSBundleID() {
        return iOSBundleID;
    }

    public void setIOSBundleID(String iOSBundleID) {
        this.iOSBundleID = iOSBundleID;
    }

    public String[] getUsers() {
        return users;
    }

    public void setUsers(String[] users) {
        this.users = users;
    }

    @Override
    public String toString() {
        return "ClassPojo [numberOfTriggers = " + numberOfTriggers + ", clientID = " + clientID + ", createdBy = " + createdBy + ", hasAccess = " + hasAccess + ", created = " + created + ", name = " + name + ", description = " + description + ", clientSecret = " + clientSecret + ", rules = " + rules + ", iOSBundleID = " + iOSBundleID + ", users = " + users + "]";
    }
}
