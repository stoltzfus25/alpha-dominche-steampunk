package com.alphadominche.steampunkhmi;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import com.alphadominche.steampunkhmi.restclient.persistenceservicehelper.DefaultPersistenceServiceHelper;
//import android.view.WindowManager;

public class SPMainMenu extends Fragment {
    public static final int MAIN_MENU_LAYOUT_OFFSET = 320;
    public static final int ANIMATION_FRAME_DELAY = 33;
    private LinearLayout mCruciblesBtn;
    private LinearLayout mLibraryBtn;
    private LinearLayout mFavoritesBtn;
    private LinearLayout mCoffeeRecipeBtn;
    private LinearLayout mTeaRecipeBtn;
    private LinearLayout mMachineSettingsBtn;
    private LinearLayout mMyRecipesBtn;
    private LinearLayout mCleaningCycleBtn;
    private LinearLayout mCheckFoUpdatesBtn;
    private LinearLayout mSwitchboardBtn;
    private LinearLayout mTermsBtn;
    private LinearLayout mLogoutBtn;
    private LinearLayout mForceStopBtn;
    private ProgressDialog mDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        mDialog = new ProgressDialog(getActivity());
        mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mDialog.setMessage(getActivity().getResources().getString(R.string.please_wait));
        mDialog.setCancelable(false);

        View rootView = inflater.inflate(R.layout.activity_spmain_menu, container, false);
        mLibraryBtn = (LinearLayout) rootView.findViewById(R.id.main_menu_library_button);
        mMyRecipesBtn = (LinearLayout) rootView.findViewById(R.id.main_menu_my_recipes_button);
        mCruciblesBtn = (LinearLayout) rootView.findViewById(R.id.main_menu_crucibles_button);
        mCoffeeRecipeBtn = (LinearLayout) rootView.findViewById(R.id.main_menu_add_coffee_recipe_button);
        mTeaRecipeBtn = (LinearLayout) rootView.findViewById(R.id.main_menu_add_tea_recipe_button);
        mMachineSettingsBtn = (LinearLayout) rootView.findViewById(R.id.main_menu_machine_settings_button);
        mCleaningCycleBtn = (LinearLayout) rootView.findViewById(R.id.main_menu_cleaning_cycle_button);
        mFavoritesBtn = (LinearLayout) rootView.findViewById(R.id.main_menu_favorites_button);
        mCheckFoUpdatesBtn = (LinearLayout) rootView.findViewById(R.id.main_menu_check_for_updates_button);
        mSwitchboardBtn = (LinearLayout) rootView.findViewById(R.id.switchboard_button);
        mTermsBtn = (LinearLayout) rootView.findViewById(R.id.main_menu_terms_and_policies_button);
        mLogoutBtn = (LinearLayout) rootView.findViewById(R.id.main_menu_logout_button);
        mForceStopBtn = (LinearLayout) rootView.findViewById(R.id.main_menu_force_stop);

