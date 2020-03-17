#  SpotSense SDK

## Getting Started
### Create an App
1. Create an app in the [SpotSense Dashboard](http://dashboard.spotsense.io)
2. Download the SpotSense SDK
    Download the SpotSenseSDK via GitHub or dependency by doing the following
    
```groovy
dependencies {
    ...
     implementation 'com.spotsenseio:spotsense:1.0.0'
    ...
}
```
   
    
    
3. `import spotsense` and initialize SpotSense with Client ID and Secret from the Dashboard
```java
import com.spotsense.interfaces.GetSpotSenseData;
import com.spotsense.utils.sportSenseGeofencing.SpotSence;


SpotSence spotSence = new SpotSence(this, "Replace with your client id", "Replace with your client Secret", new GetSpotSenseData() {
    @Override
    public void getSpotSenseGeofencingData(String GeofenceTransactions, ArrayList<String> GeofenceTransactionsRequestedId, String geofenceTransitionDetails) {
    }
    @Override
    public void getSpotSenceBeaconData(String GeofenceTransactions, String geofenceTransitionDetails) {
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

