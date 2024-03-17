package com.alphadominche.steampunkhmi;

import android.app.Notification;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import ioio.lib.api.DigitalOutput;
import ioio.lib.api.IOIO;
import ioio.lib.api.exception.ConnectionLostException;
import ioio.lib.util.BaseIOIOLooper;
import ioio.lib.util.IOIOLooper;
import ioio.lib.util.android.IOIOService;

public class SPIOIOService extends IOIOService {
    public static final int MAX_CRUCIBLE_COUNT = 4;

    private static final int[] FLOW_METER_PIN = {5, 6, 7, 8};
    private static final int[] FILL_SOLENOID_PIN = {1, 2, 3, 4};
    private static final int[] PULL_DOWN_ACTUATOR_PIN = {30, 32, 33, 31};
    private static final int[] STEAM_SOLENOID_PIN = {34, 36, 37, 35};
    private static final int[] CRUCIBLE_TEMP_PIN = {46, 45, 44, 43};
    private static final int BOILER_FILL_PIN = 41;
    private static final int BOILER_HEAT_PIN = 27;
    private static final int BOILER_TEMP_PIN = 42;
    private static final int KEEP_ALIVE_PIN = 12; //AKA SPIout
    private static final int RED_LED_PIN = 23;
    private static final int BLUE_LED_PIN = 22;
    private static final int GREEN_LED_PIN = 21;

    private static final double BOILER_INITIAL_TARGET_TEMP = 402.59;

    public static final String START_SPIOIO_SERVICE_INTENT = "START_SPIOIO_SERVICE";
    public static final String CRUCIBLE_COMMAND_INTENT = "CRUCIBLE_COMMAND";
    public static final String MACHINE_COMMAND_INTENT = "MACHINE_COMMAND";
    public static final String MANUAL_MODE_COMMAND_INTENT = "MANUAL_MODE_COMMAND";

    public static final String IOIO_CONNECTED_THROUGH_USB = "IOIO_CONNECTED_THROUGH_USB";

    public static final String STOP_SERVICE_COMMAND = "stop";

    public static final String START_BREW_PROCESS = "START_BREW_PROCESS";
    public static final String COMMENCE_BREW_PROCESS = "COMMENCE_BREW_PROCESS";
    public static final String START_RINSE_CYCLE = "START_RINSE_CYCLE";
    public static final String CANCEL_PROCESS = "CANCEL_PROCESS";
    public static final String READ_CRUCIBLE_STATE = "READ_CRUCIBLE_STATE";
    public static final String RELEASE_STEAM = "RELEASE_STEAM";
    public static final String START_FORCE_STEAM = "START_FORCE_STEAM";
    public static final String STOP_FORCE_STEAM = "STOP_FORCE_STEAM";
    public static final String START_FORCE_FILL = "START_FORCE_FILL";
    public static final String STOP_FORCE_FILL = "STOP_FORCE_FILL";
    public static final String SET_BOILER_MAX_TEMP = "SET_BOILER_MAX_TEMP";
    public static final String READ_BOILER_STATE = "READ_BOILER_STATE";
    public static final String START_FORCE_BOILER_HEAT_ON = "START_FORCE_BOILER_HEAT_ON";
    public static final String STOP_FORCE_BOILER_HEAT_ON = "STOP_FORCE_BOILER_HEAT_ON";
    public static final String START_FORCE_BOILER_HEAT_OFF = "START_FORCE_BOILER_HEAT_OFF";
    public static final String STOP_FORCE_BOILER_HEAT_OFF = "STOP_FORCE_BOILER_HEAT_OFF";
    public static final String MACHINE_SETTINGS = "MACHINE_SETTINGS";
    public static final String READ_MACHINE_SETTINGS = "READ_MACHINE_SETTINGS";
    public static final String START_CLEANING_CYCLE = "START_CLEANING_CYCLE";
    public static final String FINISH_CLEANING_CYCLE = "FINISH_CLEANING_CYCLE";
    public static final String RESET_CRUCIBLE_ERROR_STATE = "RESET_CRUCIBLE_ERROR_STATE";
    public static final String LOCK_CRUCIBLE = "LOCK_CRUCIBLE";
    public static final String UNLOCK_CRUCIBLE = "UNLOCK_CRUCIBLE";
    public static final String CRUCIBLE = "crucible";
    public static final String COMMAND = "command";
    public static final String VOLUME = "volume";
    public static final String TEMPERATURE = "temperature";
    public static final String CYCLES = "cycles";
    public static final String DRAIN_TIME = "drainTime";
    public static final String VACUUM_BREAK = "vacuumBreak";
    public static final String AGITATIONS = "agitations";
    public static final String PULSE_WIDTHS = "pulseWidths";
    public static final String CRUCIBLE_COUNT = "crucibleCount";
    public static final String RINSE_VOLUME = "rinseVolume";
    public static final String RINSE_TEMP = "rinseTemp";
    public static final String BOILER_TEMP = "boilerTemp";
    public static final String SET_CRUCIBLE_COUNT = "setCrucibleCount";

