package com.alphadominche.steampunkhmi;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.content.Intent;
import android.util.LongSparseArray;
import android.view.Gravity;
import android.widget.Toast;
import com.alphadominche.steampunkhmi.utils.AccountSettings;
import com.alphadominche.steampunkhmi.utils.CleaningCycle;
import com.alphadominche.steampunkhmi.utils.MachineSettings;
import com.alphadominche.steampunkhmi.utils.SteampunkUtils;

public class SPModel extends Observable implements Observer {
    public final static long IOIO_CONNECTION_TIMEOUT = 3000000000L;
    public final static double MAX_BOILER_TEMP_F = 275.0;
    public final static double MIN_BOILER_TEMP_F = 180.0;
    public final static double MAX_BOILER_TEMP_C = 135.0;
    public final static double MIN_BOILER_TEMP_C = 82.0;
    public final static double MAX_CLEANING_TEMP_F = 210.0;
    public final static double MIN_CLEANING_TEMP_F = 180.0;
    public final static double MAX_CLEANING_TEMP_C = 99.0;
    public final static double MIN_CLEANING_TEMP_C = 82.0;
    public final static double DEFAULT_BOILER_TEMP_F = 265.0;
    public final static double DEFAULT_BOILER_TEMP_C = 129.0;
    public final static int BOILER_TEMP_PRECISION = 1;
    public final static double MAX_RINSE_TEMP_F = 202.0;
    public final static double MIN_RINSE_TEMP_F = 140.0;
    public final static double MAX_RINSE_TEMP_C = 94.0;
    public final static double MIN_RINSE_TEMP_C = 60.0;
    public final static double DEFAULT_RINSE_TEMP_F = 195.0;
    public final static double DEFAULT_RINSE_TEMP_C = 90.0;
    public final static int RINSE_TEMP_PRECISION = 1;
    public final static double MAX_RINSE_VOL_OZ = 16.0;
    public final static double MIN_RINSE_VOL_OZ = 0.0;
    public final static double MAX_RINSE_VOL_ML = 475.0;
    public final static double MIN_RINSE_VOL_ML = 0.0;
    public final static double DEFAULT_RINSE_VOL_OZ = 12.0;
    public final static double DEFAULT_RINSE_VOL_ML = 355.0;
    public final static int RINSE_VOL_PRECISION = 0;
    public final static SPTempUnitType DEFAULT_TEMP_UNITS = SPTempUnitType.FAHRENHEIT;
    public final static SPVolumeUnitType DEFAULT_VOL_UNITS = SPVolumeUnitType.OUNCES;
    public final static double DEFAULT_ELEVATION_FT = 4429;
    public final static double DEFAULT_ELEVATION_M = 1350.0;
    public final static double DEFAULT_CLEANING_TEMP_F = 200.0;
    public final static double DEFAULT_CLEANING_TEMP_C = 93.0;
    ;
    public final static double DEFAULT_CLEANING_VOL_OZ = 12.0;
    public final static double DEFAULT_CLEANING_VOL_ML = 355.0;
    public final static double SAVING_RECIPE_DELAY = 1.0;
    public final static double VACUUM_BREAK_MIN = 0.0;
    public final static double VACUUM_BREAK_MAX = 10.0;
    public final static int EXTRACTION_MIN = 0;
    public final static int EXTRACTION_MAX = 300;
    public final static int MIN_AGITATION_COUNT = 1;
    public final static int MAX_AGITATION_COUNT = 10;
    public final static double MIN_AGITATION_LENGTH = 0.0;
    public final static double MAX_AGITATION_LENGTH = 15.0;
    public final static double MIN_AGITATION_PULSE_WIDTH = 0.0;
    public final static double MAX_AGITATION_PULSE_WIDTH = 1.0;

    public final static long SAVE_RECIPE_NOTIFICATION_DELAY = 1000L;

    private static Context sContext = null;
    private static SPModel sInstance = null;

    public static Context getContext() {
        return sContext;
    }

    public static SPModel getInstance(Context context) {
        if (sContext == null && context != null) {
            sContext = context.getApplicationContext();
        }

        if (sInstance == null) {
            sInstance = new SPModel();
        }
        return sInstance;
    }

    private int mCrucibleCount;

    private ArrayList<SPCrucibleModel> mCrucibles;
    private ArrayList<ArrayList<SPCrucibleObserver>> mObservers;
    private ArrayList<IOIOConnectionObserver> mConnectionObservers;
    private SPRecipe mCurrentlyEditedRecipe;
    private double mBoilerTargetTemp;
    private double mRinseTemp;
    private double mRinseVolume;
    private SPTempUnitType mTempUnits;
    private SPVolumeUnitType mVolumeUnits;
    private double mElevation;
    private ArrayList<Long> mRecipesSelected;
    private String mUsername;
    private String mEmail;
    private String mSerialNum;
    private String mPassword;
    private String mAddress;
    private String mCity;
    private String mState;
    private String mZip;
    private Boolean mProtectRecipes;
    private boolean mBoilerHeating;
    private boolean mBoilerFilling;
    private double mBoilerCurrentTemp;
    private double mCleaningTemp;
    private double mCleaningVol;
    private long mLastConnectionNotification;

