package com.alphadominche.steampunkhmi;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;
import com.alphadominche.steampunkhmi.BatteryBroadcastReceiver.LowBatteryLevelListener;
import com.alphadominche.steampunkhmi.SPServiceBoiler.OverheatingListener;
import com.alphadominche.steampunkhmi.SPServiceCrucible.ColdWaterPressureLowListener;
import com.alphadominche.steampunkhmi.contentprovider.Provider;
import com.alphadominche.steampunkhmi.database.tables.PersistenceQueueTable;
import com.alphadominche.steampunkhmi.restclient.persistenceservicehelper.DefaultPersistenceServiceHelper;
import com.alphadominche.steampunkhmi.restclient.persistenceservicehelper.DefaultPersistenceServiceHelperEvents;
import com.alphadominche.steampunkhmi.utils.Constants;
import com.alphadominche.steampunkhmi.utils.MachineSettings;
import de.greenrobot.event.EventBus;

public class SPActivity extends Activity implements LowBatteryLevelListener, OverheatingListener, ColdWaterPressureLowListener {
    public static final long NANOS_BEFORE_SCREEN_BLANKS = 10l * 60l * 1000000000l;
    public static final String R_ID_STR = "id";
    public static final long ONE_SECOND_IN_MILLIS = 1000;
    public static final int ALERT_WINDOW_WIDTH = 600;
    public static final int ALERT_WINDOW_HEIGHT = 400;
    public static final int ALERT_POPUP_PADDING = 35;
    public static final double OVERHEAT_WARNING_OFFSET = .5;
    public static final String DEGREE_SYMBOL = "°";

    private static boolean sAlreadyTriggered = false;
    private static boolean sUpdateTriggered = false;

    private static boolean sInitialized = false;
    private static boolean sBlankingScreen;
    private static boolean sStopTimer;
    private static long sNanosAtLastEvent;
    private static SPActivity sCurrent;
    private static Timer sBlankingTimer;
    private static TimerTask sBlankingTask;

    private Context mContext;

    private BatteryBroadcastReceiver mBatteryBroadcastReceiver;
    private IntentFilter mReceiverFilter;

