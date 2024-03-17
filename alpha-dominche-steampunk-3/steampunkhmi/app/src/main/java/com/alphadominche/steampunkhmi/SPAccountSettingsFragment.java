package com.alphadominche.steampunkhmi;

import java.util.Arrays;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import com.alphadominche.steampunkhmi.restclient.persistenceservicehelper.DefaultPersistenceServiceHelper;
import com.alphadominche.steampunkhmi.utils.AccountSettings;

/**
 * @author zack
 */

public class SPAccountSettingsFragment extends SPFragment {
    public static final int COUNTRY_CODE_START_INDEX_OFFSET = 3;
    public static final int COUNTRY_CODE_STOP_INDEX_OFFSET = 1;
    private static final int ALERT_WINDOW_WIDTH = 500;
    private static final int ALERT_WINDOW_HEIGHT = 400;

    private ImageView mSaveButton;
    private ImageView mCancelButton;

    private String mUsername;
    private EditText mUsernameView;
    private String mEmail;
    private EditText mEmailView;
    //TODO Allow password changes
//	private String mPassword;
//	private EditText mPasswordView;
//	private String mNewPassword;
//	private EditText mNewPasswordView;
//	private String mConfirmPassword;
//	private EditText mConfirmPasswordView;
    private String mAddress;
    private EditText mAddressView;
    private String mCity;
    private EditText mCityView;
    private String mState;
    private EditText mStateView;
    private String mCountry;
    private Spinner mCountryView;
    private String mZip;
    private EditText mZipView;
    private Boolean mProtectRecipes;
    private CheckBox mProtectRecipesCheckbox;
    private LinearLayout mProtectRecipesView;

    private LinearLayout mUpdatesView;
    private LinearLayout mPINView;
    private LinearLayout mChangePasswordView;

    String[] countries;

    public void getPersistedSettings() {
        AccountSettings shared = AccountSettings
                .getAccountSettingsFromSharedPreferences(getActivity()
                        .getApplicationContext());
        mUsername = shared.getUsername() != null ? shared.getUsername() : "";
        mEmail = shared.getEmail() != null ? shared.getEmail() : "";
        mAddress = shared.getAddress() != null ? shared.getAddress() : "";
        mCity = shared.getCity() != null ? shared.getCity() : "";
        mState = shared.getState() != null ? shared.getState() : "";
        mCountry = shared.getCountry() != null ? shared.getCountry() : "";
        mZip = shared.getZipCode() != null ? shared.getZipCode() : "";
        mProtectRecipes = shared.getProtectRecipes() != null ? shared.getProtectRecipes() : true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.sp_account_settings,
                container, false);

        countries = getResources().getStringArray(R.array.countries);

        mSaveButton = (ImageView) rootView.findViewById(R.id.save_button);
        mSaveButton.setOnClickListener(this);
        mCancelButton = (ImageView) rootView.findViewById(R.id.cancel_button);
        mCancelButton.setOnClickListener(this);

        mUpdatesView = (LinearLayout) rootView.findViewById(R.id.account_settings_updates);
        mUpdatesView.setOnClickListener(this);
        mPINView = (LinearLayout) rootView.findViewById(R.id.account_settings_change_pin);
        mPINView.setOnClickListener(this);
        mChangePasswordView = (LinearLayout) rootView.findViewById(R.id.account_settings_change_password);
        mChangePasswordView.setOnClickListener(this);
        mProtectRecipesView = (LinearLayout) rootView
                .findViewById(R.id.account_settings_protect_recipes);
        mProtectRecipesView.setOnClickListener(this);

        getPersistedSettings();

        mUsernameView = (EditText) rootView
                .findViewById(R.id.account_settings_name_input);
        mEmailView = (EditText) rootView
                .findViewById(R.id.account_settings_email_input);
        //TODO Allow password changes
//		mPasswordView = (EditText) rootView
//				.findViewById(R.id.account_settings_password_input);
//		mNewPasswordView = (EditText) rootView
//				.findViewById(R.id.account_settings_new_password_input);
//		mConfirmPasswordView = (EditText) rootView
//				.findViewById(R.id.account_settings_confirm_password_input);
        mAddressView = (EditText) rootView
                .findViewById(R.id.account_settings_address_input);
        mCityView = (EditText) rootView
                .findViewById(R.id.account_settings_city_input);
        mStateView = (EditText) rootView
                .findViewById(R.id.account_settings_state_input);
        mCountryView = (Spinner) rootView
                .findViewById(R.id.account_settings_country_spinner);
        mZipView = (EditText) rootView
                .findViewById(R.id.account_settings_zip_input);
        mProtectRecipesCheckbox = (CheckBox) rootView
                .findViewById(R.id.account_settings_protect_recipes_checkbox);

        // Countries Spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                R.layout.country_spinner_text_vew, Arrays.asList(countries));
        mCountryView.setAdapter(adapter);
        mCountryView.setSelection(getCountryPos());