    private LongSparseArray<SPRecipe> mAssignedRecipes;

    private SPUser mUser;

    private long mSavingRecipe;
    private boolean mStillSavingRecipe;
    private ArrayList<SaveRecipeListener> mSaveRecipeListeners;
    private boolean mConnectedToNetwork;
    private boolean mConnectedToIOIO;

    SPModel() { //SPLog.debug("SPModel instantiated!");
        mSavingRecipe = -1L;
        mStillSavingRecipe = false;
        mSaveRecipeListeners = new ArrayList<SaveRecipeListener>();

        mAssignedRecipes = new LongSparseArray<SPRecipe>();

        mConnectionObservers = new ArrayList<IOIOConnectionObserver>();

        mCurrentlyEditedRecipe = null;

        mBoilerCurrentTemp = 0;
        mConnectedToIOIO = false;
        mLastConnectionNotification = 0;

        // Cleaning Cycle
        CleaningCycle cleaningCycle = CleaningCycle.getCleaningCycleFromSharedPreferences(sContext);

        mCleaningTemp = cleaningCycle.getTemperature() != null ? cleaningCycle.getTemperature() : SPServiceThermistor.convertFromTempToTemp(SPTempUnitType.FAHRENHEIT, SPTempUnitType.KELVIN, DEFAULT_CLEANING_TEMP_F);
        mCleaningVol = cleaningCycle.getVolume() != null ? cleaningCycle.getVolume() : SPFlowMeter.convertFromOuncesToMilliliters(DEFAULT_CLEANING_VOL_OZ);

        // Machine Settings
        MachineSettings machine = MachineSettings.getMachineSettingsFromSharedPreferences(sContext);

        mCrucibleCount = machine.getCrucibleCount() != null ? machine.getCrucibleCount() : SPIOIOService.MAX_CRUCIBLE_COUNT;
        mBoilerTargetTemp = machine.getBoilerTemp() != null ? machine.getBoilerTemp() : DEFAULT_BOILER_TEMP_F;
        mRinseTemp = machine.getRinseTemp() != null ? machine.getRinseTemp() : DEFAULT_RINSE_TEMP_F;
        mRinseVolume = machine.getRinseVolume() != null ? machine.getRinseVolume() : DEFAULT_RINSE_VOL_OZ;
        mTempUnits = machine.getTempUnitType() != null ? machine.getTempUnitType() : DEFAULT_TEMP_UNITS;
        mVolumeUnits = machine.getVolumeUnitType() != null ? machine.getVolumeUnitType() : DEFAULT_VOL_UNITS;
        mElevation = machine.getElevation() != null ? machine.getElevation() : DEFAULT_ELEVATION_FT;
        mConnectedToNetwork = !machine.isLocalOnly();
        //SPLog.debug("mConnectedToNetwork: " + mConnectedToNetwork + " isLocalOnly: " + machine.isLocalOnly());

        ArrayList<Boolean> crucibleStates = (ArrayList<Boolean>) machine.getCrucibleStates();

        if (crucibleStates == null || crucibleStates.size() != mCrucibleCount) {
            crucibleStates = new ArrayList<Boolean>();
            for (int i = 0; i < mCrucibleCount; i++) {
                crucibleStates.add(i, false);
            }
        }

        // Account Settings
        AccountSettings account = AccountSettings.getAccountSettingsFromSharedPreferences(sContext);

        mUsername = account.getUsername() != null ? account.getUsername() : "";
        mEmail = account.getEmail() != null ? account.getEmail() : "";
        mAddress = account.getAddress() != null ? account.getAddress() : "";
        mCity = account.getCity() != null ? account.getCity() : "";
        mState = account.getState() != null ? account.getState() : "";
        mZip = account.getZipCode() != null ? account.getZipCode() : "";
        mProtectRecipes = account.getProtectRecipes() != null ? account.getProtectRecipes() : true;
        mPassword = "somethingcool";

        mObservers = new ArrayList<ArrayList<SPCrucibleObserver>>();
        for (int i = 0; i < mCrucibleCount; i++) {
            mObservers.add(new ArrayList<SPCrucibleObserver>()); //one list of observers per crucible
        }
        mCrucibles = new ArrayList<SPCrucibleModel>();

        // don't persist selected recipes, just start w/ empty crucibles
        mRecipesSelected = new ArrayList<Long>();
        for (int i = 0; i < /*mCrucibleCount*/SPIOIOService.MAX_CRUCIBLE_COUNT; i++) {
            mRecipesSelected.add(-1L);
            mCrucibles.add(new SPCrucibleModel(null, sContext, i));
            mCrucibles.get(i).addObserver(this);
        }

        for (int i = 0; i < SPIOIOService.MAX_CRUCIBLE_COUNT; i++) {
            setRecipeForCrucible(i, SteampunkUtils.getRecipeForCrucible(sContext, i));
        }

        // Set locked state of crucibles
        if (crucibleStates != null) {
            for (int i = 0; i < crucibleStates.size(); i++) {
                setCrucibleLocked(i, crucibleStates.get(i));
            }
        }

        Intent startIntent = new Intent(SPIOIOService.START_SPIOIO_SERVICE_INTENT);
        startIntent.putExtra(SPIOIOService.SET_CRUCIBLE_COUNT, true);
        startIntent.putExtra(SPIOIOService.CRUCIBLE_COUNT, mCrucibleCount);
        sContext.startService(startIntent);
//		Intent startTemps = new Intent(SPIOIOService.START_SPIOIO_SERVICE_INTENT);
//		sContext.startService(startTemps);;

        String userType = SteampunkUtils.getCurrentSteampunkUserType(sContext);
        long userId = SteampunkUtils.getCurrentSteampunkUserId(sContext);
        mUser = new SPUser(userId, "name", userType);

        Thread connectionWatchThread = new Thread() {
            @Override
            public void run() {
                while (true) { //SPLog.debug("checking for connectivity");
                    if (System.nanoTime() - mLastConnectionNotification > IOIO_CONNECTION_TIMEOUT) { //if it's been too long, the connection is lost!
                        setIOIOConnectionStatus(false);
                    }

                    try {
                        sleep(2000);
                    } catch (InterruptedException e) {
                        setIOIOConnectionStatus(false);
                    }
                }
            }
        };
        connectionWatchThread.start();
    }

