package com.alphadominche.steampunkhmi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import com.alphadominche.steampunkhmi.database.tables.StackTable;

public class SPRecipeStack extends Observable implements Observer {
    private long mId;
    private double mTemperature;
    private SPTempUnitType mTempType;
    private double mVolume;
    private SPVolumeUnitType mVolumeType;
    private int mTotalTime;
    private double mVacuumBreak;
    private int mExtractionTime;
    private ArrayList<SPRecipeAgitation> mAgitations;

    SPRecipeStack() {
        mId = -1L;
        mAgitations = new ArrayList<SPRecipeAgitation>();
    }

    SPRecipeStack(long id) {
        mId = id;
        mAgitations = new ArrayList<SPRecipeAgitation>();
    }

    public SPRecipeStack(SPRecipeStack stack) {
        mAgitations = new ArrayList<SPRecipeAgitation>();
        this.copyStackIntoSelf(stack);
    }

    public SPRecipeStack(JSONObject json) throws JSONException {
//		setId(json.getLong(StackTable.ID));
        setVolume(json.getDouble(StackTable.VOLUME));
        setTemperature(json.getDouble(StackTable.TEMPERATURE));
        setVacuumBreak(json.getDouble(StackTable.VACUUM_BREAK));
        setExtractionTime(json.getInt(StackTable.PULL_DOWN_TIME));
        setTotalTime(json.getInt(StackTable.DURATION));
        mAgitations = new ArrayList<SPRecipeAgitation>();
        JSONArray ags = json.getJSONArray(SPRecipe.AGITATIONS);
        for (int i = 0; i < ags.length(); i++) {
            SPRecipeAgitation newAg = new SPRecipeAgitation(ags.getJSONObject(i));
            newAg.addObserver(this);
            mAgitations.add(newAg);
        }

        sortAgs();
    }

    private void sortAgs() { // O(n^2), but n is really small
        for (int i = 0; i < mAgitations.size(); i++) {
            swapEarliestAtOrAfter(i);
        }
    }

    private void swapEarliestAtOrAfter(int index) {
        for (int i = index; i < mAgitations.size(); i++) {
            if (mAgitations.get(i).getStartTime() < mAgitations.get(index).getStartTime()) {
                SPRecipeAgitation temp = mAgitations.get(i);
                mAgitations.set(i, mAgitations.get(index));
                mAgitations.set(index, temp);
            }
        }
    }

    public JSONObject unparse() throws JSONException {
        JSONObject json = new JSONObject();

//		json.put(StackTable.ID, mId);
        json.put(StackTable.VOLUME, mVolume);
        json.put(StackTable.TEMPERATURE, mTemperature);
        json.put(StackTable.VACUUM_BREAK, mVacuumBreak);
        json.put(StackTable.PULL_DOWN_TIME, mExtractionTime);
        json.put(StackTable.DURATION, mTotalTime);
        JSONArray ags = new JSONArray();
        for (int i = 0; i < mAgitations.size(); i++) {
            ags.put(mAgitations.get(i).unparse());
        }

        json.put(SPRecipe.AGITATIONS, ags);

        return json;
    }

    // Validate that the stack was raised in righteousness
    public void validate() {
//		// Validate Temp Type
//		if (mTempType == null)
//			mTempType = SPModel.DEFAULT_TEMP_UNITS;
//		
//		// Validate Volume Type
//		if (mVolumeType == null)
//			mVolumeType = SPModel.DEFAULT_VOL_UNITS;
//		
//		// Validate temperature
//		double MAX_KELVIN = SPServiceThermistor.convertCelciusToKelvin(SPRecipe.MAX_TEMPERATURE_C);
//		double MIN_KELVIN = SPServiceThermistor.convertCelciusToKelvin(SPRecipe.MIN_TEMPERATURE_C);
//		if (mTemperature < MIN_KELVIN) 
//			mTemperature = MIN_KELVIN;
//		else if (mTemperature > MAX_KELVIN) 
//			mTemperature = MAX_KELVIN;
//		
//		// Validate Volume
//		if (mVolume < SPModel.MIN_RINSE_VOL_ML)
//			mVolume = SPModel.MIN_RINSE_VOL_ML;
//		else if (mVolume > SPModel.MAX_RINSE_VOL_ML)
//			mVolume = SPModel.MAX_RINSE_VOL_ML;
//		
//		// Validate Vacuum Break
//		if (mVacuumBreak < SPModel.VACUUM_BREAK_MIN)
//			mVacuumBreak = SPModel.VACUUM_BREAK_MIN;
//		else if (mVacuumBreak > SPModel.VACUUM_BREAK_MAX)
//			mVacuumBreak = SPModel.VACUUM_BREAK_MAX;
//
//		// Validate Extraction Time
//		if (mExtractionTime < SPModel.EXTRACTION_MIN)
//			mExtractionTime = SPModel.EXTRACTION_MIN;
//		else if (mExtractionTime > SPModel.EXTRACTION_MAX)
//			mExtractionTime = SPModel.EXTRACTION_MAX;
//		
//		// Validate Agitations
//		while (mAgitations.size() > SPModel.MAX_AGITATION_COUNT) {
//			mAgitations.remove(mAgitations.size() - 1);
//		}
//		while (mAgitations.size() < SPModel.MIN_AGITATION_COUNT) {
//			mAgitations.add(new SPRecipeAgitation());
//		}
//		
//		for (int i = 0; i< mAgitations.size(); i++) {
//			// TODO check for overlap
//			
//			if (mAgitations.get(i).getLength() < SPModel.MIN_AGITATION_LENGTH)
//				mAgitations.get(i).setLength(SPModel.MIN_AGITATION_LENGTH);
//			if (mAgitations.get(i).getLength() > SPModel.MAX_AGITATION_LENGTH)
//				mAgitations.get(i).setLength(SPModel.MAX_AGITATION_LENGTH);
//			if (mAgitations.get(i).getPulseWidth() < SPModel.MIN_AGITATION_PULSE_WIDTH) {
//				mAgitations.get(i).setPulseWidth(SPModel.MIN_AGITATION_PULSE_WIDTH);
//			} else if (mAgitations.get(i).getPulseWidth() > SPModel.MAX_AGITATION_PULSE_WIDTH) {
//				mAgitations.get(i).setPulseWidth(SPModel.MAX_AGITATION_COUNT);
//			}
//		}
    }

