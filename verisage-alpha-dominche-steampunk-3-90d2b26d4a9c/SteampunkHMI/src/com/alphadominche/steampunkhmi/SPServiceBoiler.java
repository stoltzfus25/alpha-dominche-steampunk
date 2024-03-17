package com.alphadominche.steampunkhmi;

import ioio.lib.api.AnalogInput;
import ioio.lib.api.DigitalInput;
import ioio.lib.api.DigitalOutput;

import java.util.Observable;
import java.util.Observer;

public class SPServiceBoiler extends Thread {
	public final static long ONE_SECOND = 1000000000; //in nanoseconds
	public final static long ONE_HALF_SECOND = 500000000; //in nanoseconds
	public final static double STEAM_BLOW_OFF_DURATION = 100.0; //30.0; //in seconds
	public final static double TOO_COLD_TEMP = SPServiceThermistor.convertFromTempToTemp(SPTempUnitType.FAHRENHEIT, SPTempUnitType.KELVIN, 200.0);
	public final static double WARMED_FROM_TOO_COLD_TEMP = SPServiceThermistor.convertFromTempToTemp(SPTempUnitType.FAHRENHEIT, SPTempUnitType.KELVIN, 220.0);
	public static final double TEMP_UNDERFLOW_TOLERANCE = 10.0 * 5.0 / 9.0;
	public static final long LOOP_DELAY = 10L;
	public static final int START_CYCLE_LIMIT = 15;
	public static final int CYCLES_BETWEEN_LOGS = 5;

	private boolean mRunning;
	private DigitalInput mFillSignal;
	private DigitalOutput mHeatSignal;
	private SPServiceThermistor mThermistor;
	private double mMaxTemperature;
	private boolean mSignal;
	private boolean mForceHeatOn;
	private boolean mForceHeatOff;
	
	private double mSteamReleaseStartTime;
	private SPBoilerState mState;
	
	SPIOIOService mService;
	
	private long mLastTime;
	
	private int mCyclesSinceLastLog;
	private int mStartCycleCount;
	
	private boolean mInManualMode;
	
	SPServiceBoiler(
			double maxTemp,
			DigitalInput fillSignal,
			DigitalOutput heatSignal,
			AnalogInput temperatureSignal,
			SPIOIOService service,
			SPTempUnitType tempType) {
		super(); SPLog.debug("############# instantiating boiler");
		mInManualMode = false;
		mState = SPBoilerState.NORMAL;
		mSteamReleaseStartTime = 0.0;
		
		mFillSignal = fillSignal;
		mHeatSignal = heatSignal;
		
		mCyclesSinceLastLog = 0;
		mStartCycleCount = 0;
		
		try {
			mHeatSignal.write(false);
		} catch (Exception e) {
			
		}
		
		mThermistor = new SPServiceThermistor(temperatureSignal, tempType, service); mThermistor.boiler = true;
		mThermistor.addObserver(new Observer() {

			@Override
			public void update(Observable observable, Object data) {
				mCyclesSinceLastLog++;
				if (mCyclesSinceLastLog >= CYCLES_BETWEEN_LOGS) {
					mCyclesSinceLastLog = 0;
				}
				if (mStartCycleCount < START_CYCLE_LIMIT) {
					mStartCycleCount++;
				}
			}
			
		});
		mRunning = true;
		mMaxTemperature = maxTemp;
		mSignal = false;
		mForceHeatOn = false;
		mForceHeatOff = false;
		mService = service;
		try {
			mHeatSignal.write(mSignal);
		} catch (Exception e) {
			mRunning = false;
		}
		
		mLastTime = System.nanoTime();
	}
	
	public boolean isHeating() {
		return mForceHeatOn || (mSignal && !mForceHeatOff);
	}
	
	public boolean isFilling() throws Exception {
		return mFillSignal.read();
	}
	
	public double getTemperature() {
		return mThermistor.getTemperature();
	}
	
	public void setMaxTemperature(double maxTemp) {
		mMaxTemperature = maxTemp;
	}
	
	public double getMaxTemperature() {
		return mMaxTemperature;
	}
	
	public void forceHeatOn() {
		mForceHeatOff = false;
		mForceHeatOn = true;
		setHeatSignal(mSignal);
	}
	
	public void stopForceHeatOn() {
		mForceHeatOn = false;
		setHeatSignal(mSignal);
	}
	
	public void forceHeatOff() {
		mForceHeatOn = false;
		mForceHeatOff = true;
		setHeatSignal(mSignal);
	}
	