    public SPRecipe getRecipeFromId(long id) {
        if (1 > id)
            return null;
        SPRecipe newRecipe = null;
        try {
            newRecipe = new SPRecipe(id, sContext);
        } catch (IndexOutOfBoundsException err) {
            newRecipe = null;
        }
        return newRecipe;
    }

    public SPRecipe getCurrentlyEditedRecipe() {
        return mCurrentlyEditedRecipe;
    }

    public void logoutPrep() {
        for (int i = 0; i < mCrucibleCount; i++) {
            mCrucibles.get(i).stop();
            setRecipeForCrucible(i, -1);
        }
    }

    public void setCurrentlyEditedRecipe(SPRecipe newRecipe) {
        if (mCurrentlyEditedRecipe != null) {
            mCurrentlyEditedRecipe = null;
            if (!recipeIsAssigned(mCurrentlyEditedRecipe)) {
                mAssignedRecipes.remove(mCurrentlyEditedRecipe.getId());
            }
        }

        if (newRecipe != null) {
            mAssignedRecipes.put(newRecipe.getId(), newRecipe);
        }

        mCurrentlyEditedRecipe = newRecipe;
    }

    public void addCrucibleObserver(SPCrucibleObserver o, int index) {
        if (index >= mCrucibleCount) return;
        if (!mObservers.get(index).contains(o)) {
            mObservers.get(index).add(o);
        }
    }

    public void removeCrucibleObserver(SPCrucibleObserver o) {
        for (int i = 0; i < mObservers.size(); i++) {
            if (mObservers.get(i).contains(o)) {
                mObservers.get(i).remove(o);
            }
        }
    }

    private void notifyCrucibleObservers(Object o) {
        for (int i = 0; i < mCrucibleCount; i++) {
            if (mCrucibles.get(i) == o) {
                for (int j = 0; j < mObservers.get(i).size(); j++) {
                    mObservers.get(i).get(j).notifyOfCrucibleChange(i);
                }
                break;
            }
        }
    }

    public void setContext(SPCruciblesActivity c) {
        sContext = c.getApplicationContext();
    }

    public SPUser getRecipeRoasterForCrucible(int crucibleIndex) {
        if (null == mCrucibles.get(crucibleIndex)) {
            return null;
        }
        return mCrucibles.get(crucibleIndex).getRoaster();
    }

    public String getRecipeCoffeeForCrucible(int crucibleIndex) {
        return mCrucibles.get(crucibleIndex).getCoffee();
    }

    public int getCurrentStackForCrucible(int crucibleIndex) {
        return mCrucibles.get(crucibleIndex).getCurrentStack();
    }

    public int getRecipeTimeForCrucible(int crucibleIndex, int stackIndex) {
        return mCrucibles.get(crucibleIndex).getRecipeTime(stackIndex);
    }