    public long getId() {
        return mId;
    }

    public void setId(long Id) {
        this.mId = Id;
    }


    public double getTemperature() {
        return mTemperature;
    }

    public void setTemperature(double mTemperature) {
        this.mTemperature = (double) (Math.round(mTemperature * SPRecipe.TWO_DECIMAL_PLACES)) / SPRecipe.TWO_DECIMAL_PLACES;
        setChanged();
        notifyObservers();
    }

    public SPTempUnitType getTempType() {
        return mTempType;
    }

    public void setTempType(SPTempUnitType type) {
        mTempType = type;
        setChanged();
        notifyObservers();
    }

    public double getVolume() {
        return mVolume;
    }

    public void setVolume(double mVolume) {
        this.mVolume = (double) (Math.round(mVolume * SPRecipe.TWO_DECIMAL_PLACES)) / SPRecipe.TWO_DECIMAL_PLACES;
        setChanged();
        notifyObservers();
    }

    public SPVolumeUnitType getVolumeType() {
        return mVolumeType;
    }

    public void setVolumeType(SPVolumeUnitType units) {
        mVolumeType = units;
        setChanged();
        notifyObservers();
    }

    public int getTotalTime() {
        return mTotalTime;
    }

    public void setTotalTime(int totalTime) {
        mTotalTime = totalTime;

        //clip agitations here...
        if (mAgitations != null) {
            for (int i = mAgitations.size() - 1; i >= 0; i--) {
                if (i > 0 && mAgitations.get(i).getStartTime() >= totalTime) {
                    this.removeAgitation(i);
                    continue;
                }

                int start = mAgitations.get(i).getStartTime();
                double length = mAgitations.get(i).getLength();
                if (start + length > totalTime) {
                    double timeAfterEnd = start + length - totalTime;
                    mAgitations.get(i).setLength(length - timeAfterEnd);
                }
            }
        }

        setChanged();
        notifyObservers();
    }

    public double getVacuumBreak() {
        return mVacuumBreak;
    }

    public void setVacuumBreak(double vacuumBreak) {
        this.mVacuumBreak = vacuumBreak;
        setChanged();
        notifyObservers();
    }

    public int getExtractionTime() {
        return mExtractionTime;
    }

    public void setExtractionTime(int extractionTime) {
        this.mExtractionTime = extractionTime;
        setChanged();
        notifyObservers();
    }

    public int getAgitationCount() {
        return mAgitations.size();
    }

    public long getAgitationId(int index) {
        return mAgitations.get(index).getId();
    }

    public double getAgitationLength(int index) {
        return mAgitations.get(index).getLength();
    }

    public int getAgitationStartTime(int index) {
        return mAgitations.get(index).getStartTime();
    }

    public double getAgitationPulseWidth(int index) {
        return mAgitations.get(index).getPulseWidth();
    }

    public void setAgitation(int index, double seconds, int startTime, double pulseWidth) {
        while (index > (mAgitations.size() - 1)) {
            SPRecipeAgitation newAgitation = new SPRecipeAgitation();
            newAgitation.addObserver(this);
            mAgitations.add(newAgitation);
        }
        mAgitations.get(index).setLength(seconds);
        mAgitations.get(index).setStartTime(startTime);
        mAgitations.get(index).setPulseWidth(pulseWidth);
    }

    public void removeAgitation(int index) {
        SPLog.debug("remove ag...has: " + mAgitations.size() + " deleting: " + index);
        mAgitations.remove(index);
        SPLog.debug("after remove, has: " + mAgitations.size());
        setChanged();
        notifyObservers();
    }

    @Override
    public void update(Observable observable, Object data) {
        SPLog.debug("GOT AN UPDATE FROM AN AGITATION!");
        setChanged();
        notifyObservers();
    }

    public void save() {
    }

    public void delete() {
        for (int i = 0; i < mAgitations.size(); i++) {
            removeAgitation(i);
        }
    }

    public ArrayList<SPRecipeAgitation> getAgitationList() {
        return mAgitations;
    }

    public void copyStackIntoSelf(SPRecipeStack stack) {
        while (mAgitations.size() > stack.getAgitationCount()) {
            mAgitations.remove(mAgitations.size() - 1);
        }
        while (mAgitations.size() < stack.getAgitationCount()) {
            mAgitations.add(new SPRecipeAgitation());
            mAgitations.get(mAgitations.size() - 1).addObserver(this);
        }
        ArrayList<SPRecipeAgitation> ags = stack.getAgitationList();
        for (int i = 0; i < stack.getAgitationCount(); i++) {
            mAgitations.get(i).copyAgitationIntoSelf(ags.get(i));
        }
        mId = stack.getId();
        mTemperature = stack.getTemperature();
        mTempType = stack.getTempType();
        mVolume = stack.getVolume();
        mVolumeType = stack.getVolumeType();
        mTotalTime = stack.getTotalTime();
        mVacuumBreak = stack.getVacuumBreak();
        mExtractionTime = stack.getExtractionTime();
    }
}
