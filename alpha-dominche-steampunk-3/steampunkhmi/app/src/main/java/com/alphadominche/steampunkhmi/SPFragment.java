/**
 *
 */
package com.alphadominche.steampunkhmi;

import android.app.Fragment;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;

/**
 * @author guy
 */
public abstract class SPFragment extends Fragment implements ImageView.OnClickListener, SeekBar.OnSeekBarChangeListener {
    public final static double ONE_DECIMAL_PLACE = 10.0;
    public final static double TWO_DECIMAL_PLACES = 100.0;
    public final static String DEGREE_SYMBOL = "º";

    protected ImageView mSaveButton;
    protected ImageView mCancelButton;


    /**
     * Calculates the progress for a slider control.
     * Assumes that minSlider is zero (which is the case for SeekBar
     * on Android).
     *
     * @param value     - The value from the models.
     * @param maxSlider - Maximum possible for the slider.
     * @param minUnit   - Minimum possible for the units value.
     * @param maxUnit   - Maximum possible for the units value.
     * @return
     */
    protected int calculateProgress(double value, int maxSlider, double minUnit, double maxUnit) {
        int minSlider = 0;
        return (int) Math.round((minSlider + (value - minUnit) * (maxSlider - minSlider) / (maxUnit - minUnit)));
    }

    /**
     * Calculates the new value for a slider control.
     * Assumes that minSlider is zero (which is the case for SeekBar
     * on Android).
     *
     * @param progress  - The new progress value from the slider widget.
     * @param maxSlider - Maximum possible for the slider.
     * @param minUnit   - Minimum possible for the units value.
     * @param maxUnit   - Maximum possible for the units value.
     * @return
     */
    protected double calculateValue(int progress, int maxSlider,
                                    double minUnit, double maxUnit, int precision) {
        int minSlider = 0;
//		return Math.round((minUnit + (progress - minSlider)
//				* (maxUnit - minUnit) / (maxSlider - minSlider)) 
//				* Math.pow(ONE_DECIMAL_PLACE, precision)) / Math.pow(ONE_DECIMAL_PLACE,  precision);
        return Math.round((minUnit + (progress - minSlider)
                * (maxUnit - minUnit) / (maxSlider - minSlider)));
    }

    /* SeekBar */
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress,
                                  boolean fromUser) {
        // TODO flesh-out method stub
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        // TODO Auto-generated method stub

    }

    // OnClickListener
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.save_button:
                // TODO save the settings
            case R.id.cancel_button:
                this.getActivity().finish();
                break;
            default:
                break;
        }
    }

}
