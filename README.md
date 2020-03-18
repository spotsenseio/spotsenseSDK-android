#  SpotSense SDK

## AndroidX
PhotoEditor ```v.1.0.0``` is a migration to androidX and dropping the support of older support library. There are no API changes. If you find any issue migrating to v.1.0.0 , please follow this [Guide](https://developer.android.com/jetpack/androidx/migrate). If you still facing the issue than you can always rollback to v.0.4.0. Any fix in PR are Welcome :)


## Getting Started
### Create an App
1. Create an app in the [SpotSense Dashboard](http://dashboard.spotsense.io)
2. Download the SpotSense SDK
    Download the SpotSenseSDK via GitHub or dependency by doing the following
    
```groovy
dependencies {
    ...
     implementation 'com.spotsenseio:spotsense:1.0.1'
    ...
}
```
   
    
    
3. `import spotsense` and initialize SpotSense with Client ID and Secret from the Dashboard
```java
import com.spotsense.interfaces.GetSpotSenseData;
import com.spotsense.utils.sportSenseGeofencing.SpotSence;


SpotSence spotSence = new SpotSence(this, "Replace with your client id", "Replace with your client Secret", new GetSpotSenseData() {
            @Override
            public void getSpotSenseGeofencingData(String GeofenceTransactions, String geofenceName) {

            }

            @Override
            public void getSpotSenceBeaconData(String beaconTransactions, String beaconName) {

            }
});
```


4. start geofence tracking and location tracking using below function

```java
spotSence.start();
```

5. Select your new app and create a rule in the SpotSense Dashboard
5. Test your rule out in the Android device

Have a question or got stuck? Let us know in the SpotSense Slack Community or shoot us an email (help@spotsense.io). We are happy to help!

