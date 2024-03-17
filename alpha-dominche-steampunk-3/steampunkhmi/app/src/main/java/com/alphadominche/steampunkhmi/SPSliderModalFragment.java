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

public class SPSliderModalFragment extends DialogFragment implements OnSeekBarChangeListener {
    private View mView = null;
    private View mSaveBtn = null;
    private View mCancelBtn = null;
    private Dialog mDialog = null;
    private SeekBar mImposter = null;
    private OnSeekBarChangeListener mListener = null;
    private boolean mUseIntValues = true;
    private int mIntMin = 0;
    private int mIntMax = 100;
    private double mDoubleMin = 0.0;
    private double mDoubleMax = 1.0;
    private String mUnits = "";
    private String mTitle = "";
    private int mDisplayDecimalPlaces = 1;
    private TextView mTitleLabel = null;
    private TextView mUnitsLabel = null;
    private TextView mMinLabel = null;
    private TextView mMaxLabel = null;
    private TextView mValueLabel = null;
    private View mUpBtn = null;
    private View mDownBtn = null;
    private SPSliderModalResponder mResponder = null;

    private int mSeekBarMax;
    private int mSeekBarProgress;
    private SPSliderColor mColor = SPSliderColor.WHITE;
    private Context mContext;

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        mView = inflater.inflate(R.layout.sp_slider_modal, null);
        builder.setView(mView);


        mSaveBtn = mView.findViewById(R.id.sp_slider_modal_save_btn);
        mCancelBtn = mView.findViewById(R.id.sp_slider_modal_cancel_btn);

        OnClickListener dismissClickListener = new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (v == mSaveBtn) {
                    SeekBar seekBar = (SeekBar) mView.findViewById(R.id.sp_slider_modal_seek_bar);
                    if (mResponder == null) {
                        mListener.onProgressChanged(mImposter, seekBar.getProgress(), true);
                    } else {
                        mResponder.setValue(seekBar.getProgress());
                    }
                } else if (v == mCancelBtn) {
                    //done!
                }

                mDialog.dismiss();
            }

        };

        mSaveBtn.setOnClickListener(dismissClickListener);
        mCancelBtn.setOnClickListener(dismissClickListener);

        if (mResponder == null) {
            ((SeekBar) mView.findViewById(R.id.sp_slider_modal_seek_bar)).setMax(mImposter.getMax());
            ((SeekBar) mView.findViewById(R.id.sp_slider_modal_seek_bar)).setProgress(mImposter.getProgress());
        } else {
            ((SeekBar) mView.findViewById(R.id.sp_slider_modal_seek_bar)).setMax(mSeekBarMax);
            ((SeekBar) mView.findViewById(R.id.sp_slider_modal_seek_bar)).setProgress(mSeekBarProgress);
        }
        ((SeekBar) mView.findViewById(R.id.sp_slider_modal_seek_bar)).setOnSeekBarChangeListener(this);

        mTitleLabel = (TextView) mView.findViewById(R.id.sp_slider_modal_title_label);
        mUnitsLabel = (TextView) mView.findViewById(R.id.sp_slider_modal_units_label);
        mMinLabel = (TextView) mView.findViewById(R.id.sp_modal_min_label);
        mMaxLabel = (TextView) mView.findViewById(R.id.sp_modal_max_label);
        mValueLabel = (TextView) mView.findViewById(R.id.sp_slider_modal_value_label);
        if (mContext != null && mColor == SPSliderColor.RED) {
            SeekBar sb = (SeekBar) mView.findViewById(R.id.sp_slider_modal_seek_bar);
            sb.setProgressDrawable(mContext.getResources().getDrawable(R.drawable.slider_large_red_bars_progress));
        } else if (mContext != null && mColor == SPSliderColor.BLUE) {
            SeekBar sb = (SeekBar) mView.findViewById(R.id.sp_slider_modal_seek_bar);
            sb.setProgressDrawable(mContext.getResources().getDrawable(R.drawable.slider_large_blue_bars_progress));
        }

        if (mUseIntValues) {
            mMinLabel.setText("" + mIntMin);
            mMaxLabel.setText("" + mIntMax);
        } else {
            mMinLabel.setText("" + formatDouble(mDoubleMin, mDisplayDecimalPlaces));
            mMaxLabel.setText("" + formatDouble(mDoubleMax, mDisplayDecimalPlaces));
        }
        mUnitsLabel.setText(mUnits);
        mTitleLabel.setText(mTitle);


        mUpBtn = mView.findViewById(R.id.sp_slider_modal_up_arrow);
        mDownBtn = mView.findViewById(R.id.sp_slider_modal_down_arrow);

        OnClickListener upDownClickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                SeekBar sb = (SeekBar) mView.findViewById(R.id.sp_slider_modal_seek_bar);
                if (v == mUpBtn) {
                    sb.setProgress(sb.getProgress() + 1);
                } else if (v == mDownBtn) {
                    sb.setProgress(sb.getProgress() - 1);
                }
                update();
            }
        };

        mUpBtn.setOnClickListener(upDownClickListener);
        mDownBtn.setOnClickListener(upDownClickListener);


        mDialog = builder.create();

        update();

        return mDialog;
    }

    public void setProgressColor(SPSliderColor c, Context context) {
        mContext = context;
        mColor = c;
    }

    public void setListenerAndImposter(OnSeekBarChangeListener listener, SeekBar imposter) {
        mImposter = imposter;
        mListener = listener;
        mResponder = null;
    }

    public void setResponder(SPSliderModalResponder responder, int max, int progress) {
        mResponder = responder;
        mSeekBarMax = max;
        mSeekBarProgress = progress;
    }

    public void setLimitsUnitsAndTitle(int min, int max, String units, String title) {
        mIntMin = min;
        mIntMax = max;
        mUseIntValues = true;
        mTitle = title;
        mUnits = units;
    }

    public void setLimitsUnitsAndTitle(int displayDecimalPlaces, double min, double max, String units, String title) {
        mDoubleMin = min;
        mDoubleMax = max;
        mDisplayDecimalPlaces = displayDecimalPlaces;
        mUseIntValues = false;
        mTitle = title;
        mUnits = units;
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
        SeekBar sb = (SeekBar) mView.findViewById(R.id.sp_slider_modal_seek_bar);
        if (mUseIntValues) {
            mValueLabel.setText("" + (sb.getProgress() * (mIntMax - mIntMin) / sb.getMax() + mIntMin));
        } else {
            mValueLabel.setText("" + formatDouble((sb.getProgress() / (double) sb.getMax()) * (mDoubleMax - mDoubleMin) + mDoubleMin, mDisplayDecimalPlaces));
        }
    }

    public static String formatDouble(double val, int decimalPlaces) {
        return String.format("%." + decimalPlaces + "f", val);
    }
}
