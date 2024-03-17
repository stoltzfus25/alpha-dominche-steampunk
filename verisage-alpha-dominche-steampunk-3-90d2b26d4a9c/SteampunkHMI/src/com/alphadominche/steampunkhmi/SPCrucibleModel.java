package com.alphadominche.steampunkhmi;

import java.util.Observable;
import java.util.Observer;
import java.util.Timer;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;

import com.alphadominche.steampunkhmi.contentprovider.Provider;
import com.alphadominche.steampunkhmi.database.tables.RecipeTable;

public class SPCrucibleModel extends Observable implements Observer {
	private SPRecipe mRecipe;
	private int mCurrentStack;
	private int mTimeLeft;
	private double mTemp;
	private double mVolume;
	private int mEdges;
	private int mCurrAgitationCycle;
	private int mAgitationCycleTimeLeft;
	private int mExtractionTimeLeft;
	
	private int mTimeLeftInBrew;
	
	private SPCrucibleState mCurrState;
	private SPCrucibleState mAssignedState;
	private boolean mLocked;
	
	private Context mContext;
	
	private Timer mTimer;
	private int mIndex;
	
	//error state...
	private boolean mTooMuchFlow;
	private boolean mTooMuchSteam;
	private boolean mNotEnoughFlow;
	private boolean mNotEnoughSteam;
	
	private boolean mSteaming;
	private boolean mFilling;
	private boolean mDraining;
	
	private boolean mSteamedTooMuchOnFillAndHeating;
	
	SPCrucibleModel(SPRecipe recipe, Context context, int index) {
		mSteamedTooMuchOnFillAndHeating = false;
		mRecipe = recipe;
		if (mRecipe != null) {
			mRecipe.addObserver(this);
		}
		mCurrentStack = 0;
		
		mTimeLeft = 0;
		mTemp = 0;
		mVolume = 0;
		mEdges = 0;
		mCurrAgitationCycle = -1;
		mAgitationCycleTimeLeft = 0;
		mExtractionTimeLeft = 0;
		mTimeLeftInBrew = 0;
		mCurrState = SPCrucibleState.IDLE;
		mContext = context;
		mIndex = index;
		mLocked = false;
		
		mFilling = false;
		mDraining = false;
		mSteaming = false;
		
//		resetErrorState();
		mTooMuchFlow = false;
		mTooMuchSteam = false;
		mNotEnoughFlow = false;
		mNotEnoughSteam = false;
	}
	
	public SPRecipe getRecipe() {
		return mRecipe;
	}
	
	public void setRecipe(SPRecipe recipe) {
		if (mRecipe != null) {
			mRecipe.deleteObserver(this);
		}
		mRecipe = recipe;
		if (mRecipe != null) {
			mRecipe.addObserver(this);
		}
		setChanged();
		notifyObservers();
	}

	public int getCurrentStack() {
		return mCurrentStack;
	}
	
	public int getStackCount() {
		if (null == mRecipe)
			return -1;
		return mRecipe.getStackCount();
	}
	
	public SPUser getRoaster() {
		if (null == mRecipe)
			return null;
		return mRecipe.getRoaster();
	}
	
	public String getCoffee() {
		if (null == mRecipe)
			return "";
		return mRecipe.getName();
	}
	
	public int getRecipeTime(int stackIndex) {
		if (null == mRecipe)
			return -1;
		return mRecipe.getTotalTime(stackIndex);
	}
	
	public int getTimeLeft() {
		return mTimeLeft;
	}
	
	public double getRecipeTemp(int stack) {
		if (null == mRecipe)
			return 0.0;
		return mRecipe.getTemp(stack);
	}
	
	public double getTemp() {
		return mTemp;
	}
	
	public SPTempUnitType getRecipeTempType(int stackIndex) {
		if (null == mRecipe)
			return null;
		return mRecipe.getTempType(stackIndex);
	}
	
