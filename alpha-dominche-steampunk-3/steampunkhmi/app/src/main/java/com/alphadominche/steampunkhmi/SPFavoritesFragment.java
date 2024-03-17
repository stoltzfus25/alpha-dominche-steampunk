package com.alphadominche.steampunkhmi;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.alphadominche.steampunkhmi.contentprovider.Provider;
import com.alphadominche.steampunkhmi.database.Database;
import com.alphadominche.steampunkhmi.database.tables.FavoriteTable;
import com.alphadominche.steampunkhmi.database.tables.RecipeTable;
import com.alphadominche.steampunkhmi.restclient.persistenceservicehelper.DefaultPersistenceServiceHelper;
import com.alphadominche.steampunkhmi.utils.Constants;
import com.alphadominche.steampunkhmi.utils.MachineSettings;
import com.alphadominche.steampunkhmi.utils.SteampunkUtils;

/**
 * @author guy
 */

public class SPFavoritesFragment extends SPFragment implements LoaderCallbacks<Cursor>, OnItemLongClickListener {
    private static final int MODAL_WIDTH = 600;
    private static final int MODAL_HEIGHT = 400;

    private SPModel mFavoritesModel = SPModel.getInstance(getActivity());
    private CursorLoader mLoader;
    private ImageView mCancelButton;
    private ImageView mSaveButton;
    private View mCleanButton;
    private ListView mListView;

    private View mFavoritesSelection1View;
    private ImageView mFavoritesSelection1Icon;
    private TextView mFavoritesSelection1Text;
    private View mFavoritesSelection2View;
    private ImageView mFavoritesSelection2Icon;
    private TextView mFavoritesSelection2Text;
    private View mFavoritesSelection3View;
    private ImageView mFavoritesSelection3Icon;
    private TextView mFavoritesSelection3Text;
    private View mFavoritesSelection4View;
    private ImageView mFavoritesSelection4Icon;
    private TextView mFavoritesSelection4Text;

    private ArrayList<String> mFavoriteRecipes;
    private ArrayList<Long> mSelectedRecipes;
    private SPFavoritesCursorAdapter mAdapter;
    private long mSelected;
    private int mCrucibleCount;
    private Cursor mContentCursor;

    private Cursor queryFavorites() {
        Database databaseHelper = new Database(getActivity());
        String selectionList = RecipeTable.TABLE_NAME +
                "." +
                RecipeTable.ID +
                "," +
                RecipeTable.TABLE_NAME +
                "." +
                RecipeTable.NAME +
                "," +
                RecipeTable.TABLE_NAME +
                "." +
                RecipeTable.TYPE +
                "," +
                RecipeTable.TABLE_NAME +
                "." +
                RecipeTable.STEAMPUNK_USER_ID;
        String query = "SELECT " +
                selectionList +
                " FROM " +
                RecipeTable.TABLE_NAME +
                " JOIN " +
                FavoriteTable.TABLE_NAME +
                " ON " +
                RecipeTable.TABLE_NAME +
                "." +
                RecipeTable.ID +
                " = " +
                FavoriteTable.TABLE_NAME +
                "." +
                FavoriteTable.RECIPE_ID +
                " ORDER BY " +
                RecipeTable.NAME + Constants.ASCENDING;
        Cursor cursor = databaseHelper.getWritableDatabase().rawQuery(query, null);
        return cursor;
    }

    public void updateFavorites() {
        mContentCursor = queryFavorites();
        mContentCursor.moveToFirst();
        mAdapter = new SPFavoritesCursorAdapter(getActivity(),
                R.drawable.favorites_list_view,
                mContentCursor,
                new String[]{RecipeTable.NAME, RecipeTable.TYPE, RecipeTable.ID},
                new int[]{android.R.id.text1});
        mListView.setAdapter(mAdapter);
    }

    // Configure displays for crucible squares at top
    public void setBlock(ImageView icon, TextView text, int i) {
        long recipeId = mSelectedRecipes.get(i);
        if (recipeId >= 1) {
            Cursor recipeCursor = getActivity().getContentResolver().query(
                    Provider.RECIPE_CONTENT_URI,
                    RecipeTable.ALL_COLUMNS, RecipeTable.ID + " =?",
                    new String[]{Long.toString(recipeId)},
                    null);

            if (mFavoritesModel.getStateForCrucible(i) != SPCrucibleState.IDLE) {
                text.setText(getActivity().getResources().getString(R.string.favorites_crucible_status_brewing));
                icon.setVisibility(View.INVISIBLE);
                icon.setAlpha(0.5f);
            } else if (recipeCursor.moveToFirst()) {
                int index = recipeCursor.getColumnIndex(RecipeTable.NAME);
                if (!recipeCursor.isNull(index)) {
                    text.setText(recipeCursor.getString(index));
                }
                index = recipeCursor.getColumnIndex(RecipeTable.TYPE);
                if (!recipeCursor.isNull(index)) {
                    if (recipeCursor.getInt(index) == 1) {
                        icon.setImageResource(R.drawable.bean_gray);
                    } else {
                        icon.setImageResource(R.drawable.tea_gray);
                    }
                    icon.setVisibility(View.VISIBLE);
                }
                icon.setAlpha(1.0f);
            } else {
                text.setText(getActivity().getResources().getString(R.string.favorites_crucible_status_missing));
                icon.setVisibility(View.INVISIBLE);
                icon.setAlpha(1.0f);
            }

            recipeCursor.close();
        } else {
            text.setText(getActivity().getResources().getString(R.string.favorites_crucible_status_empty));
            icon.setVisibility(View.INVISIBLE);
            icon.setAlpha(1.0f);
        }
    }

