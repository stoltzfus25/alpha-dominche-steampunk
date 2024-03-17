package com.alphadominche.steampunkhmi;

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
import com.alphadominche.steampunkhmi.utils.Constants;

public class SPFavoritesCursorAdapter extends SimpleCursorAdapter {
    private int mLayout;
    private LayoutInflater mInflater;
    private long mSelectedItem;
    private ContentResolver mContentResolver;

    public SPFavoritesCursorAdapter(Context context, int layout, Cursor c, String[] from, int[] to) {
        super(context, layout, c, from, to, FLAG_REGISTER_CONTENT_OBSERVER);
//		super(context, layout, c, from, to);
        this.mLayout = layout;
        this.mInflater = LayoutInflater.from(context);
        this.mContentResolver = context.getContentResolver();
    }


    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return mInflater.inflate(mLayout, null);
    }

    public String getId(int position) {
        Cursor c = (Cursor) this.getItem(position);
        Cursor temp = mContentResolver.query(
                Provider.RECIPE_CONTENT_URI,
                RecipeTable.ALL_COLUMNS,
                RecipeTable.NAME + "=?",
                new String[]{c.getString(c.getColumnIndex(RecipeTable.NAME))},
                RecipeTable.NAME + Constants.ASCENDING);
        if (temp.moveToFirst()) {

        }
        return c.getString(c.getColumnIndex(RecipeTable.ID));

    }

    public void setSelected(long selected) {
//		for(int i = 0;i < getCursor().getCount();i++) {
//			View v = this.getView(i, null, null);
//			v.findViewById(R.id.favorite_selected_image).setVisibility(View.INVISIBLE);
//		}
        mSelectedItem = selected;
        getCursor().requery();
//		for(int i = 0;i < getCursor().getCount();i++) {
//			long id = this.getItemId(i);
//			if (mSelectedItem == id) { SPLog.debug("turning it on");
//				View v = this.getView(i, null, null);
//				ImageView img = (ImageView)v.findViewById(R.id.favorite_selected_image); SPLog.debug("img: " + img);
//				img.setImageResource(R.drawable.teal_circle);
//				img.setVisibility(View.VISIBLE);
//			}
//		}
    }

    @Override
    public void bindView(View convertView, Context context, Cursor cursor) { //SPLog.debug("binding view...");
        super.bindView(convertView, context, cursor);

        ImageView type = (ImageView) convertView.findViewById(R.id.recipe_type_image);
        ImageView selected = (ImageView) convertView.findViewById(R.id.favorite_selected_image);
        TextView roasterNameView = (TextView) convertView.findViewById(R.id.roaster_name_label);
        TextView recipeNameView = (TextView) convertView.findViewById(R.id.recipe_name_label);
        TextView volumeView = (TextView) convertView.findViewById(R.id.volume_label);

        int Name_index = cursor.getColumnIndexOrThrow(RecipeTable.NAME);
        int Type_index = cursor.getColumnIndexOrThrow(RecipeTable.TYPE);
        int ID_index = cursor.getColumnIndexOrThrow(RecipeTable.ID);
        int Roaster_index = cursor.getColumnIndexOrThrow(RecipeTable.STEAMPUNK_USER_ID);
        int roasterId = cursor.getInt(Roaster_index);

        // Get roaster username
        Cursor roasterCursor = mContentResolver.query(
                Provider.ROASTER_CONTENT_URI,
                new String[]{RoasterTable.USERNAME.toString()},
                RoasterTable.WHERE_SP_ID_EQUALS,
                new String[]{Integer.toString(roasterId)},
                null);

        if (roasterCursor.getCount() > 0) {
            roasterCursor.moveToFirst();
            int column = roasterCursor.getColumnIndex(RoasterTable.USERNAME);
            roasterNameView.setText(roasterCursor.getString(column));
        }
        roasterCursor.close();

        recipeNameView.setText(cursor.getString(Name_index));
        SPLog.debug("binding view..." + cursor.getString(Name_index));

        long ID = cursor.getLong(ID_index);
        selected.setImageResource(R.drawable.teal_circle);
        selected.setVisibility(View.VISIBLE);
        if (mSelectedItem == ID) {
            SPLog.debug("this item is getting shown as selected!");
            selected.setVisibility(View.VISIBLE);
        } else {
            selected.setVisibility(View.INVISIBLE);
        }

        String typ = cursor.getString(Type_index);
        if (typ.equals(SPRecipe.TEA_RRECIPE_TYPE_DB_STR)) {
            type.setImageResource(R.drawable.tea_gray);
        } else {
            type.setImageResource(R.drawable.bean_gray);
        }

    }
}
