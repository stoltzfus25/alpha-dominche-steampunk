package com.alphadominche.steampunkhmi.uitest;

import android.os.RemoteException;
import android.util.Log;
import android.widget.TextView;

import com.android.uiautomator.core.UiObject;
import com.android.uiautomator.core.UiObjectNotFoundException;
import com.android.uiautomator.core.UiScrollable;
import com.android.uiautomator.core.UiSelector;
import com.android.uiautomator.core.UiWatcher;
import com.android.uiautomator.testrunner.UiAutomatorTestCase;

public class SideBarMenuScreensTest extends UiAutomatorTestCase {

	private final String TAG = SideBarMenuScreensTest.class.getCanonicalName();
	private final String STEAMPUNK_STOPPED_WATCHER_STRING = "STEAMPUNK_STOPPED_WATCHER";
	private final long defaultWindowWaitTime = 30000;

	public void testTemp() throws UiObjectNotFoundException, RemoteException {
		setupWatchers();

		openApp();

		login();

		// Open the library screen
		clickButtonWithResourceIdAndWaitForDefaultTime("com.alphadominche.steampunkhmi:id/mainMenuBtn");
		clickButtonWithResourceIdAndWaitForDefaultTime("com.alphadominche.steampunkhmi:id/main_menu_library_button");
		Log.i(TAG, "Opened the library screen");

		// Open and close the my recipes screen
		clickButtonWithResourceIdAndWaitForDefaultTime("com.alphadominche.steampunkhmi:id/menu_button");
		clickButtonWithResourceIdAndWaitForDefaultTime("com.alphadominche.steampunkhmi:id/main_menu_my_recipes_button");
		Log.i(TAG, "Opened the my recipes screen");
		clickButtonWithResourceIdAndWaitForDefaultTime("com.alphadominche.steampunkhmi:id/cancel_button");
		Log.i(TAG, "Closed the my recipes screen");

		// Click the favorites button
		clickButtonWithResourceIdAndWaitForDefaultTime("com.alphadominche.steampunkhmi:id/menu_button");
		clickButtonWithResourceIdAndWaitForDefaultTime("com.alphadominche.steampunkhmi:id/main_menu_favorites_button");
		clickButtonWithResourceIdAndWaitForDefaultTime("com.alphadominche.steampunkhmi:id/cancel_button");

		// Open and close the add coffee recipe screen
		clickButtonWithResourceIdAndWaitForDefaultTime("com.alphadominche.steampunkhmi:id/menu_button");
		clickButtonWithResourceIdAndWaitForDefaultTime("com.alphadominche.steampunkhmi:id/main_menu_add_coffee_recipe_button");
		Log.i(TAG, "Opened the add coffee recipe screen");
		clickButtonWithResourceIdAndWaitForDefaultTime("com.alphadominche.steampunkhmi:id/cancel_button");
		Log.i(TAG, "Close the add coffee recipe screen");

		// Open and close the add tea recipe screen
		clickButtonWithResourceIdAndWaitForDefaultTime("com.alphadominche.steampunkhmi:id/menu_button");
		clickButtonWithResourceIdAndWaitForDefaultTime("com.alphadominche.steampunkhmi:id/main_menu_add_tea_recipe_button");
		Log.i(TAG, "Opened the add tea recipe screen");
		clickButtonWithResourceIdAndWaitForDefaultTime("com.alphadominche.steampunkhmi:id/cancel_button");
		Log.i(TAG, "Closed the add tea recipe screen");

		// Open and close the steampunk settings screen
		clickButtonWithResourceIdAndWaitForDefaultTime("com.alphadominche.steampunkhmi:id/menu_button");
		clickButtonWithResourceIdAndWaitForDefaultTime("com.alphadominche.steampunkhmi:id/main_menu_machine_settings_button");
		Log.i(TAG, "Opened the machine settings screen");
		clickButtonWithResourceIdAndWaitForDefaultTime("com.alphadominche.steampunkhmi:id/cancel_button");
		Log.i(TAG, "Closed the machine settings screen");

		// Open and close the account settings screen
		clickButtonWithResourceIdAndWaitForDefaultTime("com.alphadominche.steampunkhmi:id/menu_button");
		clickButtonWithResourceIdAndWaitForDefaultTime("com.alphadominche.steampunkhmi:id/main_menu_account_settings_button");
		Log.i(TAG, "Opened the account settings screen");
		clickButtonWithResourceIdAndWaitForDefaultTime("com.alphadominche.steampunkhmi:id/cancel_button");
		Log.i(TAG, "Closed the account settings screen");

		// Open and close the terms and policies screen
		clickButtonWithResourceIdAndWaitForDefaultTime("com.alphadominche.steampunkhmi:id/menu_button");
		clickButtonWithResourceIdAndWaitForDefaultTime("com.alphadominche.steampunkhmi:id/main_menu_terms_and_policies_button");
		Log.i(TAG, "Opened the terms and policies screen");
		clickButtonWithResourceIdAndWaitForDefaultTime("com.alphadominche.steampunkhmi:id/cancel_button");
		Log.i(TAG, "Closed the terms and policies screen");

		// Go back to the crucibles screen
		clickButtonWithResourceIdAndWaitForDefaultTime("com.alphadominche.steampunkhmi:id/menu_button");
		clickButtonWithResourceIdAndWaitForDefaultTime("com.alphadominche.steampunkhmi:id/main_menu_crucibles_button");
		Log.i(TAG, "Opened the crucibles screen");
		clickButtonWithResourceIdAndWaitForDefaultTime("com.alphadominche.steampunkhmi:id/mainMenuBtn");

		// Logout
		clickButtonWithResourceIdAndWaitForDefaultTime("com.alphadominche.steampunkhmi:id/main_menu_logout_button");
		Log.i(TAG, "Clicked the logout button");
		UiObject yesButton = new UiObject(new UiSelector().className(
				"android.widget.Button").text("Yes"));
		yesButton.clickAndWaitForNewWindow(defaultWindowWaitTime);
		Log.i(TAG, "Clicked confirm logout");
	}