    // Update Four selected recipes
    public void refresh() {
        setBlock(mFavoritesSelection1Icon, mFavoritesSelection1Text, 0);
        if (mCrucibleCount > 1) {
            setBlock(mFavoritesSelection2Icon, mFavoritesSelection2Text, 1);
        }
        if (mCrucibleCount > 2) {
            setBlock(mFavoritesSelection3Icon, mFavoritesSelection3Text, 2);
        }
        if (mCrucibleCount > 3) {
            setBlock(mFavoritesSelection4Icon, mFavoritesSelection4Text, 3);
        }

        // THIS IS A HACK TO MAKE THE FAVORITES UPDATE ON CHANGES
        (new Thread() {
            @Override
            public void run() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            sleep(100);
                        } catch (InterruptedException e) {

                        }
                        if (mContentCursor != null) mContentCursor.requery();
                        SPLog.debug("requeried!");
                    }
                });
            }
        }).start();
    }

    public void getFavorites() {
        mFavoriteRecipes = new ArrayList<String>();
        long userId = SteampunkUtils.getCurrentUserId(getActivity().getApplicationContext());
        Cursor favCursor = getActivity().getContentResolver().query(
                Provider.FAVORITE_CONTENT_URI,
                FavoriteTable.ALL_COLUMNS,
                FavoriteTable.USER +
                        "=?",
                new String[]{Long.toString(userId)},
                null);
        favCursor.moveToFirst();

        while (!favCursor.isAfterLast()) {
            int index = favCursor.getColumnIndex(FavoriteTable.RECIPE_ID);
            mFavoriteRecipes.add(favCursor.getString(index));
            favCursor.moveToNext();
        }

        favCursor.close();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.sp_favorites, container, false);
        Integer crucibleCount = MachineSettings
                .getMachineCrucibleCount(getActivity().getApplicationContext());
        if (null == crucibleCount) {
            Toast.makeText(
                    getActivity(),
                    getActivity().getResources().getString(R.string.favorites_incomplete_machine_settings_toast),
                    Toast.LENGTH_LONG).show();
            mCrucibleCount = SPIOIOService.MAX_CRUCIBLE_COUNT; // select a reasonable default
        } else {
            mCrucibleCount = crucibleCount.intValue();
        }
        mSaveButton = (ImageView) rootView.findViewById(R.id.save_button);
        mSaveButton.setOnClickListener(this);
        mCancelButton = (ImageView) rootView.findViewById(R.id.cancel_button);
        mCancelButton.setOnClickListener(this);
        mCleanButton = rootView.findViewById(R.id.favorites_clean_selections);
        mCleanButton.setOnClickListener(this);

        mFavoritesSelection1Text = (TextView) rootView.findViewById(R.id.favorites_selection_1_text);
        mFavoritesSelection1Icon = (ImageView) rootView.findViewById(R.id.favorites_selection_1_icon);
        mFavoritesSelection1View = rootView.findViewById(R.id.favorites_selection_1_view);

        mFavoritesSelection2Text = (TextView) rootView.findViewById(R.id.favorites_selection_2_text);
        mFavoritesSelection2Icon = (ImageView) rootView.findViewById(R.id.favorites_selection_2_icon);
        mFavoritesSelection2View = rootView.findViewById(R.id.favorites_selection_2_view);

        mFavoritesSelection3Text = (TextView) rootView.findViewById(R.id.favorites_selection_3_text);
        mFavoritesSelection3Icon = (ImageView) rootView.findViewById(R.id.favorites_selection_3_icon);
        mFavoritesSelection3View = rootView.findViewById(R.id.favorites_selection_3_view);

        mFavoritesSelection4Text = (TextView) rootView.findViewById(R.id.favorites_selection_4_text);
        mFavoritesSelection4Icon = (ImageView) rootView.findViewById(R.id.favorites_selection_4_icon);
        mFavoritesSelection4View = rootView.findViewById(R.id.favorites_selection_4_view);

        mFavoritesSelection1View.setOnClickListener(this);
        if (mCrucibleCount > 1) mFavoritesSelection2View.setOnClickListener(this);
        else mFavoritesSelection2View.setVisibility(View.GONE);
        if (mCrucibleCount > 2) mFavoritesSelection3View.setOnClickListener(this);
        else mFavoritesSelection3View.setVisibility(View.GONE);
        if (mCrucibleCount > 3) mFavoritesSelection4View.setOnClickListener(this);
        else mFavoritesSelection4View.setVisibility(View.GONE);

        // Pull favorites from Provider
        getFavorites();

        // update the recipes in the crucible boxes
        mSelectedRecipes = mFavoritesModel.getSelectedRecipes();
        refresh();
        mSelected = -1;
        mListView = (ListView) rootView.findViewById(R.id.favorites_list);
        updateFavorites();

        mListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mSelected = Long.parseLong(mAdapter.getId(position));
                mAdapter.setSelected(mSelected);
