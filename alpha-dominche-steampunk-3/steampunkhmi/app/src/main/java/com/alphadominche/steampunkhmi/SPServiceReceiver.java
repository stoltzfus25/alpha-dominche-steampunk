package com.alphadominche.steampunkhmi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import de.greenrobot.event.EventBus;

public class SPServiceReceiver extends BroadcastReceiver {
    public static final String STEAM_PUNK_CONTROL_PERMISSION = "com.alphadominche.STEAM_PUNK_CONTROL";
    public static final String BOILER_STATUS = "BOILER_STATUS";
    public static final String CRUCIBLE_STATUS = "CRUCIBLE_STATUS";
    public static final String MACHINE_SETTINGS_STATUS = "MACHINE_SETTINGS_STATUS";
    public static final String STEAMPUNK_ERROR_BROADCAST = "STEAMPUNK_ERROR_BROADCAST";
    public static final String MACHINE_SETTINGS_REQUEST = "MACHINE_SETTINGS_REQUEST";
    public static final String RELEASE_STEAM_BROADCAST = "RELEASE_STEAM_BROADCAST";
    public static final String NETWORK_CONNECTED_BROADCAST = "NETWORK_CONNECTED_BROADCAST";
    public static final String NETWORK_NOT_CONNECTED_BROADCAST = "NETWORK_NOT_CONNECTED_BROADCAST";
    public static final String IOIO_CONNECTED_BROADCAST = "IOIO_CONNECTED_BROADCAST";

    public static final String CRUCIBLE = "crucible";
    public static final String FILL = "fill";
    public static final String STEAM = "steam";
    public static final String DRAIN = "drain";
    public static final String TEMPERATURE = "temperature";
    public static final String RUNNING = "running";
    public static final String EDGES = "edges";
    public static final String VOLUME = "volume";
    public static final String TIME_LEFT_IN_BREW = "timeLeftInBrew";
    public static final String STATE = "state";
    public static final String TOO_MUCH_FLOW = "tooMuchFlow";
    public static final String TOO_MUCH_STEAM = "tooMuchSteam";
    public static final String NOT_ENOUGH_FLOW = "notEnoughFlow";
    public static final String NOT_ENOUGH_STEAM = "notEnoughSteam";
    public static final String HEAT = "heat";
    public static final String ERROR_MESSAGE = "errMsg";
    public static final String STEAMED_TOO_MUCH_FOR_VOLUME = "steamedTooMuchForVolume";
    public static final String UID = "UID";
    public static final String RINSE_VOLUME = "rinseVolume";
    public static final String RINSE_TEMP = "rinseTemp";
    public static final String BOILER_TEMP = "boilerTemp";

