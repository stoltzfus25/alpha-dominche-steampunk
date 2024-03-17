package com.alphadominche.steampunkhmi;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import com.alphadominche.steampunkhmi.restclient.persistenceservicehelper.DefaultPersistenceServiceHelper;

public class SPLoginActivity extends SPActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (true == DefaultPersistenceServiceHelper.getInstance(this.getBaseContext()).isLoggedIn()) {
            Intent intent = new Intent();
            intent.setClass(this, SPCruciblesActivity.class);
            startActivity(intent);
        }

        if (savedInstanceState == null) {
            requestWindowFeature(Window.FEATURE_NO_TITLE);

            // During initial setup, plug in the details fragment.
            SPLoginFragment fragment = new SPLoginFragment();
            fragment.setArguments(getIntent().getExtras());
            getFragmentManager().beginTransaction().add(android.R.id.content, fragment).commit();
        }

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
    }

}