//				mAdapter.notifyDataSetChanged();
//				mContentCursor.requery();
            }
        });
        mListView.setOnItemLongClickListener(this);

        refresh();

        return rootView;
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

        mSelected = Long.parseLong(mAdapter.getId(position));
        mAdapter.setSelected(mSelected);
        mAdapter.notifyDataSetChanged();
        AlertDialog alert = new AlertDialog.Builder(view.getContext())
                .setMessage(getActivity().getResources().getString(R.string.favorites_should_remove_question))
                .setPositiveButton(getActivity().getResources().getString(R.string.favorites_should_remove_answer_yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        // Remove Locally
                        mFavoriteRecipes.remove(Long.toString(mSelected));

                        //Remove from selectedRecipes
                        int index = mSelectedRecipes.indexOf(mSelected);
                        while (index != -1) {
                            mSelectedRecipes.set(index, 0L);
                            index = mSelectedRecipes.indexOf(mSelected);
                        }
                        DefaultPersistenceServiceHelper.getInstance(getActivity().getApplicationContext()).deleteFavorite(mSelected);
                        mSelected = -1;
                        mAdapter.setSelected(mSelected);
//		        	try {
//						Thread.sleep(5000);
//					} catch (InterruptedException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//		        	mAdapter.getCursor().requery();
//		        	mContentCursor.requery();
//		        	mAdapter.swapCursor(mContentCursor);
//		        	mAdapter.notifyDataSetChanged();
//		        	mAdapter.notifyDataSetInvalidated();
//		        	mListView.setAdapter(mAdapter);
//		        	updateFavorites();
                        refresh();
                    }
                })
                .setNegativeButton(getActivity().getResources().getString(R.string.favorites_should_remove_answer_no), null)
                .show();
        alert.getWindow().setLayout(MODAL_WIDTH, MODAL_HEIGHT);
        TextView text = (TextView) alert.findViewById(android.R.id.message);
        text.setGravity(Gravity.CENTER);
        return true;
    }

    // OnClickListener
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.save_button:
                mFavoritesModel.setSelectedRecipes(mSelectedRecipes);
                this.getActivity().finish();
                break;
            case R.id.cancel_button:
                this.getActivity().finish();
                break;
            case R.id.favorites_clean_selections:
                for (int i = 0; i < mSelectedRecipes.size(); i++) {
                    if (mFavoritesModel.getStateForCrucible(i) == SPCrucibleState.IDLE) {
                        mSelectedRecipes.set(i, 0L);
                    }
                }
                refresh();
                break;
            case R.id.favorites_selection_1_view:
                if (mSelected >= 0 && mFavoritesModel.getStateForCrucible(0) == SPCrucibleState.IDLE) {
                    mSelectedRecipes.set(0, mSelected);
                }
                refresh();
                break;
            case R.id.favorites_selection_2_view:
                if (mSelected >= 0 && mFavoritesModel.getStateForCrucible(1) == SPCrucibleState.IDLE) {
                    mSelectedRecipes.set(1, mSelected);
                }
                refresh();
                break;
            case R.id.favorites_selection_3_view:
                if (mSelected >= 0 && mFavoritesModel.getStateForCrucible(2) == SPCrucibleState.IDLE) {
                    mSelectedRecipes.set(2, mSelected);
                }
                refresh();
                break;
            case R.id.favorites_selection_4_view:
                if (mSelected >= 0 && mFavoritesModel.getStateForCrucible(3) == SPCrucibleState.IDLE) {
                    mSelectedRecipes.set(3, mSelected);
                }
                refresh();
                break;
            default:
                break;
        }
        System.out.println(mSelectedRecipes);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        mLoader = new CursorLoader(
                getActivity(),
                Provider.RECIPE_CONTENT_URI,
                new String[]{RecipeTable.NAME,
                        RecipeTable.TYPE},
                null,
                null,
                RecipeTable.NAME + " COLLATE NOCASE");
        return mLoader;
    }

    @Override
    public void onResume() {
//		mAdapter.changeCursor(queryFavorites());
        super.onResume();
        updateFavorites();
    }

    @Override
    public void onLoadFinished(Loader<Cursor> arg0, Cursor cursor) {
        mAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> arg0) {
        mAdapter.swapCursor(null);
    }
}
