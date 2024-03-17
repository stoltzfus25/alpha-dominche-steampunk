package com.alphadominche.steampunkhmi;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import com.alphadominche.steampunkhmi.restclient.persistenceservicehelper.DefaultPersistenceServiceHelper;
import com.alphadominche.steampunkhmi.restclient.persistenceservicehelper.DefaultPersistenceServiceHelperEvents;
import com.alphadominche.steampunkhmi.utils.MachineSettings;
import de.greenrobot.event.EventBus;

/**
 * @author zack
 */

public class SPMachineSettingsFragment extends SPFragment implements Observer {
    public final static int BRIGHT = 255;
    public final static int DIM = 100;
    public final static double Meters_TO_FEET = 3.2808;

    private SPMachineSettingsSeekBarListener mListener;
    private SPModel mMachineModel;

    private ArrayList<Boolean> mCrucibleStates;

    private TextView mBoilerCurrentTempView;
    private SeekBar mBoilerTempSlider;
    private TextView mBoilerTempMaxLabel;
    private TextView mBoilerTempMinLabel;
    private TextView mBoilerTempLabel;
    private TextView mBoilerTempUnitLabel;
    private SeekBar mRinseTempSlider;
    private TextView mRinseTempMaxLabel;
    private TextView mRinseTempMinLabel;
    private TextView mRinseTempLabel;
    private TextView mRinseTempUnitLabel;
    private SeekBar mRinseVolSlider;
    private TextView mRinseVolMaxLabel;
    private TextView mRinseVolMinLabel;
    private TextView mRinseVolLabel;
    private TextView mRinseVolUnitLabel;

    private View mBoilerTempLink;
    private View mRinseTempLink;
    private View mRinseVolumeLink;

    private View mCalibrateButton;
    private TextView mElevationLabel;
    private TextView mElevationUnitLabel;
    private View mFarenheit;
    private View mCelcius;
    private View mOunces;
    private View mMilliLiters;

    private View mDisableNetworkingButton;
    private View mEnableNetworkingButton;

    private View mDisableBox;
    private boolean mDisableSave;

    private TextView mSerialNumView;

    private String mSerialNum;
    private SPTempUnitType mTempUnit;
    private SPVolumeUnitType mVolumeUnit;
    private double mElevation;
    private Location mLocation;
    private LocationManager mLocationManager;
    private LocationListener mLocationListener;

