package com.alphadominche.steampunkhmi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;

public class BatteryBroadcastReceiver extends BroadcastReceiver {
    private static final int BATTERY_WARNING_THRESHOLD = 50;

    private static int mNextWarningLevel = BATTERY_WARNING_THRESHOLD;

    private final int NEXT_WARNING_OFFSET = 5;
    private final int BAD_READING = -1;

    private LowBatteryLevelListener mBatteryLevelListener;

    @Override
    public void onReceive(Context context, Intent intent) {
        int batteryLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, BAD_READING);

        boolean needsWarning = batteryLevel != BAD_READING && batteryLevel <= BATTERY_WARNING_THRESHOLD && batteryLevel <= mNextWarningLevel;

        if (needsWarning) {
            if (mBatteryLevelListener != null) {
                mBatteryLevelListener.batteryLevelLow();
                mNextWarningLevel = batteryLevel - NEXT_WARNING_OFFSET;
            }
        } else if (batteryLevel > BATTERY_WARNING_THRESHOLD) {
            mNextWarningLevel = BATTERY_WARNING_THRESHOLD;
        }
    }

    public void registerListener(LowBatteryLevelListener listener) {
        mBatteryLevelListener = listener;
    }

    public void unregisterListener() {
        mBatteryLevelListener = null;
    }

    public interface LowBatteryLevelListener {
        void batteryLevelLow();
    }
}
