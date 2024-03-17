package com.alphadominche.steampunkhmi;

import android.os.Bundle;
import android.view.Window;

public class SPAccountSettingsActivity extends SPActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            SPAccountSettingsFragment accountFragment = new SPAccountSettingsFragment();
            accountFragment.setArguments(getIntent().getExtras());
            getFragmentManager().beginTransaction().add(android.R.id.content, accountFragment).commit();
            SPEnterPinNumberFragment pinFragment = new SPEnterPinNumberFragment();
            pinFragment.setCancelable(false);
            pinFragment.show(getFragmentManager(), "PinModal");
        }
    }

}
