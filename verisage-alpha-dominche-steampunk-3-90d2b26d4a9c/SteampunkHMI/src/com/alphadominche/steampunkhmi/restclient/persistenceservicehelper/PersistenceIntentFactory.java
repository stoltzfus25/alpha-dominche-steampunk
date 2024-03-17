package com.alphadominche.steampunkhmi.restclient.persistenceservicehelper;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;

import com.alphadominche.steampunkhmi.SPTempUnitType;
import com.alphadominche.steampunkhmi.SPVolumeUnitType;
import com.alphadominche.steampunkhmi.database.tables.FavoriteTable;
import com.alphadominche.steampunkhmi.database.tables.LogTable;
import com.alphadominche.steampunkhmi.database.tables.RecipeTable;
import com.alphadominche.steampunkhmi.restclient.presistenceservice.PersistenceService;
import com.alphadominche.steampunkhmi.utils.Constants;

public class PersistenceIntentFactory {

	private Context mContext;

	public PersistenceIntentFactory(Context context) {
		mContext = context;
	}

	/**
	 * Makes an intent for saving a recipe in the persistence service
	 * 
	 * @param localId
	 * @param name
	 * @param type
	 * @param published
	 * @param grams
	 * @param teaspoons
	 * @param grind
	 * @param filter
	 * @param stacks a sparse array of stacks where the dictionary holds simple attributes and agitation cycles which are stored in a similar manner
	 * @return
	 */
	public Intent makeSaveRecipeIntent(long id, String name, int type,
			boolean published, double grams, double teaspoons, double grind,
			int filter, String stacks) {
		Intent createRecipeIntent = makeRecipeIntent(id, name, type,
				published, grams, teaspoons, grind, filter, stacks);

		createRecipeIntent.putExtra(RecipeTable.ID, id);
		createRecipeIntent.setAction(PersistenceService.ACTION_SAVE_RECIPE);

		return createRecipeIntent;
	}
	
	public Intent makeCreateFavoriteIntent(long userId, long recipeId) {
		Intent createFavIntent = new Intent(mContext, PersistenceService.class);
		
		createFavIntent.setAction(PersistenceService.ACTION_CREATE_FAVORITE);
		
		createFavIntent.putExtra(Constants.SP_USER_ID, userId);
		createFavIntent.putExtra(PersistenceService.BUNDLE_RECIPE_ID, recipeId);
		
		return createFavIntent;
	}

	public Intent makeDeleteFavoriteIntent(long userId, long recipeId) {
		Intent createFavIntent = new Intent(mContext, PersistenceService.class);
		
		createFavIntent.setAction(PersistenceService.ACTION_DELETE_FAVORITE);
		
		createFavIntent.putExtra(Constants.SP_USER_ID, userId);
		createFavIntent.putExtra(PersistenceService.BUNDLE_RECIPE_ID, recipeId);
		
		return createFavIntent;
	}
	
	public Intent makeSyncRoastersIntent(int requestId) {
		Intent getRoastersIntent = new Intent(mContext,
				PersistenceService.class);

		getRoastersIntent.setAction(PersistenceService.ACTION_SYNC_ROASTERS);
		getRoastersIntent.putExtra(Constants.INTENT_EXTRA_REQUEST_ID, requestId);

		return getRoastersIntent;
	}
	
	public Intent makeSyncRecipeIntent() {
		Intent syncRecipeIntent = new Intent(mContext,
				PersistenceService.class);

		syncRecipeIntent.setAction(PersistenceService.ACTION_SYNC_RECIPES);

		return syncRecipeIntent;
	}

	private Intent makeRecipeIntent(long id, String name, int type,
			boolean published, double grams, double teaspoons, double grind,
			int filter, String stacks) {
		Intent recipeIntent = new Intent(mContext,
				PersistenceService.class);

		recipeIntent.putExtra(RecipeTable.ID, id);
		recipeIntent.putExtra(RecipeTable.NAME, name);
		recipeIntent.putExtra(RecipeTable.TYPE, type);
		recipeIntent.putExtra(RecipeTable.PUBLISHED, published);
		recipeIntent.putExtra(RecipeTable.GRAMS, grams);
		recipeIntent.putExtra(RecipeTable.TEASPOONS, teaspoons);
		recipeIntent.putExtra(RecipeTable.GRIND, grind);
		recipeIntent.putExtra(RecipeTable.FILTER, filter);
		recipeIntent.putExtra(RecipeTable.STACKS, stacks);

		return recipeIntent;
	}

	public Intent makeDeleteRecipeIntent(long id) {
		Intent deleteRecipeIntent = new Intent(mContext,
				PersistenceService.class);

		deleteRecipeIntent.setAction(PersistenceService.ACTION_DELETE_RECIPE);

		deleteRecipeIntent.putExtra(RecipeTable.ID, id);

		return deleteRecipeIntent;
	}