    public static final int TIME_ENTRIES_PER_AGITATION_CYCLE = 2;

    public static final String START_MANUAL_MODE = "START_MANUAL_MODE";
    public static final String STOP_MANUAL_MODE = "STOP_MANUAL_MODE";
    public static final String FORCE_DRAIN_OPEN = "FORCE_DRAIN_OPEN";
    public static final String STOP_FORCE_DRAIN_OPEN = "STOP_FORCE_DRAIN_OPEN";

    public static final int CLEANING_CYCLES = 3;
    public static final int CLEANING_RINSE_CYCLES = 3;

    public static final long KEEP_ALIVE_DELAY = 1000;

    private boolean mAlreadyStarted = false;
    private boolean mConnected = false;
    private int mCrucibleCount;
    private boolean mCountHasBeenSet;

    private SPServiceCrucible[] mCrucibles;
    private SPServiceBoiler mBoiler;
    private DigitalOutput mKeepAliveSignal;
    private DigitalOutput mRedLED;
    private DigitalOutput mBlueLED;
    private DigitalOutput mGreenLED;

    private double mRinseVolume;
    private double mRinseTemp;

    private IOIO ioio;

    private SPIOIOService service;

    private boolean mInManualMode;

    @Override
    protected IOIOLooper createIOIOLooper() {
        return new BaseIOIOLooper() {

            private DigitalOutput led_;

            @Override
            protected void setup() throws ConnectionLostException, InterruptedException { //SPLog.debug("setting up the ioio");
                Intent ni = new Intent(SPServiceReceiver.STEAMPUNK_ERROR_BROADCAST);
                ni.putExtra(SPServiceReceiver.ERROR_MESSAGE, "entered setup!");
//				service.sendOrderedBroadcast(ni, SPServiceReceiver.STEAM_PUNK_CONTROL_PERMISSION);
                doBroadcast(ni, SPServiceReceiver.STEAM_PUNK_CONTROL_PERMISSION);

                try {
                    led_ = ioio_.openDigitalOutput(IOIO.LED_PIN);
                } catch (Exception e) {
                }

                ioio = ioio_;

                mBoiler = new SPServiceBoiler(
                        BOILER_INITIAL_TARGET_TEMP,
                        ioio.openDigitalInput(BOILER_FILL_PIN),
                        ioio.openDigitalOutput(BOILER_HEAT_PIN),
                        ioio.openAnalogInput(BOILER_TEMP_PIN),
                        service,
                        SPTempUnitType.KELVIN);
                mBoiler.start();
                SPLog.debug("################# just started boiler");

                mCrucibles = new SPServiceCrucible[MAX_CRUCIBLE_COUNT];
                for (int i = 0; i < MAX_CRUCIBLE_COUNT; i++) {
                    mCrucibles[i] = new SPServiceCrucible(
                            ioio.openDigitalInput(FLOW_METER_PIN[i]),
                            ioio.openAnalogInput(CRUCIBLE_TEMP_PIN[i]),
                            ioio.openDigitalOutput(FILL_SOLENOID_PIN[i]),
                            ioio.openDigitalOutput(STEAM_SOLENOID_PIN[i]),
                            ioio.openDigitalOutput(PULL_DOWN_ACTUATOR_PIN[i]),
                            service,
                            i);
                    if (i < mCrucibleCount) {
                        mCrucibles[i].start();
                    } else {
                        mCrucibles[i].stopRunning();
                    }
                }

                if (mInManualMode) {
                    for (int i = 0; i < mCrucibleCount; i++) {
                        mCrucibles[i].setInManualMode(true);
                    }
                    mBoiler.setInManualMode(true);
                }

                try {
                    mKeepAliveSignal = ioio.openDigitalOutput(KEEP_ALIVE_PIN);
                    mRedLED = ioio.openDigitalOutput(RED_LED_PIN);
                    mGreenLED = ioio.openDigitalOutput(GREEN_LED_PIN);
                    mBlueLED = ioio.openDigitalOutput(BLUE_LED_PIN);

                    mRedLED.write(false);
                    mGreenLED.write(false);
                    mBlueLED.write(false);
                } catch (Exception e) {
                }

                Intent machineSettingsRequest = new Intent(SPServiceReceiver.MACHINE_SETTINGS_REQUEST);
//				sendOrderedBroadcast(machineSettingsRequest, SPServiceReceiver.STEAM_PUNK_CONTROL_PERMISSION);
                doBroadcast(machineSettingsRequest, SPServiceReceiver.STEAM_PUNK_CONTROL_PERMISSION);
                mConnected = true;
                Thread connectionNotificationThread = new Thread() {
                    @Override
                    public void run() {
                        while (mConnected) {
                            sendConnectionNotification();
                            try {
                                sleep(1000);
                            } catch (InterruptedException e) {
                                mConnected = false;
                            }
                        }
                    }
                };
                connectionNotificationThread.start();
            }

            @Override
            public void loop() throws ConnectionLostException, InterruptedException {
                if (!mAlreadyStarted) return;
                led_.write(false);
                mKeepAliveSignal.write(false);
                Thread.sleep(KEEP_ALIVE_DELAY);
                led_.write(true);
                mKeepAliveSignal.write(true);
                Thread.sleep(KEEP_ALIVE_DELAY);
            }

            @Override
            public void disconnected() {
                mConnected = false;
            }
        };
    }

