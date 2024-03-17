package com.alphadominche.steampunkhmi.test;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.test.AndroidTestCase;

import com.alphadominche.steampunkhmi.restclient.persistenceservicehelper.DefaultPersistenceServiceHelper;
import com.alphadominche.steampunkhmi.restclient.presistenceservice.PersistenceService;
import com.alphadominche.steampunkhmi.utils.Constants;

public class DefaultPersistenceServiceHelperTest extends AndroidTestCase {

	private final static String VALID_USERNAME = "test";
	private final static String VALID_PASSWORD = "testing";
	
	private DefaultPersistenceServiceHelper mDefaultPersistenceServiceHelper;
	private SharedPreferences mPreferences;

	@Override
	protected void setUp() throws Exception {
		mDefaultPersistenceServiceHelper = DefaultPersistenceServiceHelper
				.getInstance(getContext());
		mPreferences = getContext().getSharedPreferences(
				Constants.SHARED_PREFS_NAME, Context.MODE_PRIVATE);

		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		mDefaultPersistenceServiceHelper = null;
		SharedPreferences.Editor editor = mPreferences.edit();
		editor.clear();
		editor.commit();
		mPreferences = null;

		super.tearDown();
	}
		
	public void testLogin() {

		this.mContext.stopService(new Intent(this.mContext,
				PersistenceService.class));

		this.mDefaultPersistenceServiceHelper.login(VALID_USERNAME,
				VALID_PASSWORD);
		
		// We can't tell until the asynchronous network calls complete
		// if the login succeeded. So, just check that the expected service
		// is running
		assertTrue(isServiceRunning(PersistenceService.class));
		
		this.mContext.stopService(new Intent(this.mContext,
				PersistenceService.class));
		
		this.mDefaultPersistenceServiceHelper.logout();
	}
	
	public void testIsLoggedIn() {
		// log in
		this.mContext.stopService(new Intent(this.mContext,
				PersistenceService.class));

		this.mDefaultPersistenceServiceHelper.login(VALID_USERNAME,
				VALID_PASSWORD);
		
//		android.os.SystemClock.sleep(1000);
		
		// call isLoggedIn() -> should return true
		assertTrue(this.mDefaultPersistenceServiceHelper.isLoggedIn());
		
		// log out
		
		// call isLoggedIn() -> should return false
		
		// log in w/ invalid credentials
		
		// call isLoggedIn() -> should return false
	}

	public void testLogout() {

		String expectedUsername = "test";
		String testPassword = "testing";

		mDefaultPersistenceServiceHelper.login(expectedUsername, testPassword);
		mDefaultPersistenceServiceHelper.logout();

		String actualUsername = mPreferences.getString(Constants.SP_USERNAME,
				null);
		String actualAuthToken = mPreferences.getString(
				Constants.SP_AUTH_TOKEN, null);

		assertEquals(null, actualUsername);
		assertEquals(null, actualAuthToken);
	}

	// public void testCreateRecipe() {
	// String actualName = "Test Recipe";
	// int actualType = 1;
	// boolean actualPublished = true;
	// Grind actualGrind = new Grind();
	// Filter actualFilter = new Filter();
	//
	// actualGrind.setName("Test Grind");
	// actualGrind.setDescription(" Description goes here");
	// actualGrind.setIcon("/path/to/icon");
	//
	// actualFilter.setName("Test Grind");
	// actualFilter.setDescription(" Description goes here");
	// actualFilter.setIcon("/path/to/icon");
	//
	// mDefaultPersistenceServiceHelper.createRecipe(actualName,
	// actualType, actualPublished, actualGrind, actualFilter);
	//
	// Cursor mCursor = getContext().getContentResolver().query(
	// Provider.RECIPE_CONTENT_URI, RecipeTable.ALL_COLUMNS, null,
	// null, null);
	//
	// assertNotNull(mCursor);
	// assertEquals(1, mCursor.getCount());
	//
	// }

	public void testCreateRecipe() throws InterruptedException {
		// mDefaultPersistenceServiceHelper.createRecipe("test", 0, false, 0,
		// 0);
		// assertTrue(isServiceRunning(PersistenceService.class));
	}

	// private <T> boolean isServiceRunning(Class<T> serivceClass) {
	// ActivityManager activityManager = (ActivityManager) getContext()
	// .getSystemService(Context.ACTIVITY_SERVICE);
	// List<ActivityManager.RunningServiceInfo> serviceList = activityManager
	// .getRunningServices(Integer.MAX_VALUE);
	//
	// if (serviceList.isEmpty()) {
	// return false;
	// }
	//
	// for (RunningServiceInfo serviceInfo : serviceList) {
	// ComponentName serviceName = serviceInfo.service;
	// if (serviceName.getClassName().equals(serivceClass.getName())) {
	// return true;
	// }
	// }
	//
	// return false;
	// }
	
	private <T> boolean isServiceRunning(Class<T> serviceClass) {
		ActivityManager activityManager = (ActivityManager) getContext()
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningServiceInfo> serviceList = activityManager
				.getRunningServices(Integer.MAX_VALUE);
		if (serviceList.isEmpty()) {
			return false;
		}
		for (RunningServiceInfo serviceInfo : serviceList) {
			ComponentName serviceName = serviceInfo.service;
			if (serviceName.getClassName().equals(serviceClass.getName())) {
				return true;
			}
		}
		return false;
	}
}
