package com.alphadominche.steampunkhmi.restclient.networkclient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.converter.ConversionException;
import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;
import retrofit.http.Query;
import retrofit.mime.TypedByteArray;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.alphadominche.steampunkhmi.R;
import com.alphadominche.steampunkhmi.SPLog;
import com.alphadominche.steampunkhmi.restclient.contentprocessor.DefaultContentProcessor;
import com.alphadominche.steampunkhmi.restclient.networkclient.RemoteClasses.PasswordChange;
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
import com.alphadominche.steampunkhmi.utils.Constants;

public class DefaultNetworkClient implements NetworkClient {

	private static final String TAG = DefaultNetworkClient.class
			.getCanonicalName();

	private static final String SERVER_BASE_URL = "https://api.alphadominche.com/";
//	private static final String SERVER_BASE_URL = "http://steampunkdev.verisage.us/";
//	private static final String SERVER_BASE_URL = "http://192.168.1.211:8000/";
//	private static final String API_VERSION = "v1";
//	private static final String API_VERSION = "v2";
	private static final String API_VERSION = "v3";
	private static final String SERVER_URL = SERVER_BASE_URL + API_VERSION;
	private static final String AUTHORIZATION_HEADER = "Authorization";

	/**
	 * @category endpoint paths
	 */
	private static final String API_TOKEN_AUTH_PATH = "/api-token-auth";
	private static final String RECIPES_LIST_PATH = "/recipes";
	private static final String ROASTERS_LIST_PATH = "/roasters";
	private static final String USER_MAPPING_PATH = "/idmapping";
//	private static final String STACK_LIST_PATH = "/stacks";
//	private static final String AGITATION_LIST_PATH = "/agitations";
	private static final String RECIPES_TIMESTAMP_PARAM = "timestamp";
	private static final String RECIPES_PATH_UUID = "uuid";
//	private static final String STACK_PATH_ID = "id";
//	private static final String AGITATION_PATH_ID = "id";
	private static final String USER_PATH_ID = "id";
	private static final String STEAMPUNKUSER_PATH_ID = "id";
	private static final String FAVORITE_PATH_UUID = "uuid";
	private static final String RECIPES_INSTANCE_PATH = "/recipes/{"
			+ RECIPES_PATH_UUID + "}";
//	private static final String STACK_INSTANCE_PATH = "/stacks/{"
//			+ STACK_PATH_ID + "}";
//	private static final String AGITATION_INSTANCE_PATH = "/agitations/{"
//			+ AGITATION_PATH_ID + "}";
	private static final String USER_INSTANCE_PATH = "/users/{" + USER_PATH_ID
			+ "}";
	private static final String STEAMPUNKUSER_INSTANCE_PATH = "/steampunkusers/{"
			+ STEAMPUNKUSER_PATH_ID + "}";
	private static final String GRINDS_PATH = "/grinds";
	private static final String LOGS_PATH = "/logs";
	private static final String MACHINE_SERIAL_NUMBER_PATH = "/machines";
	private static final String FAVORITE_LIST_PATH = "/favorites";
	private static final String FAVORITE_INSTANCE_PATH = "/favorites/{"
			+ FAVORITE_PATH_UUID + "}";
	private static final String PASSWORD_RESET_PATH = "/password_reset";
	private static final String PASSWORD_CHANGE_PATH = "/password_change";
	private static final String PIN_RESET_PATH = "/pin_reset/{machine}";
	private static final String SUBSCRIBE_PATH = "/subscribe/{steampunk_user}";

	private static final String CHECK_FOR_UPDATES_PATH = "/updates/{version}";
	private static final String DOWNLOAD_UPDATE_PATH = "/download/";
	private static final String CREATE_NEW_DEVICE_PATH = "/devices/{version}";
	private static final String DEFAULT_EMPTY_BODY = "-none-";

	private Context mContext;
	private NetworkClientCallbacks mNetworkClientCallbacks;
	private SteampunkService mSteampunkService;

	public DefaultNetworkClient(Context context,
			NetworkClientCallbacks networkClientCallbacks) {
		mContext = context;
		mNetworkClientCallbacks = networkClientCallbacks;

		SteampunkInterceptor steampunkInterceptor = new SteampunkInterceptor();
		Executor executor = Executors.newCachedThreadPool();
		RestAdapter restAdapter = new RestAdapter.Builder()
				.setServer(SERVER_URL).setExecutors(executor, null)
				.setRequestInterceptor(steampunkInterceptor)
				.setLogLevel(RestAdapter.LogLevel.BASIC).build();

		mSteampunkService = restAdapter.create(SteampunkService.class);
	}

	@Override
	public void postLogin(final int requestId, final String username,
			String password) {

		RemoteClasses.RemoteLogin newLogin = new RemoteClasses.RemoteLogin(
				username, password);

		Callback<RemoteClasses.RemoteLogin> callback = new Callback<RemoteClasses.RemoteLogin>() {

			@Override
			public void failure(RetrofitError error) {
				Response response = error.getResponse();
//				String reason;
//				String body = null;
				String errorMessage = "";
				if (checkForNetworkError(error)) {
//					reason = error.getMessage();
//					body = "-none-";
					mNetworkClientCallbacks.postLoginRequestFinished(requestId,
							null, false, errorMessage, GATEWAY_TIMEOUT);
					mNetworkClientCallbacks.networkError(requestId,
							GATEWAY_TIMEOUT);
					return;
				} else if (null == response) {
//					reason = error.getMessage();
//					body = DEFAULT_EMPTY_BODY;
					errorMessage = mContext.getResources().getString(R.string.username_or_email_incorrect);
				} else {
//					reason = response.getReason();
//					body = new String(
//							((TypedByteArray) response.getBody()).getBytes());
					errorMessage = mContext.getResources().getString(R.string.bad_credentials);
				}
				int status = response != null ? response.getStatus() : -1;
				mNetworkClientCallbacks.postLoginRequestFinished(requestId,
						null, false, errorMessage, status);
			}

			@Override
			public void success(RemoteLogin remoteLogin, Response response) { //SPLog.debug("response: " + response.getBody().length());
				remoteLogin.username = username;
				mNetworkClientCallbacks.postLoginRequestFinished(requestId,
						remoteLogin, true, null, response.getStatus());
			}
		};

		mSteampunkService.postLogin(newLogin, callback);

	}

