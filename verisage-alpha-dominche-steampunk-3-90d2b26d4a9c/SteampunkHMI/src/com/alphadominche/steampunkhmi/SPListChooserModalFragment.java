package com.alphadominche.steampunkhmi;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.TextView;

public class SPListChooserModalFragment extends DialogFragment {
	private View mView = null;
	private View mSaveBtn = null;
	private View mCancelBtn = null;
	private TextView mTitleLabel = null;
	private ListView mListView = null;
	private Dialog mDialog = null;
	
	private SPModalDismissListener mListener = null;
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		LayoutInflater inflater = getActivity().getLayoutInflater();
		mView = inflater.inflate(R.layout.sp_list_chooser_modal, null);
		builder.setView(mView);
		
		mSaveBtn = mView.findViewById(R.id.sp_modal_save_btn);
		mCancelBtn = mView.findViewById(R.id.sp_modal_cancel_btn);
		mTitleLabel = (TextView)mView.findViewById(R.id.sp_list_chooser_title_label);
		mListView = (ListView)mView.findViewById(R.id.sp_list_chooser_body);
		
		OnClickListener clickListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (v == mSaveBtn) {
					if (mListener != null) {
						mListener.onConfirm();
					}
				} else if (v == mCancelBtn) {
					if (mListener != null) {
						mListener.onCancel();
					}
				}
			}
		};
		mSaveBtn.setOnClickListener(clickListener);
		mCancelBtn.setOnClickListener(clickListener);
		
		mDialog = builder.create();
		
		return mDialog;
	}
	
	protected void setTitle(String title) {
		mTitleLabel.setText(title);
	}
	
	protected ListView getListView() {
		return mListView;
	}
	
	protected void setSPModalDismissListener(SPModalDismissListener listener) {
		mListener = listener;
	}
}
