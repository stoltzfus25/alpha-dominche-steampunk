package com.alphadominche.steampunkhmi.utils;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager.NameNotFoundException;
import com.alphadominche.steampunkhmi.SPTempUnitType;
import com.alphadominche.steampunkhmi.SPVolumeUnitType;

public class MachineSettings {
    private Long id;
    private String serialNumber;
    private Double boilerTemp;
    private Double rinseTemp;
    private Double rinseVolume;
    private Double elevation;
    private Integer crucibleCount;
    private List<Boolean> crucibleStates;
    private SPTempUnitType tempUnitType;
    private SPVolumeUnitType volumeUnitType;
    private boolean localOnlyMode;

    private String mDeviceName = "NEXUS_7";

    public MachineSettings(Long id, String serialNumber, Double boilerTemp,
                           Double rinseTemp, Double rinseVolume, Double elevation,
                           Integer crucibleCount, List<Boolean> crucibleStates,
                           SPTempUnitType tempUnitType, SPVolumeUnitType volumeUnitType, boolean localOnlyMode) {
        this.id = id;
        this.serialNumber = serialNumber;
        this.boilerTemp = boilerTemp;
        this.rinseTemp = rinseTemp;
        this.rinseVolume = rinseVolume;
        this.elevation = elevation;
        this.crucibleCount = crucibleCount;
        this.crucibleStates = crucibleStates;
        this.tempUnitType = tempUnitType;
        this.volumeUnitType = volumeUnitType;
        this.localOnlyMode = localOnlyMode;
    }

    /**
     * Persist all the data in a given machine settings object to the shared
     * preferences
     *
     * @param machineSettings the object to write to the shared preferences
     * @param context         app context
     */
    public static void writeMachineSettingsToSharedPreferences(
            MachineSettings machineSettings, Context context) {
        Editor editor = SteampunkUtils.getSteampunkSharedPreferences(context)
                .edit();

        if (machineSettings.getId() != null) {
            editor.putLong(Constants.SP_MACHINE_ID, machineSettings.getId());
        }
        editor.putString(Constants.SP_MACHINE_SERIAL_NUMBER,
                machineSettings.getSerialNumber());

        // Shared prefs doesn't support doubles
        putDouble(editor, Constants.SP_BOILER_TEMP,
                machineSettings.getBoilerTemp());
        putDouble(editor, Constants.SP_RINSE_TEMP,
                machineSettings.getRinseTemp());
        putDouble(editor, Constants.SP_RINSE_VOLUME,
                machineSettings.getRinseVolume());
        putDouble(editor, Constants.SP_ELEVATION,
                machineSettings.getElevation());

        if (machineSettings.getCrucibleCount() != null) {
            editor.putInt(Constants.SP_CRUCIBLE_COUNT,
                    machineSettings.getCrucibleCount());
        }

        // Shared prefs doesn't support boolean[], so store it as JSON
        if (machineSettings.getCrucibleStates() != null) {
            JSONArray crucibleStatesJSONArray = new JSONArray(
                    machineSettings.getCrucibleStates());
            String crucibleStatesJSONString = crucibleStatesJSONArray
                    .toString();
            editor.putString(Constants.SP_CRUCIBLE_STATES,
                    crucibleStatesJSONString);
        }

        if (machineSettings.getTempUnitType() != null) {
            editor.putInt(Constants.SP_TEMP_UNIT_TYPE, machineSettings
                    .getTempUnitType().ordinal());

        }
        if (machineSettings.getVolumeUnitType() != null) {
            editor.putInt(Constants.SP_VOLUME_UNIT_TYPE, machineSettings
                    .getVolumeUnitType().ordinal());
        }
        editor.putBoolean(Constants.SP_LOCAL_ONLY, machineSettings.isLocalOnly());

        editor.apply();
    }

    /**
     * Write a double to long bits in shared preferences
     *
     * @param editor shared preferences editor
     * @param key    key to write to
     * @param value  double to convert to long bits
     */
    private static void putDouble(final Editor editor, final String key,
                                  final Double value) {
        if (value != null) {
            editor.putLong(key, Double.doubleToRawLongBits(value));
        }
    }