    public void log(String msg) {
        Intent ni = new Intent(SPServiceReceiver.STEAMPUNK_ERROR_BROADCAST);
        ni.putExtra(SPServiceReceiver.ERROR_MESSAGE, msg);
//		sendOrderedBroadcast(ni, SPServiceReceiver.STEAM_PUNK_CONTROL_PERMISSION);
        doBroadcast(ni, SPServiceReceiver.STEAM_PUNK_CONTROL_PERMISSION);
    }

    /**
     * expects all SI units: milliliters for volume, Kelvins for temperature
     */
    @Override
    public void onStart(Intent intent, int startId) {
        if (!mAlreadyStarted) { //SPLog.debug("NOT STARTED! starting up...");
            mInManualMode = false;
//			if (intent.getExtras() != null && intent.hasExtra(CRUCIBLE_COUNT)) {
//				mCrucibleCount = intent.getExtras().getInt(CRUCIBLE_COUNT);
//				if (mCrucibleCount <= 0) mCrucibleCount = MAX_CRUCIBLE_COUNT;
//			} else {
            mCrucibleCount = MAX_CRUCIBLE_COUNT;
//			}
            mCountHasBeenSet = false;
            mBoiler = null;
            service = this;

//			if (intent.getAction().equals(START_SPIOIO_SERVICE_INTENT) &&
//					intent.hasExtra(IOIO_CONNECTED_THROUGH_USB) &&
//					intent.getBooleanExtra(IOIO_CONNECTED_THROUGH_USB, false)) {
//				//SPLog.debug("ON FIRST START, calling onStart()");
//				super.onStart(intent, startId); //keep this here? or put outside?
//			}

            super.onStart(intent, startId); //keep this here? or put outside?

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
            builder.setNumber(1);
            builder.setContentTitle(getResources().getString(R.string.steampunk_notification_title));
            builder.setContentText(getResources().getString(R.string.steampunk_notification_message));
            Notification n = builder.build();
            startForeground(1, n);
        } //else {

        if (intent == null) return;
//		if (intent.getAction() == null) return;

        Bundle extras = intent.getExtras();

        if (mInManualMode && !intent.getAction().equals(MANUAL_MODE_COMMAND_INTENT)) {
            return;
        }
        if (intent.getAction().equals(START_SPIOIO_SERVICE_INTENT)) {
            if (extras != null && extras.getBoolean(SET_CRUCIBLE_COUNT)) {
                int oldCount = mCrucibleCount;
                mCrucibleCount = extras.getInt(CRUCIBLE_COUNT);
                mCountHasBeenSet = true;

//				if (extras.containsKey(IOIO_CONNECTED_THROUGH_USB) &&
//						extras.getBoolean(IOIO_CONNECTED_THROUGH_USB, false)) {
//					//SPLog.debug("IN SECOND START, SET CRUCIBLE COUNT, calling onStart()");
//					super.onStart(intent, startId); //keep this here? or put outside?
//				}

                //need to disable any extra crucibles...
//				boolean successfullyUpdatedCrucibleArray = true;
                if (mCrucibles != null) {
                    for (int i = mCrucibleCount; i < oldCount; i++) {
                        //					try {
                        mCrucibles[i].stopRunning();
                        //					} catch (Exception e) {
                        //						successfullyUpdatedCrucibleArray = false;
                        //							SPLog.debug("<<<<<<<<<<< *********** mCrucibleCount: " + mCrucibleCount + " oldCount: " + oldCount + " *********** >>>>>>>>>");
                        //							SPLog.debug("<<<<<<<<<<< *********** mCrucibles: " + mCrucibles + " *********** >>>>>>>>>");
                        //							SPLog.debug("<<<<<<<<<<< *********** curr crucible: " + mCrucibles[i] + " *********** >>>>>>>>>");
                        //							SPLog.debug("<<<<<<<<<<< *********** ERROR STOPPING CRUCIBLE: " + i + " *********** >>>>>>>>>");
                        //					}
                    }
//					if (!successfullyUpdatedCrucibleArray) {
//						(new Exception("FAILED TO UPDATE CRUCIBLE COUNT!")).printStackTrace();
//					}
                    for (int i = oldCount; i < mCrucibleCount; i++) {
                        try {
                            mCrucibles[i] = new SPServiceCrucible(
                                    ioio.openDigitalInput(FLOW_METER_PIN[i]),
                                    ioio.openAnalogInput(CRUCIBLE_TEMP_PIN[i]),
                                    ioio.openDigitalOutput(FILL_SOLENOID_PIN[i]),
                                    ioio.openDigitalOutput(STEAM_SOLENOID_PIN[i]),
                                    ioio.openDigitalOutput(PULL_DOWN_ACTUATOR_PIN[i]),
                                    this,
                                    i);
                        } catch (Exception e) {

                        }
                    }
                    oldCount = mCrucibleCount;
                }
            } else if (intent.hasExtra(IOIO_CONNECTED_THROUGH_USB) &&
                    intent.getBooleanExtra(IOIO_CONNECTED_THROUGH_USB, false)) { //ENTRY POINT FOR STARTING IOIO CONNECTION
                //SPLog.debug("ON SECOND START, calling onStart()");
//				super.onStart(intent, startId); //keep this here? or put outside?
            }
        } else if (intent.getAction().equals(CRUCIBLE_COMMAND_INTENT)) {
            int index = extras.getInt(CRUCIBLE);
            String cmd = extras.getString(COMMAND);
            if (cmd.equals(START_BREW_PROCESS)) {
                double volume = extras.getDouble(VOLUME);
                double targetTemp = extras.getDouble(TEMPERATURE);
                double drainTime = extras.getDouble(DRAIN_TIME);
                try {
                    mCrucibles[index].startBrewProcess(volume, targetTemp, drainTime);
                } catch (Exception e) {
                    //SPLog.debug("<<<<<<<<<<< *********** ERROR STARTING BREW ON CRUCIBLE: " + index + " *********** >>>>>>>>>");
                    e.printStackTrace();
                }
            } else if (cmd.equals(COMMENCE_BREW_PROCESS)) {
                double[] agitationsAndSteeps = extras.getDoubleArray(AGITATIONS);
                double[] pulseWidths = extras.getDoubleArray(PULSE_WIDTHS);
                double vacuumBreak = extras.getDouble(VACUUM_BREAK);
                double drainTime = extras.getDouble(DRAIN_TIME);
                mCrucibles[index].commenceBrewProcess(agitationsAndSteeps, pulseWidths, vacuumBreak, drainTime);
            } else if (cmd.equals(START_RINSE_CYCLE)) {
                double drainTime = extras.getDouble(DRAIN_TIME);
                mCrucibles[index].startRinseCycle(mRinseVolume, mRinseTemp, drainTime);
            } else if (cmd.equals(CANCEL_PROCESS)) {
                if (mCrucibles != null) mCrucibles[index].cancelCycle();
            } else if (cmd.equals(READ_CRUCIBLE_STATE)) {
                sendCrucibleState(index);
            } else if (cmd.equals(START_FORCE_STEAM)) {
                mCrucibles[index].forceSteamOn();
            } else if (cmd.equals(STOP_FORCE_STEAM)) {
                mCrucibles[index].stopForceSteamOn();
            } else if (cmd.equals(START_FORCE_FILL)) {
                if (mCrucibles != null && mCrucibles[index] != null) mCrucibles[index].forceFillOn();
            } else if (cmd.equals(STOP_FORCE_FILL)) {
                if (mCrucibles != null && mCrucibles[index] != null) mCrucibles[index].stopForceFillOn();
            } else if (cmd.equals(START_CLEANING_CYCLE)) {
                double waterVolume = extras.getDouble(VOLUME);
                double temperature = extras.getDouble(TEMPERATURE);
                int cycles = extras.getInt(CYCLES);
                mCrucibles[index].startCleanCycle(waterVolume, temperature, cycles);
            } else if (cmd.equals(FINISH_CLEANING_CYCLE)) {
                double waterVolume = extras.getDouble(VOLUME);
                double temperature = extras.getDouble(TEMPERATURE);
                int cycles = extras.getInt(CYCLES);
                mCrucibles[index].finishCleanCycle(waterVolume, temperature, cycles);
            } else if (cmd.equals(RESET_CRUCIBLE_ERROR_STATE)) {
                if (mCrucibles != null && mCrucibles.length > index) mCrucibles[index].resetErrorState();
            } else if (cmd.equals(LOCK_CRUCIBLE)) {
                if (mCrucibles != null && mCrucibles.length > index) mCrucibles[index].lock();
            } else if (cmd.equals(UNLOCK_CRUCIBLE)) {
                if (mCrucibles != null && mCrucibles.length > index) mCrucibles[index].unlock();
            }
        } else if (intent.getAction().equals(MACHINE_COMMAND_INTENT)) {
            String cmd = extras.getString(COMMAND);
            if (cmd.equals(MACHINE_SETTINGS)) {
                double volume = intent.getExtras().getDouble(RINSE_VOLUME);
                double rinseTemp = intent.getExtras().getDouble(RINSE_TEMP);
                mRinseTemp = rinseTemp;
                mRinseVolume = volume;
                double boilerTemp = extras.getDouble(BOILER_TEMP);
                if (mBoiler != null)
                    mBoiler.setMaxTemperature(boilerTemp);
            } else if (cmd.equals(READ_MACHINE_SETTINGS)) {
                Intent stateIntent = new Intent(SPServiceReceiver.MACHINE_SETTINGS_STATUS);
                stateIntent.putExtra(SPServiceReceiver.BOILER_TEMP, mBoiler.getMaxTemperature());
                stateIntent.putExtra(SPServiceReceiver.RINSE_TEMP, mRinseTemp);
                stateIntent.putExtra(SPServiceReceiver.RINSE_VOLUME, Math.ceil(mRinseVolume));
//				sendOrderedBroadcast(stateIntent, SPServiceReceiver.STEAM_PUNK_CONTROL_PERMISSION);
                doBroadcast(stateIntent, SPServiceReceiver.STEAM_PUNK_CONTROL_PERMISSION);
            } else if (cmd.equals(READ_BOILER_STATE)) {
                sendBoilerState();
            } else if (cmd.equals(START_FORCE_BOILER_HEAT_OFF)) {
                mBoiler.forceHeatOff();
            } else if (cmd.equals(STOP_FORCE_BOILER_HEAT_OFF)) {
                mBoiler.stopForceHeatOff();
            } else if (cmd.equals(START_FORCE_BOILER_HEAT_ON)) {
                mBoiler.forceHeatOn();
            } else if (cmd.equals(STOP_FORCE_BOILER_HEAT_ON)) {
                mBoiler.stopForceHeatOn();
            } else if (cmd.equals(RELEASE_STEAM)) {
                mBoiler.releaseExcessSteam();
            }
        } else if (intent.getAction().equals(MANUAL_MODE_COMMAND_INTENT)) {
            String cmd = (String) extras.get(COMMAND);
            if (cmd.equals(START_MANUAL_MODE)) {
                mInManualMode = true;
                if (mCrucibles != null) {
                    for (int i = 0; i < mCrucibleCount; i++) {
                        mCrucibles[i].setInManualMode(true);
                    }
                    mBoiler.setInManualMode(true);
                }
            } else if (cmd.equals(STOP_MANUAL_MODE)) {
                mInManualMode = false;
                if (mCrucibles != null) {
                    for (int i = 0; i < mCrucibleCount; i++) {
                        mCrucibles[i].setInManualMode(false);
                    }
                    mBoiler.setInManualMode(false);
                }
            } else if (cmd.equals(START_FORCE_FILL)) {
                int index = extras.getInt(CRUCIBLE);
                mCrucibles[index].forceFillOn();
                sendCrucibleState(index);
            } else if (cmd.equals(STOP_FORCE_FILL)) {
                int index = extras.getInt(CRUCIBLE);
                mCrucibles[index].stopForceFillOn();
                sendCrucibleState(index);
            } else if (cmd.equals(START_FORCE_STEAM)) {
                int index = extras.getInt(CRUCIBLE);
                mCrucibles[index].forceSteamOn();
                sendCrucibleState(index);
            } else if (cmd.equals(STOP_FORCE_STEAM)) {
                int index = extras.getInt(CRUCIBLE);
                mCrucibles[index].stopForceSteamOn();
                sendCrucibleState(index);
            } else if (cmd.equals(FORCE_DRAIN_OPEN)) {
                int index = extras.getInt(CRUCIBLE);
                mCrucibles[index].forceDrainOpen();
                sendCrucibleState(index);
            } else if (cmd.equals(STOP_FORCE_DRAIN_OPEN)) {
                int index = extras.getInt(CRUCIBLE);
                mCrucibles[index].stopForceDrainOpen();
                sendCrucibleState(index);
            } else if (cmd.equals(START_FORCE_BOILER_HEAT_ON)) {
                mBoiler.forceHeatOn();
                sendBoilerState();
            } else if (cmd.equals(START_FORCE_BOILER_HEAT_OFF)) {
                mBoiler.forceHeatOff();
                sendBoilerState();
            }
        }
//		}
        mAlreadyStarted = true;
    }

    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    public double getTempForCrucible(int index) {
        return 0.0;
    }

