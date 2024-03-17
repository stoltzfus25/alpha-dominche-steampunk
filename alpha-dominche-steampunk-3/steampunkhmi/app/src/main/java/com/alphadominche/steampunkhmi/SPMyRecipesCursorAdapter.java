package com.alphadominche.steampunkhmi;

import org.json.JSONArray;
import org.json.JSONException;

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
import com.alphadominche.steampunkhmi.database.tables.RoasterTable;

public class SPMyRecipesCursorAdapter extends SimpleCursorAdapter {
    private int mLayout;
    private LayoutInflater mInflater;
    private Set<Long> mFavList;
    private ContentResolver mContentResolver;
    private Context mContext;

    public SPMyRecipesCursorAdapter(Context context, int layout, Cursor cursor, String[] from, int[] to, Set<Long> favList) {
        // TODO get this working with the non-deprecated constructor
        super(context, layout, cursor, from, to);
//		super(context, layout, cursor, from, to, FLAG_AUTO_REQUERY); //same as deprecated contructor
//		super(context, layout, cursor, from, to, FLAG_REGISTER_CONTENT_OBSERVER); //the proper constructor call
        mFavList = favList;
        mLayout = layout;
        mInflater = LayoutInflater.from(context);
        mContentResolver = context.getContentResolver();
        mContext = context;
    }

    public void removeFav(Long i) {
        mFavList.remove(i);
    }

    public void addFav(Long i) {
        mFavList.add(i);
    }

    public String getId(int position) {
        Cursor c = (Cursor) this.getItem(position);
        return c.getString(c.getColumnIndex(RecipeTable.ID));
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return mInflater.inflate(mLayout, null);
    }

    @Override
    public void bindView(View convertView, Context context, Cursor cursor) {

        super.bindView(convertView, context, cursor);
        View dropdown = convertView.findViewById(R.id.my_recipes_dropdown);
        if (dropdown.getVisibility() == View.VISIBLE) {
            dropdown.setVisibility(View.GONE);
        }
        TextView publishedText = (TextView) convertView.findViewById(R.id.my_recipes_publish_text);
        ImageView type = (ImageView) convertView.findViewById(R.id.recipe_type_image);
        ImageView fav = (ImageView) convertView.findViewById(R.id.favorite_star_image);
        TextView roasterNameView = (TextView) convertView.findViewById(R.id.roaster_name_label);
        TextView recipeNameView = (TextView) convertView.findViewById(R.id.recipe_name_label);
        TextView volumeView = (TextView) convertView.findViewById(R.id.volume_label);

        int Name_index = cursor.getColumnIndexOrThrow(RecipeTable.NAME);
        int Type_index = cursor.getColumnIndexOrThrow(RecipeTable.TYPE);
        int ID_index = cursor.getColumnIndexOrThrow(RecipeTable.ID);
        int Published_index = cursor.getColumnIndexOrThrow(RecipeTable.PUBLISHED);
        int Roaster_index = cursor.getColumnIndexOrThrow(RecipeTable.STEAMPUNK_USER_ID);
        int roasterId = cursor.getInt(Roaster_index);
        String stackJSON = cursor.getString(cursor.getColumnIndex(RecipeTable.STACKS));
        JSONArray stackArray;
        SPVolumeUnitType volUnit = SPModel.getInstance(mContext).getVolumeUnits();
        try {
            stackArray = new JSONArray(stackJSON);
            SPRecipeStack firstStack = new SPRecipeStack(stackArray.getJSONObject(0));
            double volume = Math.round(firstStack.getVolume());
            if (volUnit == SPVolumeUnitType.OUNCES) volume = Math.round(SPFlowMeter.convertFromMillilitersToOunces(volume));
            volumeView.setText("" + (int) volume + " " + mContext.getString(volUnit == SPVolumeUnitType.OUNCES ? R.string.ounces_abreviation : R.string.milliliters_abreviation));
        } catch (JSONException e) {
            SPLog.send(mContext, cursor.getString(cursor.getColumnIndex(RecipeTable.UUID)), -1, SPLog.ERROR, SPLog.GENERAL, "Could not populate My Recipes entry because of a JSONException");
        }

        // Get roaster username
        Cursor roasterCursor = mContentResolver.query(Provider.ROASTER_CONTENT_URI,
                new String[]{RoasterTable.USERNAME.toString()}, RoasterTable.WHERE_SP_ID_EQUALS,
                new String[]{Integer.toString(roasterId)}, null);

        if (roasterCursor.getCount() > 0) {
            roasterCursor.moveToFirst();
            int column = roasterCursor.getColumnIndex(RoasterTable.USERNAME);
            roasterNameView.setText(roasterCursor.getString(column));
        }
        roasterCursor.close();

        recipeNameView.setText(cursor.getString(Name_index));

        if (cursor.getInt(Published_index) == 1) {
            publishedText.setText(mContext.getResources().getString(R.string.unpublish_label_value));
        } else {
            publishedText.setText(mContext.getResources().getString(R.string.publish_label_value));
        }

        String typ = cursor.getString(Type_index);
        if (typ.equals(SPRecipe.TEA_RRECIPE_TYPE_DB_STR)) {
            type.setImageResource(R.drawable.tea_gray);
        } else {
            type.setImageResource(R.drawable.bean_gray);
        }

        fav.setImageResource(R.drawable.teal_star);

        if (mFavList.contains(cursor.getLong(ID_index))) {
            fav.setVisibility(View.VISIBLE);
        } else {
            fav.setVisibility(View.GONE);
        }
    }

//	@Override
//	protected void onContentChanged() {
//		SPLog.debug("content changed!");
//		super.onContentChanged();
////		getCursor().requery();
//		runQueryOnBackgroundThread(null);
//		notifyDataSetChanged();
//	}
}