    private double mMinBoilerTemp;
    private double mMaxBoilerTemp;
    private double mBoilerCurrentTemp;
    private double mBoilerTemp;
    private double mMinRinseTemp;
    private double mMaxRinseTemp;
    private double mRinseTemp;
    private double mMinRinseVol;
    private double mMaxRinseVol;
    private double mRinseVol;
    private boolean mLocalOnly;
    private int mCrucibleCount;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void getPersistedSettings(View rootView) {
        MachineSettings shared = MachineSettings.getMachineSettingsFromSharedPreferences(getActivity().getApplicationContext());
        mTempUnit = shared.getTempUnitType() != null ? shared.getTempUnitType() : SPModel.DEFAULT_TEMP_UNITS;
        mVolumeUnit = shared.getVolumeUnitType() != null ? shared.getVolumeUnitType() : SPModel.DEFAULT_VOL_UNITS;
        mElevation = shared.getElevation() != null ? shared.getElevation() : SPModel.DEFAULT_ELEVATION_FT;
        mBoilerTemp = shared.getBoilerTemp() != null ? shared.getBoilerTemp() : SPModel.DEFAULT_BOILER_TEMP_F;
        mBoilerTempLabel.setText("" + (int) Math.round(mBoilerTemp) + DEGREE_SYMBOL);
        mRinseTemp = shared.getRinseTemp() != null ? shared.getRinseTemp() : SPModel.DEFAULT_RINSE_TEMP_F;
        mRinseTempLabel.setText("" + (int) Math.round(mRinseTemp) + DEGREE_SYMBOL);
        mRinseVol = shared.getRinseVolume() != null ? shared.getRinseVolume() : SPModel.DEFAULT_RINSE_VOL_OZ;
        mRinseVolLabel.setText("" + mRinseVol + DEGREE_SYMBOL);
        mSerialNum = shared.getSerialNumber() != null ? shared.getSerialNumber() : "";
        mSerialNumView.setText(mSerialNum);
        mCrucibleStates = (ArrayList<Boolean>) shared.getCrucibleStates();
        mCrucibleCount = shared.getCrucibleCount() != null ? shared.getCrucibleCount() : SPIOIOService.MAX_CRUCIBLE_COUNT;
        mLocalOnly = !mMachineModel.isConnectedToNetwork(); //shared.isLocalOnly();

        // Check if null or new count
        if (mCrucibleStates == null || mCrucibleStates.size() != mCrucibleCount) {
            mCrucibleStates = new ArrayList<Boolean>();
            for (int i = 0; i < mCrucibleCount; i++) {
                mCrucibleStates.add(i, false);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.sp_machine_settings, container, false);

        mMachineModel = SPModel.getInstance(getActivity());

//		mMachineModel.addObserver(this);

        mSaveButton = (ImageView) rootView.findViewById(R.id.save_button);
        mSaveButton.setOnClickListener(this);
        mCancelButton = (ImageView) rootView.findViewById(R.id.cancel_button);
        mCancelButton.setOnClickListener(this);

        mBoilerCurrentTempView = (TextView) rootView.findViewById(R.id.machine_settings_current_boiler_temp);

        mBoilerTempSlider = (SeekBar) rootView.findViewById(R.id.machine_settings_boiler_temp_slider);
        mBoilerTempMaxLabel = (TextView) rootView.findViewById(R.id.machine_settings_boiler_temp_max_label);
        mBoilerTempMinLabel = (TextView) rootView.findViewById(R.id.machine_settings_boiler_temp_min_label);
        mBoilerTempLabel = (TextView) rootView.findViewById(R.id.boiler_temp_value_label);
        mBoilerTempUnitLabel = (TextView) rootView.findViewById(R.id.boiler_temp_units_label);
        mRinseTempSlider = (SeekBar) rootView.findViewById(R.id.machine_settings_rinse_temp_slider);
        mRinseTempMaxLabel = (TextView) rootView.findViewById(R.id.machine_settings_rinse_temp_max_label);
        mRinseTempMinLabel = (TextView) rootView.findViewById(R.id.machine_settings_rinse_temp_min_label);
        mRinseTempLabel = (TextView) rootView.findViewById(R.id.rinse_temp_value_label);
        mRinseTempUnitLabel = (TextView) rootView.findViewById(R.id.rinse_temp_units_label);
        mRinseVolLabel = (TextView) rootView.findViewById(R.id.rinse_volume_value_label);
        mRinseVolUnitLabel = (TextView) rootView.findViewById(R.id.rinse_volume_units_label);
        mRinseVolSlider = (SeekBar) rootView.findViewById(R.id.rinse_volume_slider);
        mRinseVolMaxLabel = (TextView) rootView.findViewById(R.id.machine_settings_rinse_vol_max_label);
        mRinseVolMinLabel = (TextView) rootView.findViewById(R.id.machine_settings_rinse_vol_min_label);

        mCalibrateButton = rootView.findViewById(R.id.machine_settings_calibrate);
        mCalibrateButton.setOnClickListener(this);
        mElevationLabel = (TextView) rootView.findViewById(R.id.machine_settings_elevation);
        mElevationUnitLabel = (TextView) rootView.findViewById(R.id.machine_settings_elevation_units_label);

        mFarenheit = rootView.findViewById(R.id.machine_settings_farenheit);
        mFarenheit.setOnClickListener(this);
        mCelcius = rootView.findViewById(R.id.machine_settings_celcius);
        mCelcius.setOnClickListener(this);
        mOunces = rootView.findViewById(R.id.machine_settings_oz);
        mOunces.setOnClickListener(this);
        mMilliLiters = rootView.findViewById(R.id.machine_settings_ml);
        mMilliLiters.setOnClickListener(this);
        mSerialNumView = (TextView) rootView.findViewById(R.id.machine_settings_serial_num);

        getPersistedSettings(rootView);

        refreshUI();

        mBoilerTempSlider.setProgress(calculateProgress(mBoilerTemp, mBoilerTempSlider.getMax(), mMinBoilerTemp, mMaxBoilerTemp));
        mRinseTempSlider.setProgress(calculateProgress(mRinseTemp, mRinseTempSlider.getMax(), mMinRinseTemp, mMaxRinseTemp));
        mRinseVolSlider.setProgress(calculateProgress(mRinseVol, mRinseVolSlider.getMax(), mMinRinseVol, mMaxRinseVol));

        initializeCrucibles(rootView, inflater);

        mListener = new SPMachineSettingsSeekBarListener((SPMachineSettingsActivity) getActivity());

        // Slider modal links
        mBoilerTempLink = rootView.findViewById(R.id.boiler_temp_link);
        mBoilerTempLink.setOnClickListener(getSliderListener());
        mRinseTempLink = rootView.findViewById(R.id.rinse_temp_link);
        mRinseTempLink.setOnClickListener(getSliderListener());
        mRinseVolumeLink = rootView.findViewById(R.id.rinse_volume_link);
        mRinseVolumeLink.setOnClickListener(getSliderListener());

        mDisableNetworkingButton = rootView.findViewById(R.id.machine_settings_local_only_button);
        mEnableNetworkingButton = rootView.findViewById(R.id.machine_settings_connect_to_server_button);
        // TODO need to initialize mNetworkingHasBeenDisabled based on the current state
        OnClickListener networkSwitchListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                SPLog.debug("clicked");
                if (v == mDisableNetworkingButton && mMachineModel.isConnectedToNetwork()) {
                    getActivity().stopService(new Intent("SPUpdateService"));
                    DefaultPersistenceServiceHelper.getInstance(getActivity()).disableNetworking();
                    SPLog.debug("called disable networking on helper");
                } else if (v == mEnableNetworkingButton && !mMachineModel.isConnectedToNetwork()) {
                    getActivity().startService(new Intent("SPUpdateService"));
                    DefaultPersistenceServiceHelper.getInstance(getActivity()).enableNetworking();
                    SPLog.debug("called enable networking on helper");
                }
            }
        };
        mDisableNetworkingButton.setOnClickListener(networkSwitchListener);
        mEnableNetworkingButton.setOnClickListener(networkSwitchListener);
        mDisableNetworkingButton.setAlpha(DIM);
        mEnableNetworkingButton.setAlpha(DIM);

