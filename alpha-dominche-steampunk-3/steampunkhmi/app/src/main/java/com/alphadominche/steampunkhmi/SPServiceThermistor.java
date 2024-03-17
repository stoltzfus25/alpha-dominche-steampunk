package com.alphadominche.steampunkhmi;

import java.util.Observable;

import ioio.lib.api.AnalogInput;

public class SPServiceThermistor extends Observable {
    public boolean boiler = false;
    public static final int SAMPLES_PER_READING = 10;
    public static final double K = 273.15; //Kelvins at 0 deg C
    public static final double T0 = 298.15;
    public static final double R0 = 10000.0;
    public static final double R2 = 2499;
    public static final double B = 3575; //thermistor coefficient!
    public static final long MILLIS_BETWEEN_SAMPLES = 100L;
    public static final double FAHRENHEIT_OFFSET = 32.0;
    public static final double FAHRENHEIT_TO_CELCIUS_FACTOR = 5.0 / 9.0;
    public static final double CELCIUS_TO_FAHRENHEIT_FACTOR = 1.0 / FAHRENHEIT_TO_CELCIUS_FACTOR;
    public static final double VARIANCE_M = 0.9265;
    public static final double VARIANCE_B = 23.306;

    private AnalogInput mThermistorPin;
    private double[] mReadings; //averaged to get the temperature, probably ten samples should be averaged for each entry
    private int mReadingIndex;
    private boolean mRunning;
    private SPTempUnitType mUnits;
    private Thread mThread;

    SPServiceThermistor(AnalogInput in, SPTempUnitType units, SPIOIOService service) {
        mReadings = new double[SAMPLES_PER_READING];
        mThermistorPin = in;
        mRunning = true;
        mReadingIndex = 0;
        mUnits = units;
        mThread = new SPThermThread();
        mThread.start();
    }

    private class SPThermThread extends Thread {
        SPThermThread() {
            super();
        }

        @Override
        public void run() {
            try {
                double factor = 1.0 / SAMPLES_PER_READING;
                while (mRunning) {
                    sleep(MILLIS_BETWEEN_SAMPLES);
                    double reading = 0.0;
                    for (int i = 0; i < SAMPLES_PER_READING; i++) {
                        double singleRead = mThermistorPin.read(); //if (boiler) SPLog.debug("read temp voltage: " + singleRead);
                        double singleTemp = convertToTemp(singleRead, mUnits);
                        reading += singleTemp;
                    }
                    mReadings[mReadingIndex] = reading * factor;
                    mReadingIndex++;
                    if (mReadingIndex >= mReadings.length) {
                        mReadingIndex = 0;
                    }
                    setChanged();
                    notifyObservers();
                }
            } catch (Exception e) {
                mRunning = false;
            }
        }
    }

    public boolean stillRunning() {
        return mRunning;
    }

    public void stopRunning() {
        SPLog.debug("thermistor stopping...");
        mRunning = false;
    }

    public double getTemperature() {
        double factor = 1.0 / SAMPLES_PER_READING;
        double temp = 0.0;
        for (int i = 0; i < mReadings.length; i++) {
            temp += mReadings[i];
        }
        temp *= factor;
        return temp;
    }

    public void setUnits(SPTempUnitType units) {
        if (units != mUnits) {
            for (int i = 0; i < mReadings.length; i++) {
                mReadings[i] = convertFromTempToTemp(mUnits, units, mReadings[i]);
            }
            mUnits = units;
        }
    }

    public static double convertToTemp(double pinVal, SPTempUnitType units) {
        double temp = 1.0 / (Math.log((R2 / pinVal - R2) / R0) / B + 1.0 / T0);

        //correction for variance from observed...
        temp = VARIANCE_M * temp + VARIANCE_B;

        if (units != SPTempUnitType.KELVIN) {
            temp = convertFromTempToTemp(SPTempUnitType.KELVIN, units, temp);
        }
        return temp;
    }

    public static double convertFromTempToTemp(SPTempUnitType origUnits, SPTempUnitType endUnits, double temp) {
        if (origUnits == endUnits) {
            //this function shouldn't have been called!
        } else if (origUnits == SPTempUnitType.KELVIN) {
            temp = convertKelvinToCelcius(temp);
            if (endUnits == SPTempUnitType.FAHRENHEIT) {
                temp = convertCelciusToFarenheit(temp);
            }
        } else if (origUnits == SPTempUnitType.CELCIUS) {
            if (endUnits == SPTempUnitType.KELVIN) {
                temp = convertCelciusToKelvin(temp);
            } else if (endUnits == SPTempUnitType.FAHRENHEIT) {
                temp = convertCelciusToFarenheit(temp);
            }
        } else if (origUnits == SPTempUnitType.FAHRENHEIT) {
            temp = convertFarenheitToCelcius(temp);
            if (endUnits == SPTempUnitType.KELVIN) {
                temp = convertCelciusToKelvin(temp);
            }
        }
        return temp;
    }

    public static double convertKelvinToCelcius(double temp) {
        return temp - K;
    }

    public static double convertCelciusToKelvin(double temp) {
        return temp + K;
    }

    public static double convertFarenheitToCelcius(double temp) {
        return (temp - FAHRENHEIT_OFFSET) * FAHRENHEIT_TO_CELCIUS_FACTOR;
    }

    public static double convertCelciusToFarenheit(double temp) {
        return temp * CELCIUS_TO_FAHRENHEIT_FACTOR + FAHRENHEIT_OFFSET;
    }
}
