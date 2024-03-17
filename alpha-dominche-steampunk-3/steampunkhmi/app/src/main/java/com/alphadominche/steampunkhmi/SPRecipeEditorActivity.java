package com.alphadominche.steampunkhmi;

import java.util.Arrays;
import java.util.Observable;
import java.util.Observer;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import com.alphadominche.steampunkhmi.utils.MachineSettings;
import com.alphadominche.steampunkhmi.utils.SteampunkUtils;

public class SPRecipeEditorActivity extends SPActivity implements Observer, OnClickListener {
    private Spinner mTeaRecipeFilterSpinner;

    private SeekBar mTempSlider;
    private SeekBar mVolumeSlider;
    private SeekBar mTotalTimeSlider;
    private SeekBar mVacuumBreakSlider;
    private SeekBar mExtractSlider;
    private SeekBar mGrindSlider;

    private TextView mTempLabel;
    private TextView mTempMinLabel;
    private TextView mTempMaxLabel;
    private TextView mTempUnitsLabel;
    private TextView mVolumeLabel;
    private TextView mVolumeMinLabel;
    private TextView mVolumeMaxLabel;
    private TextView mVolumeUnitsLabel;
    private TextView mTotalTimeLabel;
    private TextView mSmallTotalTimeLabel;
    private TextView mVacuumBreakLabel;
    private TextView mExtractLabel;

    private EditText mTeaRecipeNameField;
    private TextView mTeaRecipeGramsField;
    private TextView mTeaRecipeTeaspoonField;

    private EditText mCoffeeRecipeNameField;
    private TextView mCoffeeRecipeGramsField;
    private TextView mCoffeeRecipeGrindField;
    private Spinner mCoffeeRecipeFilterSpinner;

    private View mStackButton1;
    private View mStackButton2;
    private View mStackButton3;
    private View mStackButtonAdd2;
    private View mStackButtonAdd3;
    private View mStackRemoveButton;

    private View mTempHolder;
    private View mVolumeHolder;
    private View mTotalTimeHolder;
    private View mTotalTimeTitle;
    private View mVacuumBreakHolder;
    private View mVacuumBreakTitle;
    private View mExtractHolder;
    private View mExtractTitle;
    private View mGrindHolder;

    private SPAgListViewManager mAgMgr;

    private View mTempButton;
    private View mVolumeButton;

    private View mCancelButton;
    private View mSaveButton;

