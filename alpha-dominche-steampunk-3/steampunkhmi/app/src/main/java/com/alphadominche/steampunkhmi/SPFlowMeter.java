package com.alphadominche.steampunkhmi;

import java.util.Date;
import java.util.Observable;

import ioio.lib.api.DigitalInput;

public class SPFlowMeter extends Observable {
    public final static double EDGES_PER_OUNCE = 14.9 * 0.965; //scaled to make allowance for steaming adding water and then back as a correction
    public final static double STEAM_EDGE_CORRECTION_RATIO = 1.0;
    public final static double MILLILITERS_PER_OUNCE = 29.5735296;
    public final static long MAX_DELAY_BEFORE_NEXT_EDGE = 9999; //maximum milliseconds until the next edge should come
    public final static int MAX_EDGE_OVERFLOW_TOLERANCE = 3;

    private FlowMeterThread mThread;
    private UnderflowCheckThread mCheckThread;
    private int mEdgeCount;
    private int mEdgesWhileOff;
    private boolean mFillSignalOn;
    private boolean mOverflowError;
    private boolean mUnderflowError;
    private Date mLastFillHighTime;

    SPFlowMeter(DigitalInput flowMeterSignal) {
        mThread = new FlowMeterThread(flowMeterSignal);
        mCheckThread = new UnderflowCheckThread();
        mLastFillHighTime = new Date();
        mFillSignalOn = false;
        resetEdgeCount();
        mThread.start();
        mCheckThread.start();
    }

    public int getEdgeCount() {
        return mEdgeCount;
    }

    public boolean isOverflowing() {
        return mOverflowError;
    }

    public boolean isUnderflowing() {
        return mUnderflowError;
    }

    public void setFillSignalStatus(boolean on) {
        mFillSignalOn = on;
        if (mFillSignalOn) {
            mLastFillHighTime = new Date();
            mEdgesWhileOff = 0;
        }
    }

    public void resetEdgeCount() {
        mEdgeCount = 0;
        mEdgesWhileOff = 0;
        mOverflowError = false;
        mUnderflowError = false;
        setChanged();
        notifyObservers();
    }

    protected void incrementEdgeCount() {
        if (mFillSignalOn) {
            mEdgeCount++;
            Date curr = new Date();
            if (curr.getTime() - mLastFillHighTime.getTime() > MAX_DELAY_BEFORE_NEXT_EDGE) {
                mUnderflowError = true;
            }
            mLastFillHighTime = curr;
        } else {
            if (++mEdgesWhileOff > MAX_EDGE_OVERFLOW_TOLERANCE) {
                mOverflowError = true;
            }
        }
        setChanged();
        notifyObservers();
    }

    public void stop() {
        mThread.stopRunning();
        mThread.interrupt();
    }

    public boolean stillRunning() {
        return mThread.stillRunning();
    }

    private class FlowMeterThread extends Thread {
        private DigitalInput mFlowMeterSignal;
        private boolean mRunning;

        FlowMeterThread(DigitalInput flowMeterSignal) {
            mFlowMeterSignal = flowMeterSignal;
            mRunning = true;
        }

        @Override
        public void run() {
            try {
                if (mRunning && mFlowMeterSignal.read()) {
                    mFlowMeterSignal.waitForValue(false);
                    incrementEdgeCount();
                }
                while (mRunning) {
                    mFlowMeterSignal.waitForValue(true);
                    incrementEdgeCount();
                    if (!mRunning) break;
                    mFlowMeterSignal.waitForValue(false);
                    incrementEdgeCount();
                }
            } catch (Exception e) {
                stopRunning();
            }
        }

        public void stopRunning() {
            mRunning = false;
            setChanged();
            notifyObservers();
        }

        public boolean stillRunning() {
            return mRunning;
        }
    }

    private class UnderflowCheckThread extends Thread {
        @Override
        public void run() {
            if (mFillSignalOn && (new Date()).getTime() - mLastFillHighTime.getTime() > MAX_DELAY_BEFORE_NEXT_EDGE) {
                mUnderflowError = true;
                setChanged();
                notifyObservers();
            }

            try {
                sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static int convertFromOuncesToEdges(double ounces) {
        return (int) Math.round(ounces * EDGES_PER_OUNCE * STEAM_EDGE_CORRECTION_RATIO);
    }

    public static int convertFromMillilitersToEdges(double milliliters) {
        return convertFromOuncesToEdges(milliliters / MILLILITERS_PER_OUNCE);
    }

    public static double convertFromMillilitersToOunces(double milliliters) {
        return milliliters / MILLILITERS_PER_OUNCE;
    }

    public static double convertFromOuncesToMilliliters(double ounces) {
        return ounces * MILLILITERS_PER_OUNCE;
    }

    public static double convertEdgesToOunces(int edges) {
        return edges / EDGES_PER_OUNCE;
    }

    public static double convertEdgesToMilliliters(int edges) {
        return edges / EDGES_PER_OUNCE * MILLILITERS_PER_OUNCE;
    }
}