    public int getTimeLeftForCrucible(int crucibleIndex) {
        return mCrucibles.get(crucibleIndex).getTimeLeft();
    }

    public double getRecipeTempForCrucible(int crucibleIndex, int stackIndex) {
        return mCrucibles.get(crucibleIndex).getRecipeTemp(stackIndex);
    }

    public double getTargetTempForCrucible(int crucibleIndex) {
        return mCrucibles.get(crucibleIndex).getRecipeTemp(mCrucibles.get(crucibleIndex).getCurrentStack());
    }

    public double getTempForCrucible(int crucibleIndex) {
        return mCrucibles.get(crucibleIndex).getTemp();
    }

    public double getRecipeVolumeForCrucible(int crucibleIndex, int stackIndex) {
        return mCrucibles.get(crucibleIndex).getRecipeVolume(stackIndex);
    }

    public double getVolumeForCrucible(int crucibleIndex) {
        return mCrucibles.get(crucibleIndex).getVolume();
    }

    public int getEdgesForCrucible(int crucibleIndex) {
        return mCrucibles.get(crucibleIndex).getEdges();
    }

    public double[] getAgitationCyclesForCrucible(int crucibleIndex, int stackIndex) {
        return mCrucibles.get(crucibleIndex).getAgitationCycles(stackIndex);
    }

    public int[] getAgitationStartTimesForCrucible(int crucibleIndex, int stackIndex) {
        return mCrucibles.get(crucibleIndex).getAgitationStartTimes(stackIndex);
    }

    public double[] getAgitationCyclePulseWidthsForCrucible(int crucibleIndex, int stackIndex) {
        return mCrucibles.get(crucibleIndex).getAgitationPulseWidths(stackIndex);
    }

    public int getCurrAgitationCycleIndexForCrucible(int crucibleIndex) {
        return mCrucibles.get(crucibleIndex).getAgitationIndex();
    }

    public int getCurrAgitationCycleTimeLeftForCrucible(int crucibleIndex) {
        return mCrucibles.get(crucibleIndex).getAgitationTimeLeft();
    }

    public double getRecipeGrindForCrucible(int crucibleIndex) {
        return mCrucibles.get(crucibleIndex).getGrind();
    }

    public String getRecipeFilterForCrucible(int crucibleIndex) {
        return mCrucibles.get(crucibleIndex).getFilter();
    }

    public int getRecipeExtractionTimeForCrucible(int crucibleIndex, int stackIndex) {
        return mCrucibles.get(crucibleIndex).getRecipeExtractionTime(stackIndex);
    }

    public int getExtractionTimeLeftForCrucible(int crucibleIndex) {
        return mCrucibles.get(crucibleIndex).getExtractionTimeLeft();
    }

    public void setExtractionTimeLeftForCrucible(int crucibleIndex, int time) {
        mCrucibles.get(crucibleIndex).setExtractionTimeLeft(time);
    }

    public SPCrucibleState getStateForCrucible(int crucibleIndex) {
        return mCrucibles.get(crucibleIndex).getState();
    }

    public SPRecipe getRecipeForCrucible(int crucibleIndex) {
        return mCrucibles.get(crucibleIndex).getRecipe();
    }

    public void setRecipeForCrucible(int crucibleIndex, long id) {
        SPRecipe newRecipe = null;
        if (id <= 0L) {
            newRecipe = null; //0 and -1 map to nothing
        } else if (mAssignedRecipes.get(id) == null) {
            newRecipe = getRecipeFromId(id);
            mAssignedRecipes.put(id, newRecipe);
        } else {
            newRecipe = mAssignedRecipes.get(id);
        }

        SPRecipe replacedRecipe = mCrucibles.get(crucibleIndex).getRecipe(); //mAssignedRecipes.get();
        mCrucibles.get(crucibleIndex).setRecipe(null);
        if (replacedRecipe != null) {
            long replacedRecipeId = replacedRecipe.getId();
            if (!recipeIsAssigned(replacedRecipe)) {
                mAssignedRecipes.remove(replacedRecipeId);
            }
        }

        if (newRecipe == null || newRecipe.getStackCount() == 0) { //the recipe either doesn't exist or is bad (likely an old v2 recipe)...can't assign it!
            id = -1L;
            newRecipe = null;
        }

        SteampunkUtils.saveRecipeForCrucible(sContext, crucibleIndex, id);
        setRecipeForCrucible(crucibleIndex, newRecipe);
    }

    private boolean recipeIsAssigned(SPRecipe recipe) {
        for (int i = 0; i < mCrucibles.size(); i++) {
            if (mCrucibles.get(i).getRecipe() == recipe) {
                return true;
            }
        }
        if (mCurrentlyEditedRecipe == recipe) {
            return true;
        }

        return false;
    }