	@Override
	public void getRoasters(final int requestId) {
		Callback<List<RemoteRoaster>> callback = new Callback<List<RemoteRoaster>>() {

			@Override
			public void failure(RetrofitError error) {
				String message;
				if (checkForNetworkError(error)) {
					message = "GET roasters failed due to a network error";
					mNetworkClientCallbacks.getRoastersRequestFinished(
							requestId, null, false, GATEWAY_TIMEOUT);
				} else {
					message = "GET roasters failed";
					mNetworkClientCallbacks.getRoastersRequestFinished(
							requestId, null, false,
							(error.getResponse() != null ? error.getResponse()
									.getStatus() : GATEWAY_TIMEOUT));
				}
				logRetroFitError(error, message);
			}

			@Override
			public void success(List<RemoteRoaster> remoteRoasters,
					Response response) {
				mNetworkClientCallbacks.getRoastersRequestFinished(requestId,
						remoteRoasters, true, response.getStatus());
			}
		};

		mSteampunkService.listRoasters(callback);
	}

	@Override
	public List<UserIdMapping> getUserMapping() {
		return mSteampunkService.listRoastersSynchronously();
	}

	@Override
	public void getRecipes(final int requestId, final long timestamp) {
		Callback<List<RemoteRecipe>> callback = new Callback<List<RemoteRecipe>>() {

			@Override
			public void failure(RetrofitError error) {
				String message;
				if (checkForNetworkError(error)) {
					message = "GET recipes failed due to a network error";
					mNetworkClientCallbacks.getRecipesRequestFinished(
							requestId, null, false, GATEWAY_TIMEOUT);
				} else {
					message = "GET recipes failed";
					mNetworkClientCallbacks.getRecipesRequestFinished(
							requestId, null, false,
							(error.getResponse() != null ? error.getResponse()
									.getStatus() : GATEWAY_TIMEOUT));
				}

				logRetroFitError(error, message);
			}

			@Override
			public void success(List<RemoteRecipe> remoteRecipes,
					Response response) {
				mNetworkClientCallbacks.getRecipesRequestFinished(requestId,
						remoteRecipes, true, response.getStatus());
			}
		};

		mSteampunkService.listRecipes(timestamp, callback);
	}

	@Override
	public void postRecipe(final int requestId, final RemoteRecipe newRecipe) { //.debug("******* POSTING RECIPE *******");

		Callback<RemoteRecipe> callback = new Callback<RemoteClasses.RemoteRecipe>() {

			@Override
			public void failure(RetrofitError error) {
				String message;
				if (checkForNetworkError(error)) {
					message = "POST recipe failed due to a network error";
					mNetworkClientCallbacks.postRecipeRequestFinished(
							requestId, null, false, GATEWAY_TIMEOUT);
					//SPLog.fromService(mContext, DefaultContentProcessor.getCallbacks(), "_", -1, SPLog.INFO, SPLog.GENERAL,"recipe with the following request ID came back as a network error: "+requestId+ " recipe name is : "+newRecipe.name);
				} else {
					message = "POST recipe failed";
					mNetworkClientCallbacks.postRecipeRequestFinished(
							requestId, null, false,
							error.getResponse() != null ? error.getResponse()
									.getStatus() : GATEWAY_TIMEOUT);

					//SPLog.fromService(mContext, DefaultContentProcessor.getCallbacks(), "_", -1, SPLog.INFO, SPLog.GENERAL,"recipe with the following request ID came back as a failure: "+requestId+ " recipe name is : "+newRecipe.name);
				}
				logRetroFitError(error, message);
			}

			@Override
			public void success(RemoteRecipe newRecipe, Response response) {
				Log.i(TAG, "POST recipe succeeded");
				mNetworkClientCallbacks.postRecipeRequestFinished(requestId,
						newRecipe, true, response.getStatus());
				//SPLog.fromService(mContext, DefaultContentProcessor.getCallbacks(), newRecipe.uuid, -1, SPLog.INFO, SPLog.GENERAL,"recipe with the following request ID came back successful: "+requestId+ " recipe name is : "+newRecipe.name);
			}
		};

		mSteampunkService.postRecipe(newRecipe, callback);
		//SPLog.fromService(mContext, DefaultContentProcessor.getCallbacks(), "_", -1, SPLog.INFO, SPLog.GENERAL,"sending recipe with the following request ID: "+requestId+ " recipe name is : "+newRecipe.name);
	}

//	@Override
//	public void postStack(final int requestId, final RemoteStack newStack) {
//		Callback<RemoteStack> callback = new Callback<RemoteClasses.RemoteStack>() {
//
//			@Override
//			public void failure(RetrofitError error) {
//				String message;
//				if (checkForNetworkError(error)) {
//					message = "POST stack failed due to a network error";
//					mNetworkClientCallbacks.postStackRequestFinished(requestId,
//							null, false, GATEWAY_TIMEOUT);
//
//					SPLog.fromService(mContext, DefaultContentProcessor.getCallbacks(), -1, -1, SPLog.INFO, SPLog.GENERAL,"stack with the following request ID came back as a failure: "+requestId+" the id for the recipe for this stack is: "+newStack.recipe);
//				} else {
//					message = "POST stack failed";
//					mNetworkClientCallbacks.postStackRequestFinished(requestId,
//							null, false, (error.getResponse() != null ? error
//									.getResponse().getStatus() : GATEWAY_TIMEOUT));
//					SPLog.fromService(mContext, DefaultContentProcessor.getCallbacks(), -1, -1, SPLog.INFO, SPLog.GENERAL,"stack with the following request ID came back as a failure: "+requestId+" the id for the recipe for this stack is: "+newStack.recipe);
//					
//				}
//				logRetroFitError(error, message);
//			}
//
//			@Override
//			public void success(RemoteStack newStack, Response response) {
//				mNetworkClientCallbacks.postStackRequestFinished(requestId,
//						newStack, true, response.getStatus());
//				SPLog.fromService(mContext, DefaultContentProcessor.getCallbacks(), newStack.id, -1, SPLog.INFO, SPLog.GENERAL,"stack with the following request ID came back successful: "+requestId+" the id for the recipe for this stack is: "+newStack.recipe);
//			}
//		};
//
//		mSteampunkService.postStack(newStack, callback);
//		SPLog.fromService(mContext, DefaultContentProcessor.getCallbacks(), -1, -1, SPLog.INFO, SPLog.GENERAL,"sending stack with the following request ID: "+requestId+" the id for the recipe for this stack is: "+newStack.recipe);
//
//	}

