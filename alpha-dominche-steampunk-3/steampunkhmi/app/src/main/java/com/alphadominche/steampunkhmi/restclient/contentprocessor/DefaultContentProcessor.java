package com.alphadominche.steampunkhmi.restclient.contentprocessor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantLock;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import com.alphadominche.steampunkhmi.R;
import com.alphadominche.steampunkhmi.SPServiceReceiver;
import com.alphadominche.steampunkhmi.contentprovider.Provider;
import com.alphadominche.steampunkhmi.database.tables.FavoriteTable;
import com.alphadominche.steampunkhmi.database.tables.PersistenceQueueTable;
import com.alphadominche.steampunkhmi.database.tables.RecipeTable;
import com.alphadominche.steampunkhmi.database.tables.RoasterTable;
import com.alphadominche.steampunkhmi.model.Recipe;
import com.alphadominche.steampunkhmi.restclient.networkclient.DefaultNetworkClient;
import com.alphadominche.steampunkhmi.restclient.networkclient.NetworkClient;
import com.alphadominche.steampunkhmi.restclient.networkclient.NetworkClient.NetworkClientCallbacks;
import com.alphadominche.steampunkhmi.restclient.networkclient.RemoteClasses.RemoteFavorite;
import com.alphadominche.steampunkhmi.restclient.networkclient.RemoteClasses.RemoteGrind;
import com.alphadominche.steampunkhmi.restclient.networkclient.RemoteClasses.RemoteLog;
import com.alphadominche.steampunkhmi.restclient.networkclient.RemoteClasses.RemoteLogin;
import com.alphadominche.steampunkhmi.restclient.networkclient.RemoteClasses.RemoteMachine;
import com.alphadominche.steampunkhmi.restclient.networkclient.RemoteClasses.RemoteRecipe;
import com.alphadominche.steampunkhmi.restclient.networkclient.RemoteClasses.RemoteRoaster;
import com.alphadominche.steampunkhmi.restclient.networkclient.RemoteClasses.RemoteSteamPunkUser;
import com.alphadominche.steampunkhmi.restclient.networkclient.RemoteClasses.RemoteUser;
import com.alphadominche.steampunkhmi.restclient.networkclient.RemoteClasses.UserIdMapping;
import com.alphadominche.steampunkhmi.restclient.persistenceservicehelper.DefaultPersistenceServiceHelperEvents;
import com.alphadominche.steampunkhmi.utils.AccountSettings;
import com.alphadominche.steampunkhmi.utils.Constants;
import com.alphadominche.steampunkhmi.utils.MachineSettings;
import com.alphadominche.steampunkhmi.utils.SteampunkUtils;
import de.greenrobot.event.EventBus;

