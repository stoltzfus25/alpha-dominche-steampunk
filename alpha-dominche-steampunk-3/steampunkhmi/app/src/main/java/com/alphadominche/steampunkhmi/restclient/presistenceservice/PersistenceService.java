package com.alphadominche.steampunkhmi.restclient.presistenceservice;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;
import com.alphadominche.steampunkhmi.database.tables.LogTable;
import com.alphadominche.steampunkhmi.database.tables.RecipeTable;
import com.alphadominche.steampunkhmi.model.Recipe;
import com.alphadominche.steampunkhmi.restclient.contentprocessor.ContentProcessor;
import com.alphadominche.steampunkhmi.restclient.contentprocessor.DefaultContentProcessor;
import com.alphadominche.steampunkhmi.utils.AccountSettings;
import com.alphadominche.steampunkhmi.utils.Constants;
import com.alphadominche.steampunkhmi.utils.MachineSettings;

/**
 * A stateless object that simply handles intents from the ServiceHelper and
 * calls upon the processor to take specific actions
 *
 * @author jnuss
 */
public class PersistenceService extends IntentService implements
        ContentProcessor.ContentProcessorCallbacks {

    private static final String TAG = PersistenceService.class
            .getCanonicalName();

    /**
     * @category Intent actions
     */
    public static final String ACTION_HANDLE_BACKLOG = "com.alphadominche.steampunkhmi.ACTION_HANDLE_BACKLOG";
    public static final String ACTION_SYNC_ROASTERS = "com.alphadominche.steampunkhmi.ACTION_SYNC_ROASTERS";
    public static final String ACTION_SAVE_RECIPE = "com.alphadominche.steampunkhmi.ACTION_SAVE_RECIPE";
    public static final String ACTION_SYNC_RECIPES = "com.alphadominche.steampunkhmi.ACTION_SYNC_RECIPES";
    public static final String ACTION_DELETE_RECIPE = "com.alphadominche.steampunkhmi.ACTION_DELETE_RECIPE";
    public static final String ACTION_CREATE_LOG = "com.alphadominche.steampunkhmi.ACTION_CREATE_LOG";
    public static final String ACTION_LOGIN = "com.alphadominche.steampunkhmi.ACTION_LOGIN";
    public static final String ACTION_LOGOUT = "com.alphadominche.steampunkhmi.ACTION_LOGOUT";
    public static final String ACTION_SAVE_MACHINE_SETTINGS = "com.alphadominche.steampunkhmi.ACTION_SAVE_MACHINE_SETTINGS";
    public static final String ACTION_GET_MACHINE_SETTINGS = "com.alphadominche.steampunkhmi.ACTION_GET_MACHINE_SETTINGS";
    public static final String ACTION_SAVE_USER_SETTINGS = "com.alphadominche.steampunkhmi.ACTION_SAVE_USER_SETTINGS";
    public static final String ACTION_CREATE_FAVORITE = "com.alphadominche.steampunkhmi.ACTION_CREATE_FAVORITE";
    public static final String ACTION_DELETE_FAVORITE = "com.alphadominche.steampunkhmi.ACTION_DELETE_FAVORITE";
    public static final String ACTION_RESET_PASSWORD = "com.alphadominche.steampunkhmi.ACTION_RESET_PASSWORD";
    public static final String ACTION_CHANGE_PASSWORD = "com.alphadominche.steampunkhmi.ACTION_CHANGE_PASSWORD";
    public static final String ACTION_CHECK_FOR_UPDATE = "com.alphadominche.steampunkhmi.ACTION_CHECK_FOR_UPDATE";
    public static final String ACTION_DOWNLOAD_UPDATE = "com.alphadominche.steampunkhmi.ACTION_DOWNLOAD_UPDATE";
    public static final String ACTION_CREATE_DEVICE = "com.alphadominche.steampunkhmi.ACTION_CREATE_DEVICE";
    public static final String ACTION_CHANGE_PIN = "com.alphadominche.steampunkhmi.ACTION_CHANGE_PIN";
    public static final String ACTION_SUBSCRIBE_TO_ROASTER = "com.alphadominche.steampunkhmi.ACTION_SUBSCRIBE_TO_ROASTER";
    public static final String ACTION_ENABLE_NETWORK_CONNECTION = "com.alphadominche.steampunkhmi.ACTION_START_QUEUE_THREAD";
    public static final String ACTION_DISABLE_NETWORK_CONNECTION = "com.alphadominche.steampunkhmi.ACTION_STOP_QUEUE_THREAD";

    /**
     * @category Bundle keys
     */
    public static final String BUNDLE_REQUEST_ID = "BUNDLE_REQUEST_ID";
    public static final String BUNDLE_SUCCESSFUL = "BUNDLE_SUCCESSFUL";
    public static final String BUNDLE_NEW_RECIPE_ID = "BUNDLE_NEW_RECIPE_ID";
    public static final String BUNDLE_LOCAL_RECIPE_ID = "BUNDLE_LOCAL_RECIPE_ID";
    public static final String BUNDLE_LOCAL_STACK_ID = "BUNDLE_LOCAL_STACK_ID";
    public static final String BUNDLE_NEW_STACK_ID = "BUNDLE_NEW_STACK_ID";
    public static final String BUNDLE_NEW_AGITATION_ID = "BUNDLE_NEW_AGITATION_ID";

    public static final String BUNDLE_RECIPE_ID = "BUNDLE_RECIPE_ID";
    public static final String BUNDLE_VERSION = "BUNDLE_VERSION";
    public static final String BUNDLE_PIN = "BUNDLE_PIN";

    public static final String ACTION_CREATE_RECIPE = "ACTION_CREATE_RECIPE";

    public static final String ACTION_UPDATE_RECIPE = "ACTION_UPDATE_RECIPE";

    public static final String ACTION_SYNC_GRINDS = "ACTION_SYNC_GRINDS";

    public static String ACTION_SYNC_FAVORITES = "ACTION_SYNC_FAVORITES";

    public static enum ResultCode {
        SYNC_RECIPES, CREATE_RECIPE, UPDATE_RECIPE, DELETE_RECIPE, SYNC_GRINDS,
        CREATE_LOG, INVALID_MACHINE_SERIAL_NUMBER, MACHINE_SETTINGS_SAVED,
        NETWORK_ERROR, SYNC_STACKS, CREATE_STACK, CREATE_AGITATION,
        AVAILABLE_UPDATE, PIN_RESET
    }

    private ResultReceiver mResultReceiver;
    private IntentParser mIntentParser;

    public PersistenceService() {
        super(PersistenceService.class.getCanonicalName());
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mIntentParser = new IntentParser(getApplicationContext());
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        mResultReceiver = intent
                .getParcelableExtra(Constants.INTENT_EXTRA_RECEIVER_TAG);

        String intentAction = intent.getAction();
        if (intentAction.equals(ACTION_SYNC_ROASTERS)) {
            syncRoasters(intent);
        } else if (intentAction.equals(ACTION_SAVE_RECIPE)) {
            saveRecipe(intent);
        } else if (intentAction.equals(ACTION_SYNC_RECIPES)) {
            syncRecipes(intent);
        } else if (intentAction.equals(ACTION_DELETE_RECIPE)) {
            deleteRecipe(intent);
        } else if (intentAction.equals(ACTION_LOGIN)) {
            login(intent);
        } else if (intentAction.equals(ACTION_LOGOUT)) {
            logout(intent);
        } else if (intentAction.equals(ACTION_CREATE_LOG)) {
            createLog(intent);
        } else if (intentAction.equals(ACTION_SAVE_MACHINE_SETTINGS)) {
            saveMachineSettings(intent);
        } else if (intentAction.equals(ACTION_GET_MACHINE_SETTINGS)) {
            getMachineSettings(intent);
        } else if (intentAction.equals(ACTION_SAVE_USER_SETTINGS)) {
            saveAccountSettings(intent);
        } else if (intentAction.equals(ACTION_CREATE_FAVORITE)) {
            createFavorite(intent);
        } else if (intentAction.equals(ACTION_DELETE_FAVORITE)) {
            deleteFavorite(intent);
        } else if (intentAction.equals(ACTION_SYNC_FAVORITES)) {
            syncFavorite(intent);
        } else if (intentAction.equals(ACTION_RESET_PASSWORD)) {
            resetPassword(intent);
        } else if (intentAction.equals(ACTION_CHANGE_PASSWORD)) {
            changePassword(intent);
        } else if (intentAction.equals(ACTION_CHECK_FOR_UPDATE)) {
            checkForUpdates(intent);
        } else if (intentAction.equals(ACTION_DOWNLOAD_UPDATE)) {
            downloadUpdate(intent);
        } else if (intentAction.equals(ACTION_CREATE_DEVICE)) {
            createNewDevice(intent);
        } else if (intentAction.equals(ACTION_CHANGE_PIN)) {
            resetPin(intent);
        } else if (intentAction.equals(ACTION_SUBSCRIBE_TO_ROASTER)) {
            subscribeToRoaster(intent);
        } else if (intentAction.equals(ACTION_ENABLE_NETWORK_CONNECTION)) {
            enableNetworkConnection(intent);
        } else if (intentAction.equals(ACTION_DISABLE_NETWORK_CONNECTION)) {
            disableNetworkConnection(intent);
        }
    }

    private void syncFavorite(Intent intent) {
        DefaultContentProcessor.getInstance(getApplicationContext(), this).syncFavorites();
    }

    private void login(Intent intent) {

        ContentProcessor contentProcessor = DefaultContentProcessor.getInstance(getApplicationContext(), this);
        int requestId = getRequestId(intent);

        String username = intent
                .getStringExtra(Constants.INTENT_LOGIN_USERNAME);
        String password = intent
                .getStringExtra(Constants.INTENT_LOGIN_PASSWORD);
        contentProcessor.login(requestId, username, password);

    }

    private void logout(Intent intent) {

        ContentProcessor contentProcessor = DefaultContentProcessor.getInstance(getApplicationContext(), this);
//		disableNetworkConnection(intent);
        ((DefaultContentProcessor) contentProcessor).stopQueue();

        contentProcessor.logout();

    }

    private void syncRoasters(Intent intent) {
        ContentProcessor contentProcessor = DefaultContentProcessor.getInstance(getApplicationContext(), this);

        contentProcessor.syncRoasters();
    }

    private void saveRecipe(Intent intent) {
        ContentProcessor contentProcessor = DefaultContentProcessor.getInstance(getApplicationContext(), this);

        Recipe newRecipe = mIntentParser.parseSaveRecipeIntent(intent);

        contentProcessor.saveRecipe(newRecipe);
    }

    private void syncRecipes(Intent intent) {
        ContentProcessor contentProcessor = DefaultContentProcessor.getInstance(getApplicationContext(), this);

        Log.i("GETRECIPES", "persistence service called");

        contentProcessor.syncRecipes();
    }

    private void deleteRecipe(Intent intent) {
        ContentProcessor contentProcessor = DefaultContentProcessor.getInstance(getApplicationContext(), this);

        long id = intent.getLongExtra(RecipeTable.ID, -1);

        contentProcessor.deleteRecipe(id);
    }

    private void createLog(Intent intent) {
        ContentProcessor contentProcessor = DefaultContentProcessor.getInstance(getApplicationContext(), this);

        Integer requestId = getRequestId(intent);

        // TODO pull this intent parsing into the IntentParser

        Long machineId = intent.getLongExtra(LogTable.MACHINE, -1);
        Integer severity = intent.getIntExtra(LogTable.SEVERITY, -1);
        Integer type = intent.getIntExtra(LogTable.TYPE, -1);

        String date = intent.getStringExtra(LogTable.DATE);
        String message = intent.getStringExtra(LogTable.MESSAGE);

        String recipeUuid = intent.hasExtra(LogTable.RECIPE_ID) ? intent
                .getStringExtra(LogTable.RECIPE_ID) : null;
        Integer crudibleIndex = intent.hasExtra(LogTable.CRUCIBLE) ? intent
                .getIntExtra(LogTable.CRUCIBLE, -1) : null;
        Long userId = intent.hasExtra(Constants.INTENT_EXTRA_USER_ID) ? intent
                .getLongExtra(Constants.INTENT_EXTRA_USER_ID, -1) : null;

        contentProcessor.createLog(requestId, machineId, userId, date,
                recipeUuid, crudibleIndex, severity, type, message);

    }

    private void getMachineSettings(Intent intent) {
        ContentProcessor contentProcessor = DefaultContentProcessor.getInstance(getApplicationContext(), this);
        Integer requestId = getRequestId(intent);

        String serialNum = mIntentParser
                .parseGetMachineSettingsIntent(intent);

        contentProcessor.getMachineSettings(requestId, serialNum);

    }

    private void saveMachineSettings(Intent intent) {
        ContentProcessor contentProcessor = DefaultContentProcessor.getInstance(getApplicationContext(), this);
        Integer requestId = getRequestId(intent);

        MachineSettings machineSettings = mIntentParser
                .parseSaveMachineSettingsIntent(intent);

        contentProcessor.saveMachineSettings(requestId, machineSettings);
    }

    private void saveAccountSettings(Intent intent) {
        ContentProcessor contentProcessor = DefaultContentProcessor.getInstance(getApplicationContext(), this);
        Integer requestId = getRequestId(intent);
        //separating the changed username so we can only update it if the call succeeds
        String intentusername = intent.getStringExtra(Constants.USER_SETTINGS_USERNAME);
        intent.removeExtra(Constants.USER_SETTINGS_USERNAME);
        intent.putExtra(Constants.USER_SETTINGS_USERNAME, AccountSettings.getAccountSettingsFromSharedPreferences(this).getUsername());

        AccountSettings intentAccountSettings = mIntentParser
                .parseSaveAccountIntent(intent);

        contentProcessor.saveAccountSettings(requestId, intentAccountSettings, intentusername);
    }

    private void resetPassword(Intent intent) {
        ContentProcessor contentProcessor = DefaultContentProcessor.getInstance(getApplicationContext(), this);
        Integer requestId = getRequestId(intent);
        String identifier = mIntentParser.parsePasswordResetIdentifier(intent);

        contentProcessor.resetPassword(requestId, identifier);
    }

    private void changePassword(Intent intent) {
        ContentProcessor contentProcessor = DefaultContentProcessor.getInstance(getApplicationContext(), this);
        Integer requestId = getRequestId(intent);
        String old = intent.getStringExtra(Constants.SP_OLD_PASSWORD);
        String newPass = intent.getStringExtra(Constants.SP_NEW_PASSWORD);

        contentProcessor.changePassword(requestId, old, newPass);
    }

    private void resetPin(Intent intent) {
        ContentProcessor contentProcessor = DefaultContentProcessor.getInstance(getApplicationContext(), this);
        Integer requestId = getRequestId(intent);
        long machineId = intent.getLongExtra(Constants.INTENT_MACHINE_ID, -1);
        String PIN = intent.getStringExtra(Constants.INTENT_PIN);

        contentProcessor.resetPin(requestId, machineId, PIN);
    }

    private void checkForUpdates(Intent intent) {
        ContentProcessor contentProcessor = DefaultContentProcessor.getInstance(getApplicationContext(), this);

        Integer requestId = getRequestId(intent);
        Integer versionId = intent.getIntExtra(Constants.INTENT_VERSION, 0);
        Integer deviceId = intent.getIntExtra(Constants.INTENT_DEVICE, 0);

        contentProcessor.checkForUpdates(requestId, versionId, deviceId);
    }

    private void downloadUpdate(Intent intent) {
        ContentProcessor contentProcessor = DefaultContentProcessor.getInstance(getApplicationContext(), this);
        Integer requestId = getRequestId(intent);
        Integer newVersion = intent.getIntExtra(Constants.INTENT_VERSION, 0);

        contentProcessor.downloadUpdate(requestId, newVersion);
    }

    private void createNewDevice(Intent intent) {
        ContentProcessor contentProcessor = DefaultContentProcessor.getInstance(getApplicationContext(), this);

        Integer requestId = getRequestId(intent);
        String name = intent.getStringExtra(Constants.INTENT_NAME);
        int version = intent.getIntExtra(Constants.INTENT_VERSION, 0);

        contentProcessor.createNewDevice(requestId, version, name);
    }

    private int getRequestId(Intent intent) {
        return intent.getIntExtra(Constants.INTENT_EXTRA_REQUEST_ID, 0);
    }

    private void createFavorite(Intent intent) {
        long userId = intent.getLongExtra(Constants.SP_USER_ID, 0);
        long recipeId = intent.getLongExtra(BUNDLE_RECIPE_ID, -1);

        DefaultContentProcessor contentProcessor = DefaultContentProcessor.getInstance(getApplicationContext(), this);

        contentProcessor.createFavorite(userId, recipeId);
    }

    private void deleteFavorite(Intent intent) {
        long userId = intent.getLongExtra(Constants.SP_USER_ID, 0);
        long recipeId = intent.getLongExtra(BUNDLE_RECIPE_ID, -1);

        DefaultContentProcessor contentProcessor = DefaultContentProcessor.getInstance(getApplicationContext(), this);

        contentProcessor.deleteFavorite(userId, recipeId);
    }

    private void subscribeToRoaster(Intent intent) {
        long steampunkUserId = intent.getLongExtra(Constants.SUBSCRIBE_TO_ID, 0);

        DefaultContentProcessor contentProcessor = DefaultContentProcessor.getInstance(getApplicationContext(), this);

        contentProcessor.subscribeToRoaster(steampunkUserId);
    }

    private void enableNetworkConnection(Intent intent) {
//		DefaultContentProcessor.getInstance(this, this).(); SPLog.debug("called start queue thread!");
        DefaultContentProcessor.getInstance(this, this).enableNetworkConnection(); //SPLog.debug("called start queue thread!");
    }

    private void disableNetworkConnection(Intent intent) {
        DefaultContentProcessor.getInstance(this, this).disableNetworkConnection(); //SPLog.debug("called stop queue thread!");
    }

    /**
     * @category Content Processor Callbacks
     */
    @Override
    public void getRecipesRequestFinished(int requestId, boolean successful) {
        Log.i(TAG, "Get recipes request finished persistence service callback");
        sendBundle(new Bundle(), ResultCode.SYNC_RECIPES, requestId, successful);
    }

    @Override
    public void postRecipeRequestFinished(int requestId, long recipeId, long localId,
                                          boolean successful) {
        Log.i(TAG, "Post recipe request finished persistence service callback");
        Bundle bundle = new Bundle();

        bundle.putLong(BUNDLE_NEW_RECIPE_ID, recipeId);
        bundle.putLong(BUNDLE_LOCAL_RECIPE_ID, localId);

        sendBundle(bundle, ResultCode.CREATE_RECIPE, requestId, successful);
    }

    @Override
    public void putRecipeRequestFinished(int requestId, boolean successful) {
        Log.i(TAG, "Post recipe finished persistence service callback");
        sendBundle(new Bundle(), ResultCode.UPDATE_RECIPE, requestId,
                successful);
    }

    @Override
    public void deleteRecipeRequestFinished(int requestId, boolean successful) {
        Log.i(TAG,
                "Delete recipe request finished persistence service callback");
        sendBundle(new Bundle(), ResultCode.DELETE_RECIPE, requestId,
                successful);
    }

    @Override
    public void syncGrindsRequestFinished(int requestId, boolean sucessful) {
        Log.i(TAG, "Sync grinds request finished persistence service callback");
        sendBundle(new Bundle(), ResultCode.SYNC_GRINDS, requestId, sucessful);
    }

    @Override
    public void postLogRequestFinished(int requestId, boolean successful) {
        Log.i(TAG, "Post log request finished persistence service callback");
        sendBundle(new Bundle(), ResultCode.CREATE_LOG, requestId, successful);
    }

    @Override
    public void invalidMachineSerialNumber(int requestId) {
        Log.i(TAG, "Invalid machine serial number persistence service callback");
        sendBundle(new Bundle(), ResultCode.INVALID_MACHINE_SERIAL_NUMBER,
                requestId, true);
    }

    @Override
    public void machineSettingsSaved(int requestId) {
        Log.i(TAG, "Machine settings saved persistence service callback");
        sendBundle(new Bundle(), ResultCode.MACHINE_SETTINGS_SAVED, requestId,
                true);
    }

    @Override
    public void networkError(int requestId) {
        Log.i(TAG, "Network error persistence service callback");
        sendBundle(new Bundle(), ResultCode.NETWORK_ERROR, requestId, false);
    }

    private void sendBundle(Bundle bundle, ResultCode resultCode,
                            int requestId,
                            boolean successful) {

        bundle.putInt(BUNDLE_REQUEST_ID, requestId);
        bundle.putBoolean(BUNDLE_SUCCESSFUL, successful);

        mResultReceiver.send(resultCode.ordinal(), bundle);
    }

    @Override
    public void deleteStackRequestFinished(int requestId, boolean successful) {
        Log.i(TAG, "Get stack request finished persistence service callback");
        sendBundle(new Bundle(), ResultCode.SYNC_STACKS, requestId, successful);

    }

    @Override
    public void postStackRequestFinished(int requestId, long stackId, long localId,
                                         boolean successful) {

        Log.i(TAG, "Post stack request finished persistence service callback");
        Bundle bundle = new Bundle();

        bundle.putLong(BUNDLE_NEW_STACK_ID, stackId);
        bundle.putLong(BUNDLE_LOCAL_STACK_ID, localId);

        sendBundle(bundle, ResultCode.CREATE_STACK, requestId, successful);

    }

    @Override
    public void postAgitationCycleRequestFinished(int requestId, Long id,
                                                  boolean success) {
        Log.i(TAG, "Post agitation request finished persistence service callback");
        Bundle bundle = new Bundle();

        bundle.putLong(BUNDLE_NEW_AGITATION_ID, id);

        sendBundle(bundle, ResultCode.CREATE_AGITATION, requestId, success);

    }

    @Override
    public void getAvailableUpdatesRequestFinished(int requestId, int newVersion) {
        Bundle bundle = new Bundle();

        bundle.putInt(BUNDLE_REQUEST_ID, requestId);
        bundle.putInt(BUNDLE_VERSION, newVersion);

        mResultReceiver.send(ResultCode.AVAILABLE_UPDATE.ordinal(), bundle);
    }

    @Override
    public void postPinResetRequestFinished(int requestId, String PIN, boolean successful) {
        Bundle bundle = new Bundle();

        bundle.putInt(BUNDLE_REQUEST_ID, requestId);
        bundle.putString(BUNDLE_PIN, PIN);
        bundle.putBoolean(BUNDLE_SUCCESSFUL, successful);

        mResultReceiver.send(ResultCode.PIN_RESET.ordinal(), bundle);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}

