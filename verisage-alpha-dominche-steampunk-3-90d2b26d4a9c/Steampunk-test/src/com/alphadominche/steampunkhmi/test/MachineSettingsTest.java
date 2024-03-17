package com.alphadominche.steampunkhmi.test;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.test.AndroidTestCase;

import com.alphadominche.steampunkhmi.SPTempUnitType;
import com.alphadominche.steampunkhmi.SPVolumeUnitType;
import com.alphadominche.steampunkhmi.utils.Constants;
import com.alphadominche.steampunkhmi.utils.MachineSettings;

public class MachineSettingsTest extends AndroidTestCase {

	@Override
	protected void setUp() throws Exception {
		getContext()
				.getSharedPreferences(Constants.SHARED_PREFS_NAME,
						Context.MODE_PRIVATE).edit().clear().apply();
		super.setUp();
	}

	public void testConstructorGettersAndSetters() {
		Long expectedId = 1L;
		String expectedSerialNumber = "1234ABC";
		Double expectedBoilerTemp = 212.0;
		Double expectedRinseTemp = 150.0;
		Double expectedRinseVolume = 20.0;
		Double expectedElevation = 1100.0;
		Integer expectedCrucibleCount = 4;
		List<Boolean> expectedCrucibleStates = new ArrayList<Boolean>();
		expectedCrucibleStates.add(true);
		expectedCrucibleStates.add(true);
		expectedCrucibleStates.add(false);
		expectedCrucibleStates.add(false);
		SPTempUnitType expectedTempUnitType = SPTempUnitType.FAHRENHEIT;
		SPVolumeUnitType expectedVolumeUnitType = SPVolumeUnitType.OUNCES;
		boolean expectedLocalOnly = false;

		MachineSettings testMachineSettings = new MachineSettings(expectedId,
				expectedSerialNumber, expectedBoilerTemp, expectedRinseTemp,
				expectedRinseVolume, expectedElevation, expectedCrucibleCount,
				expectedCrucibleStates, expectedTempUnitType,
				expectedVolumeUnitType, expectedLocalOnly);

		assertEquals(expectedSerialNumber,
				testMachineSettings.getSerialNumber());
		assertEquals(expectedBoilerTemp, testMachineSettings.getBoilerTemp());
		assertEquals(expectedRinseTemp, testMachineSettings.getRinseTemp());
		assertEquals(expectedRinseVolume, testMachineSettings.getRinseVolume());
		assertEquals(expectedElevation, testMachineSettings.getElevation());
		assertEquals(expectedCrucibleCount,
				testMachineSettings.getCrucibleCount());
		assertEquals(expectedCrucibleStates,
				testMachineSettings.getCrucibleStates());
		assertEquals(expectedTempUnitType,
				testMachineSettings.getTempUnitType());
		assertEquals(expectedVolumeUnitType,
				testMachineSettings.getVolumeUnitType());

		expectedId = 2L;
		testMachineSettings.setId(expectedId);

		assertEquals(expectedId, testMachineSettings.getId());

		expectedCrucibleCount = 2;
		testMachineSettings.setCrucibleCount(expectedCrucibleCount);

		assertEquals(expectedCrucibleCount,
				testMachineSettings.getCrucibleCount());
	}

