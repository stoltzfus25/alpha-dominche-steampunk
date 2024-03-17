package com.alphadominche.steampunkhmi;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Observable;

import com.alphadominche.steampunkhmi.database.tables.AgitationCycleTable;
import com.alphadominche.steampunkhmi.model.AgitationCycle;

public class SPRecipeAgitation extends Observable {
    public static final String PULSE_WIDTH = "pulse_width";

    private long mId;
    private int mStartTime;
    private double mLength;
    private double mPulseWidth;

    SPRecipeAgitation() {
        mId = -1;
        mStartTime = 0;
        mLength = 0;
        mPulseWidth = 0.5;
    }

    SPRecipeAgitation(int startTime, double length, double pulseWidth) {
        mId = -1L;
        mStartTime = startTime;
        mLength = length;
        mPulseWidth = pulseWidth;
    }

    SPRecipeAgitation(long id, int startTime, int length, double pulseWidth) {
        mId = id;
        mStartTime = startTime;
        mLength = length;
        mPulseWidth = pulseWidth;
    }

    SPRecipeAgitation(JSONObject json) throws JSONException {
        mStartTime = json.getInt(AgitationCycleTable.START_TIME);
        mLength = json.getDouble(AgitationCycleTable.DURATION);
        if (json.has(PULSE_WIDTH)) {
            mPulseWidth = json.getDouble(PULSE_WIDTH);
        } else {
            mPulseWidth = 0.5;
        }
    }

    SPRecipeAgitation(SPRecipeAgitation agitationCycle) {
        mId = agitationCycle.mId;
        setStartTime(agitationCycle.getStartTime());
        setLength(agitationCycle.getLength());
        setPulseWidth(agitationCycle.getPulseWidth());
    }

    SPRecipeAgitation(AgitationCycle agitationCycle) {
        mId = agitationCycle.getId();
        setStartTime(agitationCycle.getStart_time());
        setLength(agitationCycle.getDuration());
    }

    public JSONObject unparse() throws JSONException {
        JSONObject json = new JSONObject();

        json.put(AgitationCycleTable.START_TIME, mStartTime);
        json.put(AgitationCycleTable.DURATION, mLength);
        json.put(PULSE_WIDTH, mPulseWidth);

        return json;
    }

    public long getId() {
        return mId;
    }

    public int getStartTime() {
        return mStartTime;
    }

    public void setStartTime(int startTime) {
        mStartTime = startTime;
        setChanged();
        notifyObservers();
    }

    public double getLength() {
        return mLength;
    }

    public void setLength(double length) {
        SPLog.debug("set length!" + "stack..."); //StackTraceElement[] e = (new Exception()).getStackTrace(); for(int i = 0;i < e.length;i++) SPLog.debug("line: " + e[i]);
        mLength = length;
        setChanged();
        notifyObservers();
    }

    public double getPulseWidth() {
        return mPulseWidth;
    }

    public void setPulseWidth(double pulseWidth) {
        mPulseWidth = pulseWidth;
        setChanged();
        notifyObservers();
    }

    public void copyAgitationIntoSelf(SPRecipeAgitation ag) {
        mId = ag.getId();
        mStartTime = ag.getStartTime();
        mLength = ag.getLength();
        mPulseWidth = ag.getPulseWidth();
    }
}