	@Override
	public void putRecipe(final int requestId,
			final RemoteRecipe updatedLocalRecipe) {
		Callback<RemoteRecipe> callback = new Callback<RemoteRecipe>() {

			@Override
			public void failure(RetrofitError error) {
				String message;
				if (checkForNetworkError(error)) {
					message = "PUT recipe failed due to a network error";
					mNetworkClientCallbacks.putRecipeRequestFinished(requestId,
							null, false, GATEWAY_TIMEOUT);
				} else {
					message = "PUT recipe failed";
					mNetworkClientCallbacks.putRecipeRequestFinished(requestId,
							null, false, error.getResponse() != null ? error
									.getResponse().getStatus() : GATEWAY_TIMEOUT);
				}
				logRetroFitError(error, message);
			}

			@Override
			public void success(RemoteRecipe updatedRemoteRecipe,
					Response response) {
				mNetworkClientCallbacks.putRecipeRequestFinished(requestId,
						updatedRemoteRecipe, true, response.getStatus());
			}
		};

		mSteampunkService.putRecipe(updatedLocalRecipe.uuid, updatedLocalRecipe,
				callback);
	}

	@Override
	public void deleteRecipe(final int requestId, final String recipeUuid) {

		Callback<RemoteRecipe> callback = new Callback<RemoteClasses.RemoteRecipe>() {

			@Override
			public void failure(RetrofitError error) {
				String message;
				if (checkForNetworkError(error)) {
					message = "DELETE recipe failed due to a network error";
					mNetworkClientCallbacks.deleteRecipeRequestFinished(
							requestId, recipeUuid, false, GATEWAY_TIMEOUT);
				} else {
					message = "DELETE recipe failed";
					mNetworkClientCallbacks.deleteRecipeRequestFinished(
							requestId, recipeUuid, false,
							error.getResponse() != null ? error.getResponse()
									.getStatus() : GATEWAY_TIMEOUT);
				}

				logRetroFitError(error, message);
			}

			@Override
			public void success(RemoteRecipe newRecipe, Response response) {
				mNetworkClientCallbacks.deleteRecipeRequestFinished(requestId,
						recipeUuid, true, response.getStatus());
			}
		};

		mSteampunkService.deleteRecipe(recipeUuid, callback);

	}

	@Override
	public void postLog(final int requestId, RemoteLog newLog) {
		Callback<RemoteLog> callback = new Callback<RemoteLog>() {

			@Override
			public void failure(RetrofitError error) {
				// TODO send error up the stack if an invalid token is used
				String message;
				if (checkForNetworkError(error)) {
					message = "POST log failed due to a networking error";
					mNetworkClientCallbacks.postLogRequestFinished(requestId,
							null, false, GATEWAY_TIMEOUT);
				} else {
					message = "POST log failed";
					mNetworkClientCallbacks.postLogRequestFinished(requestId,
							null, false, error.getResponse() != null ? error
									.getResponse().getStatus() : GATEWAY_TIMEOUT);
				}

				logRetroFitError(error, message);
			}

			@Override
			public void success(RemoteLog remoteLog, Response response) {
				mNetworkClientCallbacks.postLogRequestFinished(requestId,
						remoteLog, true, response.getStatus());
			}

		};

		mSteampunkService.postLog(newLog, callback);
	}

