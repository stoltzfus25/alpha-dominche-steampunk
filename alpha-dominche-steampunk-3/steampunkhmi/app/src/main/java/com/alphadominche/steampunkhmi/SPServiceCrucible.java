package com.alphadominche.steampunkhmi;

import java.util.Date;
import java.util.Observable;
import java.util.Observer;
import java.util.Timer;
import java.util.TimerTask;

import ioio.lib.api.AnalogInput;
import ioio.lib.api.DigitalInput;
import ioio.lib.api.DigitalOutput;
import ioio.lib.api.exception.ConnectionLostException;


public class SPServiceCrucible extends Thread implements Observer {
    public static final double SECONDS_TO_PUSH_UP_WATER = 7.0; //9.0;
    public static final double SECONDS_TO_PUSH_UP_WATER_FOR_CLEANING = 5.0;
    public static final double BREW_WATER_OVER_TEMP_PUSH_UP_TIME = 2.0;
    public static final double SECONDS_TO_OVERLAP_STEAM_WITH_DRAIN = 0.333333333;
    public static final double CLEAN_SOAK_TIME = 20.0;
    public static final double CLEAN_DRAIN_TIME = 30.0;
    public static final double CLEAN_RINSE_DRAIN_TIME = 30.0;
    public static final double CLEAN_RINSE_FINAL_DRAIN_TIME = 30.0;
    public static final double CLEAN_AGITATION_TIME = 10.0;
    public static final double CLEAN_RINSE_AGITATION_TIME = 10.0;
    public static final double AGITATION_SPURT_LENGTH = 0.5;
    public static final int EDGE_COUNT_TOLERANCE = 3;
    public static final double MAX_ACCEPTABLE_TEMP = 380.0; //Kelvins
    public static final double MIN_ACCEPTABLE_TEMP_DELTA = 1.0;
    public static final double ACCEPTABLE_TEMP_DELTA_TIME_LIMIT = 0.75;
    public static final int WAIT_SECONDS_PER_EXTRA_STEAM_PULSE = 10;
    public static final double CLEAN_VACUUM_BREAK_TIME = 2.0;
    public static final double A_BILLION = 1000000000.0;
    public static final long DELAY_BEFORE_FLOW_ERROR_CHECKING = 5000L;
    public static final double SECONDS_NOT_STEAMING_WAITING_FOR_PISTON = 3.0;
    public static final double SECONDS_STEAMING_WAITING_FOR_PISTON = 1.0;

    public static final double MAX_SECONDS_BEFORE_FIRST_FILL_EDGE = 1.0;

    public static final double TEMP_REACHED_EARLY_TOLERANCE = SPServiceThermistor.convertFromTempToTemp(SPTempUnitType.FAHRENHEIT, SPTempUnitType.KELVIN, 5.0);

    public static final double INITIAL_DOSE = 0.7; //0.8; //the amount of water relative to the total beverage volume to put in at the start of the fill and heat cycle
    public static final double SECONDS_PER_DEGREE_OF_HEATING = 1.32; //+10 stands for the five seconds of push up time and an approximation of the steam added in agitating
    private DigitalOutput mFillSignal;
    private DigitalOutput mSteamSignal;
    private DigitalOutput mPullDownActuator;
    private SPServiceThermistor mThermistor;
    private SPFlowMeter mFlowMeter;
    private double mTargetTemp;
    private int mFillTarget;
    private double mTargetVolume; //total beverage volume in mL
    private double mCurrentVolume;
    private double mVolumeLeftToAdd;
    private boolean mAtOrCloseToTargetTempBeforeHeating;
    private double mDrainTime;
    private SPIOIOService mService;
    private double mBeverageVacuumBreakTime;

    private boolean mRunning;

    private SPServiceCrucibleState mState;
    private boolean mForceFill;
    private boolean mForceSteam;
    private boolean mForceDrainOpen;

    private boolean mIsFilling; //for fill signal
    private boolean mIsSteaming; //for steam signal
    private boolean mIsDraining; //for pull down actuator

    private double mLastValidTemperature;
    private int mTempReadings;
    private boolean mTooMuchFlow;
    private boolean mTooMuchSteam;
    private boolean mNotEnoughFlow;
    private boolean mNotEnoughSteam;
    private double mProcessStartTemp;
    private boolean mReadyToCheckForTooMuchFlow;

    private double[] mAgitationAndSteepLengths;
    private double[] mPulseWidths;
    private double mCurrPulseWidth;
    private int mCurrentAgitation; //index into agitation
    private double mTimeRequiredForProcess; //in seconds, used for anything requiring a delay
    private long mStartTime; //in nanoseconds
    private long mLastTime; //in nanoseconds
    private long mEndTimeForFirstDrain; //in nanoseconds
    private int mIndex; //the number of this crucible

    private long mBrewStartTime;

    private int mCleaningCycleCount;
    private int mTotalCleaningCycles;

    private Timer mUpdateSchedule;

    private boolean mInManualMode;

    private boolean mLocked;

    private static ColdWaterPressureLowListener mColdWaterPressureLowListener;

