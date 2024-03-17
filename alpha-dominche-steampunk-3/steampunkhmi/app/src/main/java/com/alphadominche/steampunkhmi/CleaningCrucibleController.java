package com.alphadominche.steampunkhmi;

import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.alphadominche.steampunkhmi.HomeCrucibleController.BorderStyle;

public class CleaningCrucibleController implements SPCrucibleObserver {
    public static final int MERCURY_TOP = 145; //maximum temperature
    public static final int MERCURY_BOTTOM = 350; //minimum temperature
    public static final int MERCURY_ZERO_HEIGHT = 50; //0 degrees fahrenheight
    public static final int MERCURY_LEFT = 35;
    public static final int MAX_TEMP = 374;
    public static final int MIN_TEMP = 255;
    public static final float BIG_DROP_START_ALPHA_DELTA = 0.05f;
    public static int ONE_TENTH_SECOND_IN_MILLIS = 100;
    public static int FULLY_OPAQUE_ALPHA = 255;
    public static int ONE_HALF_SECOND_IN_MILLIS = 500;
    public static int PULSE_ALPHA_STEP = 26;
    public static int FULL_TRANSPARENT_ALPHA = 0;

    private Timer mFlashTimer;
    private Timer mAnimTimer;
    private float mDensity;

    private View mWholeLayout;

    private TextView mStatusLabel;

    private int mCrucibleIndex;
    private SPCrucibleState mPreviousState;
    private SPModel mModel;
    private SPActivity mView;

    private FrameLayout mCrucibleViewBox;
    private ArrayList<View> crucibleElements;

    private ImageView mContainer;
    private ImageView mThermometer;
    private ImageView[] mDroplet;
    private ImageView mPiston;
    private ImageView mDownArrow;
    private ImageView mSteamArrows;
    private ImageView mBigDrop;
    private float mBigDropAlpha;
    private float mBigDropAlphaDelta;
    private ImageView mXButton;
    private ImageView mDropButton;
    private View mMercury;
    private TextView mFillLabel;
    private TextView mTopNumberLabel;
    private TextView mBottomNumberLabel;

    private ImageView mDropletHider;

    private boolean mShowingErrorView;
    private boolean mWasLocked;

    CleaningCrucibleController(SPActivity view, int index) {
        mView = view;
        mModel = SPModel.getInstance(mView);
        mCrucibleIndex = index;
        mDensity = mView.getResources().getDisplayMetrics().density;
    }

    public void resume() {
        viewBecameActive();
        update();
    }

    public void pause() {
        viewBecameInactive();
    }

    public void update() {
        if (mModel.isCrucibleLocked(mCrucibleIndex)) {
            showDisabledView();
            mWasLocked = true;
            return;
        } else if (mWasLocked) {
            mWasLocked = false;
            showIdleView();
        }

        if (mModel.crucibleInErrorState(mCrucibleIndex)) {
            showErrorView();
        }

        SPCrucibleState state = mModel.getStateForCrucible(mCrucibleIndex);

        if (mPreviousState != state || mShowingErrorView) {
            if (state == SPCrucibleState.IDLE) {
                showIdleView();
            } else if (state == SPCrucibleState.CLEANING_FILL_AND_HEAT) {
                showFillAndHeatView();
            } else if (state == SPCrucibleState.CLEANING_SOAK) {
                showRinsingView();
            } else if (state == SPCrucibleState.CLEANING_DRAIN) {
                showRinsingView();
            } else if (state == SPCrucibleState.CLEANING_AGITATING) {
                showRinsingView();
            } else if (state == SPCrucibleState.WAITING_FOR_CLEANING_RINSE) {
                showStartRinsingView();
            } else if (state == SPCrucibleState.CLEANING_RINSE_FILL_AND_HEAT) {
                showRinseFillAndHeatView();
            } else if (state == SPCrucibleState.CLEANING_RINSE_SOAK) {
                showRinsingView();
            } else if (state == SPCrucibleState.CLEANING_RINSE_DRAIN) {
                showRinsingView();
            } else if (state == SPCrucibleState.CLEANING_RINSE_AGITATING) {
                showRinsingView();
            }
        }

        if (state == SPCrucibleState.CLEANING_FILL_AND_HEAT || state == SPCrucibleState.CLEANING_RINSE_FILL_AND_HEAT) {
            updateFillAndHeatWidgets();
        } else if (state == SPCrucibleState.CLEANING_DRAIN || state == SPCrucibleState.CLEANING_AGITATING) {
            updateCleanDrainAndAgitationWidgets();
        } else if (state == SPCrucibleState.CLEANING_RINSE_DRAIN || state == SPCrucibleState.CLEANING_RINSE_AGITATING) {
            updateRinseDrainAndAgitationWidgets();
        }

    }