    public void setRecipeForCrucible(int crucibleIndex, SPRecipe recipe) {
        mCrucibles.get(crucibleIndex).setRecipe(recipe);
    }

    public void fillAndHeatBeverageWaterOnCrucible(int crucibleIndex) {
        mCrucibles.get(crucibleIndex).fillAndHeatBeverageWater();
    }

    public void brewBeverageOnCrucible(int crucibleIndex) {
        mCrucibles.get(crucibleIndex).brewBeverage();
    }

    public void rinseCrucible(int crucibleIndex) {
        mCrucibles.get(crucibleIndex).rinseCrucible();
    }

    public void startCleaningCrucible(int crucibleIndex) {
        mCrucibles.get(crucibleIndex).startCleaningCrucible();
    }

    public void finishCleaningCrucible(int crucibleIndex) {
        mCrucibles.get(crucibleIndex).finishCleaningCrucible();
    }

    public void stopBrewingOnCrucible(int crucibleIndex) {
        mCrucibles.get(crucibleIndex).stop();
    }

    @Override
    public void update(Observable o, Object data) {
        if (o.getClass() == mCrucibles.get(0).getClass()) {
            notifyCrucibleObservers(o);
        }
    }

    public void updateCrucibleState(
            int crucibleIndex,
            boolean fill,
            boolean steam,
            boolean drain,
            double temp,
            double volume,
            int edges,
            SPCrucibleState state,
            int timeLeftInBrew) {
        SPCrucibleModel c = mCrucibles.get(crucibleIndex);
        c.setState(state);
        c.setTemperature(temp);
        c.setVolume(volume);
        c.setEdges(edges);
        c.setTimeLeftInBrew(timeLeftInBrew);
        c.setSteaming(steam);
        c.setFilling(fill);
        c.setDraining(drain);
    }

    public int getCrucibleTimeLeftInBrew(int crucibleIndex) {
        return mCrucibles.get(crucibleIndex).getTimeLeftInBrew();
    }

    public void updateCrucibleErrorState(int crucibleIndex, boolean tooMuchFlow, boolean tooMuchSteam, boolean notEnoughFlow, boolean notEnoughSteam) {
        mCrucibles.get(crucibleIndex).setErrorState(tooMuchFlow, tooMuchSteam, notEnoughFlow, notEnoughSteam);
    }

    public boolean crucibleInErrorState(int crucibleIndex) {
        return mCrucibles.get(crucibleIndex).inErrorState();
    }

    public boolean crucibleHasTooMuchFlow(int crucibleIndex) {
        return mCrucibles.get(crucibleIndex).tooMuchFlow();
    }

    public boolean crucibleHasTooMuchSteam(int crucibleIndex) {
        return mCrucibles.get(crucibleIndex).tooMuchSteam();
    }

    public boolean crucibleNotEnoughFlow(int crucibleIndex) {
        return mCrucibles.get(crucibleIndex).notEnoughFlow();
    }

    public boolean crucibleNotEnoughSteam(int crucibleIndex) {
        return mCrucibles.get(crucibleIndex).notEnoughSteam();
    }

    public void resetCrucibleErrorState(int crucibleIndex) {
        mCrucibles.get(crucibleIndex).resetErrorState();
    }

    public int getCrucibleCount() {
        return mCrucibleCount;
    }

    public void setCrucibleCount(int newCount) {
        mCrucibleCount = newCount;
        setChanged();
        notifyObservers();
    }

    public void setCrucibleLocked(int index, boolean locked) {
        mCrucibles.get(index).setLocked(locked);
    }

    public boolean isCrucibleLocked(int crucibleIndex) {
        return mCrucibles.get(crucibleIndex).isLocked();
    }

    public void setUnits(SPTempUnitType tempUnit, SPVolumeUnitType volumeUnit) {
        if (mTempUnits != tempUnit) {
            mTempUnits = tempUnit;
        }
        if (mVolumeUnits != volumeUnit) {
            mVolumeUnits = volumeUnit;
        }
        setCleaningSettings(mCleaningTemp, mCleaningVol);
        setChanged();
        notifyObservers();
    }

