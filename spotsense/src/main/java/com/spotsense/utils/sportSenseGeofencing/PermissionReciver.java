package com.spotsense.utils.sportSenseGeofencing;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class PermissionReciver  extends BroadcastReceiver {
    public PermissionReciver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {

       // Toast.makeText(context, "Action: " + intent.getAction(), Toast.LENGTH_SHORT).show();
    }
}