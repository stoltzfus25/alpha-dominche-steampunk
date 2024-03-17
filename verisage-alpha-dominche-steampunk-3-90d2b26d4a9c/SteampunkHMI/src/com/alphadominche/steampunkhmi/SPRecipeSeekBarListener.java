package com.alphadominche.steampunkhmi;

import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;


public class SPRecipeSeekBarListener implements OnSeekBarChangeListener {
	private SPRecipeEditorActivity view;
	
	SPRecipeSeekBarListener(SPRecipeEditorActivity v) {
		view = v;
	}
	
	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		if (fromUser) {
			view.seekBarProgressChanged(seekBar, progress);
		}
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {}
}
