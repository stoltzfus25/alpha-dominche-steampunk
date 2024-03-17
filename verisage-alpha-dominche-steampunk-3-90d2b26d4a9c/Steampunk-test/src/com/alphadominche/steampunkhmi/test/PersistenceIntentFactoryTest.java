package com.alphadominche.steampunkhmi.test;

import java.util.ArrayList;

import android.content.Intent;
import android.test.AndroidTestCase;

import com.alphadominche.steampunkhmi.SPTempUnitType;
import com.alphadominche.steampunkhmi.SPVolumeUnitType;
import com.alphadominche.steampunkhmi.database.tables.LogTable;
import com.alphadominche.steampunkhmi.database.tables.RecipeTable;
import com.alphadominche.steampunkhmi.restclient.persistenceservicehelper.PersistenceIntentFactory;
import com.alphadominche.steampunkhmi.restclient.presistenceservice.PersistenceService;
import com.alphadominche.steampunkhmi.utils.Constants;

public class PersistenceIntentFactoryTest extends AndroidTestCase {

	private PersistenceIntentFactory mPersistenceIntentFactory;

	@Override
	protected void setUp() throws Exception {
		mPersistenceIntentFactory = new PersistenceIntentFactory(getContext());
		super.setUp();
	}

	public void testMakeCreateRecipeIntent() {
		int expectedRequestId = 1;
		String expectedName = "Test Recipe";
		int expectedType = 1;
		boolean expectedPublished = false;
		double expectedGrams = 3.0;
		double expectedTeaspoons = 7.0;
		double expectedGrind = 2.5;
		int expectedFilter = 1;
		String expectedStacks = "";

		int expectedBundleSize = 8;

		Intent testIntent = mPersistenceIntentFactory.makeSaveRecipeIntent(
				expectedRequestId, expectedName, expectedType,
				expectedPublished, expectedGrams, expectedTeaspoons,
				expectedGrind, expectedFilter, expectedStacks);

		String actualAction = testIntent.getAction();
		int actualRequestId = testIntent.getIntExtra(
				Constants.INTENT_EXTRA_REQUEST_ID, -1);
		String actualName = testIntent.getStringExtra(RecipeTable.NAME);
		int actualType = testIntent.getIntExtra(RecipeTable.TYPE, -1);
		boolean actualPublished = testIntent.getBooleanExtra(
				RecipeTable.PUBLISHED, true);
		double actualGrams = testIntent.getDoubleExtra(RecipeTable.GRAMS, -1);
		double actualTeaspoons = testIntent.getDoubleExtra(
				RecipeTable.TEASPOONS, -1);
		double actualGrind = testIntent.getDoubleExtra(RecipeTable.GRIND, -1);
		int actualFilter = testIntent.getIntExtra(RecipeTable.FILTER, -1);

		assertEquals(PersistenceService.class.getName(), testIntent
				.getComponent().getClassName());
		assertEquals(PersistenceService.ACTION_CREATE_RECIPE, actualAction);
		assertEquals(expectedBundleSize, testIntent.getExtras().size());
		assertEquals(expectedRequestId, actualRequestId);
		assertEquals(expectedName, actualName);
		assertEquals(expectedType, actualType);
		assertEquals(expectedPublished, actualPublished);
		assertEquals(expectedGrams, actualGrams);
		assertEquals(expectedTeaspoons, actualTeaspoons);
		assertEquals(expectedGrind, actualGrind);
		assertEquals(expectedFilter, actualFilter);
	}

//	public void testMakeUpdateRecipeIntent() {
//		int expectedRequestId = 2;
//		long expectedRecipeId = 3;
//		String expectedName = "Test Recipe";
//		int expectedType = 2;
//		boolean expectedPublished = false;
//		double expectedGrams = 3.0;
//		double expectedTeaspoons = 7.0;
//		double expectedGrind = 2.5;
//		int expectedFilter = 1;
//
//		int expectedBundleSize = 9;
//
//		Intent testIntent = mPersistenceIntentFactory.makeUpdateRecipeIntent(
//				expectedRequestId, expectedRecipeId, expectedName,
//				expectedType, expectedPublished, expectedGrams,
//				expectedTeaspoons, expectedGrind, expectedFilter);
//
//		String actualAction = testIntent.getAction();
//		int actualRequestId = testIntent.getIntExtra(
//				Constants.INTENT_EXTRA_REQUEST_ID, -1);
//		long actualRecipeId = testIntent.getLongExtra(RecipeTable.ID, -1);
//		String actualName = testIntent.getStringExtra(RecipeTable.NAME);
//		int actualType = testIntent.getIntExtra(RecipeTable.TYPE, -1);
//		boolean actualPublished = testIntent.getBooleanExtra(
//				RecipeTable.PUBLISHED, true);
//		double actualGrams = testIntent.getDoubleExtra(RecipeTable.GRAMS, -1);
//		double actualTeaspoons = testIntent.getDoubleExtra(
//				RecipeTable.TEASPOONS, -1);
//		double actualGrind = testIntent.getDoubleExtra(RecipeTable.GRIND, -1);
//		int actualtFilterId = testIntent.getIntExtra(RecipeTable.FILTER, -1);
//
//		assertEquals(PersistenceService.class.getName(), testIntent
//				.getComponent().getClassName());
//		assertEquals(PersistenceService.ACTION_UPDATE_RECIPE, actualAction);
//		assertEquals(expectedBundleSize, testIntent.getExtras().size());
//		assertEquals(expectedRequestId, actualRequestId);
//		assertEquals(expectedRecipeId, actualRecipeId);
//		assertEquals(expectedName, actualName);
//		assertEquals(expectedType, actualType);
//		assertEquals(expectedPublished, actualPublished);
//		assertEquals(expectedGrams, actualGrams);
//		assertEquals(expectedTeaspoons, actualTeaspoons);
//		assertEquals(expectedGrind, actualGrind);
//		assertEquals(expectedFilter, actualtFilterId);
//	}