        // Location manager for calculating elevation
        mLocationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        mLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        mLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                mLocation = location;
            }

            @Override
            public void onProviderDisabled(String provider) {
            }

            @Override
            public void onProviderEnabled(String provider) {
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }
        };

        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mLocationListener);

        mDisableBox = rootView.findViewById(R.id.disable_box);

        this.update(null, null);

        return rootView;
    }

    public void seekBarProgressChanged(SeekBar seekBar, int progress) {
        if (seekBar == mBoilerTempSlider) {
            mBoilerTemp = calculateValue(progress, seekBar.getMax(), mMinBoilerTemp, mMaxBoilerTemp, SPModel.BOILER_TEMP_PRECISION);
            mBoilerTempLabel.setText("" + (int) Math.round(mBoilerTemp) + DEGREE_SYMBOL);
            mBoilerTempSlider.setProgress(calculateProgress(mBoilerTemp, mBoilerTempSlider.getMax(), mMinBoilerTemp, mMaxBoilerTemp));
        } else if (seekBar == mRinseTempSlider) {
            mRinseTemp = calculateValue(progress, seekBar.getMax(), mMinRinseTemp, mMaxRinseTemp, SPModel.RINSE_TEMP_PRECISION);
            mRinseTempLabel.setText("" + (int) Math.round(mRinseTemp) + DEGREE_SYMBOL);
            mRinseTempSlider.setProgress(calculateProgress(mRinseTemp, mRinseTempSlider.getMax(), mMinRinseTemp, mMaxRinseTemp));
        } else if (seekBar == mRinseVolSlider) {
            mRinseVol = calculateValue(progress, seekBar.getMax(), mMinRinseVol, mMaxRinseVol, SPModel.RINSE_VOL_PRECISION);
            mRinseVolLabel.setText("" + (int) mRinseVol);
            mRinseVolSlider.setProgress(calculateProgress(mRinseVol, mRinseVolSlider.getMax(), mMinRinseVol, mMaxRinseVol));
        }
    }

    public OnClickListener getSliderListener() {
        OnClickListener sliderClickListener = new OnClickListener() {

            @Override
            public void onClick(View v) {
                SeekBar seekbar;
                String tUnit = getActivity().getResources().getString(R.string.fahrenheit_abreviation);
                String vUnit = getActivity().getResources().getString(R.string.ounces_abreviation);
                if (mTempUnit == SPTempUnitType.CELCIUS) {
                    tUnit = getActivity().getResources().getString(R.string.celcius_abreviation);
                }
                if (mVolumeUnit == SPVolumeUnitType.MILLILITERS) {
                    vUnit = getActivity().getResources().getString(R.string.milliliters_abreviation);
                }

                if (v == mBoilerTempLink) {
                    seekbar = mBoilerTempSlider;
                    SPSliderModalFragment frag = new SPSliderModalFragment();
                    frag.setProgressColor(SPSliderColor.RED, getActivity().getApplicationContext());
                    frag.show(getFragmentManager(), "sliderModal");
                    frag.setListenerAndImposter(mListener, seekbar);
                    frag.setLimitsUnitsAndTitle((int) mMinBoilerTemp, (int) mMaxBoilerTemp, tUnit, getActivity().getResources().getString(R.string.boiler_temp_label_text));
                } else if (v == mRinseTempLink) {
                    seekbar = mRinseTempSlider;
                    SPSliderModalFragment frag = new SPSliderModalFragment();
                    frag.setProgressColor(SPSliderColor.RED, getActivity().getApplicationContext());
                    frag.show(getFragmentManager(), "sliderModal");
                    frag.setListenerAndImposter(mListener, seekbar);
                    frag.setLimitsUnitsAndTitle((int) mMinRinseTemp, (int) mMaxRinseTemp, tUnit, getActivity().getResources().getString(R.string.rinse_temp_label_text));
                } else if (v == mRinseVolumeLink) {
                    seekbar = mRinseVolSlider;
                    SPSliderModalFragment frag = new SPSliderModalFragment();
                    frag.setProgressColor(SPSliderColor.BLUE, getActivity().getApplicationContext());
                    frag.show(getFragmentManager(), "sliderModal");
                    frag.setListenerAndImposter(mListener, seekbar);
                    frag.setLimitsUnitsAndTitle((int) mMinRinseVol, (int) mMaxRinseVol, vUnit, getActivity().getResources().getString(R.string.rinse_vol_label_text));
                }
            }
        };
        return sliderClickListener;
    }

    /* SeekBar */
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress,
                                  boolean fromUser) {
        //seekBarProgressChanged(seekBar, progress);
    }

    public OnLongClickListener getLongClickListener(final int index) {
        return new OnLongClickListener() {

            @Override
            public boolean onLongClick(final View v) {
                String text = getActivity().getResources().getString(R.string.lock_crucible_prompt);
                if (mCrucibleStates.get(index)) {
                    text = text.replace(getActivity().getResources().getString(R.string.lock), getActivity().getResources().getString(R.string.unlock));
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setMessage(text);
                builder.setPositiveButton(getActivity().getResources().getString(R.string.yes_capitalized), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        mCrucibleStates.set(index, !mCrucibleStates.get(index));

                        if (mCrucibleStates.get(index)) {
                            v.findViewById(R.id.machine_settings_crucible_locked).setVisibility(View.VISIBLE);
                            v.findViewById(R.id.machine_settings_crucible_unlocked).setVisibility(View.GONE);
                        } else {
                            v.findViewById(R.id.machine_settings_crucible_locked).setVisibility(View.GONE);
                            v.findViewById(R.id.machine_settings_crucible_unlocked).setVisibility(View.VISIBLE);
                        }
                    }
                });
                builder.setNegativeButton(getActivity().getResources().getString(R.string.no_capitalized), null);

                AlertDialog alert = builder.show();
                alert.getWindow().setLayout(SPActivity.ALERT_WINDOW_WIDTH, SPActivity.ALERT_WINDOW_HEIGHT);
                TextView popup = (TextView) alert.findViewById(android.R.id.message);
                popup.setGravity(Gravity.CENTER);
                popup.setPadding(0, SPActivity.ALERT_POPUP_PADDING, 0, 0);

                return false;
            }
        };
    }

    public void initializeCrucibles(View rootView, LayoutInflater inflater) {
        ViewGroup parent = (ViewGroup) rootView.findViewById(R.id.machine_settings_crucibles);
        int index = 0;

        // Add Logo if size is 2
        if (mCrucibleCount == 2) {
            inflater.inflate(R.layout.machine_settings_crucible, parent);
            View logo = parent.getChildAt(0).findViewById(R.id.machine_settings_crucible_logo);
            logo.setVisibility(View.VISIBLE);
            index++;
        }

        // Create crucible view, set status, set long listener
        for (int i = 0; i < mCrucibleCount; i++, index++) {
            inflater.inflate(R.layout.machine_settings_crucible, parent);
            View locked = parent.getChildAt(index).findViewById(R.id.machine_settings_crucible_locked);
            View unlocked = parent.getChildAt(index).findViewById(R.id.machine_settings_crucible_unlocked);

            if (mCrucibleStates.get(i)) {
                locked.setVisibility(View.VISIBLE);
            } else {
                unlocked.setVisibility(View.VISIBLE);
            }
            parent.getChildAt(index).setOnLongClickListener(getLongClickListener(i));
        }

        // Add Logos in missing places
        for (int i = index; i < SPIOIOService.MAX_CRUCIBLE_COUNT; i++) {
            inflater.inflate(R.layout.machine_settings_crucible, parent);
            View logo = parent.getChildAt(i).findViewById(R.id.machine_settings_crucible_logo);
            logo.setVisibility(View.VISIBLE);
        }
    }

    public void refreshUI() {
        double temp = mElevation;
        if (mTempUnit == SPTempUnitType.FAHRENHEIT) {
            mMinBoilerTemp = SPModel.MIN_BOILER_TEMP_F;
            mMaxBoilerTemp = SPModel.MAX_BOILER_TEMP_F;
            mMinRinseTemp = SPModel.MIN_RINSE_TEMP_F;
            mMaxRinseTemp = SPModel.MAX_RINSE_TEMP_F;
            mElevationUnitLabel.setText(getActivity().getResources().getString(R.string.feet_abreviation));
            mBoilerTempUnitLabel.setText(getActivity().getResources().getString(R.string.fahrenheit_abreviation));
            mRinseTempUnitLabel.setText(getActivity().getResources().getString(R.string.fahrenheit_abreviation));
            mCelcius.getBackground().setAlpha(DIM);
            mFarenheit.getBackground().setAlpha(BRIGHT);
            temp *= Meters_TO_FEET;
            int rinseProgress = mRinseTempSlider.getProgress();
            double currRinseTemp = calculateValue(rinseProgress, mRinseTempSlider.getMax(), mMinRinseTemp, mMaxRinseTemp, SPModel.RINSE_TEMP_PRECISION);
            if ((int) (mMaxRinseTemp - mMinRinseTemp) != mRinseTempSlider.getMax()) {
                currRinseTemp = calculateValue(rinseProgress, mRinseTempSlider.getMax(), SPModel.MIN_RINSE_TEMP_C, SPModel.MAX_RINSE_TEMP_C, SPModel.RINSE_TEMP_PRECISION);
                currRinseTemp = SPServiceThermistor.convertFromTempToTemp(SPTempUnitType.CELCIUS, SPTempUnitType.FAHRENHEIT, currRinseTemp);
            }
            mRinseTempSlider.setMax((int) (mMaxRinseTemp - mMinRinseTemp));
            rinseProgress = calculateProgress(currRinseTemp, mRinseTempSlider.getMax(), mMinRinseTemp, mMaxRinseTemp);
            mRinseTempSlider.setProgress(rinseProgress);
            int boilerProgress = mBoilerTempSlider.getProgress();
            double currBoilerTemp = calculateValue(boilerProgress, mBoilerTempSlider.getMax(), mMinBoilerTemp, mMaxBoilerTemp, SPModel.BOILER_TEMP_PRECISION);
            if ((int) (mMaxBoilerTemp - mMinBoilerTemp) != mBoilerTempSlider.getMax()) {
                currBoilerTemp = calculateValue(boilerProgress, mBoilerTempSlider.getMax(), SPModel.MIN_BOILER_TEMP_C, SPModel.MAX_BOILER_TEMP_C, SPModel.RINSE_TEMP_PRECISION);
                currBoilerTemp = SPServiceThermistor.convertFromTempToTemp(SPTempUnitType.CELCIUS, SPTempUnitType.FAHRENHEIT, currBoilerTemp);
            }
            mBoilerTempSlider.setMax((int) (mMaxBoilerTemp - mMinBoilerTemp));
            boilerProgress = calculateProgress(currBoilerTemp, mBoilerTempSlider.getMax(), mMinBoilerTemp, mMaxBoilerTemp);
            mBoilerTempSlider.setProgress(boilerProgress);
        } else {
            mMinBoilerTemp = SPModel.MIN_BOILER_TEMP_C;
            mMaxBoilerTemp = SPModel.MAX_BOILER_TEMP_C;
            mMinRinseTemp = SPModel.MIN_RINSE_TEMP_C;
            mMaxRinseTemp = SPModel.MAX_RINSE_TEMP_C;
            mElevationUnitLabel.setText(getActivity().getResources().getString(R.string.meters_abreviation));
            mBoilerTempUnitLabel.setText(getActivity().getResources().getString(R.string.celcius_abreviation));
            mRinseTempUnitLabel.setText(getActivity().getResources().getString(R.string.celcius_abreviation));
            mFarenheit.getBackground().setAlpha(DIM);
            mCelcius.getBackground().setAlpha(BRIGHT);
            int rinseProgress = mRinseTempSlider.getProgress();
            double currRinseTemp = calculateValue(rinseProgress, mRinseTempSlider.getMax(), mMinRinseTemp, mMaxRinseTemp, SPModel.RINSE_TEMP_PRECISION);
            if ((int) (mMaxRinseTemp - mMinRinseTemp) != mRinseTempSlider.getMax()) {
                currRinseTemp = calculateValue(rinseProgress, mRinseTempSlider.getMax(), SPModel.MIN_RINSE_TEMP_F, SPModel.MAX_RINSE_TEMP_F, SPModel.RINSE_TEMP_PRECISION);
                currRinseTemp = SPServiceThermistor.convertFromTempToTemp(SPTempUnitType.FAHRENHEIT, SPTempUnitType.CELCIUS, currRinseTemp);
            }
            mRinseTempSlider.setMax((int) (mMaxRinseTemp - mMinRinseTemp));
            rinseProgress = calculateProgress(currRinseTemp, mRinseTempSlider.getMax(), mMinRinseTemp, mMaxRinseTemp);
            mRinseTempSlider.setProgress(rinseProgress);
            int boilerProgress = mBoilerTempSlider.getProgress();
            double currBoilerTemp = calculateValue(boilerProgress, mBoilerTempSlider.getMax(), mMinBoilerTemp, mMaxBoilerTemp, SPModel.BOILER_TEMP_PRECISION);
            if ((int) (mMaxBoilerTemp - mMinBoilerTemp) != mBoilerTempSlider.getMax()) {
                currBoilerTemp = calculateValue(boilerProgress, mBoilerTempSlider.getMax(), SPModel.MIN_BOILER_TEMP_F, SPModel.MAX_BOILER_TEMP_F, SPModel.RINSE_TEMP_PRECISION);
                currBoilerTemp = SPServiceThermistor.convertFromTempToTemp(SPTempUnitType.FAHRENHEIT, SPTempUnitType.CELCIUS, currBoilerTemp);
            }
            mBoilerTempSlider.setMax((int) (mMaxBoilerTemp - mMinBoilerTemp));
            boilerProgress = calculateProgress(currBoilerTemp, mBoilerTempSlider.getMax(), mMinBoilerTemp, mMaxBoilerTemp);
            mBoilerTempSlider.setProgress(boilerProgress);
        }
        if (mVolumeUnit == SPVolumeUnitType.OUNCES) {
            mMinRinseVol = SPModel.MIN_RINSE_VOL_OZ;
            mMaxRinseVol = SPModel.MAX_RINSE_VOL_OZ;
            mRinseVolUnitLabel.setText(getActivity().getResources().getString(R.string.ounces_abreviation));
            mMilliLiters.getBackground().setAlpha(DIM);
            mOunces.getBackground().setAlpha(BRIGHT);
        } else {
            mMinRinseVol = SPModel.MIN_RINSE_VOL_ML;
            mMaxRinseVol = SPModel.MAX_RINSE_VOL_ML;
            mRinseVolUnitLabel.setText(getActivity().getResources().getString(R.string.milliliters_abreviation));
            mMilliLiters.getBackground().setAlpha(BRIGHT);
            mOunces.getBackground().setAlpha(DIM);
        }
        mBoilerTempMaxLabel.setText(Double.toString(mMaxBoilerTemp));
        mBoilerTempMinLabel.setText(Double.toString(mMinBoilerTemp));
        mRinseTempMaxLabel.setText(Double.toString(mMaxRinseTemp));
        mRinseTempMinLabel.setText(Double.toString(mMinRinseTemp));
        mRinseVolMaxLabel.setText(Double.toString(mMaxRinseVol));
        mRinseVolMinLabel.setText(Double.toString(mMinRinseVol));

        temp = Math.round(temp * ONE_DECIMAL_PLACE);
        mElevationLabel.setText("" + Double.toString(temp / ONE_DECIMAL_PLACE) + "");
    }

    @Override
    public void onPause() {
        mMachineModel.deleteObserver(this);
        EventBus.getDefault().unregister(this);
        SPModel.getInstance(getActivity().getApplicationContext()).deleteObserver(this);
        super.onPause();
    }

    @Override
    public void onResume() {
        mDisableSave = false;
        mMachineModel.addObserver(this);
        EventBus.getDefault().register(this);
        super.onResume();
        mDisableBox.setVisibility(View.GONE);
        SPModel.getInstance(getActivity().getApplicationContext()).addObserver(this);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    // OnClickListener
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.save_button:
                if (!mDisableSave) {
                    for (int i = 0; i < mCrucibleCount; i++) {
                        mMachineModel.setCrucibleLocked(i, mCrucibleStates.get(i));
                    }
                    mMachineModel.setUnits(mTempUnit, mVolumeUnit);
                    mMachineModel.setMachineSettings(mBoilerTemp, mRinseTemp, mRinseVol);
                    mMachineModel.setElevation(mElevation);
                    mSerialNum = mSerialNumView.getText().toString();

                    DefaultPersistenceServiceHelper d = DefaultPersistenceServiceHelper.getInstance(getActivity().getApplicationContext());
                    d.saveMachineSettings(mSerialNum, mBoilerTemp, mRinseTemp, mRinseVol, mElevation, mCrucibleStates, mTempUnit, mVolumeUnit, mLocalOnly);

                    mDisableBox.setVisibility(View.VISIBLE);
                }
                mDisableSave = true;

                break;
            case R.id.cancel_button:
                this.getActivity().finish();
                break;
            case R.id.machine_settings_calibrate:
                if (mLocation != null && mLocation.hasAltitude()) {
                    mElevation = mLocation.getAltitude();
                    refreshUI();
                }
                break;
            case R.id.machine_settings_farenheit:
                mTempUnit = SPTempUnitType.FAHRENHEIT;
                refreshUI();
                seekBarProgressChanged(mBoilerTempSlider, mBoilerTempSlider.getProgress());
                seekBarProgressChanged(mRinseTempSlider, mRinseTempSlider.getProgress());
                break;
            case R.id.machine_settings_celcius:
                mTempUnit = SPTempUnitType.CELCIUS;
                refreshUI();
                seekBarProgressChanged(mBoilerTempSlider, mBoilerTempSlider.getProgress());
                seekBarProgressChanged(mRinseTempSlider, mRinseTempSlider.getProgress());
                break;
            case R.id.machine_settings_ml:
                mVolumeUnit = SPVolumeUnitType.MILLILITERS;
                refreshUI();
                seekBarProgressChanged(mRinseVolSlider, mRinseVolSlider.getProgress());
                break;
            case R.id.machine_settings_oz:
                mVolumeUnit = SPVolumeUnitType.OUNCES;
                refreshUI();
                seekBarProgressChanged(mRinseVolSlider, mRinseVolSlider.getProgress());
                break;
            case R.id.machine_settings_crucible_locked:
                break;
            case R.id.machine_settings_crucible_unlocked:
                break;
            default:
                break;
        }
    }

    public void onEvent(DefaultPersistenceServiceHelperEvents.InvalidMachineSerialNumberEvent serialNumEvent) {
        closeFragment(getActivity().getResources().getString(R.string.incorrect_serial_number_prompt));
    }

    public void onEvent(DefaultPersistenceServiceHelperEvents.MachineSettingsSaved settingsEvent) {
        closeFragment(getActivity().getResources().getString(R.string.steampunk_settings_saved_prompt));
    }

    public void closeFragment(String message) {
        Toast toast = Toast.makeText(getActivity(), message, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 0, 0);
        toast.show();

        mLocationManager.removeUpdates(mLocationListener);
        getActivity().finish();
    }

    @Override
    public void update(Observable observable, Object data) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mBoilerCurrentTemp = mMachineModel.getBoilerCurrentTemp();
                mBoilerCurrentTemp = SPServiceThermistor.convertFromTempToTemp(SPTempUnitType.KELVIN, mTempUnit, mBoilerCurrentTemp);
                String unit = " " + (mTempUnit == SPTempUnitType.CELCIUS ?
                        getActivity().getResources().getString(R.string.celcius_abreviation) :
                        getActivity().getResources().getString(R.string.fahrenheit_abreviation));
                mBoilerCurrentTempView.setText(String.valueOf((int) Math.round(mBoilerCurrentTemp)) + unit);
                boolean connected = mMachineModel.isConnectedToNetwork();
                mLocalOnly = !connected;
                if (connected) {
                    SPLog.debug("networking is on!");
                    mDisableNetworkingButton.setAlpha(DIM / (float) BRIGHT);
                    mEnableNetworkingButton.setAlpha(BRIGHT / (float) BRIGHT);
                } else {
                    SPLog.debug("networking is off!");
                    mDisableNetworkingButton.setAlpha(BRIGHT / (float) BRIGHT);
                    mEnableNetworkingButton.setAlpha(DIM / (float) BRIGHT);
                }
            }
        });
    }
}
