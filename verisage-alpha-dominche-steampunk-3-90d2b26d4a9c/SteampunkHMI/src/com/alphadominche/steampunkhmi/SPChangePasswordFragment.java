package com.alphadominche.steampunkhmi;

import java.util.Timer;
import java.util.TimerTask;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.alphadominche.steampunkhmi.restclient.persistenceservicehelper.DefaultPersistenceServiceHelper;

public class SPChangePasswordFragment extends DialogFragment {
	private static final long ONE_AND_A_HALF_SECONDS_IN_MILLIS = 1500;
	
	private View rootView;
	
	private View mPasswordMissMatch;
	
	private Button mCancelButton;
	private Button mSubmitButton;
	
	private EditText mOldPassword;
	private EditText mNewPassword;
	private EditText mConfirmation;
	
	private Dialog mDialog;
	
	private Context mContext;
	
	
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		mContext = getActivity();
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		LayoutInflater inflater = getActivity().getLayoutInflater();
		rootView = inflater.inflate(R.layout.sp_password_change, null);
		builder.setView(rootView);
		
		
		mPasswordMissMatch = (TextView) rootView.findViewById(R.id.sp_password_change_password_missmatch);
		
		mCancelButton = (Button) rootView.findViewById(R.id.sp_change_password_cancel_button);
		mSubmitButton = (Button) rootView.findViewById(R.id.sp_change_password_submit_button);
		
		mOldPassword = (EditText) rootView.findViewById(R.id.sp_username_change_old_password);
		mNewPassword = (EditText) rootView.findViewById(R.id.sp_username_change_new_password);
		mConfirmation = (EditText) rootView.findViewById(R.id.sp_username_change_confirm_new_password);
		
	
		// Outline input with focus
		mOldPassword.setOnFocusChangeListener(getFocusChangeListener());
		mNewPassword.setOnFocusChangeListener(getFocusChangeListener());
		mConfirmation.setOnFocusChangeListener(getFocusChangeListener());

		mOldPassword.requestFocus();
		mOldPassword.setBackgroundResource(R.drawable.submit_button_border);
		
		mCancelButton.setOnClickListener(getOnClickListener());
		mSubmitButton.setOnClickListener(getOnClickListener());
		
		mDialog = builder.create();
		mDialog.setCanceledOnTouchOutside(false);

		return mDialog;
	}
	
	// Highlight box of currently in focus view
	private OnFocusChangeListener getFocusChangeListener() {
		return new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View focused, boolean hasFocus) {
				if (hasFocus) {
					// Remove outline from all inputs
					mOldPassword.setBackgroundResource(R.drawable.text_field_border);
					mNewPassword.setBackgroundResource(R.drawable.text_field_border);
					mConfirmation.setBackgroundResource(R.drawable.text_field_border);
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
					String pass = mNewPassword.getText().toString();
							
					if (!pass.equals(mConfirmation.getText().toString())) {
						invalidPass();
						return;
					} else {
						//this is where we run with the password change submission
						DefaultPersistenceServiceHelper serviceHelper = DefaultPersistenceServiceHelper.getInstance(mContext);
						serviceHelper.changePassword(mOldPassword.getText().toString(),pass);
						mDialog.dismiss();
					}
				} else if (view == mCancelButton) {
				    mDialog.dismiss();
				}
			}
		};
	}
	
	// Hide Password inputs, display password missmatch message
	private void invalidPass() {
		mPasswordMissMatch.setVisibility(View.VISIBLE);
		mOldPassword.setVisibility(View.INVISIBLE);
		mNewPassword.setVisibility(View.INVISIBLE);
		mConfirmation.setVisibility(View.INVISIBLE);
		Timer timer = new Timer(false);
		timer.schedule(new TimerTask() {
		    @Override
		    public void run() {
		       getActivity().runOnUiThread(new Runnable() {
		            public void run() {
		            	mPasswordMissMatch.setVisibility(View.INVISIBLE);
		            	mOldPassword.setVisibility(View.VISIBLE);
		            	mNewPassword.setVisibility(View.VISIBLE);
		            	mConfirmation.setVisibility(View.VISIBLE);
		            	mOldPassword.setText("");
		            	mNewPassword.setText("");
		            	mConfirmation.setText("");
		            }
		        });
		    }
		}, ONE_AND_A_HALF_SECONDS_IN_MILLIS);
	}
}