	public void testWriteMachineSettingsToSharedPreferences() {
		List<Boolean> expectedCrucibleStates = new ArrayList<Boolean>();
		expectedCrucibleStates.add(true);
		expectedCrucibleStates.add(true);
		expectedCrucibleStates.add(false);
		expectedCrucibleStates.add(false);

		testWriteMachineSettingsToSharedPreferencesWithValues(1L, "1234ABC",
				212.0, 150.0, 20.0, 1100.0, 4, expectedCrucibleStates,
				SPTempUnitType.FAHRENHEIT, SPVolumeUnitType.OUNCES, true);
		testWriteMachineSettingsToSharedPreferencesWithValues(1L, "1234ABC",
				null, 150.0, 20.0, 1100.0, 4, expectedCrucibleStates,
				SPTempUnitType.FAHRENHEIT, SPVolumeUnitType.OUNCES, true);
		testWriteMachineSettingsToSharedPreferencesWithValues(1L, "1234ABC",
				null, null, 20.0, 1100.0, 4, expectedCrucibleStates,
				SPTempUnitType.FAHRENHEIT, SPVolumeUnitType.OUNCES, true);
		testWriteMachineSettingsToSharedPreferencesWithValues(1L, "1234ABC",
				null, null, null, 1100.0, 4, expectedCrucibleStates,
				SPTempUnitType.FAHRENHEIT, SPVolumeUnitType.OUNCES, true);
		testWriteMachineSettingsToSharedPreferencesWithValues(1L, "1234ABC",
				null, null, null, null, null, expectedCrucibleStates,
				SPTempUnitType.FAHRENHEIT, SPVolumeUnitType.OUNCES, true);
		testWriteMachineSettingsToSharedPreferencesWithValues(1L, "1234ABC",
				null, null, null, null, null, null, SPTempUnitType.FAHRENHEIT,
				SPVolumeUnitType.OUNCES, true);
		testWriteMachineSettingsToSharedPreferencesWithValues(1L, "1234ABC",
				null, null, null, null, null, null, null,
				SPVolumeUnitType.OUNCES, true);
		testWriteMachineSettingsToSharedPreferencesWithValues(1L, "1234ABC",
				null, null, null, null, null, null, null, null, true);
		testWriteMachineSettingsToSharedPreferencesWithValues(1L, null, null,
				null, null, null, null, null, null, null, true);
		testWriteMachineSettingsToSharedPreferencesWithValues(null, null, null,
				null, null, null, null, null, null, null, true);
	}

