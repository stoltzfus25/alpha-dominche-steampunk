package com.alphadominche.steampunkhmi.test;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.test.AndroidTestCase;

import com.alphadominche.steampunkhmi.utils.AccountSettings;
import com.alphadominche.steampunkhmi.utils.Constants;

public class AccountSettingsTest extends AndroidTestCase {

	@Override
	protected void setUp() throws Exception {
		getContext()
				.getSharedPreferences(Constants.SHARED_PREFS_NAME,
						Context.MODE_PRIVATE).edit().clear().apply();
		super.setUp();
	}

	public void testConstructorAndGetters() {
		String expectedUsername = "test";
		String expectedEmail = "test@test.com";
		String expectedAddress = "123 Sesame St.";
		String expectedCity = "Provo";
		String expectedState = "UT";
		String expectedCountry = "USA";
		String expectedZipCode = "11111";
		Boolean expectedProtectRecipes = true;

		AccountSettings testAccountSettings = new AccountSettings(
				expectedUsername, expectedEmail, expectedAddress, expectedCity,
				expectedState, expectedCountry, expectedZipCode, expectedProtectRecipes);

		assertEquals(expectedUsername, testAccountSettings.getUsername());
		assertEquals(expectedEmail, testAccountSettings.getEmail());
		assertEquals(expectedAddress, testAccountSettings.getAddress());
		assertEquals(expectedCity, testAccountSettings.getCity());
		assertEquals(expectedState, testAccountSettings.getState());
		assertEquals(expectedCountry, testAccountSettings.getCountry());
		assertEquals(expectedZipCode, testAccountSettings.getZipCode());
		assertEquals(expectedProtectRecipes,
				testAccountSettings.getProtectRecipes());
	}

	public void testWriteUserSettingsToSharedPreferences() {
		testWriteUserSettingsToSharedPreferencesWithValues("test",
				"test@test.com", "123 Sesame St.", "Provo", "UT", "USA", "11111", true);
		testWriteUserSettingsToSharedPreferencesWithValues(null,
				"test@test.com", "123 Sesame St.", "Provo", "UT", "USA", "11111", true);
		testWriteUserSettingsToSharedPreferencesWithValues(null, null,
				"123 Sesame St.", "Provo", "UT", "USA", "11111", true);
		testWriteUserSettingsToSharedPreferencesWithValues(null, null, null,
				"Provo", "UT", "USA", "11111", true);
		testWriteUserSettingsToSharedPreferencesWithValues(null, null, null,
				null, "UT", "USA", "11111", true);
		testWriteUserSettingsToSharedPreferencesWithValues(null, null, null,
				null, "UT", "USA", "11111", true);
		testWriteUserSettingsToSharedPreferencesWithValues(null, null, null,
				null, null, "USA", "11111", true);
		testWriteUserSettingsToSharedPreferencesWithValues(null, null, null,
				null, null, null, "11111", true);
		testWriteUserSettingsToSharedPreferencesWithValues(null, null, null,
				null, null, null, null, true);
		testWriteUserSettingsToSharedPreferencesWithValues(null, null, null,
				null, null, null, null, null);
	}