        OnItemSelectedListener selectionListener = new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> spinner, View selection,
                                       int position, long id) {

                if (spinner == mCountryView) {
                    String country = countries[position];
                    int l = country.length();
                    mCountry = country.substring(l - COUNTRY_CODE_START_INDEX_OFFSET, l - COUNTRY_CODE_STOP_INDEX_OFFSET).toLowerCase();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> spinner) {
            }
        };
        mCountryView.setOnItemSelectedListener(selectionListener);

        mUsernameView.setText(mUsername);
        mEmailView.setText(mEmail);
        mAddressView.setText(mAddress);
        mCityView.setText(mCity);
        mStateView.setText(mState);
        mZipView.setText(mZip);
        mProtectRecipesCheckbox.setChecked(mProtectRecipes);
        //TODO Allow password changes
//		mPasswordView.setText(mPassword);

        OnFocusChangeListener onFocusListener = new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    mUsernameView.setBackgroundResource(R.drawable.text_field_border);
                    mEmailView.setBackgroundResource(R.drawable.text_field_border);
                    mAddressView.setBackgroundResource(R.drawable.text_field_border);
                    mCityView.setBackgroundResource(R.drawable.text_field_border);
                    mStateView.setBackgroundResource(R.drawable.text_field_border);
                    mCountryView.setBackgroundResource(R.drawable.text_field_border);
                    mZipView.setBackgroundResource(R.drawable.text_field_border);
                    v.setBackgroundResource(R.drawable.submit_button_border);
                }
            }

        };

        mUsernameView.setOnFocusChangeListener(onFocusListener);
        mEmailView.setOnFocusChangeListener(onFocusListener);
        mAddressView.setOnFocusChangeListener(onFocusListener);
        mCityView.setOnFocusChangeListener(onFocusListener);
        mStateView.setOnFocusChangeListener(onFocusListener);
        mCountryView.setOnFocusChangeListener(onFocusListener);
        mZipView.setOnFocusChangeListener(onFocusListener);

        return rootView;
    }

    private void getSettingsFromView() {
        mUsername = mUsernameView.getText().toString();
        //TODO Allow password changes
//		mPassword = mPasswordView.getText().toString();
//		mNewPassword = mNewPasswordView.getText().toString();
//		mConfirmPassword = mConfirmPasswordView.getText().toString();
        mEmail = mEmailView.getText().toString();
        mAddress = mAddressView.getText().toString();
        mCity = mCityView.getText().toString();
        mState = mStateView.getText().toString();
        mZip = mZipView.getText().toString();
        mProtectRecipes = mProtectRecipesCheckbox.isChecked();
    }

    // OnClickListener
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.save_button) {

            getSettingsFromView();

            //TODO Allow password changes
//			if (mNewPassword.length() > 0) {
//				if (mNewPassword.equals(mConfirmPassword)) {
//					mPassword = mNewPassword;
//				} else {
//					alertPasswordError(view);
//					break;
//				}
//			}

            DefaultPersistenceServiceHelper serviceHelper = DefaultPersistenceServiceHelper
                    .getInstance(getActivity().getApplicationContext());
            serviceHelper.saveAccountSettings(mUsername, mEmail, mAddress, mCity, mState,
                    mCountry, mZip, mProtectRecipes);

            this.getActivity().finish();
        } else if (view.getId() == R.id.cancel_button) {
            this.getActivity().finish();
        } else if (view.getId() == R.id.account_settings_change_pin) {
            changePIN();
        } else if (view.getId() == R.id.account_settings_change_password) {
            changePassword();
        } else if (view.getId() == R.id.account_settings_updates) {
            checkForUpdates();
        } else if (view.getId() == R.id.account_settings_protect_recipes) {
            boolean checked = mProtectRecipesCheckbox.isChecked();
            mProtectRecipesCheckbox.setChecked(checked);
        }
    }


    private int getCountryPos() {
        for (int i = 0; i < countries.length; i++) {
            int l = countries[i].length();
            String country = countries[i].substring(l - COUNTRY_CODE_START_INDEX_OFFSET, l - COUNTRY_CODE_STOP_INDEX_OFFSET).toLowerCase();
            if (mCountry.equals(country)) {
                return i;
            }
        }
        return -1;
    }

    private void checkForUpdates() {
        DefaultPersistenceServiceHelper.getInstance(getActivity())
                .checkForUpdates();
    }

    private void changePIN() {
        SPEnterPinNumberFragment pinFragment = new SPEnterPinNumberFragment();
        pinFragment.changePIN();
        pinFragment.show(getFragmentManager(), "PinModal");
    }

    private void changePassword() {
        SPChangePasswordFragment passFragment = new SPChangePasswordFragment();
        passFragment.show(getFragmentManager(), "PassModal");
    }


    private void alertPasswordError(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        builder.setMessage(this.getActivity().getResources().getString(R.string.password_mismatch_notification));
        builder.setCancelable(false);
        builder.setPositiveButton(this.getActivity().getResources().getString(R.string.ok), null);

        AlertDialog alert = builder.create();
        alert.show();
        alert.getWindow().setLayout(ALERT_WINDOW_WIDTH, ALERT_WINDOW_HEIGHT);

        TextView text = (TextView) alert.findViewById(android.R.id.message);
        text.setGravity(Gravity.CENTER);
    }
}
