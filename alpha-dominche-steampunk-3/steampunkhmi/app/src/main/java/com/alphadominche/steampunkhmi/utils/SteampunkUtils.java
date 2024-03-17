package com.alphadominche.steampunkhmi.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SteampunkUtils {
    public final static String USER_TYPE_ROASTER = "Roaster";
    public final static String USER_TYPE_ADMIN = "Admin";

    /**
     * Get a a unique request id for restclient requests
     *
     * @return unique id for requests
     */
    public static int getRequestId(Context context) {
        SharedPreferences sharedPreferences = context
                .getSharedPreferences(Constants.SHARED_PREFS_NAME,
                        Context.MODE_PRIVATE);
        Editor editor = sharedPreferences.edit();

        int requestId;
        int newRequestId;

        if (!sharedPreferences.contains(Constants.SP_REQUEST_ID)) {
            requestId = 1;
        } else {
            requestId = sharedPreferences.getInt(Constants.SP_REQUEST_ID, -1);
        }

        // Reset the request id if it gets too big
        if (requestId == Integer.MAX_VALUE) {
            newRequestId = 1;
        } else {
            newRequestId = requestId + 1;
        }

        editor.putInt(Constants.SP_REQUEST_ID, newRequestId);
        editor.commit();

        return requestId;
    }

    /**
     * Get a date string of the current date that is formatted for the server
     *
     * @return a string of the current date formatted for the server
     */
    public static String getCurrentDateString() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.US);
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date date = new Date();
        String currentDateString = dateFormat.format(date);

        return currentDateString;
    }

    /**
     * Get the id of the machine
     *
     * @param context app context
     * @return the id of the machine or null if the app has not gotten its
     * machine id from the server
     */
    public static Long getMachineId(Context context) {
        SharedPreferences sharedPreferences = getSteampunkSharedPreferences(context);
        Long machineId = sharedPreferences.contains(Constants.SP_MACHINE_ID) ? sharedPreferences
                .getLong(Constants.SP_MACHINE_ID, 0) : null;

        return machineId;
    }

    /**
     * Get the shared preferences for the app with the correct name and
     * privilege
     *
     * @param context app context
     * @return the properly configured shared preferences for the app
     */
    public static SharedPreferences getSteampunkSharedPreferences(
            Context context) {
        return context.getSharedPreferences(Constants.SHARED_PREFS_NAME,
                Context.MODE_PRIVATE);
    }

    /**
     * Return the currently logged in user's id
     *
     * @param context app context
     * @return the user id of the logged in user or 0 if there is no user
     * currently logged in
     */
    public static Long getCurrentUserId(Context context) {
        SharedPreferences sharedPreferences = getSteampunkSharedPreferences(context);
        Long currentSteampunkUserId = sharedPreferences.getLong(
                Constants.SP_USER_ID, 0);
        // TODO Either this code needs to come back, or we need to raise an
        // exception when you try to get the current steampunk user id and
        // there's not a logged in user; make sure to update the docs
        // Integer currentSteampunkUserId = sharedPreferences
        // .contains(Constants.SP_STEAMPUNKUSER_ID) ? sharedPreferences.getInt(
        // Constants.SP_STEAMPUNKUSER_ID, 0) : null;

        return currentSteampunkUserId;
    }

    /**
     * Return the currently logged in user's id
     *
     * @param context app context
     * @return the user id of the logged in user or 0 if there is no user
     * currently logged in
     */
    public static Long getCurrentSteampunkUserId(Context context) {
        SharedPreferences sharedPreferences = getSteampunkSharedPreferences(context);
        Long currentSteampunkUserId = sharedPreferences.getLong(
                Constants.SP_STEAMPUNKUSER_ID, 0);
        // TODO Either this code needs to come back, or we need to raise an
        // exception when you try to get the current steampunk user id and
        // there's not a logged in user; make sure to update the docs
        // Integer currentSteampunkUserId = sharedPreferences
        // .contains(Constants.SP_STEAMPUNKUSER_ID) ? sharedPreferences.getInt(
        // Constants.SP_STEAMPUNKUSER_ID, 0) : null;

        return currentSteampunkUserId;
    }

    /**
     * Return the currently logged in user's type
     *
     * @param context app context
     * @return the type of the logged in user or null if there is no user
     * currently logged in
     */
    public static String getCurrentSteampunkUserType(Context context) {
        SharedPreferences sharedPreferences = getSteampunkSharedPreferences(context);
        String currentSteampunkUserType = sharedPreferences.getString(
                Constants.SP_USER_TYPE, null);

        return currentSteampunkUserType;
    }

    public static long getLastRecipeSyncDate(Context context) {
        // TODO get actual timestamp from shared prefs
        return 1381881600;
    }

    public static long getLastFavoritesSyncDate(Context mContext) {
        // TODO Auto-generated method stub
        return 1381881600;
    }

    public static long getRecipeForCrucible(Context context, int crucibleIndex) {
        SharedPreferences prefs = SteampunkUtils.getSteampunkSharedPreferences(context);
        return prefs.getLong(Constants.CRUCIBLE_RECIPE_ID_PREFIX + crucibleIndex, -1);
    }

    public static void saveRecipeForCrucible(Context context, int crucibleIndex, long id) {
        SharedPreferences prefs = SteampunkUtils.getSteampunkSharedPreferences(context);
        Editor e = prefs.edit();
        e.putLong(Constants.CRUCIBLE_RECIPE_ID_PREFIX + crucibleIndex, id);
        e.apply();
    }
}