    /**
     * //int crucible, ounces/temp
     */
    @Override
    public void onReceive(Context context, Intent intent) {

        //receive flow meter signals...should be 1 ounce per intent
        Bundle extras = intent.getExtras();
        if (intent.getAction().equals(CRUCIBLE_STATUS)) {
            int index = extras.getInt(CRUCIBLE);
            boolean fill = extras.getBoolean(FILL);
            boolean steam = extras.getBoolean(STEAM);
            boolean drain = extras.getBoolean(DRAIN);
            double temperature = extras.getDouble(TEMPERATURE);
            int edges = extras.getInt(EDGES);
            double volume = extras.getDouble(VOLUME);
            int time = extras.getInt(TIME_LEFT_IN_BREW);
            SPServiceCrucibleState state = (SPServiceCrucibleState) extras.get(STATE);
            boolean tooMuchFlow = extras.getBoolean(TOO_MUCH_FLOW);
            boolean tooMuchSteam = extras.getBoolean(TOO_MUCH_STEAM);
            boolean notEnoughFlow = extras.getBoolean(NOT_ENOUGH_FLOW);
            boolean notEnoughSteam = extras.getBoolean(NOT_ENOUGH_STEAM);
            SPCrucibleState modelState = this.mapCrucibleServiceToModelState(state);
            SPModel.getInstance(context).updateCrucibleState(index, fill, steam, drain, temperature, volume, edges, modelState, time);
            if (modelState == SPCrucibleState.EXTRACTING) {
                SPModel.getInstance(context).setExtractionTimeLeftForCrucible(index, time);
            }

            //used for screen blanking!
            if (modelState != SPCrucibleState.IDLE) {
                SPActivity.notifyOfEvent();
            }
        } else if (intent.getAction().equals(BOILER_STATUS)) {
            boolean heating = extras.getBoolean(HEAT);
            boolean filling = extras.getBoolean(FILL);
            double temp = extras.getDouble(TEMPERATURE);
            boolean running = extras.getBoolean(RUNNING);
            String error = extras.getString(ERROR_MESSAGE);
            SPModel.getInstance(context).setBoilerStatus(heating, filling, temp, running, error);
        } else if (intent.getAction().equals(MACHINE_SETTINGS_STATUS)) {
            //need to communicate this back somehow
        } else if (intent.getAction().equals(MACHINE_SETTINGS_REQUEST)) {
            SPModel.getInstance(context).setMachineSettings(
                    SPModel.getInstance(context).getBoilerTargetTemp(),
                    SPModel.getInstance(context).getRinseTemp(),
                    SPModel.getInstance(context).getRinseVolume());
        } else if (intent.getAction().equals(STEAMPUNK_ERROR_BROADCAST)) {
            if (extras.get(STEAMED_TOO_MUCH_FOR_VOLUME) != null) {
                boolean steamedTooMuchForVolume = extras.getBoolean(STEAMED_TOO_MUCH_FOR_VOLUME);
                int index = extras.getInt(CRUCIBLE);
                SPModel.getInstance(context).setCrucibleSteamedTooMuchOnFillAndHeating(index);
            }
        } else if (intent.getAction().equals(RELEASE_STEAM_BROADCAST)) {
            EventBus.getDefault().post(new SPServiceBoiler.ReleaseSteamRequest());
        } else if (intent.getAction().equals(NETWORK_CONNECTED_BROADCAST)) {
            SPModel.getInstance(context).setNetworkConnectionStatus(true);
        } else if (intent.getAction().equals(NETWORK_NOT_CONNECTED_BROADCAST)) {
            SPModel.getInstance(context).setNetworkConnectionStatus(false);
        } else if (intent.getAction().equals(IOIO_CONNECTED_BROADCAST)) {
            SPModel.getInstance(context).setIOIOConnectionStatus(true);
        }

        SPLog.debug("finished receiving message");
    }

