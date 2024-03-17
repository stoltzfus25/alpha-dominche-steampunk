/**
 *
 */
package com.alphadominche.steampunkhmi;

import java.util.TreeSet;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.alphadominche.steampunkhmi.SPModel.SaveRecipeListener;
import com.alphadominche.steampunkhmi.contentprovider.Provider;
import com.alphadominche.steampunkhmi.database.tables.FavoriteTable;
import com.alphadominche.steampunkhmi.database.tables.RecipeTable;
import com.alphadominche.steampunkhmi.restclient.persistenceservicehelper.DefaultPersistenceServiceHelper;
import com.alphadominche.steampunkhmi.utils.AccountSettings;
import com.alphadominche.steampunkhmi.utils.Constants;
import com.alphadominche.steampunkhmi.utils.SteampunkUtils;

//import android.app.LoaderManager.LoaderCallbacks;
//import android.content.CursorLoader;
//import android.content.Loader;

/**
 * @author guy
 */
public class SPMyRecipesFragment extends SPFragment implements /*LoaderCallbacks<Cursor>,*/ OnItemLongClickListener, SaveRecipeListener {
    //	private CursorLoader mLoader;
    private SPMyRecipesCursorAdapter mAdapter;
    private SPModel mMyRecipesModel = SPModel.getInstance(getActivity());

    private ImageView mCancelButton;
    private ImageView mSaveButton;
    private ListView mListView;
    private View mDropdown;
    private TreeSet<Long> mfavoriteList;

    private int mPosition;

    View dimmableEditButton;

    public void setupAdapter() {
        long userId = SteampunkUtils.getCurrentSteampunkUserId(getActivity().getApplicationContext());

        Cursor cursor = getActivity().getContentResolver().query(
                Provider.RECIPE_CONTENT_URI,
                RecipeTable.ALL_COLUMNS,
                RecipeTable.STEAMPUNK_USER_ID + "=?",
                new String[]{Long.toString(userId)},
                RecipeTable.NAME + " COLLATE NOCASE");

        mAdapter = new SPMyRecipesCursorAdapter(
                getActivity(),
                R.drawable.my_recipes_list_view, cursor,
                new String[]{RecipeTable.NAME, RecipeTable.TYPE, RecipeTable.ID},
                new int[]{android.R.id.text1},
                mfavoriteList);

        mListView.setAdapter(mAdapter);
    }

    public void editRecipe(int position) {
        String recipeId = mAdapter.getId(position);
        SPRecipe recipe = new SPRecipe(Long.parseLong(recipeId), getActivity());

        mMyRecipesModel.setCurrentlyEditedRecipe(recipe);
        long userId = SteampunkUtils.getCurrentSteampunkUserId(getActivity().getApplicationContext());
        String userType = SteampunkUtils.getCurrentSteampunkUserType(getActivity().getApplicationContext());
        String username = AccountSettings.getAccountSettingsFromSharedPreferences(getActivity()).getUsername();
        SPUser user = new SPUser(userId, username, userType);
        mMyRecipesModel.setUser(user);
        Intent intent = new Intent();
        intent.setClass(getActivity(), SPRecipeEditorActivity.class);
        if (null != intent) {
            startActivity(intent);
        }
    }

    public void duplicateRecipe(int position) {
        String recipeId = mAdapter.getId(position);
        SPRecipe recipe = new SPRecipe(Long.parseLong(recipeId), getActivity());
        recipe.setNewRecipe(true);

        mMyRecipesModel.setCurrentlyEditedRecipe(recipe);
        long userId = SteampunkUtils.getCurrentSteampunkUserId(getActivity().getApplicationContext());
        String userType = SteampunkUtils.getCurrentSteampunkUserType(getActivity().getApplicationContext());
        String username = AccountSettings.getAccountSettingsFromSharedPreferences(getActivity()).getUsername();
        SPUser user = new SPUser(userId, username, userType);
        mMyRecipesModel.setUser(user);
        Intent intent = new Intent();
        intent.setClass(getActivity(), SPRecipeEditorActivity.class);
        if (null != intent) {
            startActivity(intent);
        }

        mAdapter.notifyDataSetChanged();
    }