	public void stopForceHeatOff() {
		mForceHeatOff = false;
		setHeatSignal(mSignal);
	}
	
	public void setHeatSignal(boolean on) {
		try {
			mSignal = on;
			if (mForceHeatOn) {
				mHeatSignal.write(true);
			} else if (mForceHeatOff) {
				mHeatSignal.write(false);
			} else {
				mHeatSignal.write(mSignal);
			}
		} catch (Exception e) {
			
		}
	}
	
	@Override
	public void run() {
		try {
			while(mRunning) {
				long thisTime = System.nanoTime();
				if (thisTime - mLastTime > ONE_HALF_SECOND) {
					mService.sendBoilerState();
					mLastTime = thisTime;
				}
				
				if (mStartCycleCount < START_CYCLE_LIMIT - 1) {
					sleep(LOOP_DELAY);
					continue; //avoid blipping the heating element at startup!
				}
				
				if (mInManualMode) {
					sleep(LOOP_DELAY);
					continue; //preempt state machine if in manual mode
				}
				
				boolean filling = mFillSignal.read();
				
				if (mState == SPBoilerState.NORMAL) {
					if (mSignal && (filling || mThermistor.getTemperature() >= mMaxTemperature)) {
						setHeatSignal(false);
					} else if (!mSignal && !filling && (mThermistor.getTemperature() < mMaxTemperature)) {
						setHeatSignal(true);
					}
					
					if (mThermistor.getTemperature() < TOO_COLD_TEMP) {
						mState = SPBoilerState.TOO_COLD;
					}
				} else if (mState == SPBoilerState.TOO_COLD) {
					if (mSignal && (filling || mThermistor.getTemperature() >= mMaxTemperature)) {
						setHeatSignal(false);
					} else if (!mSignal && !filling && (mThermistor.getTemperature() < mMaxTemperature)) {
						setHeatSignal(true);
					}
					
					if (mThermistor.getTemperature() >= WARMED_FROM_TOO_COLD_TEMP) {
						mState = SPBoilerState.WARMED_AFTER_TOO_COLD;
						mService.sendReleaseSteam();
					}
				} else if (mState == SPBoilerState.WARMED_AFTER_TOO_COLD) {
					if (mSignal && (filling || mThermistor.getTemperature() >= WARMED_FROM_TOO_COLD_TEMP)) {
						setHeatSignal(false);
					} else if (!mSignal && !filling && (mThermistor.getTemperature() < WARMED_FROM_TOO_COLD_TEMP)) {
						setHeatSignal(true);
					}
				} else if (mState == SPBoilerState.RELEASING_EXCESS_STEAM) {
					if (mSignal && (filling || mThermistor.getTemperature() >= WARMED_FROM_TOO_COLD_TEMP)) {
						setHeatSignal(false);
					} else if (!mSignal && !filling && (mThermistor.getTemperature() < WARMED_FROM_TOO_COLD_TEMP)) {
						setHeatSignal(true);
					}
					
					if (((double)(thisTime - mSteamReleaseStartTime) / ONE_SECOND) >= STEAM_BLOW_OFF_DURATION) {
						mService.stopSteamOnAllCrucibles();
						mState = SPBoilerState.NORMAL;
					}
				}
				
				sleep(LOOP_DELAY); //don't use cycles if we don't need them
			} SPLog.debug("boiler exiting due to loop exit");
		} catch (ioio.lib.api.exception.ConnectionLostException e) { SPLog.debug("boiler exiting due to exception: " + e.toString());
			mService.stopSteamOnAllCrucibles(); //might cause an exception? if this happens it's likely because of a disconnection...in which case this call could kill the app?
			SPLog.debug("IOIO CONNECTION LOST");
		} catch (InterruptedException e) {
			SPLog.debug("BOILER CONTROL THREAD INTERRUPTED");
		}
	}
	
	public void releaseExcessSteam() {
		mSteamReleaseStartTime = System.nanoTime();
		mState = SPBoilerState.RELEASING_EXCESS_STEAM;
		mService.cancelAllBrews();
		mService.steamOnAllCrucibles();
	}
	
	public static class ReleaseSteamRequest {}
	
	public void stopRunning() { //SPLog.debug("############# just stopped boiler!");
		mRunning = false;
	}
	
	public boolean isRunning() {
		return mRunning;
	}
	
	public boolean inManualMode() {
		return mInManualMode;
	}
	
	public void setInManualMode(boolean is) {
		mInManualMode = is;
		mForceHeatOn = false;
		mForceHeatOff = false;
		mState = SPBoilerState.NORMAL;
		setHeatSignal(false);
	}
}