	private void testWriteMachineSettingsToSharedPreferencesWithValues(
			Long expectedId, String expectedSerialNumber,
			Double expectedBoilerTemp, Double expectedRinseTemp,
			Double expectedRinseVolume, Double expectedElevation,
			Integer expectedCrucibleCount,
			List<Boolean> expectedCrucibleStates,
			SPTempUnitType expectedTempUnitType,
			SPVolumeUnitType expectedVolumeUnitType,
			boolean expectedLocalOnly) {
		getContext()
				.getSharedPreferences(Constants.SHARED_PREFS_NAME,
						Context.MODE_PRIVATE).edit().clear().apply();
		MachineSettings testMachineSettings = new MachineSettings(expectedId,
				expectedSerialNumber, expectedBoilerTemp, expectedRinseTemp,
				expectedRinseVolume, expectedElevation, expectedCrucibleCount,
				expectedCrucibleStates, expectedTempUnitType,
				expectedVolumeUnitType, expectedLocalOnly);

		long expectedBoilerTempLong = (expectedBoilerTemp != null) ? Double
				.doubleToRawLongBits(expectedBoilerTemp) : 0;
		long expectedRinseTempLong = (expectedRinseTemp != null) ? Double
				.doubleToRawLongBits(expectedRinseTemp) : 0;
		long expectedRinseVolumeLong = (expectedRinseVolume != null) ? Double
				.doubleToRawLongBits(expectedRinseVolume) : 0;
		long expectedElevationLong = (expectedElevation != null) ? Double
				.doubleToRawLongBits(expectedElevation) : 0;
		String expectedCrucibleStatesJSONString = expectedCrucibleStates != null ? new JSONArray(
				expectedCrucibleStates).toString() : null;
		int expectedTempUnitTypeInt = (expectedTempUnitType != null) ? expectedTempUnitType
				.ordinal() : 0;
		int expectedVolumeUnitTypeInt = (expectedVolumeUnitType != null) ? expectedVolumeUnitType
				.ordinal() : 0;

		MachineSettings.writeMachineSettingsToSharedPreferences(
				testMachineSettings, getContext());

		SharedPreferences sharedPreferences = getContext()
				.getSharedPreferences(Constants.SHARED_PREFS_NAME,
						Context.MODE_PRIVATE);

		Long actualId = sharedPreferences.contains(Constants.SP_MACHINE_ID) ? sharedPreferences
				.getLong(Constants.SP_MACHINE_ID, 0) : null;
		String actualSerialNumber = sharedPreferences.getString(
				Constants.SP_MACHINE_SERIAL_NUMBER, null);
		long actualBoilerTempLong = sharedPreferences.getLong(
				Constants.SP_BOILER_TEMP, 0);
		long actualRinseTempLong = sharedPreferences.getLong(
				Constants.SP_RINSE_TEMP, 0);
		long actualRinseVolumeLong = sharedPreferences.getLong(
				Constants.SP_RINSE_VOLUME, 0);
		long actualElevationLong = sharedPreferences.getLong(
				Constants.SP_ELEVATION, 0);
		Integer actualCrucibleCount = sharedPreferences
				.contains(Constants.SP_CRUCIBLE_COUNT) ? sharedPreferences
				.getInt(Constants.SP_CRUCIBLE_COUNT, 0) : null;
		String actualCrucibleStatesJSONString = sharedPreferences.getString(
				Constants.SP_CRUCIBLE_STATES, null);
		int actualTempUnitTypeInt = sharedPreferences.getInt(
				Constants.SP_TEMP_UNIT_TYPE, 0);
		int actualVolumeUnitTypeInt = sharedPreferences.getInt(
				Constants.SP_VOLUME_UNIT_TYPE, 0);

		assertEquals(expectedId, actualId);
		assertEquals(expectedSerialNumber, actualSerialNumber);
		assertEquals(expectedBoilerTempLong, actualBoilerTempLong);
		assertEquals(expectedRinseTempLong, actualRinseTempLong);
		assertEquals(expectedRinseVolumeLong, actualRinseVolumeLong);
		assertEquals(expectedElevationLong, actualElevationLong);
		assertEquals(expectedCrucibleCount, actualCrucibleCount);
		assertEquals(expectedCrucibleStatesJSONString,
				actualCrucibleStatesJSONString);
		assertEquals(expectedTempUnitTypeInt, actualTempUnitTypeInt);
		assertEquals(expectedVolumeUnitTypeInt, actualVolumeUnitTypeInt);
	}

	public void testGetMachineSettingsFromSharedPreferences() {
		List<Boolean> expectedCrucibleStates = new ArrayList<Boolean>();
		expectedCrucibleStates.add(true);
		expectedCrucibleStates.add(true);
		expectedCrucibleStates.add(false);
		expectedCrucibleStates.add(false);

		testGetMachineSettingsFromSharedPreferencesWithValues(1L, "1234ABC",
				212.0, 150.0, 20.0, 1100.0, 4, expectedCrucibleStates,
				SPTempUnitType.FAHRENHEIT, SPVolumeUnitType.OUNCES, true);
		testGetMachineSettingsFromSharedPreferencesWithValues(1L, "1234ABC",
				null, 150.0, 20.0, 1100.0, 4, expectedCrucibleStates,
				SPTempUnitType.FAHRENHEIT, SPVolumeUnitType.OUNCES, true);
		testGetMachineSettingsFromSharedPreferencesWithValues(1L, "1234ABC",
				null, 150.0, 20.0, 1100.0, 4, expectedCrucibleStates,
				SPTempUnitType.FAHRENHEIT, SPVolumeUnitType.OUNCES, true);
		testGetMachineSettingsFromSharedPreferencesWithValues(1L, "1234ABC",
				null, null, 20.0, 1100.0, 4, expectedCrucibleStates,
				SPTempUnitType.FAHRENHEIT, SPVolumeUnitType.OUNCES, true);
		testGetMachineSettingsFromSharedPreferencesWithValues(1L, "1234ABC",
				null, null, null, 1100.0, 4, expectedCrucibleStates,
				SPTempUnitType.FAHRENHEIT, SPVolumeUnitType.OUNCES, true);
		testGetMachineSettingsFromSharedPreferencesWithValues(1L, "1234ABC",
				null, null, null, null, null, expectedCrucibleStates,
				SPTempUnitType.FAHRENHEIT, SPVolumeUnitType.OUNCES, true);
		testGetMachineSettingsFromSharedPreferencesWithValues(1L, "1234ABC",
				null, null, null, null, null, null, SPTempUnitType.FAHRENHEIT,
				SPVolumeUnitType.OUNCES, true);
		testGetMachineSettingsFromSharedPreferencesWithValues(1L, "1234ABC",
				null, null, null, null, null, null, null,
				SPVolumeUnitType.OUNCES, true);
		testGetMachineSettingsFromSharedPreferencesWithValues(1L, "1234ABC",
				null, null, null, null, null, null, null, null, true);
		testGetMachineSettingsFromSharedPreferencesWithValues(1L, null, null,
				null, null, null, null, null, null, null, true);
		testGetMachineSettingsFromSharedPreferencesWithValues(null, null, null,
				null, null, null, null, null, null, null, true);
	}