    public void publishRecipe(final int position) {
        String recipeId = mAdapter.getId(position);
        SPRecipe recipe = new SPRecipe(Long.parseLong(recipeId), getActivity());

        boolean isPublished = recipe.isPublished();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), AlertDialog.THEME_HOLO_DARK);
        builder.setTitle(isPublished ?
                getActivity().getResources().getString(R.string.unpublish_recipe_dialog_title) :
                getActivity().getResources().getString(R.string.publish_recipe_dialog_title));
        String msg = getActivity().getResources().getString(R.string.publish_recipe_confirm_message_first);
        msg += isPublished ?
                getActivity().getResources().getString(R.string.unpublish_label_value) :
                getActivity().getResources().getString(R.string.publish_label_value);
        msg += getActivity().getResources().getString(R.string.publish_recipe_confirm_message_last);
        builder.setMessage(msg);
        builder.setInverseBackgroundForced(true);
        builder.setPositiveButton(getActivity().getResources().getString(R.string.confirm_capitalized), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                String recipeId = mAdapter.getId(position);
                SPRecipe recipe = new SPRecipe(Long.parseLong(recipeId), getActivity());
                recipe.setPublished(!recipe.isPublished());
                recipe.save(getActivity().getApplicationContext());
                dialog.dismiss();
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
    }

    private void favoriteRecipe(int position) {
        long recipeId = Long.parseLong(mAdapter.getId(position));
        SharedPreferences preferences = getActivity().getApplicationContext().getSharedPreferences(
                Constants.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        long userId = preferences.getLong(Constants.SP_USER_ID, -1);
        Cursor favCursor = getActivity().getContentResolver().query(
                Provider.FAVORITE_CONTENT_URI,
                FavoriteTable.ALL_COLUMNS,
                FavoriteTable.RECIPE_ID + "=? AND " + FavoriteTable.USER + "=?",
                new String[]{Long.toString(recipeId),
                        Long.toString(userId)},
                null);
        favCursor.moveToFirst();
        if (favCursor.getCount() > 0) {
            favCursor.close();
            DefaultPersistenceServiceHelper.getInstance(getActivity().getApplicationContext()).deleteFavorite(recipeId);
            mfavoriteList.remove(recipeId);
        } else { //SPLog.debug("saving favorite!");
            favCursor.close();
            DefaultPersistenceServiceHelper.getInstance(getActivity().getApplicationContext()).createFavorite(recipeId);
            mfavoriteList.add(recipeId);
        }

        mAdapter.notifyDataSetChanged();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        dimmableEditButton = null;
        mMyRecipesModel.addSaveRecipeListener(this);
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.sp_my_recipes, container, false);
        mSaveButton = (ImageView) rootView.findViewById(R.id.save_button);
        mSaveButton.setOnClickListener(this);
        mCancelButton = (ImageView) rootView.findViewById(R.id.cancel_button);
        mCancelButton.setOnClickListener(this);
        mfavoriteList = new TreeSet<Long>();
        SharedPreferences preferences = getActivity().getApplicationContext().getSharedPreferences(
                Constants.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        long favUserId = preferences.getLong(Constants.SP_USER_ID, -1);
        Cursor favCursor = getActivity().getContentResolver().query(
                Provider.FAVORITE_CONTENT_URI,
                FavoriteTable.ALL_COLUMNS,
                FavoriteTable.USER + "=?",
                new String[]{Long.toString(favUserId)},
                null);
        favCursor.moveToFirst();

        for (int i = 0; i < favCursor.getCount(); i++) {
            mfavoriteList.add(favCursor.getLong(favCursor.getColumnIndex(FavoriteTable.RECIPE_ID)));
            favCursor.moveToNext();
        }

        TextView username = (TextView) rootView.findViewById(R.id.my_recipes_company_name);
        TextView location = (TextView) rootView.findViewById(R.id.my_recipes_location);
        AccountSettings shared = AccountSettings.getAccountSettingsFromSharedPreferences(getActivity());
        username.setText(shared.getUsername());
        location.setText(shared.getCity() + ", " + shared.getState());

        mListView = (ListView) rootView.findViewById(R.id.my_recipes_list);
        setupAdapter();
        mListView.setOnItemLongClickListener(this);
        mListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, final long id) {

                mPosition = position;
                System.out.println("at position " + position);
                if (mDropdown != null) {
                    mDropdown.setVisibility(View.GONE);
                }
                mDropdown = view.findViewById(R.id.my_recipes_dropdown);
                mDropdown.setVisibility(View.VISIBLE);
                View edit = mDropdown.findViewById(R.id.my_recipes_edit_button);
                if (mMyRecipesModel.stillSavingRecipe() && mMyRecipesModel.getSavingRecipe() == id) {
                    edit.setAlpha(0.5f);
                    dimmableEditButton = edit;
                } else {
                    edit.setAlpha(1.0f);
                    dimmableEditButton = null;
                }
                edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (mMyRecipesModel.stillSavingRecipe() && mMyRecipesModel.getSavingRecipe() == id) return;
                        editRecipe(mPosition);
                    }
                });
                View duplicate = mDropdown.findViewById(R.id.my_recipes_duplicate_button);
                duplicate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        duplicateRecipe(mPosition);
                    }
                });
                View publish = mDropdown.findViewById(R.id.my_recipes_publish_button);
                String type = SteampunkUtils.getCurrentSteampunkUserType(getActivity().getApplicationContext());
                if (type.equals(SteampunkUtils.USER_TYPE_ROASTER) || type.equals(SteampunkUtils.USER_TYPE_ADMIN)) {
                    publish.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            publishRecipe(mPosition);
                        }
                    });
                } else {
                    publish.setVisibility(View.GONE);
                }
                View favorite = mDropdown.findViewById(R.id.my_recipes_favorite_button);
                favorite.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        favoriteRecipe(mPosition);
                    }
                });
            }
        });

        return rootView;
    }

    @Override
    public void onDestroyView() {
        mMyRecipesModel.removeSaveRecipeListener(this);
        super.onDestroyView();
    }

    // OnClickListener
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.save_button) {
            this.getActivity().finish();
        } else if (view.getId() == R.id.cancel_button) {
            this.getActivity().finish();
        }
    }


