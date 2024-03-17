package com.alphadominche.steampunkhmi;

import android.os.Bundle;
import android.view.Window;
import com.alphadominche.steampunkhmi.restclient.persistenceservicehelper.DefaultPersistenceServiceHelperEvents;

public class SPMachineSettingsActivity extends SPActivity {
    SPMachineSettingsFragment mMachineSettings = new SPMachineSettingsFragment();
    SPMachineSettingsActivity instance = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            requestWindowFeature(Window.FEATURE_NO_TITLE);

            SPEnterPinNumberFragment pinFragment = new SPEnterPinNumberFragment();
            pinFragment.show(getFragmentManager(), "PinModal");
            pinFragment.setCancelable(false);
            // During initial setup, plug in the details fragment.
            mMachineSettings.setArguments(getIntent().getExtras());
            getFragmentManager().beginTransaction().add(android.R.id.content, mMachineSettings).commit();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    public SPMachineSettingsFragment getFragment() {
        return mMachineSettings;
    }

    @Override
    public void onEvent(DefaultPersistenceServiceHelperEvents.NetworkError networkCheck) {
        final SPNetworkErrorMessage modal = new SPNetworkErrorMessage();

        SPModalDismissListener listener = new SPModalDismissListener() {
            @Override
            public void onCancel() {
                modal.dismiss();
                instance.finish();
            }

            @Override
            public void onConfirm() {
                modal.dismiss();
                instance.finish();
            }
        };

        modal.setTitle(getResources().getString(R.string.network_error_machine_settings));
        modal.setSPModalDismissListener(listener);
        modal.show(getFragmentManager(), "error");
    }
}
