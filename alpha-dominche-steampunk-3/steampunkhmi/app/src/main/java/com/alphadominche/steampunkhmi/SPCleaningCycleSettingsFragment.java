package com.alphadominche.steampunkhmi;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

/**
 * @author zack
 */
public class SPCleaningCycleSettingsFragment extends SPFragment {

    private ImageView mCloseButton;
    private ImageView mSaveButton;

    private SeekBar mTempSlider;
    private SeekBar mVolSlider;

    private SPTempUnitType mTempUnit;
    private SPVolumeUnitType mVolumeUnit;

    private Double mCleaningTemp;
    private Double mMinCleaningTemp;
    private Double mMaxCleaningTemp;
    private Double mCleaningVol;
    private Double mMinCleaningVol;
    private Double mMaxCleaningVol;

    private TextView mCleaningTempLabel;
    private TextView mCleaningVolLabel;

    private TextView mTempUnitLabel;
    private TextView mVolUnitLabel;
    private TextView mMinCleaningTempLabel;
    private TextView mMaxCleaningTempLabel;
    private TextView mMinCleaningVolLabel;
    private TextView mMaxCleaningVolLabel;

    SPModel model;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.sp_cleaning_cycle_settings, container, false);

        mTempUnitLabel = (TextView) rootView.findViewById(R.id.sp_slider_cleaning_temp_unit_label);
        mVolUnitLabel = (TextView) rootView.findViewById(R.id.sp_slider_cleaning_vol_unit_label);
        mMinCleaningTempLabel = (TextView) rootView.findViewById(R.id.sp_slider_temp_min_label);
        mMaxCleaningTempLabel = (TextView) rootView.findViewById(R.id.sp_slider_temp_max_label);
        mMinCleaningVolLabel = (TextView) rootView.findViewById(R.id.sp_slider_vol_min_label);
        mMaxCleaningVolLabel = (TextView) rootView.findViewById(R.id.sp_slider_vol_max_label);
        mTempSlider = (SeekBar) rootView.findViewById(R.id.cleaning_cycle_settings_temp_slider);
        mVolSlider = (SeekBar) rootView.findViewById(R.id.cleaning_cycle_settings_vol_slider);
        mCleaningTempLabel = (TextView) rootView.findViewById(R.id.sp_slider_cleaning_temp_label);
        mCleaningVolLabel = (TextView) rootView.findViewById(R.id.sp_slider_cleaning_vol_label);

        model = SPModel.getInstance(getActivity());

        mTempUnit = model.getTempUnits();
        mVolumeUnit = model.getVolumeUnits();

        mCleaningTemp = model.getCleaningTemp();
        mCleaningVol = model.getCleaningVolume();

        mCleaningTemp = (double) Math.round(SPServiceThermistor.convertFromTempToTemp(SPTempUnitType.KELVIN, mTempUnit, mCleaningTemp));
        if (mVolumeUnit == SPVolumeUnitType.OUNCES) {
            mCleaningVol = SPFlowMeter.convertFromMillilitersToOunces(mCleaningVol);
        }
        mCleaningVol = (double) Math.round(mCleaningVol);

        if (mTempUnit == SPTempUnitType.FAHRENHEIT) {
            mTempUnitLabel.setText(getActivity().getResources().getString(R.string.fahrenheit_abreviation));
            mMaxCleaningTemp = SPModel.MAX_CLEANING_TEMP_F;
            mMinCleaningTemp = SPModel.MIN_CLEANING_TEMP_F;
        } else {
            mTempUnitLabel.setText(getActivity().getResources().getString(R.string.celcius_abreviation));
            mMaxCleaningTemp = SPModel.MAX_CLEANING_TEMP_C;
            mMinCleaningTemp = SPModel.MIN_CLEANING_TEMP_C;
        }

        if (mVolumeUnit == SPVolumeUnitType.OUNCES) {
            mVolUnitLabel.setText(getActivity().getResources().getString(R.string.ounces_abreviation));
            mMaxCleaningVol = SPModel.MAX_RINSE_VOL_OZ;
            mMinCleaningVol = SPModel.MIN_RINSE_VOL_OZ;
        } else {
            mVolUnitLabel.setText(getActivity().getResources().getString(R.string.milliliters_abreviation));
            mMaxCleaningVol = SPModel.MAX_RINSE_VOL_ML;
            mMinCleaningVol = SPModel.MIN_RINSE_VOL_ML;
        }

        mCleaningTempLabel.setText("" + mCleaningTemp + HomeCrucibleController.DEGREE_SYMBOL);
        mCleaningVolLabel.setText("" + mCleaningVol + "");
        mMinCleaningTempLabel.setText("" + mMinCleaningTemp + HomeCrucibleController.DEGREE_SYMBOL);
        mMaxCleaningTempLabel.setText("" + mMaxCleaningTemp + HomeCrucibleController.DEGREE_SYMBOL);
        mMinCleaningVolLabel.setText("" + mMinCleaningVol + "");
        mMaxCleaningVolLabel.setText("" + mMaxCleaningVol + "");

        mTempSlider.setProgress(calculateProgress(mCleaningTemp,
                mTempSlider.getMax(), mMinCleaningTemp, mMaxCleaningTemp));
        mVolSlider.setProgress(calculateProgress(mCleaningVol,
                mVolSlider.getMax(), mMinCleaningVol, mMaxCleaningVol));

        OnSeekBarChangeListener listener = getSeekBarListener();

        mTempSlider.setOnSeekBarChangeListener(listener);
        mVolSlider.setOnSeekBarChangeListener(listener);

        mCloseButton = (ImageView) rootView.findViewById(R.id.cleaning_cycle_settings_cancel_button);
        mCloseButton.setOnClickListener(this);

        mSaveButton = (ImageView) rootView.findViewById(R.id.cleaning_cycle_settings_save_button);
        mSaveButton.setOnClickListener(this);

        return rootView;
    }

    // OnClickListener
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.cleaning_cycle_settings_save_button) {
            double cleaningVol = mCleaningVol;
            if (model.getVolumeUnits() == SPVolumeUnitType.OUNCES) {
                cleaningVol = SPFlowMeter.convertFromOuncesToMilliliters(cleaningVol);
            }
            double cleaningTemp = SPServiceThermistor.convertFromTempToTemp(model.getTempUnits(), SPTempUnitType.KELVIN, mCleaningTemp);
            model.setCleaningSettings(cleaningTemp, cleaningVol);
            this.getActivity().finish();
        } else if (view.getId() == R.id.cleaning_cycle_settings_cancel_button) {
            this.getActivity().finish();
        }
    }

    private OnSeekBarChangeListener getSeekBarListener() {
        return new OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekbar, int progress, boolean arg2) {
                if (seekbar == mTempSlider) {
                    mCleaningTemp = calculateValue(progress, seekbar.getMax(), mMinCleaningTemp, mMaxCleaningTemp, SPModel.BOILER_TEMP_PRECISION);
                    mCleaningTempLabel.setText("" + mCleaningTemp + HomeCrucibleController.DEGREE_SYMBOL);
                    mTempSlider.setProgress(calculateProgress(mCleaningTemp, mTempSlider.getMax(), mMinCleaningTemp, mMaxCleaningTemp));
                } else if (seekbar == mVolSlider) {
                    mCleaningVol = calculateValue(progress, seekbar.getMax(), mMinCleaningVol, mMaxCleaningVol, SPModel.RINSE_VOL_PRECISION);
                    mCleaningVolLabel.setText("" + mCleaningVol + "");
                    mVolSlider.setProgress(calculateProgress(mCleaningVol, mVolSlider.getMax(), mMinCleaningVol, mMaxCleaningVol));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar arg0) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar arg0) {
            }

        };
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}
