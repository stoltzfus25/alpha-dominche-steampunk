package com.alphadominche.steampunkhmi;

import com.alphadominche.steampunkhmi.BatteryBroadcastReceiver;
import com.alphadominche.steampunkhmi.BatteryBroadcastReceiver.LowBatteryLevelListener;

import android.content.Intent;
import android.os.BatteryManager;
import android.test.AndroidTestCase;

public class BatteryBroadcastReceiverTests extends AndroidTestCase implements LowBatteryLevelListener{
	
	BatteryBroadcastReceiver mReceiver;
	
	boolean mWarned;
	
	public void setUp() throws Exception {
		super.setUp();
		mWarned = false;
		mReceiver = new BatteryBroadcastReceiver();
		mReceiver.registerListener(this);
		resetWarningOffset();
	}
	
	public void testHightBatteryLevels() {
		Intent intent = new Intent();
		
		intent.putExtra(BatteryManager.EXTRA_LEVEL, 100);
		mReceiver.onReceive(getContext(), intent);
		
		assertFalse(mWarned);
		
		intent.putExtra(BatteryManager.EXTRA_LEVEL, 51);
		mReceiver.onReceive(getContext(), intent);
		
		assertFalse(mWarned);
		
		intent.putExtra(BatteryManager.EXTRA_LEVEL, 99);
		mReceiver.onReceive(getContext(), intent);
		
		assertFalse(mWarned);
		
		intent.putExtra(BatteryManager.EXTRA_LEVEL, 73);
		mReceiver.onReceive(getContext(), intent);
		
		assertFalse(mWarned);
	}
	
	public void testUnregisterReceiver() {
		Intent intent = new Intent();
		
		mReceiver.unregisterListener();
		intent.putExtra(BatteryManager.EXTRA_LEVEL, 5);
		mReceiver.onReceive(getContext(), intent);
		
		assertFalse(mWarned);
	}
	
	public void testOffset() {
		Intent intent = new Intent();
		
		intent.putExtra(BatteryManager.EXTRA_LEVEL, 50);
		mReceiver.onReceive(getContext(), intent);
		
		assertTrue(mWarned);
		
		resetWarned();
		
		intent.putExtra(BatteryManager.EXTRA_LEVEL, 46);
		mReceiver.onReceive(getContext(), intent);
		
		assertFalse(mWarned);
		
		intent.putExtra(BatteryManager.EXTRA_LEVEL, 45);
		mReceiver.onReceive(getContext(), intent);
		
		assertTrue(mWarned);
	}
	
	public void testLowBattery() {
		Intent intent = new Intent();
		
		intent.putExtra(BatteryManager.EXTRA_LEVEL, 50);
		mReceiver.onReceive(getContext(), intent);
		
		assertTrue(mWarned);
		
		resetWarned();
		
		intent.putExtra(BatteryManager.EXTRA_LEVEL, 8);
		mReceiver.onReceive(getContext(), intent);
		
		assertTrue(mWarned);
		
		resetWarned();
		
		intent.putExtra(BatteryManager.EXTRA_LEVEL, 1);
		mReceiver.onReceive(getContext(), intent);
		
		assertTrue(mWarned);
	}
	
	public void testIncreasingBattery() {
		Intent intent = new Intent();
		
		intent.putExtra(BatteryManager.EXTRA_LEVEL, 15);
		mReceiver.onReceive(getContext(), intent);
		
		assertTrue(mWarned);
		
		resetWarned();
		
		intent.putExtra(BatteryManager.EXTRA_LEVEL, 23);
		mReceiver.onReceive(getContext(), intent);
		
		assertFalse(mWarned);
		
		intent.putExtra(BatteryManager.EXTRA_LEVEL, 47);
		mReceiver.onReceive(getContext(), intent);
		
		assertFalse(mWarned);
		
		intent.putExtra(BatteryManager.EXTRA_LEVEL, 55);
		mReceiver.onReceive(getContext(), intent);
		
		assertFalse(mWarned);
	}
	
	private void resetWarned() {
		mWarned = false;
	}
	
	private void resetWarningOffset() {
		Intent intent = new Intent();
		
		intent.putExtra(BatteryManager.EXTRA_LEVEL, 100);
		mReceiver.onReceive(getContext(), intent);
	}

	@Override
	public void batteryLevelLow() {
		mWarned = true;
	}
}