    public boolean isAgitatingOnCrucible(int index) {
        return false;
    }

    @Override
    public void onDestroy() {
        mConnected = false;
        Intent ni = new Intent(SPServiceReceiver.STEAMPUNK_ERROR_BROADCAST);
        ni.putExtra(SPServiceReceiver.ERROR_MESSAGE, "serviceDestroyed");
//		sendOrderedBroadcast(ni, SPServiceReceiver.STEAM_PUNK_CONTROL_PERMISSION);
        doBroadcast(ni, SPServiceReceiver.STEAM_PUNK_CONTROL_PERMISSION);

        if (mAlreadyStarted && mBoiler != null) {
            mBoiler.stopRunning();
            for (int i = 0; i < mCrucibleCount; i++) {
                mCrucibles[i].stopRunning();
            }
            mAlreadyStarted = false;
        }
        super.onDestroy();
    }

    public void sendCrucibleState(int index) { //can UID be removed after testing?
        sendCrucibleState(index, new UID());
    }

    public void sendCrucibleState(int index, UID id) { //SPLog.debug("sending crucible state");
        if (mCountHasBeenSet && (index >= mCrucibleCount)) {
            return; //nothing to report if the crucible doesn't actually exist
        }

        Intent stateIntent = new Intent(SPServiceReceiver.CRUCIBLE_STATUS);
        stateIntent.putExtra(SPServiceReceiver.CRUCIBLE, index);
        stateIntent.putExtra(SPServiceReceiver.FILL, mCrucibles[index].isFilling());
        stateIntent.putExtra(SPServiceReceiver.STEAM, mCrucibles[index].isSteaming());
        stateIntent.putExtra(SPServiceReceiver.DRAIN, mCrucibles[index].isDraining());
        stateIntent.putExtra(SPServiceReceiver.TEMPERATURE, mCrucibles[index].getTemperature());
        stateIntent.putExtra(SPServiceReceiver.EDGES, mCrucibles[index].getEdgeCount());
        stateIntent.putExtra(SPServiceReceiver.VOLUME, mCrucibles[index].getVolume());
        stateIntent.putExtra(SPServiceReceiver.STATE, mCrucibles[index].getCrucibleState());
        stateIntent.putExtra(SPServiceReceiver.TOO_MUCH_FLOW, mCrucibles[index].hasFlowedTooMuch());
        stateIntent.putExtra(SPServiceReceiver.TOO_MUCH_STEAM, mCrucibles[index].hasSteamedTooMuch());
        stateIntent.putExtra(SPServiceReceiver.NOT_ENOUGH_FLOW, mCrucibles[index].notEnoughFlow());
        stateIntent.putExtra(SPServiceReceiver.NOT_ENOUGH_STEAM, mCrucibles[index].notEnoughSteam());
        int time = mCrucibles[index].getTimeLeftInBrew();
        if (mCrucibles[index].getCrucibleState() == SPServiceCrucibleState.FINISH_BEVERAGE_PULL_DOWN) {
            time = mCrucibles[index].getTimeLeftInCycle();
        }
        stateIntent.putExtra(SPServiceReceiver.TIME_LEFT_IN_BREW, time);
        stateIntent.putExtra(SPServiceReceiver.UID, id.toString());
//		sendOrderedBroadcast(stateIntent, SPServiceReceiver.STEAM_PUNK_CONTROL_PERMISSION);
        doBroadcast(stateIntent, SPServiceReceiver.STEAM_PUNK_CONTROL_PERMISSION);
    }