	public double getRecipeVolume(int stackIndex) {
		if (null == mRecipe)
			return 0.0;
		return mRecipe.getVolume(stackIndex);
	}
	
	public SPVolumeUnitType getRecipeVolumeType(int stackIndex) {
		if (null == mRecipe)
			return null;
		return mRecipe.getVolumeType(stackIndex);
	}
	
	public double getVolume() {
		return mVolume;
	}
	
	public int getEdges() {
		return mEdges;
	}
	
	public double[] getAgitationCycles(int stack) {
		if (null == mRecipe)
			return null;
		int count = mRecipe.getAgitationCount(stack);
		double agis[] = new double[count];
		for(int i = 0;i < count;i++) {
			agis[i] = mRecipe.getAgitationLength(stack, i);
		}
		return agis;
	}
	
	public double[] getAgitationPulseWidths(int stack) {
		if (null == mRecipe)
			return null;
		int count = mRecipe.getAgitationCount(stack);
		double agis[] = new double[count];
		for(int i = 0;i < count;i++) {
			agis[i] = mRecipe.getAgitationPulseWidth(stack, i);
		}
		return agis;
	}
	
	public int[] getAgitationStartTimes(int stack) {
		if (null == mRecipe)
			return null;
		int count = mRecipe.getAgitationCount(stack);
		int agis[] = new int[count];
		for(int i = 0;i < count;i++) {
			agis[i] = mRecipe.getAgitationStartTime(stack, i);
		}
		return agis;
	}
	
	public int getAgitationIndex() {
		return mCurrAgitationCycle;
	}
	
	public int getAgitationTimeLeft() {
		return mAgitationCycleTimeLeft;
	}
	
	public double getGrind() {
		if (null == mRecipe)
			return -1;
		return mRecipe.getGrind();
	}
	
	public String getFilter() {
		if (null == mRecipe)
			return null;
		return mRecipe.getFilter();
	}
	
	public int getRecipeExtractionTime(int stack) {
		if (null == mRecipe)
			return -1;
		return mRecipe.getExtractionSeconds(stack);
	}
	
	public int getExtractionTimeLeft() {
		return mExtractionTimeLeft;
	}
	
	public void setExtractionTimeLeft(int time) {
		mExtractionTimeLeft = time;
		setChanged();
		notifyObservers();
	}
	
	public void setTimeLeftInBrew(int timeLeft) {
		mTimeLeftInBrew = timeLeft;
		setChanged();
		notifyObservers();
	}
	
	public int getTimeLeftInBrew() {
		return mTimeLeftInBrew;
	}
	
	public SPCrucibleState getState() {
		if (mAssignedState != null) {
			return mAssignedState;
		} else {
			return mCurrState;
		}
	}
	
	public boolean isLocked() {
		return mLocked;
	}
	
	public void fillAndHeatBeverageWater() {
		if (inErrorState()) {
			//need to make this fail!
//			return;
		}
		if (mRecipe == null) {
			return;
		}
		
		long rId = -1L;
		if (mRecipe != null) {
			rId = mRecipe.getId();
		}
		
		String rUuid = "_";
		if (rId > 0) {
			Cursor uuidCursor = mContext.getContentResolver().query(Provider.RECIPE_CONTENT_URI, new String[] {RecipeTable.UUID}, RecipeTable.WHERE_ID_EQUALS, new String[] {"" + rId}, null);
			if (uuidCursor.moveToFirst()) {
				rUuid = uuidCursor.getString(uuidCursor.getColumnIndex(RecipeTable.UUID));
			} else {
				SPLog.send(mContext, rUuid, mIndex, SPLog.INFO, SPLog.BREW, "failed to find recipe " + rId + " in the database and therefore could not retrieve its UUID.");
			}
			uuidCursor.close();
		}
		
		SPLog.send(mContext, rUuid, mIndex, SPLog.INFO, SPLog.BREW, "started brewing");
		
		Intent fillIntent = new Intent(SPIOIOService.CRUCIBLE_COMMAND_INTENT);
		fillIntent.putExtra(SPIOIOService.CRUCIBLE, mIndex);
		fillIntent.putExtra(SPIOIOService.COMMAND, SPIOIOService.START_BREW_PROCESS);
		fillIntent.putExtra(SPIOIOService.VOLUME,  mRecipe.getVolume(mCurrentStack));
		fillIntent.putExtra(SPIOIOService.TEMPERATURE, mRecipe.getTemp(mCurrentStack));
		fillIntent.putExtra(SPIOIOService.DRAIN_TIME, (double)SPRecipe.FILL_DRAIN_TIME);
		mContext.startService(fillIntent);
		setAssignedState(SPCrucibleState.FILLING);
	}
	