	@Override
	public void getGrinds(final int requestId) {
		Callback<List<RemoteClasses.RemoteGrind>> callback = new Callback<List<RemoteClasses.RemoteGrind>>() {

			@Override
			public void failure(RetrofitError error) {
				String message;
				if (error.isNetworkError()) {
					message = "GET grind failed due to a network error";
					mNetworkClientCallbacks.networkError(requestId, error
							.getResponse().getStatus());
					mNetworkClientCallbacks.getGrindsRequestFinished(requestId,
							null, false, GATEWAY_TIMEOUT);
				} else {
					message = "GET grind failed";
					mNetworkClientCallbacks.getGrindsRequestFinished(requestId,
							null, false, error.getResponse() != null ? error
									.getResponse().getStatus() : GATEWAY_TIMEOUT);
				}

				logRetroFitError(error, message);
			}

			@Override
			public void success(List<RemoteGrind> remoteGrinds,
					Response response) {
				mNetworkClientCallbacks.getGrindsRequestFinished(requestId,
						remoteGrinds, true, response.getStatus());
			}
		};

		mSteampunkService.listGrinds(callback);
	}

	@Override
	public void getMachineWithSerialNumber(final int requestId,
			final String serialNumber, final boolean isSave) {
		Callback<List<RemoteClasses.RemoteMachine>> callback = new Callback<List<RemoteClasses.RemoteMachine>>() {

			@Override
			public void failure(RetrofitError error) {
				String message;
				if (checkForNetworkError(error)) {
					message = "GET machine with serial number failed due to a network error";
					mNetworkClientCallbacks.machineSettingsNetworkError(
							requestId, GATEWAY_TIMEOUT);
				} else {
					if (error != null
							&& error.getCause() != null
							&& error.getCause().getClass() == ConversionException.class) {
						message = "Mapping between server object and our local "
								+ "remote object failed. This probably means the API changed";
					}
					message = "GET machine for serial number failed";

					mNetworkClientCallbacks.getMachineWithSerialNumberFinished(
							requestId, null, false,
							error.getResponse() != null ? error.getResponse()
									.getStatus() : GATEWAY_TIMEOUT, isSave);
				}

				logRetroFitError(error, message);
			}

			@Override
			public void success(
					List<RemoteClasses.RemoteMachine> remoteMachines,
					Response response) {
				if (remoteMachines.isEmpty()) {
					mNetworkClientCallbacks
							.getMachineWithSerialNumberFinished(requestId,
									null, true, response.getStatus(), isSave);
				} else {
					mNetworkClientCallbacks.getMachineWithSerialNumberFinished(
							requestId, remoteMachines.get(0), true,
							response.getStatus(), isSave);
				}
			}
		};

		mSteampunkService.getMachineWithSerialNumber(serialNumber, callback);
	}

//	@Override
//	public void putStack(final int requestId,
//			final RemoteStack updatedLocalStack) {
//		Callback<RemoteStack> callback = new Callback<RemoteStack>() {
//
//			@Override
//			public void failure(RetrofitError error) {
//				String message;
//				if (checkForNetworkError(error)) {
//					message = "PUT stack failed due to a network error";
//					mNetworkClientCallbacks.putStackRequestFinished(requestId,
//							null, false, GATEWAY_TIMEOUT);
//				} else {
//					message = "PUT stack failed";
//					mNetworkClientCallbacks.putStackRequestFinished(requestId,
//							null, false, error.getResponse() != null ? error
//									.getResponse().getStatus() : GATEWAY_TIMEOUT);
//				}
//				logRetroFitError(error, message);
//			}
//
//			@Override
//			public void success(RemoteStack updatedRemoteStack,
//					Response response) {
//				mNetworkClientCallbacks.putStackRequestFinished(requestId,
//						updatedRemoteStack, true, response.getStatus());
//			}
//		};
//
//		mSteampunkService.putStack(updatedLocalStack.id, updatedLocalStack,
//				callback);
//
//	}

	@Override
	public void postPasswordReset(int requestId, String identifier,String machineId) {
		RemoteClasses.PasswordReset pwReset = new RemoteClasses.PasswordReset();

		pwReset.identifier = identifier;
		pwReset.machine_id = machineId;
		Callback<RemoteClasses.PasswordReset> callback = new Callback<RemoteClasses.PasswordReset>() {

			@Override
			public void failure(RetrofitError error) {
				// TODO handle failed password reset request
			}

			@Override
			public void success(RemoteClasses.PasswordReset remoteLog,
					Response response) {
				// TODO handle successful password reset request
			}

		};
		mSteampunkService.postPasswordReset(pwReset, callback);
	}

	@Override
	public void postPasswordChange(final int requestId, String old,String new_pass) {
		RemoteClasses.PasswordChange pwChange = new RemoteClasses.PasswordChange();

		pwChange.old = old;
		pwChange.new_pass = new_pass;
		Callback<RemoteClasses.PasswordChange> callback = new Callback<RemoteClasses.PasswordChange>() {

			@Override
			public void failure(RetrofitError error) {
				if (checkForNetworkError(error)) {
					mNetworkClientCallbacks.networkError(requestId,
							GATEWAY_TIMEOUT);
					return;
				}
				
				mNetworkClientCallbacks.postPasswordChangeFinished(
						requestId, false, error.getResponse() != null ? error
								.getResponse().getStatus() : GATEWAY_TIMEOUT);
			}


			@Override
			public void success(PasswordChange pwdChange, Response response) {
				mNetworkClientCallbacks.postPasswordChangeFinished(
						requestId, true, response.getStatus());
				
			}

		};
		mSteampunkService.postPasswordChange(pwChange, callback);
	}
	
