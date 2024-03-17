package com.alphadominche.steampunkhmi;

import android.os.Bundle;
import android.view.Menu;
import android.view.Window;

public class SPCleaningCycleActivity extends SPActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        SPCleaningCycleFragment cleaningFragment = new SPCleaningCycleFragment();
        cleaningFragment.setArguments(getIntent().getExtras());
        getFragmentManager().beginTransaction().add(android.R.id.content, cleaningFragment).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void onPause() {
        super.onPause();
    }

}
