package com.alphadominche.steampunkhmi;

import java.util.Timer;
import java.util.TimerTask;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alphadominche.steampunkhmi.restclient.persistenceservicehelper.DefaultPersistenceServiceHelper;
import com.alphadominche.steampunkhmi.restclient.persistenceservicehelper.DefaultPersistenceServiceHelperEvents;
import com.alphadominche.steampunkhmi.utils.Constants;
import com.alphadominche.steampunkhmi.utils.MachineSettings;
import com.alphadominche.steampunkhmi.utils.SteampunkUtils;

import de.greenrobot.event.EventBus;

public class SPEnterPinNumberFragment extends DialogFragment {
	public static final int INVALID_PIN_MSG_HIDE_DELAY = 1500;
	
	private boolean changePIN;
	private String newPIN;

	private View rootView;
	private View mInvalidInput;
	private Button mCancelButton;
	private Button mSubmitButton;
	
	private EditText mPIN1;
	private EditText mPIN2;
	private EditText mPIN3;
	private EditText mPIN4;

	private Dialog mDialog;
	
	private TextView mTitle1; // Enter Pin Number
	private TextView mTitle2; // Enter New Pin Number
	private TextView mTitle3; // Enter New Pin Again

	private Context mContext;
	
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// If there is no pin in the database, don't ask for a pin
		SharedPreferences sharedPreferences = SteampunkUtils.getSteampunkSharedPreferences(getActivity());
		if(sharedPreferences.getString(Constants.SP_PIN, null) == null) {
			this.dismiss();
		}
		
		mContext = getActivity();
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		LayoutInflater inflater = getActivity().getLayoutInflater();
		rootView = inflater.inflate(R.layout.sp_pin_modal, null);
		builder.setView(rootView);
		
		mTitle1 = (TextView) rootView.findViewById(R.id.sp_pin_title);
		mTitle2 = (TextView) rootView.findViewById(R.id.sp_pin_title2);
		mTitle3 = (TextView) rootView.findViewById(R.id.sp_pin_title3);
		
		mInvalidInput = (TextView) rootView.findViewById(R.id.sp_pin_invalid_input);
		
		mCancelButton = (Button) rootView.findViewById(R.id.sp_pin_cancel_button);
		mSubmitButton = (Button) rootView.findViewById(R.id.sp_pin_submit_button);
		
		mPIN1 = (EditText) rootView.findViewById(R.id.sp_pin_number1);
		mPIN2 = (EditText) rootView.findViewById(R.id.sp_pin_number2);
		mPIN3 = (EditText) rootView.findViewById(R.id.sp_pin_number3);
		mPIN4 = (EditText) rootView.findViewById(R.id.sp_pin_number4);
		
		if (changePIN) {
			newPIN = "";
			mTitle1.setVisibility(View.INVISIBLE);
			mTitle2.setVisibility(View.VISIBLE);
			mPIN1.setInputType(InputType.TYPE_CLASS_NUMBER);
			mPIN2.setInputType(InputType.TYPE_CLASS_NUMBER);
			mPIN3.setInputType(InputType.TYPE_CLASS_NUMBER);
			mPIN4.setInputType(InputType.TYPE_CLASS_NUMBER);
			EventBus.getDefault().register(this);
		}

		// Automatic focus change to next input
		mPIN1.addTextChangedListener(getTextWatcher());
		mPIN2.addTextChangedListener(getTextWatcher());
		mPIN3.addTextChangedListener(getTextWatcher());
		mPIN4.addTextChangedListener(getTextWatcher());
		
		// Outline input with focus
		mPIN1.setOnFocusChangeListener(getFocusChangeListener());
		mPIN2.setOnFocusChangeListener(getFocusChangeListener());
		mPIN3.setOnFocusChangeListener(getFocusChangeListener());
		mPIN4.setOnFocusChangeListener(getFocusChangeListener());

		mPIN1.requestFocus();
		mPIN1.setBackgroundResource(R.drawable.submit_button_border);
		
		mCancelButton.setOnClickListener(getOnClickListener());
		mSubmitButton.setOnClickListener(getOnClickListener());
		
