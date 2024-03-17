package com.alphadominche.steampunkhmi;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class SPNetworkErrorMessage extends DialogFragment {
	private String mTitle;
	
	public SPNetworkErrorMessage() {}
	
	public void setTitle(String title){
		mTitle = title;
	}
	
	private TextView mTitleLabel;
	private SPModalDismissListener mListener;
	private View mView;
	private View mAcceptButton;
	private AlertDialog mDialog;
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		LayoutInflater inflater = getActivity().getLayoutInflater();
		mView = inflater.inflate(R.layout.sp_network_error_modal, null);
		builder.setView(mView);
		mAcceptButton=mView.findViewById(R.id.sp_network_error_accept_btn);
		mTitleLabel = (TextView) mView.findViewById(R.id.sp_network_error_title_label);
		mTitleLabel.setText(mTitle);
		
		OnClickListener clickListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (v == mAcceptButton) {
					if (mListener != null) {
						mListener.onConfirm();
					}
				} 
			}
		};
		
		mAcceptButton.setOnClickListener(clickListener);

		if (mListener == null) {
			setSPModalDismissListener(new SPModalDismissListener() {
				@Override
				public void onCancel() {
					mDialog.dismiss();
				}
				@Override
				public void onConfirm() { 
					mDialog.dismiss();
				
				}	
			});
		}
		
		mDialog = builder.create();
		mDialog.setCanceledOnTouchOutside(false);
		return mDialog;
	}
	
	protected void setSPModalDismissListener(SPModalDismissListener listener) {
		mListener = listener;
	}
}
