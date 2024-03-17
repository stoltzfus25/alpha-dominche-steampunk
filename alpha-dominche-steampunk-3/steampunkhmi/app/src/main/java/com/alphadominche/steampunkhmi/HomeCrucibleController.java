package com.alphadominche.steampunkhmi;

import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class HomeCrucibleController implements SPCrucibleObserver {
    public static final int NUM_DROPS = 3;

    //--------- all dimensions given in "dp" ---------
    public static final int PISTON_INSERT_TOP = 30;
    public static final int PISTON_BREW_TOP = 110;
    public static final int[] DROPLET_EXTRACTION_TOP = {325, 350, 325};
    public static final int[] DROPLET_EXTRACTION_LEFT = {8, 33, 58};
    public static final int DROPLET_EXTRACTION_VERTICAL_SPACING = 70;
    public static final int DROPLET_EXRACTION_VERTICAL_OFFSET = 140;
    public static final int[] DROPLET_FILL_TOP = {150, 240, 330};
    public static final int DROPLET_FILL_LEFT = 35;
    public static final int DROPLET_TOP_FILL_STEP = 8;
    public static final int DROPLET_TOP_FILL_LIMIT = 375;
    public static final int DROPLET_TOP_FILL_RESET = 105;
    public static final int DROPLET_TOP_FILL_FADE_START_POS = 305;
    public static final int DROPLET_TOP_FILL_FADE_END = 120;
    public static final float DROPLET_TOP_FILL_ALPHA_DEMONINATOR = 15.0f;
    public static final float ARROW_TOP_STEP = 6.0f;
    public static final float ARROW_TOP_FADE_FIRST_LIMIT = 340.0f;
    public static final float ARROW_TOP_FADE_SECOND_LIMIT = 370.0f;
    public static final float ARROW_ALPHA_DENOMINATOR = 30.0f;
    public static final int DROPLET_TOP_FIRST_EXTRACT_LIMIT = 375;
    public static final int DROPLET_TOP_SECOND_EXTRACT_LIMIT = 120;
    public static final int DROPLET_TOP_THIRD_EXTRACT_LIMIT = 185;
    public static final int DROPLET_TOP_EXTRACT_RESET = 0;
    public static final float DROPLET_TOP_EXTRACT_ALPHA_SCALE_FACTOR = 15.0f;
    public static final int DROPLET_TOP_EXTRACT_STEP = 8;
    public static final int MERCURY_TOP = 145; //position for maximum temperature
    public static final int MERCURY_BOTTOM = 360; //position for minimum temperature
    public static final int MERCURY_ZERO_HEIGHT = 50; //0 degrees fahrenheight
    public static final int MERCURY_LEFT = 35;
    public static final int MAX_TEMP = 374;
    public static final int MIN_TEMP = 255;
    public static final int DOWN_ARROW_TOP = 310;
    public static final float STEAM_ARROW_MIN_ALPHA = 0.5f;
    public static final float STEAM_ARROW_MAX_ALHPA = 1.0f;
    public static final double TWO_SECONDS = 2.0;
    public static final String DEGREE_SYMBOL = "º";
    public static final String RECIPE_CHOOSER = "Recipe Chooser";
    public static final String FILL_OVER_FLOW = "FILL OVER FLOW";
    public static final String STEAM_OVER_FLOW = "STEAM OVER FLOW";
    public static final String FILL_UNDER_FLOW = "FILL UNDER FLOW";
    public static final String STEAM_UNDER_FLOW = "STEAM UNDER FLOW";

    public static final float BIG_DROP_START_ALPHA_DELTA = 0.05f;
    public static final int ONE_TENTH_SECOND_IN_MILLIS = 100;
    public static final int FULLY_OPAQUE_ALPHA = 255;
    public static final int ONE_HALF_SECOND_IN_MILLIS = 500;
    public static final int PULSE_ALPHA_STEP = 26;
    public static final int FULL_TRANSPARENT_ALPHA = 0;
    public static final float FULLY_OPAQUE_ALPHA_FLOAT = 1.0f;
    public static final float FULLY_TRANSPARENT_ALPHA_FLOAT = 0.0f;
    public static final float HALF_OPAQUE_ALPHA_FLOAT = 0.5f;
    public static final float ONE_TENTN_ALPHA_FLOAT = 0.1f;


    private View mWholeLayout;

    private View mFavoritesLink;
    private TextView mRoasterLabel;
    private TextView mCoffeeLabel;
    private TextView mVolumeLabel;
    private ImageView mInfoBtn;
    private int mCrucibleIndex;
    private SPCrucibleState mPreviousState;
    private SPModel mModel;
    private SPCruciblesActivity mView;

    private FrameLayout mCrucibleViewBox;
    private ArrayList<View> crucibleElements;

    private ImageView mContainer;
    private ImageView mThermometer;
    private ImageView[] mDroplet;
    private double[] mDropletTop;
    private ImageView mPiston;
    private ImageView mDownArrow;
    private double mDownArrowTop;
    private ImageView mSteamArrows;
    private float mSteamArrowAlpha;
    private float mSteamArrowAlphaDelta;
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

    private boolean mViewIsActive;
    private boolean mTouchEventBeingUsedForSteam;

    private Timer mFlashTimer;
    private Timer mAnimTimer;
    private float mDensity;

    private boolean mWasLocked;
    private boolean mShowingErrorView;

    private long mSteepStart;

    public enum BorderStyle {
        DIM,
        GLOW,
        FLASH,
        RED,
        PULSE
    }

    HomeCrucibleController(SPCruciblesActivity view, int index) {
        //general instance variables
        mView = view;
        mWasLocked = false;
        mShowingErrorView = false;

        mModel = SPModel.getInstance(mView);
        mCrucibleIndex = index;
        mPreviousState = null;

        mTouchEventBeingUsedForSteam = false;

        viewBecameActive(); // TODO we need to fix it so this doesn't need to be called here

        mFlashTimer = null;

        mDensity = mView.getResources().getDisplayMetrics().density;
        mSteamArrowAlpha = STEAM_ARROW_MIN_ALPHA;
        mSteamArrowAlphaDelta = ONE_TENTN_ALPHA_FLOAT;
        mBigDropAlpha = STEAM_ARROW_MIN_ALPHA;
        mBigDropAlphaDelta = BIG_DROP_START_ALPHA_DELTA;

        mSteepStart = 0;

        update();
    }

    public void resume() {
        viewBecameActive();
        update();
    }

    public void pause() {
        viewBecameInactive();
    }

    public void viewBecameActive() {
        // set up widget links here

        mModel.addCrucibleObserver(this, mCrucibleIndex);

        //outer widget variables
        int ID = mView.getResources().getIdentifier("crucibleLayout" + mCrucibleIndex, SPActivity.R_ID_STR, mView.getPackageName());
        mWholeLayout = mView.findViewById(ID);

        ID = mView.getResources().getIdentifier("favorites_link_" + mCrucibleIndex, SPActivity.R_ID_STR, mView.getPackageName());
        mFavoritesLink = mView.findViewById(ID);

        ID = mView.getResources().getIdentifier("roasterLabel" + mCrucibleIndex, SPActivity.R_ID_STR, mView.getPackageName());
        mRoasterLabel = (TextView) mView.findViewById(ID);
        ID = mView.getResources().getIdentifier("coffeeLabel" + mCrucibleIndex, SPActivity.R_ID_STR, mView.getPackageName());
        mCoffeeLabel = (TextView) mView.findViewById(ID);
        ID = mView.getResources().getIdentifier("volumeLabel" + mCrucibleIndex, SPActivity.R_ID_STR, mView.getPackageName());
        mVolumeLabel = (TextView) mView.findViewById(ID);

        OnClickListener favoritesLinkListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                SPRecipe recipe = mModel.getRecipeForCrucible(mCrucibleIndex);
                long currID = (recipe == null ? 0L : recipe.getId());
                SPFavoritesChooserModal modal = new SPFavoritesChooserModal();
                modal.setCrucibleIndex(mCrucibleIndex);
                modal.setSelectedRecipeId(currID);
                modal.show(mView.getFragmentManager(), RECIPE_CHOOSER);
            }
        };
        mFavoritesLink.setOnClickListener(favoritesLinkListener);

        ID = mView.getResources().getIdentifier("infoButton" + mCrucibleIndex, SPActivity.R_ID_STR, mView.getPackageName());
        mInfoBtn = (ImageView) mView.findViewById(ID);
        mInfoBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View infoButton) {
                SPRecipe recipe = mModel.getRecipeForCrucible(mCrucibleIndex);
                if (recipe == null) {
                    Toast.makeText(mView, mView.getString(R.string.select_recipe_first_message), Toast.LENGTH_SHORT).show();
                    return;
                }
                mModel.setCurrentlyEditedRecipe(recipe);

                // start the recipe editor
                Intent intent = new Intent();
                intent.setClass(mView, SPRecipeEditorActivity.class);
                mView.startActivity(intent);
            }
        });
        ID = mView.getResources().getIdentifier("crucibleViewBox" + mCrucibleIndex, SPActivity.R_ID_STR, mView.getPackageName());
        mCrucibleViewBox = (FrameLayout) mView.findViewById(ID);
        mCrucibleViewBox.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (shouldNotRespondToClick()) return;
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

        mCrucibleViewBox.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                SPCrucibleState state = mModel.getStateForCrucible(mCrucibleIndex);
                if (state != SPCrucibleState.AGITATING && state != SPCrucibleState.STEEPING) return false;
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    mTouchEventBeingUsedForSteam = true;
                    forceSteam();
                    showAgitationView();
                    return true;
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    mTouchEventBeingUsedForSteam = false;
                    stopForcingSteam();
                    if (state == SPCrucibleState.STEEPING) {
                        showSteepView();
                    }
                    return true;
                }
                return false;
            }
        });

        //inner widget variables
        mDroplet = new ImageView[NUM_DROPS];
        mDropletTop = new double[NUM_DROPS];
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
        mDropButton.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    forceFill();
                    return true;
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    stopForcingFill();
                    return true;
                }
                return false;
            }
        });
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

        //say it's ok to update the view
        mViewIsActive = true;
    }

    public void viewBecameInactive() {
        mViewIsActive = false;
        mModel.removeCrucibleObserver(this);
    }

    private boolean shouldNotRespondToClick() {
        double targetTemp = mModel.getBoilerTargetTemp();
        double convertedTargetTemp = SPServiceThermistor.convertFromTempToTemp(mModel.getTempUnits(), SPTempUnitType.KELVIN, targetTemp);
        double threshold = (convertedTargetTemp - SPServiceBoiler.TEMP_UNDERFLOW_TOLERANCE);
        boolean boilerTooCold = (mModel.getBoilerCurrentTemp() < threshold);
        if (boilerTooCold && mModel.getStateForCrucible(mCrucibleIndex) == SPCrucibleState.IDLE) {
            SPCrucibleTempTooLowFragment frag = new SPCrucibleTempTooLowFragment();
            frag.show(mView.getFragmentManager(), "CrucibleModal");
        }
        return mTouchEventBeingUsedForSteam || boilerTooCold;
    }

    private boolean isInABrewState(SPCrucibleState state) {
        if (state == SPCrucibleState.FILLING ||
                state == SPCrucibleState.HEATING ||
                state == SPCrucibleState.INSERT_PISTON ||
                state == SPCrucibleState.START_BREWING ||
                state == SPCrucibleState.AGITATING ||
                state == SPCrucibleState.STEEPING ||
                state == SPCrucibleState.EXTRACTING) {
            return true;
        }
        return false;
    }

    public void update() {
        if (!mViewIsActive) {
            return;
        }

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

        SPUser user = mModel.getRecipeRoasterForCrucible(mCrucibleIndex);
        String label = user != null ? user.getName() : "";
        mRoasterLabel.setText(label);
        mCoffeeLabel.setText(mModel.getRecipeCoffeeForCrucible(mCrucibleIndex));
        if (mModel.getVolumeUnits() == SPVolumeUnitType.OUNCES) {
            mVolumeLabel.setText(Double.toString(Math.round(SPFlowMeter.convertFromMillilitersToOunces(mModel
                    .getRecipeVolumeForCrucible(mCrucibleIndex, mModel.getCurrentStackForCrucible(mCrucibleIndex))))));

        } else {
            mVolumeLabel.setText(Double.toString(Math.round(mModel
                    .getRecipeVolumeForCrucible(mCrucibleIndex, mModel.getCurrentStackForCrucible(mCrucibleIndex)))));
        }

        SPCrucibleState currState = mModel.getStateForCrucible(mCrucibleIndex);

        if (mPreviousState != currState || mShowingErrorView) {
            if (mModel.crucibleInErrorState(mCrucibleIndex)) {
            } else if (currState == SPCrucibleState.IDLE) {
                mTouchEventBeingUsedForSteam = false;
                showIdleView();
            } else if (currState == SPCrucibleState.FILLING) {
                stopForcingFill();
                showFillView();
            } else if (currState == SPCrucibleState.HEATING) {
                showHeatView();
            } else if (currState == SPCrucibleState.INSERT_PISTON) {
                showInsertPistonView();
            } else if (currState == SPCrucibleState.START_BREWING) {
                mTouchEventBeingUsedForSteam = false;
                showStartBrewView();
            } else if (currState == SPCrucibleState.AGITATING) {
                showAgitationView();
            } else if (currState == SPCrucibleState.STEEPING) {
                showSteepView();
            } else if (currState == SPCrucibleState.EXTRACTING) {
                showExtractionView();
            } else if (currState == SPCrucibleState.START_RINSING) {
                mTouchEventBeingUsedForSteam = false;
                stopForcingSteam();
                showStartRinsingView();
            } else if (currState == SPCrucibleState.RINSING) {
                showRinsingView();
            } else if (currState == SPCrucibleState.WAITING_FOR_NEXT_STACK) {
                mTouchEventBeingUsedForSteam = false;
                showWaitingForNextStackView();
            }
        }

        if (currState == SPCrucibleState.IDLE) {
            mCrucibleViewBox.getBackground().setAlpha(FULLY_OPAQUE_ALPHA); //shouldn't really need this...trying to fix the extra-dim outline for the idle view
        } else if (currState == SPCrucibleState.FILLING) {
        } else if (currState == SPCrucibleState.HEATING) {
            double targetTemp = mModel.getTargetTempForCrucible(mCrucibleIndex);
            double convertedTargetTemp = SPServiceThermistor.convertFromTempToTemp(SPTempUnitType.KELVIN, mModel.getTempUnits(), targetTemp);
            double temp = mModel.getTempForCrucible(mCrucibleIndex);
            if (temp > targetTemp && isInABrewState(mModel.getStateForCrucible(mCrucibleIndex))) {
                temp = targetTemp;
            }
            double convertedCrucibleTemp = SPServiceThermistor.convertFromTempToTemp(SPTempUnitType.KELVIN, mModel.getTempUnits(), temp);
            mTopNumberLabel.setText("" + Math.round(convertedTargetTemp) + DEGREE_SYMBOL);
            mBottomNumberLabel.setText("" + Math.round(convertedCrucibleTemp) + DEGREE_SYMBOL);

            int newTop = (int) ((MERCURY_BOTTOM - MERCURY_TOP) * (mModel.getTempForCrucible(mCrucibleIndex) - MIN_TEMP));
            newTop /= (MAX_TEMP - MIN_TEMP);
            if (newTop > (MERCURY_BOTTOM - MERCURY_TOP)) newTop = (MERCURY_BOTTOM - MERCURY_TOP);
            newTop = MERCURY_BOTTOM - newTop;
            newTop = (int) (newTop * mView.getResources().getDisplayMetrics().density);
            FrameLayout.LayoutParams newParams = new FrameLayout.LayoutParams(mMercury.getLayoutParams());
            int newLeft = (int) (MERCURY_LEFT * mView.getResources().getDisplayMetrics().density);
            newParams.setMargins(newLeft, newTop, newParams.rightMargin, newParams.bottomMargin);

            //scale mercury height proportional to temperature
            int newHeight = (int) ((MERCURY_BOTTOM - MERCURY_TOP) * (mModel.getTempForCrucible(mCrucibleIndex) - MIN_TEMP));
            newHeight /= (MAX_TEMP - MIN_TEMP);
            if (newHeight > (MERCURY_BOTTOM - MERCURY_TOP)) newHeight = (MERCURY_BOTTOM - MERCURY_TOP);
            newHeight += MERCURY_ZERO_HEIGHT;
            newParams.height = (int) (newHeight * mView.getResources().getDisplayMetrics().density);
            mMercury.setLayoutParams(newParams);
        } else if (currState == SPCrucibleState.INSERT_PISTON) {
        } else if (currState == SPCrucibleState.START_BREWING) {
        } else if (currState == SPCrucibleState.AGITATING) {
            mTopNumberLabel.setText("" + mModel.getCrucibleTimeLeftInBrew(mCrucibleIndex));
            if (mModel.getCrucibleTimeLeftInBrew(mCrucibleIndex) >= mModel.getRecipeTimeForCrucible(mCrucibleIndex, mModel.getCurrentStackForCrucible(mCrucibleIndex)) - TWO_SECONDS) {
                double temp = mModel.getTempForCrucible(mCrucibleIndex);
                double convertedTemp = SPServiceThermistor.convertFromTempToTemp(SPTempUnitType.KELVIN, mModel.getTempUnits(), temp);
                mBottomNumberLabel.setText("" + Math.round(convertedTemp) + DEGREE_SYMBOL);
            }
        } else if (currState == SPCrucibleState.STEEPING) {
            mTopNumberLabel.setText("" + mModel.getCrucibleTimeLeftInBrew(mCrucibleIndex));
            if ((System.nanoTime() - mSteepStart) / SPServiceCrucible.A_BILLION >= TWO_SECONDS) {
                double temp = mModel.getTempForCrucible(mCrucibleIndex);
                double convertedTemp = SPServiceThermistor.convertFromTempToTemp(SPTempUnitType.KELVIN, mModel.getTempUnits(), temp);
                mBottomNumberLabel.setText("" + Math.round(convertedTemp) + DEGREE_SYMBOL);
            }
        } else if (currState == SPCrucibleState.EXTRACTING) {
            mTopNumberLabel.setText("" + mModel.getExtractionTimeLeftForCrucible(mCrucibleIndex));
        } else if (currState == SPCrucibleState.START_RINSING) {
        } else if (currState == SPCrucibleState.RINSING) {
            double temp = mModel.getTempForCrucible(mCrucibleIndex);
            double convertedTemp = SPServiceThermistor.convertFromTempToTemp(SPTempUnitType.KELVIN, mModel.getTempUnits(), temp);
            mTopNumberLabel.setText("" + Math.round(mModel.getRinseTemp()) + DEGREE_SYMBOL);
            mBottomNumberLabel.setText("" + Math.round(convertedTemp) + DEGREE_SYMBOL);
        } else if (currState == SPCrucibleState.WAITING_FOR_NEXT_STACK) {
        }

        mPreviousState = currState;

        if (mModel.crucibleHasSteamedTooMuchOnFillAndHeating(mCrucibleIndex)) {
            mModel.clearCrucibleSteamedTooMuchOnFillAndHeating(mCrucibleIndex);
            AlertDialog.Builder b = new AlertDialog.Builder(mView);
            b.setMessage(mView.getString(R.string.too_much_water_message));
            b.setNegativeButton(mView.getString(R.string.no), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dlgInterface, int which) {
                    cancelWasClicked();
                }
            });
            b.setPositiveButton(mView.getString(R.string.yes), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dlgInterface, int which) {
                }
            });
            AlertDialog tooMuchSteamDialog = b.create();
            tooMuchSteamDialog.show();
        }
    }

    private void crucibleWasClicked() {
        if (mModel.isCrucibleLocked(mCrucibleIndex)) {
            return;
        }

        SPCrucibleState cState = mModel.getStateForCrucible(mCrucibleIndex);
        if (mModel.crucibleInErrorState(mCrucibleIndex)) {
            //should start test cycle...
        } else if (cState == SPCrucibleState.IDLE || cState == SPCrucibleState.WAITING_FOR_NEXT_STACK) {
            if (mModel.getRecipeForCrucible(mCrucibleIndex) == null) {
                return; //no recipe...so don't try to start making a beverage!
            }
            mModel.fillAndHeatBeverageWaterOnCrucible(mCrucibleIndex);
            showFillView();
        } else if (cState == SPCrucibleState.START_BREWING) {
            mModel.brewBeverageOnCrucible(mCrucibleIndex);
            showAgitationView();
        } else if (cState == SPCrucibleState.START_RINSING) {
            mModel.rinseCrucible(mCrucibleIndex);
            showRinsingView();
        }
    }

    private void cancelWasClicked() {
        mModel.stopBrewingOnCrucible(mCrucibleIndex);
    }

    private void forceFill() {
        Intent forceFillIntent = new Intent(SPIOIOService.CRUCIBLE_COMMAND_INTENT);
        forceFillIntent.putExtra(SPIOIOService.CRUCIBLE, mCrucibleIndex);
        forceFillIntent.putExtra(SPIOIOService.COMMAND, SPIOIOService.START_FORCE_FILL);
        mView.startService(forceFillIntent);
    }

    private void stopForcingFill() {
        Intent stopForceFillIntent = new Intent(SPIOIOService.CRUCIBLE_COMMAND_INTENT);
        stopForceFillIntent.putExtra(SPIOIOService.CRUCIBLE, mCrucibleIndex);
        stopForceFillIntent.putExtra(SPIOIOService.COMMAND, SPIOIOService.STOP_FORCE_FILL);
        mView.startService(stopForceFillIntent);
    }

    private void forceSteam() {
        Intent forceSteamIntent = new Intent(SPIOIOService.CRUCIBLE_COMMAND_INTENT);
        forceSteamIntent.putExtra(SPIOIOService.CRUCIBLE, mCrucibleIndex);
        forceSteamIntent.putExtra(SPIOIOService.COMMAND, SPIOIOService.START_FORCE_STEAM);
        mView.startService(forceSteamIntent);
    }

    private void stopForcingSteam() {
        Intent stopForceSteamIntent = new Intent(SPIOIOService.CRUCIBLE_COMMAND_INTENT);
        stopForceSteamIntent.putExtra(SPIOIOService.CRUCIBLE, mCrucibleIndex);
        stopForceSteamIntent.putExtra(SPIOIOService.COMMAND, SPIOIOService.STOP_FORCE_STEAM);
        mView.startService(stopForceSteamIntent);
    }

    private void showErrorView() {
        if (!mViewIsActive || !mView.acceptsUpdates()) return;

        if (mAnimTimer != null) {
            mAnimTimer.cancel();
        }

        hideAllElements();
        setBorderStyle(BorderStyle.RED);

        mTopNumberLabel.setText(mView.getString(R.string.error_all_caps));
        mTopNumberLabel.setVisibility(View.VISIBLE);

        if (mModel.crucibleHasTooMuchFlow(mCrucibleIndex)) {
            mBottomNumberLabel.setText(FILL_OVER_FLOW);
        } else if (mModel.crucibleHasTooMuchSteam(mCrucibleIndex)) {
            mBottomNumberLabel.setText(STEAM_OVER_FLOW);
        } else if (mModel.crucibleNotEnoughFlow(mCrucibleIndex)) {
            mBottomNumberLabel.setText(FILL_UNDER_FLOW);
        } else if (mModel.crucibleNotEnoughSteam(mCrucibleIndex)) {
            mBottomNumberLabel.setText(STEAM_UNDER_FLOW);
        } else {
            mBottomNumberLabel.setText(mView.getString(R.string.error_all_caps));
        }
        mBottomNumberLabel.setVisibility(View.VISIBLE);
    }

    private void showDisabledView() {
        if (mAnimTimer != null) {
            mAnimTimer.cancel();
            mAnimTimer = null;
        }

        hideAllElements();
        setBorderStyle(BorderStyle.DIM);
    }

    private void showIdleView() {

        if (mAnimTimer != null) {
            mAnimTimer.cancel();
        }

        hideAllElements();
        setBorderStyle(BorderStyle.DIM);
        mFillLabel.setText(mView.getString(R.string.fill_all_caps));
        mFillLabel.setVisibility(View.VISIBLE);
        mDropButton.setVisibility(View.VISIBLE);
    }

    private void showWaitingForNextStackView() {

        if (mAnimTimer != null) {
            mAnimTimer.cancel();
        }

        hideAllElements();
        setBorderStyle(BorderStyle.PULSE);
        mFillLabel.setText(mView.getString(R.string.next_all_caps));
        mFillLabel.setVisibility(View.VISIBLE);
        mDropButton.setVisibility(View.VISIBLE);
    }

    private void showFillView() {

        if (mAnimTimer != null) {
            mAnimTimer.cancel();
        }

        hideAllElements();
        setBorderStyle(BorderStyle.GLOW);
        for (int i = 0; i < mDroplet.length; i++) {
            mDroplet[i].setVisibility(View.VISIBLE);
            FrameLayout.LayoutParams dropParams = new FrameLayout.LayoutParams(mDroplet[i].getLayoutParams());
            int newLeft = (int) (DROPLET_FILL_LEFT * mDensity);
            int newTop = (int) (DROPLET_FILL_TOP[i] * mDensity);
            dropParams.setMargins(newLeft, newTop, dropParams.rightMargin, dropParams.bottomMargin);
            mDroplet[i].setLayoutParams(dropParams);
            mDropletTop[i] = DROPLET_FILL_TOP[i];
        }
        mContainer.setVisibility(View.VISIBLE);
        mXButton.setVisibility(View.VISIBLE);

        mAnimTimer = new Timer();
        mAnimTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                mView.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        for (int i = 0; i < mDroplet.length; i++) {
                            mDropletTop[i] += DROPLET_TOP_FILL_STEP;

                            if (mDropletTop[i] > DROPLET_TOP_FILL_LIMIT) mDropletTop[i] = DROPLET_TOP_FILL_RESET;
                            FrameLayout.LayoutParams dropParams = new FrameLayout.LayoutParams(mDroplet[i].getLayoutParams());
                            int newLeft = (int) (DROPLET_FILL_LEFT * mDensity);
                            int newTop = (int) (mDropletTop[i] * mDensity);
                            dropParams.setMargins(newLeft, newTop, dropParams.rightMargin, dropParams.bottomMargin);
                            mDroplet[i].setLayoutParams(dropParams);
                            if (mDropletTop[i] > DROPLET_TOP_FILL_FADE_START_POS) {
                                mDroplet[i].setAlpha(FULLY_OPAQUE_ALPHA_FLOAT - Math.max(FULLY_TRANSPARENT_ALPHA_FLOAT, ((float) mDropletTop[i] - (float) DROPLET_TOP_FILL_FADE_START_POS) / DROPLET_TOP_FILL_ALPHA_DEMONINATOR));
                            } else if (mDropletTop[i] < DROPLET_TOP_FILL_FADE_END) {
                                mDroplet[i].setAlpha(FULLY_OPAQUE_ALPHA_FLOAT - Math.max(FULLY_TRANSPARENT_ALPHA_FLOAT, (DROPLET_TOP_FILL_FADE_END - (float) mDropletTop[i]) / DROPLET_TOP_FILL_ALPHA_DEMONINATOR));
                            } else {
                                mDroplet[i].setAlpha(FULLY_OPAQUE_ALPHA_FLOAT);
                            }
                        }
                    }
                });
            }
        }, new Date(), ONE_TENTH_SECOND_IN_MILLIS);
    }

    private void showHeatView() {

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
    }

    private void showInsertPistonView() {

        if (mAnimTimer != null) {
            mAnimTimer.cancel();
        }

        hideAllElements();
        setBorderStyle(BorderStyle.GLOW);
        mPiston.setVisibility(View.VISIBLE);
        FrameLayout.LayoutParams newParams = new FrameLayout.LayoutParams(mPiston.getLayoutParams());
        int newTop = (int) (PISTON_INSERT_TOP * mView.getResources().getDisplayMetrics().density);
        newParams.setMargins(newParams.leftMargin, newTop, newParams.rightMargin, newParams.bottomMargin);
        mPiston.setLayoutParams(newParams);
        mDownArrow.setVisibility(View.VISIBLE);
        mXButton.setVisibility(View.VISIBLE);
        mDownArrowTop = DOWN_ARROW_TOP;

        //down arrow animation here!
        mAnimTimer = new Timer();
        mAnimTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                mView.runOnUiThread(new Runnable() {
                    public void run() {

                        mDownArrowTop += ARROW_TOP_STEP;
                        if (mDownArrowTop > ARROW_TOP_FADE_FIRST_LIMIT) {
                            mDownArrow.setAlpha(FULLY_OPAQUE_ALPHA_FLOAT - (((float) mDownArrowTop - ARROW_TOP_FADE_FIRST_LIMIT) / ARROW_ALPHA_DENOMINATOR));
                        } else {
                            mDownArrow.setAlpha(FULLY_OPAQUE_ALPHA_FLOAT);
                        }
                        if (mDownArrowTop > ARROW_TOP_FADE_SECOND_LIMIT) {
                            mDownArrowTop = DOWN_ARROW_TOP;
                        }
                        FrameLayout.LayoutParams arrowParams = new FrameLayout.LayoutParams(mDownArrow.getLayoutParams());
                        int newTopt = (int) (mDownArrowTop * mDensity);
                        arrowParams.setMargins(arrowParams.leftMargin, newTopt, arrowParams.rightMargin, arrowParams.bottomMargin);
                        arrowParams.gravity = Gravity.CENTER_HORIZONTAL;
                        mDownArrow.setLayoutParams(arrowParams);
                    }
                });
            }
        }, new Date(), ONE_TENTH_SECOND_IN_MILLIS);
    }

    private void showStartBrewView() {

        if (mAnimTimer != null) {
            mAnimTimer.cancel();
        }

        hideAllElements();
        setBorderStyle(BorderStyle.PULSE);
        mPiston.setVisibility(View.VISIBLE);
        FrameLayout.LayoutParams newParams = new FrameLayout.LayoutParams(mPiston.getLayoutParams());
        int newTop = (int) (PISTON_BREW_TOP * mView.getResources().getDisplayMetrics().density);
        newParams.setMargins(newParams.leftMargin, newTop, newParams.rightMargin, newParams.bottomMargin);
        mPiston.setLayoutParams(newParams);
        mTopNumberLabel.setVisibility(View.VISIBLE);
        mTopNumberLabel.setText(mView.getString(R.string.start_label_text));
        mBottomNumberLabel.setVisibility(View.VISIBLE);
        mBottomNumberLabel.setText(mView.getString(R.string.brew_label_text));
        mXButton.setVisibility(View.VISIBLE);
    }

    private void showAgitationView() {

        if (mAnimTimer != null) {
            mAnimTimer.cancel();
        }

        hideAllElements();
        setBorderStyle(BorderStyle.GLOW);
        mPiston.setVisibility(View.VISIBLE);
        FrameLayout.LayoutParams newParams = new FrameLayout.LayoutParams(mPiston.getLayoutParams());
        int newTop = (int) (PISTON_BREW_TOP * mView.getResources().getDisplayMetrics().density);
        newParams.setMargins(newParams.leftMargin, newTop, newParams.rightMargin, newParams.bottomMargin);
        mPiston.setLayoutParams(newParams);
        mTopNumberLabel.setVisibility(View.VISIBLE);
        mBottomNumberLabel.setVisibility(View.VISIBLE);
        mXButton.setVisibility(View.VISIBLE);
        mSteamArrows.setVisibility(View.VISIBLE);
        mSteamArrowAlpha = STEAM_ARROW_MIN_ALPHA;
        mSteamArrowAlphaDelta = ONE_TENTN_ALPHA_FLOAT;

        //need to put steaming animation in here!
        mAnimTimer = new Timer();
        mAnimTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                mView.runOnUiThread(new Runnable() {
                    public void run() {
                        mSteamArrowAlpha += mSteamArrowAlphaDelta;
                        if (mSteamArrowAlpha <= STEAM_ARROW_MIN_ALPHA) {
                            mSteamArrowAlpha = STEAM_ARROW_MIN_ALPHA;
                            mSteamArrowAlphaDelta = -mSteamArrowAlphaDelta;
                        } else if (mSteamArrowAlpha >= STEAM_ARROW_MAX_ALHPA) {
                            mSteamArrowAlpha = STEAM_ARROW_MAX_ALHPA;
                            mSteamArrowAlphaDelta = -mSteamArrowAlphaDelta;
                        }
                        mSteamArrows.setAlpha(mSteamArrowAlpha);
                    }
                });
            }
        }, new Date(), ONE_TENTH_SECOND_IN_MILLIS);
        mBottomNumberLabel.setText(""
                + Math.round(SPServiceThermistor.convertFromTempToTemp(
                SPTempUnitType.KELVIN, mModel.getTempUnits(),
                mModel.getTempForCrucible(mCrucibleIndex))) + DEGREE_SYMBOL);
    }

    private void showSteepView() {

        if (mAnimTimer != null) {
            mAnimTimer.cancel();
        }

        hideAllElements();
        mSteepStart = System.nanoTime();
        setBorderStyle(BorderStyle.GLOW);
        mPiston.setVisibility(View.VISIBLE);
        FrameLayout.LayoutParams newParams = new FrameLayout.LayoutParams(mPiston.getLayoutParams());
        int newTop = (int) (PISTON_BREW_TOP * mView.getResources().getDisplayMetrics().density);
        newParams.setMargins(newParams.leftMargin, newTop, newParams.rightMargin, newParams.bottomMargin);
        mPiston.setLayoutParams(newParams);
        mTopNumberLabel.setVisibility(View.VISIBLE);
        mBottomNumberLabel.setVisibility(View.VISIBLE);
        mXButton.setVisibility(View.VISIBLE);
    }

    private void showExtractionView() {

        if (mAnimTimer != null) {
            mAnimTimer.cancel();
        }

        hideAllElements();
        setBorderStyle(BorderStyle.GLOW);
        mPiston.setVisibility(View.VISIBLE);
        FrameLayout.LayoutParams newParams = new FrameLayout.LayoutParams(mPiston.getLayoutParams());
        int newTop = (int) (PISTON_BREW_TOP * mView.getResources().getDisplayMetrics().density);
        newParams.setMargins(newParams.leftMargin, newTop, newParams.rightMargin, newParams.bottomMargin);
        mPiston.setLayoutParams(newParams);
        mTopNumberLabel.setVisibility(View.VISIBLE);
        mDropletHider.setVisibility(View.VISIBLE);
        for (int i = 0; i < mDroplet.length; i++) {
            mDroplet[i].setVisibility(View.VISIBLE);
            FrameLayout.LayoutParams dropParams = new FrameLayout.LayoutParams(mDroplet[i].getLayoutParams());
            int newLeft = (int) (DROPLET_EXTRACTION_LEFT[i] * mView.getResources().getDisplayMetrics().density);
            int newExtractionTop = (int) (DROPLET_EXTRACTION_TOP[i] * mView.getResources().getDisplayMetrics().density);
            dropParams.setMargins(newLeft, newExtractionTop, dropParams.rightMargin, dropParams.bottomMargin);
            mDroplet[i].setLayoutParams(dropParams);
            mDroplet[i].setAlpha(HALF_OPAQUE_ALPHA_FLOAT);
            mDropletTop[i] = i * DROPLET_EXTRACTION_VERTICAL_SPACING;
            if (i != 0) {
                mDropletTop[i] += DROPLET_EXRACTION_VERTICAL_OFFSET;
            }
        }
        mXButton.setVisibility(View.VISIBLE);

        mAnimTimer = new Timer();
        mAnimTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                mView.runOnUiThread(new Runnable() {
                    public void run() {

                        for (int i = 0; i < mDroplet.length; i++) {
                            mDropletTop[i] += DROPLET_TOP_EXTRACT_STEP;
                            if (mDropletTop[i] > DROPLET_TOP_FIRST_EXTRACT_LIMIT) mDropletTop[i] = DROPLET_TOP_EXTRACT_RESET;
                            FrameLayout.LayoutParams dropParams = new FrameLayout.LayoutParams(mDroplet[i].getLayoutParams());
                            int newExtractionLeft = (int) (DROPLET_EXTRACTION_LEFT[i] * mDensity);
                            int newExtractionTop = (int) ((mDropletTop[i] + DROPLET_EXTRACTION_TOP[0]) * mDensity);
                            dropParams.setMargins(newExtractionLeft, newExtractionTop, dropParams.rightMargin, dropParams.bottomMargin);
                            mDroplet[i].setLayoutParams(dropParams);
                            if (mDropletTop[i] > DROPLET_TOP_SECOND_EXTRACT_LIMIT) {
                                mDroplet[i].setAlpha(FULLY_OPAQUE_ALPHA_FLOAT - Math.max(FULLY_TRANSPARENT_ALPHA_FLOAT, ((float) mDropletTop[i] - DROPLET_TOP_SECOND_EXTRACT_LIMIT) / DROPLET_TOP_EXTRACT_ALPHA_SCALE_FACTOR));
                            } else if (mDropletTop[i] < DROPLET_TOP_THIRD_EXTRACT_LIMIT) { //greater than the top, but less than the bottom...
                                mDroplet[i].setAlpha(FULLY_OPAQUE_ALPHA_FLOAT - Math.max(FULLY_TRANSPARENT_ALPHA_FLOAT, (DROPLET_TOP_EXTRACT_ALPHA_SCALE_FACTOR - (float) mDropletTop[i]) / DROPLET_TOP_EXTRACT_ALPHA_SCALE_FACTOR));
                            } else {
                                mDroplet[i].setAlpha(FULLY_OPAQUE_ALPHA_FLOAT);
                            }
                        }
                    }
                });
            }
        }, new Date(), ONE_TENTH_SECOND_IN_MILLIS);
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
        mXButton.setVisibility(View.VISIBLE);
        mBigDropAlpha = STEAM_ARROW_MIN_ALPHA;
        mBigDropAlphaDelta = BIG_DROP_START_ALPHA_DELTA;

        mAnimTimer = new Timer();
        mAnimTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                mView.runOnUiThread(new Runnable() {
                    public void run() {
                        mBigDropAlpha += mBigDropAlphaDelta;
                        if (mBigDropAlpha <= STEAM_ARROW_MIN_ALPHA) {
                            mBigDropAlpha = STEAM_ARROW_MIN_ALPHA;
                            mBigDropAlphaDelta = Math.abs(mBigDropAlphaDelta);
                        } else if (mBigDropAlpha >= STEAM_ARROW_MAX_ALHPA) {
                            mBigDropAlpha = STEAM_ARROW_MAX_ALHPA;
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
        mXButton.setVisibility(View.VISIBLE);
        mBigDropAlpha = STEAM_ARROW_MIN_ALPHA;
        mBigDropAlphaDelta = BIG_DROP_START_ALPHA_DELTA;

        mAnimTimer = new Timer();
        mAnimTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                mView.runOnUiThread(new Runnable() {
                    public void run() {
                        mBigDropAlpha += mBigDropAlphaDelta;
                        if (mBigDropAlpha <= STEAM_ARROW_MIN_ALPHA) {
                            mBigDropAlpha = STEAM_ARROW_MIN_ALPHA;
                            mBigDropAlphaDelta = Math.abs(mBigDropAlphaDelta);
                        } else if (mBigDropAlpha >= STEAM_ARROW_MAX_ALHPA) {
                            mBigDropAlpha = STEAM_ARROW_MAX_ALHPA;
                            mBigDropAlphaDelta = -mBigDropAlphaDelta;
                        }
                        mBigDrop.setAlpha(mBigDropAlpha);
                    }
                });
            }
        }, new Date(), ONE_TENTH_SECOND_IN_MILLIS);
    }

    private void hideAllElements() {
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

                //				private SPCrucibleState mStartState;
                {
                    mAlpha = FULLY_OPAQUE_ALPHA;
                    mPositive = true;
//					mStartState = mModel.getStateForCrucible(mCrucibleIndex);
                }

                public void run() {
//					if (mModel.getStateForCrucible(mCrucibleIndex) != mStartState) return;

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

    public void show() {
        if (!mViewIsActive) {
            return;
        }
        mWholeLayout.setVisibility(View.VISIBLE);
    }

    public void hide() {
        if (!mViewIsActive) {
            return;
        }
        mWholeLayout.setVisibility(View.GONE);
    }
}
