package com.alphadominche.steampunkhmi;

import android.os.Bundle;
import android.view.Window;

public class SPFavoritesActivity extends SPActivity {
	  @Override
	  protected void onCreate(Bundle savedInstanceState) {
		  super.onCreate(savedInstanceState);

		  if (savedInstanceState == null) {
			  requestWindowFeature(Window.FEATURE_NO_TITLE);
			  SPFavoritesFragment fragment = new SPFavoritesFragment();
			  fragment.setArguments(getIntent().getExtras());
			  getFragmentManager().beginTransaction().add(android.R.id.content, fragment).commit();
		  }
	  }
}
