package com.alphadominche.steampunkhmi.restclient.contentprocessor;

import java.util.List;

import com.alphadominche.steampunkhmi.model.Recipe;
import com.alphadominche.steampunkhmi.restclient.networkclient.RemoteClasses.UserIdMapping;
import com.alphadominche.steampunkhmi.utils.AccountSettings;
import com.alphadominche.steampunkhmi.utils.MachineSettings;

public interface ContentProcessor {

    /**
     * Log in a user
     *
     * @param username the username of the user to login
     * @param password the user's password
     */
    public void login(int requestId, String username, String password);

    /**
     * Log out the currently logged in user
     */
    public void logout();

    /**
     * Refresh list of roasters
     */
    public void syncRoasters();

    /**
     * For Migrations, need to change user id to steampunk id
     *
     * @return List of Roasters
     */
    public List<UserIdMapping> getUserMapping();

    /**
     * Saves the described recipe
     *
     * @param recipeMap a map with members "recipe", "stacks", and "agitations" which
     *                  describes a recipe
     */
    public void saveRecipe(Recipe recipe);

    /**
     * Refresh the list of recipes by getting new ones, updating existing ones,
     * and deleting old ones
     */
    public void syncRecipes();

    /**
     * Refresh the list of favorites with the server
     *
     * @param userId
     */
    public void syncFavorites(/*long userId*/);

    /**
     * Delete a given recipe
     *
     * @param id ID of the recipe to delete
     */
    public void deleteRecipe(long id);

    /**
     * Create a new log
     *
     * @param requestId     id of the request
     * @param machineId     id of the machine that this log is referring to
     * @param userId        the id of the logged in user when this log was created, if any
     * @param date          the date and time the log was generated
     * @param recipeId      recipe that was running when log message was generated, if any
     * @param crucibleIndex crucible the log applies to, if any
     * @param severity      severity of the log
     * @param type          type of log
     * @param message       message of the log
     */
    public void createLog(
            Integer requestId,
            Long machineId,
            Long userId,
            String date,
            String recipeUuid,
            Integer crucibleIndex,
            Integer severity,
            Integer type,
            String message
    );

    /**
     * Persist the machine settings locally and check the serial number with
     * what is on the server
     *
     * @param requestId       id of the request
     * @param machineSettings the machine settings object to persist
     */
    public void saveMachineSettings(int requestId, MachineSettings machineSettings);

    /**
     * Persist the account settings locally
     *
     * @param requestId       id of the request
     * @param accountSettings the account settings object to persist
     */
    public void saveAccountSettings(int requestId, AccountSettings accountSettings, String requestedUsername);

    /**
     *
     */
    public void resetPassword(int requestId, String identifier);

    /**
     * @param requestId
     * @param PIN
     */
    public void resetPin(int requestId, long machineId, String PIN);

    /**
     * @param requestId id of the request
     * @param versionId django id of the current version
     * @param deviceId  django id of the device
     */
    void checkForUpdates(int requestId, int versionId, int deviceId);

    /**
     * @param requestId  id of the request
     * @param newVersion django id of the version to download
     */
    void downloadUpdate(int requestId, int newVersion);

    /**
     * @param requestId
     * @param name      NEXUS_7
     * @param platform  Android
     */
    void createNewDevice(int requestId, int version, String name);

    /**
     * @param requestId
     * @param serialNum
     */
    public void getMachineSettings(Integer requestId, String serialNum);


    /**
     * @param old
     * @param newPass
     */
    public void changePassword(Integer requestId, String old, String newPass);

    /**
     * The callback interface for the ContentProcessor
     *
     * @author jnuss
     */
    public interface ContentProcessorCallbacks {

        public void getRecipesRequestFinished(int requestId, boolean successful);

        public void postRecipeRequestFinished(int requestId, long recipeid, long localId, boolean successful);

        public void putRecipeRequestFinished(int requestId, boolean successful);

        public void deleteRecipeRequestFinished(int requestId, boolean successful);

        public void syncGrindsRequestFinished(int requestId, boolean sucessful);

        public void postLogRequestFinished(int requestId, boolean successful);

        public void invalidMachineSerialNumber(int requestId);

        public void machineSettingsSaved(int requestId);

        public void networkError(int requestId);

        public void deleteStackRequestFinished(int requestId, boolean successful);

        public void postStackRequestFinished(int requestId, long stackid, long localId, boolean successful);

        public void postAgitationCycleRequestFinished(int requestId, Long id, boolean success);

        public void getAvailableUpdatesRequestFinished(int requestId, int versionId);

        public void postPinResetRequestFinished(int requestId, String PIN, boolean successful);

    }


}
