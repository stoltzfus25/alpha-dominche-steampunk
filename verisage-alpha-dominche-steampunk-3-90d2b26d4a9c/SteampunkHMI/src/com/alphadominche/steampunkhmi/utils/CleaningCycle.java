package com.alphadominche.steampunkhmi.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class CleaningCycle {
	private Double mTemperature;
	private Double mVolume;

	public CleaningCycle(Double temperature, Double volume) {
		this.mTemperature = temperature;
		this.mVolume = volume;
	}

	/**
	 * Persist all the data in a given machine settings object to the shared
	 * preferences
	 * 
	 * @param machineSettings
	 *            the object to write to the shared preferences
	 * @param context
	 *            app context
	 */
	public static void writeCleaningCycleToSharedPreferences(
			CleaningCycle cycle, Context context) {
		Editor editor = SteampunkUtils.getSteampunkSharedPreferences(context)
				.edit();

		// Shared prefs doesn't support doubles
		putDouble(editor, Constants.SP_CLEANING_TEMP, cycle.getTemperature());
		putDouble(editor, Constants.SP_CLEANING_VOL, cycle.getVolume());

		editor.apply();
	}

	/**
	 * Write a double to long bits in shared preferences
	 * 
	 * @param editor
	 *            shared preferences editor
	 * @param key
	 *            key to write to
	 * @param value
	 *            double to convert to long bits
	 */
	private static void putDouble(final Editor editor, final String key,
			final Double value) {
		if (value != null) {
			editor.putLong(key, Double.doubleToRawLongBits(value));
		}
	}

	/**
	 * Get a machine settings object that contains the machine settings
	 * currently in the shared preferences
	 * 
	 * @param context
	 *            app context
	 * @return a machine settings object with the currently stored machine
	 *         settings
	 */
	public static CleaningCycle getCleaningCycleFromSharedPreferences(
			Context context) {
		SharedPreferences sharedPreferences = SteampunkUtils
				.getSteampunkSharedPreferences(context);

		Double temperature = getDouble(sharedPreferences, Constants.SP_CLEANING_TEMP);
		Double volume = getDouble(sharedPreferences, Constants.SP_CLEANING_VOL);
		
		CleaningCycle cleaningCycle = new CleaningCycle(temperature, volume);

		return cleaningCycle;
	}


	/**
	 * Get a double from long bits in shared preferences
	 * 
	 * @param sharedPreferences
	 *            the shared preferences to read from
	 * @param key
	 *            the key to get
	 * @return the double for the corresponding key or null if shared
	 *         preferences doesn't have that key
	 */
	private static Double getDouble(final SharedPreferences sharedPreferences,
			final String key) {
		return sharedPreferences.contains(key) ? Double
				.longBitsToDouble(sharedPreferences.getLong(key, 0)) : null;
	}

	public Double getTemperature() {
		return mTemperature;
	}

	public Double getVolume() {
		return mVolume;
	}
}