	public Intent makeCreateLogIntent(Long userId,
			Long machineId, String date, String recipeId,
			Integer crucibleIndex, Integer severity, Integer type,
			String message) {
		Intent createLogIntent = new Intent(mContext, PersistenceService.class);

		createLogIntent.setAction(PersistenceService.ACTION_CREATE_LOG);

		createLogIntent.putExtra(LogTable.MACHINE, machineId);
		createLogIntent.putExtra(LogTable.DATE, date);
		createLogIntent.putExtra(LogTable.SEVERITY, severity);
		createLogIntent.putExtra(LogTable.TYPE, type);

		if (userId != null) {
			createLogIntent.putExtra(Constants.INTENT_EXTRA_USER_ID, userId);
		}
		if (recipeId != null) {
			createLogIntent.putExtra(LogTable.RECIPE_ID, recipeId);
		}
		if (crucibleIndex != null) {
			createLogIntent.putExtra(LogTable.CRUCIBLE, crucibleIndex);
		}
		if (message != null) {
			createLogIntent.putExtra(LogTable.MESSAGE, message);
		}

		return createLogIntent;
	}
	public Intent makeGetMachineSettingsIntent(String serialNum) {
		Intent getMachineSettingsIntent = new Intent(mContext,
				PersistenceService.class);

		getMachineSettingsIntent
				.setAction(PersistenceService.ACTION_GET_MACHINE_SETTINGS);
		getMachineSettingsIntent.putExtra(
				Constants.MACHINE_SETTINGS_SERIAL_NUMBER, serialNum);
		return getMachineSettingsIntent;
	}
	
	public Intent makeSaveMachineSettingsIntent(
			String serialNumber, Double boilerTemp, Double rinseTemp,
			Double rinseVoulme, Double elevation,
			ArrayList<Boolean> crucibleStates, SPTempUnitType tempUnitType,
			SPVolumeUnitType volumeUnitType, boolean localOnly) {
		Intent saveMachineSettingsIntent = new Intent(mContext,
				PersistenceService.class);

		saveMachineSettingsIntent
				.setAction(PersistenceService.ACTION_SAVE_MACHINE_SETTINGS);

		saveMachineSettingsIntent.putExtra(
				Constants.MACHINE_SETTINGS_SERIAL_NUMBER, serialNumber);
		saveMachineSettingsIntent.putExtra(
				Constants.MACHINE_SETTINGS_BOILER_TEMP, boilerTemp);
		saveMachineSettingsIntent.putExtra(
				Constants.MACHINE_SETTINGS_RINSE_TEMP, rinseTemp);
		saveMachineSettingsIntent.putExtra(
				Constants.MACHINE_SETTINGS_RINSE_VOLUME, rinseVoulme);
		saveMachineSettingsIntent.putExtra(
				Constants.MACHINE_SETTINGS_ELEVATION, elevation);
		saveMachineSettingsIntent.putExtra(
				Constants.MACHINE_SETTINGS_CRUCIBLE_STATES, crucibleStates);
		saveMachineSettingsIntent.putExtra(
				Constants.MACHINE_SETTINGS_TEMP_UNIT_TYPE, tempUnitType);
		saveMachineSettingsIntent.putExtra(
				Constants.MACHINE_SETTINGS_VOLUME_UNIT_TYPE, volumeUnitType);
		saveMachineSettingsIntent.putExtra(
				Constants.MACHINE_SETTINGS_LOCAL_ONLY, localOnly);

		return saveMachineSettingsIntent;
	}

	public Intent makeSaveAccountSettingsIntent(String username,
			String email, String address, String city, String state, 
			String country, String zipCode, Boolean protectRecipes) {
		Intent saveAccountSettingsIntent = new Intent(mContext,
				PersistenceService.class);

		saveAccountSettingsIntent
				.setAction(PersistenceService.ACTION_SAVE_USER_SETTINGS);

		saveAccountSettingsIntent.putExtra(Constants.USER_SETTINGS_USERNAME,
				username);
		saveAccountSettingsIntent
				.putExtra(Constants.USER_SETTINGS_EMAIL, email);
		saveAccountSettingsIntent.putExtra(Constants.USER_SETTINGS_ADDRESS,
				address);
		saveAccountSettingsIntent.putExtra(Constants.USER_SETTINGS_CITY, city);
		saveAccountSettingsIntent
				.putExtra(Constants.USER_SETTINGS_STATE, state);
		saveAccountSettingsIntent
				.putExtra(Constants.USER_SETTINGS_COUNTRY, country);
		saveAccountSettingsIntent.putExtra(Constants.USER_SETTINGS_ZIP_CODE,
				zipCode);
		saveAccountSettingsIntent.putExtra(
				Constants.USER_SETTINGS_PROTECT_RECIPES, protectRecipes);

		return saveAccountSettingsIntent;
	}