    private void updateFillAndHeatWidgets() {
        double cleanTemp = SPServiceThermistor.convertFromTempToTemp(SPTempUnitType.KELVIN, mModel.getTempUnits(), mModel.getCleaningTemp());
        mTopNumberLabel.setText(""
                + Math.round(cleanTemp) + HomeCrucibleController.DEGREE_SYMBOL);
        double crucibleTemp = mModel.getTempForCrucible(mCrucibleIndex);
        double temp = SPServiceThermistor.convertFromTempToTemp(SPTempUnitType.KELVIN, mModel.getTempUnits(), crucibleTemp);
        mBottomNumberLabel.setText(""
                + Math.round(temp) + HomeCrucibleController.DEGREE_SYMBOL);

        //scale mercury top position proportional to temperature (212/boil max)
        int newTop = (int) ((MERCURY_BOTTOM - MERCURY_TOP) * (mModel.getTempForCrucible(mCrucibleIndex) - MIN_TEMP));
        newTop /= (MAX_TEMP - MIN_TEMP);
        if (newTop > (MERCURY_BOTTOM - MERCURY_TOP)) newTop = (MERCURY_BOTTOM - MERCURY_TOP);
        newTop = MERCURY_BOTTOM - newTop;
        newTop = (int) (newTop * mDensity);
        FrameLayout.LayoutParams newParams = new FrameLayout.LayoutParams(mMercury.getLayoutParams());
        int newLeft = (int) (MERCURY_LEFT * mDensity);
        newParams.setMargins(newLeft, newTop, newParams.rightMargin, newParams.bottomMargin);

        //scale mercury height proportional to temperature
        int newHeight = (int) ((MERCURY_BOTTOM - MERCURY_TOP) * (mModel.getTempForCrucible(mCrucibleIndex) - MIN_TEMP));
        newHeight /= (MAX_TEMP - MIN_TEMP);
        if (newHeight > (MERCURY_BOTTOM - MERCURY_TOP)) newHeight = (MERCURY_BOTTOM - MERCURY_TOP);
        newHeight += MERCURY_ZERO_HEIGHT;
        newParams.height = (int) (newHeight * mDensity);
        mMercury.setLayoutParams(newParams);
    }

    private void updateCleanDrainAndAgitationWidgets() {
        double cleanTemp = SPServiceThermistor.convertFromTempToTemp(SPTempUnitType.KELVIN, mModel.getTempUnits(), mModel.getCleaningTemp());
        mTopNumberLabel.setText(""
                + Math.round(cleanTemp) + HomeCrucibleController.DEGREE_SYMBOL);
        double crucibleTemp = mModel.getTempForCrucible(mCrucibleIndex);
        double temp = SPServiceThermistor.convertFromTempToTemp(SPTempUnitType.KELVIN, mModel.getTempUnits(), crucibleTemp);
        mBottomNumberLabel.setText(""
                + Math.round(temp) + HomeCrucibleController.DEGREE_SYMBOL);
    }

    private void updateRinseDrainAndAgitationWidgets() {
        double cleanTemp = SPServiceThermistor.convertFromTempToTemp(SPTempUnitType.KELVIN, mModel.getTempUnits(), mModel.getCleaningTemp());
        mTopNumberLabel.setText(""
                + Math.round(cleanTemp) + HomeCrucibleController.DEGREE_SYMBOL);
        double crucibleTemp = mModel.getTempForCrucible(mCrucibleIndex);
        double temp = SPServiceThermistor.convertFromTempToTemp(SPTempUnitType.KELVIN, mModel.getTempUnits(), crucibleTemp);
        mBottomNumberLabel.setText(""
                + Math.round(temp) + HomeCrucibleController.DEGREE_SYMBOL);
    }

