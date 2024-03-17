/**
 *
 */
package com.alphadominche.steampunkhmi;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.alphadominche.steampunkhmi.restclient.persistenceservicehelper.DefaultPersistenceServiceHelper;
import com.alphadominche.steampunkhmi.restclient.persistenceservicehelper.DefaultPersistenceServiceHelperEvents;
import com.alphadominche.steampunkhmi.restclient.presistenceservice.PersistenceService;
import com.alphadominche.steampunkhmi.utils.MachineSettings;
import de.greenrobot.event.EventBus;

/**
 * @author guy
 */
public class SPLoginFragment extends SPFragment {

    public static String PATH_TO_SERVICE = "com.alphadominche.steampunkhmi.SPUpdateService";
    private Button mSubmitButton;
    private EditText mUsername;
    private EditText mPassword;
    private String mErrorMessage;
    private ProgressDialog mDialog;
    private View mResetButton;

    MachineSettings mMachine;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.sp_login, container, false);
        mUsername = (EditText) rootView.findViewById(R.id.login_username);
        mPassword = (EditText) rootView.findViewById(R.id.login_password);
        mSubmitButton = (Button) rootView.findViewById(R.id.login_button);
        mSubmitButton.setOnClickListener(this);

        mResetButton = rootView.findViewById(R.id.reset_button);
        mResetButton.setOnClickListener(this);

        mMachine = MachineSettings.getMachineSettingsFromSharedPreferences(getActivity());

        return rootView;
    }

    @Override
    public void onResume() {
        EventBus.getDefault().register(this);
        super.onResume();
    }

    @Override
    public void onPause() {
        EventBus.getDefault().unregister(this);
        super.onPause();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login_button:
                mSubmitButton.setEnabled(false);
                mSubmitButton.setClickable(false);
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(
                        Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mPassword.getWindowToken(), 0);

                DefaultPersistenceServiceHelper.getInstance(
                        getActivity().getApplicationContext()).login(
                        mUsername.getEditableText().toString(),
                        mPassword.getEditableText().toString());

                mDialog = new ProgressDialog(getActivity());
                mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                mDialog.setMessage(getActivity().getResources().getString(R.string.connect_to_network_prompt));
                mDialog.setCancelable(false);
                mDialog.show();

                break;
            case R.id.reset_button:
                //send command to reset password
                SPPasswordResetModalFragment frag = new SPPasswordResetModalFragment();
                frag.show(getFragmentManager(), "PW_RESET");

                break;
            default:
                break;
        }
    }

    public void enableSubmitButton() {
        mSubmitButton.setEnabled(true);
        mSubmitButton.setClickable(true);

    }

    public void onEvent(DefaultPersistenceServiceHelperEvents.NetworkError networkCheck) {
        enableSubmitButton();
    }

    public void onEvent(
            DefaultPersistenceServiceHelperEvents.LoginEvent loginEvent) {
        if (loginEvent.wasSuccessful()) {
            mDialog.dismiss();

            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    enableSubmitButton();
                }
            });

            getOrCreateDeviceId();

            startUpdateService();

            Intent intent = new Intent();
            intent.setClass(getActivity(), SPCruciblesActivity.class);
            startActivity(intent);
        } else {
            mDialog.dismiss();
            mErrorMessage = loginEvent.getErrorMessage();
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.login_failed) + mErrorMessage, Toast.LENGTH_LONG).show();
                    enableSubmitButton();
                    mErrorMessage = "";
//					mResetButton.setVisibility(View.VISIBLE);
                }
            });


        }
    }

    // First time logging in on device, create device on server
    private void getOrCreateDeviceId() {
        if (mMachine.getDeviceFromSharedPrefs(getActivity()) == -1) {
            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    DefaultPersistenceServiceHelper.getInstance(
                            getActivity()).createDeviceId();
                }
            });
        }
    }

    private void startUpdateService() {
        boolean isLocalOnly = mMachine.isLocalOnly();
        Intent localOnlyModeIntent = new Intent(isLocalOnly ? PersistenceService.ACTION_DISABLE_NETWORK_CONNECTION : PersistenceService.ACTION_ENABLE_NETWORK_CONNECTION);
        getActivity().startService(localOnlyModeIntent);
        if (isLocalOnly) return; //don't start update request if networking has been disabled!
        Intent SPUpdateServiceIntent = new Intent(getActivity(), SPUpdateService.class);
        getActivity().startService(SPUpdateServiceIntent);
    }

}
