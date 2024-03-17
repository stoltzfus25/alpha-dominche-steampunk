package com.alphadominche.steampunkhmi.restclient.networkclient;

import java.util.List;

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

public interface NetworkClient {
    public static int STATUS_OK = 200;
    public static int RESOURCE_CREATED = 201;
    public static int BAD_REQUEST = 400;
    public static int RESOURCE_NOT_FOUND = 404;
    public static int RESOURCE_CONFLICT = 409;
    public static int GATEWAY_TIMEOUT = 504;


    public void postLogin(int requestId, String username, String password);

    public void getRoasters(int requestId);

    public List<UserIdMapping> getUserMapping();

    public void getRecipes(int requestId, long timestamp);

    public void postRecipe(int requestId, RemoteRecipe newRecipe);

    public void putRecipe(int requestId, RemoteRecipe updatedRecipe);

    public void deleteRecipe(int requestId, String recipeUuidToDelete);

    public void postLog(int requestId, RemoteLog newLog);

    public void getGrinds(int requestId);

    public void getMachineWithSerialNumber(int requestId, String serialNumber,
                                           boolean isSave);

    public void postPasswordReset(int requestId, String identifier, String machine_id);

    public void postPasswordChange(int requestId, String old, String new_pass);

    public void postPinReset(int requestId, long machineId, String PIN);

    public void checkForUpdates(int requestId, int versionId, int deviceId);

    public void downloadUpdate(int requestId, int versionId);

    public void createNewDevice(int requestId, int version, String name);

//	public void putStack(int requestId, RemoteStack updatedStack);

//	public void deleteStack(int requestId, long remoteId);

//	public void postStack(int requestId, RemoteStack newRemoteStack);

//	public void postAgitationCycle(int requestId,
//			RemoteAgitation newRemoteAgitation);

//	public void putAgitationCycle(int requestId,
//			RemoteAgitation newRemoteAgitation);

//	public void deleteAgitationCycle(int requestId, long remoteId);

    public void postFavorite(int requestId, RemoteFavorite remoteFavorite);

    public void deleteFavorite(int requestId, String uuid);

    public void getFavorites(int requestId, long timeStamp);

    public void subscribeToRoaster(int requestId, long steampunkUserId);

    public void updateAccountSettings(int requestId, RemoteUser ru,
                                      RemoteSteamPunkUser rspu, Long steampunkId, Long userId, String requestedUsername);


    /**
     * The callback interface for the NetworkClient
     *
     * @author jnuss
     */
    public interface NetworkClientCallbacks {

        public void getRoastersRequestFinished(int requestId,
                                               List<RemoteRoaster> remoteRoasters, boolean successful,
                                               int status);

        public void getRecipesRequestFinished(int requestId,
                                              List<RemoteRecipe> remoteRecipes, boolean successful, int status);

        public void postRecipeRequestFinished(int requestId,
                                              RemoteRecipe newRecipe, boolean successful, int status);

        public void putRecipeRequestFinished(int requestId,
                                             RemoteRecipe updatedRecipe, boolean successful, int status);

        public void deleteRecipeRequestFinished(int requestId, String recipeUuid,
                                                boolean successful, int status);

        public void getGrindsRequestFinished(int requestId,
                                             List<RemoteGrind> remoteGrinds, boolean successful, int status);

        public void postLoginRequestFinished(int requestId,
                                             RemoteLogin remoteLogin, boolean successful,
                                             String errorMessage, int status);

        public void postLogRequestFinished(int requestId, RemoteLog newLog,
                                           boolean successful, int status);

        public void getMachineWithSerialNumberFinished(int requestId,
                                                       RemoteMachine remoteMachine, boolean successful, int status,
                                                       boolean isSave);

        public void machineSettingsNetworkError(int requestId, int status);

//		public void putStackRequestFinished(int requestId,
//				RemoteStack updatedStack, boolean successful, int status);

        public void getAvailableUpdatesRequestFinished(int requestId,
                                                       int newVersion);

        public void createNewDeviceRequestFinished(int requestId,
                                                   int newDeviceId);

        public void pinResetRequestFinished(int requestId, String PIN,
                                            boolean successful);

        public void networkError(int requestId, int status);

//		public void deleteStackRequestFinished(int requestId, long stackId,
//				boolean success, int status);

//		public void postStackRequestFinished(int requestId,
//				RemoteStack newStack, boolean success, int status);

//		public void postAgitationCycleRequestFinished(int requestId,
//				RemoteAgitation newAgitation, boolean success, int status);

//		public void putAgitationRequestFinished(int requestId,
//				RemoteAgitation updatedRemoteAgitation, boolean success,
//				int status);

//		public void deleteAgitationRequestFinished(int requestId,
//				long remoteId, boolean success, int status);

        public void postFavoriteRequestFinished(int requestId,
                                                RemoteFavorite remoteFavorite, boolean success, int status);

        public void deleteFavoriteRequestFinished(int requestId,
                                                  String uuid, boolean success, int status);

        public void getFavoritesRequestFinished(int requestId, long timeStamp,
                                                List<RemoteFavorite> syncFavorites, boolean b, int status);

        public void postSubscribeToRoasterFinished(int requestId, boolean b,
                                                   int status);

        public void putUserRequestFinished(int requestId, Object object,
                                           boolean b, int i, String requestedUsername, String message);

        public void putSteamPunkUserRequestFinished(int requestId,
                                                    Object object, boolean b, int i);

        public void postPasswordChangeFinished(int requestId, boolean success, int i);

    }


}