	@Override
	public void subscribeToRoaster(final int requestId,
			final long steampunkUserId) {
		// TODO Auto-generated method stub

		Callback<String> callback = new Callback<String>() {

			@Override
			public void failure(RetrofitError error) {

				if (checkForNetworkError(error)) {
					mNetworkClientCallbacks.networkError(requestId,
							GATEWAY_TIMEOUT);
					return;
				}
				mNetworkClientCallbacks.postSubscribeToRoasterFinished(
						requestId, false, error.getResponse() != null ? error
								.getResponse().getStatus() : GATEWAY_TIMEOUT);
			}

			@Override
			public void success(String steampunkId, Response response) {
				mNetworkClientCallbacks.postSubscribeToRoasterFinished(
						requestId, true, response.getStatus());
			}
		};

		mSteampunkService.postSubscribe(steampunkUserId, callback);

	}

	@Override
	public void postPinReset(final int requestId, long machineId, String PIN) {
		Callback<String> callback = new Callback<String>() {

			@Override
			public void failure(RetrofitError error) {
				mNetworkClientCallbacks.pinResetRequestFinished(requestId,
						null, false);
			}

			@Override
			public void success(String PIN, Response response) {
				mNetworkClientCallbacks.pinResetRequestFinished(requestId, PIN,
						true);
			}

		};
		mSteampunkService.postPinReset(machineId, PIN, callback);

	}

