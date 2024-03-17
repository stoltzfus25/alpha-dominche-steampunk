/**
 * 
 */
package com.alphadominche.steampunkhmi;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


/**
 * @author guy
 *
 */
public class SPTermsFragment extends SPFragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View rootView = inflater.inflate(R.layout.sp_terms_and_policies, container, false);

		mSaveButton = (ImageView) rootView.findViewById(R.id.save_button);
		mSaveButton.setOnClickListener(this);
		mCancelButton = (ImageView) rootView.findViewById(R.id.cancel_button);
		mCancelButton.setOnClickListener(this);
		
		return rootView;
	}
}