	public void testMakeSyncRecipeIntent() {
//		int expectedRequestId = 1;
//		long expectedRecipeId = 1;
		
		Intent testIntent = mPersistenceIntentFactory.makeSyncRecipeIntent();
		String actualAction = testIntent.getAction();
//		int actualRequestId = testIntent.getIntExtra(
//				Constants.INTENT_EXTRA_REQUEST_ID, -1);
		
		assertEquals(PersistenceService.class.getName(), testIntent.getComponent().getClassName());
		assertEquals(PersistenceService.ACTION_SYNC_RECIPES, actualAction);
//		assertEquals(expectedRequestId, actualRequestId);


	}
	public void testMakeDeleteRecipeIntent() {
		int expectedRequestId = 1;
		long expectedRecipeId = 1;

		Intent testIntent = mPersistenceIntentFactory.makeDeleteRecipeIntent(
				expectedRecipeId);

		String actualAction = testIntent.getAction();
		int actualRequestId = testIntent.getIntExtra(
				Constants.INTENT_EXTRA_REQUEST_ID, -1);
		int actualRecipeId = testIntent.getIntExtra(RecipeTable.ID, -1);

		assertEquals(PersistenceService.class.getName(), testIntent
				.getComponent().getClassName());
		assertEquals(PersistenceService.ACTION_DELETE_RECIPE, actualAction);
		assertEquals(expectedRequestId, actualRequestId);
		assertEquals(expectedRecipeId, actualRecipeId);

	}

//	public void testSyncGrindsIntent() {
//		int expectedRequestId = 1;
//
//		Intent testIntent = mPersistenceIntentFactory
//				.makeSyncGrindIntent(expectedRequestId);
//
//		String actualAction = testIntent.getAction();
//		int actualRequestId = testIntent.getIntExtra(
//				Constants.INTENT_EXTRA_REQUEST_ID, -1);
//
//		assertEquals(PersistenceService.class.getName(), testIntent
//				.getComponent().getClassName());
//		assertEquals(PersistenceService.ACTION_SYNC_GRINDS, actualAction);
//		assertEquals(expectedRequestId, actualRequestId);
//
//	}