//	@Override
//	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
//		mLoader = new CursorLoader(getActivity(),
//				Provider.RECIPE_CONTENT_URI,
//				new String[]{RecipeTable.NAME, RecipeTable.TYPE},
//				null,
//				null,
//				RecipeTable.NAME + " COLLATE NOCASE");
//		return mLoader;
//	}

//	@Override
//	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
////		mAdapter.swapCursor(cursor);
//	}

//	@Override
//	public void onLoaderReset(Loader<Cursor> arg0) {
////		mAdapter.swapCursor(null);
//	}


    @Override
    public boolean onItemLongClick(AdapterView<?> list, View view, int position,
                                   long arg3) {
        mPosition = position;
        AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity(), AlertDialog.THEME_HOLO_DARK);
        builder.setIcon(android.R.drawable.ic_dialog_alert);
        builder.setTitle(getActivity().getResources().getString(R.string.delete_capitalized));
        builder.setMessage(getActivity().getResources().getString(R.string.delete_recipe_confirm_question));
        builder.setPositiveButton(getActivity().getResources().getString(R.string.yes_capitalized), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                delete();
            }

            private void delete() {
                String recipeId = mAdapter.getId(mPosition);
                long id = Long.parseLong(recipeId);
                SPRecipe.delete(getActivity().getApplicationContext(), id);

                SPModel model = SPModel.getInstance(getActivity());
                for (int i = 0; i < model.getCrucibleCount(); i++) {
                    if (model.getRecipeForCrucible(i) != null && model.getRecipeForCrucible(i).getId() == id) {
                        model.setRecipeForCrucible(i, 0);
                    }
                }
//				mAdapter.getCursor().requery(); SPLog.debug("requeried!");
//				mAdapter.notifyDataSetChanged();
            }
        });
        builder.setNegativeButton(getActivity().getResources().getString(R.string.no_capitalized), null);
        builder.show();

        return false;
    }

    @Override
    public void onResume() {
        if (mAdapter != null) {
            SPLog.debug("******* resuming...notifying adapter that it needs to update!");
//			mAdapter.getCursor().requery();
//			mAdapter.notifyDataSetChanged();
        }
        super.onResume();
    }

    @Override
    public void notifyRecipeFinishedSaving() {
        try {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (dimmableEditButton != null) {
                        dimmableEditButton.setAlpha(1.0f);
                        dimmableEditButton = null;
                    }
                }
            });
        } catch (Exception e) {
            //does it matter if it dies?!
        }
    }
}
