package com.alphadominche.steampunkhmi;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class SPAgitationEditorModalFragment extends DialogFragment implements
        OnSeekBarChangeListener {

    private View mView = null;
    private View mSaveBtn = null;
    private View mCancelBtn = null;
    private Dialog mDialog = null;
    private int mStartMin = 0;
    private int mStartMax = 100;
    private double mDurationMin = 0.0;
    private double mDurationMax = 15.0;
    private double mPulseMin = 0.0;
    private double mPulseMax = 1.0;
    private String mStartUnits = "";
    private String mStartTitle = "";
    private String mDurationUnits = "";
    private String mDurationTitle = "";
    private String mPulseUnits = "";
    private String mPulseTitle = "";
    private int mDisplayDecimalPlaces = 1;
    private TextView mStartTitleLabel = null;
    private TextView mStartUnitsLabel = null;
    private TextView mDurationTitleLabel = null;
    private TextView mDurationUnitsLabel = null;
    private TextView mPulseTitleLabel = null;
    private TextView mPulseUnitsLabel = null;
    private TextView mStartMinLabel = null;
    private TextView mStartMaxLabel = null;
    private TextView mDurationMinLabel = null;
    private TextView mDurationMaxLabel = null;
    private TextView mPulseMinLabel = null;
    private TextView mPulseMaxLabel = null;
    private TextView mStartValueLabel = null;
    private TextView mDurationValueLabel = null;
    private TextView mPulseValueLabel = null;
    private View mStartUpBtn = null;
    private View mStartDownBtn = null;
    private View mDurationUpBtn = null;
    private View mDurationDownBtn = null;
    private View mPulseUpBtn = null;
    private View mPulseDownBtn = null;
    private SPSliderModalResponder mStartTimeResponder = null; // these are expected to be populated by
    private SPSliderModalResponder mDurationResponder = null; // the time this fragment's onCreateDialog()
    private SPSliderModalResponder mPulseWidthResponder = null; // has been called

    private int mStartTimeSeekBarMax;
    private int mStartTimeSeekBarProgress;
    private int mDurationSeekBarMax;
    private int mDurationSeekBarProgress;
    private int mPulseWidthSeekBarMax;
    private int mPulseWidthSeekBarProgress;
    private SPSliderColor mStartTimeColor = SPSliderColor.WHITE;
    private SPSliderColor mDurationColor = SPSliderColor.WHITE;
    private SPSliderColor mPulseWidthColor = SPSliderColor.WHITE;
    private Context mContext;

    private int mLimitingTotal;
    private boolean mIgnoreUpdates = false;
    private boolean mIsFirstAg = false;
//	private SPRecipeAgSliderInfo mInfo;

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        mView = inflater.inflate(R.layout.sp_agitation_sliders_modal, null);
        builder.setView(mView);


        mSaveBtn = mView.findViewById(R.id.sp_slider_modal_save_btn);
        mCancelBtn = mView.findViewById(R.id.sp_slider_modal_cancel_btn);

        OnClickListener dismissClickListener = new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (v == mSaveBtn) {
                    View group = mView.findViewById(R.id.ag_start_slider_group);
                    SeekBar seekBar = (SeekBar) group.findViewById(R.id.seek_bar);
                    mStartTimeResponder.setValue(seekBar.getProgress());
                    group = mView.findViewById(R.id.ag_duration_slider_group);
                    seekBar = (SeekBar) group.findViewById(R.id.seek_bar);
                    mDurationResponder.setValue(seekBar.getProgress());
                    group = mView.findViewById(R.id.ag_pulse_width_slider_group);
                    seekBar = (SeekBar) group.findViewById(R.id.seek_bar);
                    mPulseWidthResponder.setValue(seekBar.getProgress());
                } else if (v == mCancelBtn) {
                    //done!
                }

                mDialog.dismiss();
            }

        };

        mSaveBtn.setOnClickListener(dismissClickListener);
        mCancelBtn.setOnClickListener(dismissClickListener);


        //Start Time widgets
        View group = mView.findViewById(R.id.ag_start_slider_group);
        SeekBar sb = (SeekBar) group.findViewById(R.id.seek_bar);
        sb.setMax(mStartTimeSeekBarMax);
        sb.setProgress(mStartTimeSeekBarProgress);
        sb.setOnSeekBarChangeListener(this);
        mStartTitleLabel = (TextView) group.findViewById(R.id.title_label);
        mStartUnitsLabel = (TextView) group.findViewById(R.id.units_label);
        mStartMinLabel = (TextView) mView.findViewById(R.id.start_min_label);
        mStartMaxLabel = (TextView) mView.findViewById(R.id.start_max_label);
        mStartValueLabel = (TextView) group.findViewById(R.id.value_label);
        if (mContext != null && mStartTimeColor == SPSliderColor.RED) {
            sb = (SeekBar) group.findViewById(R.id.seek_bar);
            sb.setProgressDrawable(mContext.getResources().getDrawable(R.drawable.slider_large_red_bars_progress));
        } else if (mContext != null && mStartTimeColor == SPSliderColor.BLUE) {
            sb = (SeekBar) group.findViewById(R.id.seek_bar);
            sb.setProgressDrawable(mContext.getResources().getDrawable(R.drawable.slider_large_blue_bars_progress));
        }

        mStartMinLabel.setText("" + mStartMin);
        mStartMaxLabel.setText("" + mStartMax);
        mStartUnitsLabel.setText(mStartUnits);
        mStartTitleLabel.setText(mStartTitle);


        mStartUpBtn = mView.findViewById(R.id.up_arrow);
        mStartDownBtn = mView.findViewById(R.id.down_arrow);

        OnClickListener upDownStartClickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                View group = mView.findViewById(R.id.ag_start_slider_group);
                SeekBar sb = (SeekBar) group.findViewById(R.id.seek_bar);
                if (v == mStartUpBtn) {
                    sb.setProgress(sb.getProgress() + 1);
                } else if (v == mStartDownBtn) {
                    sb.setProgress(sb.getProgress() - 1);
                }
                update();
            }
        };

        mStartUpBtn.setOnClickListener(upDownStartClickListener);
        mStartDownBtn.setOnClickListener(upDownStartClickListener);


        //Duration widgets
        group = mView.findViewById(R.id.ag_duration_slider_group);
        sb = (SeekBar) group.findViewById(R.id.seek_bar);
        sb.setMax(mDurationSeekBarMax);
        sb.setProgress(mDurationSeekBarProgress);
        sb.setOnSeekBarChangeListener(this);
        mDurationTitleLabel = (TextView) group.findViewById(R.id.title_label);
        mDurationUnitsLabel = (TextView) group.findViewById(R.id.units_label);
        mDurationMinLabel = (TextView) mView.findViewById(R.id.duration_min_label);
        mDurationMaxLabel = (TextView) mView.findViewById(R.id.duration_max_label);
        mDurationValueLabel = (TextView) group.findViewById(R.id.value_label);
        if (mContext != null && mDurationColor == SPSliderColor.RED) {
            sb = (SeekBar) group.findViewById(R.id.seek_bar);
            sb.setProgressDrawable(mContext.getResources().getDrawable(R.drawable.slider_large_red_bars_progress));
        } else if (mContext != null && mDurationColor == SPSliderColor.BLUE) {
            sb = (SeekBar) group.findViewById(R.id.seek_bar);
            sb.setProgressDrawable(mContext.getResources().getDrawable(R.drawable.slider_large_blue_bars_progress));
        }

        mDurationMinLabel.setText("" + formatDouble(mDurationMin, mDisplayDecimalPlaces));
        mDurationMaxLabel.setText("" + formatDouble(mDurationMax, mDisplayDecimalPlaces));
        mDurationUnitsLabel.setText(mDurationUnits);
        mDurationTitleLabel.setText(mDurationTitle);


        mDurationUpBtn = group.findViewById(R.id.up_arrow);
        mDurationDownBtn = group.findViewById(R.id.down_arrow);

        OnClickListener upDownDurationClickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                View group = mView.findViewById(R.id.ag_duration_slider_group);
                SeekBar sb = (SeekBar) group.findViewById(R.id.seek_bar);
                if (v == mDurationUpBtn) {
                    sb.setProgress(sb.getProgress() + 1);
                } else if (v == mDurationDownBtn) {
                    sb.setProgress(sb.getProgress() - 1);
                }
                update();
            }
        };

        mDurationUpBtn.setOnClickListener(upDownDurationClickListener);
        mDurationDownBtn.setOnClickListener(upDownDurationClickListener);


        //Pulse Width widgets
        group = mView.findViewById(R.id.ag_pulse_width_slider_group);
        sb = (SeekBar) group.findViewById(R.id.seek_bar);
        sb.setMax(mPulseWidthSeekBarMax);
        sb.setProgress(mPulseWidthSeekBarProgress);
        sb.setOnSeekBarChangeListener(this);
        mPulseTitleLabel = (TextView) group.findViewById(R.id.title_label);
        mPulseUnitsLabel = (TextView) group.findViewById(R.id.units_label);
        mPulseMinLabel = (TextView) mView.findViewById(R.id.pulse_min_label);
        mPulseMaxLabel = (TextView) mView.findViewById(R.id.pulse_max_label);
        mPulseValueLabel = (TextView) group.findViewById(R.id.value_label);
        if (mContext != null && mPulseWidthColor == SPSliderColor.RED) {
            sb = (SeekBar) group.findViewById(R.id.seek_bar);
            sb.setProgressDrawable(mContext.getResources().getDrawable(R.drawable.slider_large_red_bars_progress));
        } else if (mContext != null && mPulseWidthColor == SPSliderColor.BLUE) {
            sb = (SeekBar) group.findViewById(R.id.seek_bar);
            sb.setProgressDrawable(mContext.getResources().getDrawable(R.drawable.slider_large_blue_bars_progress));
        }

        mPulseMinLabel.setText("" + formatDouble(mPulseMin, mDisplayDecimalPlaces));
        mPulseMaxLabel.setText("" + formatDouble(mPulseMax, mDisplayDecimalPlaces));
        mPulseUnitsLabel.setText(mPulseUnits);
        mPulseTitleLabel.setText(mPulseTitle);


        mPulseUpBtn = group.findViewById(R.id.up_arrow);
        mPulseDownBtn = group.findViewById(R.id.down_arrow);

        OnClickListener upDownPulseClickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                View group = mView.findViewById(R.id.ag_pulse_width_slider_group);
                SeekBar sb = (SeekBar) group.findViewById(R.id.seek_bar);
                if (v == mPulseUpBtn) {
                    sb.setProgress(sb.getProgress() + 1);
                } else if (v == mPulseDownBtn) {
                    sb.setProgress(sb.getProgress() - 1);
                }
                update();
            }
        };

        mPulseUpBtn.setOnClickListener(upDownPulseClickListener);
        mPulseDownBtn.setOnClickListener(upDownPulseClickListener);


        mDialog = builder.create();

        update();

        return mDialog;
    }

    public void setStartTimeProgressColor(SPSliderColor c, Context context) {
        mContext = context;
        mStartTimeColor = c;
    }

    public void setDurationProgressColor(SPSliderColor c, Context context) {
        mContext = context;
        mDurationColor = c;
    }

    public void setPulseWidthProgressColor(SPSliderColor c, Context context) {
        mContext = context;
        mPulseWidthColor = c;
    }

    public void setStartTimeResponder(SPSliderModalResponder responder, int max, int progress) {
        if (responder != null) mStartTimeResponder = responder;
        mStartTimeSeekBarMax = max;
        mStartTimeSeekBarProgress = progress;
    }

    public void setDurationResponder(SPSliderModalResponder responder, int max, int progress) {
        if (responder != null) mDurationResponder = responder;
        mDurationSeekBarMax = max;
        mDurationSeekBarProgress = progress;
        mLimitingTotal = Math.max(mStartMax + wholeNumberValue(mDurationSeekBarProgress / 10.0), wholeNumberValue(mDurationMax));
        SPLog.debug("SET LIMITING TOTAL: " + mLimitingTotal + " setMax: " + max);
    }

    public void setPulseWidthResponder(SPSliderModalResponder responder, int max, int progress) {
        if (responder != null) mPulseWidthResponder = responder;
        mPulseWidthSeekBarMax = max;
        mPulseWidthSeekBarProgress = progress;
    }

    public void setStartLimitsUnitsAndTitle(int min, int max, String units, String title) {
        mStartMin = min;
        mStartMax = max;
        mStartTitle = title;
        mStartUnits = units;
        mIsFirstAg = mStartMax == 0 && mStartMin == 0;
        mLimitingTotal = Math.max(mStartMax + wholeNumberValue(mDurationSeekBarProgress / 10.0), wholeNumberValue(mDurationMax));
        SPLog.debug("SET LIMITING TOTAL: " + mLimitingTotal + " setMax: " + max);
    }

    public void setDurationLimitsUnitsAndTitle(double min, double max, String units, String title) {
        mDurationMin = min;
        mDurationMax = max;
        mDisplayDecimalPlaces = 1;
        mDurationTitle = title;
        mDurationUnits = units;
        mLimitingTotal = Math.max(mStartMax + wholeNumberValue(mDurationSeekBarProgress / 10.0), wholeNumberValue(mDurationMax));
        SPLog.debug("SET LIMITING TOTAL: " + mLimitingTotal + " setMax: " + max);
    }

    public void setPulseLimitsUnitsAndTitle(double min, double max, String units, String title) {
        mPulseMin = min;
        mPulseMax = max;
        mDisplayDecimalPlaces = 1;
        mPulseTitle = title;
        mPulseUnits = units;
    }

    public void setInfo(SPRecipeAgSliderInfo info) {
//		mInfo = info;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        // TODO Auto-generated method stub
        update();
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        // TODO Auto-generated method stub
    }

    private void update() {
        if (mIgnoreUpdates) return;
        mIgnoreUpdates = true;
        //update constraints first!
//		setStartLimitsUnitsAndTitle(mInfo.getStartTimeMin(), mInfo.getStartTimeMax(), getActivity().getString(R.string.seconds_abbreviation), getActivity().getString(R.string.start_time_slider_title));
//		setDurationLimitsUnitsAndTitle(mInfo.getDurationMin(), mInfo.getDurationMax(), getActivity().getString(R.string.seconds_abbreviation), getActivity().getString(R.string.duration_slider_title));
//		setPulseLimitsUnitsAndTitle(mInfo.getPulseWidthMin(), mInfo.getPulseWidthMax(), getActivity().getString(R.string.seconds_abbreviation), getActivity().getString(R.string.pulse_width_slider_title));
//		setStartTimeResponder(null, mInfo.getStartTimeProgressMax(), mInfo.getStartTimeProgress());
//		setDurationResponder(null, mInfo.getDurationProgressMax(), mInfo.getDurationProgress());
//		setPulseWidthResponder(null, mInfo.getPulseWidthProgressMax(), mInfo.getPulseWidthProgress());
        View group = mView.findViewById(R.id.ag_duration_slider_group);
        SeekBar sb = (SeekBar) group.findViewById(R.id.seek_bar);
        if (!mIsFirstAg) mStartMax = mLimitingTotal - wholeNumberValue(sb.getProgress() / 10.0);
        SPLog.debug("limiting total: " + mLimitingTotal + " startMax: " + mStartMax + " duration: " + wholeNumberValue(sb.getProgress() / 10.0) + "(" + sb.getProgress() + ")");
        group = mView.findViewById(R.id.ag_start_slider_group);
        sb = (SeekBar) group.findViewById(R.id.seek_bar);
        sb.setMax(mStartMax - mStartMin);
        SPLog.debug("start max: " + sb.getMax());
        mDurationMax = Math.min(mLimitingTotal - sb.getProgress() - mStartMin, 15.0);
        group = mView.findViewById(R.id.ag_duration_slider_group);
        sb = (SeekBar) group.findViewById(R.id.seek_bar);
        sb.setMax((int) mDurationMax * 10);

        mIgnoreUpdates = false;

        //then update the display!
        group = mView.findViewById(R.id.ag_start_slider_group);
        sb = (SeekBar) group.findViewById(R.id.seek_bar);
        double scaleTerm = sb.getMax() == 0 ? 0 : (mStartMax - mStartMin) / sb.getMax();
        mStartValueLabel.setText("" + (int) (sb.getProgress() * scaleTerm + mStartMin));
        group = mView.findViewById(R.id.ag_duration_slider_group);
        sb = (SeekBar) group.findViewById(R.id.seek_bar);
        scaleTerm = sb.getMax() == 0 ? 0 : 1.0 / (double) sb.getMax();
        mDurationValueLabel.setText("" + formatDouble((sb.getProgress() * scaleTerm) * (mDurationMax - mDurationMin) + mDurationMin, mDisplayDecimalPlaces));
        group = mView.findViewById(R.id.ag_pulse_width_slider_group);
        sb = (SeekBar) group.findViewById(R.id.seek_bar);
        scaleTerm = sb.getMax() == 0 ? 0 : 1.0 / (double) sb.getMax();
        mPulseValueLabel.setText("" + formatDouble((sb.getProgress() * scaleTerm) * (mPulseMax - mPulseMin) + mPulseMin, mDisplayDecimalPlaces));

        mStartMaxLabel.setText("" + mStartMax);
        mDurationMaxLabel.setText("" + formatDouble(mDurationMax, mDisplayDecimalPlaces));
    }

    private static boolean significantlyNonInteger(double val) {
        return Math.abs(val - Math.floor(val)) > SPRecipeDefaults.COMPARISON_TOLERANCE;
    }

    private int wholeNumberValue(double val) {
        if (significantlyNonInteger(val)) {
            return (int) (Math.ceil(val));
        } else {
            return (int) (Math.floor(val + 0.5));
        }
    }

    public static String formatDouble(double val, int decimalPlaces) {
        return String.format("%." + decimalPlaces + "f", val);
    }
}
