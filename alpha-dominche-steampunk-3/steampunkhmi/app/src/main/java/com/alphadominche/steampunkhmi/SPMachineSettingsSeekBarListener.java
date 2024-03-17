package com.alphadominche.steampunkhmi;

//import android.view.View;

import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;


public class SPMachineSettingsSeekBarListener implements OnSeekBarChangeListener {
    private SPMachineSettingsActivity view;

    SPMachineSettingsSeekBarListener(SPMachineSettingsActivity v) {
        view = v;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser) {
            view.getFragment().seekBarProgressChanged(seekBar, progress);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }

}