    SPServiceCrucible(DigitalInput flowMeterSignal, AnalogInput thermistorSignal, DigitalOutput fillSignal, DigitalOutput steamSignal, DigitalOutput pullDownActuator, SPIOIOService service, int index) {
        super();
        mInManualMode = false;
        mService = service;
        mRunning = true;
        mFillTarget = 0;
        mTargetVolume = 0.0;
        mCurrentVolume = 0.0;
        mVolumeLeftToAdd = 0.0;
        mAtOrCloseToTargetTempBeforeHeating = false;
        mFillSignal = fillSignal;
        mTargetTemp = 0.0;
        mSteamSignal = steamSignal;
        mPullDownActuator = pullDownActuator;

        try {
            mFillSignal.write(false);
            mSteamSignal.write(false);
            mPullDownActuator.write(false);
        } catch (Exception e) {

        }

        mThermistor = new SPServiceThermistor(thermistorSignal, SPTempUnitType.KELVIN, service);
        mThermistor.addObserver(this);
        mFlowMeter = new SPFlowMeter(flowMeterSignal);
        mFlowMeter.addObserver(this);
        mIsFilling = false;
        mIsSteaming = false;
        mIsDraining = false;
        mDrainTime = 0.0;

        mState = SPServiceCrucibleState.EMPTY_START;
        mForceFill = false;
        mForceSteam = false;
        mForceDrainOpen = false;
        mCurrentAgitation = -1; //no agitation currently being done
        mEndTimeForFirstDrain = mStartTime = mLastTime = mBrewStartTime = System.nanoTime();
        mAgitationAndSteepLengths = new double[0];
        mPulseWidths = new double[0];
        mBeverageVacuumBreakTime = 0.0;
        mIndex = index;

        mTooMuchFlow = false;
        mLastValidTemperature = 0.0;
        mTempReadings = 0;
        mTooMuchSteam = false;
        mNotEnoughFlow = false;
        mNotEnoughSteam = false;
        mProcessStartTemp = 0.0;
        mReadyToCheckForTooMuchFlow = false;

        //this is because the flow meter was getting edges from floating inputs...can probably change the delay to 1 second for production
        Timer initialFlowMeterResetTimer = new Timer();
        initialFlowMeterResetTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                mFlowMeter.resetEdgeCount();
                mReadyToCheckForTooMuchFlow = true;
            }
        }, DELAY_BEFORE_FLOW_ERROR_CHECKING);

        mLocked = false;

        mUpdateSchedule = null;
    }

    @Override
    public void update(Observable obj, Object data) {
        // count has been updated or the thread has been stopped
        if (obj == mFlowMeter) {
            if (mReadyToCheckForTooMuchFlow && !mTooMuchFlow && mFlowMeter.isOverflowing()) {
//				mTooMuchFlow = true;
//				cancelCycle();
//				SPLog.send(mService, -1, mIndex, SPLog.ERROR, SPLog.MACHINE, "overflow");
//				mService.sendCrucibleState(mIndex);
            } else if (mReadyToCheckForTooMuchFlow && !mNotEnoughFlow && mFlowMeter.isUnderflowing()) {
//				mNotEnoughFlow = true;
//				cancelCycle();
//				SPLog.send(mService, -1, mIndex, SPLog.ERROR, SPLog.MACHINE, "underflow");
//				mService.sendCrucibleState(mIndex);
            }
        } else if (obj == mThermistor) {
            if (++mTempReadings > 20) {
                if (isSteaming()) {
                    mLastValidTemperature = mThermistor.getTemperature();
                }
                if ((mThermistor.getTemperature() - mLastValidTemperature > 10.0) || (mThermistor.getTemperature() > MAX_ACCEPTABLE_TEMP)) {
//					mTooMuchSteam = true;
//					mService.sendCrucibleState(mIndex, new UID());
                }
            } else {
                mLastValidTemperature = mThermistor.getTemperature();
            }
        } else if (!mFlowMeter.stillRunning()) {
            stopRunning();
        }
    }

    public void setFillTargetWithOunces(double ounces) {
        if (isLocked()) return;

        if (mTooMuchFlow || mTooMuchSteam || mNotEnoughFlow || mNotEnoughSteam) {
            //send a message saying that the crucible is in an error state
            return;
        }
        mFillTarget = (int) SPFlowMeter.convertFromOuncesToEdges(ounces);
    }

    public void setFillTargetWithMilliliters(double milliliters) {
        if (isLocked()) return;

        if (mTooMuchFlow || mTooMuchSteam || mNotEnoughFlow || mNotEnoughSteam) {
            //send a message saying that the crucible is in an error state
            return;
        }
        mFillTarget = (int) SPFlowMeter.convertFromMillilitersToEdges(milliliters);
    }

    public void heatToDegreesFarenheit(double targetTemp) {
        if (isLocked()) return;

        if (mTooMuchFlow || mTooMuchSteam || mNotEnoughFlow || mNotEnoughSteam) {
            //send a message saying that the crucible is in an error state
            return;
        }
        heatToKelvin(SPServiceThermistor.convertFromTempToTemp(SPTempUnitType.FAHRENHEIT, SPTempUnitType.KELVIN, targetTemp));
    }

    public void heatToDegreesCelcius(double targetTemp) {
        if (isLocked()) return;

        if (mTooMuchFlow || mTooMuchSteam || mNotEnoughFlow || mNotEnoughSteam) {
            //send a message saying that the crucible is in an error state
            return;
        }
        heatToKelvin(SPServiceThermistor.convertFromTempToTemp(SPTempUnitType.CELCIUS, SPTempUnitType.KELVIN, targetTemp));
    }

    public void heatToKelvin(double targetTemp) {
        if (isLocked()) return;

        mTargetTemp = targetTemp;
    }

    public void stopRunning() {
        mRunning = false;
        if (mThermistor.stillRunning()) mThermistor.stopRunning();
        if (mFlowMeter.stillRunning()) mFlowMeter.stop();
        if (mUpdateSchedule != null) mUpdateSchedule.cancel();
    }

    public void startBrewProcess(double volume, double targetTemp, double drainTime) { //always expect metric units
        if (isLocked()) return;

        if (mTooMuchFlow || mTooMuchSteam || mNotEnoughFlow || mNotEnoughSteam) {
            //send a message saying that the crucible is in an error state
            System.out.println("cancel startBrewProcess command: tooMuchFlow: " + mTooMuchFlow + " tooMuchSteam: " + mTooMuchSteam + " notEnoughFlow: " + mNotEnoughFlow + " notEnoughSteam: " + mNotEnoughSteam);
            return;
        }
        if (mState != SPServiceCrucibleState.EMPTY_START) {
            //this is a failure state
        }

        mCurrentAgitation = 0;
        mTargetVolume = volume; //note the total volume so steam compensation can take place
        setFillTargetWithMilliliters(volume * INITIAL_DOSE); //partially fill at first
        mFlowMeter.resetEdgeCount();
        heatToKelvin(targetTemp);
        mDrainTime = drainTime;
        mState = SPServiceCrucibleState.FILLING_FOR_BREW;
        mStartTime = mLastTime = System.nanoTime();
        stopForceFillOn();

        startSchedulingStateUpdates();
    }

    public void commenceBrewProcess(double[] agitationsAndSteeps, double[] pulseWidths, double vacuumBreak, double drainTime) {
        if (isLocked()) return;

        if (mTooMuchFlow || mTooMuchSteam || mNotEnoughFlow || mNotEnoughSteam) {
            //send a message saying that the crucible is in an error state
            return;
        }
        if (mState != SPServiceCrucibleState.WAITING_FOR_BREW_PISTON_INSERTION && mState != SPServiceCrucibleState.WAITING_TO_DISPENSE_AND_RINSE) {
            //this is a failure state
        }

        mBeverageVacuumBreakTime = vacuumBreak;
        mDrainTime = drainTime;
        mAgitationAndSteepLengths = agitationsAndSteeps;
        mPulseWidths = pulseWidths;
        mTimeRequiredForProcess = mAgitationAndSteepLengths[0];
        mCurrPulseWidth = mPulseWidths[0];
        mState = SPServiceCrucibleState.AGITATING;
        try {
            setDraining(false);
        } catch (Exception e) {

        }
        mFlowMeter.resetEdgeCount();
        mStartTime = mLastTime = mBrewStartTime = System.nanoTime();
        mProcessStartTemp = mThermistor.getTemperature();

        startSchedulingStateUpdates();
    }

    public void startRinseCycle(double volume, double targetTemp, double drainTime) {
        if (isLocked()) return;

        if (mTooMuchFlow || mTooMuchSteam || mNotEnoughFlow || mNotEnoughSteam) {
            //send a message saying that the crucible is in an error state
            return;
        }
        if (mState != SPServiceCrucibleState.WAITING_TO_DISPENSE_AND_RINSE) {
            //this is a failure state
        }

        setFillTargetWithMilliliters(volume);
        heatToKelvin(targetTemp);
        mDrainTime = drainTime;
        mFlowMeter.resetEdgeCount();
        mState = SPServiceCrucibleState.FILLING_FOR_RINSE;
        mStartTime = mLastTime = System.nanoTime();

        startSchedulingStateUpdates();
    }

    public void startCleanCycle(double volume, double temperature, int cleaningCycles) {
        if (isLocked()) return;

        if (mTooMuchFlow || mTooMuchSteam || mNotEnoughFlow || mNotEnoughSteam) {
            //send a message saying that the crucible is in an error state
            return;
        }
        if (mState != SPServiceCrucibleState.EMPTY_START) {
            //this is a failure state
        }

        mTotalCleaningCycles = cleaningCycles;
        mCleaningCycleCount = 0;
        setFillTargetWithMilliliters(volume);
        mTargetTemp = temperature;
        mFlowMeter.resetEdgeCount();
        mState = SPServiceCrucibleState.FILL_FOR_CLEAN;

        startSchedulingStateUpdates();
    }

    public void finishCleanCycle(double volume, double temperature, int rinseCycles) {
        if (isLocked()) return;

        if (mTooMuchFlow || mTooMuchSteam || mNotEnoughFlow || mNotEnoughSteam) {
            //send a message saying that the crucible is in an error state
            return;
        }
        if (mState != SPServiceCrucibleState.WAIT_FOR_CLEAN_RINSE) {
            //this is a failure state
        }

        mTotalCleaningCycles = rinseCycles;
        mCleaningCycleCount = 0;
        setFillTargetWithMilliliters(volume);
        mTargetTemp = temperature;
        mFlowMeter.resetEdgeCount();
        mState = SPServiceCrucibleState.FILL_FOR_CLEAN_RINSE;

        startSchedulingStateUpdates();
    }

    public void cancelCycle() {
        stopSchedulingStateUpdates();
        mState = SPServiceCrucibleState.EMPTY_START;
        mForceFill = false;
        mForceSteam = false;
        mForceDrainOpen = false;
        mFlowMeter.resetEdgeCount();
        try {
            setFilling(false);
            setSteaming(false);
            setDraining(false);
        } catch (Exception e) {
            //everything should stop automatically because the IOIO connection should be dead
        }
        mService.sendCrucibleState(mIndex);
    }

    public void startCrucibleTest() {
        if (isLocked()) return;


    }

    public void resetErrorState() {
        mTooMuchFlow = false;
        mTooMuchSteam = false;
        mNotEnoughSteam = false;
        mNotEnoughFlow = false;
        mFlowMeter.resetEdgeCount();
        mTempReadings = 0;
        mLastValidTemperature = mThermistor.getTemperature();
        mService.sendCrucibleState(mIndex);
    }

    public void forceFillOn() {
        if (isLocked()) return;

        mForceFill = true;
        try {
            setFilling(mIsFilling);
        } catch (Exception e) {

        }
    }

    public void stopForceFillOn() {
        if (isLocked()) return;

        mForceFill = false;
        try {
            setFilling(mIsFilling);
        } catch (Exception e) {

        }
    }

    public void forceSteamOn() {
        if (isLocked()) return;

        mForceSteam = true;
        try {
            setSteaming(mIsSteaming);
        } catch (Exception e) {

        }
    }

    public void stopForceSteamOn() {
        if (isLocked()) return;

        mForceSteam = false;
        try {
            setSteaming(mIsSteaming);
        } catch (Exception e) {

        }
    }

    public void forceDrainOpen() {
        if (isLocked()) return;

        mForceDrainOpen = true;
        try {
            setDraining(mIsDraining);
        } catch (Exception e) {

        }
    }

    public void stopForceDrainOpen() {
        if (isLocked()) return;

        mForceDrainOpen = false;
        try {
            setDraining(mIsDraining);
        } catch (Exception e) {

        }
    }

    public void run() {
        while (mRunning) {
            try {
                if (mInManualMode) {
                    //preempt any state machine behavior
                } else if (mState == SPServiceCrucibleState.EMPTY_START) {
                    //just sort of hang out...be cool, hang loose. grab some poi and kalua pig. there's enough for everyone.
                } else if (mState == SPServiceCrucibleState.FILLING_FOR_BREW) {
                    mLastTime = System.nanoTime();
                    double timeIntoProcess = (mLastTime - mStartTime) / A_BILLION;

                    //make sure the fill signal is on
                    if (!mIsFilling && mFlowMeter.getEdgeCount() < mFillTarget) {
                        setFilling(true);
                    }

                    if (isFilling() && mFlowMeter.getEdgeCount() == 0 && timeIntoProcess > MAX_SECONDS_BEFORE_FIRST_FILL_EDGE) {
//						mNotEnoughFlow = true;
//						mService.sendCrucibleState(mIndex);
                    }

                    //change state if full
                    if (mFlowMeter.getEdgeCount() >= mFillTarget) {
                        mReadyToCheckForTooMuchFlow = true;
                        mCurrentVolume = SPFlowMeter.convertEdgesToMilliliters(mFlowMeter.getEdgeCount());
                        setFilling(false);
                        mState = SPServiceCrucibleState.PUSHING_BREW_WATER_TO_TOP;
                        mTimeRequiredForProcess = SECONDS_TO_PUSH_UP_WATER;
                        mStartTime = mLastTime = System.nanoTime();
                        mProcessStartTemp = mThermistor.getTemperature();
                    }
                } else if (mState == SPServiceCrucibleState.PUSHING_BREW_WATER_TO_TOP) {
                    mLastTime = System.nanoTime();
                    double timeIntoProcess = (mLastTime - mStartTime) / A_BILLION;
                    double currTemp = mThermistor.getTemperature();

                    //steam runs full on for the push up time!
                    if (!mIsSteaming && (timeIntoProcess < mTimeRequiredForProcess)) {
                        setSteaming(true);
                    }

                    if (currTemp - mProcessStartTemp < MIN_ACCEPTABLE_TEMP_DELTA && timeIntoProcess > ACCEPTABLE_TEMP_DELTA_TIME_LIMIT) {
//						mNotEnoughSteam = true;
//						mService.sendCrucibleState(mIndex);
                    }

                    //check for not enough steam...

                    //go to next state if done
                    if (timeIntoProcess >= mTimeRequiredForProcess) {
                        mStartTime = mLastTime = System.nanoTime();
                        mProcessStartTemp = mThermistor.getTemperature();

                        if (mProcessStartTemp >= mTargetTemp) {
                            //TODO End heat up cycle
                            if (mColdWaterPressureLowListener != null)
                                mColdWaterPressureLowListener.displayColdWaterPressureLowWarning();
                        }

                        //calculate how much water to add to compensate for steam...
                        double tempDiff = mTargetTemp - currTemp;
                        double timeLeftForHeating = tempDiff * SECONDS_PER_DEGREE_OF_HEATING + 10;
                        double steamWaterVolume = 1.225 * timeLeftForHeating + 2.45;
                        mVolumeLeftToAdd = mTargetVolume - steamWaterVolume - mCurrentVolume;
                        mFlowMeter.resetEdgeCount();
                        if (mThermistor.getTemperature() >= (mTargetTemp - TEMP_REACHED_EARLY_TOLERANCE)) {
                            mAtOrCloseToTargetTempBeforeHeating = true;
                        }
                        if (mVolumeLeftToAdd >= 0) {
                            setFillTargetWithMilliliters(mVolumeLeftToAdd);
                            setFilling(true);
                            mState = SPServiceCrucibleState.COMPENSATING_VOLUME_FOR_STEAM;
                        } else {
                            //send message to GUI to let the user know that too much water has been added
                            setFillTargetWithMilliliters(0);
                            mService.sendSteamedTooMuchForVolume(mIndex);
                            if (mAtOrCloseToTargetTempBeforeHeating) {
                                mState = SPServiceCrucibleState.MAKE_SURE_BREW_WATER_IS_IN_TOP;
                            } else {
                                mState = SPServiceCrucibleState.BREW_WATER_IN_TOP_AND_HEATING;
                            }
                        }
                    }
                } else if (mState == SPServiceCrucibleState.COMPENSATING_VOLUME_FOR_STEAM) {
                    if (!mIsFilling && mFlowMeter.getEdgeCount() < mFillTarget) {
                        setFilling(true);
                    }

                    if (mFlowMeter.getEdgeCount() >= mFillTarget) {
                        if (mIsFilling) {
                            setFilling(false);
                        }
                        if (mAtOrCloseToTargetTempBeforeHeating) {
                            mState = SPServiceCrucibleState.MAKE_SURE_BREW_WATER_IS_IN_TOP;
                            mTimeRequiredForProcess = BREW_WATER_OVER_TEMP_PUSH_UP_TIME;
                        } else {
                            mState = SPServiceCrucibleState.BREW_WATER_IN_TOP_AND_HEATING;
                        }
                        mStartTime = mLastTime = System.nanoTime();
                    }
                } else if (mState == SPServiceCrucibleState.MAKE_SURE_BREW_WATER_IS_IN_TOP) {
                    mLastTime = System.nanoTime();
                    double timeIntoProcess = (mLastTime - mStartTime) / A_BILLION;
                    double currTemp = mThermistor.getTemperature();

                    //steam runs full on for the push up time!
                    if (!mIsSteaming && (timeIntoProcess < mTimeRequiredForProcess)) {
                        setSteaming(true);
                    }

                    if (currTemp - mProcessStartTemp < MIN_ACCEPTABLE_TEMP_DELTA && timeIntoProcess > ACCEPTABLE_TEMP_DELTA_TIME_LIMIT) {
//						mNotEnoughSteam = true;
//						mService.sendCrucibleState(mIndex);
                    }

                    //go to next state if done
                    if (timeIntoProcess >= mTimeRequiredForProcess) {
                        mState = SPServiceCrucibleState.BREW_WATER_IN_TOP_AND_HEATING;
                        mStartTime = mLastTime = System.nanoTime();
                    }
                } else if (mState == SPServiceCrucibleState.BREW_WATER_IN_TOP_AND_HEATING) {
                    mLastTime = System.nanoTime();
                    double timeIntoProcess = (mLastTime - mStartTime) / A_BILLION;
                    double currTemp = mThermistor.getTemperature();

                    //steam runs full on for the heat up time!
                    if (!mIsSteaming && (mThermistor.getTemperature() < mTargetTemp)) {
                        setSteaming(true);
                    }

                    if (mIsSteaming && (mThermistor.getTemperature() >= mTargetTemp)) {
                        setSteaming(false);
                    }


                    if (currTemp - mProcessStartTemp < MIN_ACCEPTABLE_TEMP_DELTA && timeIntoProcess > ACCEPTABLE_TEMP_DELTA_TIME_LIMIT) {
//						mNotEnoughSteam = true;
//						mService.sendCrucibleState(mIndex);
                    }

                    //go to next state after hitting the right temperature
                    if (mThermistor.getTemperature() >= mTargetTemp) {
                        if (!mIsSteaming) {
                            setSteaming(true);
                        }
                        mState = SPServiceCrucibleState.START_BREW_DRAIN;
                        mStartTime = mLastTime = System.nanoTime();
                        mTimeRequiredForProcess = SECONDS_TO_OVERLAP_STEAM_WITH_DRAIN;
                    }
                } else if (mState == SPServiceCrucibleState.START_BREW_DRAIN) {
                    mLastTime = System.nanoTime();
                    double timeIntoProcess = (mLastTime - mStartTime) / A_BILLION;

                    //make sure the steam is on!
                    if (!mIsSteaming && timeIntoProcess <= mTimeRequiredForProcess) {
                        setSteaming(true);
                    }

                    //make sure drain actuator is open!
                    if (!mIsDraining) {
                        setDraining(true);
                    }

                    //finish draining if
                    if (timeIntoProcess >= mTimeRequiredForProcess) {
                        setSteaming(false);
                        mState = SPServiceCrucibleState.FINISH_BREW_DRAIN;
                        mTimeRequiredForProcess = mDrainTime;
                        mStartTime = mLastTime = System.nanoTime();
                    }
                } else if (mState == SPServiceCrucibleState.FINISH_BREW_DRAIN) {
                    mLastTime = System.nanoTime();
                    double timeIntoProcess = (mLastTime - mStartTime) / A_BILLION;

                    //make sure the drain actuator is open!
                    if (!mIsDraining && timeIntoProcess < mTimeRequiredForProcess) {
                        setDraining(true);
                    }

                    //wait for piston insertion if done draining
                    if (timeIntoProcess >= mTimeRequiredForProcess) {
                        setDraining(false);
                        mState = SPServiceCrucibleState.WAITING_FOR_BREW_PISTON_INSERTION;
                        mEndTimeForFirstDrain = mStartTime = mLastTime = System.nanoTime();
                    }
                } else if (mState == SPServiceCrucibleState.WAITING_FOR_BREW_PISTON_INSERTION) {
                    mLastTime = System.nanoTime();
                    double timeIntoProcess = (mLastTime - mStartTime) / A_BILLION;

                    if (!mIsDraining && ((timeIntoProcess % (SECONDS_NOT_STEAMING_WAITING_FOR_PISTON + SECONDS_STEAMING_WAITING_FOR_PISTON)) > SECONDS_NOT_STEAMING_WAITING_FOR_PISTON)) {
                        setDraining(true);
                    }
                    if (mIsDraining && ((timeIntoProcess % (SECONDS_NOT_STEAMING_WAITING_FOR_PISTON + SECONDS_STEAMING_WAITING_FOR_PISTON)) < SECONDS_NOT_STEAMING_WAITING_FOR_PISTON)) {
                        setDraining(false);
                    }
                } else if (mState == SPServiceCrucibleState.AGITATING) {
                    mLastTime = System.nanoTime();
                    double timeIntoProcess = (mLastTime - mStartTime) / A_BILLION;
                    double currTemp = mThermistor.getTemperature();

                    //SPLog.debug(">>>>> In AGITATING <<<<<   isSteaming: " + mIsSteaming + " timeIntoProcess: " + timeIntoProcess + " currTemp: " + currTemp);

                    if (mIsDraining) { //occasionally there have been times when the drain is stuck open after calling commence brew. this should stop that.
                        setDraining(false);
                    }

                    //steam runs for only 1/2 second at a time!
                    if (!mIsSteaming && timeIntoProcess - Math.floor(timeIntoProcess) < mCurrPulseWidth && (timeIntoProcess < mTimeRequiredForProcess)) {
                        setSteaming(true);
                    } else if (mIsSteaming && timeIntoProcess - Math.floor(timeIntoProcess) >= mCurrPulseWidth) {
                        setSteaming(false);
                    }

                    if (currTemp - mProcessStartTemp < MIN_ACCEPTABLE_TEMP_DELTA && timeIntoProcess > ACCEPTABLE_TEMP_DELTA_TIME_LIMIT) {
//						mNotEnoughSteam = true;
//						mService.sendCrucibleState(mIndex);
                    }

                    //steep between agitations
                    if (timeIntoProcess >= mTimeRequiredForProcess) {
                        setSteaming(false);
                        mState = SPServiceCrucibleState.STEEP_BETWEEN_AGITATIONS;
                        mCurrentAgitation++;
                        mTimeRequiredForProcess = mAgitationAndSteepLengths[mCurrentAgitation];
                        mStartTime = mLastTime = System.nanoTime();
                    }
                } else if (mState == SPServiceCrucibleState.STEEP_BETWEEN_AGITATIONS) {
                    mLastTime = System.nanoTime();
                    double timeIntoProcess = (mLastTime - mStartTime) / A_BILLION;

                    //SPLog.debug(">>>>> In STEEP <<<<<   isSteaming: " + mIsSteaming + " timeIntoProcess: " + timeIntoProcess + " currAg: " + mCurrentAgitation + " " + mAgitationAndSteepLengths[mCurrentAgitation]);

                    //wait between agitations
                    if (mIsSteaming) {
                        setSteaming(false);
                    }

                    //advance to vacuum break if currentAgitation >= agitationLengths.length or get ready for the next agitation
                    if (timeIntoProcess >= mTimeRequiredForProcess) {
                        mCurrentAgitation++;
                        if (mCurrentAgitation >= mAgitationAndSteepLengths.length) {
                            setSteaming(false);
                            mState = SPServiceCrucibleState.BEVERAGE_VACUUM_BREAK;
                            stopForceSteamOn(); //might not get the command if force steam on is held on transition
                            mStartTime = mLastTime = System.nanoTime();
                            mTimeRequiredForProcess = mBeverageVacuumBreakTime;
                        } else {
                            setSteaming(true);
                            mState = SPServiceCrucibleState.AGITATING;
                            mCurrPulseWidth = mPulseWidths[mCurrentAgitation];
                            mStartTime = mLastTime = System.nanoTime();
                            mTimeRequiredForProcess = mAgitationAndSteepLengths[mCurrentAgitation];
                        }
                    }
                } else if (mState == SPServiceCrucibleState.BEVERAGE_VACUUM_BREAK) {
                    mLastTime = System.nanoTime();
                    double timeIntoProcess = (mLastTime - mStartTime) / A_BILLION;

                    //SPLog.debug(">>>>> In VACUUM BREAK <<<<<   timeIntoProcess: " + timeIntoProcess + " isSteaming: " + mIsSteaming + " timeRequired: " + mTimeRequiredForProcess);

                    //make sure steam is on
                    if (!mIsSteaming) {
                        setSteaming(true);
                    }

                    if (timeIntoProcess >= mTimeRequiredForProcess) {
                        if (!mIsSteaming) {
                            setSteaming(true);
                        }

                        if (!mIsDraining) {
                            setDraining(true);
                        }

                        mStartTime = mLastTime = System.nanoTime();
                        if (mBeverageVacuumBreakTime == 0.0) {
                            if (mIsSteaming) {
                                setSteaming(false);
                            }
                            mStartTime = mLastTime = System.nanoTime();
                            mState = SPServiceCrucibleState.FINISH_BEVERAGE_PULL_DOWN;
                            mTimeRequiredForProcess = mDrainTime;
                        } else {
                            mState = SPServiceCrucibleState.START_BEVERAGE_PULL_DOWN;
                            stopForceSteamOn(); //might not get the command if force steam on is held on transition
                            mTimeRequiredForProcess = SECONDS_TO_OVERLAP_STEAM_WITH_DRAIN; //if no vacuum break, don't overlap!
                        }
                    }
                } else if (mState == SPServiceCrucibleState.START_BEVERAGE_PULL_DOWN) {
                    mLastTime = System.nanoTime();
                    double timeIntoProcess = (mLastTime - mStartTime) / A_BILLION;

                    //SPLog.debug(">>>>> In START PULL DOWN <<<<<   isSteaming: " + mIsSteaming + " isDraining: " + mIsDraining + " timeIntoProcess: " + timeIntoProcess + " timeRequired: " + mTimeRequiredForProcess);

                    //make sure steam is on
                    if (!mIsSteaming) {
                        setSteaming(true);
                    }

                    //make sure the drain is open
                    if (!mIsDraining) {
                        setDraining(true);
                    }

                    if (timeIntoProcess >= mTimeRequiredForProcess) {
                        if (mIsSteaming) {
                            setSteaming(false);
                        }
                        mStartTime = mLastTime = System.nanoTime();
                        mState = SPServiceCrucibleState.FINISH_BEVERAGE_PULL_DOWN;
                        mTimeRequiredForProcess = mDrainTime;
                    }
                } else if (mState == SPServiceCrucibleState.FINISH_BEVERAGE_PULL_DOWN) {
                    mLastTime = System.nanoTime();
                    double timeIntoProcess = (mLastTime - mStartTime) / A_BILLION;

                    //SPLog.debug(">>>>> In FINISH PULL DOWN <<<<<   isDraining: " + mIsDraining + " timeIntoProcess: " + timeIntoProcess + " timeRequired: " + mTimeRequiredForProcess);

                    //make sure drain is open
                    if (!mIsDraining) {
                        setDraining(true);
                    }

                    if (timeIntoProcess >= mTimeRequiredForProcess) {
                        if (mIsDraining) {
                            setDraining(false);
                        }
                        mState = SPServiceCrucibleState.WAITING_TO_DISPENSE_AND_RINSE;
                        stopSchedulingStateUpdates();
                    }
                } else if (mState == SPServiceCrucibleState.WAITING_TO_DISPENSE_AND_RINSE) {
                    //SPLog.debug("WAITING TO DISPENSE AND RINSE");
                } else if (mState == SPServiceCrucibleState.FILLING_FOR_RINSE) {
                    mLastTime = System.nanoTime();
                    double timeIntoProcess = (mLastTime - mStartTime) / A_BILLION;

                    if (!mIsFilling) {
                        setFilling(true);
                    }

                    if (isFilling() && mFlowMeter.getEdgeCount() == 0 && timeIntoProcess > MAX_SECONDS_BEFORE_FIRST_FILL_EDGE) {
//						mNotEnoughFlow = true;
//						mService.sendCrucibleState(mIndex);
                    }

                    if (mFlowMeter.getEdgeCount() >= mFillTarget) {
                        setFilling(false);
                        mState = SPServiceCrucibleState.PUSHING_RINSE_WATER_TO_TOP;
                        mTimeRequiredForProcess = SECONDS_TO_PUSH_UP_WATER_FOR_CLEANING;
                        mStartTime = mLastTime = System.nanoTime();
                        mProcessStartTemp = mThermistor.getTemperature();
                    }
                } else if (mState == SPServiceCrucibleState.PUSHING_RINSE_WATER_TO_TOP) {
                    mLastTime = System.nanoTime();
                    double timeIntoProcess = (mLastTime - mStartTime) / A_BILLION;
                    double currTemp = mThermistor.getTemperature();

                    //steam runs full on while pushing rinse water to top!
                    if (!mIsSteaming && (timeIntoProcess < mTimeRequiredForProcess)) {
                        setSteaming(true);
                    }

                    if (currTemp - mProcessStartTemp < MIN_ACCEPTABLE_TEMP_DELTA && timeIntoProcess > ACCEPTABLE_TEMP_DELTA_TIME_LIMIT) {
//						mNotEnoughSteam = true;
//						mService.sendCrucibleState(mIndex);
                    }

                    if (timeIntoProcess >= mTimeRequiredForProcess) {
                        mState = SPServiceCrucibleState.RINSE_WATER_IN_TOP_AND_HEATING;
                        mStartTime = mLastTime = System.nanoTime();
                        mProcessStartTemp = mThermistor.getTemperature();
                    }
                } else if (mState == SPServiceCrucibleState.RINSE_WATER_IN_TOP_AND_HEATING) {
                    mLastTime = System.nanoTime();
                    double timeIntoProcess = (mLastTime - mStartTime) / A_BILLION;
                    double currTemp = mThermistor.getTemperature();

                    //steam runs full on while heating for rinse!
                    if (!mIsSteaming && (mThermistor.getTemperature() < mTargetTemp)) {
                        setSteaming(true);
                    }

                    if (currTemp - mProcessStartTemp < MIN_ACCEPTABLE_TEMP_DELTA && timeIntoProcess > ACCEPTABLE_TEMP_DELTA_TIME_LIMIT) {
//						mNotEnoughSteam = true;
//						mService.sendCrucibleState(mIndex);
                    }

                    if (mThermistor.getTemperature() >= mTargetTemp) {
                        mState = SPServiceCrucibleState.START_RINSE_DRAIN;
                        mStartTime = mLastTime = System.nanoTime();
                        mTimeRequiredForProcess = SECONDS_TO_OVERLAP_STEAM_WITH_DRAIN;
                    }
                } else if (mState == SPServiceCrucibleState.START_RINSE_DRAIN) {
                    mLastTime = System.nanoTime();
                    double timeIntoProcess = (mLastTime - mStartTime) / A_BILLION;

                    //make sure steam is on
                    if (!mIsSteaming) {
                        setSteaming(true);
                    }

                    //make sure drain is open
                    if (!mIsDraining) {
                        setDraining(true);
                    }

                    if (timeIntoProcess >= mTimeRequiredForProcess) {
                        setSteaming(false);
                        mState = SPServiceCrucibleState.FINISH_RINSE_DRAIN;
                        mTimeRequiredForProcess = mDrainTime;
                        mStartTime = mLastTime = System.nanoTime();
                    }
                } else if (mState == SPServiceCrucibleState.FINISH_RINSE_DRAIN) {
                    mLastTime = System.nanoTime();
                    double timeIntoProcess = (mLastTime - mStartTime) / A_BILLION;

                    //make sure drain is open
                    if (!mIsDraining) {
                        setDraining(true);
                    }

                    if (timeIntoProcess >= mTimeRequiredForProcess) {
                        setDraining(false);
                        mState = SPServiceCrucibleState.EMPTY_START;
                        stopSchedulingStateUpdates();
                    }
                } else if (mState == SPServiceCrucibleState.FILL_FOR_CLEAN) { // ***************************** CLEANING CYCLE STARTS HERE
                    mLastTime = System.nanoTime();
                    double timeIntoProcess = (mLastTime - mStartTime) / A_BILLION;

                    if (!mIsFilling) {
                        setFilling(true);
                    }

                    if (isFilling() && mFlowMeter.getEdgeCount() == 0 && timeIntoProcess > MAX_SECONDS_BEFORE_FIRST_FILL_EDGE) {
//						mNotEnoughFlow = true;
//						mService.sendCrucibleState(mIndex);
                    }

                    if (mFlowMeter.getEdgeCount() >= mFillTarget) {
                        setFilling(false);
                        setSteaming(true);
                        mState = SPServiceCrucibleState.HEAT_FOR_CLEAN;
                        mTimeRequiredForProcess = SECONDS_TO_PUSH_UP_WATER_FOR_CLEANING;
                        mStartTime = mLastTime = System.nanoTime();
                        mProcessStartTemp = mThermistor.getTemperature();
                    }
                } else if (mState == SPServiceCrucibleState.HEAT_FOR_CLEAN) {
                    mLastTime = System.nanoTime();
                    double timeIntoProcess = (mLastTime - mStartTime) / A_BILLION;
                    double currTemp = mThermistor.getTemperature();

                    //steam runs full on while heating for rinse!
                    if (!mIsSteaming && ((timeIntoProcess < mTimeRequiredForProcess) || (mThermistor.getTemperature() < mTargetTemp))) {
                        setSteaming(true);
                    }

                    if (currTemp - mProcessStartTemp < MIN_ACCEPTABLE_TEMP_DELTA && timeIntoProcess > ACCEPTABLE_TEMP_DELTA_TIME_LIMIT) {
//						mNotEnoughSteam = true;
//						mService.sendCrucibleState(mIndex);
                    }

                    if (timeIntoProcess >= mTimeRequiredForProcess && mThermistor.getTemperature() >= mTargetTemp) {
                        setSteaming(true);
                        mState = SPServiceCrucibleState.AGITATE_FOR_CLEAN;
                        mStartTime = mLastTime = System.nanoTime();
                        mTimeRequiredForProcess = CLEAN_AGITATION_TIME;
                    }
                } else if (mState == SPServiceCrucibleState.AGITATE_FOR_CLEAN) {
                    mLastTime = System.nanoTime();
                    double timeIntoProcess = (mLastTime - mStartTime) / A_BILLION;
                    double diffBetweenFloorAndTimeInto = timeIntoProcess - Math.floor(timeIntoProcess);

                    if (!mIsSteaming && (timeIntoProcess < mTimeRequiredForProcess) && (diffBetweenFloorAndTimeInto < AGITATION_SPURT_LENGTH)) {
                        setSteaming(true);
                    } else if (mIsSteaming && (timeIntoProcess < mTimeRequiredForProcess) && (diffBetweenFloorAndTimeInto >= AGITATION_SPURT_LENGTH)) {
                        setSteaming(false);
                    }

                    if (timeIntoProcess >= mTimeRequiredForProcess) {
                        mTimeRequiredForProcess = CLEAN_SOAK_TIME;
                        setSteaming(false);
                        mStartTime = mLastTime = System.nanoTime();
                        mState = SPServiceCrucibleState.SIT_WITH_WATER_AT_TOP_FOR_CLEAN;
                    }
                } else if (mState == SPServiceCrucibleState.SIT_WITH_WATER_AT_TOP_FOR_CLEAN) {
                    mLastTime = System.nanoTime();
                    double timeIntoProcess = (mLastTime - mStartTime) / A_BILLION;

                    if (timeIntoProcess >= mTimeRequiredForProcess) {
                        mTimeRequiredForProcess = CLEAN_VACUUM_BREAK_TIME;
                        setSteaming(true);
                        mStartTime = mLastTime = System.nanoTime();
                        mState = SPServiceCrucibleState.CLEAN_VACUUM_BREAK;
                    }
                } else if (mState == SPServiceCrucibleState.CLEAN_VACUUM_BREAK) {
                    mLastTime = System.nanoTime();
                    double timeIntoProcess = (mLastTime - mStartTime) / A_BILLION;

                    if (!mIsSteaming && (timeIntoProcess < mTimeRequiredForProcess)) {
                        setSteaming(true);
                    }

                    if (timeIntoProcess >= mTimeRequiredForProcess) {
                        mTimeRequiredForProcess = SECONDS_TO_OVERLAP_STEAM_WITH_DRAIN;
                        setSteaming(true);
                        mState = SPServiceCrucibleState.START_CLEAN_DRAIN;
                        mStartTime = mLastTime = System.nanoTime();
                    }
                } else if (mState == SPServiceCrucibleState.START_CLEAN_DRAIN) {
                    mLastTime = System.nanoTime();
                    double timeIntoProcess = (mLastTime - mStartTime) / A_BILLION;

                    if (!mIsSteaming && (timeIntoProcess < mTimeRequiredForProcess)) {
                        setSteaming(true);
                    }
                    if (!mIsDraining && (timeIntoProcess < mTimeRequiredForProcess)) {
                        setDraining(true);
                    }

                    if (timeIntoProcess >= mTimeRequiredForProcess) {
                        setSteaming(false);
                        setDraining(true);
                        mState = SPServiceCrucibleState.WAITING_FOR_CLEAN_DRAIN;
                        mTimeRequiredForProcess = CLEAN_DRAIN_TIME;
                        mStartTime = mLastTime = System.nanoTime();
                        mProcessStartTemp = mThermistor.getTemperature();
                    }
                } else if (mState == SPServiceCrucibleState.WAITING_FOR_CLEAN_DRAIN) {
                    mLastTime = System.nanoTime();
                    double timeIntoProcess = (mLastTime - mStartTime) / A_BILLION;

                    //steam runs full on while heating for rinse!
                    if (!mIsDraining && (timeIntoProcess < mTimeRequiredForProcess)) {
                        setDraining(true);
                    }

                    if (timeIntoProcess >= mTimeRequiredForProcess) {
                        setDraining(false);
                        mState = SPServiceCrucibleState.SIT_WITH_WATER_AT_BOTTOM_FOR_CLEAN;
                        mStartTime = mLastTime = System.nanoTime();
                        mTimeRequiredForProcess = CLEAN_SOAK_TIME;
                    }
                } else if (mState == SPServiceCrucibleState.SIT_WITH_WATER_AT_BOTTOM_FOR_CLEAN) {
                    mLastTime = System.nanoTime();
                    double timeIntoProcess = (mLastTime - mStartTime) / A_BILLION;

                    if (timeIntoProcess >= mTimeRequiredForProcess) {
                        mStartTime = mLastTime = System.nanoTime();
                        if (++mCleaningCycleCount >= mTotalCleaningCycles) {
                            mState = SPServiceCrucibleState.WAIT_FOR_CLEAN_RINSE;
                        } else {
                            setSteaming(true);
                            mState = SPServiceCrucibleState.AGITATE_FOR_CLEAN;
                            mStartTime = mLastTime = System.nanoTime();
                            mTimeRequiredForProcess = CLEAN_AGITATION_TIME;
                        }
                    }
                } else if (mState == SPServiceCrucibleState.WAIT_FOR_CLEAN_RINSE) {
                    //nothing to do but sit and wait...
                } else if (mState == SPServiceCrucibleState.FILL_FOR_CLEAN_RINSE) {
                    mLastTime = System.nanoTime();
//					double timeIntoProcess = (mLastTime - mStartTime) / A_BILLION;

                    if (!mIsFilling) {
                        setFilling(true);
                    }

                    if (mFlowMeter.getEdgeCount() >= mFillTarget) {
                        setFilling(false);
                        setSteaming(true);
                        mState = SPServiceCrucibleState.HEAT_FOR_CLEAN_RINSE;
                        mTimeRequiredForProcess = SECONDS_TO_PUSH_UP_WATER_FOR_CLEANING;
                        mStartTime = mLastTime = System.nanoTime();
                        mProcessStartTemp = mThermistor.getTemperature();
                    }
                } else if (mState == SPServiceCrucibleState.HEAT_FOR_CLEAN_RINSE) {
                    mLastTime = System.nanoTime();
                    double timeIntoProcess = (mLastTime - mStartTime) / A_BILLION;
                    double currTemp = mThermistor.getTemperature();

                    //steam runs full on while heating for rinse!
                    if (!mIsSteaming && ((timeIntoProcess < mTimeRequiredForProcess) || (mThermistor.getTemperature() < mTargetTemp))) {
                        setSteaming(true);
                    }

                    if (currTemp - mProcessStartTemp < MIN_ACCEPTABLE_TEMP_DELTA && timeIntoProcess > ACCEPTABLE_TEMP_DELTA_TIME_LIMIT) {
//						mNotEnoughSteam = true;
                    }

                    if ((timeIntoProcess >= mTimeRequiredForProcess) && (mThermistor.getTemperature() >= mTargetTemp)) { //System.out.println("finished second heat for cleaning");
                        setSteaming(true);
                        mState = SPServiceCrucibleState.AGITATE_FOR_CLEAN_RINSE;
                        mStartTime = mLastTime = System.nanoTime();
                        mTimeRequiredForProcess = CLEAN_RINSE_AGITATION_TIME;
                    }
                } else if (mState == SPServiceCrucibleState.AGITATE_FOR_CLEAN_RINSE) {
                    mLastTime = System.nanoTime();
                    double timeIntoProcess = (mLastTime - mStartTime) / A_BILLION;
                    double diffBetweenFloorAndTimeInto = timeIntoProcess - Math.floor(timeIntoProcess);

                    if (!mIsSteaming && (timeIntoProcess < mTimeRequiredForProcess) && (diffBetweenFloorAndTimeInto < AGITATION_SPURT_LENGTH)) {
                        setSteaming(true);
                    } else if (mIsSteaming && (timeIntoProcess < mTimeRequiredForProcess) && (diffBetweenFloorAndTimeInto >= AGITATION_SPURT_LENGTH)) {
                        setSteaming(false);
                    }

                    if (timeIntoProcess >= mTimeRequiredForProcess) {
                        mTimeRequiredForProcess = CLEAN_SOAK_TIME;
                        setSteaming(false);
                        mStartTime = mLastTime = System.nanoTime();
                        mState = SPServiceCrucibleState.SIT_WITH_WATER_AT_TOP_FOR_CLEAN_RINSE;
                    }
                } else if (mState == SPServiceCrucibleState.SIT_WITH_WATER_AT_TOP_FOR_CLEAN_RINSE) {
                    mLastTime = System.nanoTime();
                    double timeIntoProcess = (mLastTime - mStartTime) / A_BILLION;

                    if (timeIntoProcess >= mTimeRequiredForProcess) {
                        mTimeRequiredForProcess = CLEAN_VACUUM_BREAK_TIME;
                        setSteaming(true);
                        mStartTime = mLastTime = System.nanoTime();
                        mState = SPServiceCrucibleState.CLEAN_RINSE_VACUUM_BREAK;
                    }
                } else if (mState == SPServiceCrucibleState.CLEAN_RINSE_VACUUM_BREAK) {
                    mLastTime = System.nanoTime();
                    double timeIntoProcess = (mLastTime - mStartTime) / A_BILLION;

                    if (!mIsSteaming && (timeIntoProcess < mTimeRequiredForProcess)) {
                        setSteaming(true);
                    }

                    if (timeIntoProcess >= mTimeRequiredForProcess) {
                        setDraining(true);
                        setSteaming(true);
                        mTimeRequiredForProcess = SECONDS_TO_OVERLAP_STEAM_WITH_DRAIN;
                        mStartTime = mLastTime = System.nanoTime();
                        mState = SPServiceCrucibleState.START_CLEAN_RINSE_DRAIN;
                    }
                } else if (mState == SPServiceCrucibleState.START_CLEAN_RINSE_DRAIN) {
                    mLastTime = System.nanoTime();
                    double timeIntoProcess = (mLastTime - mStartTime) / A_BILLION;

                    if (!mIsSteaming && (timeIntoProcess < mTimeRequiredForProcess)) {
                        setSteaming(true);
                    }
                    if (!mIsDraining && (timeIntoProcess < mTimeRequiredForProcess)) {
                        setDraining(true);
                    }

                    if (timeIntoProcess >= mTimeRequiredForProcess) {
                        setSteaming(false);
                        setDraining(true);
                        mState = SPServiceCrucibleState.WAITING_FOR_CLEAN_RINSE_DRAIN;
                        mTimeRequiredForProcess = CLEAN_RINSE_DRAIN_TIME;
                        mStartTime = mLastTime = System.nanoTime();
                        mProcessStartTemp = mThermistor.getTemperature();
                    }
                } else if (mState == SPServiceCrucibleState.WAITING_FOR_CLEAN_RINSE_DRAIN) {
                    mLastTime = System.nanoTime();
                    double timeIntoProcess = (mLastTime - mStartTime) / A_BILLION;

                    if (!mIsDraining && (timeIntoProcess < mTimeRequiredForProcess)) {
                        setDraining(true);
                    }

                    if (timeIntoProcess >= mTimeRequiredForProcess) {
                        setDraining(false);
                        mTimeRequiredForProcess = CLEAN_SOAK_TIME;
                        mStartTime = mLastTime = System.nanoTime();
                        mState = SPServiceCrucibleState.SIT_WITH_WATER_AT_BOTTOM_FOR_CLEAN_RINSE;
                    }
                } else if (mState == SPServiceCrucibleState.SIT_WITH_WATER_AT_BOTTOM_FOR_CLEAN_RINSE) {
                    mLastTime = System.nanoTime();
                    double timeIntoProcess = (mLastTime - mStartTime) / A_BILLION;

                    if (timeIntoProcess >= mTimeRequiredForProcess) {
                        mStartTime = mLastTime = System.nanoTime();
                        if (++mCleaningCycleCount >= mTotalCleaningCycles) {
                            mState = SPServiceCrucibleState.EMPTY_START;
                        } else {
                            setSteaming(true);
                            mState = SPServiceCrucibleState.AGITATE_FOR_CLEAN_RINSE;
                            mStartTime = mLastTime = System.nanoTime();
                            mTimeRequiredForProcess = CLEAN_RINSE_AGITATION_TIME;
                        }
                    }
                }
                sleep(10); //don't take up cycles if we don't need them (1/100 of a second sleep time)
            } catch (Exception e) {
                mRunning = false;
            }
        }
    }

    private void setFilling(boolean is) throws ConnectionLostException {
        if (isLocked()) return;

        mIsFilling = is;
        mFillSignal.write(isFilling());
        mFlowMeter.setFillSignalStatus(isFilling());
    }

    private void setSteaming(boolean is) throws ConnectionLostException {
        if (isLocked()) return;

        mIsSteaming = is;
        mSteamSignal.write(isSteaming());
    }

    private void setDraining(boolean is) throws ConnectionLostException {
        if (isLocked()) return;

        mIsDraining = is;
        mPullDownActuator.write(isDraining());
    }

    public boolean isFilling() {
        return mIsFilling || mForceFill;
    }

    public boolean isSteaming() {
        return mIsSteaming || mForceSteam;
    }

    public boolean isDraining() {
        return mIsDraining || mForceDrainOpen;
    }

    public boolean hasFlowedTooMuch() {
        return mTooMuchFlow;
    }

    public boolean hasSteamedTooMuch() {
        return mTooMuchSteam;
    }

    public boolean notEnoughFlow() {
        return mNotEnoughFlow;
    }

    public boolean notEnoughSteam() {
        return mNotEnoughSteam;
    }

    public double getTemperature() {
        return mThermistor.getTemperature();
    }

    public double getVolume() {
        return SPFlowMeter.convertEdgesToMilliliters(mFlowMeter.getEdgeCount());
    }

    public int getEdgeCount() {
        return mFlowMeter.getEdgeCount();
    }

    public SPServiceCrucibleState getCrucibleState() {
        return mState;
    }

    private void startSchedulingStateUpdates() {
        if (mUpdateSchedule != null) {
            return;
        }

        mUpdateSchedule = new Timer();
        mUpdateSchedule.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                UID id = new UID();
                mService.sendCrucibleState(mIndex, id);
            }
        }, new Date(), 500);
    }

    private void stopSchedulingStateUpdates() {
    }

    public int getTimeLeftInBrew() {
        int timeLeft = 0;
        if (mState != SPServiceCrucibleState.AGITATING && mState != SPServiceCrucibleState.STEEP_BETWEEN_AGITATIONS) {
            timeLeft = 0;
        } else {
            long now = System.nanoTime();
            int timeInto = ((int) Math.round((now - mBrewStartTime) / A_BILLION));
            timeLeft = getTotalBrewTime() - timeInto;
            if (timeLeft < 0) {
                timeLeft = 0;
            }
        }
        return timeLeft;
    }

    private int getTotalBrewTime() {
        int total = 0;
        for (int i = 0; i < mAgitationAndSteepLengths.length; i++) {
            total += mAgitationAndSteepLengths[i];
        }
        return total;
    }

    public int getTimeLeftInCycle() {
        double timeIntoProcess = (System.nanoTime() - mStartTime) / A_BILLION;
        double timeLeft = mTimeRequiredForProcess - timeIntoProcess;
        if (timeLeft < 0) timeLeft = 0;
        return (int) timeLeft;
    }

    public void setInManualMode(boolean is) {
        if (isLocked()) return;

        mInManualMode = is;
        mStartTime = mLastTime = System.nanoTime();
        mTimeRequiredForProcess = 0;
        mProcessStartTemp = mThermistor.getTemperature();
        cancelCycle();
        if (is) startSchedulingStateUpdates();
    }

    public void lock() {
        mLocked = true;
    }

    public void unlock() {
        mLocked = false;
    }

    public boolean isLocked() {
        return mLocked;
    }

    public static void registerColdWaterPressureTooLowListener(ColdWaterPressureLowListener listener) {
        mColdWaterPressureLowListener = listener;
    }

    public static void unregisterColdWaterPressureTooLowListener() {
        mColdWaterPressureLowListener = null;
    }

    public interface ColdWaterPressureLowListener {
        void displayColdWaterPressureLowWarning();
    }
}