	private UiObject getUiObjectByResourceId(String resourceId) {
		return new UiObject(new UiSelector().resourceId(resourceId));
	}

	private void clickButtonWithResourceIdAndWaitForDefaultTime(
			String resourceId) throws UiObjectNotFoundException {
		UiObject buttonToClick = getUiObjectByResourceId(resourceId);
		buttonToClick.clickAndWaitForNewWindow(defaultWindowWaitTime);
	}

	private void setupWatchers() {
		UiWatcher steampunkStoppedWatcher = new UiWatcher() {
			@Override
			public boolean checkForCondition() {
				UiObject steampunkStoppedDialog = new UiObject(
						new UiSelector()
								.textContains("Unfortunately, Steampunk HMI has stopped."));
				if (steampunkStoppedDialog.exists()) {
					assertFalse("Steampunk has stopped.",
							steampunkStoppedDialog.exists());
					return true;
				}
				return false;
			}
		};

		// Register watcher
		getUiDevice().registerWatcher(STEAMPUNK_STOPPED_WATCHER_STRING,
				steampunkStoppedWatcher);

		// Run watcher
		getUiDevice().runWatchers();
	}

	private void openApp() throws RemoteException, UiObjectNotFoundException {
		getUiDevice().wakeUp();
		getUiDevice().pressHome();

		UiObject allAppsButton = new UiObject(
				new UiSelector().description("Apps"));
		allAppsButton.clickAndWaitForNewWindow();

		UiObject appsTab = new UiObject(new UiSelector().text("Apps"));
		appsTab.click();

		UiScrollable appViews = new UiScrollable(
				new UiSelector().scrollable(true));
		UiObject steampunkApp = appViews.getChildByText(
				new UiSelector().className(TextView.class.getName()),
				"Crucibles");
		steampunkApp.clickAndWaitForNewWindow(defaultWindowWaitTime);
	}

	private void login() throws UiObjectNotFoundException {
		UiObject steampunkAppValidation = new UiObject(
				new UiSelector().packageName("com.alphadominche.steampunkhmi"));
		assertTrue("Unable to open Steampunk app",
				steampunkAppValidation.exists());

		UiObject usernameTextBox = getUiObjectByResourceId("com.alphadominche.steampunkhmi:id/login_username");
		usernameTextBox.click();
		usernameTextBox.clearTextField();
		usernameTextBox.setText("guy");

		UiObject passwordTextBox = getUiObjectByResourceId("com.alphadominche.steampunkhmi:id/login_password");
		passwordTextBox.click();
		passwordTextBox.clearTextField();
		passwordTextBox.setText("test");

		UiObject loginButton = getUiObjectByResourceId("com.alphadominche.steampunkhmi:id/login_button");
		loginButton.clickAndWaitForNewWindow();
	}
}