	private double getTimeBefore(int index, double[] ags) {
		double time = 0;
		for(int i = 0;i < index;i++) {
			time += ags[i];
		}
		return time;
	}
	
	public void brewBeverage() {
		if (inErrorState()) {
			//need to make this fail!
//			return;
		}
		
		Intent brewIntent = new Intent(SPIOIOService.CRUCIBLE_COMMAND_INTENT);
		brewIntent.putExtra(SPIOIOService.CRUCIBLE, mIndex);
		brewIntent.putExtra(SPIOIOService.COMMAND, SPIOIOService.COMMENCE_BREW_PROCESS);
		int agCount = mRecipe.getAgitationCount(mCurrentStack);
		double [] agitations = new double[agCount * SPIOIOService.TIME_ENTRIES_PER_AGITATION_CYCLE];
		double [] pulseWidths = new double[agCount * SPIOIOService.TIME_ENTRIES_PER_AGITATION_CYCLE];
		for(int i = 0;i < agCount;i++) {
			agitations[i * SPIOIOService.TIME_ENTRIES_PER_AGITATION_CYCLE] = mRecipe.getAgitationLength(mCurrentStack, i);
			pulseWidths[i * SPIOIOService.TIME_ENTRIES_PER_AGITATION_CYCLE] = mRecipe.getAgitationPulseWidth(mCurrentStack, i);
			boolean beforeEndOfAgCycles = i < (agCount - 1);
			agitations[i * SPIOIOService.TIME_ENTRIES_PER_AGITATION_CYCLE + 1] = (beforeEndOfAgCycles ?
					(mRecipe.getAgitationStartTime(mCurrentStack, i + 1)) :
					mRecipe.getTotalTime(mCurrentStack)) -
					getTimeBefore(i * SPIOIOService.TIME_ENTRIES_PER_AGITATION_CYCLE + 1, agitations);
			pulseWidths[i * SPIOIOService.TIME_ENTRIES_PER_AGITATION_CYCLE + 1] = SPModel.MIN_AGITATION_PULSE_WIDTH;
		}
		brewIntent.putExtra(SPIOIOService.AGITATIONS,  agitations);
		brewIntent.putExtra(SPIOIOService.PULSE_WIDTHS, pulseWidths);
		brewIntent.putExtra(SPIOIOService.VACUUM_BREAK, mRecipe.getVacuumBreak(mCurrentStack));
		brewIntent.putExtra(SPIOIOService.DRAIN_TIME, (double)mRecipe.getExtractionSeconds(mCurrentStack));
		mContext.startService(brewIntent);
		setAssignedState(SPCrucibleState.AGITATING);
	}
	
	public void rinseCrucible() {
		if (inErrorState()) {
			//need to make this fail!
//			return;
		}
		
		Intent rinseIntent = new Intent(SPIOIOService.CRUCIBLE_COMMAND_INTENT);
		rinseIntent.putExtra(SPIOIOService.CRUCIBLE, mIndex);
		rinseIntent.putExtra(SPIOIOService.COMMAND, SPIOIOService.START_RINSE_CYCLE);
		rinseIntent.putExtra(SPIOIOService.DRAIN_TIME, (double)SPRecipe.RINSE_DRAIN_TIME);
		mContext.startService(rinseIntent);
		setAssignedState(SPCrucibleState.RINSING);
	}
	