		mTitle1.setPaintFlags(mTitle1.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
		mTitle2.setPaintFlags(mTitle2.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
		mTitle3.setPaintFlags(mTitle3.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

		mDialog = builder.create();
		mDialog.setCanceledOnTouchOutside(false);

		return mDialog;
	}
	
	// This is for reuse of the fragment
	public void changePIN() {
		changePIN = true;
	}
	
	private void resetPinFields() {
		mPIN1.requestFocus();
		mPIN1.setText("");
		mPIN2.setText("");
		mPIN3.setText("");
		mPIN4.setText("");
	}
	
	private void changePinHandler(String PIN) {
		if (newPIN.isEmpty()) {
			// First time entered
			newPIN = PIN;
			mTitle2.setVisibility(View.INVISIBLE);
			mTitle3.setVisibility(View.VISIBLE);
			resetPinFields();
		} else {
			// Second time entered
			if (newPIN.equals(PIN)) {
				DefaultPersistenceServiceHelper.getInstance(mContext).resetPin(PIN);
			} else {
				String message = mContext.getResources().getString(R.string.pin_mismatch_message);
				Toast toast = Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL, 0, 0);
				toast.show();
				mTitle3.setVisibility(View.INVISIBLE);
				mTitle2.setVisibility(View.VISIBLE);
				resetPinFields();
			}
			newPIN = "";
		}
	}
	
	// Set focus to next on number entered
	private TextWatcher getTextWatcher() {
		return new TextWatcher() {
			@Override
			public void afterTextChanged(Editable text) {
				if (text.length() == 1) {
					View current = rootView.findFocus();
					if (current.getNextFocusDownId() != -1) {
						View next = rootView.findViewById(current.getNextFocusDownId());
						next.requestFocus();
					} else {
						mSubmitButton.callOnClick();
					}
				}
			}

			@Override
			public void beforeTextChanged(CharSequence arg0,
					int arg1, int arg2, int arg3){}
			@Override
			public void onTextChanged(CharSequence arg0,
					int arg1, int arg2, int arg3) {}
		};
	}
	
	// Highlight box of currently in focus view
	private OnFocusChangeListener getFocusChangeListener() {
		return new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View focused, boolean hasFocus) {
				if (hasFocus) {
					// Remove outline from all inputs
					mPIN1.setBackgroundResource(R.drawable.text_field_border);
					mPIN2.setBackgroundResource(R.drawable.text_field_border);
					mPIN3.setBackgroundResource(R.drawable.text_field_border);
					mPIN4.setBackgroundResource(R.drawable.text_field_border);
					// Set outline on focused input
					focused.setBackgroundResource(R.drawable.submit_button_border);
				} 
			}
		};
	}
	
	private OnClickListener getOnClickListener() {
		return new OnClickListener() {
			@Override
			public void onClick(View view) {
				if (view == mSubmitButton) {
					String PIN = mPIN1.getText().toString();
					PIN += mPIN2.getText().toString();
					PIN += mPIN3.getText().toString();
					PIN += mPIN4.getText().toString();
					if (PIN.length() < 4){
						invalidPin();
						return;
					}
					
					try {
						// This verifies that they are numeric
						Character.getNumericValue(PIN.charAt(0));
						Character.getNumericValue(PIN.charAt(1));
						Character.getNumericValue(PIN.charAt(2));
						Character.getNumericValue(PIN.charAt(3));
						
						// Changing or validating entered PIN
						if (changePIN) {
							changePinHandler(PIN);
						} else {
							if (MachineSettings.validatePIN(mContext, PIN)) {
								mDialog.dismiss();
							} else {
								invalidPin();
							}
						}
					}
					catch (NumberFormatException e) {
						invalidPin();
					}
				} else if (view == mCancelButton) {
					if (changePIN) {
				        mDialog.dismiss();
					} else {
						getActivity().finish();
					}
				}
			}
		};
	}
	
	// Hide PIN inputs, display invalid PIN message
	private void invalidPin() {
		mInvalidInput.setVisibility(View.VISIBLE);
		mPIN1.setVisibility(View.INVISIBLE);
		mPIN2.setVisibility(View.INVISIBLE);
		mPIN3.setVisibility(View.INVISIBLE);
		mPIN4.setVisibility(View.INVISIBLE);
		
		Timer timer = new Timer(false);
		timer.schedule(new TimerTask() {
		    @Override
		    public void run() {
		       getActivity().runOnUiThread(new Runnable() {
		            public void run() {
		                mInvalidInput.setVisibility(View.INVISIBLE);
		                mPIN1.setVisibility(View.VISIBLE);
						mPIN2.setVisibility(View.VISIBLE);
						mPIN3.setVisibility(View.VISIBLE);
						mPIN4.setVisibility(View.VISIBLE);
						mPIN1.setText("");
						mPIN2.setText("");
						mPIN3.setText("");
						mPIN4.setText("");
		            }
		        });
		    }
		}, INVALID_PIN_MSG_HIDE_DELAY);
	}
	
	public void onEvent(DefaultPersistenceServiceHelperEvents.UpdatedPIN pin) {
		if (pin.successful) {
			String message = mContext.getResources().getString(R.string.update_pin_success_message) + pin.PIN;
			
			Toast toast = Toast.makeText(getActivity(), message, Toast.LENGTH_LONG);
			toast.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL, 0, 0);
			toast.show();
			
			EventBus.getDefault().unregister(this);
			mDialog.dismiss();
		} else {
			String message = mContext.getResources().getString(R.string.update_pin_failure_message);
			
			Toast toast = Toast.makeText(getActivity(), message, Toast.LENGTH_LONG);
			toast.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL, 0, 0);
			toast.show();
			
			resetPinFields();
		}
	}

}