	private void testGetMachineSettingsFromSharedPreferencesWithValues(
			Long expectedId, String expectedSerialNumber,
			Double expectedBoilerTemp, Double expectedRinseTemp,
			Double expectedRinseVolume, Double expectedElevation,
			Integer expectedCrucibleCount,
			List<Boolean> expectedCrucibleStates,
			SPTempUnitType expectedTempUnitType,
			SPVolumeUnitType expectedVolumeUnitType,
			boolean expectedLocalOnly) {
		getContext()
				.getSharedPreferences(Constants.SHARED_PREFS_NAME,
						Context.MODE_PRIVATE).edit().clear().apply();

		MachineSettings testMachineSettings = new MachineSettings(expectedId,
				expectedSerialNumber, expectedBoilerTemp, expectedRinseTemp,
				expectedRinseVolume, expectedElevation, expectedCrucibleCount,
				expectedCrucibleStates, expectedTempUnitType,
				expectedVolumeUnitType, expectedLocalOnly);

		MachineSettings.writeMachineSettingsToSharedPreferences(
				testMachineSettings, getContext());

		MachineSettings actualMachineSettings = MachineSettings
				.getMachineSettingsFromSharedPreferences(getContext());

		assertEquals(expectedId, actualMachineSettings.getId());
		assertEquals(expectedSerialNumber,
				actualMachineSettings.getSerialNumber());
		assertEquals(expectedBoilerTemp, actualMachineSettings.getBoilerTemp());
		assertEquals(expectedRinseTemp, actualMachineSettings.getRinseTemp());
		assertEquals(expectedRinseVolume,
				actualMachineSettings.getRinseVolume());
		assertEquals(expectedElevation, actualMachineSettings.getElevation());
		assertEquals(expectedCrucibleCount,
				actualMachineSettings.getCrucibleCount());
		assertEquals(expectedCrucibleStates,
				actualMachineSettings.getCrucibleStates());
		assertEquals(expectedTempUnitType,
				actualMachineSettings.getTempUnitType());
		assertEquals(expectedVolumeUnitType,
				actualMachineSettings.getVolumeUnitType());
	}

	public void testGetMachineCrucibleCount() {
		Editor editor = getContext().getSharedPreferences(
				Constants.SHARED_PREFS_NAME, Context.MODE_PRIVATE).edit();

		assertNull(MachineSettings.getMachineCrucibleCount(mContext));

		Integer expectedCrucibleCount = 4;
		editor.putInt(Constants.SP_CRUCIBLE_COUNT, expectedCrucibleCount);
		editor.apply();

		assertEquals(expectedCrucibleCount,
				MachineSettings.getMachineCrucibleCount(mContext));

	}
}
