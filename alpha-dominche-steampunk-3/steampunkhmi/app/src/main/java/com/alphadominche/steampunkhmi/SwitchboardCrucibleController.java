package com.alphadominche.steampunkhmi;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

public class SwitchboardCrucibleController implements SPCrucibleObserver {
    private int mIndex;

    private Context mContext;
    private View mView;

    private View mSteamBtn;
    private View mFillBtn;
    private View mDrainBtn;
    private TextView mTempLabel;
    private TextView mFlowCountLabel;

    private boolean mActive;

    SwitchboardCrucibleController(int index, SPActivity context) {
        mIndex = index;
        mContext = context;

        mView = context.findViewById(context.getResources().getIdentifier("crucibleLayout" + mIndex, SPActivity.R_ID_STR, context.getPackageName()));

        mTempLabel = (TextView) mView.findViewById(R.id.crucible_temp_label);
        mFlowCountLabel = (TextView) mView.findViewById(R.id.flow_meter_count_label);
        mSteamBtn = mView.findViewById(R.id.steam_button);
        mFillBtn = mView.findViewById(R.id.water_button);
        mDrainBtn = mView.findViewById(R.id.actuator_button);

        SPLog.debug("fill button: (" + mIndex + ") " + mFillBtn);

        mFillBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                SPModel model = SPModel.getInstance(mContext);

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    model.turnCrucibleFillOn(mIndex);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    model.turnCrucibleFillOff(mIndex);

                    v.performClick();
                }

                return true;
            }
        });

        mSteamBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                SPModel model = SPModel.getInstance(mContext);

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    model.turnCrucibleSteamOn(mIndex);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    model.turnCrucibleSteamOff(mIndex);

                    v.performClick();
                }

                return true;
            }
        });

        mDrainBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                SPModel model = SPModel.getInstance(mContext);

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    model.turnCrucibleDrainOn(mIndex);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    model.turnCrucibleDrainOff(mIndex);

                    v.performClick();
                }

                return true;
            }
        });

        SPModel.getInstance(context).addCrucibleObserver(this, index);
    }

    public void resume() {
        mActive = true;
    }

    public void pause() {
        mActive = false;
    }

    public void show() {
        mView.setVisibility(View.VISIBLE);
    }

    public void hide() {
        mView.setVisibility(View.GONE);
    }

    public void update() {
        if (!mActive) {
            return;
        }

        SPModel model = SPModel.getInstance(mContext);
        int edges = model.getEdgesForCrucible(mIndex);
        double kelvins = model.getTempForCrucible(mIndex);
        boolean filling = model.isCrucibleFilling(mIndex);
        boolean steaming = model.isCrucibleSteaming(mIndex);
        boolean draining = model.isCrucibleDraining(mIndex);
        double convertedTemp = SPServiceThermistor.convertFromTempToTemp(SPTempUnitType.KELVIN, model.getTempUnits(), kelvins);

        mFlowCountLabel.setText("" + edges);
        mTempLabel.setText("" + Math.round(convertedTemp));

        if (steaming) {
            mSteamBtn.setBackgroundResource(R.drawable.crucible_9_patch_glow);
        } else {
            mSteamBtn.setBackgroundResource(R.drawable.crucible_9_patch);
        }
        if (filling) {
            mFillBtn.setBackgroundResource(R.drawable.crucible_9_patch_glow);
        } else {
            mFillBtn.setBackgroundResource(R.drawable.crucible_9_patch);
        }
        if (draining) {
            mDrainBtn.setBackgroundResource(R.drawable.crucible_9_patch_glow);
        } else {
            mDrainBtn.setBackgroundResource(R.drawable.crucible_9_patch);
        }
    }

    public void notifyOfCrucibleChange(int index) {
        if (index == mIndex) {
            ((SPActivity) mContext).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    update();
                }
            });
        }
    }
}