    private void register(SPActivity activity) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        sCurrent = activity;
    }

    private void unregister(SPActivity activity) {
        if (sCurrent == activity) {
            sCurrent = null;
        }
    }

    protected final void initScreenBlanking() {
        if (sInitialized) return;

        sNanosAtLastEvent = System.nanoTime();
        sBlankingScreen = false;
        sStopTimer = false;
        sCurrent = null;
        sBlankingTimer = new Timer();
        sBlankingTask = new TimerTask() {
            @Override
            public void run() {
                if (!sStopTimer) {
                    sBlankingScreen = ((System.nanoTime() - sNanosAtLastEvent) > NANOS_BEFORE_SCREEN_BLANKS);

                    if (sCurrent != null && sBlankingScreen) {
                        sCurrent.setBlanking();
                    }
                }
            }
        };
        sBlankingTimer.scheduleAtFixedRate(sBlankingTask, new Date(), ONE_SECOND_IN_MILLIS);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        notifyOfEvent();
        return super.dispatchTouchEvent(event);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        notifyOfEvent();
        return super.dispatchKeyEvent(event);
    }

    public final static void notifyOfEvent() {
        sNanosAtLastEvent = System.nanoTime();
    }

    public static void restartTimer() {
        sStopTimer = false;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBatteryBroadcastReceiver = new BatteryBroadcastReceiver();
        mReceiverFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);

        initScreenBlanking();
        mContext = this;
    }

    @Override
    public void onBackPressed() {
        return;
    }

    @Override
    protected void onPause() {
        EventBus.getDefault().unregister(this);
        unregister(this);
        unregisterReceiver(mBatteryBroadcastReceiver);
        mBatteryBroadcastReceiver.unregisterListener();
        SPServiceBoiler.unregisterOverheatingListener();
        SPServiceCrucible.unregisterColdWaterPressureTooLowListener();
        super.onPause();
    }

    @Override
    protected void onResume() {
        EventBus.getDefault().register(this);
        register(this);
        mBatteryBroadcastReceiver.registerListener(this);
        registerReceiver(mBatteryBroadcastReceiver, mReceiverFilter);
        super.onResume();

        SPServiceBoiler.registerOverheatingListener(this);
        SPServiceCrucible.registerColdWaterPressureTooLowListener(this);
    }

    public void onEvent(DefaultPersistenceServiceHelperEvents.NetworkError networkCheck) {
        SPNetworkErrorMessage modal = new SPNetworkErrorMessage();
        modal.setTitle(getResources().getString(R.string.network_error_notification));
        FragmentManager fm = this.getFragmentManager();
        modal.show(fm, "error");
    }

    public void onEvent(
            DefaultPersistenceServiceHelperEvents.ToastMessageEvent ToastMessageEvent) {
        final String mesage = ToastMessageEvent.getMessage();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast toast = Toast.makeText(mContext, mesage, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 0, 0);
                toast.show();
            }
        });
    }

    public void onEvent(DefaultPersistenceServiceHelperEvents.SyncDatabase update) {
        final Activity act = this;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                DefaultPersistenceServiceHelper service = DefaultPersistenceServiceHelper.getInstance(mContext);
                service.syncRecipes();
                service.syncRoasters();

                //if device not registered with EAS, register the device, otherwise, check for software updates
                MachineSettings machine = MachineSettings.getMachineSettingsFromSharedPreferences(act);
                if (machine.getDeviceFromSharedPrefs(act) == -1) {
                    service.createDeviceId();
                } else {
                    service.checkForUpdates();
                }

                service.getMachineSettings();
            }
        });
    }

    public void onEvent(DefaultPersistenceServiceHelperEvents.AvailableUpdate availableUpdate) {

        if ((!sBlankingScreen) && (!sUpdateTriggered)) {

            sUpdateTriggered = true;
            String text = getResources().getString(R.string.update_available_prompt);
            final int newVersionId = availableUpdate.getNewVersionId();
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(text);
            builder.setPositiveButton(getResources().getString(R.string.update), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    sUpdateTriggered = false;
                    DefaultPersistenceServiceHelper d = DefaultPersistenceServiceHelper
                            .getInstance(getApplicationContext());
                    d.downloadUpdate(newVersionId);
                }
            });
            builder.setNegativeButton(getResources().getString(R.string.dismiss), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    sUpdateTriggered = false;
                }
            });

            AlertDialog alert = builder.show();
            alert.getWindow().setLayout(ALERT_WINDOW_WIDTH, ALERT_WINDOW_HEIGHT);
            alert.setCanceledOnTouchOutside(false);
            TextView popup = (TextView) alert.findViewById(android.R.id.message);
            popup.setGravity(Gravity.CENTER);
            popup.setPadding(0, ALERT_POPUP_PADDING, 0, 0);

        }
    }

    public void onEvent(SPServiceBoiler.ReleaseSteamRequest release) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String text = getResources().getString(R.string.steam_needs_releasing);
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setMessage(text);
                builder.setPositiveButton(getResources().getString(R.string.release), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(SPIOIOService.MACHINE_COMMAND_INTENT);
                        intent.putExtra(SPIOIOService.COMMAND, SPIOIOService.RELEASE_STEAM);
                        mContext.startService(intent);
                    }
                });

                AlertDialog alert = builder.show();
                alert.getWindow().setLayout(ALERT_WINDOW_WIDTH, ALERT_WINDOW_HEIGHT);
                alert.setCanceledOnTouchOutside(false);
                TextView popup = (TextView) alert.findViewById(android.R.id.message);
                popup.setGravity(Gravity.CENTER);
                popup.setPadding(0, ALERT_POPUP_PADDING, 0, 0);
            }
        });
    }

    public void onEvent(DefaultPersistenceServiceHelperEvents.Upgrading upgrading) {
        final SPActivity me = this;
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                ProgressDialog dialog = new ProgressDialog(me);
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog.setMessage(getResources().getString(R.string.wait_for_recipe_backup_prompt));
                dialog.setCancelable(false);
                dialog.show();

                boolean keepGoing = true;
                while (keepGoing) {
                    try {
                        Thread.sleep(ONE_SECOND_IN_MILLIS);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Cursor queCursor = getApplicationContext().getContentResolver().query(Provider.PERSISTENCEQUEUE_CONTENT_URI,
                            PersistenceQueueTable.ALL_COLUMNS,
                            PersistenceQueueTable.STATE + "!=?",
                            new String[]{Constants.STATE_OK},
                            null);
                    if (queCursor.getCount() == 0) {
                        keepGoing = false;
                    }
                }
                dialog.dismiss();
            }
        });
    }

    public static boolean ismAlreadyTriggered() {
        return sAlreadyTriggered;
    }

    public static void setmAlreadyTriggered(boolean alreadyTriggered) {
        SPActivity.sAlreadyTriggered = alreadyTriggered;
    }

    protected void setBlanking() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                sStopTimer = true;
                SPBlankingFragment fragment = new SPBlankingFragment();
                fragment.setArguments(getIntent().getExtras());
                getFragmentManager().beginTransaction().add(android.R.id.content, fragment).commit();

                getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
            }
        });
    }

    protected void displayLowBatteryWarning() {
        WarningDialog dialog = new WarningDialog();
        dialog.setText(this.getResources().getString(R.string.battery_warning));
        dialog.show(getFragmentManager(), WarningDialog.TAG);
    }

    /**
     * needToBlank
     * <p/>
     * a way of communicating to descendant classes that the screen will blank
     * this method may be overridden by descendant classes
     */
    public void needToBlank() {
    }

    @Override
    public void batteryLevelLow() {
        displayLowBatteryWarning();
    }

    @Override
    public void overheating() {
        WarningDialog dialog = new WarningDialog();
        dialog.setText(getResources().getString(R.string.overheating_warning));
        dialog.show(getFragmentManager(), WarningDialog.TAG);
    }

    @Override
    public void displayColdWaterPressureLowWarning() {
        WarningDialog dialog = new WarningDialog();
        dialog.setText(getResources().getString(R.string.low_cold_water_pressure));
        dialog.show(getFragmentManager(), WarningDialog.TAG);
    }
}