    /**
     * Get a machine settings object that contains the machine settings
     * currently in the shared preferences
     *
     * @param context app context
     * @return a machine settings object with the currently stored machine
     * settings
     */
    public static MachineSettings getMachineSettingsFromSharedPreferences(
            Context context) {
        SharedPreferences sharedPreferences = SteampunkUtils
                .getSteampunkSharedPreferences(context);

        Long id = 0L;
        try { //try assigning the machine ID as a long (the way it is in the newer version)
            id = sharedPreferences.contains(Constants.SP_MACHINE_ID) ? sharedPreferences
                    .getLong(Constants.SP_MACHINE_ID, 0L) : 0L;
        } catch (ClassCastException error) { //if this is on an upgrade from build 36, use the integer version instead and replace it with a long
            Integer intId = sharedPreferences.contains(Constants.SP_MACHINE_ID) ? sharedPreferences.getInt(Constants.SP_MACHINE_ID, 0) : 0;
            id = intId.longValue();
            Editor editor = sharedPreferences.edit();
            editor.remove(Constants.SP_MACHINE_ID);
            editor.putLong(Constants.SP_MACHINE_ID, id);
            editor.apply();
        }

        String serialNumber = sharedPreferences.getString(
                Constants.SP_MACHINE_SERIAL_NUMBER, null);

        Double boilerTemp = getDouble(sharedPreferences,
                Constants.SP_BOILER_TEMP);
        Double rinseTemp = getDouble(sharedPreferences, Constants.SP_RINSE_TEMP);
        Double rinseVolume = getDouble(sharedPreferences,
                Constants.SP_RINSE_VOLUME);
        Double elevation = getDouble(sharedPreferences, Constants.SP_ELEVATION);
        Integer crucibleCount = sharedPreferences
                .contains(Constants.SP_CRUCIBLE_COUNT) ? sharedPreferences
                .getInt(Constants.SP_CRUCIBLE_COUNT, 0) : null;

        ArrayList<Boolean> crucibleStates = null;
        if (sharedPreferences.contains(Constants.SP_CRUCIBLE_STATES)) {
            String crucibleStatesJSONString = sharedPreferences.getString(
                    Constants.SP_CRUCIBLE_STATES, null);
            try {
                JSONArray crucibleStatesJSONArray = new JSONArray(
                        crucibleStatesJSONString);
                crucibleStates = new ArrayList<Boolean>();
                for (int i = 0; i < crucibleStatesJSONArray.length(); i++) {
                    crucibleStates.add(crucibleStatesJSONArray.getBoolean(i));
                }
            } catch (JSONException e) {
                // TODO Handle error where JSON was written to the shared
                // prefs incorrectly
                e.printStackTrace();
            }
        }

        SPTempUnitType tempUnitType = null;
        if (sharedPreferences.contains(Constants.SP_TEMP_UNIT_TYPE)) {
            int tempUnitTypeInt = sharedPreferences.getInt(
                    Constants.SP_TEMP_UNIT_TYPE, 0);
            tempUnitType = SPTempUnitType.values()[tempUnitTypeInt];
        }

        SPVolumeUnitType volumeUnitType = null;
        if (sharedPreferences.contains(Constants.SP_VOLUME_UNIT_TYPE)) {
            int volumeUnitTypeInt = sharedPreferences.getInt(
                    Constants.SP_VOLUME_UNIT_TYPE, 0);
            volumeUnitType = SPVolumeUnitType.values()[volumeUnitTypeInt];
        }

        boolean localOnly = true;
        if (sharedPreferences.contains(Constants.SP_LOCAL_ONLY)) {
            localOnly = sharedPreferences.getBoolean(Constants.SP_LOCAL_ONLY, true);
        }

        MachineSettings currentMachineSettings = new MachineSettings(id,
                serialNumber, boilerTemp, rinseTemp, rinseVolume, elevation,
                crucibleCount, crucibleStates, tempUnitType, volumeUnitType, localOnly);

        return currentMachineSettings;
    }

    /**
     * Get the stored crucible count. Used when other parts of the app need just
     * this info and not all the machine settings
     *
     * @param context app context
     * @return the crucible count stored in shared preferences
     */
    public static Integer getMachineCrucibleCount(Context context) {
        return getMachineSettingsFromSharedPreferences(context)
                .getCrucibleCount();
    }

    /**
     * Get a double from long bits in shared preferences
     *
     * @param sharedPreferences the shared preferences to read from
     * @param key               the key to get
     * @return the double for the corresponding key or null if shared
     * preferences doesn't have that key
     */
    private static Double getDouble(final SharedPreferences sharedPreferences,
                                    final String key) {
        return sharedPreferences.contains(key) ? Double
                .longBitsToDouble(sharedPreferences.getLong(key, 0)) : null;
    }

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public static boolean validatePIN(Context context, String input) {
        SharedPreferences sharedPreferences = SteampunkUtils
                .getSteampunkSharedPreferences(context);
        String PIN = sharedPreferences.getString(Constants.SP_PIN, null);
        if (PIN == null) return true;
        for (int i = 0; i < 4; i++) {
            if (input.charAt(i) != PIN.charAt(i)) return false;
        }
        return true;
    }

    public static void writePinToSharedPrefs(Context context, String pin) {
        Editor editor = SteampunkUtils
                .getSteampunkSharedPreferences(context).edit();
        editor.putString(Constants.SP_PIN, pin);
        editor.commit();
    }

    public Double getBoilerTemp() {
        return boilerTemp;
    }

    public Double getRinseTemp() {
        return rinseTemp;
    }

    public Double getRinseVolume() {
        return rinseVolume;
    }

    public Double getElevation() {
        return elevation;
    }

    public Integer getCrucibleCount() {
        return crucibleCount;
    }

    public void setCrucibleCount(Integer crucibleCount) {
        this.crucibleCount = crucibleCount;
    }

    public List<Boolean> getCrucibleStates() {
        return crucibleStates;
    }

    public SPTempUnitType getTempUnitType() {
        if (tempUnitType == null) {
            return SPTempUnitType.FAHRENHEIT;
        }
        return tempUnitType;
    }

    public SPVolumeUnitType getVolumeUnitType() {
        return volumeUnitType;
    }

    public boolean isLocalOnly() {
        return localOnlyMode;
    }

    public void setLocalOnly(boolean localOnly) {
        localOnlyMode = localOnly;
    }

    public int getVersion(Context context) {
        int version = 0;
        try {
            version = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return version;
    }

    public int getDeviceFromSharedPrefs(Context context) {
        SharedPreferences sharedPreferences = SteampunkUtils
                .getSteampunkSharedPreferences(context);

        return sharedPreferences.getInt(Constants.SP_DEVICE_ID, -1);
    }

    public void writeDeviceToSharedPrefs(Context context, int device) {
        Editor editor = SteampunkUtils.getSteampunkSharedPreferences(context)
                .edit();

        editor.putInt(Constants.SP_DEVICE_ID, device);

        editor.apply();
    }

    public String getDeviceName() {
        return mDeviceName;
    }

}