	private void testWriteUserSettingsToSharedPreferencesWithValues(
			String expectedUsername, String expectedEmail,
			String expectedAddress, String expectedCity, String expectedState,
			String expectedCountry, String expectedZipCode,
			Boolean expectedProtectRecipes) {
		SharedPreferences sharedPreferences = getContext()
				.getSharedPreferences(Constants.SHARED_PREFS_NAME,
						Context.MODE_PRIVATE);

		sharedPreferences.edit().clear().apply();

		AccountSettings accountSettings = new AccountSettings(expectedUsername,
				expectedEmail, expectedAddress, expectedCity, expectedState,
				expectedCountry, expectedZipCode, expectedProtectRecipes);

		AccountSettings.writeAccountSettingsToSharedPreferences(
				accountSettings, getContext());

		String actualUsername = sharedPreferences.getString(
				Constants.SP_USERNAME, null);
		String actualEmail = sharedPreferences.getString(Constants.SP_EMAIL,
				null);
		String actualAddress = sharedPreferences.getString(
				Constants.SP_ADDRESS, null);
		String actualCity = sharedPreferences
				.getString(Constants.SP_CITY, null);
		String actualState = sharedPreferences.getString(Constants.SP_STATE,
				null);
		String actualZipCode = sharedPreferences.getString(
				Constants.SP_ZIP_CODE, null);
		Boolean actualProtectRecipes = sharedPreferences
				.contains(Constants.SP_PROTECT_RECIPES) ? sharedPreferences
				.getBoolean(Constants.SP_PROTECT_RECIPES, false) : null;

		assertEquals(expectedUsername, actualUsername);
		assertEquals(expectedEmail, actualEmail);
		assertEquals(expectedAddress, actualAddress);
		assertEquals(expectedCity, actualCity);
		assertEquals(expectedState, actualState);
		assertEquals(expectedZipCode, actualZipCode);
		assertEquals(expectedProtectRecipes, actualProtectRecipes);
	}

	public void testGetAccountSettingsFromSharedPreferences() {
		testGetAccountSettingsFromSharedPreferencesWithValues("test",
				"test@test.com", "123 Sesame St.", "Provo", "UT", "11111", true);
		testGetAccountSettingsFromSharedPreferencesWithValues(null,
				"test@test.com", "123 Sesame St.", "Provo", "UT", "11111", true);
		testGetAccountSettingsFromSharedPreferencesWithValues(null, null,
				"123 Sesame St.", "Provo", "UT", "11111", true);
		testGetAccountSettingsFromSharedPreferencesWithValues(null, null, null,
				"Provo", "UT", "11111", true);
		testGetAccountSettingsFromSharedPreferencesWithValues(null, null, null,
				null, "UT", "11111", true);
		testGetAccountSettingsFromSharedPreferencesWithValues(null, null, null,
				null, null, "11111", true);
		testGetAccountSettingsFromSharedPreferencesWithValues(null, null, null,
				null, null, null, true);
		testGetAccountSettingsFromSharedPreferencesWithValues(null, null, null,
				null, null, null, null);
	}

	private void testGetAccountSettingsFromSharedPreferencesWithValues(
			String expectedUsername, String expectedEmail,
			String expectedAddress, String expectedCity, String expectedState,
			String expectedZipCode, Boolean expectedProtectRecipes) {
		Editor editor = getContext().getSharedPreferences(
				Constants.SHARED_PREFS_NAME, Context.MODE_PRIVATE).edit();

		editor.clear().apply();

		editor.putString(Constants.SP_USERNAME, expectedUsername);
		editor.putString(Constants.SP_EMAIL, expectedEmail);
		editor.putString(Constants.SP_ADDRESS, expectedAddress);
		editor.putString(Constants.SP_CITY, expectedCity);
		editor.putString(Constants.SP_STATE, expectedState);
		editor.putString(Constants.SP_ZIP_CODE, expectedZipCode);
		if (expectedProtectRecipes != null) {
			editor.putBoolean(Constants.SP_PROTECT_RECIPES,
					expectedProtectRecipes);
		}

		editor.apply();

		AccountSettings testAccountSettings = AccountSettings
				.getAccountSettingsFromSharedPreferences(getContext());

		assertEquals(expectedUsername, testAccountSettings.getUsername());
		assertEquals(expectedEmail, testAccountSettings.getEmail());
		assertEquals(expectedAddress, testAccountSettings.getAddress());
		assertEquals(expectedCity, testAccountSettings.getCity());
		assertEquals(expectedState, testAccountSettings.getState());
		assertEquals(expectedZipCode, testAccountSettings.getZipCode());
	}
}
