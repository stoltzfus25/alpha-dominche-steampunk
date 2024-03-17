package com.alphadominche.steampunkhmi;

import java.util.Observable;
import java.util.Observer;

import android.content.Context;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.TextView;

public class SPSwitchboardActivity extends SPActivity implements Observer, SPModel.IOIOConnectionObserver {
    private SwitchboardCrucibleController[] mCrucibles;
    private TextView mBoilerTempLabel;
    private View mConnectivityIndicator;
    private View mNoConnectivityIndicator;
    private View mBoilerHeatingElementBtn;
    private View mBoilerFillIndicator;
    private View mCloseBtn;

    private int mCrucibleCount;
    private boolean mActive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_manual_mode);

        mCrucibles = new SwitchboardCrucibleController[SPIOIOService.MAX_CRUCIBLE_COUNT];
        for (int i = 0; i < mCrucibles.length; i++) {
            mCrucibles[i] = new SwitchboardCrucibleController(i, this);
        }

        //get crucible count and hide any inactive crucibles
        mCrucibleCount = SPModel.getInstance(this).getCrucibleCount();
        for (int i = 0; i < mCrucibleCount; i++) {
            mCrucibles[i].show();
        }
        for (int i = mCrucibleCount; i < SPIOIOService.MAX_CRUCIBLE_COUNT; i++) {
            mCrucibles[i].hide();
        }

        //get boiler widgets
        mBoilerTempLabel = (TextView) findViewById(R.id.boiler_temp_label);
        mConnectivityIndicator = findViewById(R.id.connectivity_indicator);
        mNoConnectivityIndicator = findViewById(R.id.no_connectivity_indicator);
        mBoilerHeatingElementBtn = findViewById(R.id.heating_element_button);
//		mBoilerFillIndicator = fincViewById(R.id.boiler_fill_indicator); // TODO need this put into the design

        mCloseBtn = findViewById(R.id.close_button);

        mBoilerHeatingElementBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                SPModel model = SPModel.getInstance(getContext());

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        model.turnBoilerHeatOn();
                        break;
                    case MotionEvent.ACTION_UP:
                        model.turnBoilerHeatOff();
                        v.performClick();
                        break;
                }

                return true;
            }
        });

        mCloseBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        for (int i = 0; i < mCrucibleCount; i++) {
            mCrucibles[i].resume();
        }

        SPModel.getInstance(this).addObserver(this);
        SPModel.getInstance(this).addConnectionObserver(this);
        SPModel.getInstance(this).startManualMode();

        mActive = true;
    }

    @Override
    protected void onPause() {
        mActive = false;

        for (int i = 0; i < mCrucibleCount; i++) {
            mCrucibles[i].pause();
        }

        SPModel.getInstance(this).deleteObserver(this);
        SPModel.getInstance(this).removeConnectionObserver(this);
        SPModel.getInstance(this).stopManualMode();

        super.onPause();
    }

    private Context getContext() {
        return this;
    }

    @Override
    public void update(Observable observable, Object data) {
        final SPSwitchboardActivity thisAct = this;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!mActive) {
                    return;
                }

                SPModel model = SPModel.getInstance(thisAct);

                if (model.isBoilerHeating()) {
                    mBoilerHeatingElementBtn.setBackgroundResource(R.drawable.crucible_9_patch_glow);
                } else {
                    mBoilerHeatingElementBtn.setBackgroundResource(R.drawable.crucible_9_patch);
                }

                String boilerTempText = "" + (int) Math.floor(SPServiceThermistor.convertFromTempToTemp(SPTempUnitType.KELVIN, model.getTempUnits(), model.getBoilerCurrentTemp()));
                mBoilerTempLabel.setText(boilerTempText);


//				if (model.isBoilerFilling()) {
//					mBoilerFillIndicator.setBackgroundResource(R.drawable.crucible_9_patch_glow);
//				} else {
//					mBoilerFillIndicator.setBackgroundResource(R.drawable.crucible_9_patch);
//				}
            }
        });
    }

    public void notifyOfConnectionStatus() {
        final SPActivity me = this;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                SPLog.debug("got connection notification");
                if (!mActive) return;

                SPModel model = SPModel.getInstance(me);

                if (model.isConnectedToIOIO()) {
                    mConnectivityIndicator.setVisibility(View.VISIBLE);
                    mNoConnectivityIndicator.setVisibility(View.GONE);
                } else {
                    mConnectivityIndicator.setVisibility(View.GONE);
                    mNoConnectivityIndicator.setVisibility(View.VISIBLE);
                }
            }
        });
    }
}