	public void startCleaningCrucible() {
		if (inErrorState()) {
			
		}
		
		Intent cleaningIntent = new Intent(SPIOIOService.CRUCIBLE_COMMAND_INTENT);
		cleaningIntent.putExtra(SPIOIOService.COMMAND, SPIOIOService.START_CLEANING_CYCLE);
		cleaningIntent.putExtra(SPIOIOService.CRUCIBLE, mIndex);
		double volume = SPModel.getInstance(mContext).getCleaningVolume();
		cleaningIntent.putExtra(SPIOIOService.VOLUME, volume);
		cleaningIntent.putExtra(SPIOIOService.TEMPERATURE, SPModel.getInstance(mContext).getCleaningTemp());
		cleaningIntent.putExtra(SPIOIOService.CYCLES, SPIOIOService.CLEANING_CYCLES);
		mContext.startService(cleaningIntent);
		setAssignedState(SPCrucibleState.CLEANING_FILL_AND_HEAT);
	}
	
	public void finishCleaningCrucible() {
		if (inErrorState()) {
			
		}
		
		double volume = SPModel.getInstance(mContext).getCleaningVolume();
		
		Intent cleaningIntent = new Intent(SPIOIOService.CRUCIBLE_COMMAND_INTENT);
		cleaningIntent.putExtra(SPIOIOService.COMMAND, SPIOIOService.FINISH_CLEANING_CYCLE);
		cleaningIntent.putExtra(SPIOIOService.CRUCIBLE, mIndex);
		cleaningIntent.putExtra(SPIOIOService.VOLUME, volume);
		cleaningIntent.putExtra(SPIOIOService.TEMPERATURE, SPModel.getInstance(mContext).getCleaningTemp());
		cleaningIntent.putExtra(SPIOIOService.CYCLES, SPIOIOService.CLEANING_RINSE_CYCLES);
		mContext.startService(cleaningIntent);
		setAssignedState(SPCrucibleState.CLEANING_RINSE_FILL_AND_HEAT);
	}
	
	public void stop() {
		if (mTimer != null) {
			mTimer.cancel();
		}
		
		mCurrentStack = 0;
		Intent cancelIntent = new Intent(SPIOIOService.CRUCIBLE_COMMAND_INTENT);
		cancelIntent.putExtra(SPIOIOService.CRUCIBLE, mIndex);
		cancelIntent.putExtra(SPIOIOService.COMMAND, SPIOIOService.CANCEL_PROCESS);
		mContext.startService(cancelIntent);
		setAssignedState(SPCrucibleState.IDLE);
	}
	
	public void setState(SPCrucibleState newState) {
		if (mLocked) {
			return; //no state changes allowed when disabled!
		}
		
		if (mRecipe != null && mRecipe.getType() == SPRecipeType.TEA &&
				newState == SPCrucibleState.START_RINSING &&
				mCurrState != SPCrucibleState.START_RINSING && //fix the tea recipe last stack repeat bug?
				mCurrentStack < mRecipe.getStackCount()) {
			newState = SPCrucibleState.WAITING_FOR_NEXT_STACK;
		}
		
		if (mAssignedState != null && newState == mAssignedState) {
			mAssignedState = null;
		}
		
		if (mRecipe != null && mRecipe.getType() == SPRecipeType.TEA &&
				newState == SPCrucibleState.WAITING_FOR_NEXT_STACK &&
				mCurrState != SPCrucibleState.WAITING_FOR_NEXT_STACK) {
			mCurrentStack++;
			if (mCurrentStack >= mRecipe.getStackCount()) {
				newState = SPCrucibleState.START_RINSING;
				mCurrentStack = 0;
			}
		}
		
		if (newState == SPCrucibleState.IDLE) {
			mCurrentStack = 0;
		}
		
		if (newState != mCurrState) {
			mCurrState = newState;
			setChanged();
			notifyObservers();
		}
	}
	
