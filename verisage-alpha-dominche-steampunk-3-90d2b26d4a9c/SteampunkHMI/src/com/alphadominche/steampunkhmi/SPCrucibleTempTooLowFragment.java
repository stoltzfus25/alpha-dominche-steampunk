package com.alphadominche.steampunkhmi;

import java.util.Observable;
import java.util.Observer;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class SPCrucibleTempTooLowFragment extends DialogFragment implements Observer {
	public final static float DIM = 0.3f;
	public final static float BRIGHT = 1f;
	
	private View rootView;
	private Button mOkayButton;

	private Dialog mDialog;
	
	private double mBoilerCurrentTemp;
	private double mBoilerTargetTemp;
	
	private TextView mTitle;
	private TextView mBoilerCurrentTempView;
	private TextView mBoilerTargetTempView;

	private SPModel mModel;
	private Context mContext;
	
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		mContext = getActivity();
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		LayoutInflater inflater = getActivity().getLayoutInflater();
		rootView = inflater.inflate(R.layout.sp_crucible_modal, null);
		builder.setView(rootView);

		mModel = SPModel.getInstance(mContext);
		
		mModel.addObserver(this);
		
		mBoilerCurrentTempView = (TextView) rootView.findViewById(R.id.sp_crucible_modal_current_temp);
		mBoilerTargetTempView = (TextView) rootView.findViewById(R.id.sp_crucible_modal_target_temp);
		mTitle = (TextView) rootView.findViewById(R.id.sp_crucible_modal_title);
		mOkayButton = (Button) rootView.findViewById(R.id.sp_crucible_modal_okay_button);
		
		OnClickListener clickListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (v == mOkayButton) mDialog.dismiss();
			}
		};
		mOkayButton.setOnClickListener(clickListener);

		mTitle.setPaintFlags(mTitle.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
		
		mBoilerCurrentTemp = getAndConvertCurrentTemp();
		mBoilerTargetTemp = mModel.getBoilerTargetTemp();
		
		mBoilerCurrentTempView.setText(Double.toString(mBoilerCurrentTemp));
		mBoilerTargetTempView.setText(Double.toString(mBoilerTargetTemp));

		mDialog = builder.create();
		mDialog.setCanceledOnTouchOutside(false);

		return mDialog;
	}

	@Override
	public void update(Observable arg0, Object arg1) {
		if (getActivity() == null) return;
		getActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				mBoilerCurrentTemp = getAndConvertCurrentTemp();
				mBoilerCurrentTempView.setText(Double.toString(mBoilerCurrentTemp));
				
				double convertedBoilerTarget = SPServiceThermistor.convertFromTempToTemp(mModel.getTempUnits(), SPTempUnitType.KELVIN, mBoilerTargetTemp);
				if (mModel.getBoilerCurrentTemp() >= convertedBoilerTarget - SPServiceBoiler.TEMP_UNDERFLOW_TOLERANCE) {
					mOkayButton.setEnabled(true);
					mOkayButton.setAlpha(BRIGHT);
				}
			}
		});
	}
	
	private double getAndConvertCurrentTemp() {
		return Math.round(SPServiceThermistor.convertFromTempToTemp(
				SPTempUnitType.KELVIN,
				mModel.getTempUnits(),
				mModel.getBoilerCurrentTemp()) * SPFragment.ONE_DECIMAL_PLACE) / SPFragment.ONE_DECIMAL_PLACE;
	}
}