    public boolean setMachineSettings(double boilerTemp, double rinseTemp, double rinseVol) {
        boolean sendIntent = false;

        double mMinBoilerTemp, mMaxBoilerTemp;
        double mMinRinseTemp, mMaxRinseTemp;
        double mMinRinseVol, mMaxRinseVol;

        if (mTempUnits == SPTempUnitType.FAHRENHEIT) {
            mMinBoilerTemp = MIN_BOILER_TEMP_F;
            mMaxBoilerTemp = MAX_BOILER_TEMP_F;
            mMinRinseTemp = MIN_RINSE_TEMP_F;
            mMaxRinseTemp = MAX_RINSE_TEMP_F;
        } else {
            mMinBoilerTemp = MIN_BOILER_TEMP_C;
            mMaxBoilerTemp = MAX_BOILER_TEMP_C;
            mMinRinseTemp = MIN_RINSE_TEMP_C;
            mMaxRinseTemp = MAX_RINSE_TEMP_C;
        }

        if (mVolumeUnits == SPVolumeUnitType.OUNCES) {
            mMinRinseVol = MIN_RINSE_VOL_OZ;
            mMaxRinseVol = MAX_RINSE_VOL_OZ;
        } else {
            mMinRinseVol = MIN_RINSE_VOL_ML;
            mMaxRinseVol = MAX_RINSE_VOL_ML;
        }

        if (boilerTemp >= mMinBoilerTemp && boilerTemp <= mMaxBoilerTemp) {
            mBoilerTargetTemp = boilerTemp;
            sendIntent = true;
        }

        if (rinseTemp >= mMinRinseTemp && rinseTemp <= mMaxRinseTemp) {
            mRinseTemp = rinseTemp;
            sendIntent = true;
        }
        if (rinseVol >= mMinRinseVol && rinseVol <= mMaxRinseVol) {
            mRinseVolume = rinseVol;
            sendIntent = true;
        }
        if (true == sendIntent) {
            Intent machineSettings = new Intent(SPIOIOService.MACHINE_COMMAND_INTENT);
            machineSettings.putExtra(SPIOIOService.COMMAND, SPIOIOService.MACHINE_SETTINGS);
            double machineRinseVolume = mRinseVolume;
            if (mVolumeUnits == SPVolumeUnitType.OUNCES) {
                machineRinseVolume = SPFlowMeter.convertFromOuncesToMilliliters(machineRinseVolume);
            }
            machineSettings.putExtra(SPIOIOService.RINSE_VOLUME, machineRinseVolume);
            double machineRinseTemp = SPServiceThermistor.convertFromTempToTemp(mTempUnits, SPTempUnitType.KELVIN, mRinseTemp);
            machineSettings.putExtra(SPIOIOService.RINSE_TEMP, machineRinseTemp);
            double machineBoilerTemp = SPServiceThermistor.convertFromTempToTemp(mTempUnits, SPTempUnitType.KELVIN, mBoilerTargetTemp);
            machineSettings.putExtra(SPIOIOService.BOILER_TEMP, machineBoilerTemp);
            sContext.startService(machineSettings);
            return true;
        }
        return false;
    }

    public boolean setCleaningSettings(double temp, double volume) {
        mCleaningTemp = temp;
        mCleaningVol = volume;

        CleaningCycle cycle = new CleaningCycle(mCleaningTemp, mCleaningVol);
        CleaningCycle.writeCleaningCycleToSharedPreferences(cycle, sContext);

        setChanged();
        notifyObservers();
        return true;
    }

    public void setBoilerStatus(boolean heating, boolean filling, double temp, boolean running, String error) {
        mBoilerHeating = heating;
        mBoilerFilling = filling;
        mBoilerCurrentTemp = temp;
        setChanged();
        notifyObservers();
    }

    public ArrayList<Long> getSelectedRecipes() {
        ArrayList<Long> selected = new ArrayList<Long>();
        for (int i = 0; i < mCrucibles.size(); i++) {
            if (mCrucibles.get(i).getRecipe() != null) {
                selected.add(mCrucibles.get(i).getRecipe().getId());
            } else {
                selected.add(-1L);
            }
        }
        return selected;
    }

    public void setSelectedRecipes(ArrayList<Long> selected) {
        for (int i = 0; i < mCrucibleCount; i++) {
            setRecipeForCrucible(i, selected.get(i));
        }
    }

    public double getElevation() {
        return mElevation;
    }

    public void setElevation(double elevation) {
        mElevation = elevation;
        setChanged();
        notifyObservers();
    }

    public SPTempUnitType getTempUnits() {
        return mTempUnits;
    }

    public void setTempUnits(SPTempUnitType unit) {
        mTempUnits = unit;
        setChanged();
        notifyObservers();
    }

    public SPVolumeUnitType getVolumeUnits() {
        return mVolumeUnits;
    }

    public void setVolumeUnits(SPVolumeUnitType unit) {
        mVolumeUnits = unit;
        setChanged();
        notifyObservers();
    }

    public double getBoilerTargetTemp() {
        return mBoilerTargetTemp;
    }

    public double getBoilerCurrentTemp() {
        return mBoilerCurrentTemp;
    }

    public double getRinseTemp() {
        return mRinseTemp;
    }

    public double getRinseVolume() {
        return mRinseVolume;
    }

    public double getCleaningTemp() {
        return mCleaningTemp;
    }

    public double getCleaningVolume() {
        return mCleaningVol;
    }

