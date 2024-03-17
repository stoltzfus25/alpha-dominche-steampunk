package com.alphadominche.steampunkhmi;

import android.content.Context;
import android.content.SharedPreferences;
import android.test.AndroidTestCase;

import com.alphadominche.steampunkhmi.restclient.networkclient.DefaultNetworkClient;
import com.alphadominche.steampunkhmi.utils.Constants;
//import com.alphadominche.steampunkhmi.restclient.networkclient.DefaultNetworkClientCallbacks;

public class DefaultNetworkClientTest extends AndroidTestCase {

	private DefaultNetworkClient mDefaultNetworkClient;
	private SharedPreferences mPreferences;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		this.mDefaultNetworkClient = null;
		SharedPreferences.Editor editor = this.mPreferences.edit();
		editor.clear();
		editor.commit();
		this.mPreferences = null;

		super.tearDown();
	}

	// test is currently failing - need to complete TODOs
	public void testLogin() {
		// TODO Implement callbacks
		this.mDefaultNetworkClient = new DefaultNetworkClient(getContext(), null);
		this.mPreferences = getContext().getSharedPreferences(
				Constants.SHARED_PREFS_NAME, Context.MODE_PRIVATE);

		// TODO mock the web server so we can control what is returned
		String expectedUsername = "guy";
		String testPassword = "test";
		String expectedAuthToken = "12345";

		this.mDefaultNetworkClient.postLogin(-1, expectedUsername, testPassword);
		
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		/**
		 * The NetworkClient doesn't store the results in the shared prefs -
		 * that is the job of the ContentProcessor.
		 * 
		 * We can check that we call the callbacks correctly and 
		 * that we provide the correct inputs into the callbacks.
		 * 
		 * The rest of the key functionality, such as mapping the RemoteClass
		 * to the JSON, is provided by Retrofit. We can test that we have the
		 * correct attributes in the RemoteClass.
		 */
		String actualUsername = this.mPreferences.getString(
				Constants.SP_USERNAME, null);
		String actualAuthToken = this.mPreferences.getString(
				Constants.SP_AUTH_TOKEN, null);

		assertEquals(expectedUsername, actualUsername);
		assertEquals(expectedAuthToken, actualAuthToken);
	}

//	public void testLogout() {
//
//		String expectedUsername = "test";
//		String testPassword = "testing";
//
//		this.mDefaultPersistenceServiceHelper.login(expectedUsername,
//				testPassword);
//		this.mDefaultPersistenceServiceHelper.logout();
//
//		String actualUsername = this.mPreferences.getString(
//				Constants.SP_USERNAME, null);
//		String actualAuthToken = this.mPreferences.getString(
//				Constants.SP_AUTH_TOKEN, null);
//
//		assertEquals(null, actualUsername);
//		assertEquals(null, actualAuthToken);
//	}

}

