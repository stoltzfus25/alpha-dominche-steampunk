package com.alphadominche.steampunkhmi.test;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.test.AndroidTestCase;
import android.util.Log;

import com.alphadominche.steampunkhmi.utils.CleaningCycle;
import com.alphadominche.steampunkhmi.utils.Constants;
import com.alphadominche.steampunkhmi.utils.SteampunkUtils;

public class CleaningCycleTest extends AndroidTestCase 
{

	@Override
	protected void setUp() throws Exception 
	{
		getContext()
				.getSharedPreferences(Constants.SHARED_PREFS_NAME,
						Context.MODE_PRIVATE).edit().clear().apply();
		super.setUp();
	}

	public void testConstructorAndGetters() 
	{
		Double expectedTemperature = 189.0;
		Double expectedVolume = 6.3;

		CleaningCycle testCleaningCycle = new CleaningCycle(expectedTemperature, expectedVolume);

		assertEquals(expectedTemperature, testCleaningCycle.getTemperature());
		assertEquals(expectedVolume, testCleaningCycle.getVolume());
	}

	public void testWriteUserSettingsToSharedPreferences() 
	{
		testWriteToSharedPreferencesWithValues(189.0, 6.3);
		testWriteToSharedPreferencesWithValues(null, 6.3);
		testWriteToSharedPreferencesWithValues(189.0, null);
		testWriteToSharedPreferencesWithValues(null, null);
	}

	private void testWriteToSharedPreferencesWithValues (
			Double expectedTemperature, Double expectedVolume) 
	{
		SharedPreferences sharedPreferences = getContext()
				.getSharedPreferences(Constants.SHARED_PREFS_NAME,
						Context.MODE_PRIVATE);

		sharedPreferences.edit().clear().apply();

		CleaningCycle cleaningCycle = new CleaningCycle(
				expectedTemperature, expectedVolume);
		
		CleaningCycle.writeCleaningCycleToSharedPreferences(
				cleaningCycle, getContext());
		
		Double actualTemperature = Double.longBitsToDouble(
				sharedPreferences.getLong(
						Constants.SP_CLEANING_TEMP, 0));
		
		Double actualVolume = Double.longBitsToDouble(
				sharedPreferences.getLong(
						Constants.SP_CLEANING_VOL, 0));

		if(expectedTemperature == null) expectedTemperature = 0.0;
		if(expectedVolume == null) expectedVolume = 0.0;
		
		assertEquals(expectedTemperature, actualTemperature);
		assertEquals(expectedVolume, actualVolume);
	}

	public void testGetFromSharedPreferences() 
	{
		testGetFromSharedPreferencesWithValues(189.0, 6.3);
		testGetFromSharedPreferencesWithValues(Double.NaN, 6.3);
		testGetFromSharedPreferencesWithValues(189.0, Double.NaN);
		testGetFromSharedPreferencesWithValues(Double.NaN, Double.NaN);
	}

	private void testGetFromSharedPreferencesWithValues(
			Double expectedTemperature, Double expectedVolume) 
	{
		Editor editor = 
				getContext().getSharedPreferences(Constants.SHARED_PREFS_NAME, Context.MODE_PRIVATE)
					.edit();

		editor.clear().apply();
		editor.putLong(Constants.SP_CLEANING_TEMP, Double.doubleToRawLongBits(expectedTemperature));
		editor.putLong(Constants.SP_CLEANING_VOL, 
				Double.doubleToRawLongBits(expectedVolume));
		
		editor.apply();

		CleaningCycle cleaningCycle = CleaningCycle.
				getCleaningCycleFromSharedPreferences(getContext());

		assertEquals(expectedTemperature, cleaningCycle.getTemperature());
		assertEquals(expectedVolume, cleaningCycle.getVolume());
	}

}