	public Intent makeSyncFavoritesIntent(long userId) {
		Intent syncFavoritesIntent = new Intent(mContext, PersistenceService.class);
		
		syncFavoritesIntent.setAction(PersistenceService.ACTION_SYNC_FAVORITES);
		syncFavoritesIntent.putExtra(FavoriteTable.USER, userId);
		
		return syncFavoritesIntent;
	}
	
	public Intent makePasswordResetIntent(int requestId, String identifier) {
		Intent pwResetIntent = new Intent(mContext, PersistenceService.class);
		
		pwResetIntent.setAction(PersistenceService.ACTION_RESET_PASSWORD);
		pwResetIntent.putExtra(Constants.INTENT_EXTRA_REQUEST_ID, requestId);
		pwResetIntent.putExtra(Constants.PW_RESET_IDENTIFIER, identifier);
		
		return pwResetIntent;
	}

	public Intent makeUpdatesIntent(int requestId, int version, int device) {
		Intent updatesIntent = new Intent(mContext, PersistenceService.class);
		
		updatesIntent.setAction(PersistenceService.ACTION_CHECK_FOR_UPDATE);
		updatesIntent.putExtra(Constants.INTENT_EXTRA_REQUEST_ID, requestId);
		updatesIntent.putExtra(Constants.INTENT_VERSION, version);
		updatesIntent.putExtra(Constants.INTENT_DEVICE, device);
		
		return updatesIntent;
	}
	
	public Intent makeDownloadUpdateIntent(int requestId, int newVersion) {
		Intent downloadUpdateIntent = new Intent(mContext, PersistenceService.class);
		
		downloadUpdateIntent.setAction(PersistenceService.ACTION_DOWNLOAD_UPDATE);
		downloadUpdateIntent.putExtra(Constants.INTENT_EXTRA_REQUEST_ID, requestId);
		downloadUpdateIntent.putExtra(Constants.INTENT_VERSION, newVersion);
		
		return downloadUpdateIntent;
	}
	
	public Intent makeNewDeviceIntent(int requestId, int version, String name) {
		Intent newDeviceIntent = new Intent(mContext, PersistenceService.class);
		
		newDeviceIntent.setAction(PersistenceService.ACTION_CREATE_DEVICE);
		newDeviceIntent.putExtra(Constants.INTENT_EXTRA_REQUEST_ID, requestId);
		newDeviceIntent.putExtra(Constants.INTENT_VERSION, version);
		newDeviceIntent.putExtra(Constants.INTENT_NAME, name);
		
		return newDeviceIntent;
	}
	
	public Intent makeResetPINIntent(int requestId, Long machineId, String PIN) {
		Intent pinIntent = new Intent(mContext, PersistenceService.class);
		
		pinIntent.setAction(PersistenceService.ACTION_CHANGE_PIN);
		pinIntent.putExtra(Constants.INTENT_EXTRA_REQUEST_ID, requestId);
		pinIntent.putExtra(Constants.INTENT_MACHINE_ID, machineId);
		pinIntent.putExtra(Constants.INTENT_PIN, PIN);
		
		return pinIntent;
	}

	public Intent makeSubscribeIntent(long SteampunkUserId) {
		
		Intent subscribeIntent= new Intent(mContext, PersistenceService.class);
		
		subscribeIntent.setAction(PersistenceService.ACTION_SUBSCRIBE_TO_ROASTER);
		subscribeIntent.putExtra(Constants.SUBSCRIBE_TO_ID,SteampunkUserId);
		
		return subscribeIntent;
	}
	
	public Intent makeChangePassEvent(String old, String newPass) {
		Intent changePassIntent= new Intent(mContext, PersistenceService.class);
		
		changePassIntent.putExtra(Constants.SP_OLD_PASSWORD, old);
		changePassIntent.putExtra(Constants.SP_NEW_PASSWORD, newPass);

		changePassIntent.setAction(PersistenceService.ACTION_CHANGE_PASSWORD);
		
		return changePassIntent;
	}
	
	public Intent makeEnableNetworkingIntent() {
		Intent netIntent = new Intent(mContext, PersistenceService.class);
		netIntent.setAction(PersistenceService.ACTION_ENABLE_NETWORK_CONNECTION);
		return netIntent;
	}
	
	public Intent makeDisableNetworkingIntent() {
		Intent netIntent = new Intent(mContext, PersistenceService.class);
		netIntent.setAction(PersistenceService.ACTION_DISABLE_NETWORK_CONNECTION);
		return netIntent;
	}
}