    public void sendBoilerState() { //SPLog.debug("sending boiler state");
        UID id = new UID();
        boolean heating = mBoiler.isHeating();
        String err = "none";
        boolean filling = false;
        try {
            filling = mBoiler.isFilling();
        } catch (Exception e) {
            err = "could not get fill status";
        }
        double temperature = mBoiler.getTemperature(); //SPLog.debug("temp of boiler: " + SPServiceThermistor.convertFromTempToTemp(SPTempUnitType.KELVIN, SPTempUnitType.FAHRENHEIT, temperature));
        boolean isRunning = mBoiler.isRunning();

        Intent stateIntent = new Intent(SPServiceReceiver.BOILER_STATUS);
        stateIntent.putExtra(SPServiceReceiver.HEAT, heating);
        stateIntent.putExtra(SPServiceReceiver.FILL, filling);
        stateIntent.putExtra(SPServiceReceiver.TEMPERATURE, temperature);
        stateIntent.putExtra(SPServiceReceiver.RUNNING, isRunning);
        stateIntent.putExtra(SPServiceReceiver.ERROR_MESSAGE, err);
        stateIntent.putExtra(SPServiceReceiver.UID, id.toString());
        SPLog.debug("sending broadcast...");
//		sendOrderedBroadcast(stateIntent, SPServiceReceiver.STEAM_PUNK_CONTROL_PERMISSION);
        doBroadcast(stateIntent, SPServiceReceiver.STEAM_PUNK_CONTROL_PERMISSION);
    }