    public void setAccountSettings(String name, String email, String password, String address, String city, String state, String zip, Boolean protect) {
        mUsername = name;
        mEmail = email;
        mPassword = password;
        mAddress = address;
        mCity = city;
        mState = state;
        mZip = zip;
        mProtectRecipes = protect;
        setChanged();
        notifyObservers();
    }

    public String getName() {
        return mUsername;
    }

    public String getEmail() {
        return mEmail;
    }

    public String getSerialNum() {
        return mSerialNum;
    }

    public void setSerialNum(String num) {
        mSerialNum = num;
    }

    public String getPassword() {
        return mPassword;
    }

    public String getAddress() {
        return mAddress;
    }

    public String getCity() {
        return mCity;
    }

    public String getState() {
        return mState;
    }

    public String getZip() {
        return mZip;
    }

    public Boolean getProtectRecipes() {
        return mProtectRecipes;
    }

    public SPUser getUser() {
        return mUser;
    }

    public void setUser(SPUser user) {
        mUser = user;
        setChanged();
        notifyObservers();
    }

    public void updateCrucibleRecipes() {
        for (int i = 0; i < mCrucibles.size(); i++) {
            if (mCrucibles.get(i).getRecipe() == null || mCrucibles.get(i).getRecipe().getId() == -1) {
                continue;
            } else {
                mCrucibles.get(i).setRecipe(new SPRecipe(mCrucibles.get(i).getRecipe().getId(), sContext));
            }

        }

    }

    public SPRecipe getRecipeById(long id) {
        return mAssignedRecipes.get(id);
    }

    public void networkError(Context context) {
        Toast toast = Toast.makeText(context,
                context.getResources().getString(R.string.bad_serial_number_message),
                Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 0, 0);
        toast.show();
    }


    //save recipe delay bandaid
    public static interface SaveRecipeListener {
        public void notifyRecipeFinishedSaving();
    }

    public long getSavingRecipe() {
        return mSavingRecipe;
    }

