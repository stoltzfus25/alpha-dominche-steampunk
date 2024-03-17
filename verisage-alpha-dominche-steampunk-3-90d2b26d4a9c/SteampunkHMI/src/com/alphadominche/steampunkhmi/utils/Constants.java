package com.alphadominche.steampunkhmi.utils;

public class Constants {

	/**
	 * @category Shared Preferences
	 */
	public static final Long LOCALOFFSET=1000000000000L;
	public static final String SHARED_PREFS_NAME = "com.alphadominche.steampunkhmi";
	public static final long LOCAL_ID_MIN = 1000000000000L;

	// Account Settings
	public static final String SP_USERNAME = "username";
	public static final String SP_EMAIL = "email";
	public static final String SP_ADDRESS = "address";
	public static final String SP_CITY = "city";
	public static final String SP_STATE = "state";
	public static final String SP_COUNTRY = "country";
	public static final String SP_ZIP_CODE = "zip";
	public static final String SP_PROTECT_RECIPES = "protect_recipes";
	public static final String SP_USER_TYPE = "user_type";
	public static final String SP_STEAMPUNKUSER_ID = "steampunkuser_id";
	public static final String SP_USER_ID = "user_id";
	public static final String SP_AUTH_TOKEN = "auth_token";
	public static final String SP_OLD_PASSWORD = "old_password";
	public static final String SP_NEW_PASSWORD = "new_password";

	// Machine Settings
	public static final String SP_MACHINE_ID = "machine_id";
	public static final String SP_MACHINE_SERIAL_NUMBER = "machine_serial_number";
	public static final String SP_PIN = "machine_pin";
	public static final String SP_BOILER_TEMP = "boiler_temp";
	public static final String SP_RINSE_TEMP = "rinse_temp";
	public static final String SP_RINSE_VOLUME = "rinse_volume";
	public static final String SP_ELEVATION = "elevation";
	public static final String SP_VOLUME_UNIT_TYPE = "volume_unit";
	public static final String SP_TEMP_UNIT_TYPE = "temp_unit";
	public static final String SP_CRUCIBLE_COUNT = "crucible_count";
	public static final String SP_CRUCIBLE_STATES = "crucible_states";
	public static final String SP_LOCAL_ONLY = "local_only";
	public static final String CRUCIBLE_RECIPE_ID_PREFIX = "crucibleRecipeID_";
	
	// Cleaning Cycle Settings
	public static final String SP_CLEANING_TEMP = "cleaning_temp";
	public static final String SP_CLEANING_VOL = "cleaning_vol";
	
	public static final String SP_VERSION_ID = "version_id";
	public static final String SP_DEVICE_ID = "device_id";

	public static final String SP_REQUEST_ID = "request_id";

	/**
	 * @category Transaction State Flags
	 */
	public static final String STATE_POSTING = "STATE_POSTING";
	public static final String STATE_NEEDS_REPOSTING="STATE_NEEDS_REPOSTING";
	public static final String STATE_DELETING = "STATE_DELETING";
	public static final String STATE_PUTTING = "STATE_PUTTING";
	public static final String STATE_GETTING = "STATE_GETTING";
	public static final String STATE_HAS_UNSAVED_CHANGES = "STATE_HAS_UNSAVED_CHANGES";
	public static final String STATE_OK = "STATE_OK";
	
	/**
	 * @category http method types
	 */
	public static final String POST = "POST";
	public static final String DELETE = "DELETE";
	public static final String PUT = "PUT";
	public static final String GET = "GET";
	

	/**
	 * @category PersistenceService Intent Extra Keys
	 */
	public static final String INTENT_EXTRA_REQUEST_ID = "com.alphadominche.steampunkhmi.restclient.presistenceservice.INTENT_EXTRA_REQUEST_ID";
	public static final String INTENT_EXTRA_RECEIVER_TAG = "com.alphadominche.steampunkhmi.restclient.presistenceservice.INTENT_EXTRA_RECEIVER_TAG";
	public static final String INTENT_EXTRA_USER_ID = "com.alphadominche.steampunkhmi.restclient.presistenceservice.INTENT_EXTRA_USER_ID";
	public static final String INTENT_LOGIN_USERNAME = "com.alphadominche.steampunkhmi.restclient.presistenceservice.INTENT_LOGIN_USERNAME";
	public static final String INTENT_LOGIN_PASSWORD = "com.alphadominche.steampunkhmi.restclient.presistenceservice.INTENT_LOGIN_PASSWORD";
	public static final String INTENT_VERSION = "com.alphadominche.steampunkhmi.restclient.persistenceservice.INTENT_VERSION";
	public static final String INTENT_DEVICE = "com.alphadominche.steampunkhmi.restclient.persistenceservice.INTENT_DEVICE";
	public static final String INTENT_PLATFORM = "com.alphadominche.steampunkhmi.restclient.persistenceservice.INTENT_PLATFORM";
	public static final String INTENT_NAME = "com.alphadominche.steampunkhmi.restclient.persistenceservice.INTENT_NAME";
	public static final String INTENT_PIN = "com.alphadominche.steampunkhmi.restclient.persistenceservice.INTENT_PIN";
	public static final String INTENT_MACHINE_ID = "com.alphadominche.steampunkhmi.restclient.persistenceservice.INTENT_MACHINE_ID";
	
