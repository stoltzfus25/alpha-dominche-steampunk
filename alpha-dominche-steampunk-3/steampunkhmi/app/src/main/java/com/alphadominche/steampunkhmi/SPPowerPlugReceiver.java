package com.alphadominche.steampunkhmi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class SPPowerPlugReceiver extends BroadcastReceiver {
    private SPCruciblesActivity activity;
    private boolean alreadyRunning = false;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (!alreadyRunning) {
            activity = SPCruciblesActivity.getInstance();
            alreadyRunning = true;
        }

        if (intent.getAction().equals(Intent.ACTION_POWER_CONNECTED)) {
            if (activity != null) activity.powerConnected(true);
        } else if (intent.getAction().equals(Intent.ACTION_POWER_DISCONNECTED)) {
            if (activity != null) activity.powerConnected(false);
        }
    }
}