	private String getAuthorizationToken() {
		SharedPreferences sharedPreferences = mContext.getSharedPreferences(
				Constants.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
		String token = sharedPreferences.getString(Constants.SP_AUTH_TOKEN, "");

		return token;
	}

	private boolean checkForNetworkError(RetrofitError error) {
		if (error.isNetworkError()
				&& (error.getCause() == null
						|| error.getCause().getMessage() == null || !error
						.getCause()
						.getMessage()
						.equals(Constants.NO_AUTHENTICATION_CHALLENGES_ERROR_MESSAGE))) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void checkForUpdates(final int requestId, int version, int device) {
		Callback<RemoteClasses.AvailableUpdate> callback = new Callback<RemoteClasses.AvailableUpdate>() {

			@Override
			public void failure(RetrofitError error) {
			}

			@Override
			public void success(RemoteClasses.AvailableUpdate availableUpdates,
					Response response) {
				int newVersion = availableUpdates.version;
				mNetworkClientCallbacks.getAvailableUpdatesRequestFinished(
						requestId, newVersion);
			}
		};

		mSteampunkService.getAvailableUpdates(version, device, callback);
	}

	@Override
	public void downloadUpdate(final int requestId, int newVersion) {

		String apkurl = SERVER_URL + DOWNLOAD_UPDATE_PATH + newVersion;

		try {
			URL url = new URL(apkurl);
			HttpURLConnection c = (HttpURLConnection) url.openConnection();
			c.setRequestMethod("GET");
			c.addRequestProperty(AUTHORIZATION_HEADER, "Token "
					+ getAuthorizationToken());
			c.connect();

			String PATH = Environment.getExternalStorageDirectory()
					+ "/download/";

			File file = new File(PATH);
			file.mkdirs();
			File outputFile = new File(file, "app.apk");

			FileOutputStream outStream = new FileOutputStream(outputFile);

			InputStream inStream = c.getInputStream();

			byte[] buffer = new byte[1024];

			int len1 = 0;

			while ((len1 = inStream.read(buffer)) != -1) {
				outStream.write(buffer, 0, len1);
			}

			outStream.close();
			inStream.close();

			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.setDataAndType(Uri.fromFile(new File(PATH + "app.apk")),
					"application/vnd.android.package-archive");

			mContext.startActivity(intent);

		} catch (IOException e) {
		}

	}

	@Override
	public void createNewDevice(final int requestId, int version, String name) {

		Callback<RemoteClasses.Device> callback = new Callback<RemoteClasses.Device>() {

			@Override
			public void failure(RetrofitError error) {
				Log.i(TAG, "NEW DEVICE REQUEST FAILED");
			}

			@Override
			public void success(RemoteClasses.Device device, Response response) {
				mNetworkClientCallbacks.createNewDeviceRequestFinished(
						requestId, device.id);
			}
		};
		mSteampunkService.createNewDevice(version, name, callback);
	}

	private void logRetroFitError(RetrofitError error, String message) {
		Response response = error.getResponse();
		String reason;
		String body = null;
		if (null == response) {
			reason = error.getMessage();
			body = DEFAULT_EMPTY_BODY;
		} else {
			reason = response.getReason();
			body = new String(((TypedByteArray) response.getBody()).getBytes());
		}
		Log.i(TAG, message + "\nError: " + reason + "\nBody: " + body);
	}

	/**
	 * @category Retrofit classes
	 * 
	 */
	private interface SteampunkService {
		@GET(RECIPES_LIST_PATH)
		void listRecipes(@Query(RECIPES_TIMESTAMP_PARAM) long timestamp,
				Callback<List<RemoteRecipe>> callback);

		@GET(ROASTERS_LIST_PATH)
		void listRoasters(Callback<List<RemoteRoaster>> callback);

		@GET(USER_MAPPING_PATH)
		List<UserIdMapping> listRoastersSynchronously();

		@GET(FAVORITE_LIST_PATH)
		void listFavorites(Callback<List<RemoteFavorite>> callback);

		@POST(API_TOKEN_AUTH_PATH)
		void postLogin(@Body RemoteLogin newLogin,
				Callback<RemoteLogin> callback);

		@POST(RECIPES_LIST_PATH)
		void postRecipe(@Body RemoteRecipe newRecipe,
				Callback<RemoteRecipe> callback);

//		@POST(STACK_LIST_PATH)
//		void postStack(@Body RemoteStack newStack,
//				Callback<RemoteStack> callback);

//		@POST(AGITATION_LIST_PATH)
//		void postAgitationCycle(@Body RemoteAgitation newAgitation,
//				Callback<RemoteAgitation> callback);

		@POST(SUBSCRIBE_PATH)
		void postSubscribe(@Path("steampunk_user") long steampunkId,
				Callback<String> callback);

		@PUT(RECIPES_INSTANCE_PATH)
		void putRecipe(@Path(RECIPES_PATH_UUID) String uuid,
				@Body RemoteRecipe updatedRecipe,
				Callback<RemoteRecipe> callback);

//		@PUT(STACK_INSTANCE_PATH)
//		void putStack(@Path(STACK_PATH_ID) long id,
//				@Body RemoteStack updatedStack, Callback<RemoteStack> callback);

//		@PUT(AGITATION_INSTANCE_PATH)
//		void putAgitationCycle(@Path(AGITATION_PATH_ID) long id,
//				@Body RemoteAgitation updatedAgitation,
//				Callback<RemoteAgitation> callback);

		@DELETE(RECIPES_INSTANCE_PATH)
		void deleteRecipe(@Path(RECIPES_PATH_UUID) String uuid,
				Callback<RemoteRecipe> callback);

//		@DELETE(STACK_INSTANCE_PATH)
//		void deleteStack(@Path(STACK_PATH_ID) long id,
//				Callback<RemoteStack> callback);

//		@DELETE(AGITATION_INSTANCE_PATH)
//		void deleteAgitationCycle(@Path(AGITATION_PATH_ID) long id,
//				Callback<RemoteAgitation> callback);

		@GET(GRINDS_PATH)
		void listGrinds(Callback<List<RemoteGrind>> callback);

		@POST(LOGS_PATH)
		void postLog(@Body RemoteLog newLog, Callback<RemoteLog> callback);

		// This needs to be a list because our api returns an array
		@GET(MACHINE_SERIAL_NUMBER_PATH)
		void getMachineWithSerialNumber(
				@Query("serial_number") String serialNumber,
				Callback<List<RemoteMachine>> callback);

		@POST(RECIPES_LIST_PATH)
		void rePostRecipe(@Body RemoteRecipe newRecipe,
				Callback<RemoteRecipe> callback);

//		@POST(STACK_LIST_PATH)
//		void rePostStack(@Body RemoteStack newStack,
//				Callback<RemoteStack> callback);

		@POST(FAVORITE_LIST_PATH)
		void postFavorite(@Body RemoteFavorite newFavorite,
				Callback<RemoteFavorite> callback);

		@DELETE(FAVORITE_INSTANCE_PATH)
		void deleteFavorite(@Path(FAVORITE_PATH_UUID) String uuid,
				Callback<RemoteFavorite> callback);

		@POST(PASSWORD_RESET_PATH)
		void postPasswordReset(@Body RemoteClasses.PasswordReset pwReset,
				Callback<RemoteClasses.PasswordReset> callback);
		
		@POST(PASSWORD_CHANGE_PATH)
		void postPasswordChange(@Body RemoteClasses.PasswordChange pwChange,
				Callback<RemoteClasses.PasswordChange> callback);

		@POST(PIN_RESET_PATH)
		void postPinReset(@Path("machine") long machineId, @Body String PIN,
				Callback<String> callback);

		@GET(CHECK_FOR_UPDATES_PATH)
		void getAvailableUpdates(@Path("version") int version,
				@Query("device") int device,
				Callback<RemoteClasses.AvailableUpdate> callback);

		@GET(CREATE_NEW_DEVICE_PATH)
		void createNewDevice(@Path("version") int version,
				@Query("name") String name,
				Callback<RemoteClasses.Device> callback);

		@PUT(USER_INSTANCE_PATH)
		void putUser(@Path(USER_PATH_ID) Long userId, @Body RemoteUser ru,
				Callback<RemoteUser> callback);

		@PUT(STEAMPUNKUSER_INSTANCE_PATH)
		void putSteamPunkUser(@Path(STEAMPUNKUSER_PATH_ID) Long steampunkId,
				@Body RemoteSteamPunkUser rspu, Callback<RemoteUser> spcallback);

	}

	private class SteampunkInterceptor implements RequestInterceptor {

		@Override
		public void intercept(RequestFacade request) {
			String token = getAuthorizationToken();
			if (!("".equals(token)))
				request.addHeader(AUTHORIZATION_HEADER, "Token "
						+ getAuthorizationToken());
		}
	}

//	@Override
//	public void deleteStack(final int requestId, final long stackId) {
//
//		Callback<RemoteStack> callback = new Callback<RemoteClasses.RemoteStack>() {
//
//			@Override
//			public void failure(RetrofitError error) {
//				String message;
//				if (checkForNetworkError(error)) {
//					message = "DELETE stack failed due to a network error";
//					mNetworkClientCallbacks.deleteStackRequestFinished(
//							requestId, stackId, false, GATEWAY_TIMEOUT);
//				} else {
//					message = "DELETE stack failed";
//					mNetworkClientCallbacks.deleteStackRequestFinished(
//							requestId, stackId, false,
//							error.getResponse() != null ? error.getResponse()
//									.getStatus() : GATEWAY_TIMEOUT);
//				}
//
//				logRetroFitError(error, message);
//			}
//
//			@Override
//			public void success(RemoteStack newStack, Response response) {
//				Log.i(TAG, "DELETE recipe successful");
//				mNetworkClientCallbacks.deleteStackRequestFinished(requestId,
//						stackId, true, response.getStatus());
//			}
//		};
//
//		mSteampunkService.deleteStack(stackId, callback);
//	}

//	@Override
//	public void postAgitationCycle(final int requestId,
//			final RemoteAgitation newRemoteAgitation) {
//		Callback<RemoteAgitation> callback = new Callback<RemoteClasses.RemoteAgitation>() {
//
//			@Override
//			public void failure(RetrofitError error) {
//				String message;
//				if (checkForNetworkError(error)) {
//					message = "POST agitation cycle failed due to a network error";
//					mNetworkClientCallbacks.postAgitationCycleRequestFinished(
//							requestId, null, false, GATEWAY_TIMEOUT);
//					SPLog.fromService(mContext, DefaultContentProcessor.getCallbacks(), -1, -1, SPLog.INFO, SPLog.GENERAL,"agitation with the following request ID came back as a network error: "+requestId+" the stack id is: "+newRemoteAgitation.stack);
//				} else {
//					message = "POST agitation cycle failed";
//					mNetworkClientCallbacks.postAgitationCycleRequestFinished(
//							requestId, null, false,
//							error.getResponse() != null ? error.getResponse()
//									.getStatus() : GATEWAY_TIMEOUT);
//					SPLog.fromService(mContext, DefaultContentProcessor.getCallbacks(), -1, -1, SPLog.INFO, SPLog.GENERAL,"agitation with the following request ID came back as a failure: "+requestId+" the stack id is: "+newRemoteAgitation.stack);
//				}
//				logRetroFitError(error, message);
//			}
//
//			@Override
//			public void success(RemoteAgitation newRemoteAgitation,
//					Response response) {
//				mNetworkClientCallbacks.postAgitationCycleRequestFinished(
//						requestId, newRemoteAgitation, true,
//						response.getStatus());
//				SPLog.fromService(mContext, DefaultContentProcessor.getCallbacks(), newRemoteAgitation.id, -1, SPLog.INFO, SPLog.GENERAL,"agitation with the following request ID came back successful: "+requestId+" the stack id is: "+newRemoteAgitation.stack);
//			}
//		};
//
//		mSteampunkService.postAgitationCycle(newRemoteAgitation, callback);
//		SPLog.fromService(mContext, DefaultContentProcessor.getCallbacks(), -1, -1, SPLog.INFO, SPLog.GENERAL,"sending agitation with the following request ID: "+requestId+" the stack id is: "+newRemoteAgitation.stack);
//		
//	}

//	@Override
//	public void putAgitationCycle(final int requestId,
//			final RemoteAgitation updatedLocalAgitation) {
//		Callback<RemoteAgitation> callback = new Callback<RemoteAgitation>() {
//
//			@Override
//			public void failure(RetrofitError error) {
//				String message;
//				if (checkForNetworkError(error)) {
//					message = "PUT agitation failed due to a network error";
//					mNetworkClientCallbacks.putAgitationRequestFinished(
//							requestId, null, false, GATEWAY_TIMEOUT);
//				} else {
//					message = "PUT agitation failed";
//					mNetworkClientCallbacks.putAgitationRequestFinished(
//							requestId, null, false,
//							error.getResponse() != null ? error.getResponse()
//									.getStatus() : GATEWAY_TIMEOUT);
//				}
//				logRetroFitError(error, message);
//			}
//
//			@Override
//			public void success(RemoteAgitation updatedRemoteAgitation,
//					Response response) {
//				mNetworkClientCallbacks.putAgitationRequestFinished(requestId,
//						updatedRemoteAgitation, true, response.getStatus());
//			}
//		};
//
//		mSteampunkService.putAgitationCycle(updatedLocalAgitation.id,
//				updatedLocalAgitation, callback);
//	}

//	@Override
//	public void deleteAgitationCycle(final int requestId, final long remoteId) {
//		Callback<RemoteAgitation> callback = new Callback<RemoteAgitation>() {
//
//			@Override
//			public void failure(RetrofitError error) {
//				String message;
//				if (checkForNetworkError(error)) {
//					message = "DELETE agitation failed due to a network error";
//					mNetworkClientCallbacks.deleteAgitationRequestFinished(
//							requestId, remoteId, false, GATEWAY_TIMEOUT);
//				} else {
//					message = "DELETE agitation failed";
//					mNetworkClientCallbacks.deleteAgitationRequestFinished(
//							requestId, remoteId, false,
//							error.getResponse() != null ? error.getResponse()
//									.getStatus() : GATEWAY_TIMEOUT);
//				}
//				logRetroFitError(error, message);
//			}
//
//			@Override
//			public void success(RemoteAgitation updatedRemoteAgitation,
//					Response response) {
//				mNetworkClientCallbacks.deleteAgitationRequestFinished(
//						requestId, remoteId, true, response.getStatus());
//			}
//		};
//
//		mSteampunkService.deleteAgitationCycle(remoteId, callback);
//
//	}

	@Override
	public void postFavorite(final int requestId,
			final RemoteFavorite remoteFavorite) {
		Callback<RemoteFavorite> callback = new Callback<RemoteFavorite>() {

			@Override
			public void failure(RetrofitError error) {
				String message;
				if (checkForNetworkError(error)) {
					message = "POST favorite failed due to a network error";
					// mNetworkClientCallbacks.networkError(requestId,GATEWAY_TIMEOUT);
					mNetworkClientCallbacks.postFavoriteRequestFinished(
							requestId, remoteFavorite, false, GATEWAY_TIMEOUT);
				} else {
					message = "POST favorite failed";
					mNetworkClientCallbacks.postFavoriteRequestFinished(
							requestId, remoteFavorite, false, error
									.getResponse() != null ? error
									.getResponse().getStatus() : GATEWAY_TIMEOUT);
				}
				logRetroFitError(error, message);
			}

			@Override
			public void success(RemoteFavorite updatedFavorite,
					Response response) {
				mNetworkClientCallbacks.postFavoriteRequestFinished(requestId,
						updatedFavorite, true, response.getStatus());
			}
		};

		mSteampunkService.postFavorite(remoteFavorite, callback);
	}

	@Override
	public void deleteFavorite(final int requestId, final String uuid) {
		Callback<RemoteFavorite> callback = new Callback<RemoteFavorite>() {

			@Override
			public void failure(RetrofitError error) {
				String message;
				if (checkForNetworkError(error)) {
					message = "DELETE favorite failed due to a network error";
					mNetworkClientCallbacks.deleteFavoriteRequestFinished(
							requestId, uuid, false, GATEWAY_TIMEOUT);
				} else {
					message = "DELETE favorite failed";
					mNetworkClientCallbacks.deleteFavoriteRequestFinished(
							requestId, uuid, false,
							error.getResponse() != null ? error.getResponse()
									.getStatus() : GATEWAY_TIMEOUT);
				}
				logRetroFitError(error, message);
			}

			@Override
			public void success(RemoteFavorite updatedFavorite,
					Response response) {
				mNetworkClientCallbacks.deleteFavoriteRequestFinished(
						requestId, uuid, true, response.getStatus());
			}
		};

		mSteampunkService.deleteFavorite(uuid, callback);
	}

	@Override
	public void getFavorites(final int requestId, final long timeStamp) {
		// TODO Auto-generated method stub
		Callback<List<RemoteFavorite>> callback = new Callback<List<RemoteFavorite>>() {

			@Override
			public void failure(RetrofitError error) {
				String message;
				if (checkForNetworkError(error)) {
					message = "GET favorites failed due to a network error";
					mNetworkClientCallbacks.getFavoritesRequestFinished(
							requestId, timeStamp, null, false, GATEWAY_TIMEOUT);
				} else {
					message = "GET favorites failed";
					mNetworkClientCallbacks.getFavoritesRequestFinished(
							requestId, timeStamp, null, false, error
									.getResponse() != null ? error
									.getResponse().getStatus() : GATEWAY_TIMEOUT);
				}
				logRetroFitError(error, message);
			}

			@Override
			public void success(List<RemoteFavorite> favoritesList,
					Response response) {
				mNetworkClientCallbacks.getFavoritesRequestFinished(requestId,
						timeStamp, favoritesList, true, response.getStatus());
			}

		};

		mSteampunkService.listFavorites(callback);
	}

	@Override
	public void updateAccountSettings(final int requestId, final RemoteUser ru,
			final RemoteSteamPunkUser rspu, final Long steampunkId,
			final Long userId, final String requestedUsername) {

		Callback<RemoteUser> callback = new Callback<RemoteUser>() {

			@Override
			public void failure(RetrofitError error) {
				String message;
				//SPLog.debug(" failure to put user "+error.getBody().toString());
				if (checkForNetworkError(error)) {
					message = "PUT user failed due to a network error";
					mNetworkClientCallbacks.putUserRequestFinished(requestId,
							null, false, GATEWAY_TIMEOUT, requestedUsername, null);
				} else {
					message = "PUT user failed";
					Response response = error.getResponse();
					String body = null;
					if (null == response) {
						body = DEFAULT_EMPTY_BODY;
					} else {
						body = new String(((TypedByteArray) response.getBody()).getBytes());
					}
					mNetworkClientCallbacks.putUserRequestFinished(requestId,
							null, false, error.getResponse() != null ? error
									.getResponse().getStatus() : GATEWAY_TIMEOUT, requestedUsername,body);
				}
				logRetroFitError(error, message);
			}

			@Override
			public void success(RemoteUser updatedRemoteUser, Response response) {
				mNetworkClientCallbacks.putUserRequestFinished(requestId,
						updatedRemoteUser, true, response.getStatus(), requestedUsername, null);
			}
		};

		Callback<RemoteUser> spcallback = new Callback<RemoteUser>() {

			@Override
			public void failure(RetrofitError error) {
				String message;
				if (checkForNetworkError(error)) {
					message = "PUT steampunkuser failed due to a network error";
					mNetworkClientCallbacks.putSteamPunkUserRequestFinished(
							requestId, null, false, GATEWAY_TIMEOUT);
				} else {
					message = "PUT steampunkuser failed";
					mNetworkClientCallbacks.putSteamPunkUserRequestFinished(
							requestId, null, false,
							error.getResponse() != null ? error.getResponse()
									.getStatus() : GATEWAY_TIMEOUT);
				}
				logRetroFitError(error, message);
			}

			@Override
			public void success(RemoteUser updatedRemoteUser, Response response) {
				mNetworkClientCallbacks.putSteamPunkUserRequestFinished(
						requestId, updatedRemoteUser, true,
						response.getStatus());
			}
		};

		mSteampunkService.putUser(userId, ru, callback);
		mSteampunkService.putSteamPunkUser(steampunkId, rspu, spcallback);
	}
}