    private void sendConnectionNotification() { //SPLog.debug("sending notification in service");
        Intent intent;
        if (mConnected) {
            intent = new Intent(SPServiceReceiver.IOIO_CONNECTED_BROADCAST);
//			sendOrderedBroadcast(intent, SPServiceReceiver.STEAM_PUNK_CONTROL_PERMISSION);
            doBroadcast(intent, SPServiceReceiver.STEAM_PUNK_CONTROL_PERMISSION);
        }
    }

    public void sendSteamedTooMuchForVolume(int crucibleIndex) {
        Intent intent = new Intent(SPServiceReceiver.STEAMPUNK_ERROR_BROADCAST);
        intent.putExtra(SPServiceReceiver.STEAMED_TOO_MUCH_FOR_VOLUME, true);
        intent.putExtra(CRUCIBLE, crucibleIndex);
//		sendOrderedBroadcast(intent, SPServiceReceiver.STEAM_PUNK_CONTROL_PERMISSION);
        doBroadcast(intent, SPServiceReceiver.STEAM_PUNK_CONTROL_PERMISSION);
    }

    public void sendReleaseSteam() { //SPLog.debug("got release steam call, temp is: " + SPServiceThermistor.convertFromTempToTemp(SPTempUnitType.KELVIN, SPTempUnitType.FAHRENHEIT, mBoiler.getTemperature()));
        Intent intent = new Intent(SPServiceReceiver.RELEASE_STEAM_BROADCAST);
//		sendOrderedBroadcast(intent, SPServiceReceiver.STEAM_PUNK_CONTROL_PERMISSION);
        doBroadcast(intent, SPServiceReceiver.STEAM_PUNK_CONTROL_PERMISSION);
    }

    public void cancelAllBrews() {
        for (int i = 0; i < mCrucibleCount; i++) {
            mCrucibles[i].cancelCycle();
        }
    }

    public void steamOnAllCrucibles() {
        for (int i = 0; i < mCrucibleCount; i++) {
            mCrucibles[i].forceSteamOn();
        }
    }

    public void stopSteamOnAllCrucibles() {
        for (int i = 0; i < mCrucibleCount; i++) {
            mCrucibles[i].stopForceSteamOn();
        }
    }

    public void doBroadcast(Intent intent, String permissions) {
        SPServiceReceiver receiver = new SPServiceReceiver();
        receiver.onReceive(SPModel.getContext(), intent);
    }
}
