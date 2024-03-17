package com.alphadominche.steampunkhmi.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class AccountSettings {
	private String username;
	private String email;
	private String address;
	private String city;
	private String state;
	private String country;
	private String zipCode;
	private Boolean protectRecipes;

	public AccountSettings(String username, String email, String address,
			String city, String state, String country, String zipCode, Boolean protectRecipes) {
		this.username = username;
		this.email = email;
		this.address = address;
		this.city = city;
		this.state = state;
		this.country = country;
		this.zipCode = zipCode;
		this.protectRecipes = protectRecipes;
	}

	public static void writeAccountSettingsToSharedPreferences(
			AccountSettings accountSettings, Context context) {
		Editor editor = SteampunkUtils.getSteampunkSharedPreferences(context)
				.edit();

		editor.putString(Constants.SP_USERNAME, accountSettings.getUsername());
		editor.putString(Constants.SP_EMAIL, accountSettings.getEmail());
		editor.putString(Constants.SP_ADDRESS, accountSettings.getAddress());
		editor.putString(Constants.SP_CITY, accountSettings.getCity());
		editor.putString(Constants.SP_STATE, accountSettings.getState());
		editor.putString(Constants.SP_COUNTRY,  accountSettings.getCountry());
		editor.putString(Constants.SP_ZIP_CODE, accountSettings.getZipCode());
		if (accountSettings.getProtectRecipes() != null) {
			editor.putBoolean(Constants.SP_PROTECT_RECIPES,
					accountSettings.getProtectRecipes());
		}

		editor.apply();
	}

	public static AccountSettings getAccountSettingsFromSharedPreferences(
			Context context) {
		SharedPreferences sharedPreferences = SteampunkUtils
				.getSteampunkSharedPreferences(context);

		String username = sharedPreferences.getString(Constants.SP_USERNAME,
				null);
		String email = sharedPreferences.getString(Constants.SP_EMAIL, null);
		String address = sharedPreferences
				.getString(Constants.SP_ADDRESS, null);
		String city = sharedPreferences.getString(Constants.SP_CITY, null);
		String state = sharedPreferences.getString(Constants.SP_STATE, null);
		String country = sharedPreferences.getString(Constants.SP_COUNTRY, null);
		String zipCode = sharedPreferences.getString(Constants.SP_ZIP_CODE,
				null);
		Boolean protectRecipes = sharedPreferences.getBoolean(
				Constants.SP_PROTECT_RECIPES, true);

		return new AccountSettings(username, email, address, city, state,
				country, zipCode, protectRecipes);
	}

	public String getUsername() {
		return username;
	}

	public String getEmail() {
		return email;
	}

	public String getAddress() {
		return address;
	}

	public String getCity() {
		return city;
	}

	public String getState() {
		return state;
	}
	
	public String getCountry() {
		return country;
	}

	public String getZipCode() {
		return zipCode;
	}

	public Boolean getProtectRecipes() {
		return protectRecipes;
	}
}