public class DefaultContentProcessor implements ContentProcessor,
        NetworkClientCallbacks {

    public final static long Q_RETRY_RATE = 60L;
    public final static long PERSISTENCE_QUEUE_CYCLE_DELAY = 1000L;
    public final static long ONE_SECOND_IN_MILLIS = 1000L;
    public final static String USERNAME_ALREADY_EXISTS_MESSAGE = "User with this Username already exists";
    public final static String DEPENDENCY_DELIMITER = ",";

    private ReentrantLock mPersistLock;

    private Context mContext;
    private static ContentProcessorCallbacks sContentProcessorCallbacks;
    private MachineSettings mTempMachineSettings;
    private boolean mMachineSettingsSaving;
    private boolean mIsFirstSync = true; // adding this to make sure the first
    // sync doesn't delete recipes this is
    // here for migration purposes so that
    // recipes that aren't on the server can
    // make it up.
    private boolean mConnected;
    private ContentResolver mContentResolver;

    private RequestQueueProcessor mThread;

    private void startQueueThread() {
        mThread.mConnected = mConnected;
        mThread.run();
//		mContext.sendOrderedBroadcast(new Intent(SPServiceReceiver.NETWORK_CONNECTED_BROADCAST), SPServiceReceiver.STEAM_PUNK_CONTROL_PERMISSION);
    }

    public void enableNetworkConnection() {
        mThread.enableConnection(); //SPLog.debug("got told to be connected");
        mConnected = true;
//		mThread.mConnected = mConnected;
        saveConnectionStatus();
        mContext.sendOrderedBroadcast(new Intent(SPServiceReceiver.NETWORK_CONNECTED_BROADCAST), SPServiceReceiver.STEAM_PUNK_CONTROL_PERMISSION);
    }

    public void disableNetworkConnection() {
        mThread.disableConnection(); //SPLog.debug("got told to be disconnected");
        mConnected = false;
//		mThread.mConnected = mConnected;
        saveConnectionStatus();
        mContext.sendOrderedBroadcast(new Intent(SPServiceReceiver.NETWORK_NOT_CONNECTED_BROADCAST), SPServiceReceiver.STEAM_PUNK_CONTROL_PERMISSION);
    }

    public void stopQueue() {
        mThread.stopThread();
    }

    private void saveConnectionStatus() {
        MachineSettings machine = MachineSettings.getMachineSettingsFromSharedPreferences(mContext);
        machine.setLocalOnly(!mConnected);
        MachineSettings.writeMachineSettingsToSharedPreferences(
                machine, mContext);
    }

    private static DefaultContentProcessor sInstance = null;

    public static DefaultContentProcessor getInstance(Context context,
                                                      ContentProcessorCallbacks callbacks) {
        if (callbacks != null) {
            sContentProcessorCallbacks = callbacks;
        }

        if (sInstance == null) {
            sInstance = new DefaultContentProcessor(context);
        }

        return sInstance;
    }

    private DefaultContentProcessor(Context context) {
        mContext = context;

        mConnected = true;
        mContext.sendOrderedBroadcast(new Intent(SPServiceReceiver.NETWORK_CONNECTED_BROADCAST), SPServiceReceiver.STEAM_PUNK_CONTROL_PERMISSION);
        mMachineSettingsSaving = false;
        mPersistLock = new ReentrantLock();

        mContentResolver = mContext.getContentResolver();

        mThread = new RequestQueueProcessor();
        mThread.start();
    }

    /**
     * Login methods
     */

    @Override
    public void login(int requestId, String username, String password) {
        NetworkClient networkClient = new DefaultNetworkClient(mContext, this);

        networkClient.postLogin(requestId, username, password);
    }

    @Override
    public void postLoginRequestFinished(int requestId,
                                         RemoteLogin remoteLogin, final boolean successful,
                                         final String errorMessage, int status) { //SPLog.debug("remote login: " + remoteLogin); SPLog.debug("err: " + errorMessage + " successful: " + successful); //SPLog.debug("spuID: " + remoteLogin.steampunkuserId);
        mPersistLock.lock();

        SharedPreferences preferences = mContext.getSharedPreferences(
                Constants.SHARED_PREFS_NAME, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = preferences.edit(); //SPLog.debug("editor: " + editor);
        if (true == successful) {
            editor.putString(Constants.SP_USERNAME, remoteLogin.username);
            editor.putString(Constants.SP_AUTH_TOKEN, remoteLogin.token);
            editor.putString(Constants.SP_USER_TYPE, remoteLogin.type);
            editor.putLong(Constants.SP_STEAMPUNKUSER_ID,
                    remoteLogin.steampunkuserId);
            editor.putLong(Constants.SP_USER_ID, remoteLogin.id);
            editor.commit();// we need to do this before we call any syncing
            // functions
            AccountSettings accountSettings = new AccountSettings(
                    remoteLogin.username, remoteLogin.email,
                    remoteLogin.address, remoteLogin.city, remoteLogin.state,
                    remoteLogin.country, remoteLogin.postal_code,
                    remoteLogin.public_status);
            AccountSettings.writeAccountSettingsToSharedPreferences(
                    accountSettings, mContext);
        } else {
            editor.commit();
        }

        Runnable loginRunnable = new Runnable() {
            public void run() {
                EventBus.getDefault().post(
                        new DefaultPersistenceServiceHelperEvents.LoginEvent(
                                successful, errorMessage));
            }
        };

        new Thread(loginRunnable).start();

        mPersistLock.unlock();

        if (true == successful && mThread.keepRunning == false) {
            startQueueThread();
        }
    }

    @Override
    public void logout() {
        mPersistLock.lock();

        // TODO we may need to clear setting and preference information
        mContentResolver.delete(Provider.RECIPE_CONTENT_URI, null, null);
        mContentResolver.delete(Provider.STACK_CONTENT_URI, null, null);
        mContentResolver.delete(Provider.AGITATIONCYCLE_CONTENT_URI, null, null);
        mContentResolver.delete(Provider.PERSISTENCEQUEUE_CONTENT_URI, null, null);
        mContentResolver.delete(Provider.REMOTEIDMAPPING_CONTENT_URI, null, null);
        mContentResolver.delete(Provider.FAVORITE_CONTENT_URI, null, null);
        mContentResolver.delete(Provider.ROASTER_CONTENT_URI, null, null);

        mPersistLock.unlock();
    }

    @Override
    public void syncRoasters() {
        if (!mConnected) return;

        mPersistLock.lock();

        ContentValues persistenceQContentValues = new ContentValues();

        persistenceQContentValues.put(PersistenceQueueTable.REQUEST_COUNT, 0);
        persistenceQContentValues.put(PersistenceQueueTable.REQUEST_TYPE, Constants.GET);
        persistenceQContentValues.put(PersistenceQueueTable.TBL_NAME, RoasterTable.TABLE_NAME);
        persistenceQContentValues.put(PersistenceQueueTable.STATE, Constants.STATE_NEEDS_REPOSTING);

        mContentResolver.insert(Provider.PERSISTENCEQUEUE_CONTENT_URI, persistenceQContentValues);

        mPersistLock.unlock();
    }

    @Override
    public List<UserIdMapping> getUserMapping() {
        NetworkClient networkClient = new DefaultNetworkClient(mContext, this);

        return networkClient.getUserMapping();
    }

    @Override
    public void saveRecipe(Recipe recipe) {
        mPersistLock.lock();

        if (recipe.getId() == -1L) {
            // create recipe
            createLocalRecipe(recipe); // expect the id and uuid in the object in RAM to
            // be updated!
            if (mConnected) {
                createRemoteRecipe(recipe, new ArrayList<Long>());
            }
            // if the recipe is old...
        } else if (recipe.getId() > 0L) {
            //fetch UUID from database
            Cursor c = mContentResolver.query(Provider.RECIPE_CONTENT_URI, RecipeTable.ALL_COLUMNS, RecipeTable.WHERE_ID_EQUALS, new String[]{Long.toString(recipe.getId())}, null);
            c.moveToFirst();
            if (!c.isAfterLast()) {
                recipe.setUuid(c.getString(c.getColumnIndex(RecipeTable.UUID)));
                c.close();
                updateLocalRecipe(recipe);
                if (mConnected) {
                    updateRemoteRecipe(recipe, getDependencies(RecipeTable.TABLE_NAME, recipe.getId()));
                }
            } else { //create a new recipe if this one isn't in the database
                createLocalRecipe(recipe);

                if (mConnected) {
                    createRemoteRecipe(recipe, new ArrayList<Long>());
                }
            }
        }

        mPersistLock.unlock();
    }

    @Override
    public void deleteRecipe(long id) { //SPLog.debug("recipe id to delete: " + id);
        mPersistLock.lock();

        Cursor c = mContentResolver.query(Provider.RECIPE_CONTENT_URI, RecipeTable.ALL_COLUMNS, RecipeTable.WHERE_ID_EQUALS, new String[]{Long.toString(id)}, null);

        //the fix for the delete on recipe crash, probably needed to keep MyRecipes from crashing when deleting the same recipe twice
        if (c.moveToFirst()) {
            String uuid = c.getString(c.getColumnIndex(RecipeTable.UUID));

            deleteLocalRecipe(id);
            if (mConnected) {
                deleteRemoteRecipe(uuid, getDependencies(RecipeTable.TABLE_NAME, id));
            }
        }
        c.close();

        //the original code...might not have to change it because the multiple deletes could be from the MyRecipes screen not updating
//		c.moveToFirst();
//		String uuid = c.getString(c.getColumnIndex(RecipeTable.UUID));
//		c.close();
//
//		deleteLocalRecipe(id);
//		if (mConnected) {
//			deleteRemoteRecipe(uuid, new ArrayList<Long>());
//		}

        mPersistLock.unlock();
    }

    /**
     * Creates the recipe record in the database, making sure to update its ID
     * if necessary
     *
     * @param recipe
     */
    private void createLocalRecipe(Recipe recipe) {
        mPersistLock.lock();

        recipe.setUuid(UUID.randomUUID().toString());

        ContentValues newRecipeContentValues = getRecipeContentValues(recipe);

        newRecipeContentValues.put(RecipeTable.TRANSACTION_STATE,
                Constants.STATE_POSTING);

        mContentResolver.insert(Provider.RECIPE_CONTENT_URI, newRecipeContentValues);

        mPersistLock.unlock();
    }

    /**
     * Updates the recipe record in the database
     *
     * @param recipe
     */
    private void updateLocalRecipe(Recipe recipe) {
        mPersistLock.lock();

        ContentValues updatedRecipeContentValues = getRecipeContentValues(recipe);

        updatedRecipeContentValues.put(RecipeTable.TRANSACTION_STATE,
                Constants.STATE_PUTTING);

        String selectionClause = RecipeTable.WHERE_ID_EQUALS;
        String[] selectionArgs = {Long.toString(recipe.getId())};

        mContentResolver.update(Provider.RECIPE_CONTENT_URI,
                updatedRecipeContentValues, selectionClause, selectionArgs);

        mPersistLock.unlock();
    }

    /**
     * Deletes the recipe record in the database
     *
     * @param recipe
     */
    private void deleteLocalRecipe(long id) {
        mPersistLock.lock();

        mContentResolver
                .delete(Provider.RECIPE_CONTENT_URI,
                        RecipeTable.WHERE_ID_EQUALS,
                        new String[]{Long.toString(id)});

        mPersistLock.unlock();
    }

    /**
     * Convert ArrayList of dependencies to String
     *
     * @param dependencies
     * @return commaDelimittedDependencies
     */
    private String unParseDependencies(ArrayList<Long> dependencies) {
        String commaDelimittedDependencies = "";

        int size = dependencies.size();
        for (int i = 0; i < size; i++) {
            commaDelimittedDependencies += dependencies.get(i)
                    + ((i == size - 1) ? "" : ",");
        }

        return commaDelimittedDependencies;
    }

    /**
     * Accumulate ArrayList of active queued up requests for the specified id
     *
     * @param objectType the table name of the object whose dependencies are being retrieved
     * @param id
     * @return list of dependencies
     */
    private ArrayList<Long> getDependencies(String objectType, long id) {
        Cursor c = mContentResolver.query(Provider.PERSISTENCEQUEUE_CONTENT_URI,
                new String[]{PersistenceQueueTable.ID},
                PersistenceQueueTable.TBL_NAME + "=? AND " + PersistenceQueueTable.OBJ_ID + "=? AND " + PersistenceQueueTable.STATE + "!=?",
                new String[]{objectType, "" + id, Constants.STATE_OK},
                null);
        c.moveToFirst();
        ArrayList<Long> ids = new ArrayList<Long>();
        while (!c.isAfterLast()) {
            ids.add(c.getLong(c.getColumnIndex(PersistenceQueueTable.ID)));
            c.moveToNext();
        }
        return ids;
    }

    /**
     * Queues a request to make a remote record of this recipe
     *
     * @param recipe
     * @param dependencies list of db request ids this depends on
     * @return the db id of the request record
     */
    public long createRemoteRecipe(Recipe recipe, ArrayList<Long> dependencies) {
        mPersistLock.lock();

        String commaDelimittedDependencies = unParseDependencies(dependencies);
        long now = (System.currentTimeMillis() / 1000L);

        ContentValues persistenceQContentValues = new ContentValues();
        persistenceQContentValues.put(PersistenceQueueTable.BACKPOINTERS,
                commaDelimittedDependencies);
        persistenceQContentValues.put(PersistenceQueueTable.OBJ_ID,
                recipe.getId());
        persistenceQContentValues.put(PersistenceQueueTable.OBJ_UUID,
                recipe.getUuid());
        persistenceQContentValues.put(PersistenceQueueTable.REQUEST_COUNT, 0);
        persistenceQContentValues.put(PersistenceQueueTable.REQUEST_TYPE,
                Constants.POST);
        persistenceQContentValues.put(PersistenceQueueTable.TBL_NAME,
                RecipeTable.TABLE_NAME);
        persistenceQContentValues.put(PersistenceQueueTable.STATE,
                Constants.STATE_NEEDS_REPOSTING);
        persistenceQContentValues.put(PersistenceQueueTable.LAST_ATTEMPT,
                (now - Q_RETRY_RATE));
        long recordId = ContentUris.parseId(mContentResolver.insert(
                Provider.PERSISTENCEQUEUE_CONTENT_URI,
                persistenceQContentValues));

        mPersistLock.unlock();

        return recordId;
    }

    /**
     * Queue a request to update the remote record of this recipe
     *
     * @param recipe
     * @param dependencies list of db request ids this depends on
     * @return the db id of the request record
     */
    private long updateRemoteRecipe(Recipe recipe, ArrayList<Long> dependencies) {
        mPersistLock.lock();

        String commaDelimittedDependencies = unParseDependencies(dependencies);
        long now = (System.currentTimeMillis() / 1000L);

        ContentValues persistenceQContentValues = new ContentValues();
        persistenceQContentValues.put(PersistenceQueueTable.BACKPOINTERS,
                commaDelimittedDependencies);
        persistenceQContentValues.put(PersistenceQueueTable.OBJ_ID,
                recipe.getId());
        persistenceQContentValues.put(PersistenceQueueTable.OBJ_UUID,
                recipe.getUuid());
        persistenceQContentValues.put(PersistenceQueueTable.REQUEST_COUNT, 0);
        persistenceQContentValues.put(PersistenceQueueTable.REQUEST_TYPE,
                Constants.PUT);
        persistenceQContentValues.put(PersistenceQueueTable.TBL_NAME,
                RecipeTable.TABLE_NAME);
        persistenceQContentValues.put(PersistenceQueueTable.STATE,
                Constants.STATE_HAS_UNSAVED_CHANGES);
        persistenceQContentValues.put(PersistenceQueueTable.LAST_ATTEMPT,
                (now - Q_RETRY_RATE));
        long recordId = ContentUris.parseId(mContentResolver.insert(
                Provider.PERSISTENCEQUEUE_CONTENT_URI,
                persistenceQContentValues));

        mPersistLock.unlock();

        return recordId;
    }

    /**
     * Queue a request to delete the remote record of this recipe
     *
     * @param recipe
     * @param dependencies list of db request ids this depends on
     * @return the db id of the request record
     */
    private long deleteRemoteRecipe(String uuid, ArrayList<Long> dependencies) {
        mPersistLock.lock();

        String commaDelimittedDependencies = unParseDependencies(dependencies);
        long now = (System.currentTimeMillis() / 1000L);

        ContentValues persistenceQContentValues = new ContentValues();
        persistenceQContentValues.put(PersistenceQueueTable.BACKPOINTERS,
                commaDelimittedDependencies);
        persistenceQContentValues.put(PersistenceQueueTable.OBJ_UUID, uuid);
        persistenceQContentValues.put(PersistenceQueueTable.REQUEST_COUNT, 0);
        persistenceQContentValues.put(PersistenceQueueTable.REQUEST_TYPE,
                Constants.DELETE);
        persistenceQContentValues.put(PersistenceQueueTable.TBL_NAME,
                RecipeTable.TABLE_NAME);
        persistenceQContentValues.put(PersistenceQueueTable.STATE,
                Constants.STATE_HAS_UNSAVED_CHANGES);
        persistenceQContentValues.put(PersistenceQueueTable.LAST_ATTEMPT,
                (now - Q_RETRY_RATE));
        long recordId = ContentUris.parseId(mContentResolver.insert(
                Provider.PERSISTENCEQUEUE_CONTENT_URI,
                persistenceQContentValues));

        mPersistLock.unlock();

        return recordId;
    }


    private ContentValues getRecipeContentValues(Recipe newRecipe) {
        ContentValues recipeContentValues = new ContentValues();

        recipeContentValues.put(RecipeTable.NAME, newRecipe.getName());
        recipeContentValues.put(RecipeTable.TYPE, newRecipe.getType());
        recipeContentValues.put(RecipeTable.STEAMPUNK_USER_ID,
                newRecipe.getSteampunk_user_id());
        recipeContentValues
                .put(RecipeTable.PUBLISHED, newRecipe.getPublished());
        recipeContentValues.put(RecipeTable.GRAMS, newRecipe.getGrams());
        recipeContentValues
                .put(RecipeTable.TEASPOONS, newRecipe.getTeaspoons());
        recipeContentValues.put(RecipeTable.GRIND, newRecipe.getGrind());
        recipeContentValues.put(RecipeTable.FILTER, newRecipe.getFilter());
        recipeContentValues.put(RecipeTable.STACKS, newRecipe.getStacks());
        recipeContentValues.put(RecipeTable.UUID, newRecipe.getUuid());

        return recipeContentValues;
    }

    @Override
    public void syncRecipes() {
        if (!mConnected) return;

        mPersistLock.lock();

        NetworkClient networkClient = new DefaultNetworkClient(mContext, this);

        long timestamp = SteampunkUtils.getLastRecipeSyncDate(mContext);

        networkClient.getRecipes(SteampunkUtils.getRequestId(mContext),
                timestamp);

        mPersistLock.unlock();
    }

    public void syncFavorites() {
        if (!mConnected) return;

        mPersistLock.lock();

        NetworkClient networkClient = new DefaultNetworkClient(mContext, this);
        long timeStamp = SteampunkUtils.getLastFavoritesSyncDate(mContext);

        networkClient.getFavorites(SteampunkUtils.getRequestId(mContext),
                timeStamp);

        mPersistLock.unlock();
    }

    @Override
    public void createLog(Integer requestId, Long machineId, Long userId,
                          String date, String recipeUuid, Integer crucibleIndex,
                          Integer severity, Integer type, String message) {
        if (!mConnected) return;

        mPersistLock.lock();

        NetworkClient networkClient = new DefaultNetworkClient(mContext, this);

        RemoteLog newLog = new RemoteLog(requestId, machineId, userId, date,
                crucibleIndex, recipeUuid, severity, type, message);

        networkClient.postLog(requestId, newLog);

        mPersistLock.unlock();
    }

    @Override
    public void getMachineSettings(Integer requestId, String serialNum) {
        if (mMachineSettingsSaving || !mConnected) {
            return;
        }
        mPersistLock.lock();

        mTempMachineSettings = MachineSettings
                .getMachineSettingsFromSharedPreferences(mContext);
        NetworkClient networkClient = new DefaultNetworkClient(mContext, this);
        networkClient.getMachineWithSerialNumber(requestId, serialNum, false);

        mPersistLock.unlock();
    }

    @Override
    public void saveMachineSettings(int requestId,
                                    MachineSettings machineSettings) {
        if (!mConnected) {
            MachineSettings.writeMachineSettingsToSharedPreferences(
                    machineSettings, mContext);
            sContentProcessorCallbacks.machineSettingsSaved(requestId);
            return;
        }

        mMachineSettingsSaving = true;
        mPersistLock.lock();

        mTempMachineSettings = machineSettings;
        NetworkClient networkClient = new DefaultNetworkClient(mContext, this);

        if (machineSettings.getSerialNumber().isEmpty())
            sContentProcessorCallbacks.invalidMachineSerialNumber(requestId);
        else {
            networkClient.getMachineWithSerialNumber(requestId,
                    machineSettings.getSerialNumber(), true);
        }

        mPersistLock.unlock();
    }

    @Override
    public void saveAccountSettings(int requestId,
                                    AccountSettings accountSettings, String requestedUsername) {

        mPersistLock.lock();

        if (!mConnected) {
            AccountSettings.writeAccountSettingsToSharedPreferences(
                    accountSettings, mContext);
            mPersistLock.unlock();
            return;
        }

        AccountSettings.writeAccountSettingsToSharedPreferences(
                accountSettings, mContext);
        accountSettings = new AccountSettings(requestedUsername, accountSettings.getEmail(), accountSettings.getAddress(),
                accountSettings.getCity(), accountSettings.getState(), accountSettings.getCountry(), accountSettings.getZipCode(), accountSettings.getProtectRecipes());
        Long steampunkId = SteampunkUtils.getCurrentSteampunkUserId(mContext);
        Long userId = SteampunkUtils.getCurrentUserId(mContext);
        NetworkClient networkClient = new DefaultNetworkClient(mContext, this);
        RemoteUser ru = new RemoteUser();
        ru.email = accountSettings.getEmail();
        RemoteSteamPunkUser rspu = new RemoteSteamPunkUser();
        rspu.address = accountSettings.getAddress();
        rspu.state = accountSettings.getState();
        rspu.city = accountSettings.getCity();
        rspu.country = accountSettings.getCountry();
        rspu.postal_code = accountSettings.getZipCode();
        ru.username = accountSettings.getUsername();

        networkClient.updateAccountSettings(requestId, ru, rspu, steampunkId,
                userId, requestedUsername);

        mPersistLock.unlock();
    }

    @Override
    public void resetPassword(int requestId, String identifier) {
        if (!mConnected) return;

        NetworkClient networkClient = new DefaultNetworkClient(mContext, this);
        String machine_id = Long.toString(SteampunkUtils.getMachineId(mContext));
        networkClient.postPasswordReset(requestId, identifier, machine_id);
    }

    @Override
    public void changePassword(Integer requestId, String old, String newPass) {
        NetworkClient networkClient = new DefaultNetworkClient(mContext, this);

        networkClient.postPasswordChange(requestId, old, newPass);
    }

    @Override
    public void resetPin(int requestId, long machineId, String PIN) {
        NetworkClient networkClient = new DefaultNetworkClient(mContext, this);
        networkClient.postPinReset(requestId, machineId, PIN);
    }

    @Override
    public void postSubscribeToRoasterFinished(int requestId,
                                               final boolean successful, int status) {
        EventBus.getDefault().post(
                new DefaultPersistenceServiceHelperEvents.SubscribeEvent(
                        successful));
    }

    @Override
    public void checkForUpdates(int requestId, int versionId, int deviceId) {
        NetworkClient networkClient = new DefaultNetworkClient(mContext, this);
        networkClient.checkForUpdates(requestId, versionId, deviceId);
    }

    @Override
    public void downloadUpdate(int requestId, int newVersion) {
        NetworkClient networkClient = new DefaultNetworkClient(mContext, this);
        networkClient.downloadUpdate(requestId, newVersion);
    }

    @Override
    public void createNewDevice(int requestId, int version, String name) {
        NetworkClient networkClient = new DefaultNetworkClient(mContext, this);
        networkClient.createNewDevice(requestId, version, name);
    }

    /**
     * @category Network Client callbacks
     */

    @Override
    public void getRoastersRequestFinished(int requestId,
                                           List<RemoteRoaster> remoteRoasters, boolean successful, int status) {
        mPersistLock.lock();

        Set<Long> ids = new HashSet<Long>();

        // Set state of queue request
        updateQueue(requestId, successful);

        if (successful) {
            for (RemoteRoaster roaster : remoteRoasters) {
                ids.add((Long) roaster.id);

                ContentValues roasterValues = new ContentValues();
                roasterValues.put(RoasterTable.ID, roaster.id);
                roasterValues.put(RoasterTable.FIRST_NAME, roaster.first_name);
                roasterValues.put(RoasterTable.LAST_NAME, roaster.last_name);
                roasterValues.put(RoasterTable.USERNAME, roaster.username);
                roasterValues.put(RoasterTable.STEAMPUNK_ID,
                        roaster.steampunkuser);
                roasterValues.put(RoasterTable.SUBSCRIBED_TO,
                        roaster.subscribed_to);

                Cursor roasterCursor = mContentResolver.query(
                        Provider.ROASTER_CONTENT_URI,
                        new String[]{RoasterTable.ID.toString()},
                        RoasterTable.WHERE_ID_EQUALS,
                        new String[]{roaster.id.toString()}, null);

                // Test if roaster exists in local data
                if (roasterCursor.getCount() > 0) {
                    mContentResolver.update(Provider.ROASTER_CONTENT_URI,
                            roasterValues, RoasterTable.WHERE_ID_EQUALS,
                            new String[]{roaster.id.toString()});
                } else {
                    mContentResolver.insert(Provider.ROASTER_CONTENT_URI,
                            roasterValues);
                }
                roasterCursor.close();
            }

            // Get all roaster IDs
            Cursor roasterCursor = mContentResolver.query(
                    Provider.ROASTER_CONTENT_URI,
                    new String[]{RoasterTable.ID.toString()}, null, null,
                    null);

            // Clean up deleted roasters
            Integer index = roasterCursor.getColumnIndex(RoasterTable.ID);
            while (roasterCursor.moveToNext()) {
                Long id = roasterCursor.getLong(index);
                if (!ids.contains(id)) {
                    mContentResolver.delete(Provider.ROASTER_CONTENT_URI,
                            RoasterTable.WHERE_ID_EQUALS,
                            new String[]{id.toString()});
                }
            }
            roasterCursor.close();
        }

        mPersistLock.unlock();
    }

    public void createRemoteFavorite(long favoriteId, String uuid) {
        mPersistLock.lock();

        // queue up the request to store remotely...
        ContentValues persistenceQContentValues = new ContentValues();

        persistenceQContentValues.put(PersistenceQueueTable.REQUEST_COUNT, 0);
        persistenceQContentValues.put(PersistenceQueueTable.OBJ_ID, favoriteId);
        persistenceQContentValues.put(PersistenceQueueTable.OBJ_UUID, uuid);
        persistenceQContentValues.put(PersistenceQueueTable.REQUEST_TYPE,
                Constants.POST);
        persistenceQContentValues.put(PersistenceQueueTable.TBL_NAME,
                FavoriteTable.TABLE_NAME);
        persistenceQContentValues.put(PersistenceQueueTable.STATE,
                Constants.STATE_NEEDS_REPOSTING);
        long now = (System.currentTimeMillis() / 1000L);
        persistenceQContentValues.put(PersistenceQueueTable.LAST_ATTEMPT,
                (now - Q_RETRY_RATE));

        mContentResolver.insert(Provider.PERSISTENCEQUEUE_CONTENT_URI,
                persistenceQContentValues);

        mPersistLock.unlock();
    }

    public void subscribeToRoaster(long steampunkUserId) {
        NetworkClient networkClient = new DefaultNetworkClient(mContext, this);

        networkClient.subscribeToRoaster(SteampunkUtils.getRequestId(mContext),
                steampunkUserId);
    }

    public void createFavorite(long userId, long recipeId) {
        mPersistLock.lock();

        // create local favorite...
        ContentValues favContent = new ContentValues();
        String uuid = UUID.randomUUID().toString();

        String recipeUuid;
        Cursor c = mContentResolver.query(Provider.RECIPE_CONTENT_URI, new String[]{RecipeTable.UUID}, RecipeTable.WHERE_ID_EQUALS, new String[]{Long.toString(recipeId)}, null);
        c.moveToFirst();
        recipeUuid = c.getString(c.getColumnIndex(RecipeTable.UUID));

        favContent.put(FavoriteTable.USER, userId);
        favContent.put(FavoriteTable.RECIPE_ID, recipeId);
        favContent.put(FavoriteTable.RECIPE_UUID, recipeUuid);
        favContent.put(FavoriteTable.UUID, uuid);

        long favoriteId = ContentUris.parseId(mContentResolver.insert(Provider.FAVORITE_CONTENT_URI, favContent));

        if (mConnected) {
            createRemoteFavorite(favoriteId, uuid);
        }

        mPersistLock.unlock();
    }

    public void deleteFavorite(long userId, String recipeUuid) {
        Cursor c = mContentResolver.query(Provider.FAVORITE_CONTENT_URI, FavoriteTable.ALL_COLUMNS, FavoriteTable.RECIPE_UUID + "=?", new String[]{recipeUuid}, null);
        if (c.moveToFirst()) {
            long recipeId = c.getLong(c.getColumnIndex(FavoriteTable.RECIPE_ID));
            deleteFavorite(userId, recipeId);
        }
        c.close();
    }

    public void deleteFavorite(long userId, long recipeId) { //SPLog.debug("delete favorite called...");
        (new Exception()).printStackTrace();
        mPersistLock.lock();

        Cursor cursor = mContentResolver.query(Provider.FAVORITE_CONTENT_URI,
                FavoriteTable.ALL_COLUMNS, FavoriteTable.RECIPE_ID
                        + "=? AND " + FavoriteTable.USER + "=?", new String[]{
                        Long.toString(recipeId), Long.toString(userId)}, null);

        String uuid;
        long favoriteId;
        if (cursor.moveToFirst()) {
            favoriteId = cursor
                    .getLong(cursor.getColumnIndex(FavoriteTable.ID));
            uuid = cursor.getString(cursor.getColumnIndex(FavoriteTable.UUID));
            cursor.close();
        } else {
            cursor.close();
            mPersistLock.unlock();

            return;
        }
        mContentResolver.delete(Provider.FAVORITE_CONTENT_URI,
                FavoriteTable.WHERE_ID_EQUALS,
                new String[]{Long.toString(favoriteId)});

        if (mConnected) {
            String deps = unParseDependencies(getDependencies(FavoriteTable.TABLE_NAME, favoriteId)); //SPLog.debug("inserting delete favorite request into queue! deps: " + deps);
            ContentValues persistenceQContentValues = new ContentValues();

            persistenceQContentValues.put(PersistenceQueueTable.REQUEST_COUNT, 0);
            persistenceQContentValues.put(PersistenceQueueTable.REQUEST_TYPE,
                    Constants.DELETE);
            persistenceQContentValues.put(PersistenceQueueTable.BACKPOINTERS, deps);
            persistenceQContentValues.put(PersistenceQueueTable.OBJ_ID, favoriteId);
            persistenceQContentValues.put(PersistenceQueueTable.OBJ_UUID, uuid);
            persistenceQContentValues.put(PersistenceQueueTable.TBL_NAME,
                    FavoriteTable.TABLE_NAME);
            persistenceQContentValues.put(PersistenceQueueTable.STATE,
                    Constants.STATE_NEEDS_REPOSTING);
            long now = (System.currentTimeMillis() / 1000L);
            persistenceQContentValues.put(PersistenceQueueTable.LAST_ATTEMPT,
                    (now - Q_RETRY_RATE));

            mContentResolver.insert(Provider.PERSISTENCEQUEUE_CONTENT_URI,
                    persistenceQContentValues);
        }

        mPersistLock.unlock();
    }

    @Override
    public void getRecipesRequestFinished(int requestId,
                                          List<RemoteRecipe> remoteRecipes, boolean successful, int status) { //SPLog.debug("recipes came back");
        mPersistLock.lock();

        updateQueue(requestId, successful);

        if (successful) {
            Set<String> recipeSet = new TreeSet<String>();
            Set<String> recipesToDeleteSet = new TreeSet<String>();
            for (RemoteRecipe remoteRecipe : remoteRecipes) {
                recipeSet.add(remoteRecipe.uuid);
            }
            Cursor storedRecipeCursor = mContentResolver.query(
                    Provider.RECIPE_CONTENT_URI,
                    new String[]{RecipeTable.UUID}, null, null, null);
            if (storedRecipeCursor.moveToFirst()) {
                while (!storedRecipeCursor.isAfterLast()) {
                    String uuid = storedRecipeCursor.getString(storedRecipeCursor
                            .getColumnIndex(RecipeTable.UUID));
                    if (!(recipeSet.contains(uuid))) {
                        recipesToDeleteSet.add(uuid);
                    }
                    storedRecipeCursor.moveToNext();
                }
            }
            if (!mIsFirstSync) {
                Iterator<String> it = recipesToDeleteSet.iterator();

                while (it.hasNext()) {
                    String uuid = it.next();
                    mContentResolver.delete(Provider.RECIPE_CONTENT_URI,
                            RecipeTable.WHERE_UUID_EQUALS, new String[]{uuid});
                    this.deleteFavorite(
                            SteampunkUtils.getCurrentUserId(mContext)
                                    .longValue(), uuid);

                }
            }
            for (RemoteRecipe remoteRecipe : remoteRecipes) {
                // get content values for the remote recipe
                ContentValues recipeContentValues = new ContentValues();
                recipeContentValues.put(RecipeTable.UUID, remoteRecipe.uuid); //SPLog.debug("syncing recipe: " + remoteRecipe.uuid);
                recipeContentValues.put(RecipeTable.NAME, remoteRecipe.name);
                recipeContentValues.put(RecipeTable.TYPE, remoteRecipe.type);
                recipeContentValues.put(RecipeTable.STEAMPUNK_USER_ID,
                        remoteRecipe.steampunkuser);
                recipeContentValues.put(RecipeTable.PUBLISHED,
                        remoteRecipe.published);
                recipeContentValues.put(RecipeTable.GRAMS, remoteRecipe.grams);
                recipeContentValues.put(RecipeTable.TEASPOONS,
                        remoteRecipe.teaspoons);
                recipeContentValues.put(RecipeTable.GRIND, remoteRecipe.grind);
                recipeContentValues
                        .put(RecipeTable.FILTER, remoteRecipe.filter);
                recipeContentValues.put(RecipeTable.STACKS, remoteRecipe.stacks);

                Cursor recipeCursor = mContentResolver.query(
                        Provider.RECIPE_CONTENT_URI,
                        new String[]{RecipeTable.ID},
                        RecipeTable.WHERE_UUID_EQUALS,
                        new String[]{remoteRecipe.uuid}, null);

                recipeCursor.moveToFirst();

                if (recipeCursor.getCount() > 0) { // Already exists in database
                    String selectionVars = PersistenceQueueTable.OBJ_UUID
                            + "=? AND " + PersistenceQueueTable.TBL_NAME + "=?";
                    String[] selectionArgs = new String[]{
                            remoteRecipe.uuid, RecipeTable.TABLE_NAME};

                    // Get columns requestId, recipeId, and state for current request
                    Cursor recordCursor = mContentResolver.query(
                            Provider.PERSISTENCEQUEUE_CONTENT_URI,
                            new String[]{PersistenceQueueTable.ID,
                                    PersistenceQueueTable.OBJ_ID,
                                    PersistenceQueueTable.STATE,
                                    PersistenceQueueTable.TBL_NAME},
                            selectionVars, selectionArgs, null);

                    if (recordCursor.moveToFirst()) {
                        String state = recordCursor.getString(recordCursor
                                .getColumnIndex(PersistenceQueueTable.STATE));

                        // Do not update recipes with current outgoing queued requests
                        if (!state.equals(Constants.STATE_OK)) {
                            recordCursor.close();
                            continue;
                        }
                    }
                    recordCursor.close();

                    mContentResolver.update(Provider.RECIPE_CONTENT_URI,
                            recipeContentValues, RecipeTable.WHERE_UUID_EQUALS,
                            new String[]{remoteRecipe.uuid});
                } else { // it's not in the database
                    mContentResolver.insert(Provider.RECIPE_CONTENT_URI,
                            recipeContentValues);
                }

                recipeCursor.close();

            }

            mIsFirstSync = false;
            this.syncFavorites();
        }

        mPersistLock.unlock();
    }

    @Override
    public void postRecipeRequestFinished(int requestId,
                                          RemoteRecipe newRemoteRecipe, boolean successful, int status) {
        mPersistLock.lock();

        if (successful) {
            //.debug("post recipe successful");
        } else if (status == NetworkClient.BAD_REQUEST || status == NetworkClient.RESOURCE_CONFLICT) { //assume that the request went through to the server but didn't come back and that this just needs to update the already existing recipe
            ContentValues updateReqVals = new ContentValues();
            updateReqVals.put(PersistenceQueueTable.REQUEST_TYPE, Constants.PUT);
            updateReqVals.put(PersistenceQueueTable.STATE, Constants.STATE_NEEDS_REPOSTING);
            int rows = mContentResolver.update(Provider.PERSISTENCEQUEUE_CONTENT_URI, updateReqVals, PersistenceQueueTable.WHERE_ID_EQUALS, new String[]{"" + requestId});
            //SPLog.debug("updated the request record: " + requestId + " so that it's now a put!   ..." + rows);
        } else {
            //SPLog.debug("post recipe failed: " + status + " name: " + (newRemoteRecipe != null ? newRemoteRecipe.name : "none") + " uuid: " + (newRemoteRecipe != null ? newRemoteRecipe.uuid : "none"));
        }
        updateQueue(requestId, successful);

        mPersistLock.unlock();
    }

    @Override
    public void putRecipeRequestFinished(final int requestId,
                                         final RemoteRecipe updatedRecipe, final boolean successful,
                                         int status) {
        mPersistLock.lock();

        if (successful) { //SPLog.debug("put recipe succeeded");
            if (status == NetworkClient.RESOURCE_CREATED) {
            }

        }
        updateQueue(requestId, successful);

        mPersistLock.unlock();
    }

    @Override
    public void deleteRecipeRequestFinished(int requestId, String recipeUuid,
                                            boolean successful, int status) {
        mPersistLock.lock();

        if (successful) {

        } else {
            if (status == NetworkClient.RESOURCE_NOT_FOUND) {
                updateQueue(requestId, true);

                mPersistLock.unlock();
                return;
            }
        }
        updateQueue(requestId, successful);

        mPersistLock.unlock();
    }

    @Override
    public void getGrindsRequestFinished(int requestId,
                                         List<RemoteGrind> remoteGrinds, boolean successful, int status) {
        mPersistLock.lock();

        sContentProcessorCallbacks.syncGrindsRequestFinished(requestId,
                successful);

        mPersistLock.unlock();
    }

    @Override
    public void postLogRequestFinished(int requestId, RemoteLog newLog,
                                       boolean successful, int status) {
        mPersistLock.lock();

        sContentProcessorCallbacks
                .postLogRequestFinished(requestId, successful);

        mPersistLock.unlock();
    }

    @Override
    public void getMachineWithSerialNumberFinished(int requestId,
                                                   RemoteMachine remoteMachine, boolean successful, int status,
                                                   boolean isSave) {
        mPersistLock.lock();

        if (mMachineSettingsSaving) {
            if (!isSave) {
                return;
            }
        }

        boolean validSerialNumber = true;

        if (successful && mTempMachineSettings != null) {
            if (remoteMachine == null) {
                validSerialNumber = false;

                revertServerMachineSettingsFields();
            } else if (mTempMachineSettings != null) {
                mTempMachineSettings.setId(remoteMachine.id);
                mTempMachineSettings
                        .setCrucibleCount(remoteMachine.crucible_count);
                MachineSettings.writeMachineSettingsToSharedPreferences(
                        mTempMachineSettings, mContext);
                MachineSettings.writePinToSharedPrefs(mContext,
                        remoteMachine.PIN);
                if (isSave) {
                    sContentProcessorCallbacks.machineSettingsSaved(requestId);
                }
            }
        } else {
            revertServerMachineSettingsFields();
        }

        MachineSettings.writeMachineSettingsToSharedPreferences(
                mTempMachineSettings, mContext);

        mTempMachineSettings = null;

        if (validSerialNumber && isSave) {
            sContentProcessorCallbacks.machineSettingsSaved(requestId);
        } else if (isSave) {
            sContentProcessorCallbacks.invalidMachineSerialNumber(requestId);
        }
        if (isSave) {
            mMachineSettingsSaving = false;
        }

        mPersistLock.unlock();
    }

    public static ContentProcessorCallbacks getCallbacks() {
        return sContentProcessorCallbacks;
    }

    /**
     * Revert the serial number, id, and crucible count to their current values
     */
    private void revertServerMachineSettingsFields() {
        mPersistLock.lock();

        MachineSettings currentMachineSettings = MachineSettings
                .getMachineSettingsFromSharedPreferences(mContext);

        mTempMachineSettings.setSerialNumber(currentMachineSettings
                .getSerialNumber());
        mTempMachineSettings.setId(currentMachineSettings.getId());
        mTempMachineSettings.setCrucibleCount(currentMachineSettings
                .getCrucibleCount());

        mPersistLock.unlock();
    }

    @Override
    public void machineSettingsNetworkError(int requestId, int status) {
        mPersistLock.lock();

        // If there's a network error, don't change the current serial number.
        mTempMachineSettings.setSerialNumber(MachineSettings
                .getMachineSettingsFromSharedPreferences(mContext)
                .getSerialNumber());
        MachineSettings.writeMachineSettingsToSharedPreferences(
                mTempMachineSettings, mContext);
        sContentProcessorCallbacks.networkError(requestId);

        mPersistLock.unlock();
    }

    @Override
    public void networkError(int requestId, int status) {
        sContentProcessorCallbacks.networkError(requestId);
    }

    @Override
    public void postFavoriteRequestFinished(int requestId,
                                            RemoteFavorite remoteFavorite, boolean success, int status) {
        mPersistLock.lock();

        if (success) {
        }
        updateQueue(requestId, success);

        mPersistLock.unlock();
    }

    @Override
    public void deleteFavoriteRequestFinished(int requestId, String uuid,
                                              boolean success, int status) {
        mPersistLock.lock();

        if (success) {

        } else {
            if (status == NetworkClient.RESOURCE_NOT_FOUND) {
                updateQueue(requestId, true);

                mPersistLock.unlock();
                return;
            }
        }
        updateQueue(requestId, success);

        mPersistLock.unlock();
    }

    // TODO get favorites syncing with UUID's
    @Override
    public void getFavoritesRequestFinished(int requestId, long timeStamp,
                                            List<RemoteFavorite> syncFavorites, boolean successful, int status) { //SPLog.debug("favorites came back!");
        mPersistLock.lock();

        Set<String> ids = new HashSet<String>();

        // use when refactored to push through request queue
        // updateQueue(requestId, successful);

        if (successful) {
            for (RemoteFavorite favorite : syncFavorites) {
                ids.add(favorite.uuid); //SPLog.debug("added: " + favorite.uuid);

                ContentValues favValues = new ContentValues();
                favValues.put(FavoriteTable.UUID, favorite.uuid);
                favValues.put(FavoriteTable.RECIPE_UUID, favorite.recipe_uuid);
                favValues.put(FavoriteTable.USER, favorite.user);

                Cursor recipeCursor = mContentResolver.query(Provider.RECIPE_CONTENT_URI, new String[]{FavoriteTable.ID}, FavoriteTable.WHERE_UUID_EQUALS, new String[]{favorite.recipe_uuid}, null);
                recipeCursor.moveToFirst();
//				SPLog.debug("got recipe cursor so the favorite can be stored locally");

                if (recipeCursor.getCount() > 0) {
                    long recipeId = recipeCursor.getLong(recipeCursor.getColumnIndex(RecipeTable.ID));
                    favValues.put(FavoriteTable.RECIPE_ID, recipeId);

                    Cursor cursor = mContentResolver.query(
                            Provider.FAVORITE_CONTENT_URI,
                            new String[]{FavoriteTable.UUID},
                            FavoriteTable.WHERE_UUID_EQUALS,
                            new String[]{favorite.uuid}, null);
                    if (cursor.getCount() > 0) {
                        mContentResolver.update(Provider.FAVORITE_CONTENT_URI,
                                favValues, FavoriteTable.WHERE_ID_EQUALS,
                                new String[]{favorite.uuid});
                    } else {
                        mContentResolver.insert(Provider.FAVORITE_CONTENT_URI,
                                favValues);
                    }
                    cursor.close();
                }

                recipeCursor.close();
            }

            Cursor c = mContentResolver.query(Provider.FAVORITE_CONTENT_URI, new String[]{FavoriteTable.UUID}, null, null, null);
            c.moveToFirst();
            while (!c.isAfterLast()) {
                String curr = c.getString(c.getColumnIndex(FavoriteTable.UUID)); //SPLog.debug("curr fav to check for delete: " + curr);
                if (!ids.contains(curr)) { //SPLog.debug("curr NOT FOUND!!!");
                    mContentResolver.delete(Provider.FAVORITE_CONTENT_URI, FavoriteTable.WHERE_UUID_EQUALS, new String[]{curr});
                } else {
//					SPLog.debug("curr NOT DELETED!!!");
                }
                c.moveToNext();
            }
        } else {

        }

        mPersistLock.unlock();
    }

    private void updateQueue(int requestId, boolean successful) {
        mPersistLock.lock();

        ContentValues persistenceQContentValues = new ContentValues();

        if (successful) {
            persistenceQContentValues.put(PersistenceQueueTable.STATE,
                    Constants.STATE_OK);
        } else {
            persistenceQContentValues.put(PersistenceQueueTable.STATE,
                    Constants.STATE_NEEDS_REPOSTING);
        }

        mContentResolver.update(Provider.PERSISTENCEQUEUE_CONTENT_URI,
                persistenceQContentValues, PersistenceQueueTable.ID + "=?",
                new String[]{String.valueOf(requestId)});

        mPersistLock.unlock();
    }

    @Override
    public void getAvailableUpdatesRequestFinished(int requestId, int newVersion) {
        if (newVersion != -1) {
            // Send new version available callback
            sContentProcessorCallbacks.getAvailableUpdatesRequestFinished(
                    requestId, newVersion);
        }
    }

    @Override
    public void createNewDeviceRequestFinished(int requestId, int newDevice) {
        mPersistLock.lock();

        // Save device id to shared prefs
        MachineSettings.getMachineSettingsFromSharedPreferences(mContext)
                .writeDeviceToSharedPrefs(mContext, newDevice);

        mPersistLock.unlock();
    }

    @Override
    public void pinResetRequestFinished(int requestId, String PIN,
                                        boolean successful) {
        mPersistLock.lock();

        MachineSettings.writePinToSharedPrefs(mContext, PIN);
        sContentProcessorCallbacks.postPinResetRequestFinished(requestId, PIN,
                successful);

        mPersistLock.unlock();
    }

    private class RequestQueueProcessor extends Thread {
        private boolean mConnected;
        private boolean keepRunning;
        private boolean mJustStarted;

        RequestQueueProcessor() {
            super();
            mConnected = false;
            mJustStarted = true;
        }

        public void stopThread() {
            this.keepRunning = false;
        }

        public void syncRoasters(int requestId) {
            NetworkClient networkClient = new DefaultNetworkClient(mContext,
                    DefaultContentProcessor.getInstance(mContext, null));
            networkClient.getRoasters(requestId);
        }

        public void createRecipe(int requestId, Recipe newRecipe) {
            mPersistLock.lock();

            NetworkClient networkClient = new DefaultNetworkClient(mContext,
                    DefaultContentProcessor.getInstance(mContext, null));

            RemoteRecipe newRemoteRecipe = new RemoteRecipe(null,
                    newRecipe.getLocal_id(), newRecipe.getName(),
                    newRecipe.getType(), newRecipe.getSteampunk_user_id(),
                    newRecipe.getPublished(), newRecipe.getGrams(),
                    newRecipe.getTeaspoons(), newRecipe.getGrind(),
                    newRecipe.getFilter(), newRecipe.getStacks(), newRecipe.getUuid());

            networkClient.postRecipe(requestId, newRemoteRecipe);

            mPersistLock.unlock();
        }

        public void updateRecipe(final int requestId, final Recipe updatedRecipe) {
            mPersistLock.lock();

            NetworkClient networkClient = new DefaultNetworkClient(mContext,
                    DefaultContentProcessor.getInstance(mContext, null));

            RemoteRecipe newRemoteRecipe = new RemoteRecipe(null,
                    updatedRecipe.getLocal_id(), updatedRecipe.getName(),
                    updatedRecipe.getType(),
                    updatedRecipe.getSteampunk_user_id(),
                    updatedRecipe.getPublished(), updatedRecipe.getGrams(),
                    updatedRecipe.getTeaspoons(), updatedRecipe.getGrind(),
                    updatedRecipe.getFilter(), updatedRecipe.getStacks(), updatedRecipe.getUuid());

            networkClient.putRecipe(requestId, newRemoteRecipe);

            mPersistLock.unlock();
        }


        public void deleteRecipe(int requestId, String uuid) { //SPLog.debug("delete recipe: " + uuid);
            mPersistLock.lock();

            // Send delete request to REST client
            NetworkClient networkClient = new DefaultNetworkClient(mContext,
                    DefaultContentProcessor.getInstance(mContext, null));
            networkClient.deleteRecipe(requestId, uuid);

            mPersistLock.unlock();
        }


        public void postFavorite(int requestId, long favoriteId) {
            mPersistLock.lock();

            Cursor cursor = mContentResolver.query(
                    Provider.FAVORITE_CONTENT_URI, FavoriteTable.ALL_COLUMNS,
                    FavoriteTable.ID + "=?",
                    new String[]{Long.toString(favoriteId)}, null);

            cursor.moveToFirst();
            if (cursor.getCount() > 0) {
                String recipeUuid = cursor.getString(cursor
                        .getColumnIndex(FavoriteTable.RECIPE_UUID));
                long userId = cursor.getLong(cursor
                        .getColumnIndex(FavoriteTable.USER));
                String uuid = cursor.getString(cursor.getColumnIndex(FavoriteTable.UUID));

                cursor.close();

                RemoteFavorite remoteFavorite = new RemoteFavorite(recipeUuid, userId, uuid);
                NetworkClient networkClient = new DefaultNetworkClient(
                        mContext, DefaultContentProcessor.getInstance(mContext,
                        null));
                networkClient.postFavorite(requestId, remoteFavorite);
            } else {
                ContentValues cv = new ContentValues();
                cv.put(PersistenceQueueTable.STATE, Constants.STATE_OK);
                mContentResolver.update(Provider.PERSISTENCEQUEUE_CONTENT_URI,
                        cv, PersistenceQueueTable.OBJ_ID + "=? AND "
                                + PersistenceQueueTable.TBL_NAME + "=?",
                        new String[]{Long.toString(favoriteId),
                                FavoriteTable.TABLE_NAME});

                cursor.close();
            }

            mPersistLock.unlock();
        }

        public void deleteFavorite(int requestId, String uuid) { //SPLog.debug("deleveFavorite() just called with " + uuid);
            (new Exception()).printStackTrace();
            mPersistLock.lock();

            NetworkClient networkClient = new DefaultNetworkClient(mContext,
                    DefaultContentProcessor.getInstance(mContext, null));
            networkClient.deleteFavorite(requestId, uuid);

            mPersistLock.unlock();
        }

        private void updateState(String state, ContentValues cv, long now,
                                 int recordId) {
            mPersistLock.lock();

            cv.put(PersistenceQueueTable.STATE, state);
            cv.put(PersistenceQueueTable.LAST_ATTEMPT, now);
            mContentResolver.update(Provider.PERSISTENCEQUEUE_CONTENT_URI, cv,
                    PersistenceQueueTable.WHERE_ID_EQUALS,
                    new String[]{Integer.toString(recordId)});

            mPersistLock.unlock();
        }

        private String getDependenciesFromQueue(long queueId) {
            Cursor currentRequestCursor = mContentResolver.query(Provider.PERSISTENCEQUEUE_CONTENT_URI, new String[]{PersistenceQueueTable.BACKPOINTERS}, PersistenceQueueTable.ID + "=?", new String[]{"" + queueId}, null);
            currentRequestCursor.moveToFirst();
            if (!currentRequestCursor.isAfterLast()) {
                String depStr = currentRequestCursor.getString(currentRequestCursor.getColumnIndex(PersistenceQueueTable.BACKPOINTERS)); //SPLog.debug("depStr is: " + depStr);
                currentRequestCursor.close();
                if (depStr == null) depStr = "";
                return depStr;
//				if (depStr == null) depStr = "";
//				String[] depStrs = depStr.split(DEPENDENCY_DELIMITER);
//				return depStrs;
            }
            currentRequestCursor.close();
//			return new String[0];
            return "";
        }

        private boolean hasUnresolvedDependencies(long queueId) {
            mPersistLock.lock();

            boolean has = false;
            String dependencies = getDependenciesFromQueue(queueId);
            Cursor depCursor = mContentResolver.query(Provider.PERSISTENCEQUEUE_CONTENT_URI, new String[]{PersistenceQueueTable.STATE}, PersistenceQueueTable.ID + " IN(?)", new String[]{dependencies}, null);
            depCursor.moveToFirst();
            while (!depCursor.isAfterLast()) {
                has |= !depCursor.getString(depCursor.getColumnIndex(PersistenceQueueTable.STATE)).equals(Constants.STATE_OK);
                depCursor.moveToNext();
            }
            depCursor.close();

            mPersistLock.unlock();
            return has;
        }

        @Override
        public void run() {
            keepRunning = true;
            while (keepRunning) {
                try {
                    sleep(PERSISTENCE_QUEUE_CYCLE_DELAY);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (!mConnected) { //SPLog.debug("skipping queue processing!");
                    continue;
                }
                //SPLog.debug("processing queue");

                mPersistLock.lock();

//				Cursor queCursor = mContentResolver.query(
//						Provider.PERSISTENCEQUEUE_CONTENT_URI,
//						PersistenceQueueTable.ALL_COLUMNS,
//						PersistenceQueueTable.STATE + "!=?",
//						new String[] { Constants.STATE_OK },
//						null);
                Cursor queCursor = mContentResolver.query(
                        Provider.PERSISTENCEQUEUE_CONTENT_URI,
                        PersistenceQueueTable.ALL_COLUMNS,
                        null,
                        null,
                        null);
                queCursor.moveToFirst();
                for (int i = 0; i < queCursor.getCount(); i++) {
                    String table = queCursor.getString(queCursor
                            .getColumnIndex(PersistenceQueueTable.TBL_NAME));
                    int recordId = queCursor.getInt(queCursor
                            .getColumnIndex(PersistenceQueueTable.ID));
                    String requestType = queCursor
                            .getString(queCursor
                                    .getColumnIndex(PersistenceQueueTable.REQUEST_TYPE));
                    String state = queCursor.getString(queCursor
                            .getColumnIndex(PersistenceQueueTable.STATE));
                    int attemptNumber = queCursor
                            .getInt(queCursor
                                    .getColumnIndex(PersistenceQueueTable.REQUEST_COUNT));
                    long objId = queCursor.getLong(queCursor
                            .getColumnIndex(PersistenceQueueTable.OBJ_ID));
                    String objUuid = queCursor.getString(queCursor.getColumnIndex(PersistenceQueueTable.OBJ_UUID));
                    long now = (System.currentTimeMillis() / ONE_SECOND_IN_MILLIS);
                    long lastAttempt = queCursor
                            .getLong(queCursor
                                    .getColumnIndex(PersistenceQueueTable.LAST_ATTEMPT));

                    //SPLog.debug("queue State: " + state + " recordId: " + recordId + " table: " + table + " type: " + requestType);

                    if (state.equals(Constants.STATE_OK)) {
                        mContentResolver.delete(Provider.PERSISTENCEQUEUE_CONTENT_URI, PersistenceQueueTable.WHERE_ID_EQUALS, new String[]{"" + recordId});
                        queCursor.moveToNext();
                        continue;
                    }

                    boolean stateDependent = Constants.STATE_DELETING.equals(state)
                            || Constants.STATE_POSTING.equals(state)
                            || Constants.STATE_PUTTING.equals(state)
                            || Constants.STATE_GETTING.equals(state);
                    boolean retryDependent = ((now - lastAttempt) < Q_RETRY_RATE);
                    if (stateDependent || retryDependent) {
                        if (!mJustStarted) {
                            queCursor.moveToNext();
                            String msg = !stateDependent ? " because it's too early to try again" : " because it's in process";
                            //SPLog.debug("skipped " + recordId + msg);
                            continue;
                        }
                    }

                    if (hasUnresolvedDependencies(recordId)) {
                        //SPLog.debug("dependencies: " + getDependenciesFromQueue(recordId));
                        queCursor.moveToNext();
                        //SPLog.debug("skipped " + recordId + " because it has waiting dependencies");
                        continue;
                    }

                    //SPLog.debug("dependencies seem to be resolved");

                    ContentValues cv = new ContentValues();
                    cv.put(PersistenceQueueTable.REQUEST_COUNT, ++attemptNumber);
                    cv.put(PersistenceQueueTable.ID, recordId);
                    if (table.equals(RecipeTable.TABLE_NAME)) { //SPLog.debug("found a recipe request");
                        if (Constants.POST.equals(requestType)) { //SPLog.debug("it's a post: " + recordId);
                            updateState(Constants.STATE_POSTING, cv, now,
                                    recordId);
                            Cursor recipeCursor = mContentResolver
                                    .query(Provider.RECIPE_CONTENT_URI,
                                            RecipeTable.ALL_COLUMNS,
                                            RecipeTable.WHERE_UUID_EQUALS,
                                            new String[]{objUuid},
                                            null);
                            if (recipeCursor.moveToFirst()) {
                                createRecipe(recordId, new Recipe(recipeCursor));
                            } else {
                                mContentResolver.delete(Provider.PERSISTENCEQUEUE_CONTENT_URI, PersistenceQueueTable.WHERE_ID_EQUALS, new String[]{"" + recordId});
                                //SPLog.debug("couldn't find the recipe!");
                                //SPLog.fromService(mContext, sContentProcessorCallbacks, objUuid, -1, SPLog.WARNING, SPLog.GENERAL, "could not post the recipe because the recipe was not found in the local database");
                            }
                        } else if (Constants.PUT.equals(requestType)) { //SPLog.debug("it's a put: " + recordId);
                            updateState(Constants.STATE_PUTTING, cv, now,
                                    recordId);
                            Cursor recipeCursor = mContentResolver
                                    .query(Provider.RECIPE_CONTENT_URI,
                                            RecipeTable.ALL_COLUMNS,
                                            RecipeTable.WHERE_UUID_EQUALS,
                                            new String[]{objUuid},
                                            null);
                            if (recipeCursor.moveToFirst()) {
                                updateRecipe(recordId, new Recipe(recipeCursor));
                            } else {
                                mContentResolver.delete(Provider.PERSISTENCEQUEUE_CONTENT_URI, PersistenceQueueTable.WHERE_ID_EQUALS, new String[]{"" + recordId});
                                //SPLog.debug("couldn't find the recipe!");
                                //SPLog.fromService(mContext, sContentProcessorCallbacks, objUuid, -1, SPLog.WARNING, SPLog.GENERAL, "could not post the recipe because the recipe was not found in the local database");
                            }
                        } else if (Constants.DELETE.equals(requestType)) { //SPLog.debug("it's a delete");
                            updateState(Constants.STATE_DELETING, cv, now, recordId);
                            deleteRecipe(recordId, objUuid);
                        }
                    } else if (table.equals(RoasterTable.TABLE_NAME)) {
                        if (Constants.GET.equals(requestType)) {
                            updateState(Constants.STATE_GETTING, cv, now,
                                    recordId);
                            syncRoasters(recordId);
                        }
                    } else if (table.equals(FavoriteTable.TABLE_NAME)) {
                        if (requestType.equals(Constants.POST)) {
                            updateState(Constants.STATE_POSTING, cv, now,
                                    recordId);
                            postFavorite(recordId, objId);
                        } else if (requestType.equals(Constants.DELETE)) {
                            updateState(Constants.STATE_DELETING, cv, now,
                                    recordId);
                            deleteFavorite(recordId, objUuid);
                        }
                    }
                    queCursor.moveToNext();

                }
                mJustStarted = false;
                queCursor.close();

                mPersistLock.unlock();
            }
        }

        public void enableConnection() {
            mConnected = true;
        }

        public void disableConnection() {
            mConnected = false;
        }
    }

    @Override
    public void putUserRequestFinished(int requestId, Object object, boolean success,
                                       int i, String requestedUsername, final String message) {

        if (success) {
            AccountSettings settings = AccountSettings.getAccountSettingsFromSharedPreferences(mContext);
            AccountSettings newSettings = new AccountSettings(requestedUsername, settings.getEmail(), settings.getAddress(), settings.getCity(), settings.getState(),
                    settings.getCountry(), settings.getZipCode(), settings.getProtectRecipes());
            AccountSettings.writeAccountSettingsToSharedPreferences(newSettings, mContext);
        } else if (message != null) {
            if (message.contains(USERNAME_ALREADY_EXISTS_MESSAGE)) {
                final String takenMessage = mContext.getResources().getString(R.string.username_taken);
                Runnable messageRunnable = new Runnable() {
                    public void run() {
                        EventBus.getDefault().post(
                                new DefaultPersistenceServiceHelperEvents.ToastMessageEvent(
                                        takenMessage));
                    }
                };
                messageRunnable.run();
            } else {
                final String errorMessage = mContext.getResources().getString(R.string.username_change_error);
                Runnable messageRunnable = new Runnable() {
                    public void run() {
                        EventBus.getDefault().post(
                                new DefaultPersistenceServiceHelperEvents.ToastMessageEvent(
                                        errorMessage));
                    }
                };
                messageRunnable.run();
            }
        }
    }

    @Override
    public void putSteamPunkUserRequestFinished(int requestId, Object object, boolean success, int i) {
        // TODO Auto-generated method stub

    }

    @Override
    public void postPasswordChangeFinished(int requestId, boolean success, int i) {
        if (success) {
            final String errorMessage = mContext.getResources().getString(R.string.your_password_changed_success_message);
            Runnable messageRunnable = new Runnable() {
                public void run() {
                    EventBus.getDefault().post(
                            new DefaultPersistenceServiceHelperEvents.ToastMessageEvent(
                                    errorMessage));
                }
            };
            messageRunnable.run();
        } else {
            final String errorMessage = mContext.getResources().getString(R.string.original_password_incorrect);
            Runnable messageRunnable = new Runnable() {
                public void run() {
                    EventBus.getDefault().post(
                            new DefaultPersistenceServiceHelperEvents.ToastMessageEvent(
                                    errorMessage));
                }
            };
            messageRunnable.run();
        }
    }
}
