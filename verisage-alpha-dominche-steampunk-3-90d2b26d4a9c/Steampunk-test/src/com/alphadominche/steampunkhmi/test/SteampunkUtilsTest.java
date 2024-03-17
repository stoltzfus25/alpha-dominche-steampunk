package com.alphadominche.steampunkhmi.test;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.test.AndroidTestCase;

import com.alphadominche.steampunkhmi.utils.Constants;
import com.alphadominche.steampunkhmi.utils.SteampunkUtils;

public class SteampunkUtilsTest extends AndroidTestCase {

	@Override
	protected void setUp() throws Exception {
		mContext.getSharedPreferences(Constants.SHARED_PREFS_NAME,
				Context.MODE_PRIVATE).edit().clear().apply();
		super.setUp();
	}

	public void testGetCurrentDateSting() {
		// TODO implement
	}

	public void testGetMachineId() {
		// TODO implement
	}

	public void testGetSteampunkSharedPreferences() {
		// TODO implement
	}

	public void testGetCurrentSteampunkUserId() {
		Integer noLoggedInUserId = 0;
		assertEquals(noLoggedInUserId,
				SteampunkUtils.getCurrentSteampunkUserId(mContext));

		SharedPreferences sharedPreferences = mContext.getSharedPreferences(
				Constants.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
		Editor editor = sharedPreferences.edit();

		Integer expectedSteampunkUserId = 1;

		editor.putInt(Constants.SP_STEAMPUNKUSER_ID, expectedSteampunkUserId);
		editor.apply();

		assertEquals(expectedSteampunkUserId, SteampunkUtils.getCurrentSteampunkUserId(mContext));

		editor.remove(Constants.SP_STEAMPUNKUSER_ID);
		editor.apply();

		assertEquals(noLoggedInUserId,
				SteampunkUtils.getCurrentSteampunkUserId(mContext));
	}

	public void testGetCurrentUserType() {
		String noLoggedInUserType = null;

		assertEquals(noLoggedInUserType, SteampunkUtils.getCurrentSteampunkUserType(mContext));

		String expectedUserType = "Roaster";
		SharedPreferences sharedPreferences = getContext()
				.getSharedPreferences(Constants.SHARED_PREFS_NAME,
						Context.MODE_PRIVATE);
		Editor editor = sharedPreferences.edit();

		editor.putString(Constants.SP_USER_TYPE, expectedUserType);
		editor.commit();

		assertEquals(expectedUserType,
				SteampunkUtils.getCurrentSteampunkUserType(mContext));

		editor.remove(Constants.SP_USER_TYPE);
		editor.commit();

		assertEquals(noLoggedInUserType,
				SteampunkUtils.getCurrentSteampunkUserType(mContext));

	}

}
