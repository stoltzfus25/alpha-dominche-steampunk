package com.alphadominche.steampunkhmi;

import java.util.Set;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.alphadominche.steampunkhmi.contentprovider.Provider;
import com.alphadominche.steampunkhmi.database.tables.RecipeTable;
import com.alphadominche.steampunkhmi.database.tables.StackTable;
import com.alphadominche.steampunkhmi.restclient.persistenceservicehelper.DefaultPersistenceServiceHelper;

public class SPLibraryCursorAdapter extends SimpleCursorAdapter {
	private final Context mContext;
	private int mLayout;
	private LayoutInflater mInflater;
	private Set<Long> mFavList;
	private ContentResolver mContentResolver;
	
	public SPLibraryCursorAdapter(Context context, int layout, Cursor c, String[] from, int[] to,Set<Long> favList) {
		super(context, layout, c, from, to, 0 /*FLAG_REGISTER_CONTENT_OBSERVER*/);
		mContext = context;
		mLayout = layout;
		mFavList = favList;
		mInflater = LayoutInflater.from(context);
		mContentResolver = context.getContentResolver();
	}

	public Long getId(int position) {
		Cursor c = (Cursor)this.getItem(position);
		return c.getLong(c.getColumnIndex(RecipeTable.ID));
	}
	
	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		return mInflater.inflate(mLayout, null);
	}
    
	@Override
	public void bindView(View convertView, Context context, Cursor cursor) {
		super.bindView(convertView, context, cursor);
		
		ImageView type = (ImageView) convertView.findViewById(R.id.recipe_type_image);
		ImageView fav = (ImageView) convertView.findViewById(R.id.favorite_star_image);
		TextView recipeNameView = (TextView) convertView.findViewById(R.id.recipe_name_label);
		TextView volumeView = (TextView) convertView.findViewById(R.id.volume_label);
		
		int Name_index = cursor.getColumnIndexOrThrow(RecipeTable.NAME);
		int Type_index = cursor.getColumnIndexOrThrow(RecipeTable.TYPE);
		int ID_index = cursor.getColumnIndexOrThrow(RecipeTable.ID);
		
		// Get first stack volume
//		Cursor stackCursor = mContentResolver.query(
//				Provider.STACK_CONTENT_URI,
//				new String[] {StackTable.VOLUME.toString()},
//				StackTable.WHERE_RECIPE_ID_EQUALS,
//				new String[] {Long.toString(cursor.getLong(ID_index))},
//				null);
		
//		if (stackCursor.getCount() > 0) {
//			stackCursor.moveToFirst();
//			int column = stackCursor.getColumnIndex(StackTable.VOLUME);
//			double volume = stackCursor.getDouble(column);
//			if (SPModel.getInstance(context).getVolumeUnits() == SPVolumeUnitType.OUNCES) {
//				volume = Math.round(SPFlowMeter.convertFromMillilitersToOunces(volume));
//			}
//			volumeView.setText(Double.toString(volume));
//		}
//		stackCursor.close();

		recipeNameView.setText(cursor.getString(Name_index));

		String typ = cursor.getString(Type_index);
		if (typ.equals(SPRecipe.TEA_RRECIPE_TYPE_DB_STR)) {
			type.setImageResource(R.drawable.tea_gray);
		} else {
			type.setImageResource(R.drawable.bean_gray);
		}

		fav.setImageResource(R.drawable.teal_star);

		if (mFavList.contains(Long.parseLong(cursor.getString(ID_index)))) {
			fav.setVisibility(View.VISIBLE);
		} else {
			fav.setVisibility(View.GONE);
		}
	}

	public void toggleFavorite(int pos) {
		boolean favorited = mFavList.contains(this.getId(pos));

		if (favorited) {
			mFavList.remove(this.getId(pos));
			DefaultPersistenceServiceHelper.getInstance(mContext).deleteFavorite(getId(pos));
		} else {
			mFavList.add(this.getId(pos));
			DefaultPersistenceServiceHelper.getInstance(mContext).createFavorite(getId(pos));
		}
//		SPLibraryModel.setFavoriteRecipes(favorites);
	}
}
