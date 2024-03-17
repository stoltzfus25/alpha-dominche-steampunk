package com.alphadominche.steampunkhmi;

import android.os.Bundle;
import android.view.Window;

public class SPMyRecipesActivity extends SPActivity {
	SPMyRecipesFragment mFragment;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	  
		if (savedInstanceState == null) {
			requestWindowFeature(Window.FEATURE_NO_TITLE);
			mFragment = new SPMyRecipesFragment();
			mFragment.setArguments(getIntent().getExtras());
			getFragmentManager().beginTransaction().add(android.R.id.content, mFragment).commit();
		}
	}
	
//	@Override
//	public void onPause() {
//		super.onPause();
//	}
//	
//	@Override
//	public void onResume() {
////		mFragment.onResume();
//		super.onRestart();
//	}
}
