package com.alphadominche.steampunkhmi;

import android.os.Bundle;
import android.view.Menu;
import android.view.Window;


public class SPCleaningCycleSettingsActivity extends SPActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		SPCleaningCycleSettingsFragment settings = new SPCleaningCycleSettingsFragment();
		settings.setArguments(getIntent().getExtras());
		getFragmentManager().beginTransaction().add(android.R.id.content, settings).commit();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.home, menu);
		return true;
	}
}