	public void testMakeCreateLogIntent() {
		Integer expectedRequestId = 1;
		Long expectedMachineId = 1L;
		Long expectedUserId = 1L;
		String expectedDate = "1970-01-01T00:00:00.000+0000";
		String expectedRecipeId = "_";
		Integer expectedCrudibleIndex = 1;
		Integer expectedSeverity = 1;
		Integer expectedType = 1;
		String expectedMessage = "Something crazy happened";

		Intent testIntent = mPersistenceIntentFactory.makeCreateLogIntent(
				expectedMachineId, expectedUserId,
				expectedDate, expectedRecipeId, expectedCrudibleIndex,
				expectedSeverity, expectedType, expectedMessage);

		String actualAction = testIntent.getAction();
		Integer actualRequestId = testIntent.getIntExtra(
				Constants.INTENT_EXTRA_REQUEST_ID, -1);
		Long actualMachineId = testIntent.getLongExtra(LogTable.MACHINE, -1);
		Long actualUserId = testIntent.getLongExtra(
				Constants.INTENT_EXTRA_USER_ID, -1);
		String actualDate = testIntent.getStringExtra(LogTable.DATE);
		String actualRecipeId = testIntent.getStringExtra(LogTable.RECIPE_ID);
		Integer actualCrudibleIndex = testIntent.getIntExtra(LogTable.CRUCIBLE,
				-1);
		Integer actualSeverity = testIntent.getIntExtra(LogTable.SEVERITY, -1);
		Integer actualType = testIntent.getIntExtra(LogTable.TYPE, -1);
		String actualMessage = testIntent.getStringExtra(LogTable.MESSAGE);

		assertEquals(PersistenceService.class.getName(), testIntent
				.getComponent().getClassName());
		assertEquals(PersistenceService.ACTION_CREATE_LOG, actualAction);
		assertEquals(9, testIntent.getExtras().size());
		assertEquals(expectedRequestId, actualRequestId);
		assertEquals(expectedUserId, actualUserId);
		assertEquals(expectedMachineId, actualMachineId);
		assertEquals(expectedDate, actualDate);
		assertEquals(expectedRecipeId, actualRecipeId);
		assertEquals(expectedCrudibleIndex, actualCrudibleIndex);
		assertEquals(expectedSeverity, actualSeverity);
		assertEquals(expectedType, actualType);
		assertEquals(expectedMessage, actualMessage);
	}

	public void testMakeSaveMachineSettingsIntent() {
		ArrayList<Boolean> expectedCrucibleStates = new ArrayList<Boolean>();
		expectedCrucibleStates.add(true);
		expectedCrucibleStates.add(true);
		expectedCrucibleStates.add(false);
		expectedCrucibleStates.add(false);

		testMakeSaveMachineSettingsIntentWithValues(1, "1234ABC", 212.0, 150.0,
				20.0, 1110.0, expectedCrucibleStates, SPTempUnitType.FAHRENHEIT,
				SPVolumeUnitType.OUNCES, true);
		testMakeSaveMachineSettingsIntentWithValues(1, "1234ABC", null, 150.0,
				20.0, 1110.0, expectedCrucibleStates, SPTempUnitType.FAHRENHEIT,
				SPVolumeUnitType.OUNCES, true);
		testMakeSaveMachineSettingsIntentWithValues(1, "1234ABC", null, null,
				20.0, 1110.0, expectedCrucibleStates, SPTempUnitType.FAHRENHEIT,
				SPVolumeUnitType.OUNCES, true);
		testMakeSaveMachineSettingsIntentWithValues(1, "1234ABC", null, null,
				null, 1110.0, expectedCrucibleStates, SPTempUnitType.FAHRENHEIT,
				SPVolumeUnitType.OUNCES, true);
		testMakeSaveMachineSettingsIntentWithValues(1, "1234ABC", null, null,
				null, null, expectedCrucibleStates, SPTempUnitType.FAHRENHEIT,
				SPVolumeUnitType.OUNCES, true);
		testMakeSaveMachineSettingsIntentWithValues(1, "1234ABC", null, null,
				null, null, null, SPTempUnitType.FAHRENHEIT,
				SPVolumeUnitType.OUNCES, true);
		testMakeSaveMachineSettingsIntentWithValues(1, "1234ABC", null, null,
				null, null, null, null, SPVolumeUnitType.OUNCES, true);
		testMakeSaveMachineSettingsIntentWithValues(1, "1234ABC", null, null,
				null, null, null, null, null, true);

	}