    public void setSavingRecipe(long id) {
        mSavingRecipe = id;
        mStillSavingRecipe = true;
        Timer savingRecipeTimer = new Timer();
        savingRecipeTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                mStillSavingRecipe = false;
                notifySaveRecipeListeners();
            }
        }, SAVE_RECIPE_NOTIFICATION_DELAY);
    }

    public void notifySaveRecipeListeners() {
        for (int i = 0; i < mSaveRecipeListeners.size(); i++) {
            mSaveRecipeListeners.get(i).notifyRecipeFinishedSaving();
        }
    }

    public void addSaveRecipeListener(SaveRecipeListener listener) {
        mSaveRecipeListeners.add(listener);
    }

    public void removeSaveRecipeListener(SaveRecipeListener listener) {
        mSaveRecipeListeners.remove(listener);
    }

    public boolean stillSavingRecipe() {
        return mStillSavingRecipe;
    }

    public void setCrucibleSteamedTooMuchOnFillAndHeating(int crucibleIndex) {
        mCrucibles.get(crucibleIndex).setSteamedTooMuchOnFillAndHeating();
    }

    public void clearCrucibleSteamedTooMuchOnFillAndHeating(int crucibleIndex) {
        mCrucibles.get(crucibleIndex).clearSteamedTooMuchOnFillAndHeating();
    }

    public boolean crucibleHasSteamedTooMuchOnFillAndHeating(int crucibleIndex) {
        return mCrucibles.get(crucibleIndex).hasSteamedTooMuchOnFillAndHeating();
    }

    public boolean isBoilerHeating() {
        return mBoilerHeating;
    }

    public boolean isBoilerFilling() {
        return mBoilerFilling;
    }

    public void turnBoilerHeatOn() {
        Intent heatIntent = new Intent();
        heatIntent.setAction(SPIOIOService.MANUAL_MODE_COMMAND_INTENT);
        heatIntent.putExtra(SPIOIOService.COMMAND, SPIOIOService.START_FORCE_BOILER_HEAT_ON);
        sContext.startService(heatIntent);
    }

    public void turnBoilerHeatOff() {
        Intent heatIntent = new Intent();
        heatIntent.setAction(SPIOIOService.MANUAL_MODE_COMMAND_INTENT);
        heatIntent.putExtra(SPIOIOService.COMMAND, SPIOIOService.START_FORCE_BOILER_HEAT_OFF);
        sContext.startService(heatIntent);
    }

    public boolean isCrucibleFilling(int crucibleIndex) {
        return mCrucibles.get(crucibleIndex).isFilling();
    }

    public boolean isCrucibleSteaming(int crucibleIndex) {
        return mCrucibles.get(crucibleIndex).isSteaming();
    }

    public boolean isCrucibleDraining(int crucibleIndex) {
        return mCrucibles.get(crucibleIndex).isDraining();
    }

    public void turnCrucibleFillOn(int crucibleIndex) {
        Intent fillIntent = new Intent();
        fillIntent.setAction(SPIOIOService.MANUAL_MODE_COMMAND_INTENT);
        fillIntent.putExtra(SPIOIOService.COMMAND, SPIOIOService.START_FORCE_FILL);
        fillIntent.putExtra(SPIOIOService.CRUCIBLE, crucibleIndex);
        sContext.startService(fillIntent);
    }

    public void turnCrucibleFillOff(int crucibleIndex) {
        Intent fillIntent = new Intent();
        fillIntent.setAction(SPIOIOService.MANUAL_MODE_COMMAND_INTENT);
        fillIntent.putExtra(SPIOIOService.COMMAND, SPIOIOService.STOP_FORCE_FILL);
        fillIntent.putExtra(SPIOIOService.CRUCIBLE, crucibleIndex);
        sContext.startService(fillIntent);
    }

    public void turnCrucibleSteamOn(int crucibleIndex) {
        Intent heatIntent = new Intent();
        heatIntent.setAction(SPIOIOService.MANUAL_MODE_COMMAND_INTENT);
        heatIntent.putExtra(SPIOIOService.COMMAND, SPIOIOService.START_FORCE_STEAM);
        heatIntent.putExtra(SPIOIOService.CRUCIBLE, crucibleIndex);
        sContext.startService(heatIntent);
    }

    public void turnCrucibleSteamOff(int crucibleIndex) {
        Intent heatIntent = new Intent();
        heatIntent.setAction(SPIOIOService.MANUAL_MODE_COMMAND_INTENT);
        heatIntent.putExtra(SPIOIOService.COMMAND, SPIOIOService.STOP_FORCE_STEAM);
        heatIntent.putExtra(SPIOIOService.CRUCIBLE, crucibleIndex);
        sContext.startService(heatIntent);
    }

    public void turnCrucibleDrainOn(int crucibleIndex) {
        Intent heatIntent = new Intent();
        heatIntent.setAction(SPIOIOService.MANUAL_MODE_COMMAND_INTENT);
        heatIntent.putExtra(SPIOIOService.COMMAND, SPIOIOService.FORCE_DRAIN_OPEN);
        heatIntent.putExtra(SPIOIOService.CRUCIBLE, crucibleIndex);
        sContext.startService(heatIntent);
    }

    public void turnCrucibleDrainOff(int crucibleIndex) {
        Intent heatIntent = new Intent();
        heatIntent.setAction(SPIOIOService.MANUAL_MODE_COMMAND_INTENT);
        heatIntent.putExtra(SPIOIOService.COMMAND, SPIOIOService.STOP_FORCE_DRAIN_OPEN);
        heatIntent.putExtra(SPIOIOService.CRUCIBLE, crucibleIndex);
        sContext.startService(heatIntent);
    }

    public boolean isConnectedToIOIO() { //SPLog.debug("returning connection status");
        return mConnectedToIOIO;
    }

    public void startManualMode() {
        Intent startManualIntent = new Intent();
        startManualIntent.setAction(SPIOIOService.MANUAL_MODE_COMMAND_INTENT);
        startManualIntent.putExtra(SPIOIOService.COMMAND, SPIOIOService.START_MANUAL_MODE);
        sContext.startService(startManualIntent);
    }

    public void stopManualMode() {
        Intent stopManualIntent = new Intent();
        stopManualIntent.setAction(SPIOIOService.MANUAL_MODE_COMMAND_INTENT);
        stopManualIntent.putExtra(SPIOIOService.COMMAND, SPIOIOService.STOP_MANUAL_MODE);
        sContext.startService(stopManualIntent);
    }

    public void setNetworkConnectionStatus(boolean is) {
        mConnectedToNetwork = is; //SPLog.debug("set network connection to: " + is + " in model");
        setChanged();
        notifyObservers();
    }

    public void setIOIOConnectionStatus(boolean is) { //SPLog.debug("setting connection status to: " + is);
        mConnectedToIOIO = is; //SPLog.debug("set IOIO connection to: " + is + " in model");
        mLastConnectionNotification = System.nanoTime();
        notifyConnectionObservers();
    }

    public boolean isConnectedToNetwork() {
        return mConnectedToNetwork;
    }

    //IOIOConnectionObserver
    public void addConnectionObserver(IOIOConnectionObserver observer) {
        mConnectionObservers.add(observer);
    }

    public void removeConnectionObserver(IOIOConnectionObserver observer) {
        mConnectionObservers.remove(observer);
    }

    public void notifyConnectionObservers() {
        for (int i = 0; i < mConnectionObservers.size(); i++) {
            mConnectionObservers.get(i).notifyOfConnectionStatus();
        }
    }

    public interface IOIOConnectionObserver {
        public void notifyOfConnectionStatus();
    }
}