	/**
	 * @category Machine Settings Intent Keys
	 */
	public static final String MACHINE_SETTINGS_SERIAL_NUMBER = "com.alphadominche.steampunkhmi.restclient.presistenceservice.INTENT_MACHINE_SETTINGS_SERIAL_NUMBER";
	public static final String MACHINE_SETTINGS_BOILER_TEMP = "com.alphadominche.steampunkhmi.restclient.presistenceservice.INTENT_MACHINE_SETTINGS_BOILER_TEMP";
	public static final String MACHINE_SETTINGS_RINSE_TEMP = "com.alphadominche.steampunkhmi.restclient.presistenceservice.INTENT_MACHINE_SETTINGS_RINSE_TEMP";
	public static final String MACHINE_SETTINGS_RINSE_VOLUME = "com.alphadominche.steampunkhmi.restclient.presistenceservice.INTENT_MACHINE_SETTINGS_RINSE_VOLUME";
	public static final String MACHINE_SETTINGS_ELEVATION = "com.alphadominche.steampunkhmi.restclient.presistenceservice.INTENT_MACHINE_SETTINGS_ELEVATION";
	public static final String MACHINE_SETTINGS_CRUCIBLE_STATES = "com.alphadominche.steampunkhmi.restclient.presistenceservice.INTENT_MACHINE_SETTINGS_CRUCIBLE_STATES";
	public static final String MACHINE_SETTINGS_TEMP_UNIT_TYPE = "com.alphadominche.steampunkhmi.restclient.presistenceservice.INTENT_MACHINE_SETTINGS_TEMP_UNIT_TYPE";
	public static final String MACHINE_SETTINGS_VOLUME_UNIT_TYPE = "com.alphadominche.steampunkhmi.restclient.presistenceservice.INTENT_MACHINE_SETTINGS_VOLUME_UNIT_TYPE";
	public static final String MACHINE_SETTINGS_LOCAL_ONLY = "com.alphadominche.steampunkhmi.restclient.presistenceservice.INTENT_MACHINE_SETTINGS_LOCAL_ONLY";

	/**
	 * @category User Settings Intent Keys
	 */
	public static final String USER_SETTINGS_USERNAME = "com.alphadominche.steampunkhmi.restclient.presistenceservice.USER_SETTINGS_USERNAME";
	public static final String USER_SETTINGS_EMAIL = "com.alphadominche.steampunkhmi.restclient.presistenceservice.USER_SETTINGS_EMAIL";
	public static final String USER_SETTINGS_ADDRESS = "com.alphadominche.steampunkhmi.restclient.presistenceservice.USER_SETTINGS_ADDRESS";
	public static final String USER_SETTINGS_CITY = "com.alphadominche.steampunkhmi.restclient.presistenceservice.USER_SETTINGS_CITY";
	public static final String USER_SETTINGS_STATE = "com.alphadominche.steampunkhmi.restclient.presistenceservice.USER_SETTINGS_STATE";
	public static final String USER_SETTINGS_COUNTRY = "com.alphadominche.steampunkhmi.restclient.presistenceservice.USER_SETTINGS_COUNTRY";
	public static final String USER_SETTINGS_ZIP_CODE = "com.alphadominche.steampunkhmi.restclient.presistenceservice.USER_SETTINGS_ZIP_CODE";
	public static final String USER_SETTINGS_PROTECT_RECIPES = "com.alphadominche.steampunkhmi.restclient.presistenceservice.USER_SETTINGS_PROTECT_RECIPES";
	
	/**
	 * @category Authentication Keys
	 */
	public static final String PW_RESET_IDENTIFIER = "com.alphadominche.steampunkhmi.restclient.persistenceservice.PW_RESET_IDENTIFIER";
			
	/**
	 * @category Network Client Constants
	 */
	public static final String NO_AUTHENTICATION_CHALLENGES_ERROR_MESSAGE = "No authentication challenges found";
	
	/**
	 * @category Recipe Type Constants
	 */
	public static final int RECIPE_TYPE_TEA = 0;
	public static final int RECIPE_TYPE_COFFEE = 1;
	
	/**
	 * @category Recipe Keys
	 */
	public static final String STACKS_INTENT_KEY = "STACKS_INTENT_KEY";
	public static final String AGITATION_INTENT_KEY = "AGITATION_INTENT_KEY";
	
	/**
	 * @category Favorites Keys
	 */
	public static final String FAVORITE_ID = "FAVORITE_ID";
	
	/**
	 * @category Subscription Keys
	 */
	public static final String SUBSCRIBE_TO_ID = "SUBSCRIBE_TO_ID";
	
	/**
	 * @category SpecialMigrationIndicator Version 36 to ?
	 */
	public static final String MIGRATION_INDICATOR_FROM_36 = "MIGRATION_INDICATOR_FROM_36";
	
	/**
	 * @category DB Helpers
	 */
	 public static final String ASCENDING = " COLLATE NOCASE ASC";
	 public static final String DESCENDING = " COLLATE NOCASE DESC";
}
