package com.alphadominche.steampunkhmi;

import java.util.Observable;
import java.util.Observer;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.os.BatteryManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import com.alphadominche.steampunkhmi.contentprovider.Provider;
import com.alphadominche.steampunkhmi.database.tables.RecipeTable;
import com.alphadominche.steampunkhmi.restclient.persistenceservicehelper.DefaultPersistenceServiceHelper;
import com.alphadominche.steampunkhmi.utils.Constants;
import com.alphadominche.steampunkhmi.utils.SteampunkUtils;

public class SPCruciblesActivity extends SPActivity implements SPMainMenuHaver, Observer {
    public static final String USB_ACCESSORY_INTENT = "android.hardware.usb.action.USB_ACCESSORY_ATTACHED";
    private HomeCrucibleController[] mCrucibleController;
    private int mCrucibleCount;

    private ImageView mFavoritesButton;
    private ImageView mainMenuBtn;
    private FrameLayout mWrap;
    private FrameLayout mMenu;
    private boolean mShowingMenu;
    Timer menuAnimTimer;
    TimerTask menuAnimTask;
    SPMainMenuInAnim menuAnimIn;
    SPMainMenuOutAnim menuAnimOut;

    boolean mAcceptUpdates;

    private DisplayMetrics metrics;

    private static SPCruciblesActivity instance;

    public static SPCruciblesActivity getInstance() {
        return instance;
    }

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.out.println("got into create: ACTION IS: " + getIntent().getAction());
        mAcceptUpdates = true;
        instance = this;
        mShowingMenu = false;
        super.onCreate(savedInstanceState);

        metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        menuAnimTimer = new Timer();

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_home);

        if (null != getIntent() && null != getIntent().getAction() && getIntent().getAction().equals(USB_ACCESSORY_INTENT)) {
            //SPLog.debug("got started through usb connection");
            Intent serviceIntent = new Intent(SPIOIOService.START_SPIOIO_SERVICE_INTENT);
            serviceIntent.putExtra(SPIOIOService.IOIO_CONNECTED_THROUGH_USB, true);
            startService(serviceIntent);
        } else {
            //SPLog.debug("got started some other way");
        }

        SPModel.getInstance(this).addObserver(this);

        mCrucibleCount = SPIOIOService.MAX_CRUCIBLE_COUNT;

        mCrucibleController = new HomeCrucibleController[SPIOIOService.MAX_CRUCIBLE_COUNT];
        for (int i = 0; i < SPIOIOService.MAX_CRUCIBLE_COUNT; i++) {
            mCrucibleController[i] = new HomeCrucibleController(this, i);
        }

        mainMenuBtn = (ImageView) findViewById(R.id.mainMenuBtn);
        mFavoritesButton = (ImageView) findViewById(R.id.favorites_button);
        mWrap = (FrameLayout) findViewById(R.id.layout_wrap);
        mMenu = (FrameLayout) findViewById(R.id.menu_wrap);

        menuAnimIn = new SPMainMenuInAnim(menuAnimTask, mWrap, metrics.density, this, SPMainMenu.MAIN_MENU_LAYOUT_OFFSET);
        menuAnimOut = new SPMainMenuOutAnim(menuAnimTask, mWrap, metrics.density, this, SPMainMenu.MAIN_MENU_LAYOUT_OFFSET);

        OnClickListener clickListener = new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (v == mainMenuBtn) {
                    if (mShowingMenu) {
                        hideMenu();
                        mMenu.setVisibility(View.GONE);
                    } else {
                        mMenu.setVisibility(View.VISIBLE);
                        showMenu();
                    }
                } else if (v == mFavoritesButton) {
                    Intent intent = new Intent(instance, SPFavoritesActivity.class);
                    startActivity(intent);
                }
            }

        };
        mainMenuBtn.setOnClickListener(clickListener);
        mFavoritesButton.setOnClickListener(clickListener);

        powerConnected(isConnected());
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        update(SPModel.getInstance(this), null);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!mShowingMenu) {
            mMenu.setVisibility(View.GONE);
        }
        for (int i = 0; i < mCrucibleCount; i++) {
            mCrucibleController[i].resume();
        }

        //check for an old version (build 36) which needs significant migration and a forced logout
        SharedPreferences preferences = getApplicationContext().getSharedPreferences(
                Constants.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        try { //if the type of user id is a long, we're in a newer version and everything is ok!
            preferences.getLong(Constants.SP_USER_ID, -1);
        } catch (Exception e) {
            //if the type of user id is NOT a long, but is an int, we're in build 36 and need
            //to force a logout and a removal of the offendingly typed shared prefs
            Editor editor = SteampunkUtils
                    .getSteampunkSharedPreferences(this).edit();
            editor.remove(Constants.SP_USER_ID);
            editor.remove(Constants.SP_AUTH_TOKEN);
            editor.apply();
        }

        Cursor v2RecipeCursor = getContentResolver().query(Provider.RECIPE_CONTENT_URI, new String[]{RecipeTable.STACKS}, RecipeTable.STACKS + " is null or " + RecipeTable.STACKS + "=?", new String[]{""}, null);
        boolean hasV2Recipes = v2RecipeCursor.moveToFirst();
        //moved from above shared prefs and below crucible resume to here to avoid checking for being logged in
        //until after the check for the need for a forced logout due to a migration problem!
        boolean notLoggedIn = !DefaultPersistenceServiceHelper.getInstance(this).isLoggedIn();
        if (notLoggedIn || hasV2Recipes) {
            if (hasV2Recipes) {
                DefaultPersistenceServiceHelper.getInstance(this).logout(); //do logout and force the credentials to be removed so there are no v2 recipes in the database
            }
            Intent loginIntent = new Intent();
            loginIntent.setClass(this, SPLoginActivity.class);
            startActivity(loginIntent);
        } else {
            boolean isLocalOnly = !SPModel.getInstance(this).isConnectedToNetwork();
            if (isLocalOnly) { //SPLog.debug("enabling local mode in crucibles activity!");
                DefaultPersistenceServiceHelper.getInstance(this).disableNetworking();
            } else { //SPLog.debug("disabling local mode in crucibles activity!");
                DefaultPersistenceServiceHelper.getInstance(this).enableNetworking();
            }
            if (isLocalOnly) return; //don't request updates if networking has been disabled
            Intent updateIntent = new Intent();
            updateIntent.setClass(this, SPUpdateService.class);
            startService(updateIntent);
        }
    }


    @Override
    public void onPause() {
        for (int i = 0; i < mCrucibleCount; i++) {
            mCrucibleController[i].pause();
        }
        super.onPause();
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (null != intent.getAction() && intent.getAction().equals(USB_ACCESSORY_INTENT)) {
            //SPLog.debug("got usb connection intent");
            Intent serviceIntent = new Intent(SPIOIOService.START_SPIOIO_SERVICE_INTENT);
            serviceIntent.putExtra(SPIOIOService.IOIO_CONNECTED_THROUGH_USB, true);
            startService(serviceIntent);
        }
    }

    public boolean isConnected() {
        Intent intent = registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        return plugged == BatteryManager.BATTERY_PLUGGED_AC || plugged == BatteryManager.BATTERY_PLUGGED_USB;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    public void setShowingMenu(boolean is) {
        if (mShowingMenu == is) {
            return;
        }

        mShowingMenu = is;
    }

    public void hideMenu() {
        menuAnimIn.reset();
        menuAnimOut.reset();
        menuAnimTask = new TimerTask() {
            public void run() {
                runOnUiThread(menuAnimOut);
            }
        };
        menuAnimOut.setTask(menuAnimTask);
        menuAnimTimer.schedule(menuAnimTask, 0, SPMainMenu.ANIMATION_FRAME_DELAY);
    }

    public void showMenu() {
        menuAnimOut.reset();
        menuAnimIn.reset();
        menuAnimTask = new TimerTask() {
            public void run() {
                runOnUiThread(menuAnimIn);
            }
        };
        menuAnimIn.setTask(menuAnimTask);
        menuAnimTimer.schedule(menuAnimTask, 0, SPMainMenu.ANIMATION_FRAME_DELAY);
    }

    public void powerConnected(final boolean is) {
        runOnUiThread(new Runnable() {
            public void run() {
                if (is) {
                    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                } else {
                    Intent serviceIntent = new Intent("START_SPIOIO_SERVICE");
                    serviceIntent.putExtra(SPIOIOService.STOP_SERVICE_COMMAND, true);
                    stopService(serviceIntent);
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                }
            }
        });
    }

    public void acceptUpdates(boolean does) {
        mAcceptUpdates = does;
    }

    public boolean acceptsUpdates() {
        return mAcceptUpdates;
    }

    @Override
    public void update(Observable observable, Object data) {
        if (observable == SPModel.getInstance(this)) {
            if (mCrucibleCount != SPModel.getInstance(this).getCrucibleCount()) {
                mCrucibleCount = SPModel.getInstance(this).getCrucibleCount();
                for (int i = 0; i < mCrucibleCount; i++) {
                    mCrucibleController[i].show();
                    if (i > 0) {
                        int ID = getResources().getIdentifier("verticalDivider" + (i - 1), SPActivity.R_ID_STR, getPackageName());
                        findViewById(ID).setVisibility(View.VISIBLE);
                    }
                }
                for (int i = mCrucibleCount; i < SPIOIOService.MAX_CRUCIBLE_COUNT; i++) {
                    mCrucibleController[i].hide();
                    if (i > 0) {
                        int ID = getResources().getIdentifier("verticalDivider" + (i - 1), SPActivity.R_ID_STR, getPackageName());
                        findViewById(ID).setVisibility(View.GONE);
                    }
                }
            }
        }
    }


    @Override
    public void needToBlank() {
        if (mShowingMenu) {
            hideMenu();
        }
    }
}