        OnClickListener clickListener = new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                switch (v.getId()) {
                    case R.id.main_menu_check_for_updates_button:
                        intent = null;
                        DefaultPersistenceServiceHelper.getInstance(getActivity()).checkForUpdates();
                        hideMenu();
                        break;
                    case R.id.switchboard_button:
                        intent.setClass(getActivity(), SPSwitchboardActivity.class);
                        break;
                    case R.id.main_menu_add_coffee_recipe_button:
                        SPRecipe coffeeRecipe = SPRecipeDefaults.getNewCoffeeRecipe(SPModel.getInstance(getActivity()).getUser());
                        SPModel.getInstance(getActivity()).setCurrentlyEditedRecipe(coffeeRecipe);
                        intent.setClass(getActivity(), SPRecipeEditorActivity.class);
                        break;
                    case R.id.main_menu_add_tea_recipe_button:
                        SPRecipe teaRecipe = SPRecipeDefaults.getNewTeaRecipe(SPModel.getInstance(getActivity()).getUser());
                        SPModel.getInstance(getActivity()).setCurrentlyEditedRecipe(teaRecipe);
                        intent.setClass(getActivity(), SPRecipeEditorActivity.class);
                        break;
                    case R.id.main_menu_cleaning_cycle_button:
                        intent.setClass(getActivity(), SPCleaningCycleActivity.class);
                        break;
                    case R.id.main_menu_favorites_button:
                        intent.setClass(getActivity(), SPFavoritesActivity.class);
                        break;
                    case R.id.main_menu_crucibles_button:
                        if (getActivity().getClass() != SPCruciblesActivity.class)
                            intent.setClass(getActivity(), SPCruciblesActivity.class);
                        else
                            intent = null;
                        hideMenu();
                        break;
                    case R.id.main_menu_library_button:
                        if (getActivity().getClass() != SPLibraryActivity.class) {
                            intent.setClass(getActivity(), SPLibraryActivity.class);
                        } else {
                            intent = null;
                            ((SPLibraryActivity) getActivity()).hideMenu();
                        }
                        break;
                    case R.id.main_menu_logout_button:
                        new android.app.AlertDialog.Builder(getActivity(), AlertDialog.THEME_HOLO_DARK)
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .setTitle(getActivity().getResources().getString(R.string.logout_dialog_title))
                                .setMessage(getActivity().getResources().getString(R.string.logout_confirm_question))
                                .setPositiveButton(getActivity().getResources().getString(R.string.yes_capitalized), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        stopUpdateService();
                                        logout();
                                    }

                                    public void stopUpdateService() {
                                        Intent SPUpdateServiceIntent = new Intent(getActivity(), SPUpdateService.class);
                                        getActivity().stopService(SPUpdateServiceIntent);
                                    }

                                    private void logout() {
                                        DefaultPersistenceServiceHelper.getInstance(getActivity().getBaseContext()).logout();
                                        Intent intent = new Intent(getActivity(), SPLoginActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                        getActivity().finish();
                                    }

                                })
                                .setNegativeButton(getActivity().getResources().getString(R.string.no_capitalized), null)
                                .show();
                        intent = null;
                        break;
                    case R.id.main_menu_machine_settings_button:
                        intent.setClass(getActivity(), SPMachineSettingsActivity.class);
                        break;
                    case R.id.main_menu_my_recipes_button:
                        intent.setClass(getActivity(), SPMyRecipesActivity.class);
                        break;
                    case R.id.main_menu_terms_and_policies_button:
                        intent.setClass(getActivity(), SPTermsActivity.class);
                        break;
                    case R.id.main_menu_force_stop:
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), AlertDialog.THEME_HOLO_DARK);
                        builder.setTitle(getActivity().getResources().getString(R.string.force_stop_dialog_title));
                        builder.setMessage(getActivity().getResources().getString(R.string.force_stop_confirm_question));
                        builder.setInverseBackgroundForced(true);

                        builder.setPositiveButton(getActivity().getResources().getString(R.string.ok_capitalized), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                Intent clearStackIntent = new Intent(getActivity(), SPCruciblesActivity.class);
                                getActivity().startActivity(clearStackIntent);
                                getActivity().stopService(new Intent(getActivity(), SPUpdateService.class));
                                getActivity().stopService(new Intent(SPIOIOService.START_SPIOIO_SERVICE_INTENT));
                                android.os.Process.killProcess(android.os.Process.myPid());
                                getActivity().finish();
                                getActivity().getParent().finish();
                                System.exit(0);
                            }
                        });

                        builder.setNegativeButton(getActivity().getResources().getString(R.string.cancel_capitalized), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                        Dialog dialog = builder.create();
                        dialog.show();
                        intent = null;

                    default:
                        break;
                }

                if (null != intent) {
                    mDialog.show();
                    hideMenu();
                    startActivity(intent);
                }
            }

        };

        mLibraryBtn.setOnClickListener(clickListener);
        mMyRecipesBtn.setOnClickListener(clickListener);
        mCruciblesBtn.setOnClickListener(clickListener);
        mCoffeeRecipeBtn.setOnClickListener(clickListener);
        mTeaRecipeBtn.setOnClickListener(clickListener);
        mMachineSettingsBtn.setOnClickListener(clickListener);
        mCleaningCycleBtn.setOnClickListener(clickListener);
        mFavoritesBtn.setOnClickListener(clickListener);
        mCheckFoUpdatesBtn.setOnClickListener(clickListener);
        mSwitchboardBtn.setOnClickListener(clickListener);
        mTermsBtn.setOnClickListener(clickListener);
        mLogoutBtn.setOnClickListener(clickListener);
        mForceStopBtn.setOnClickListener(clickListener);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mDialog.hide();
    }

    @Override
    public void onPause() {
        super.onPause();
        mDialog.dismiss();
    }

    public void hideMenu() {
        if (getActivity() instanceof SPMainMenuHaver) {
            ((SPMainMenuHaver) getActivity()).hideMenu();
        }
    }
}