    private SPCrucibleState mapCrucibleServiceToModelState(SPServiceCrucibleState state) {
        SPCrucibleState modelState = SPCrucibleState.IDLE;
        if (state == SPServiceCrucibleState.EMPTY_START) {
            modelState = SPCrucibleState.IDLE;
        } else if (state == SPServiceCrucibleState.FILLING_FOR_BREW) {
            modelState = SPCrucibleState.FILLING;
        } else if (state == SPServiceCrucibleState.COMPENSATING_VOLUME_FOR_STEAM) {
            modelState = SPCrucibleState.HEATING;
        } else if (state == SPServiceCrucibleState.MAKE_SURE_BREW_WATER_IS_IN_TOP) {
            modelState = SPCrucibleState.HEATING;
        } else if (state == SPServiceCrucibleState.PUSHING_BREW_WATER_TO_TOP) {
            modelState = SPCrucibleState.HEATING;
        } else if (state == SPServiceCrucibleState.BREW_WATER_IN_TOP_AND_HEATING) {
            modelState = SPCrucibleState.HEATING;
        } else if (state == SPServiceCrucibleState.START_BREW_DRAIN) {
            modelState = SPCrucibleState.HEATING;
        } else if (state == SPServiceCrucibleState.FINISH_BREW_DRAIN) {
            modelState = SPCrucibleState.INSERT_PISTON;
        } else if (state == SPServiceCrucibleState.WAITING_FOR_BREW_PISTON_INSERTION) {
            modelState = SPCrucibleState.START_BREWING;
        } else if (state == SPServiceCrucibleState.AGITATING) {
            modelState = SPCrucibleState.AGITATING;
        } else if (state == SPServiceCrucibleState.STEEP_BETWEEN_AGITATIONS) {
            modelState = SPCrucibleState.STEEPING;
        } else if (state == SPServiceCrucibleState.BEVERAGE_VACUUM_BREAK) {
            modelState = SPCrucibleState.EXTRACTING;
        } else if (state == SPServiceCrucibleState.START_BEVERAGE_PULL_DOWN) {
            modelState = SPCrucibleState.EXTRACTING;
        } else if (state == SPServiceCrucibleState.FINISH_BEVERAGE_PULL_DOWN) {
            modelState = SPCrucibleState.EXTRACTING;
        } else if (state == SPServiceCrucibleState.WAITING_TO_DISPENSE_AND_RINSE) {
            modelState = SPCrucibleState.START_RINSING;
        } else if (state == SPServiceCrucibleState.FILLING_FOR_RINSE) {
            modelState = SPCrucibleState.RINSING;
        } else if (state == SPServiceCrucibleState.PUSHING_RINSE_WATER_TO_TOP) {
            modelState = SPCrucibleState.RINSING;
        } else if (state == SPServiceCrucibleState.RINSE_WATER_IN_TOP_AND_HEATING) {
            modelState = SPCrucibleState.RINSING;
        } else if (state == SPServiceCrucibleState.START_RINSE_DRAIN) {
            modelState = SPCrucibleState.RINSING;
        } else if (state == SPServiceCrucibleState.FINISH_RINSE_DRAIN) {
            modelState = SPCrucibleState.RINSING;
        } else if (state == SPServiceCrucibleState.FILL_FOR_CLEAN) {
            modelState = SPCrucibleState.CLEANING_FILL_AND_HEAT;
        } else if (state == SPServiceCrucibleState.HEAT_FOR_CLEAN) {
            modelState = SPCrucibleState.CLEANING_FILL_AND_HEAT;
        } else if (state == SPServiceCrucibleState.AGITATE_FOR_CLEAN) {
            modelState = SPCrucibleState.CLEANING_AGITATING;
        } else if (state == SPServiceCrucibleState.SIT_WITH_WATER_AT_TOP_FOR_CLEAN) {
            modelState = SPCrucibleState.CLEANING_SOAK;
        } else if (state == SPServiceCrucibleState.CLEAN_VACUUM_BREAK) {
            modelState = SPCrucibleState.CLEANING_DRAIN;
        } else if (state == SPServiceCrucibleState.START_CLEAN_DRAIN) {
            modelState = SPCrucibleState.CLEANING_DRAIN;
        } else if (state == SPServiceCrucibleState.WAITING_FOR_CLEAN_DRAIN) {
            modelState = SPCrucibleState.CLEANING_DRAIN;
        } else if (state == SPServiceCrucibleState.SIT_WITH_WATER_AT_BOTTOM_FOR_CLEAN) {
            modelState = SPCrucibleState.CLEANING_SOAK;
        } else if (state == SPServiceCrucibleState.WAIT_FOR_CLEAN_RINSE) {
            modelState = SPCrucibleState.WAITING_FOR_CLEANING_RINSE;
        } else if (state == SPServiceCrucibleState.FILL_FOR_CLEAN_RINSE) {
            modelState = SPCrucibleState.CLEANING_RINSE_FILL_AND_HEAT;
        } else if (state == SPServiceCrucibleState.HEAT_FOR_CLEAN_RINSE) {
            modelState = SPCrucibleState.CLEANING_RINSE_FILL_AND_HEAT;
        } else if (state == SPServiceCrucibleState.AGITATE_FOR_CLEAN_RINSE) {
            modelState = SPCrucibleState.CLEANING_RINSE_AGITATING;
        } else if (state == SPServiceCrucibleState.SIT_WITH_WATER_AT_TOP_FOR_CLEAN_RINSE) {
            modelState = SPCrucibleState.CLEANING_RINSE_SOAK;
        } else if (state == SPServiceCrucibleState.CLEAN_RINSE_VACUUM_BREAK) {
            modelState = SPCrucibleState.CLEANING_RINSE_DRAIN;
        } else if (state == SPServiceCrucibleState.START_CLEAN_RINSE_DRAIN) {
            modelState = SPCrucibleState.CLEANING_RINSE_DRAIN;
        } else if (state == SPServiceCrucibleState.WAITING_FOR_CLEAN_RINSE_DRAIN) {
            modelState = SPCrucibleState.CLEANING_RINSE_DRAIN;
        } else if (state == SPServiceCrucibleState.SIT_WITH_WATER_AT_BOTTOM_FOR_CLEAN_RINSE) {
            modelState = SPCrucibleState.CLEANING_RINSE_SOAK;
        }
        return modelState;
    }

}
