package com.alphadominche.steampunkhmi;

import java.util.ArrayList;
import java.util.Arrays;

import android.app.Dialog;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.alphadominche.steampunkhmi.contentprovider.Provider;
import com.alphadominche.steampunkhmi.database.tables.FavoriteTable;
import com.alphadominche.steampunkhmi.database.tables.RecipeTable;
import com.alphadominche.steampunkhmi.utils.Constants;
import com.alphadominche.steampunkhmi.utils.SteampunkUtils;

public class SPFavoritesChooserModal extends SPListChooserModalFragment {
	private ArrayList<String> mFavoriteRecipes;
	
	private ListView mListView;
	
	private SPFavoritesCursorAdapter mAdapter;
	
	private long mSelected = -1L;
	private int mCrucibleIndex = -1;
	
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		final Dialog d = super.onCreateDialog(savedInstanceState);
		
		getFavorites();
		
		mListView = getListView();

		String select = RecipeTable.ID + "=?";
		for(int i = 1;i < mFavoriteRecipes.size();i++){
			
			select += " OR " + RecipeTable.ID + "=?";
		}
		
		Cursor recipeCursor = getActivity().getContentResolver().query(Provider.RECIPE_CONTENT_URI,
				RecipeTable.ALL_COLUMNS,
				select,
				Arrays.copyOf(mFavoriteRecipes.toArray(), mFavoriteRecipes.size(), String[].class),
				RecipeTable.NAME + Constants.ASCENDING);
		recipeCursor.moveToFirst();
		mAdapter = new SPFavoritesCursorAdapter(getActivity(),
				R.drawable.favorites_list_view,
				recipeCursor,
				new String[] { RecipeTable.NAME, RecipeTable.TYPE, RecipeTable.ID},
				new int[] { android.R.id.text1 });
		mListView.setAdapter(mAdapter);
		
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				mSelected = Long.parseLong(mAdapter.getId(position));
				mAdapter.setSelected(mSelected);
				mAdapter.notifyDataSetChanged();
			}
		}); 
		
		
		setSPModalDismissListener(new SPModalDismissListener() {

			@Override
			public void onCancel() {
				d.dismiss();
			}

			@Override
			public void onConfirm() {
				if (mSelected > 0L) {
					SPModel.getInstance(getActivity()).setRecipeForCrucible(mCrucibleIndex, mSelected);
					d.dismiss();
				}
			}
			
		});
		
		setTitle(getActivity().getResources().getString(R.string.choose_recipe_title));
		
		return d;
	}
	
	public void getFavorites() {
		mFavoriteRecipes = new ArrayList<String>();
		long userId = SteampunkUtils.getCurrentUserId(getActivity().getApplicationContext());
		
		// Get all favorited recipes for current user
		Cursor favCursor = getActivity().getContentResolver().query(Provider.FAVORITE_CONTENT_URI,
				FavoriteTable.ALL_COLUMNS,
				FavoriteTable.USER + "=?",
				new String[]{Long.toString(userId)},
				null);
		
        favCursor.moveToFirst();
        
        while(!favCursor.isAfterLast()) {
        	int index = favCursor.getColumnIndex(FavoriteTable.RECIPE_ID);
        	mFavoriteRecipes.add(favCursor.getString(index));
	        favCursor.moveToNext();
        }
        
        favCursor.close();
	}
	
	public void setCrucibleIndex(int crucibleIndex) {
		mCrucibleIndex = crucibleIndex;
	}
	
	public void setSelectedRecipeId(long id) {
		mSelected = id;
	}
}