	private void testMakeSaveMachineSettingsIntentWithValues(
			Integer expectedRequestId, String expectedSerialNumber,
			Double expectedBoilerTemp, Double expectedRinseTemp,
			Double expectedRinseVolume, Double expectedElevation,
			ArrayList<Boolean> expectedCrucibleStates,
			SPTempUnitType expectedTempUnitType,
			SPVolumeUnitType expectedVolumeUnitType,
			boolean expectedLocalOnly) {
		Intent testIntent = mPersistenceIntentFactory
				.makeSaveMachineSettingsIntent(expectedSerialNumber, expectedBoilerTemp,
						expectedRinseTemp, expectedRinseVolume,
						expectedElevation, expectedCrucibleStates,
						expectedTempUnitType, expectedVolumeUnitType, expectedLocalOnly);

		double defaultValueDouble = 0;
		if (expectedBoilerTemp == null)
			expectedBoilerTemp = defaultValueDouble;
		if (expectedRinseTemp == null)
			expectedRinseTemp = defaultValueDouble;
		if (expectedRinseVolume == null)
			expectedRinseVolume = defaultValueDouble;
		if (expectedElevation == null)
			expectedElevation = defaultValueDouble;

		String actualAction = testIntent.getAction();
		Integer actualRequestId = testIntent.getIntExtra(
				Constants.INTENT_EXTRA_REQUEST_ID, 0);
		String actualSerialNumber = testIntent
				.getStringExtra(Constants.MACHINE_SETTINGS_SERIAL_NUMBER);
		Double actualBoilerTemp = testIntent
				.hasExtra(Constants.MACHINE_SETTINGS_BOILER_TEMP) ? testIntent
				.getDoubleExtra(Constants.MACHINE_SETTINGS_BOILER_TEMP,
						defaultValueDouble) : null;
		Double actualRinseTemp = testIntent
				.hasExtra(Constants.MACHINE_SETTINGS_RINSE_TEMP) ? testIntent
				.getDoubleExtra(Constants.MACHINE_SETTINGS_RINSE_TEMP,
						defaultValueDouble) : null;
		Double actualRinseVolume = testIntent
				.hasExtra(Constants.MACHINE_SETTINGS_RINSE_VOLUME) ? testIntent
				.getDoubleExtra(Constants.MACHINE_SETTINGS_RINSE_VOLUME,
						defaultValueDouble) : null;
		Double actualElevation = testIntent
				.hasExtra(Constants.MACHINE_SETTINGS_ELEVATION) ? testIntent
				.getDoubleExtra(Constants.MACHINE_SETTINGS_ELEVATION,
						defaultValueDouble) : null;
		// Because of type erasure, I can't do a check on a generic and this is
		// the only way to pass an ArrayList through an intent
		@SuppressWarnings("unchecked")
		ArrayList<Boolean> actualCrucibleStates = (ArrayList<Boolean>) testIntent
				.getSerializableExtra(Constants.MACHINE_SETTINGS_CRUCIBLE_STATES);
		SPTempUnitType actualTempUnitType = (SPTempUnitType) testIntent
				.getSerializableExtra(Constants.MACHINE_SETTINGS_TEMP_UNIT_TYPE);
		SPVolumeUnitType actualVolumeUnitType = (SPVolumeUnitType) testIntent
				.getSerializableExtra(Constants.MACHINE_SETTINGS_VOLUME_UNIT_TYPE);

		assertEquals(PersistenceService.class.getName(), testIntent
				.getComponent().getClassName());
		assertEquals(PersistenceService.ACTION_SAVE_MACHINE_SETTINGS,
				actualAction);
		assertEquals(expectedRequestId, actualRequestId);
		assertEquals(9, testIntent.getExtras().size());
		assertEquals(expectedSerialNumber, actualSerialNumber);
		assertEquals(expectedBoilerTemp, actualBoilerTemp);
		assertEquals(expectedRinseTemp, actualRinseTemp);
		assertEquals(expectedRinseVolume, actualRinseVolume);
		assertEquals(expectedElevation, actualElevation);
		assertEquals(expectedCrucibleStates, actualCrucibleStates);
		assertEquals(expectedTempUnitType, actualTempUnitType);
		assertEquals(expectedVolumeUnitType, actualVolumeUnitType);
	}

}