    public void viewBecameActive() {
        // set up widget links here

        mModel.addCrucibleObserver(this, mCrucibleIndex);

        int ID = mView.getResources().getIdentifier("statusLabel" + mCrucibleIndex, SPActivity.R_ID_STR, mView.getPackageName());
        mStatusLabel = (TextView) mView.findViewById(ID);

        //outer widget variables
        ID = mView.getResources().getIdentifier("crucibleLayout" + mCrucibleIndex, SPActivity.R_ID_STR, mView.getPackageName());
        mWholeLayout = mView.findViewById(ID);

        ID = mView.getResources().getIdentifier("crucibleViewBox" + mCrucibleIndex, SPActivity.R_ID_STR, mView.getPackageName());
        mCrucibleViewBox = (FrameLayout) mView.findViewById(ID);
        mCrucibleViewBox.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                crucibleWasClicked();
            }
        });

        mCrucibleViewBox.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mModel.crucibleInErrorState(mCrucibleIndex)) {
                    AlertDialog.Builder b = new AlertDialog.Builder(mView);
                    b.setMessage(mView.getString(R.string.reset_this_crucible_prompt));
                    b.setNegativeButton(mView.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dlgInterface, int which) {
                        }
                    });
                    b.setPositiveButton(mView.getString(R.string.reset), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dlgInterface, int which) {
                            mModel.resetCrucibleErrorState(mCrucibleIndex);
                        }
                    });
                    AlertDialog clearErrorDialog = b.create();
                    clearErrorDialog.show();

                    return true;
                }
                return false;
            }
        });


        //inner widget variables
        mDroplet = new ImageView[HomeCrucibleController.NUM_DROPS];
        ID = mView.getResources().getIdentifier("container" + mCrucibleIndex, SPActivity.R_ID_STR, mView.getPackageName());
        mContainer = (ImageView) mView.findViewById(ID);
        ID = mView.getResources().getIdentifier("thermometer" + mCrucibleIndex, SPActivity.R_ID_STR, mView.getPackageName());
        mThermometer = (ImageView) mView.findViewById(ID);
        for (int i = 0; i < 3; i++) {
            ID = mView.getResources().getIdentifier("droplet" + mCrucibleIndex + "_" + i, SPActivity.R_ID_STR, mView.getPackageName());
            mDroplet[i] = (ImageView) mView.findViewById(ID);
        }
        ID = mView.getResources().getIdentifier("piston" + mCrucibleIndex, SPActivity.R_ID_STR, mView.getPackageName());
        mPiston = (ImageView) mView.findViewById(ID);
        ID = mView.getResources().getIdentifier("bigDownArrow" + mCrucibleIndex, SPActivity.R_ID_STR, mView.getPackageName());
        mDownArrow = (ImageView) mView.findViewById(ID);
        ID = mView.getResources().getIdentifier("crucible_steam_arrows_" + mCrucibleIndex, SPActivity.R_ID_STR, mView.getPackageName());
        mSteamArrows = (ImageView) mView.findViewById(ID);
        ID = mView.getResources().getIdentifier("bigDrop" + mCrucibleIndex, SPActivity.R_ID_STR, mView.getPackageName());
        mBigDrop = (ImageView) mView.findViewById(ID);
        ID = mView.getResources().getIdentifier("crucibleBottomXButton" + mCrucibleIndex, SPActivity.R_ID_STR, mView.getPackageName());
        mXButton = (ImageView) mView.findViewById(ID);
        mXButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                cancelWasClicked();
            }
        });
        ID = mView.getResources().getIdentifier("crucibleBottomDropButton" + mCrucibleIndex, SPActivity.R_ID_STR, mView.getPackageName());
        mDropButton = (ImageView) mView.findViewById(ID);
        ID = mView.getResources().getIdentifier("mercury" + mCrucibleIndex, SPActivity.R_ID_STR, mView.getPackageName());
        mMercury = mView.findViewById(ID);
        ID = mView.getResources().getIdentifier("fillLabel" + mCrucibleIndex, SPActivity.R_ID_STR, mView.getPackageName());
        mFillLabel = (TextView) mView.findViewById(ID);
        ID = mView.getResources().getIdentifier("topNumberLabel" + mCrucibleIndex, SPActivity.R_ID_STR, mView.getPackageName());
        mTopNumberLabel = (TextView) mView.findViewById(ID);
        ID = mView.getResources().getIdentifier("bottomNumberLabel" + mCrucibleIndex, SPActivity.R_ID_STR, mView.getPackageName());
        mBottomNumberLabel = (TextView) mView.findViewById(ID);
        ID = mView.getResources().getIdentifier("dropletHider" + mCrucibleIndex, SPActivity.R_ID_STR, mView.getPackageName());
        mDropletHider = (ImageView) mView.findViewById(ID);

        crucibleElements = new ArrayList<View>();

        crucibleElements.add(mContainer);
        crucibleElements.add(mThermometer);
        for (int i = 0; i < mDroplet.length; i++) {
            crucibleElements.add(mDroplet[i]);
        }
        crucibleElements.add(mPiston);
        crucibleElements.add(mDownArrow);
        crucibleElements.add(mSteamArrows);
        crucibleElements.add(mBigDrop);
        crucibleElements.add(mXButton);
        crucibleElements.add(mDropButton);
        crucibleElements.add(mMercury);
        crucibleElements.add(mFillLabel);
        crucibleElements.add(mTopNumberLabel);
        crucibleElements.add(mBottomNumberLabel);
        crucibleElements.add(mDropletHider);
    }

    public void viewBecameInactive() {
        mModel.removeCrucibleObserver(this);
    }

    private void showErrorView() {

        if (mAnimTimer != null) {
            mAnimTimer.cancel();
        }

        hideAllElements();
        setBorderStyle(BorderStyle.RED);

        mShowingErrorView = true;

        mTopNumberLabel.setText(mView.getString(R.string.error_all_caps));
        mTopNumberLabel.setVisibility(View.VISIBLE);

        if (mModel.crucibleHasTooMuchFlow(mCrucibleIndex)) {
            mBottomNumberLabel.setText(HomeCrucibleController.FILL_OVER_FLOW);
        } else if (mModel.crucibleHasTooMuchSteam(mCrucibleIndex)) {
            mBottomNumberLabel.setText(HomeCrucibleController.STEAM_OVER_FLOW);
        } else if (mModel.crucibleNotEnoughFlow(mCrucibleIndex)) {
            mBottomNumberLabel.setText(HomeCrucibleController.FILL_UNDER_FLOW);
        } else if (mModel.crucibleNotEnoughSteam(mCrucibleIndex)) {
            mBottomNumberLabel.setText(HomeCrucibleController.STEAM_UNDER_FLOW);
        } else {
            mBottomNumberLabel.setText(mView.getString(R.string.error_all_caps));
        }
        mBottomNumberLabel.setVisibility(View.VISIBLE);

        mStatusLabel.setText(mView.getString(R.string.error_all_caps));
    }

    private void showDisabledView() {
        if (mAnimTimer != null) {
            mAnimTimer.cancel();
            mAnimTimer = null;
        }

        hideAllElements();
        setBorderStyle(BorderStyle.DIM);
        mStatusLabel.setText("");
    }

    private void showIdleView() {

        if (mAnimTimer != null) {
            mAnimTimer.cancel();
        }

        hideAllElements();
        setBorderStyle(BorderStyle.DIM);
        mFillLabel.setText(mView.getString(R.string.clean));
        mFillLabel.setVisibility(View.VISIBLE);
        mStatusLabel.setText(mView.getString(R.string.insert_cleaning_piston_prompt));
    }

    private void showFillAndHeatView() {

        if (mAnimTimer != null) {
            mAnimTimer.cancel();
        }

        hideAllElements();
        setBorderStyle(BorderStyle.GLOW);
        mThermometer.setVisibility(View.VISIBLE);
        mMercury.setVisibility(View.VISIBLE);
        mTopNumberLabel.setVisibility(View.VISIBLE);
        mBottomNumberLabel.setVisibility(View.VISIBLE);
        mXButton.setVisibility(View.VISIBLE);
        mStatusLabel.setText("");
    }

    private void showRinseFillAndHeatView() {

        if (mAnimTimer != null) {
            mAnimTimer.cancel();
        }

        hideAllElements();
        setBorderStyle(BorderStyle.GLOW);
        mThermometer.setVisibility(View.VISIBLE);
        mMercury.setVisibility(View.VISIBLE);
        mTopNumberLabel.setVisibility(View.VISIBLE);
        mBottomNumberLabel.setVisibility(View.VISIBLE);
        mXButton.setVisibility(View.VISIBLE);
        mStatusLabel.setText("");
    }


    private void showStartRinsingView() {

        if (mAnimTimer != null) {
            mAnimTimer.cancel();
        }

        hideAllElements();

        setBorderStyle(BorderStyle.PULSE);
        mBigDrop.setVisibility(View.VISIBLE);
        mTopNumberLabel.setVisibility(View.VISIBLE);
        mTopNumberLabel.setText(mView.getString(R.string.start_label_text));
        mBottomNumberLabel.setVisibility(View.VISIBLE);
        mBottomNumberLabel.setText(mView.getString(R.string.rinse_label_text));
        mStatusLabel.setText(mView.getString(R.string.drain_crucible));
        mXButton.setVisibility(View.VISIBLE);
        mBigDropAlpha = HomeCrucibleController.STEAM_ARROW_MIN_ALPHA;
        mBigDropAlphaDelta = BIG_DROP_START_ALPHA_DELTA;

        mAnimTimer = new Timer();
        mAnimTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                mView.runOnUiThread(new Runnable() {
                    public void run() {

                        mBigDropAlpha += mBigDropAlphaDelta;
                        if (mBigDropAlpha <= HomeCrucibleController.STEAM_ARROW_MIN_ALPHA) {
                            mBigDropAlpha = HomeCrucibleController.STEAM_ARROW_MIN_ALPHA;
                            mBigDropAlphaDelta = Math.abs(mBigDropAlphaDelta);
                        } else if (mBigDropAlpha >= HomeCrucibleController.STEAM_ARROW_MAX_ALHPA) {
                            mBigDropAlpha = HomeCrucibleController.STEAM_ARROW_MAX_ALHPA;
                            mBigDropAlphaDelta = -mBigDropAlphaDelta;
                        }
                        mBigDrop.setAlpha(mBigDropAlpha);
                    }
                });
            }
        }, new Date(), ONE_TENTH_SECOND_IN_MILLIS);
    }

    private void showRinsingView() {

        if (mAnimTimer != null) {
            mAnimTimer.cancel();
        }

        hideAllElements();
        setBorderStyle(BorderStyle.GLOW);
        mBigDrop.setVisibility(View.VISIBLE);
        mTopNumberLabel.setVisibility(View.VISIBLE);
        mBottomNumberLabel.setVisibility(View.VISIBLE);
        mStatusLabel.setText("");
        mXButton.setVisibility(View.VISIBLE);
        mBigDropAlpha = HomeCrucibleController.STEAM_ARROW_MIN_ALPHA;
        mBigDropAlphaDelta = BIG_DROP_START_ALPHA_DELTA;

        mAnimTimer = new Timer();
        mAnimTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                mView.runOnUiThread(new Runnable() {
                    public void run() {

                        mBigDropAlpha += mBigDropAlphaDelta;
                        if (mBigDropAlpha <= HomeCrucibleController.STEAM_ARROW_MIN_ALPHA) {
                            mBigDropAlpha = HomeCrucibleController.STEAM_ARROW_MIN_ALPHA;
                            mBigDropAlphaDelta = Math.abs(mBigDropAlphaDelta);
                        } else if (mBigDropAlpha >= HomeCrucibleController.STEAM_ARROW_MAX_ALHPA) {
                            mBigDropAlpha = HomeCrucibleController.STEAM_ARROW_MAX_ALHPA;
                            mBigDropAlphaDelta = -mBigDropAlphaDelta;
                        }
                        mBigDrop.setAlpha(mBigDropAlpha);
                    }
                });
            }
        }, new Date(), ONE_TENTH_SECOND_IN_MILLIS);
    }

    private void hideAllElements() {
        mShowingErrorView = false;

        for (int i = 0; i < crucibleElements.size(); i++) {
            if (crucibleElements.get(i) == mDropButton) {
                crucibleElements.get(i).setVisibility(View.INVISIBLE);
            } else {
                crucibleElements.get(i).setVisibility(View.GONE);
            }
        }
    }

    private void setBorderStyle(BorderStyle style) {

        if (mFlashTimer != null) {
            mFlashTimer.cancel();
        }

        if (style == BorderStyle.DIM) {
            mCrucibleViewBox.setBackgroundResource(R.drawable.crucible_9_patch);
            mCrucibleViewBox.getBackground().setAlpha(FULLY_OPAQUE_ALPHA);
        } else if (style == BorderStyle.GLOW) {
            mCrucibleViewBox.setBackgroundResource(R.drawable.crucible_9_patch_glow);
            mCrucibleViewBox.getBackground().setAlpha(FULLY_OPAQUE_ALPHA);
        } else if (style == BorderStyle.FLASH) {
            mCrucibleViewBox.setBackgroundResource(R.drawable.crucible_9_patch);
            mCrucibleViewBox.getBackground().setAlpha(FULLY_OPAQUE_ALPHA);
            mFlashTimer = new Timer();
            mFlashTimer.scheduleAtFixedRate(new TimerTask() {
                private boolean mOn;

                {
                    mOn = false;
                }

                public void run() {
                    mView.runOnUiThread(new Runnable() {
                        public void run() {

                            if (mOn) {
                                mCrucibleViewBox.setBackgroundResource(R.drawable.crucible_9_patch_glow);
                            } else {
                                mCrucibleViewBox.setBackgroundResource(R.drawable.crucible_9_patch);
                            }
                            mOn = !mOn;
                        }

                    });
                }
            }, new Date(), ONE_HALF_SECOND_IN_MILLIS);
        } else if (style == BorderStyle.RED) {
            mCrucibleViewBox.setBackgroundResource(R.drawable.crucible_9_patch_red);
        } else if (style == BorderStyle.PULSE) {
            mCrucibleViewBox.setBackgroundResource(R.drawable.crucible_9_patch_glow);
            mFlashTimer = new Timer();
            mFlashTimer.scheduleAtFixedRate(new TimerTask() {
                private int mAlpha = FULL_TRANSPARENT_ALPHA;
                private boolean mPositive = true;

                {
                    mAlpha = FULLY_OPAQUE_ALPHA;
                    mPositive = true;
                }

                public void run() {
                    mView.runOnUiThread(new Runnable() {

                        public void run() {

                            if (mPositive) {
                                mAlpha += PULSE_ALPHA_STEP;
                            } else {
                                mAlpha -= PULSE_ALPHA_STEP;
                            }
                            if (mAlpha > FULLY_OPAQUE_ALPHA) {
                                mAlpha = FULLY_OPAQUE_ALPHA;
                                mPositive = false;
                            } else if (mAlpha < FULL_TRANSPARENT_ALPHA) {
                                mAlpha = FULL_TRANSPARENT_ALPHA;
                                mPositive = true;
                            }
                            mCrucibleViewBox.getBackground().setAlpha(mAlpha);
                        }

                    });
                }
            }, new Date(), ONE_TENTH_SECOND_IN_MILLIS);
        }
    }

    @Override
    public void notifyOfCrucibleChange(final int index) {

        mView.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                update();
            }
        });
    }

    public void crucibleWasClicked() {
        if (mModel.isCrucibleLocked(mCrucibleIndex)) {
            return;
        }

        SPCrucibleState state = mModel.getStateForCrucible(mCrucibleIndex);
        if (state == SPCrucibleState.IDLE) {
            mModel.startCleaningCrucible(mCrucibleIndex);
            showFillAndHeatView();
        } else if (state == SPCrucibleState.WAITING_FOR_CLEANING_RINSE) {
            mModel.finishCleaningCrucible(mCrucibleIndex);
            showRinseFillAndHeatView();
        }
    }

    public void cancelWasClicked() {
        if (mModel.getStateForCrucible(mCrucibleIndex) != SPCrucibleState.IDLE) {
            mModel.stopBrewingOnCrucible(mCrucibleIndex);
            showIdleView();
        }
    }
}