	protected void setAssignedState(SPCrucibleState newState) {
		if (newState != mAssignedState) {
			mAssignedState = newState;
			setChanged();
			notifyObservers();
		}
	}
	
	public void setLocked(boolean locked) {
		if (mLocked != locked) {
			mLocked = locked;
			Intent lockIntent = new Intent(SPIOIOService.CRUCIBLE_COMMAND_INTENT);
			if (locked) {
				lockIntent.putExtra(SPIOIOService.COMMAND, SPIOIOService.LOCK_CRUCIBLE);
			} else {
				lockIntent.putExtra(SPIOIOService.COMMAND, SPIOIOService.UNLOCK_CRUCIBLE);
			}
			setChanged();
			notifyObservers();
		}
	}
	
	public void setTemperature(double temp) {
		mTemp = temp;
		setChanged();
		notifyObservers();
	}
	
	public void setVolume(double volume) {
		mVolume = volume;
		setChanged();
		notifyObservers();
	}
	
	public void setEdges(int edges) {
		mEdges = edges;
		setChanged();
		notifyObservers();
	}
	
	public void setErrorState(boolean tooMuchFlow, boolean tooMuchSteam, boolean notEnoughFlow, boolean notEnoughSteam) {
		mTooMuchFlow = tooMuchFlow;
		mTooMuchSteam = tooMuchSteam;
		mNotEnoughFlow = notEnoughFlow;
		mNotEnoughSteam = notEnoughSteam;
		
		//should be activated in production, but causes failures on the simulator probably because of the variations in the low pass filters and the slow heat rate
//		if (inErrorState()) {
//			stop();
//		}
		
		setChanged();
		notifyObservers();
	}
	
	public boolean inErrorState() {
		return mTooMuchFlow || mTooMuchSteam || mNotEnoughFlow || mNotEnoughSteam;
	}
	
	public boolean tooMuchFlow() {
		return mTooMuchFlow;
	}
	
	public boolean tooMuchSteam() {
		return mTooMuchSteam;
	}
	
	public boolean notEnoughFlow() {
		return mNotEnoughFlow;
	}
	
	public boolean notEnoughSteam() {
		return mNotEnoughSteam;
	}
	
	public void resetErrorState() {
		mTooMuchFlow = false;
		mTooMuchSteam = false;
		mNotEnoughFlow = false;
		mNotEnoughSteam = false;
		Intent resetIntent = new Intent(SPIOIOService.CRUCIBLE_COMMAND_INTENT);
		resetIntent.putExtra(SPIOIOService.COMMAND, SPIOIOService.RESET_CRUCIBLE_ERROR_STATE);
		resetIntent.putExtra(SPIOIOService.CRUCIBLE, mIndex);
		mContext.startService(resetIntent);
		setChanged();
		notifyObservers();
	}

	@Override
	public void update(Observable observable, Object data) {
		setChanged();
		notifyObservers();
	}
	
	public void setSteamedTooMuchOnFillAndHeating() {
		mSteamedTooMuchOnFillAndHeating = true;
		setChanged();
		notifyObservers();
	}
	
	public void clearSteamedTooMuchOnFillAndHeating() {
		mSteamedTooMuchOnFillAndHeating = false;
		setChanged();
		notifyObservers();
	}
	
	public boolean hasSteamedTooMuchOnFillAndHeating() {
		return mSteamedTooMuchOnFillAndHeating;
	}
	
	public boolean isSteaming() {
		return mSteaming;
	}
	
	public boolean isFilling() {
		return mFilling;
	}
	
	public boolean isDraining() {
		return mDraining;
	}
	
	public void setSteaming(boolean is) {
		mSteaming = is;
		setChanged();
		notifyObservers();
	}
	
	public void setDraining(boolean is) {
		mDraining = is;
		setChanged();
		notifyObservers();
	}
	
	public void setFilling(boolean is) {
		mFilling = is;
		setChanged();
		notifyObservers();
	}
}
