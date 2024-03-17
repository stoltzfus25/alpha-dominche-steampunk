package com.alphadominche.steampunkhmi.restclient.persistenceservicehelper;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import com.alphadominche.steampunkhmi.SPLog;
import com.alphadominche.steampunkhmi.SPModel;
import com.alphadominche.steampunkhmi.SPTempUnitType;
import com.alphadominche.steampunkhmi.SPVolumeUnitType;
import com.alphadominche.steampunkhmi.restclient.presistenceservice.PersistenceService;
import com.alphadominche.steampunkhmi.restclient.presistenceservice.PersistenceService.ResultCode;
import com.alphadominche.steampunkhmi.restclient.presistenceservice.PersistenceServiceResultReceiver;
import com.alphadominche.steampunkhmi.restclient.presistenceservice.PersistenceServiceResultReceiver.Receiver;
import com.alphadominche.steampunkhmi.utils.Constants;
import com.alphadominche.steampunkhmi.utils.MachineSettings;
import com.alphadominche.steampunkhmi.utils.SteampunkUtils;

import de.greenrobot.event.EventBus;

/**
 * A singleton implementation of the {@link PersistenceServiceHelper} interface.
 * 
 * @author jnuss
 * 
 */
public class DefaultPersistenceServiceHelper implements
		PersistenceServiceHelper, Receiver {
	private static final DefaultPersistenceServiceHelper INSTANCE = new DefaultPersistenceServiceHelper();
	private Context mCurrentContext;
	private PersistenceIntentFactory mPersistenceIntentFactory;
	private PersistenceServiceResultReceiver mPersistenceServiceResultReceiver;

	public static synchronized DefaultPersistenceServiceHelper getInstance(
			Context newContext) {

		INSTANCE.mCurrentContext = newContext;

		INSTANCE.mPersistenceServiceResultReceiver = new PersistenceServiceResultReceiver(
				new Handler());

		INSTANCE.mPersistenceIntentFactory = new PersistenceIntentFactory(
				newContext);

		INSTANCE.mPersistenceServiceResultReceiver.setReceiver(INSTANCE);

		return INSTANCE;
	}

	@Override
	public void login(String username, String password) {
		// post to the auth url
		Intent loginIntent = new Intent(this.mCurrentContext,
				PersistenceService.class);
		loginIntent.setAction(PersistenceService.ACTION_LOGIN);

		loginIntent.putExtra(Constants.INTENT_LOGIN_USERNAME, username);
		loginIntent.putExtra(Constants.INTENT_LOGIN_PASSWORD, password);

		loginIntent.putExtra(Constants.INTENT_EXTRA_RECEIVER_TAG,
				INSTANCE.mPersistenceServiceResultReceiver);

		INSTANCE.mCurrentContext.startService(loginIntent);
	}

	public boolean isLoggedIn() {
		SharedPreferences preferences = this.mCurrentContext
				.getSharedPreferences(Constants.SHARED_PREFS_NAME,
						Context.MODE_PRIVATE);
		String authToken = preferences.getString(Constants.SP_AUTH_TOKEN, "");
		if (null == authToken || 0 == authToken.length()) {
			return false;
		}

		return true;
	}

	@Override
	public void logout() {
		
		SPModel.getInstance(mCurrentContext).logoutPrep();
		
		SharedPreferences preferences = mCurrentContext.getSharedPreferences(
				Constants.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = preferences.edit();
		editor.remove(Constants.SP_USERNAME);
		editor.remove(Constants.SP_AUTH_TOKEN);
		editor.remove(Constants.SP_USER_TYPE);
		editor.remove(Constants.SP_STEAMPUNKUSER_ID);
		editor.commit();
		
		Intent logoutIntent = new Intent(this.mCurrentContext,
				PersistenceService.class);
		logoutIntent.setAction(PersistenceService.ACTION_LOGOUT);
		startPersistenceService(logoutIntent);
		
		Runnable logoutRunnable = new Runnable() {
			public void run() {
				EventBus.getDefault().post(
						new DefaultPersistenceServiceHelperEvents.LogoutEvent(
								true));
			}
		};

		Handler runnableHandler = new Handler();
		runnableHandler.postDelayed(logoutRunnable, 250);
		
	}
	
	@Override
	public void syncRoasters() {
		int requestId = SteampunkUtils.getRequestId(mCurrentContext);
		Intent syncRoastersIntent = mPersistenceIntentFactory.makeSyncRoastersIntent(requestId);
		startPersistenceService(syncRoastersIntent);
	}

	@Override
	public void getMachineSettings() {
		MachineSettings ms = MachineSettings.getMachineSettingsFromSharedPreferences(mCurrentContext);
			if(ms != null && ms.getSerialNumber() != null && !ms.getSerialNumber().isEmpty()){
			startPersistenceService(mPersistenceIntentFactory.makeGetMachineSettingsIntent(ms.getSerialNumber()));
		}
		

	}
	
	@Override
	public void saveRecipe(final long id, final String name, final int type, final boolean published, final double grams, final double teaspoons, final double grind, final int filter, String stacks) {
		Intent saveRecipeIntent = mPersistenceIntentFactory
				.makeSaveRecipeIntent(id, name, type, published,
						grams, teaspoons, grind, filter, stacks);
		
//		SPLog.debug("stacks(helper:save): " + stacks);

		startPersistenceService(saveRecipeIntent);
	}

	@Override
	public void syncRecipes() {
		Intent syncRecipesIntent = mPersistenceIntentFactory
				.makeSyncRecipeIntent();
		startPersistenceService(syncRecipesIntent);
	}

	@Override
	public void deleteRecipe(long recipeId) {
		Intent deleteRecipeIntent = mPersistenceIntentFactory
				.makeDeleteRecipeIntent(recipeId);

		startPersistenceService(deleteRecipeIntent);
	}

	@Override
	public void createLog(String recipeId, Integer crucibleIndex,
			Integer severity, Integer type, String message) {
		Long userId = SteampunkUtils.getCurrentSteampunkUserId(mCurrentContext);

		Intent createLogIntent = mPersistenceIntentFactory.makeCreateLogIntent(
				userId, SteampunkUtils.getMachineId(mCurrentContext),
				SteampunkUtils.getCurrentDateString(), recipeId, crucibleIndex,
				severity, type, message);

		startPersistenceService(createLogIntent);
	}


	@Override
	public void createFavorite(long recipeId) {
		SharedPreferences preferences = mCurrentContext.getSharedPreferences(
                 Constants.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
		long userId = preferences.getLong(Constants.SP_USER_ID, -1);

		Intent intent = mPersistenceIntentFactory.makeCreateFavoriteIntent(userId, recipeId);
		
		startPersistenceService(intent);
	}

	@Override
	public void deleteFavorite(long recipeId) {
		 SharedPreferences preferences = mCurrentContext.getSharedPreferences(
                 Constants.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
		long userId = preferences.getLong(Constants.SP_USER_ID, -1);
		Intent intent = mPersistenceIntentFactory.makeDeleteFavoriteIntent(userId, recipeId);
		
		startPersistenceService(intent);
	}
	
	public void syncFavorites() {
		long userId = SteampunkUtils.getCurrentSteampunkUserId(mCurrentContext);
		
		Intent intent = mPersistenceIntentFactory.makeSyncFavoritesIntent(userId);
		
		startPersistenceService(intent);
	}

	
	@Override
	public void saveMachineSettings(String serialNumber, Double boilerTemp,
			Double rinseTemp, Double rinseVoulme, Double elevation,
			ArrayList<Boolean> crucibleStates, SPTempUnitType tempUnitType,
			SPVolumeUnitType volumeUnitType, boolean localOnly) {
		startPersistenceService(mPersistenceIntentFactory
				.makeSaveMachineSettingsIntent(serialNumber,
						boilerTemp, rinseTemp, rinseVoulme, elevation,
						crucibleStates, tempUnitType, volumeUnitType, localOnly));
	}

	@Override
	public void saveAccountSettings(String username, String email,
			String address, String city, String state, String country,
			String zipCode, Boolean protectRecipes) {
		Intent saveAccountSettingsIntent = mPersistenceIntentFactory
				.makeSaveAccountSettingsIntent(username, email,
						address, city, state, country, zipCode, protectRecipes);

		startPersistenceService(saveAccountSettingsIntent);
	}

	private void startPersistenceService(Intent intent) {
		intent.putExtra(Constants.INTENT_EXTRA_RECEIVER_TAG,
				mPersistenceServiceResultReceiver);

		INSTANCE.mCurrentContext.startService(intent);
	}

	@Override
	public void onReceiveResult(int resultCode, Bundle resultData) {

		ResultCode resultCodeEnum = ResultCode.values()[resultCode];

		switch (resultCodeEnum) {
		case INVALID_MACHINE_SERIAL_NUMBER:
			EventBus.getDefault()
					.post(new DefaultPersistenceServiceHelperEvents.InvalidMachineSerialNumberEvent());
			break;
		case MACHINE_SETTINGS_SAVED:
			EventBus.getDefault()
					.post(new DefaultPersistenceServiceHelperEvents.MachineSettingsSaved());
			break;
		case NETWORK_ERROR:
			EventBus.getDefault().post(
					new DefaultPersistenceServiceHelperEvents.NetworkError());
			break;
		case CREATE_RECIPE:
			long recipeId = (long) resultData
					.getLong(PersistenceService.BUNDLE_NEW_RECIPE_ID);
			long localRecipeId = (long) resultData.getLong(PersistenceService.BUNDLE_LOCAL_RECIPE_ID);
			EventBus.getDefault()
					.post(new DefaultPersistenceServiceHelperEvents.CreateRecipeEvent(
							recipeId, localRecipeId));
			break;
//		case CREATE_STACK:
//			long stackId = (long) resultData
//					.getLong(PersistenceService.BUNDLE_NEW_STACK_ID);
//			long localStackId = (long) resultData
//					.getLong(PersistenceService.BUNDLE_LOCAL_STACK_ID);
//			EventBus.getDefault()
//					.post(new DefaultPersistenceServiceHelperEvents.CreateStackEvent(
//							stackId,localStackId));
//			break;
		case AVAILABLE_UPDATE:
			int newVersion = resultData.getInt(PersistenceService.BUNDLE_VERSION);
			EventBus.getDefault().post(
					new DefaultPersistenceServiceHelperEvents.AvailableUpdate(newVersion));
		case PIN_RESET:
			String PIN = resultData.getString(PersistenceService.BUNDLE_PIN);
			boolean success = resultData.getBoolean(PersistenceService.BUNDLE_SUCCESSFUL);
			EventBus.getDefault().post(
					new DefaultPersistenceServiceHelperEvents.UpdatedPIN(PIN, success));
		default:
			break;
		}
	}

	@Override
	public void requestPasswordReset(String identifier) {
		Intent pwResetIntent = mPersistenceIntentFactory
				.makePasswordResetIntent(SteampunkUtils.getRequestId(mCurrentContext), identifier);
		startPersistenceService(pwResetIntent);
	}
	
	public void resetPin(String PIN) {
		Long machineId = MachineSettings
				.getMachineSettingsFromSharedPreferences(mCurrentContext)
				.getId();

		Intent pinIntent = mPersistenceIntentFactory
				.makeResetPINIntent(SteampunkUtils.getRequestId(mCurrentContext), machineId, PIN);
		startPersistenceService(pinIntent);
	}
	
	@Override
	public void checkForUpdates() {
		MachineSettings mMachine = MachineSettings
				.getMachineSettingsFromSharedPreferences(mCurrentContext);
		
		int versionId = mMachine.getVersion(mCurrentContext);
		int deviceId = mMachine.getDeviceFromSharedPrefs(mCurrentContext);
		
		Intent checkForUpdatesIntent = mPersistenceIntentFactory
				.makeUpdatesIntent(SteampunkUtils.getRequestId(mCurrentContext), versionId, deviceId);	
		
		startPersistenceService(checkForUpdatesIntent);
	}

	public void downloadUpdate(int newVersion) {
		Intent downloadUpdateIntent = mPersistenceIntentFactory
				.makeDownloadUpdateIntent(SteampunkUtils.getRequestId(mCurrentContext), newVersion);
		startPersistenceService(downloadUpdateIntent);
	}
	
	public void createDeviceId() {
		MachineSettings mMachine = MachineSettings
				.getMachineSettingsFromSharedPreferences(mCurrentContext);
		
		int version = mMachine.getVersion(mCurrentContext);
		String name = mMachine.getDeviceName();
		
		Intent newDeviceIntent = mPersistenceIntentFactory
				.makeNewDeviceIntent(SteampunkUtils.getRequestId(mCurrentContext), version, name);
		startPersistenceService(newDeviceIntent);
	}

	public void subscribeToRoaster(long mSelectedRoaster) {
		Intent subscribeIntent = mPersistenceIntentFactory.makeSubscribeIntent(mSelectedRoaster);
		startPersistenceService(subscribeIntent);
	}
	
	public void changePassword(String old, String newPass) {
		Intent changePassIntent = mPersistenceIntentFactory.makeChangePassEvent(old,newPass);
		startPersistenceService(changePassIntent);
	}
	
	@Override
	public void enableNetworking() {
		Intent enableNetworkingIntent = mPersistenceIntentFactory.makeEnableNetworkingIntent();
		startPersistenceService(enableNetworkingIntent); //SPLog.debug("made and sent enable networking intent");
	}
	
	@Override
	public void disableNetworking() {
		Exception trace = new Exception("stack trace");
		trace.printStackTrace();
//		for(int i = 0;i < lines.length;i++) {
//			SPLog.debug(" trace: " + lines[i].toString());
//		}
		Intent disableNetworkingIntent = mPersistenceIntentFactory.makeDisableNetworkingIntent();
		startPersistenceService(disableNetworkingIntent); //SPLog.debug("made and sent disable networking intent");
	}
}
