package com.alphadominche.steampunkhmi;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.alphadominche.steampunkhmi.utils.MachineSettings;

/**
 * @author zack
 */
public class SPCleaningCycleFragment extends SPFragment {

    private int mCrucibleCount;

    private ImageView mCloseButton;
    private ImageView mSettingsButton;

    private ArrayList<CleaningCrucibleController> mControllers;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.sp_cleaning_cycle, container, false);

        mCloseButton = (ImageView) rootView.findViewById(R.id.cleaning_cycle_close_button);
        mCloseButton.setOnClickListener(this);

        mSettingsButton = (ImageView) rootView.findViewById(R.id.cleaning_cycle_settings_button);
        mSettingsButton.setOnClickListener(this);

        Context context = getActivity().getApplicationContext();
        mCrucibleCount = MachineSettings.getMachineCrucibleCount(context) != null ? MachineSettings.getMachineCrucibleCount(context) : 4;

        mControllers = new ArrayList<CleaningCrucibleController>();

        for (int i = 0; i < mCrucibleCount; i++) {
            mControllers.add(new CleaningCrucibleController((SPActivity) getActivity(), i));
        }

        for (int i = mCrucibleCount; i < SPIOIOService.MAX_CRUCIBLE_COUNT; i++) {
            int id = context.getResources().getIdentifier("crucibleLayout" + i, SPActivity.R_ID_STR, context.getPackageName());
            rootView.findViewById(id).setVisibility(View.GONE);
            if (i > 0) {
                id = context.getResources().getIdentifier("verticalDivider" + (i - 1), SPActivity.R_ID_STR, context.getPackageName());
                rootView.findViewById(id).setVisibility(View.GONE);
            }
        }

        return rootView;
    }

    // OnClickListener
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cleaning_cycle_settings_button:
                Intent intent = new Intent();
                intent.setClass(getActivity(), SPCleaningCycleSettingsActivity.class);
                startActivity(intent);
                break;
            case R.id.cleaning_cycle_close_button:
                SPModel model = SPModel.getInstance(getActivity());
                int crucibles = model.getCrucibleCount();
                for (int i = 0; i < crucibles; i++) {
                    SPCrucibleState state = model.getStateForCrucible(i);
                    if (state == SPCrucibleState.CLEANING_FILL_AND_HEAT ||
                            state == SPCrucibleState.CLEANING_DRAIN ||
                            state == SPCrucibleState.CLEANING_AGITATING ||
                            state == SPCrucibleState.WAITING_FOR_CLEANING_RINSE ||
                            state == SPCrucibleState.CLEANING_RINSE_FILL_AND_HEAT ||
                            state == SPCrucibleState.CLEANING_RINSE_DRAIN ||
                            state == SPCrucibleState.CLEANING_RINSE_AGITATING) {
                        model.stopBrewingOnCrucible(i);
                    }
                }
                this.getActivity().finish();
                break;
            default:
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        for (int i = 0; i < mCrucibleCount; i++) {
            mControllers.get(i).resume();
        }
    }

    @Override
    public void onPause() {
        for (int i = 0; i < mCrucibleCount; i++) {
            mControllers.get(i).pause();
        }
        super.onPause();
    }
}