    private SPModel mSPModel;
    private SPRecipe mRecipe;
    private SPRecipe mOriginalRecipe;
    private int mCurrStack;
    private boolean mIgnoreUpdates;
    private SPRecipeSeekBarListener mListener;
    private boolean mBlockBackButton;
    private MachineSettings mMachineSettings;
    private SPTempUnitType mTempUnits;
    private SPVolumeUnitType mVolUnits;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBlockBackButton = false;
        mSPModel = SPModel.getInstance(this);
        mMachineSettings = MachineSettings.getMachineSettingsFromSharedPreferences(this.getApplicationContext());
        mTempUnits = mMachineSettings.getTempUnitType();
        mVolUnits = mMachineSettings.getVolumeUnitType();

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.sp_recipe_editor);

        mTeaRecipeFilterSpinner = (Spinner) findViewById(R.id.teaRecipeFilterChooser);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this,
                R.layout.spinner_text_vew,
                Arrays.asList(getResources().getStringArray(R.array.tea_filter_types)));
        mTeaRecipeFilterSpinner.setAdapter(adapter);
        mCoffeeRecipeFilterSpinner = (Spinner) findViewById(R.id.coffeeRecipeFilterChooser);
        adapter = new ArrayAdapter<String>(this, R.layout.spinner_text_vew, Arrays.asList(getResources().getStringArray(R.array.coffee_filter_types)));
        mCoffeeRecipeFilterSpinner.setAdapter(adapter);

        mRecipe = mSPModel.getCurrentlyEditedRecipe();
        mOriginalRecipe = new SPRecipe(mRecipe);
        if (mRecipe == null) {
            throw new IllegalArgumentException("No recipe found to edit!");
        }

        mIgnoreUpdates = false;
        mCurrStack = 0;
        mListener = new SPRecipeSeekBarListener(this);

        if (mRecipe.getType() == SPRecipeType.COFFEE) {
            findViewById(R.id.coffeeRecipeLayoutGeneralInfoHolder).setVisibility(View.VISIBLE);
            findViewById(R.id.teaRecipeLayoutGeneralInfoHolder).setVisibility(View.GONE);
        } else {
            findViewById(R.id.coffeeRecipeLayoutGeneralInfoHolder).setVisibility(View.GONE);
            findViewById(R.id.teaRecipeLayoutGeneralInfoHolder).setVisibility(View.VISIBLE);
        }

        mTempSlider = (SeekBar) findViewById(R.id.recipe_temp_slider);
        mVolumeSlider = (SeekBar) findViewById(R.id.recipe_volume_slider);
        mTotalTimeSlider = (SeekBar) findViewById(R.id.teaRecipeTotalTimeSlider);
        mVacuumBreakSlider = (SeekBar) findViewById(R.id.teaRecipeVacuumBreakTimeSlider);
        mExtractSlider = (SeekBar) findViewById(R.id.teaRecipeExtractTimeSlider);
        mGrindSlider = (SeekBar) findViewById(R.id.teaRecipeGrindSlider);

        mTempLabel = (TextView) findViewById(R.id.teaRecipeTempValueLabel);
        mTempUnitsLabel = (TextView) findViewById(R.id.teaRecipeTempUnitsLabel);
        mTempMinLabel = (TextView) findViewById(R.id.teaRecipeTempMinLabel);
        mTempMaxLabel = (TextView) findViewById(R.id.teaRecipeTempMaxLabel);
        mVolumeLabel = (TextView) findViewById(R.id.teaRecipeVolumeValueLabel);
        mVolumeUnitsLabel = (TextView) findViewById(R.id.teaRecipeVolumeUnitsLabel);
        mVolumeMinLabel = (TextView) findViewById(R.id.teaRecipeVolumeMinLabel);
        mVolumeMaxLabel = (TextView) findViewById(R.id.teaRecipeVolumeMaxLabel);
        mTotalTimeLabel = (TextView) findViewById(R.id.teaRecipeTotalTimeValueLabel);
        mSmallTotalTimeLabel = (TextView) findViewById(R.id.teaRecipeTotalTimeSmallValueLabel);
        mVacuumBreakLabel = (TextView) findViewById(R.id.teaRecipeVacuumBreakTimeValueLabel);
        mExtractLabel = (TextView) findViewById(R.id.teaRecipeExtractTimeValueLabel);

        mTeaRecipeNameField = (EditText) findViewById(R.id.teaRecipeNameField);
        mCoffeeRecipeNameField = (EditText) findViewById(R.id.coffeeRecipeNameField);

        if (mTempUnits == SPTempUnitType.FAHRENHEIT) {
            mTempMaxLabel.setText("" + SPRecipe.MAX_TEMPERATURE_F);
            mTempMinLabel.setText("" + SPRecipe.MIN_TEMPERATURE_F);
            mTempUnitsLabel.setText(getResources().getString(R.string.fahrenheit_abreviation));
            mTempSlider.setMax((int) ((SPRecipe.MAX_TEMPERATURE_F - SPRecipe.MIN_TEMPERATURE_F) / (double) SPRecipe.TEMPERATURE_STEP));
        } else {
            mTempMaxLabel.setText("" + SPRecipe.MAX_TEMPERATURE_C);
            mTempMinLabel.setText("" + SPRecipe.MIN_TEMPERATURE_C);
            mTempUnitsLabel.setText(getResources().getString(R.string.celcius_abreviation));
            mTempSlider.setMax((int) ((SPRecipe.MAX_TEMPERATURE_C - SPRecipe.MIN_TEMPERATURE_C) / (double) SPRecipe.TEMPERATURE_STEP));
        }
        if (mVolUnits == SPVolumeUnitType.OUNCES) {
            mVolumeMaxLabel.setText("" + SPRecipe.MAX_VOLUME_OZ);
            mVolumeMinLabel.setText("" + SPRecipe.MIN_VOLUME_OZ);
            mVolumeUnitsLabel.setText(getResources().getString(R.string.ounces_abreviation));
            mVolumeSlider.setMax((int) ((SPRecipe.MAX_VOLUME_OZ - SPRecipe.MIN_VOLUME_OZ) / (double) SPRecipe.VOLUME_OZ_STEP));
        } else {
            mVolumeMaxLabel.setText("" + SPRecipe.MAX_VOLUME_ML);
            mVolumeMinLabel.setText("" + SPRecipe.MIN_VOLUME_ML);
            mVolumeUnitsLabel.setText(getResources().getString(R.string.milliliters_abreviation));
            mVolumeSlider.setMax((int) ((SPRecipe.MAX_VOLUME_ML - SPRecipe.MIN_VOLUME_ML) / (double) SPRecipe.VOLUME_ML_STEP));
        }
        mTotalTimeSlider.setMax((int) ((SPRecipe.MAX_TOTAL_TIME - SPRecipe.MIN_TOTAL_TIME) / (double) SPRecipe.TOTAL_TIME_STEP));
        mVacuumBreakSlider.setMax((int) ((SPRecipe.MAX_VACUUM_BREAK_TIME - SPRecipe.MIN_VACUUM_BREAK_TIME) / (double) SPRecipe.VACUUM_BREAK_STEP));
        mExtractSlider.setMax((int) ((SPRecipe.MAX_EXTRACT_TIME - SPRecipe.MIN_EXTRACT_TIME) / (double) SPRecipe.EXTRACT_TIME_STEP));
        mGrindSlider.setMax((int) ((SPRecipe.MAX_GRIND - SPRecipe.MIN_GRIND) / (double) SPRecipe.GRIND_STEP));
        mTempSlider.setOnSeekBarChangeListener(mListener);
        mVolumeSlider.setOnSeekBarChangeListener(mListener);
        mTempSlider.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        mVolumeSlider.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        mTotalTimeSlider.setOnSeekBarChangeListener(mListener);
        mVacuumBreakSlider.setOnSeekBarChangeListener(mListener);
        mGrindSlider.setOnSeekBarChangeListener(mListener);
        mExtractSlider.setOnSeekBarChangeListener(mListener);


        TextWatcher nameWatcher = new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (mRecipe.getType() == SPRecipeType.COFFEE) {
                    mRecipe.setName(mCoffeeRecipeNameField.getText().toString());
                } else {
                    mRecipe.setName(mTeaRecipeNameField.getText().toString());
                }
            }
        };
        if (mRecipe.getType() == SPRecipeType.COFFEE) {
            mCoffeeRecipeNameField.setText(mRecipe.getName());
        } else {
            mTeaRecipeNameField.setText(mRecipe.getName());
        }
        mCoffeeRecipeNameField.addTextChangedListener(nameWatcher);
        mTeaRecipeNameField.addTextChangedListener(nameWatcher);

        mTeaRecipeGramsField = (TextView) findViewById(R.id.teaRecipeGramsField);
        mTeaRecipeTeaspoonField = (TextView) findViewById(R.id.teaRecipeTeaspoonField);
        mCoffeeRecipeGrindField = (TextView) findViewById(R.id.coffeeRecipeGrindField);

        mGrindHolder = findViewById(R.id.coffeeRecipeGrindHolder);
        mTempHolder = findViewById(R.id.teaRecipeTempInfo);
        mVolumeHolder = findViewById(R.id.teaRecipeVolumeInfo);
        mTotalTimeHolder = findViewById(R.id.teaRecipeTotalTimeLabelHolder);
        mTotalTimeTitle = findViewById(R.id.teaRecipeTotalTimeLabel);
        mVacuumBreakHolder = findViewById(R.id.teaRecipeVacuumBreakTimeLabelHolder);
        mVacuumBreakTitle = findViewById(R.id.teaRecipeVacuumBreakTitleLabel);
        mExtractHolder = findViewById(R.id.teaRecipeExtractTimeLabelHolder);
        mExtractTitle = findViewById(R.id.teaRecipeExtractTimeLabel);

        mTempButton = findViewById(R.id.recipe_temp_link);
        mVolumeButton = findViewById(R.id.recipe_volume_link);

        mCoffeeRecipeGramsField = (TextView) findViewById(R.id.coffeeRecipeGramsField);

        OnClickListener sliderClickListener = new OnClickListener() {

            @Override
            public void onClick(View v) {
                SeekBar imposter = null;
                if (v == mTotalTimeHolder || v == mTotalTimeTitle) {
                    imposter = mTotalTimeSlider;
                    SPSliderModalFragment frag = new SPSliderModalFragment();
                    frag.show(getFragmentManager(), "sliderModal");
                    frag.setListenerAndImposter(mListener, imposter);
                    frag.setLimitsUnitsAndTitle(SPRecipe.MIN_TOTAL_TIME, SPRecipe.MAX_TOTAL_TIME, getResources().getString(R.string.seconds_abbreviation), getResources().getString(R.string.total_time_slider_title));
                } else if (v == mTempHolder || v == mTempLabel || v == mTempButton) {
                    imposter = mTempSlider;
                    SPSliderModalFragment frag = new SPSliderModalFragment();
                    frag.setProgressColor(SPSliderColor.RED, getApplicationContext());
                    frag.show(getFragmentManager(), "sliderModal");
                    frag.setListenerAndImposter(mListener, imposter);
                    if (mTempUnits == SPTempUnitType.FAHRENHEIT) {
                        frag.setLimitsUnitsAndTitle(SPRecipe.MIN_TEMPERATURE_F, SPRecipe.MAX_TEMPERATURE_F, getResources().getString(R.string.fahrenheit_abreviation), getResources().getString(R.string.temperature_slider_title));
                    } else {
                        frag.setLimitsUnitsAndTitle(SPRecipe.MIN_TEMPERATURE_C, SPRecipe.MAX_TEMPERATURE_C, getResources().getString(R.string.celcius_abreviation), getResources().getString(R.string.temperature_slider_title));
                    }
                } else if (v == mVolumeHolder || v == mVolumeLabel || v == mVolumeButton) {
                    imposter = mVolumeSlider;
                    SPSliderModalFragment frag = new SPSliderModalFragment();
                    frag.setProgressColor(SPSliderColor.BLUE, getApplicationContext());
                    frag.show(getFragmentManager(), "sliderModal");
                    frag.setListenerAndImposter(mListener, imposter);
                    if (mVolUnits == SPVolumeUnitType.OUNCES) {
                        frag.setLimitsUnitsAndTitle((int) SPRecipe.MIN_VOLUME_OZ, (int) SPRecipe.MAX_VOLUME_OZ, getResources().getString(R.string.ounces_abreviation), getResources().getString(R.string.volume_slider_title));
                    } else {
                        frag.setLimitsUnitsAndTitle((int) SPRecipe.MIN_VOLUME_ML, (int) SPRecipe.MAX_VOLUME_ML, getResources().getString(R.string.milliliters_abreviation), getResources().getString(R.string.volume_slider_title));
                    }
                } else if (v == mVacuumBreakHolder || v == mVacuumBreakTitle) {
                    imposter = mVacuumBreakSlider;
                    SPSliderModalFragment frag = new SPSliderModalFragment();
                    frag.show(getFragmentManager(), "sliderModal");
                    frag.setListenerAndImposter(mListener, imposter);
                    frag.setLimitsUnitsAndTitle(2, SPRecipe.MIN_VACUUM_BREAK_TIME, SPRecipe.MAX_VACUUM_BREAK_TIME, getResources().getString(R.string.seconds_abbreviation), getResources().getString(R.string.vacuum_break_slider_title));
                } else if (v == mExtractHolder || v == mExtractTitle) {
                    imposter = mExtractSlider;
                    SPSliderModalFragment frag = new SPSliderModalFragment();
                    frag.show(getFragmentManager(), "sliderModal");
                    frag.setListenerAndImposter(mListener, imposter);
                    frag.setLimitsUnitsAndTitle(SPRecipe.MIN_EXTRACT_TIME, SPRecipe.MAX_EXTRACT_TIME, getResources().getString(R.string.seconds_abbreviation), getResources().getString(R.string.pull_down_slider_title));
                } else if (v == mGrindHolder || v == mCoffeeRecipeGrindField) {
                    int newMax = (int) ((SPRecipe.MAX_GRIND - SPRecipe.MIN_GRIND) / (double) SPRecipe.GRIND_STEP);
                    int newProgress = (int) (((mRecipe.getGrind() - SPRecipe.MIN_GRIND) / SPRecipe.GRIND_STEP));
                    SPSliderModalFragment frag = new SPSliderModalFragment();
                    frag.show(getFragmentManager(), "sliderModal");
                    frag.setResponder(new SPSliderModalResponder() {
                        @Override
                        public void setValue(int value) {
                            mCoffeeRecipeGrindField.setText("" + value * SPRecipe.GRIND_STEP + SPRecipe.MIN_GRIND);
                            // update the model...
                            mRecipe.setGrind(value * SPRecipe.GRIND_STEP + SPRecipe.MIN_GRIND);
                        }
                    }, newMax, newProgress);

                    frag.setLimitsUnitsAndTitle(2, SPRecipe.MIN_GRIND, SPRecipe.MAX_GRIND, getResources().getString(R.string.value), getResources().getString(R.string.grind_slider_title));
                } else if (v == mTeaRecipeGramsField) {

                    int newMax = (int) ((SPRecipe.MAX_GRAMS - SPRecipe.MIN_GRAMS) / (double) SPRecipe.GRAMS_STEP);
                    int newProgress = (int) (((mRecipe.getGrams() - SPRecipe.MIN_GRAMS) / SPRecipe.GRAMS_STEP));
                    SPSliderModalFragment frag = new SPSliderModalFragment();
                    frag.show(getFragmentManager(), "sliderModal");
                    frag.setResponder(new SPSliderModalResponder() {
                        @Override
                        public void setValue(int value) {
                            mTeaRecipeGramsField.setText("" + value * SPRecipe.GRAMS_STEP + SPRecipe.MIN_GRAMS);
                            // update the model...
                            mRecipe.setGrams(value * SPRecipe.GRAMS_STEP + SPRecipe.MIN_GRAMS);
                        }
                    }, newMax, newProgress);
                    frag.setLimitsUnitsAndTitle(SPRecipe.MIN_GRAMS,
                            SPRecipe.MAX_GRAMS, "grams", "Tea Weight");
                } else if (v == mTeaRecipeTeaspoonField) {
                    int newMax = (int) ((SPRecipe.MAX_TEASPOONS - SPRecipe.MIN_TEASPOONS) / (double) SPRecipe.TEASPOONS_STEP);
                    int newProgress = (int) (((mRecipe.getTeaspoons() - SPRecipe.MIN_TEASPOONS) / SPRecipe.TEASPOONS_STEP));
                    SPSliderModalFragment frag = new SPSliderModalFragment();
                    frag.show(getFragmentManager(), "sliderModal");
                    frag.setResponder(new SPSliderModalResponder() {
                        @Override
                        public void setValue(int value) {
                            mTeaRecipeTeaspoonField.setText("" + value * SPRecipe.TEASPOONS_STEP + SPRecipe.MIN_TEASPOONS);
                            // update the model...
                            mRecipe.setTeaspoons(value * SPRecipe.TEASPOONS_STEP + SPRecipe.MIN_TEASPOONS);
                        }
                    }, newMax, newProgress);
                    frag.setLimitsUnitsAndTitle(1, SPRecipe.MIN_TEASPOONS, SPRecipe.MAX_TEASPOONS, getResources().getString(R.string.teaspoon_abbreviation), getResources().getString(R.string.tea_volume_slider_title));
                } else if (v == mCoffeeRecipeGramsField) {
                    int newMax = (int) ((SPRecipe.MAX_GRAMS - SPRecipe.MIN_GRAMS) / (double) SPRecipe.GRAMS_STEP);
                    int newProgress = (int) (((mRecipe.getGrams() - SPRecipe.MIN_GRAMS) / SPRecipe.GRAMS_STEP));
                    SPSliderModalFragment frag = new SPSliderModalFragment();
                    frag.show(getFragmentManager(), "sliderModal");
                    frag.setResponder(new SPSliderModalResponder() {
                        @Override
                        public void setValue(int value) {
                            mCoffeeRecipeGramsField.setText("" + value * SPRecipe.GRAMS_STEP + SPRecipe.MIN_GRAMS);
                            // update the model...
                            mRecipe.setGrams(value * SPRecipe.GRAMS_STEP + SPRecipe.MIN_GRAMS);
                        }
                    }, newMax, newProgress);
                    frag.setLimitsUnitsAndTitle(SPRecipe.MIN_GRAMS, SPRecipe.MAX_GRAMS, getResources().getString(R.string.grams), getResources().getString(R.string.coffee_weight_slider_title));
                }
            }
        };

        mTempHolder.setOnClickListener(sliderClickListener);
        mVolumeHolder.setOnClickListener(sliderClickListener);
        mTotalTimeHolder.setOnClickListener(sliderClickListener);
        mTotalTimeTitle.setOnClickListener(sliderClickListener);
        mVacuumBreakHolder.setOnClickListener(sliderClickListener);
        mVacuumBreakTitle.setOnClickListener(sliderClickListener);
        mExtractHolder.setOnClickListener(sliderClickListener);
        mExtractTitle.setOnClickListener(sliderClickListener);
        mTeaRecipeGramsField.setOnClickListener(sliderClickListener);
        mTeaRecipeTeaspoonField.setOnClickListener(sliderClickListener);
        mCoffeeRecipeGramsField.setOnClickListener(sliderClickListener);
        mGrindHolder.setOnClickListener(sliderClickListener);

        mTempButton.setOnClickListener(sliderClickListener);
        mVolumeButton.setOnClickListener(sliderClickListener);

        mStackButton1 = findViewById(R.id.teaRecipeStackButton1);
        mStackButton2 = findViewById(R.id.teaRecipeStackButton2);
        mStackButton3 = findViewById(R.id.teaRecipeStackButton3);
        mStackButtonAdd2 = findViewById(R.id.teaRecipeStackButtonAdd2);
        mStackButtonAdd3 = findViewById(R.id.teaRecipeStackButtonAdd3);
        mStackRemoveButton = findViewById(R.id.teaRecipeStackRemoveButton);

        if (mRecipe.getType() == SPRecipeType.COFFEE) {
            mStackButton1.setVisibility(View.GONE);
            mStackButton2.setVisibility(View.GONE);
            mStackButton3.setVisibility(View.GONE);
            mStackButtonAdd2.setVisibility(View.GONE);
            mStackButtonAdd3.setVisibility(View.GONE);
            mStackRemoveButton.setVisibility(View.GONE);
        }

        mCancelButton = findViewById(R.id.cancel_button);
        mSaveButton = findViewById(R.id.save_button);

        mStackButton1.setOnClickListener(this);
        mStackButton2.setOnClickListener(this);
        mStackButton3.setOnClickListener(this);
        mStackButtonAdd2.setOnClickListener(this);
        mStackButtonAdd3.setOnClickListener(this);
        mStackRemoveButton.setOnClickListener(this);

        mCancelButton.setOnClickListener(this);
        mSaveButton.setOnClickListener(this);

        // Disable all fields for unowned recipes
        if (mRecipe.getRoaster() != null && SteampunkUtils.getCurrentSteampunkUserId(getApplicationContext()) != mRecipe.getRoaster().getId()) {
            if (!mRecipe.isNewRecipe()) {
                disableView();
            }
        }

        displayWarningMessage();
    }

    private void disableView() {
        mSaveButton.setVisibility(View.INVISIBLE);
        mCoffeeRecipeNameField.setEnabled(false);
        mTeaRecipeNameField.setEnabled(false);

        mTempButton.setEnabled(false);
        mVolumeButton.setEnabled(false);

        mCoffeeRecipeFilterSpinner.setEnabled(false);
        mTeaRecipeFilterSpinner.setEnabled(false);
        mTempHolder.setEnabled(false);
        mVolumeHolder.setEnabled(false);
        mTotalTimeHolder.setEnabled(false);
        mTotalTimeTitle.setEnabled(false);
        mVacuumBreakHolder.setEnabled(false);
        mVacuumBreakTitle.setEnabled(false);
        mExtractHolder.setEnabled(false);
        mExtractTitle.setEnabled(false);
        mTeaRecipeGramsField.setEnabled(false);
        mTeaRecipeTeaspoonField.setEnabled(false);
        mCoffeeRecipeGramsField.setEnabled(false);
        mGrindHolder.setEnabled(false);

        mStackButtonAdd2.setEnabled(false);
        mStackButtonAdd3.setEnabled(false);
        mStackRemoveButton.setEnabled(false);
    }

    @Override
    public void onStart() {
        super.onStart();
        OnItemSelectedListener selectionListener = new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> spinner, View selection, int position, long id) {

                if (spinner == mTeaRecipeFilterSpinner) {
                    mRecipe.setFilter(getResources().getStringArray(R.array.tea_filter_types)[position]);
                } else if (spinner == mCoffeeRecipeFilterSpinner) {
                    mRecipe.setFilter(getResources().getStringArray(R.array.coffee_filter_types)[position]);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> spinner) {
                if (spinner == mTeaRecipeFilterSpinner) {
                } else if (spinner == mCoffeeRecipeFilterSpinner) {
                }
            }
        };

        mTeaRecipeFilterSpinner.setOnItemSelectedListener(selectionListener);
        mCoffeeRecipeFilterSpinner.setOnItemSelectedListener(selectionListener);
    }

    @Override
    public void onResume() {
        super.onResume();
        mMachineSettings = MachineSettings.getMachineSettingsFromSharedPreferences(this.getApplicationContext());
        mTempUnits = mMachineSettings.getTempUnitType();
        mVolUnits = mMachineSettings.getVolumeUnitType();
        initAgBtnMgr();
        mRecipe.addObserver(this);
        update(null, null);
    }

    private void initAgBtnMgr() {
        if (mAgMgr != null) {
            SPLog.debug("changing ag list view manager");
            mAgMgr.setRecipe(mRecipe);
            mAgMgr.setStackIndex(mCurrStack);
            //mAgMgr.cleanOutGUIMembers();
        } else {
            SPLog.debug("creating ag list view manager");
            View buttonContainer = findViewById(R.id.ag_edit_btn_layout);
            FrameLayout graphicContainer = (FrameLayout) findViewById(R.id.agitation_graphic_holder_inner);
//			if (mAgMgr != null) {
//				mRecipe.deleteObserver(mAgMgr);
//			}
            mAgMgr = new SPAgListViewManager(buttonContainer, graphicContainer, mRecipe, 0, this);
//			mRecipe.addObserver(mAgMgr)
            ;
        }
    }

    public void onPause() {
        super.onPause();
        mRecipe.deleteObserver(this);
//		if (mAgMgr != null) {
//			mRecipe.deleteObserver(mAgMgr);
//		}
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.tea_recipe_editor, menu);
        return true;
    }

    public void seekBarProgressChanged(SeekBar seekBar, int progress) {
        mTempUnits = SPModel.getInstance(this).getTempUnits();
        if (seekBar == mTempSlider) {
            if (mTempUnits == SPTempUnitType.FAHRENHEIT) {
                mRecipe.setTemp(mCurrStack, SPServiceThermistor.convertFromTempToTemp(
                        mTempUnits,
                        SPTempUnitType.KELVIN, progress * SPRecipe.TEMPERATURE_STEP + SPRecipe.MIN_TEMPERATURE_F));
            } else {
                mRecipe.setTemp(
                        mCurrStack,
                        SPServiceThermistor.convertFromTempToTemp(
                                mTempUnits,
                                SPTempUnitType.KELVIN,
                                progress * SPRecipe.TEMPERATURE_STEP + SPRecipe.MIN_TEMPERATURE_C));
            }
        } else if (seekBar == mVolumeSlider) {
            if (mVolUnits == SPVolumeUnitType.OUNCES) {
                mRecipe.setVolume(mCurrStack, SPFlowMeter.convertFromOuncesToMilliliters(progress * SPRecipe.VOLUME_OZ_STEP + SPRecipe.MIN_VOLUME_OZ));
            } else {
                mRecipe.setVolume(mCurrStack, progress * SPRecipe.VOLUME_ML_STEP + SPRecipe.MIN_VOLUME_ML);
            }
        } else if (seekBar == mTotalTimeSlider) {
            mRecipe.setTotalTime(mCurrStack, progress * SPRecipe.TOTAL_TIME_STEP + SPRecipe.MIN_TOTAL_TIME);
        } else if (seekBar == mVacuumBreakSlider) {
            mRecipe.setVacuumBreak(mCurrStack, progress * SPRecipe.VACUUM_BREAK_STEP + SPRecipe.MIN_VACUUM_BREAK_TIME);
        } else if (seekBar == mExtractSlider) {
            mRecipe.setExtractionSeconds(mCurrStack, progress);
        } else if (seekBar == mGrindSlider) {
            mRecipe.setGrind(progress * SPRecipe.GRIND_STEP + SPRecipe.MIN_GRIND);
        }
        update(null, null);
    }

    @Override
    public void update(Observable arg0, Object arg1) {
        SPLog.debug("updating from recipe edit!");
        if (mIgnoreUpdates)
            return;

        mTempUnits = SPModel.getInstance(this).getTempUnits();

        if (mTempUnits == SPTempUnitType.FAHRENHEIT) {
            mTempMinLabel.setText("" + SPRecipe.MIN_TEMPERATURE_F);
            mTempMaxLabel.setText("" + SPRecipe.MAX_TEMPERATURE_F);
            mTempSlider.setMax(SPRecipe.MAX_TEMPERATURE_F - SPRecipe.MIN_TEMPERATURE_F);
            mTempSlider.setProgress((int) Math.round((SPServiceThermistor.convertFromTempToTemp(
                    SPTempUnitType.KELVIN,
                    mTempUnits,
                    mRecipe.getTemp(mCurrStack)) - SPRecipe.MIN_TEMPERATURE_F) / SPRecipe.TEMPERATURE_STEP));
        } else {
            mTempMinLabel.setText("" + SPRecipe.MIN_TEMPERATURE_C);
            mTempMaxLabel.setText("" + SPRecipe.MAX_TEMPERATURE_C);
            mTempSlider.setMax(SPRecipe.MAX_TEMPERATURE_C - SPRecipe.MIN_TEMPERATURE_C);
            mTempSlider.setProgress((int) Math.round((SPServiceThermistor.convertFromTempToTemp(
                    SPTempUnitType.KELVIN,
                    mTempUnits,
                    mRecipe.getTemp(mCurrStack)) - SPRecipe.MIN_TEMPERATURE_C) / SPRecipe.TEMPERATURE_STEP));
        }
        mTempLabel.setText((int) Math.round(SPServiceThermistor.convertFromTempToTemp(
                SPTempUnitType.KELVIN,
                mTempUnits,
                mRecipe.getTemp(mCurrStack))) + DEGREE_SYMBOL);

        if (mVolUnits == SPVolumeUnitType.OUNCES) {
            mVolumeSlider.setProgress((int) Math.round(SPFlowMeter.convertFromMillilitersToOunces(mRecipe.getVolume(mCurrStack)) - SPRecipe.MIN_VOLUME_OZ));
            mVolumeLabel.setText("" + (int) Math.round(SPFlowMeter.convertFromMillilitersToOunces(mRecipe.getVolume(mCurrStack))));

        } else {
            mVolumeSlider.setProgress((int) Math.round(mRecipe.getVolume(mCurrStack) - SPRecipe.MIN_VOLUME_ML));
            mVolumeLabel.setText("" + (int) Math.round(mRecipe.getVolume(mCurrStack)));
        }

        mTotalTimeSlider.setProgress((int) (mRecipe.getTotalTime(mCurrStack)));
        mTotalTimeLabel.setText("" + mRecipe.getTotalTime(mCurrStack));
        mSmallTotalTimeLabel.setText("" + mRecipe.getTotalTime(mCurrStack));
        mVacuumBreakSlider.setProgress((int) (mRecipe.getVacuumBreak(mCurrStack) / SPRecipe.VACUUM_BREAK_STEP));
        mVacuumBreakLabel.setText("" + String.format("%.2f", mRecipe.getVacuumBreak(mCurrStack)));
        mExtractSlider.setProgress(mRecipe.getExtractionSeconds(mCurrStack));
        mExtractLabel.setText("" + mRecipe.getExtractionSeconds(mCurrStack));
        if (mRecipe.getType() == SPRecipeType.TEA) {
            mTeaRecipeFilterSpinner.setSelection(mRecipe.getFilterId());
        } else {
            mCoffeeRecipeFilterSpinner.setSelection(mRecipe.getFilterId());
            mCoffeeRecipeGrindField.setText("" + String.format("%.2f", mRecipe.getGrind()));
        }
        mTeaRecipeGramsField.setText("" + mRecipe.getGrams());
        mCoffeeRecipeGramsField.setText("" + mRecipe.getGrams());
        mTeaRecipeTeaspoonField.setText("" + mRecipe.getTeaspoons());
        if (mRecipe.getType() == SPRecipeType.TEA) {
            int stackCount = mRecipe.getStackCount();
            if (stackCount - 1 == 0) {
                mStackButton1.setVisibility(View.VISIBLE);
                mStackButton2.setVisibility(View.INVISIBLE);
                mStackButton3.setVisibility(View.INVISIBLE);
                mStackRemoveButton.setVisibility(View.INVISIBLE);
                mStackButtonAdd2.setVisibility(View.VISIBLE);
                mStackButtonAdd3.setVisibility(View.INVISIBLE);
            } else if (stackCount - 1 == 1) {
                mStackButton1.setVisibility(View.VISIBLE);
                mStackButton2.setVisibility(View.VISIBLE);
                mStackButton3.setVisibility(View.INVISIBLE);
                mStackRemoveButton.setVisibility(View.VISIBLE);
                mStackButtonAdd2.setVisibility(View.INVISIBLE);
                mStackButtonAdd3.setVisibility(View.VISIBLE);
            } else if (stackCount - 1 == 2) {
                mStackButton1.setVisibility(View.VISIBLE);
                mStackButton2.setVisibility(View.VISIBLE);
                mStackButton3.setVisibility(View.VISIBLE);
                mStackRemoveButton.setVisibility(View.VISIBLE);
                mStackButtonAdd2.setVisibility(View.INVISIBLE);
                mStackButtonAdd3.setVisibility(View.INVISIBLE);
            }
        }
        mAgMgr.setStackIndex(mCurrStack);
        mAgMgr.update(null, null);
    }

    @Override
    public void onClick(View v) {
        if (v == mStackButtonAdd2) {
            mRecipe.addStack();
            mStackButton2.setVisibility(View.VISIBLE);
            mStackButtonAdd2.setVisibility(View.INVISIBLE);
            mStackButtonAdd3.setVisibility(View.VISIBLE);
            mStackRemoveButton.setVisibility(View.VISIBLE);
            mStackButton1.setBackground(getResources().getDrawable(R.drawable.inactive_stack));
            mStackButton2.setBackground(getResources().getDrawable(R.drawable.active_stack));
            mCurrStack = 1;
            mIgnoreUpdates = true;
            mIgnoreUpdates = false;
            update(null, null);
        } else if (v == mStackButtonAdd3) {
            mRecipe.addStack();
            mStackButton3.setVisibility(View.VISIBLE);
            mStackButtonAdd3.setVisibility(View.INVISIBLE);
            mStackRemoveButton.setVisibility(View.VISIBLE);
            mStackButton1.setBackground(getResources().getDrawable(R.drawable.inactive_stack));
            mStackButton2.setBackground(getResources().getDrawable(R.drawable.inactive_stack));
            mStackButton3.setBackground(getResources().getDrawable(R.drawable.active_stack));
            mCurrStack = 2;
            mIgnoreUpdates = true;
            mIgnoreUpdates = false;
            update(null, null);
        } else if (v == mStackButton1) {
            mStackButton1.setBackground(getResources().getDrawable(R.drawable.active_stack));
            mStackButton2.setBackground(getResources().getDrawable(R.drawable.inactive_stack));
            mStackButton3.setBackground(getResources().getDrawable(R.drawable.inactive_stack));
            mCurrStack = 0;
            update(null, null);
        } else if (v == mStackButton2) {
            mStackButton1.setBackground(getResources().getDrawable(R.drawable.inactive_stack));
            mStackButton2.setBackground(getResources().getDrawable(R.drawable.active_stack));
            mStackButton3.setBackground(getResources().getDrawable(R.drawable.inactive_stack));
            mCurrStack = 1;
            update(null, null);
        } else if (v == mStackButton3) {
            mStackButton1.setBackground(getResources().getDrawable(R.drawable.inactive_stack));
            mStackButton2.setBackground(getResources().getDrawable(R.drawable.inactive_stack));
            mStackButton3.setBackground(getResources().getDrawable(R.drawable.active_stack));
            mCurrStack = 2;
            update(null, null);
        } else if (v == mStackRemoveButton) {
            mCurrStack -= 1;
            if (mCurrStack < 0) {
                mCurrStack = 0;
                mRecipe.removeStack(mCurrStack);
            } else {
                mRecipe.removeStack(mCurrStack + 1);
            }
            if (mRecipe.getStackCount() > 1) {
                mStackRemoveButton.setVisibility(View.VISIBLE);
            } else {
                mStackRemoveButton.setVisibility(View.INVISIBLE);
            }
            if (mRecipe.getStackCount() > 1) { // need to make the appropriate
                // stacks appear and disappear
                // based on stack count
                mStackButton2.setVisibility(View.VISIBLE);
                mStackButtonAdd2.setVisibility(View.INVISIBLE);
                mStackButtonAdd3.setVisibility(View.VISIBLE);
                if (mRecipe.getStackCount() > 2) {
                    mStackButton3.setVisibility(View.VISIBLE);
                    mStackButtonAdd3.setVisibility(View.INVISIBLE);
                } else {
                    mStackButton3.setVisibility(View.INVISIBLE);
                }
            } else {
                mStackButton2.setVisibility(View.INVISIBLE);
                mStackButtonAdd2.setVisibility(View.VISIBLE);
                mStackButtonAdd3.setVisibility(View.INVISIBLE);
            }
            if (mCurrStack > mRecipe.getStackCount() - 1) {
                mCurrStack--;
            }
            if (mCurrStack == 0) {
                mStackButton1.setBackground(getResources().getDrawable(R.drawable.active_stack));
                mStackButton2.setBackground(getResources().getDrawable(R.drawable.inactive_stack));
                mStackButton3.setBackground(getResources().getDrawable(R.drawable.inactive_stack));
            } else if (mCurrStack == 1) {
                mStackButton1.setBackground(getResources().getDrawable(R.drawable.inactive_stack));
                mStackButton2.setBackground(getResources().getDrawable(R.drawable.active_stack));
                mStackButton3.setBackground(getResources().getDrawable(R.drawable.inactive_stack));
            } else if (mCurrStack == 2) {
                mStackButton1.setBackground(getResources().getDrawable(R.drawable.inactive_stack));
                mStackButton2.setBackground(getResources().getDrawable(R.drawable.inactive_stack));
                mStackButton3.setBackground(getResources().getDrawable(R.drawable.active_stack));
            }
            update(null, null);
        } else if (v == mCancelButton) {
            mIgnoreUpdates = true;

            //copy the original recipe back!
            mRecipe.copyRecipeIntoSelf(mOriginalRecipe);

            //disconect GUI!
            mCurrStack = 0;
            mStackButton1.setBackground(getResources().getDrawable(R.drawable.active_stack));
            mStackButton2.setBackground(getResources().getDrawable(R.drawable.inactive_stack));
            mStackButton3.setBackground(getResources().getDrawable(R.drawable.inactive_stack));
            mStackButton2.setVisibility(View.INVISIBLE);
            mStackButton3.setVisibility(View.INVISIBLE);
            mStackButtonAdd2.setVisibility(View.VISIBLE);
            mStackButtonAdd3.setVisibility(View.INVISIBLE);
            SPModel.getInstance(this).setCurrentlyEditedRecipe(null);
            mIgnoreUpdates = false;
            cancelPressed();
        } else if (v == mSaveButton) {
            mRecipe.save(this.getApplicationContext());
            cancelPressed();
        }
    }

    private void blockAllUserAction() {
        mSaveButton.setClickable(false);
        mCancelButton.setClickable(false);
        mBlockBackButton = true;

    }

    private void enableUserAction() {
        mSaveButton.setClickable(true);
        mCancelButton.setClickable(true);
        mBlockBackButton = false;
    }

    public void cancelPressed() {
        if (!mBlockBackButton) {
            setResult(RESULT_CANCELED);
            this.finish();
        }
    }

    public void displayWarningMessage() {
        WarningDialog dialog = new WarningDialog();
        dialog.setText(this.getResources().getString(R.string.edit_recipe_warning_dialog_message));
        dialog.show(getFragmentManager(), WarningDialog.TAG);
    }

}
